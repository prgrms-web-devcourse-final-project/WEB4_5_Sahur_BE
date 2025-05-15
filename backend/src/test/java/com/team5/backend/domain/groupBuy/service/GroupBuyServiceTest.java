package com.team5.backend.domain.groupBuy.service;

import com.team5.backend.domain.category.entity.Category;
import com.team5.backend.domain.category.entity.KeywordType;
import com.team5.backend.domain.category.repository.CategoryRepository;
import com.team5.backend.domain.dibs.repository.DibsRepository;
import com.team5.backend.domain.groupBuy.dto.*;
import com.team5.backend.domain.groupBuy.entity.GroupBuy;
import com.team5.backend.domain.groupBuy.entity.GroupBuySortField;
import com.team5.backend.domain.groupBuy.entity.GroupBuyStatus;
import com.team5.backend.domain.groupBuy.repository.GroupBuyRepository;
import com.team5.backend.domain.history.repository.HistoryRepository;
import com.team5.backend.domain.member.member.entity.Member;
import com.team5.backend.domain.product.entity.Product;
import com.team5.backend.domain.product.repository.ProductRepository;
import com.team5.backend.domain.review.repository.ReviewRepository;
import com.team5.backend.global.security.PrincipalDetails;
import com.team5.backend.global.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GroupBuyServiceTest {

    @Mock private GroupBuyRepository groupBuyRepository;
    @Mock private ProductRepository productRepository;
    @Mock private HistoryRepository historyRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private ReviewRepository reviewRepository;
    @Mock private DibsRepository dibsRepository;
    @Mock private JwtUtil jwtUtil;

    @InjectMocks
    private GroupBuyService groupBuyService;

    private Member testMember;
    private Product testProduct;
    private Category testCategory;
    private List<GroupBuy> groupBuys;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testMember = Member.builder()
                .memberId(1L)
                .email("user@example.com")
                .nickname("테스트유저")
                .name("홍길동")
                .password("password")
                .emailVerified(true)
                .build();

        testProduct = Product.builder()
                .productId(1L)
                .title("테스트 상품")
                .price(10000)
                .dibCount(5L)
                .build();

        testCategory = Category.builder()
                .categoryId(1L)
                .categoryType(null)
                .keyword(KeywordType.MAKEUP)
                .uid(101)
                .build();

        groupBuys = List.of(
                GroupBuy.builder()
                        .groupBuyId(1L)
                        .product(testProduct)
                        .targetParticipants(10)
                        .currentParticipantCount(1)
                        .round(1)
                        .deadline(LocalDateTime.now().plusDays(1))
                        .status(GroupBuyStatus.ONGOING)
                        .createdAt(LocalDateTime.now().minusDays(3))
                        .build(),
                GroupBuy.builder()
                        .groupBuyId(2L)
                        .product(testProduct)
                        .targetParticipants(15)
                        .currentParticipantCount(5)
                        .round(2)
                        .deadline(LocalDateTime.now().plusDays(2))
                        .status(GroupBuyStatus.ONGOING)
                        .createdAt(LocalDateTime.now().minusDays(1))
                        .build()
        );
    }

    @Test
    @DisplayName("공동구매 생성 - 성공적으로 생성되면 DTO 반환")
    void createGroupBuy_shouldReturnSavedGroupBuyResDto() {
        GroupBuyCreateReqDto req = GroupBuyCreateReqDto.builder()
                .productId(1L)
                .targetParticipants(10)
                .round(1)
                .deadline(LocalDateTime.now().plusDays(3))
                .build();

        GroupBuy saved = GroupBuy.builder()
                .groupBuyId(1L)
                .product(testProduct)
                .targetParticipants(10)
                .status(GroupBuyStatus.ONGOING)
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(groupBuyRepository.save(any(GroupBuy.class))).thenReturn(saved);

        GroupBuyResDto result = groupBuyService.createGroupBuy(req);

        assertNotNull(result);
        assertEquals(1L, result.getGroupBuyId());
        verify(groupBuyRepository).save(any(GroupBuy.class));
    }

    @Test
    @DisplayName("전체 공동구매 조회 - 정렬 포함")
    void getAllGroupBuys_shouldReturnPagedResult() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<GroupBuy> page = new PageImpl<>(groupBuys);

        when(groupBuyRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<GroupBuyResDto> result = groupBuyService.getAllGroupBuys(pageable, GroupBuySortField.LATEST);

        assertEquals(2, result.getTotalElements());
    }

    @Test
    @DisplayName("공동구매 단건 조회 - 존재하는 경우, 로그인 없이")
    void getGroupBuyById_shouldReturnDetailDtoWithoutLogin() {
        GroupBuy groupBuy = groupBuys.get(0);
        groupBuy.getProduct().updateCategory(testCategory);

        when(groupBuyRepository.findById(1L)).thenReturn(Optional.of(groupBuy));
        when(reviewRepository.findAverageRatingByProductId(groupBuy.getProduct().getProductId())).thenReturn(4.5);

        GroupBuyDetailResDto result = groupBuyService.getGroupBuyById(groupBuy.getGroupBuyId(), null);

        assertNotNull(result);
        assertEquals(groupBuy.getGroupBuyId(), result.getGroupBuyId());
        assertEquals(4.5, result.getAverageRate());
        assertFalse(result.isDibs()); // 로그인 안 한 경우
    }

    @Test
    @DisplayName("공동구매 단건 조회 - 로그인 상태에서 Dibs 한 경우")
    void getGroupBuyById_shouldReturnDetailDtoWithDibs() {
        // given
        GroupBuy groupBuy = groupBuys.get(0);
        groupBuy.getProduct().updateCategory(testCategory);

        PrincipalDetails userDetails = new PrincipalDetails(testMember, Map.of());

        when(groupBuyRepository.findById(1L)).thenReturn(Optional.of(groupBuy));
        when(reviewRepository.findAverageRatingByProductId(groupBuy.getProduct().getProductId())).thenReturn(4.0);
        when(dibsRepository.findByProduct_ProductIdAndMember_MemberId(
                groupBuy.getProduct().getProductId(),
                testMember.getMemberId())
        ).thenReturn(Optional.of(mock(com.team5.backend.domain.dibs.entity.Dibs.class)));

        // when
        GroupBuyDetailResDto result = groupBuyService.getGroupBuyById(groupBuy.getGroupBuyId(), userDetails);

        // then
        assertNotNull(result);
        assertEquals(groupBuy.getGroupBuyId(), result.getGroupBuyId());
        assertEquals(4.0, result.getAverageRate());
        assertTrue(result.isDibs()); // Dibs 했으므로 true
    }



    @Test
    @DisplayName("공동구매 수정 - 필드 전체 업데이트")
    void updateGroupBuy_shouldUpdateFields() {
        GroupBuyUpdateReqDto req = GroupBuyUpdateReqDto.builder()
                .targetParticipants(15)
                .currentParticipantCount(5)
                .round(2)
                .deadline(LocalDateTime.now().plusDays(2))
                .status(GroupBuyStatus.ONGOING)
                .build();

        GroupBuy existing = groupBuys.get(0);

        when(groupBuyRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(groupBuyRepository.save(any(GroupBuy.class))).thenReturn(existing);

        GroupBuyResDto result = groupBuyService.updateGroupBuy(1L, req);

        assertEquals(15, result.getTargetParticipants());
        verify(groupBuyRepository).save(existing);
    }

    @Test
    @DisplayName("PrincipalDetails 기반 공동구매 조회 - 최신순 정렬 포함")
    void getGroupBuysByMember_shouldReturnPagedResult() {
        // given
        PrincipalDetails principalDetails = new PrincipalDetails(testMember, Map.of());
        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<GroupBuy> mockPage = new PageImpl<>(groupBuys);

        when(historyRepository.findDistinctGroupBuysByMemberId(1L, pageable)).thenReturn(mockPage);

        // when
        Page<GroupBuyResDto> result = groupBuyService.getGroupBuysByMember(principalDetails, pageable);

        // then
        assertEquals(2, result.getTotalElements());
        verify(historyRepository).findDistinctGroupBuysByMemberId(1L, pageable);
    }


    @Test
    @DisplayName("공동구매 상태 일괄 업데이트 - 마감일 지난 경우 CLOSED 설정")
    void updateGroupBuyStatuses_shouldCloseExpiredGroupBuys() {
        GroupBuy expired = GroupBuy.builder()
                .groupBuyId(1L)
                .deadline(LocalDateTime.now().minusDays(1))
                .status(GroupBuyStatus.ONGOING)
                .build();

        List<GroupBuy> ongoing = List.of(expired);

        when(groupBuyRepository.findByStatus(GroupBuyStatus.ONGOING)).thenReturn(ongoing);
        when(groupBuyRepository.saveAll(any())).thenReturn(ongoing);

        groupBuyService.updateGroupBuyStatuses();

        assertEquals(GroupBuyStatus.CLOSED, expired.getStatus());
        verify(groupBuyRepository).saveAll(ongoing);
    }

    @Test
    @DisplayName("공동구매 부분 업데이트 - 일부 필드만 변경")
    void patchGroupBuy_shouldPartiallyUpdateFields() {
        GroupBuyPatchReqDto req = GroupBuyPatchReqDto.builder()
                .targetParticipants(30)
                .round(2)
                .build();

        GroupBuy existing = groupBuys.get(0); // ← 기존에 만든 거 재사용

        when(groupBuyRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(groupBuyRepository.save(any(GroupBuy.class))).thenReturn(existing);

        GroupBuyResDto result = groupBuyService.patchGroupBuy(1L, req);

        assertEquals(30, result.getTargetParticipants());
        assertEquals(2, result.getRound());
        assertNotNull(result.getProduct()); // product가 null 아님 확인
    }

    @Test
    @DisplayName("공동구매 삭제 - 성공적으로 삭제됨")
    void deleteGroupBuy_shouldInvokeRepositoryDelete() {
        groupBuyService.deleteGroupBuy(1L);
        verify(groupBuyRepository).deleteById(1L);
    }

    @Test
    @DisplayName("오늘 마감되는 공동구매 조회 - 날짜 필터링 포함")
    void getTodayDeadlineGroupBuys_shouldReturnFilteredGroupBuys() {
        Pageable pageable = PageRequest.of(0, 5);
        GroupBuy groupBuy = GroupBuy.builder()
                .groupBuyId(1L)
                .product(testProduct) // 추가
                .deadline(LocalDateTime.now().plusHours(3))
                .build();

        Page<GroupBuy> page = new PageImpl<>(List.of(groupBuy));
        when(groupBuyRepository.findByDeadlineBetween(any(), any(), any())).thenReturn(page);

        Page<GroupBuyResDto> result = groupBuyService.getTodayDeadlineGroupBuys(pageable, GroupBuySortField.LATEST);

        assertEquals(1, result.getTotalElements());
        verify(groupBuyRepository).findByDeadlineBetween(any(), any(), any());
    }

    @Test
    @DisplayName("공동구매 상태 조회 - 정상 반환")
    void getGroupBuyStatus_shouldReturnCorrectStatus() {
        GroupBuy gb = GroupBuy.builder()
                .groupBuyId(1L)
                .status(GroupBuyStatus.ONGOING)
                .build();

        when(groupBuyRepository.findById(1L)).thenReturn(Optional.of(gb));

        GroupBuyStatusResDto result = groupBuyService.getGroupBuyStatus(1L);

        assertEquals(GroupBuyStatus.ONGOING, result.getStatus());
    }

    @Test
    @DisplayName("공동구매 인기순 TOP3 조회 - Dib 기준")
    void getTop3GroupBuysByDibs_shouldReturnList() {
        when(groupBuyRepository.findTop3ByDibsOrder(any())).thenReturn(groupBuys);
        List<GroupBuyResDto> result = groupBuyService.getTop3GroupBuysByDibs();
        assertEquals(2, result.size());
        verify(groupBuyRepository).findTop3ByDibsOrder(any());
    }

    @Test
    @DisplayName("공동구매 마감 처리 - 상태 변경")
    void closeGroupBuy_shouldSetStatusToClosed() {
        GroupBuy gb = GroupBuy.builder()
                .groupBuyId(1L)
                .status(GroupBuyStatus.ONGOING)
                .build();

        when(groupBuyRepository.findById(1L)).thenReturn(Optional.of(gb));

        groupBuyService.closeGroupBuy(1L);

        assertEquals(GroupBuyStatus.CLOSED, gb.getStatus());
    }

    @Test
    @DisplayName("같은 카테고리 랜덤 공동구매 3개 조회")
    void getRandomTop3GroupBuysBySameCategory_shouldReturn3Items() {
        GroupBuy base = groupBuys.get(0);
        base.getProduct().updateCategory(testCategory);

        List<GroupBuy> randoms = List.of(base, groupBuys.get(1));
        Pageable pageable = PageRequest.of(0, 3);

        when(groupBuyRepository.findById(1L)).thenReturn(Optional.of(base));
        when(groupBuyRepository.findRandomTop3ByCategoryIdExcludingSelf(1L, 1L, pageable)).thenReturn(randoms);

        List<GroupBuyResDto> result = groupBuyService.getRandomTop3GroupBuysBySameCategory(1L);

        assertEquals(2, result.size());
        verify(groupBuyRepository).findRandomTop3ByCategoryIdExcludingSelf(1L, 1L, pageable);
    }

}
