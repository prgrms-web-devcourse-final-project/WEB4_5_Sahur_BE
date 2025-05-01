package com.team5.backend.global.init;

import com.team5.backend.domain.category.entity.*;
import com.team5.backend.domain.category.repository.CategoryRepository;
import com.team5.backend.domain.delivery.entity.Delivery;
import com.team5.backend.domain.delivery.repository.DeliveryRepository;
import com.team5.backend.domain.dibs.entity.Dibs;
import com.team5.backend.domain.dibs.repository.DibsRepository;
import com.team5.backend.domain.groupBuy.entity.GroupBuy;
import com.team5.backend.domain.groupBuy.entity.GroupBuyStatus;
import com.team5.backend.domain.groupBuy.repository.GroupBuyRepository;
import com.team5.backend.domain.history.entity.History;
import com.team5.backend.domain.history.repository.HistoryRepository;
import com.team5.backend.domain.member.admin.entity.GroupBuyRequest;
import com.team5.backend.domain.member.admin.entity.ProductRequest;
import com.team5.backend.domain.member.admin.entity.ProductRequestStatus;
import com.team5.backend.domain.member.admin.repository.GroupBuyRequestRepository;
import com.team5.backend.domain.member.admin.repository.ProductRequestRepository;
import com.team5.backend.domain.member.member.entity.Member;
import com.team5.backend.domain.member.member.entity.Role;
import com.team5.backend.domain.member.member.repository.MemberRepository;
import com.team5.backend.domain.notification.entity.Notification;
import com.team5.backend.domain.notification.entity.NotificationType;
import com.team5.backend.domain.notification.repository.NotificationRepository;
import com.team5.backend.domain.order.entity.Order;
import com.team5.backend.domain.order.entity.OrderStatus;
import com.team5.backend.domain.order.repository.OrderRepository;
import com.team5.backend.domain.payment.entity.Payment;
import com.team5.backend.domain.payment.repository.PaymentRepository;
import com.team5.backend.domain.product.entity.Product;
import com.team5.backend.domain.product.repository.ProductRepository;
import com.team5.backend.domain.review.entity.Review;
import com.team5.backend.domain.review.repository.ReviewRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

import static com.team5.backend.domain.category.entity.CategoryType.*;

@Component
@RequiredArgsConstructor
public class BaseInitData {

    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final GroupBuyRepository groupBuyRepository;
    private final ReviewRepository reviewRepository;
    private final DibsRepository dibsRepository;
    private final NotificationRepository notificationRepository;
    private final HistoryRepository historyRepository;
    private final OrderRepository orderRepository;
    private final DeliveryRepository deliveryRepository;
    private final PaymentRepository paymentRepository;
    private final ProductRequestRepository productRequestRepository;
    private final GroupBuyRequestRepository groupBuyRequestRepository;

