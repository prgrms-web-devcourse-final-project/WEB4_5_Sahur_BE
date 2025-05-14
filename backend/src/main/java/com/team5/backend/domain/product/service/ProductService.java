package com.team5.backend.domain.product.service;

import com.team5.backend.domain.category.entity.Category;
import com.team5.backend.domain.category.repository.CategoryRepository;
import com.team5.backend.domain.product.dto.ProductCreateReqDto;
import com.team5.backend.domain.product.dto.ProductResDto;
import com.team5.backend.domain.product.dto.ProductUpdateReqDto;
import com.team5.backend.domain.product.entity.Product;
import com.team5.backend.domain.product.repository.ProductRepository;
import com.team5.backend.domain.product.search.repository.ProductSearchRepository;
import com.team5.backend.domain.product.search.service.ProductSearchService;
import com.team5.backend.global.exception.CustomException;
import com.team5.backend.global.exception.code.ProductErrorCode;
import com.team5.backend.global.util.ImageType;
import com.team5.backend.global.util.ImageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ImageUtil imageUtil;


    /**
     * 상품 등록
     */
    @Transactional
    public ProductResDto createProduct(ProductCreateReqDto request, List<MultipartFile> imageFiles) throws IOException {

        if (request == null) throw new CustomException(ProductErrorCode.INVALID_PRODUCT_STATUS);
        if (imageFiles == null) throw new CustomException(ProductErrorCode.PRODUCT_IMAGE_NOT_FOUND);

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CustomException(ProductErrorCode.CATEGORY_NOT_FOUND));

        List<String> imageUrls = new ArrayList<>();
        if (imageFiles != null && !imageFiles.isEmpty()) {
            imageUrls = imageUtil.saveImages(imageFiles, ImageType.PRODUCT);
        }

        Product product = Product.create(
                category,
                request.getTitle(),
                request.getDescription(),
                imageUrls,
                request.getPrice()
        );

        Product savedProduct = productRepository.save(product);

        return ProductResDto.fromEntity(savedProduct);
    }

    /**
     * 전체 상품 조회 (카테고리 또는 키워드 기준 필터링 포함, 페이징)
     */
    @Transactional(readOnly = true)
    public Page<Product> getAllProducts(String category, String keyword, Pageable pageable) {
        return productRepository.findAllByFilter(category, keyword, pageable);
    }

    /**
     * 단건 상품 조회
     */
    @Transactional(readOnly = true)
    public ProductResDto getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ProductErrorCode.PRODUCT_NOT_FOUND));
        return ProductResDto.fromEntity(product);
    }

    /**
     * 상품 정보 수정
     */
    @Transactional
    public ProductResDto updateProduct(Long productId, ProductUpdateReqDto request, List<MultipartFile> imageFiles) throws IOException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ProductErrorCode.PRODUCT_NOT_FOUND));

        if (request == null) {
            throw new CustomException(ProductErrorCode.INVALID_PRODUCT_STATUS);
        }

        // 카테고리 업데이트
        if (request.getCategoryId() != null) {
            Category newCategory = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new CustomException(ProductErrorCode.CATEGORY_NOT_FOUND));

            product.updateCategory(newCategory);
        }

        // 이미지 처리
        List<String> imageUrls = product.getImageUrl(); // 기존 이미지 URL을 기본값으로 사용
        if (imageFiles != null && !imageFiles.isEmpty()) {
            // 새 이미지가 있으면 기존 이미지 삭제 후 새로 업로드
            if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
                imageUtil.deleteImages(product.getImageUrl());
            }
            imageUrls = imageUtil.saveImages(imageFiles, ImageType.PRODUCT);
        }

        // 상품 정보 업데이트
        product.update(
                request.getTitle(),
                request.getDescription(),
                imageUrls,
                request.getPrice()
        );

        return ProductResDto.fromEntity(product);
    }

    /**
     * 상품 삭제
     */
    @Transactional
    public void deleteProduct(Long productId) throws IOException {
        // 상품 존재 여부 확인 및 조회
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ProductErrorCode.PRODUCT_NOT_FOUND));

        // S3에 저장된 이미지 삭제
        List<String> imageUrls = product.getImageUrl();
        if (imageUrls != null && !imageUrls.isEmpty()) {
            imageUtil.deleteImages(imageUrls);
        }

        // 상품 삭제
        productRepository.deleteById(productId);
    }

    /**
     * 찜 수 조회
     */
    @Transactional(readOnly = true)
    public Long getDibCount(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ProductErrorCode.PRODUCT_NOT_FOUND));
        return product.getDibCount();
    }
}



