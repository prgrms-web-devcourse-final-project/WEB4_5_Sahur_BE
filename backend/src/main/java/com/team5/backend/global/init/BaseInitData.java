// 전체 리팩토링된 BaseInitData - 모든 엔티티 흐름 연결

package com.team5.backend.global.init;

import com.team5.backend.domain.category.entity.*;
import com.team5.backend.domain.category.repository.CategoryRepository;
import com.team5.backend.domain.delivery.entity.*;
import com.team5.backend.domain.delivery.repository.DeliveryRepository;
import com.team5.backend.domain.dibs.entity.Dibs;
import com.team5.backend.domain.dibs.repository.DibsRepository;
import com.team5.backend.domain.groupBuy.entity.*;
import com.team5.backend.domain.groupBuy.repository.GroupBuyRepository;
import com.team5.backend.domain.history.entity.History;
import com.team5.backend.domain.history.repository.HistoryRepository;
import com.team5.backend.domain.member.member.entity.Member;
import com.team5.backend.domain.member.member.entity.Role;
import com.team5.backend.domain.member.member.repository.MemberRepository;
import com.team5.backend.domain.member.admin.entity.ProductRequest;
import com.team5.backend.domain.member.admin.entity.ProductRequestStatus;
import com.team5.backend.domain.member.admin.repository.ProductRequestRepository;
import com.team5.backend.domain.notification.entity.*;
import com.team5.backend.domain.notification.repository.NotificationRepository;
import com.team5.backend.domain.order.entity.*;
import com.team5.backend.domain.order.repository.OrderRepository;
import com.team5.backend.domain.payment.entity.Payment;
import com.team5.backend.domain.payment.repository.PaymentRepository;
import com.team5.backend.domain.product.entity.Product;
import com.team5.backend.domain.product.repository.ProductRepository;
import com.team5.backend.domain.review.entity.Review;
import com.team5.backend.domain.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
@Transactional
public class BaseInitData implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductRequestRepository productRequestRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final ReviewRepository reviewRepository;
    private final GroupBuyRepository groupBuyRepository;
    private final NotificationRepository notificationRepository;
    private final DeliveryRepository deliveryRepository;
    private final DibsRepository dibsRepository;
    private final HistoryRepository historyRepository;

    private static final List<String> PRODUCT_URLS = List.of(
            "https://example.com/item1",
            "https://example.com/item2",
            "https://example.com/item3"
    );

    private static final List<String> NAMES = List.of("김민지", "박철수", "이영희", "정다은", "최준혁", "한서준", "오유진", "유재석", "신동엽", "장도연");
    private static final List<String> NICKS = List.of("코딩천재", "디버깅왕", "파라미터수집가", "테스트의신", "프론트킬러", "백엔드마스터", "DB매니아", "API지배자", "모니터링러", "도커장인");
    private static final List<String> ADDRS = List.of("서울", "부산", "인천", "대구", "광주", "대전", "수원", "울산", "제주", "청주");
    private static final List<String> REVIEWS = List.of("정말 만족합니다!", "배송이 느렸어요", "품질이 별로예요", "친구 추천으로 샀어요", "다시 구매하고 싶어요");
    private static final List<String> PRODUCT_TITLES = List.of("갤럭시 S24", "아이폰 15", "에어팟 맥스", "키보드", "안마의자", "커피머신", "책상", "시계", "사전", "헤드폰");
    private static final List<String> PRODUCT_DESCS = List.of("최신 기능", "가성비 최고", "학생 필수", "부모님 선물", "편리함", "튼튼함", "예쁨");
    private static final List<NotificationType> NOTI_TYPES = Arrays.asList(NotificationType.ORDER, NotificationType.MESSAGE, NotificationType.EVENT, NotificationType.ETC);

    @Override
    public void run(String... args) {
        if (memberRepository.count() == 0) {
            List<CategoryType> categoryTypes = Arrays.asList(CategoryType.values());
            List<Category> categories = new ArrayList<>();
            for (int i = 0; i < categoryTypes.size(); i++) {
                categories.add(categoryRepository.save(Category.builder()
                        .category(categoryTypes.get(i))
                        .keyword(KeywordType.DEFAULT)
                        .uid(i + 1)
                        .build()));
            }

            List<Member> members = new ArrayList<>();
            for (int i = 0; i < 30; i++) {
                members.add(memberRepository.save(Member.builder()
                        .name(NAMES.get(i % NAMES.size()))
                        .email("member" + i + "@example.com")
                        .password(passwordEncoder.encode("1234"))
                        .nickname(NICKS.get(i % NICKS.size()))
                        .address(ADDRS.get(i % ADDRS.size()))
                        .role(Role.USER)
                        .emailVerified(true)
                        .imageUrl("http://example.com/user" + i + ".jpg")
                        .build()));
            }

            List<Product> products = new ArrayList<>();
            for (int i = 0; i < 30; i++) {
                Category category = categories.get(i % categories.size());
                Member requester = members.get(i % members.size());
                String title = PRODUCT_TITLES.get(i % PRODUCT_TITLES.size()) + " 요청형";

                productRequestRepository.save(ProductRequest.builder()
                        .member(requester)
                        .category(category)
                        .title(title)
                        .productUrl(PRODUCT_URLS.get(i % PRODUCT_URLS.size()))
                        .etc("원하는 색상은 블랙입니다.")
                        .status(ProductRequestStatus.APPROVED)
                        .createdAt(LocalDateTime.now())
                        .build());

                products.add(productRepository.save(Product.builder()
                                .category(category)
                                .title(title)
                                .description(PRODUCT_DESCS.get(i % PRODUCT_DESCS.size()))
                                .imageUrl("http://example.com/prod" + i + ".jpg")
                                .price(10000 + i * 1000)
                        .dibCount(ThreadLocalRandom.current().nextLong(1, 50))
                        .createdAt(LocalDateTime.now().minusDays(i % 7))
                        .build()));
            }

            for (Product product : products) {
                GroupBuy groupBuy = groupBuyRepository.save(GroupBuy.builder()
                        .product(product)
                        .targetParticipants(10)
                        .currentParticipantCount(ThreadLocalRandom.current().nextInt(1, 10))
                        .round(ThreadLocalRandom.current().nextInt(1, 4))
                        .deadline(LocalDateTime.now().plusDays(7))
                        .status(ThreadLocalRandom.current().nextBoolean() ? GroupBuyStatus.ONGOING : GroupBuyStatus.CLOSED)
                        .build());

                for (int j = 0; j < 3; j++) {
                    Member member = members.get((j + product.getProductId().intValue()) % members.size());
                    Order order = orderRepository.save(Order.create(member, groupBuy, product, ThreadLocalRandom.current().nextInt(1, 3)));
                    paymentRepository.save(Payment.create(order, UUID.randomUUID().toString()));

                    DeliveryStatus deliveryStatus = DeliveryStatus.values()[ThreadLocalRandom.current().nextInt(DeliveryStatus.values().length)];
                    deliveryRepository.save(Delivery.builder()
                            .order(order)
                            .address(member.getAddress())
                            .pccc(null)
                            .contact("010-" + String.format("%04d", new Random().nextInt(10000)) + "-" + String.format("%04d", new Random().nextInt(10000)))
                            .status(deliveryStatus)
                            .shipping("우체국택배")
                            .build());

                    dibsRepository.save(Dibs.builder()
                            .member(member)
                            .product(product)
                            .build());

                    History history = historyRepository.save(History.builder()
                            .member(member)
                            .product(product)
                            .groupBuy(groupBuy)
                            .order(order)
                            .writable(j % 2 == 0)
                            .createdAt(LocalDateTime.now())
                            .build());

                    if (j % 2 == 0) {
                        reviewRepository.save(Review.builder()
                                .member(member)
                                .product(product)
                                .history(history)
                                .comment(REVIEWS.get(j % REVIEWS.size()))
                                .rate(3 + (j % 3))
                                .createdAt(LocalDateTime.now())
                                .imageUrl("http://example.com/review" + product.getProductId() + ".jpg")
                                .build());
                    }

                    notificationRepository.save(Notification.builder()
                            .member(member)
                            .type(NOTI_TYPES.get(j % NOTI_TYPES.size()))
                            .title(NOTI_TYPES.get(j % NOTI_TYPES.size()).name() + " 알림")
                            .message("이벤트 발생: " + NOTI_TYPES.get(j % NOTI_TYPES.size()).name())
                            .url("/orders/" + order.getOrderId())
                            .read(false)
                            .createdAt(LocalDateTime.now())
                            .build());
                }
            }
        }
    }
}

