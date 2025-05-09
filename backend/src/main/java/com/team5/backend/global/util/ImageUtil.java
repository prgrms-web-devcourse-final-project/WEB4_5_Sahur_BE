package com.team5.backend.global.util;

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
import java.util.UUID;
import java.util.regex.Pattern;

@Component
public class ImageUtil {

    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${cloud.aws.s3.base-url}")
    private String baseUrl;

    // S3 클라이언트 초기화
    private S3Client getS3Client() {

        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }


    // S3에 이미지 저장
    public String saveImage(MultipartFile imageFile) throws IOException {

        if (imageFile == null || imageFile.isEmpty()) {
            return null; // 이미지가 없으면 null 반환
        }

        try {
            // 고유한 파일명 생성 및 정규화 (알파벳, 숫자, 밑줄, 하이픈, 점만 허용)
            String originalName = imageFile.getOriginalFilename() != null ? imageFile.getOriginalFilename() : "unknown";
            String sanitizedOriginalName = Pattern.compile("[^a-zA-Z0-9._-]").matcher(originalName).replaceAll("");
            String fileName = "images/" + UUID.randomUUID() + "_" + sanitizedOriginalName;

            // S3 클라이언트 생성
            S3Client s3Client = getS3Client();

            // S3에 업로드
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(imageFile.getContentType())
                    .build();

            s3Client.putObject(request, RequestBody.fromInputStream(
                    imageFile.getInputStream(),
                    imageFile.getSize()
            ));

            // URL 생성 시 슬래시 중복 방지
            String normalizedBaseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;

            // S3 객체 URL 반환
            return normalizedBaseUrl + "/" + fileName;

        } catch (Exception e) {
            throw new IOException("S3 업로드 실패: " + e.getMessage(), e);
        }
    }

    // S3에서 이미지 삭제
    public void deleteImage(String imagePath) throws IOException {

        if (imagePath == null || imagePath.trim().isEmpty()) {
            return;
        }

        try {
            // S3 URL에서 키 추출
            // 예: https://bucket-name.s3.region.amazonaws.com/images/uuid_filename.jpg
            // 또는: https://custom-domain.com/images/uuid_filename.jpg
            String key;
            if (imagePath.contains(baseUrl)) {
                key = imagePath.substring(imagePath.indexOf(baseUrl) + baseUrl.length());
                if (key.startsWith("/")) {
                    key = key.substring(1);
                }
            } else {
                // URL 형식이 다른 경우 images/ 디렉토리를 기준으로 처리
                int startIndex = imagePath.indexOf("images/");
                if (startIndex != -1) {
                    key = imagePath.substring(startIndex);
                } else {
                    throw new IOException("유효하지 않은 이미지 경로: " + imagePath);
                }
            }

            // S3 클라이언트 생성
            S3Client s3Client = getS3Client();

            // S3에서 객체 삭제
            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.deleteObject(request);

        } catch (Exception e) {
            throw new IOException("S3 객체 삭제 실패: " + e.getMessage(), e);
        }
    }
}
