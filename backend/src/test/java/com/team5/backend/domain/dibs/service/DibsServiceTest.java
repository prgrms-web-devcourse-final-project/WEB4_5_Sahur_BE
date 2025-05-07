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

    @InjectMocks
    private DibsService dibsService;

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
    }


    @Test
    @DisplayName("관심상품 등록")
    void createDibs() {
        // given
        DibsCreateReqDto dto = DibsCreateReqDto.builder()
                .memberId(1L)
                .productId(100L)
                .build();

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(productRepository.findById(100L)).thenReturn(Optional.of(product));
        when(dibsRepository.save(any())).thenReturn(dibs);

        // when
        DibsResDto result = dibsService.createDibs(dto);

        // then
        assertEquals(1L, result.getDibsId());
        assertEquals(1L, result.getMemberId());
        assertEquals(100L, result.getProductId());
        verify(dibsRepository).save(any());
    }

    @Test
    @DisplayName("회원별 관심상품 페이징 조회")
    void getPagedDibsByMemberId() {
        // given
        Pageable pageable = PageRequest.of(0, 5);
        Page<Dibs> page = new PageImpl<>(List.of(dibs));

        when(dibsRepository.findByMember_MemberId(eq(1L), any(Pageable.class)))
                .thenReturn(page);

        // when
        Page<DibsResDto> result = dibsService.getPagedDibsByMemberId(1L, pageable);

        // then
        assertEquals(1, result.getTotalElements());

    }

    @Test
    @DisplayName("회원별 관심상품 전체 조회")
    void getAllDibsByMemberId() {
        // given
        when(dibsRepository.findByMember_MemberId(1L)).thenReturn(List.of(dibs));

        // when
        List<DibsResDto> result = dibsService.getAllDibsByMemberId(1L);

        // then
        assertEquals(1, result.size());
        assertEquals(100L, result.get(0).getProductId());
    }

    @Test
    @DisplayName("관심상품 삭제")
    void deleteByProductAndMember() {
        // given
        when(dibsRepository.findByProduct_ProductIdAndMember_MemberId(100L, 1L))
                .thenReturn(Optional.of(dibs));

        // when
        dibsService.deleteByProductAndMember(100L, 1L);

        // then
        verify(dibsRepository).delete(dibs);
    }

    @Test
    @DisplayName("존재하지 않는 회원일 경우 예외 발생")
    void createDibs_MemberNotFound() {
        // given
        DibsCreateReqDto dto = DibsCreateReqDto.builder()
                .memberId(999L)
                .productId(100L)
                .build();

        when(memberRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            dibsService.createDibs(dto);
        });

        assertEquals("회원을 찾을 수 없습니다.", e.getMessage());
    }

    @Test
    @DisplayName("존재하지 않는 상품일 경우 예외 발생")
    void createDibs_ProductNotFound() {
        // given
        DibsCreateReqDto dto = DibsCreateReqDto.builder()
                .memberId(1L)
                .productId(999L)
                .build();

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            dibsService.createDibs(dto);
        });

        assertEquals("상품을 찾을 수 없습니다.", e.getMessage());
    }

    @Test
    @DisplayName("관심상품 삭제 - 존재하지 않는 경우 예외 발생")
    void deleteByProductAndMember_NotFound() {
        // given
        when(dibsRepository.findByProduct_ProductIdAndMember_MemberId(100L, 1L))
                .thenReturn(Optional.empty());

        // when & then
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            dibsService.deleteByProductAndMember(100L, 1L);
        });

        assertEquals("해당 관심상품이 존재하지 않습니다.", e.getMessage());
    }

    @Test
    @DisplayName("찜 등록 중 중복 요청(빠른 더블클릭) 시 예외 발생")
    void createDibs_DuplicateDibs() {
        // given
        DibsCreateReqDto dto = DibsCreateReqDto.builder()
                .memberId(1L)
                .productId(100L)
                .build();

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(productRepository.findById(100L)).thenReturn(Optional.of(product));
        when(dibsRepository.findByProduct_ProductIdAndMember_MemberId(100L, 1L))
                .thenReturn(Optional.of(dibs));

        // when & then
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> {
            dibsService.createDibs(dto);
        });

        assertEquals("이미 찜한 상품입니다.", e.getMessage());
    }

    @Test
    @DisplayName("동일한 회원이 다른 상품 찜 가능")
    void createDibs_DifferentProduct() {
        // given
        Product anotherProduct = Product.builder()
                .productId(200L)
                .title("다른 상품")
                .price(20000)
                .description("설명")
                .category(Category.builder().categoryId(1L).build())
                .createdAt(LocalDateTime.now())
                .build();


        DibsCreateReqDto dto = DibsCreateReqDto.builder()
                .memberId(1L)
                .productId(200L)
                .build();

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(productRepository.findById(200L)).thenReturn(Optional.of(anotherProduct));
        when(dibsRepository.findByProduct_ProductIdAndMember_MemberId(200L, 1L))
                .thenReturn(Optional.empty());
        when(dibsRepository.save(any())).thenReturn(Dibs.builder()
                .dibsId(2L)
                .member(member)
                .product(anotherProduct)
                .build());

        // when
        DibsResDto result = dibsService.createDibs(dto);

        // then
        assertEquals(2L, result.getDibsId());
        assertEquals(1L, result.getMemberId());
        assertEquals(200L, result.getProductId());
    }

    @Test
    @DisplayName("다른 회원이 동일한 상품 찜 가능")
    void createDibs_DifferentMember() {
        // given
        Member anotherMember = Member.builder()
                .memberId(2L)
                .nickname("다른 사용자")
                .email("another@example.com")
                .name("홍길동")
                .password("password1234")
                .role(Role.USER)
                .emailVerified(true)
                .build();

        DibsCreateReqDto dto = DibsCreateReqDto.builder()
                .memberId(2L)
                .productId(100L)
                .build();

        when(memberRepository.findById(2L)).thenReturn(Optional.of(anotherMember));
        when(productRepository.findById(100L)).thenReturn(Optional.of(product));
        when(dibsRepository.findByProduct_ProductIdAndMember_MemberId(100L, 2L))
                .thenReturn(Optional.empty());
        when(dibsRepository.save(any())).thenReturn(Dibs.builder()
                .dibsId(3L)
                .member(anotherMember)
                .product(product)
                .build());

        // when
        DibsResDto result = dibsService.createDibs(dto);

        // then
        assertEquals(3L, result.getDibsId());
        assertEquals(2L, result.getMemberId());
        assertEquals(100L, result.getProductId());
    }

}