package com.team5.backend.global.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Slf4j
@Component
public class ImageUtil {

    @Value("${spring.cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${spring.cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${spring.cloud.aws.region.static}")
    private String region;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${spring.cloud.aws.s3.base-url}")
    private String baseUrl;

    // S3 클라이언트 초기화
    private S3Client getS3Client() {

        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }


    // 단일 이미지 저장
    public String saveImage(MultipartFile imageFile, ImageType imageType) throws IOException {

        if (imageFile == null || imageFile.isEmpty()) {
            return null ;
        }

        try {
            String fileName = generateFileName(imageFile.getOriginalFilename(), imageType);
            uploadToS3(imageFile, fileName);
            return buildImageUrl(fileName);
        } catch (Exception e) {
            throw new IOException("S3 업로드 실패: " + e.getMessage(), e);
        }
    }

    // 다중 이미지 저장
    public List<String> saveImages(List<MultipartFile> imageFiles, ImageType imageType) throws IOException {

        if (imageFiles == null || imageFiles.isEmpty()) {
            throw new IllegalArgumentException("이미지 파일이 없습니다.");
        }

        List<String> imageUrls = new ArrayList<>();
        List<String> uploadedFiles = new ArrayList<>();

        try {
            for (MultipartFile imageFile : imageFiles) {
                if (imageFile != null && !imageFile.isEmpty()) {
                    String fileName = generateFileName(imageFile.getOriginalFilename(), imageType);
                    uploadToS3(imageFile, fileName);
                    String imageUrl = buildImageUrl(fileName);
                    imageUrls.add(imageUrl);
                    uploadedFiles.add(fileName);
                }
            }

            if (imageUrls.isEmpty()) {
                throw new IllegalArgumentException("유효한 이미지 파일이 없습니다.");
            }

            return imageUrls;
        } catch (Exception e) {
            // 업로드 실패 시 이미 업로드된 파일들 삭제
            rollbackUploadedFiles(uploadedFiles);
            throw new IOException("이미지 업로드 실패: " + e.getMessage(), e);
        }
    }

    // 파일명 생성
    private String generateFileName(String originalName, ImageType imageType) {

        String sanitizedName = sanitizeFileName(originalName);
        return imageType.getDirectory() + UUID.randomUUID() + "_" + sanitizedName;
    }

    // 파일명 정규화
    private String sanitizeFileName(String originalName) {

        if (originalName == null || originalName.trim().isEmpty()) {
            return "unknown";
        }
        return Pattern.compile("[^a-zA-Z0-9._-]").matcher(originalName).replaceAll("");
    }

    // S3 업로드
    private void uploadToS3(MultipartFile imageFile, String fileName) throws IOException {

        S3Client s3Client = getS3Client();

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(imageFile.getContentType())
                .build();

        s3Client.putObject(request, RequestBody.fromInputStream(
                imageFile.getInputStream(),
                imageFile.getSize()
        ));
    }

    // 이미지 URL 생성
    private String buildImageUrl(String fileName) {

        String normalizedBaseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        return normalizedBaseUrl + "/" + fileName;
    }

    // 업로드 실패 시 롤백
    private void rollbackUploadedFiles(List<String> uploadedFiles) {

        S3Client s3Client = getS3Client();

        for (String fileName : uploadedFiles) {
            try {
                DeleteObjectRequest request = DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(fileName)
                        .build();
                s3Client.deleteObject(request);
            } catch (Exception e) {
                // 롤백 실패는 로그만 남기고 계속 진행
                System.err.println("롤백 실패: " + fileName + " - " + e.getMessage());
            }
        }
    }

    // 단일 이미지 삭제
    public boolean deleteImage(String imagePath) throws IOException {

        if (imagePath == null || imagePath.trim().isEmpty()) {
            return false;
        }

        try {
            String key = extractKeyFromUrl(imagePath);
            deleteFromS3(key);
            log.info("S3 이미지 삭제 성공: {}", imagePath);
            return true;
        } catch (Exception e) {
            // S3 삭제 실패 (파일이 없는 경우 포함)
            log.warn("S3 이미지 삭제 실패: {}, 오류: {}", imagePath, e.getMessage());
            return false;
        }
    }

    // 다중 이미지 삭제
    public void deleteImages(List<String> imagePaths) throws IOException {

        if (imagePaths == null || imagePaths.isEmpty()) {
            return ;
        }

        List<String> failedDeletions = new ArrayList<>();

        for (String imagePath : imagePaths) {
            try {
                deleteImage(imagePath);
            } catch (IOException e) {
                failedDeletions.add(imagePath);
            }
        }

        if (!failedDeletions.isEmpty()) {
            throw new IOException("일부 이미지 삭제 실패: " + failedDeletions);
        }
    }

    // URL에서 S3 키 추출
    private String extractKeyFromUrl(String imagePath) throws IOException {

        if (imagePath.contains(baseUrl)) {

            String key = imagePath.substring(imagePath.indexOf(baseUrl) + baseUrl.length());
            return key.startsWith("/") ? key.substring(1) : key;
        }

        // profiles/ 또는 products/ 디렉토리를 기준으로 처리
        for (ImageType type : ImageType.values()) {

            int startIndex = imagePath.indexOf(type.getDirectory());
            if (startIndex != -1) {
                return imagePath.substring(startIndex);
            }
        }

        throw new IOException("유효하지 않은 이미지 경로: " + imagePath);
    }

    // S3에서 삭제
    private void deleteFromS3(String key) {

        S3Client s3Client = getS3Client();
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        s3Client.deleteObject(request);
    }
}
