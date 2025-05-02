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
import com.team5.backend.domain.notification.repository.NotificationRepository;
import com.team5.backend.domain.order.entity.Order;
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
    private final GroupBuyRequestRepository groupBuyRequestRepository;
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

            // Member
            Member member = Member.builder()
                    .name("홍길동")
                    .email("hong@example.com")
                    .password(passwordEncoder.encode("password123!"))
                    .nickname("길동이")
                    .address("부산")
                    .role(Role.USER)
                    .emailVerified(true)
                    .imageUrl("http://example.com/image.jpg")
                    .build();
            memberRepository.save(member);

            // Category
            Category category = Category.builder()
                    .category(CategoryType.DIGITAL_APPLIANCE)
                    .keyword(KeywordType.DEFAULT)
                    .uid(1)
                    .build();
            categoryRepository.save(category);

            // Product
            Product product = Product.builder()
                    .category(category)
                    .title("스마트폰")
                    .description("최신형 스마트폰입니다.")
                    .imageUrl("http://example.com/product.jpg")
                    .price(999000)
                    .createdAt(LocalDateTime.now())
                    .build();
            productRepository.save(product);

            // Product Request
            ProductRequest request = ProductRequest.builder()
                    .member(member)
                    .category(category)
                    .title("원하는 스마트폰")
                    .productUrl("http://request.com/item")
                    .etc("색상은 블랙으로요.")
                    .status(ProductRequestStatus.WAITING)
                    .createdAt(LocalDateTime.now())
                    .build();
            productRequestRepository.save(request);

            // Order
            Order order = Order.builder()
                    .member(member)
                    .product(product)
                    .productUid(1L)
                    .totalPrice(999000)
                    .status("ORDERED")
                    .quantity(1)
                    .createAt(LocalDateTime.now())
                    .build();
            orderRepository.save(order);

            // Payment
            Payment payment = Payment.create(
                    order,
                    member.getEmail(),
                    "CREDIT_CARD",
                    999000,
                    "PAYMENT_COMPLETED",
                    LocalDateTime.now()
            );
            paymentRepository.save(payment);

            // GroupBuy
            GroupBuy groupBuy = GroupBuy.builder()
                    .productId(product.getId())
                    .memberId(member.getId())
                    .targetParticipants(10)
                    .currentParticipantsCount(1)
                    .round(1)
                    .deadline(LocalDateTime.now().plusDays(7))
                    .status("OPEN")
                    .createdAt(LocalDateTime.now())
                    .build();
            groupBuyRepository.save(groupBuy);

            // GroupBuyRequest
            GroupBuyRequest groupBuyRequest = GroupBuyRequest.builder()
                    .groupBuyId(groupBuy.getId())
                    .productId(product.getId())
                    .memberId(member.getId())
                    .createdAt(LocalDateTime.now())
                    .build();
            groupBuyRequestRepository.save(groupBuyRequest);

            // Notification
            Notification notification = Notification.builder()
                    .memberId(member.getId())
                    .type("ORDER")
                    .title("주문이 완료되었습니다.")
                    .message("주문하신 상품이 곧 배송됩니다.")
                    .url("/orders/" + order.getId())
                    .read(false)
                    .createdAt(LocalDateTime.now())
                    .build();
            notificationRepository.save(notification);

            // Delivery
            Delivery delivery = Delivery.builder()
                    .order(order)
                    .address(member.getAddress())
                    .status("SHIPPING")
                    .shipping("우체국택배")
                    .build();
            deliveryRepository.save(delivery);

            // Dibs
            Dibs dibs = Dibs.builder()
                    .member(member)
                    .product(product)
                    .build();
            dibsRepository.save(dibs);

            // History
            History history = History.builder()
                    .member(member)
                    .product(product)
                    .groupBuy(groupBuy)
                    .order(order)
                    .writable(true)
                    .createdAt(LocalDateTime.now())
                    .build();
            historyRepository.save(history);

            // Review
            Review review = Review.builder()
                    .member(member)
                    .product(product)
                    .history(history)
                    .comment("아주 만족스럽습니다!")
                    .rate(5)
                    .createdAt(LocalDateTime.now())
                    .imageUrl("http://example.com/review.jpg")
                    .build();
            reviewRepository.save(review);
        }
    }
}
