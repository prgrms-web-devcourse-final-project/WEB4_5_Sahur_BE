package com.team5.backend.global.init;

import com.team5.backend.domain.category.entity.Category;
import com.team5.backend.domain.category.entity.CategoryType;
import com.team5.backend.domain.category.entity.KeywordType;
import com.team5.backend.domain.category.repository.CategoryRepository;
import com.team5.backend.domain.delivery.entity.Delivery;
import com.team5.backend.domain.delivery.entity.DeliveryStatus;
import com.team5.backend.domain.delivery.repository.DeliveryRepository;
import com.team5.backend.domain.dibs.entity.Dibs;
import com.team5.backend.domain.dibs.repository.DibsRepository;
import com.team5.backend.domain.groupBuy.entity.GroupBuy;
import com.team5.backend.domain.groupBuy.entity.GroupBuyStatus;
import com.team5.backend.domain.groupBuy.repository.GroupBuyRepository;
import com.team5.backend.domain.history.entity.History;
import com.team5.backend.domain.history.repository.HistoryRepository;
import com.team5.backend.domain.member.productrequest.entity.ProductRequest;
import com.team5.backend.domain.member.productrequest.entity.ProductRequestStatus;
import com.team5.backend.domain.member.productrequest.repository.ProductRequestRepository;
import com.team5.backend.domain.member.member.entity.Member;
import com.team5.backend.domain.member.member.entity.Role;
import com.team5.backend.domain.member.member.repository.MemberRepository;
import com.team5.backend.domain.notification.entity.Notification;
import com.team5.backend.domain.notification.entity.NotificationType;
import com.team5.backend.domain.notification.repository.NotificationRepository;
import com.team5.backend.domain.order.entity.Order;
import com.team5.backend.domain.order.repository.OrderRepository;
import com.team5.backend.domain.order.service.OrderIdGenerator;
import com.team5.backend.domain.payment.entity.Payment;
import com.team5.backend.domain.payment.repository.PaymentRepository;
import com.team5.backend.domain.product.entity.Product;
import com.team5.backend.domain.product.repository.ProductRepository;
import com.team5.backend.domain.review.entity.Review;
import com.team5.backend.domain.review.repository.ReviewRepository;
import com.team5.backend.global.entity.Address;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

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
    private final OrderIdGenerator orderIdGenerator;

    private final List<String> reviewComments = List.of(
            "배송도 빠르고 제품도 만족해요!",
            "디자인이 깔끔하고 성능도 좋습니다.",
            "가격 대비 괜찮은 선택이었어요.",
            "재구매 의사 있습니다!",
            "기대한 만큼의 품질은 아니네요.",
            "색상이 사진과 조금 달라요.",
            "친구 추천으로 샀는데 잘 산 것 같아요.",
            "설명서가 불친절해서 아쉬웠어요.",
            "조립이 쉬워서 만족합니다.",
            "소음이 좀 있지만 괜찮은 편입니다."
    );

    private final List<String> productTitles = List.of(
            "트렌디한 여름 반팔티", "프리미엄 가죽 지갑", "촉촉한 수분 크림", "고출력 블루투스 스피커",
            "모던 디자인 책상", "다기능 전기밥솥", "견고한 철제 선반", "유기농 곡물 세트",
            "무릎 보호 스포츠 레깅스", "차량용 방향제", "베스트셀러 에세이집", "친환경 유아용 식기", "고양이 자동 화장실"
    );

    private final List<String> productDescriptions = List.of(
            "여름에 입기 좋은 시원한 소재의 반팔티입니다.",
            "소가죽으로 제작된 고급스러운 남성용 지갑.",
            "건조한 피부를 위한 보습 특화 크림입니다.",
            "실내외 모두 사용 가능한 대출력 스피커.",
            "공간 활용에 탁월한 모던 스타일의 책상.",
            "다양한 기능을 탑재한 최신형 전기밥솥.",
            "튼튼하고 조립이 쉬운 철제 선반입니다.",
            "100% 유기농 인증을 받은 건강한 곡물 세트.",
            "운동 시 무릎을 보호하는 기능성 레깅스.",
            "장시간 지속되는 차량용 방향제.",
            "많은 이들에게 감동을 준 인기 에세이.",
            "아이 건강을 생각한 친환경 식기 세트.",
            "편리한 청소 기능이 탑재된 고양이 화장실."
    );

    @Override
    public void run(String... args) {
        if (memberRepository.count() == 0) {
            List<Category> categories = new ArrayList<>();
            int uid = 1;
            for (CategoryType type : CategoryType.values()) {
                if (type == CategoryType.ALL) continue;
                for (KeywordType keyword : KeywordType.ofParent(type)) {
                    categories.add(categoryRepository.save(
                            Category.builder()
                                    .categoryType(type)
                                    .keyword(keyword)
                                    .uid(uid++)
                                    .build()
                    ));
                }
            }

            List<Member> members = List.of(
                    createMember("이수민", "alice@example.com", "수민짱", new Address("04524", "서울 마포구 월드컵북로 396", "102동 1101호"), "user_alice.jpg"),
                    createMember("박지훈", "bob@example.com", "지훈이", new Address("34121", "대전 서구 둔산로 123", "301호"), "user_bob.jpg"),
                    createMember("최유리", "carol@example.com", "율무차", new Address("48058", "부산 해운대구 센텀서로 30", "1501호"), "user_carol.jpg"),
                    createMember("정예린", "yerin@example.com", "예린스타", new Address("21945", "인천 연수구 송도과학로 16", "A동 1804호"), "user_yerin.jpg"),
                    createMember("김태호", "taeho@example.com", "호박고구마", new Address("61177", "광주 북구 설죽로 150", "101동 202호"), "user_taeho.jpg")
            );

            Member admin = memberRepository.save(Member.builder()
                    .name("관리자")
                    .email("admin@example.com")
                    .password(passwordEncoder.encode("admin123!"))
                    .nickname("관리자계정")
                    .address(new Address("00000", "서울 종로구 청와대로 1", "청사 101호"))
                    .role(Role.ADMIN)
                    .deleted(false)
                    .emailVerified(true)
                    .imageUrl("http://example.com/admin.jpg")
                    .build());

            for (int i = 0; i < 62; i++) {
                Member requester = members.get(i % members.size());
                Category category = categories.get(i % categories.size());
                String title = productTitles.get(i % productTitles.size());
                String description = productDescriptions.get(i % productDescriptions.size());
                String imageUrl = "https://i.pravatar.cc/150?img=" + i + ".jpg";

                productRequestRepository.save(ProductRequest.builder()
                        .member(requester)
                        .category(category)
                        .title(title + " 요청")
                        .productUrl("https://i.pravatar.cc/150?img=" + i + ".jpg")
                        .description("옵션: 다양함 / 이미지: " + imageUrl)
                        .status(i % 3 == 0
                                ? ProductRequestStatus.APPROVED
                                : ProductRequestStatus.WAITING)
                        .createdAt(LocalDateTime.now().minusDays(i % 7))
                        .build());

                Product product = productRepository.save(Product.builder()
                        .category(category)
                        .title(title)
                        .description(description)
                        .imageUrl(List.of("https://i.pravatar.cc/150?img=" + i + ".jpg"))
                        .price((int) (100000 + (i * 7000L)))
                        .dibCount((long) (3 + (i % 10)))
                        .createdAt(LocalDateTime.now().minusDays(i % 5))
                        .build());

                GroupBuy groupBuy = groupBuyRepository.save(GroupBuy.builder()
                        .product(product)
                        .targetParticipants(5 + (i % 4))
                        .currentParticipantCount((i % 6) + 1)
                        .round(1 + (i % 3))
                        .deadline(LocalDateTime.now().plusDays(5 - (i % 3)))
                        .status(GroupBuyStatus.ONGOING)
                        .build());

                Member buyer = members.get((i + 1) % members.size());
                Long orderId = orderIdGenerator.generateOrderId();
                Order order = orderRepository.save(Order.create(orderId, buyer, groupBuy, product, 1));
                order.markAsPaid();
                paymentRepository.save(Payment.create(order, UUID.randomUUID().toString()));

                deliveryRepository.save(Delivery.builder()
                        .order(order)
                        .address(buyer.getAddress())
                        .contact("010-0000-00" + String.format("%02d", i))
                        .status(DeliveryStatus.values()[i % DeliveryStatus.values().length])
                        .shipping("TRK" + String.format("%07d", i * 37))
                        .build());

                dibsRepository.save(Dibs.builder().member(buyer).product(product).build());
                dibsRepository.save(Dibs.builder().member(requester).product(product).build());

                boolean shouldWriteReview = i % 2 == 0;

                History history = historyRepository.save(History.builder()
                        .member(buyer)
                        .product(product)
                        .groupBuy(groupBuy)
                        .order(order)
                        .writable(!shouldWriteReview)  // 리뷰를 쓸 거면 false로 저장, 아닐 땐 true 유지
                        .createdAt(LocalDateTime.now())
                        .build());

                if (shouldWriteReview) {
                    reviewRepository.save(Review.builder()
                            .member(buyer)
                            .product(product)
                            .history(history)
                            .comment(reviewComments.get(i % reviewComments.size()))
                            .rate(3 + (i % 3))
                            .createdAt(LocalDateTime.now())
                            .imageUrl(List.of("https://example.com/review/img" + i + ".jpg"))
                            .build());
                }

                notificationRepository.save(Notification.builder()
                        .member(buyer)
                        .type(NotificationType.ORDER)
                        .title("주문 완료 알림")
                        .message(product.getTitle() + " 주문이 완료되었습니다.")
                        .url("/orders/" + order.getOrderId())
                        .isRead(false)
                        .createdAt(LocalDateTime.now())
                        .build());

                if (i % 4 == 0) {
                    notificationRepository.save(Notification.builder()
                            .member(buyer)
                            .type(NotificationType.EVENT)
                            .title("이벤트 소식 #" + i)
                            .message("신규 혜택 오픈!")
                            .url("/events/" + i)
                            .isRead(false)
                            .createdAt(LocalDateTime.now())
                            .build());
                } else {
                    notificationRepository.save(Notification.builder()
                            .member(requester)
                            .type(NotificationType.ETC)
                            .title("시스템 공지")
                            .message("정기 점검 예정 안내")
                            .url("/notice")
                            .isRead(false)
                            .createdAt(LocalDateTime.now())
                            .build());
                }
            }
        }
    }


    private Member createMember(String name, String email, String nickname, Address address, String imageFile) {
        return memberRepository.save(Member.builder()
                .name(name)
                .email(email)
                .password(passwordEncoder.encode("password123!"))
                .nickname(nickname)
                .address(address)
                .role(Role.USER)
                .deleted(false)
                .emailVerified(true)
                .imageUrl("http://example.com/" + imageFile)
                .build());
    }
}
