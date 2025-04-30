
package com.team5.backend.global.init;

import com.team5.backend.domain.category.entity.Category;
import com.team5.backend.domain.category.entity.CategoryType;
import com.team5.backend.domain.category.entity.KeywordType;
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
import java.util.ArrayList;
import java.util.List;

import static com.team5.backend.domain.category.entity.CategoryType.*;

@Component
@RequiredArgsConstructor
public class BaseInitData{

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

        // 카테고리 생성
        Category food = categoryRepository.save(Category.builder()
                .category(FOOD).
                build());
        Category digitalAppliance = categoryRepository.save(Category.builder().category(DIGITAL_APPLIANCE).build());
        Category beauty = categoryRepository.save(Category.builder().category(BEAUTY).build());

        List<CategoryType> categoryTypes = List.of(FOOD, DIGITAL_APPLIANCE, BEAUTY);
        List<Product> allProducts = new ArrayList<>();
        List<GroupBuy> allGroupBuys = new ArrayList<>();

        // 각 카테고리에 상품 5개씩 + GroupBuy 1개씩 생성
        for (CategoryType categoryType : categoryTypes) {
            for (int i = 1; i <= 5; i++) {
                Product product = productRepository.save(Product.builder()
                        .title(categoryType.name() + " 상품 " + i)
                        .price(10000 + i * 1000)
                        .imageUrl("http://example.com/" + categoryType.name().toLowerCase() + "/" + i + ".jpg")
                        .description(categoryType.name() + " 상품 " + i + " 설명입니다.")
                        .dibCount(0L)
                        .build());
                allProducts.add(product);

                // 해당 상품의 카테고리 엔티티 생성 및 저장
                Category category = categoryRepository.save(Category.builder()
                        .product(product)
                        .category(categoryType)
                        .keyword(KeywordType.DEFAULT)
                        .uid(100 + i)
                        .build());

                // GroupBuy 생성
                GroupBuy groupBuy = groupBuyRepository.save(GroupBuy.builder()
                        .product(product)
                        .category(category)
                        .targetParticipants(10)
                        .currentParticipantCount(1)
                        .round(1)
                        .deadline(LocalDateTime.now().plusDays(5))
                        .status(GroupBuyStatus.ONGOING)
                        .build());
                allGroupBuys.add(groupBuy);
            }
        }

        // 각 상품마다 리뷰어 3명이 리뷰 작성
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

        // 기본 사용자 찜 3개 (앞의 3개 상품)
        for (int i = 0; i < 3; i++) {
            dibsRepository.save(Dibs.builder()
                    .member(defaultUser)
                    .product(allProducts.get(i))
                    .build());
        }

        // 알림 3개
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

        // 참여 기록 1개 (첫 번째 groupBuy 기준)
        historyRepository.save(History.builder()
                .member(defaultUser)
                .product(allProducts.get(0))
                .groupBuy(allGroupBuys.get(0))
                .writable(true)
                .build());

        // 주문 1개
        Order order = orderRepository.save(Order.builder()
                .member(defaultUser)
                .status(OrderStatus.PAID)
                .totalPrice(allProducts.get(0).getPrice())
                .createdAt(LocalDateTime.now())
                .build());

        // 배송 1개
        Delivery delivery = Delivery.create(order,
                "서울시 테스트구 테스트동",
                1234,
                "010-1234-5678");
        deliveryRepository.save(delivery);

        // 결제 내역 1개
        Payment payment = Payment.create(
                order,
                "payment-001");
        paymentRepository.save(payment);

        // 상품 요청 1개
        productRequestRepository.save(ProductRequest.builder()
                .member(defaultUser)
                .category(food)
                .title("식품 요청합니다")
                .productUrl("http://example.com/requested-food")
                .etc("건강한 식품 추가해주세요")
                .status(ProductRequestStatus.WAITING)
                .build());

        // 공동구매 요청 1개
        groupBuyRequestRepository.save(GroupBuyRequest.builder()
                .product(allProducts.get(1))
                .member(defaultUser)
                .build());
    }
}

