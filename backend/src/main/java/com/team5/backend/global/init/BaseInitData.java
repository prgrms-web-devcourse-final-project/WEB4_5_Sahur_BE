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
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional
public class BaseInitData implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final ReviewRepository reviewRepository;
    private final GroupBuyRepository groupBuyRepository;
    private final NotificationRepository notificationRepository;
    private final DeliveryRepository deliveryRepository;
    private final DibsRepository dibsRepository;
    private final HistoryRepository historyRepository;

    @Override
    public void run(String... args) {
        initData();
    }

    private void initData() {
        if (memberRepository.count() == 0) {

            List<CategoryType> categoryTypes = Arrays.asList(
                    CategoryType.FASHION_CLOTHES,
                    CategoryType.FASHION_ACCESSORY,
                    CategoryType.BEAUTY,
                    CategoryType.DIGITAL_APPLIANCE,
                    CategoryType.FURNITURE,
                    CategoryType.LIVING,
                    CategoryType.FOOD,
                    CategoryType.SPORTS,
                    CategoryType.CAR,
                    CategoryType.BOOK,
                    CategoryType.KIDS,
                    CategoryType.PET
            );

            for (int i = 0; i < categoryTypes.size(); i++) {
                Category category = categoryRepository.save(Category.builder()
                        .category(categoryTypes.get(i))
                        .keyword(KeywordType.DEFAULT)
                        .uid(i + 1)
                        .build());

                Member member = memberRepository.save(Member.builder()
                        .name("사용자" + (i + 1))
                        .email("user" + (i + 1) + "@example.com")
                        .password(passwordEncoder.encode("password123!"))
                        .nickname("유저" + (i + 1))
                        .address((i % 2 == 0 ? "서울" : "부산"))
                        .role(Role.USER)
                        .emailVerified(true)
                        .imageUrl("http://example.com/user" + (i + 1) + ".jpg")
                        .build());

                Product product = productRepository.save(Product.builder()
                        .category(category)
                        .title(category.getCategory().name() + " 상품")
                        .description("이것은 " + category.getCategory().name() + " 카테고리의 상품입니다.")
                        .imageUrl("http://example.com/" + category.getCategory().name().toLowerCase() + ".jpg")
                        .price(10000 * (i + 1))
                        .createdAt(LocalDateTime.now())
                        .build());

                GroupBuy groupBuy = groupBuyRepository.save(GroupBuy.builder()
                        .product(product)
                        .category(category)
                        .targetParticipants(5 + i)
                        .currentParticipantCount(1)
                        .round(1)
                        .deadline(LocalDateTime.now().plusDays(7))
                        .status(GroupBuyStatus.ONGOING)
                        .build());

                Order order = orderRepository.save(Order.create(member, groupBuy, product, 1));

                paymentRepository.save(Payment.create(order, "payment-key-cat" + i));

                notificationRepository.save(Notification.builder()
                        .member(member)
                        .type(NotificationType.ORDER)
                        .title("[알림] 주문이 완료되었습니다.")
                        .message("상품 주문이 완료되었습니다. 주문번호: " + order.getOrderId())
                        .url("/orders/" + order.getOrderId())
                        .read(false)
                        .build());

                deliveryRepository.save(Delivery.builder()
                        .order(order)
                        .address(member.getAddress())
                        .pccc(null)
                        .contact("010-1000-10" + String.format("%02d", i))
                        .status(DeliveryStatus.PREPARING)
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
                        .writable(true)
                        .createdAt(LocalDateTime.now())
                        .build());

                reviewRepository.save(Review.builder()
                        .member(member)
                        .product(product)
                        .history(history)
                        .comment("카테고리 " + category.getCategory().name() + "의 리뷰입니다.")
                        .rate(5 - (i % 3))
                        .createdAt(LocalDateTime.now())
                        .imageUrl("http://example.com/review_cat" + i + ".jpg")
                        .build());
            }
        }
    }
}