    @PostConstruct
    public void init() {
        // 사용자 생성
        Member admin = memberRepository.save(Member.builder()
                .email("admin@team5.com")
                .nickname("관리자")
                .name("이름")
                .password("encodedPassword")
                .address("서울시 어딘가")
                .emailVerified(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .role(Role.ADMIN)
                .build());

        Member defaultUser = memberRepository.save(Member.builder()
                .email("user@team5.com")
                .nickname("기본유저")
                .name("이름")
                .password("encodedPassword")
                .address("서울시 어딘가")
                .emailVerified(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .role(Role.USER)
                .build());

        Member reviewer1 = memberRepository.save(Member.builder()
                .email("rev1@team5.com")
                .nickname("리뷰1")
                .name("이름")
                .password("encodedPassword")
                .address("서울시 어딘가")
                .emailVerified(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .role(Role.USER)
                .build());

        Member reviewer2 = memberRepository.save(Member.builder()
                .email("rev2@team5.com")
                .nickname("리뷰2")
                .name("이름")
                .password("encodedPassword")
                .address("서울시 어딘가")
                .emailVerified(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .role(Role.USER)
                .build());

        Member reviewer3 = memberRepository.save(Member.builder()
                .email("rev3@team5.com")
                .nickname("리뷰3")
                .name("이름")
                .password("encodedPassword")
                .address("서울시 어딘가")
                .emailVerified(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .role(Role.USER)
                .build());

        // 카테고리 3개 미리 생성
        Category food = categoryRepository.save(Category.builder()
                .category(FOOD)
                .keyword(KeywordType.DEFAULT)
                .uid(101).build());

        Category digital = categoryRepository.save(Category.builder()
                .category(DIGITAL_APPLIANCE)
                .keyword(KeywordType.DEFAULT)
                .uid(102).build());

        Category beauty = categoryRepository.save(Category.builder()
                .category(BEAUTY)
                .keyword(KeywordType.DEFAULT)
                .uid(103).build());

        Map<CategoryType, Category> categoryMap = Map.of(
                FOOD, food,
                DIGITAL_APPLIANCE, digital,
                BEAUTY, beauty
        );

        List<Product> allProducts = new ArrayList<>();
        List<GroupBuy> allGroupBuys = new ArrayList<>();

        // 각 카테고리에 상품 5개씩 + GroupBuy 생성
        for (CategoryType categoryType : categoryMap.keySet()) {
            Category category = categoryMap.get(categoryType);
            for (int i = 1; i <= 5; i++) {
                Product product = productRepository.save(Product.builder()
                        .title(categoryType.name() + " 상품 " + i)
                        .price(10000 + i * 1000)
                        .imageUrl("http://example.com/" + categoryType.name().toLowerCase() + "/" + i + ".jpg")
                        .description(categoryType.name() + " 상품 " + i + " 설명입니다.")
                        .dibCount(0L)
                        .category(category)
                        .createdAt(LocalDateTime.now())
                        .build());
                allProducts.add(product);

                GroupBuy groupBuy = groupBuyRepository.save(GroupBuy.builder()
                        .product(product)
                        .category(category)
                        .targetParticipants(10)
                        .currentParticipantCount(1)
                        .round(1)
                        .deadline(LocalDateTime.now().plusDays(5))
                        .status(GroupBuyStatus.ONGOING)
                        .createdAt(LocalDateTime.now())
                        .build());
                allGroupBuys.add(groupBuy);
            }
        }

        // 리뷰 작성
        List<Member> reviewers = List.of(reviewer1, reviewer2, reviewer3);
        for (Product product : allProducts) {
            for (Member reviewer : reviewers) {
                reviewRepository.save(Review.builder()
                        .product(product)
                        .member(reviewer)
                        .comment(product.getTitle() + "에 대한 리뷰 by " + reviewer.getNickname())
                        .rate(4)
                        .createdAt(LocalDateTime.now())
                        .build());
            }
        }

        // 찜 등록
        for (int i = 0; i < 3; i++) {
            dibsRepository.save(Dibs.builder()
                    .member(defaultUser)
                    .product(allProducts.get(i))
                    .status(false)
                    .build());
        }

        // 알림 등록
        for (int i = 1; i <= 3; i++) {
            notificationRepository.save(Notification.builder()
                    .member(defaultUser)
                    .type(NotificationType.ETC)
                    .title("알림 제목 " + i)
                    .message("알림 내용 " + i)
                    .url("/notice/" + i)
                    .read(false)
                    .createdAt(LocalDateTime.now())
                    .build());
        }

        // 주문
        Order order = orderRepository.save(Order.builder()
                .member(defaultUser)
                .groupBuy(allGroupBuys.get(0))
                .status(OrderStatus.PAID)
                .totalPrice(allProducts.get(0).getPrice())
                .createdAt(LocalDateTime.now())
                .build());

        // 히스토리
        historyRepository.save(History.builder()
                .member(defaultUser)
                .product(allProducts.get(0))
                .groupBuy(allGroupBuys.get(0))
                .order(order)
                .writable(true)
                .build());

        // 배송
        deliveryRepository.save(Delivery.create(order,
                "서울시 테스트구 테스트동", 1234, "010-1234-5678"));

        // 결제
        paymentRepository.save(Payment.create(order, "payment-001"));

        // 상품 요청
        productRequestRepository.save(ProductRequest.builder()
                .member(defaultUser)
                .category(food)
                .title("식품 요청합니다")
                .productUrl("http://example.com/requested-food")
                .etc("건강한 식품 추가해주세요")
                .status(ProductRequestStatus.WAITING)
                .build());

        // 공동구매 요청
        groupBuyRequestRepository.save(GroupBuyRequest.builder()
                .product(allProducts.get(1))
                .member(defaultUser)
                .build());
    }
}
