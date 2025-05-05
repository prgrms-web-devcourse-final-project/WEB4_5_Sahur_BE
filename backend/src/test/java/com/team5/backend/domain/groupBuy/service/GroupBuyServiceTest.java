package com.team5.backend.domain.groupBuy.service;

import com.team5.backend.domain.category.entity.Category;
import com.team5.backend.domain.category.entity.KeywordType;
import com.team5.backend.domain.category.repository.CategoryRepository;
import com.team5.backend.domain.groupBuy.dto.*;
import com.team5.backend.domain.groupBuy.entity.GroupBuy;
import com.team5.backend.domain.groupBuy.entity.GroupBuySortField;
import com.team5.backend.domain.groupBuy.entity.GroupBuyStatus;
import com.team5.backend.domain.groupBuy.repository.GroupBuyRepository;
import com.team5.backend.domain.history.repository.HistoryRepository;
import com.team5.backend.domain.member.member.entity.Member;
import com.team5.backend.domain.product.entity.Product;
import com.team5.backend.domain.product.repository.ProductRepository;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GroupBuyServiceTest {

    @Mock private GroupBuyRepository groupBuyRepository;
    @Mock private ProductRepository productRepository;
    @Mock private HistoryRepository historyRepository;
    @Mock private CategoryRepository categoryRepository;
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
                .category(null)
                .keyword(KeywordType.DEFAULT)
                .uid(101)
                .build();

        groupBuys = List.of(
                GroupBuy.builder()
                        .groupBuyId(1L)
                        .product(testProduct)
                        .category(testCategory)
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
                        .category(testCategory)
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
                .categoryId(1L)
                .targetParticipants(10)
                .round(1)
                .deadline(LocalDateTime.now().plusDays(3))
                .build();

        GroupBuy saved = GroupBuy.builder()
                .groupBuyId(1L)
                .product(testProduct)
                .category(testCategory)
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
    @DisplayName("공동구매 단건 조회 - 존재하는 경우")
    void getGroupBuyById_shouldReturnDtoWhenFound() {
        when(groupBuyRepository.findById(1L)).thenReturn(Optional.of(groupBuys.get(0)));

        GroupBuyResDto result = groupBuyService.getGroupBuyById(1L);

        assertTrue(result instanceof GroupBuyResDto);
        assertEquals(1L, result.getGroupBuyId());
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
    @DisplayName("토큰 기반 공동구매 조회 - 최신순 정렬 포함")
    void getGroupBuysByToken_shouldReturnPagedResult() {
        // given
        String token = "Bearer fake.jwt.token";
        String extractedToken = "fake.jwt.token";

        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<GroupBuy> mockPage = new PageImpl<>(groupBuys);

        // when
        when(jwtUtil.isTokenBlacklisted(extractedToken)).thenReturn(false);
        when(jwtUtil.extractEmail(extractedToken)).thenReturn("user@example.com");
        when(jwtUtil.validateAccessTokenInRedis("user@example.com", extractedToken)).thenReturn(true);
        when(jwtUtil.extractMemberId(extractedToken)).thenReturn(1L);
        when(historyRepository.findDistinctGroupBuysByMemberId(1L, pageable)).thenReturn(mockPage);

        Page<GroupBuyResDto> result = groupBuyService.getGroupBuysByToken(token, pageable);

        // then
        assertEquals(2, result.getTotalElements());
        verify(jwtUtil).extractMemberId(extractedToken);
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
        assertNotNull(result.getProductId()); // productId가 null 아님 확인
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
                .category(testCategory)
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
}
