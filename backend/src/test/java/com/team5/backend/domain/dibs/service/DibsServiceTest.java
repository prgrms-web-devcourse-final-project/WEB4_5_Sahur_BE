package com.team5.backend.domain.dibs.service;

import com.team5.backend.domain.category.entity.Category;
import com.team5.backend.domain.dibs.dto.DibsCreateReqDto;
import com.team5.backend.domain.dibs.dto.DibsResDto;
import com.team5.backend.domain.dibs.entity.Dibs;
import com.team5.backend.domain.dibs.repository.DibsRepository;
import com.team5.backend.domain.member.member.entity.Member;
import com.team5.backend.domain.member.member.entity.Role;
import com.team5.backend.domain.member.member.repository.MemberRepository;
import com.team5.backend.domain.product.entity.Product;
import com.team5.backend.domain.product.repository.ProductRepository;
import com.team5.backend.global.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DibsServiceTest {

    @Mock private DibsRepository dibsRepository;
    @Mock private MemberRepository memberRepository;
    @Mock private ProductRepository productRepository;
    @Mock private JwtUtil jwtUtil;

    @InjectMocks
    private DibsService dibsService;

    private final String token = "Bearer fake-token";
    private final Long memberId = 1L;

    private Member member;
    private Product product;
    private Dibs dibs;

    @BeforeEach
    void setUp() {
        member = Member.builder()
                .memberId(1L)
                .nickname("사용자1")
                .email("test@example.com")
                .build();

        product = Product.builder()
                .productId(100L)
                .title("테스트 상품")
                .price(10000)
                .build();

        dibs = Dibs.builder()
                .dibsId(1L)
                .member(member)
                .product(product)
                .build();

        when(jwtUtil.extractMemberId(any())).thenReturn(memberId);
        when(jwtUtil.extractEmail(any())).thenReturn("test@example.com");
        when(jwtUtil.validateAccessTokenInRedis(any(), any())).thenReturn(true);
        when(jwtUtil.isTokenBlacklisted(any())).thenReturn(false);
    }


    @Test
    @DisplayName("관심상품 등록")
    void createDibs() {
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(productRepository.findById(100L)).thenReturn(Optional.of(product));
        when(dibsRepository.save(any())).thenReturn(dibs);
        when(dibsRepository.findByProduct_ProductIdAndMember_MemberId(100L, memberId))
                .thenReturn(Optional.empty());

        DibsResDto result = dibsService.createDibs(100L, token);

        assertEquals(1L, result.getDibsId());
        assertEquals(memberId, result.getMemberId());
        assertEquals(100L, result.getProductId());
    }

    @Test
    @DisplayName("관심상품 전체 조회")
    void getAllDibsByToken() {
        when(dibsRepository.findByMember_MemberId(memberId)).thenReturn(List.of(dibs));

        List<DibsResDto> result = dibsService.getAllDibsByToken(token);

        assertEquals(1, result.size());
        assertEquals(100L, result.get(0).getProductId());
    }

    @Test
    @DisplayName("관심상품 페이징 조회")
    void getPagedDibsByToken() {
        Page<Dibs> page = new PageImpl<>(List.of(dibs));
        when(dibsRepository.findByMember_MemberId(eq(memberId), any(Pageable.class))).thenReturn(page);

        Page<DibsResDto> result = dibsService.getPagedDibsByToken(token, PageRequest.of(0, 5));

        assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("관심상품 삭제")
    void deleteDibs() {
        when(dibsRepository.findByProduct_ProductIdAndMember_MemberId(100L, memberId))
                .thenReturn(Optional.of(dibs));

        dibsService.deleteByProductAndToken(100L, token);

        verify(dibsRepository).delete(dibs);
    }

    @Test
    @DisplayName("관심상품 중복 등록 예외")
    void createDibs_Duplicate() {
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(productRepository.findById(100L)).thenReturn(Optional.of(product));
        when(dibsRepository.findByProduct_ProductIdAndMember_MemberId(100L, memberId))
                .thenReturn(Optional.of(dibs));

        var ex = assertThrows(RuntimeException.class, () ->
                dibsService.createDibs(100L, token)
        );

        assertEquals("이미 관심상품에 등록되어 있습니다.", ex.getMessage());
    }

    @Test
    @DisplayName("관심상품 삭제 실패 - 없음")
    void deleteDibs_NotFound() {
        when(dibsRepository.findByProduct_ProductIdAndMember_MemberId(100L, memberId))
                .thenReturn(Optional.empty());

        var ex = assertThrows(RuntimeException.class, () ->
                dibsService.deleteByProductAndToken(100L, token)
        );

        assertEquals("관심상품을 찾을 수 없습니다.", ex.getMessage());
    }


} 
