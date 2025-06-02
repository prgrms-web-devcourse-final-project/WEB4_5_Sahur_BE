import {
  Badge,
  Button,
  Card,
  Image,
  Stack,
  Alert,
  Spinner,
} from "react-bootstrap";
import FilterButtonGroup from "./FilterButtonGroup";
import { useNavigate } from "react-router-dom";
import React, { useState, useEffect } from "react";
import styles from "./MyPageOrders.module.scss";

const MyPageOrders = () => {
  const navigate = useNavigate();
  const [activeFilter, setActiveFilter] = useState("ALL");
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // API 호출 함수
  const fetchOrders = async (status = null) => {
    try {
      setLoading(true);

      let url = `${process.env.REACT_APP_API_URL}/api/v1/orders/me`;
      if (status && status !== "ALL") {
        url += `?status=${status}`;
      }

      const response = await fetch(url, {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
        },
        credentials: "include",
      });

      if (!response.ok) {
        throw new Error("주문 내역을 불러오는데 실패했습니다.");
      }

      const data = await response.json();
      setOrders(data.data.content || []);
      setError(null);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  // 컴포넌트 마운트 시 데이터 로드
  useEffect(() => {
    fetchOrders();
  }, []);

  // 필터 변경 핸들러
  const handleFilterChange = (clickedFilter) => {
    setActiveFilter(clickedFilter.status);
    fetchOrders(clickedFilter.status);
  };

  // 결제 취소 함수
  const handlePaymentCancel = async (orderId) => {
    if (!window.confirm("정말 취소하시겠습니까?")) {
      return;
    }

    try {
      // 주문 취소 API 호출
      const response = await fetch(
        `${process.env.REACT_APP_API_URL}/api/v1/orders/${orderId}`,
        {
          method: "DELETE",
          headers: {
            "Content-Type": "application/json",
          },
          credentials: "include",
        }
      );

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || "주문 취소에 실패했습니다.");
      }

      // 성공 시 데이터 새로고침
      alert("주문이 취소되었습니다.");
      fetchOrders(activeFilter);
    } catch (err) {
      alert(err.message);
    }
  };

  // 상태에 따른 배지 반환
  const getBadge = (status) => {
    switch (status) {
      case "BEFOREPAID":
      case "PAYMENT_PENDING":
        return (
          <Badge bg="" style={{ backgroundColor: "#E0E7FF", color: "#3730A3" }}>
            결제 대기
          </Badge>
        );
      case "PAID":
      case "PAYMENT_COMPLETED":
        return (
          <Badge bg="" style={{ backgroundColor: "#FEF3C7", color: "#92400E" }}>
            결제 완료
          </Badge>
        );
      case "PREPARING":
        return (
          <Badge bg="" style={{ backgroundColor: "#FEF3C7", color: "#92400E" }}>
            배송 준비
          </Badge>
        );
      case "INDELIVERY":
      case "SHIPPING":
        return (
          <Badge bg="" style={{ backgroundColor: "#F3E8FF", color: "#6B21A8" }}>
            배송중
          </Badge>
        );
      case "COMPLETED":
      case "DELIVERED":
        return (
          <Badge bg="" style={{ backgroundColor: "#DCFCE7", color: "#166534" }}>
            배송 완료
          </Badge>
        );
      case "CANCELED":
        return (
          <Badge bg="" style={{ backgroundColor: "#FEE2E2", color: "#991B1B" }}>
            취소됨
          </Badge>
        );
      default:
        return <Badge bg="secondary">{status}</Badge>;
    }
  };

  // 결제 취소 버튼 표시 여부 확인 (배송 준비 상태부터는 취소 불가)
  const canCancelPayment = (status) => {
    return status === "PAID" || status === "PAYMENT_COMPLETED";
  };

  // 배송 완료 상태 확인
  const isDelivered = (status) => {
    return status === "COMPLETED" || status === "DELIVERED";
  };

  if (loading) {
    return (
      <Card className={"me-4 shadow my-3"}>
        <Card.Body className="text-center p-5">
          <Spinner animation="border" role="status">
            <span className="visually-hidden">Loading...</span>
          </Spinner>
          <div className="mt-2">주문 내역을 불러오는 중...</div>
        </Card.Body>
      </Card>
    );
  }

  if (error) {
    return (
      <Card className={"me-4 shadow my-3"}>
        <Card.Body>
          <Alert variant="danger">{error}</Alert>
        </Card.Body>
      </Card>
    );
  }

  return (
    <Card className={"me-4 shadow my-3"}>
      <Card.Body>
        <FilterButtonGroup
          activeFilter={activeFilter}
          handleChange={handleFilterChange}
        />
        {orders.length === 0 ? (
          <div className="text-center p-5 text-muted">
            주문 내역이 없습니다.
          </div>
        ) : (
          orders.map((order, index) => (
            <React.Fragment key={order.orderId}>
              <Stack direction={"horizontal"} gap={2} className={"p-4"}>
                <Image
                  width={50}
                  height={50}
                  src={
                    order.productImage || "https://i.pravatar.cc/150?img=49.jpg"
                  }
                  rounded
                />
                <Stack gap={1}>
                  <div className="fw-semibold">
                    {order.productTitle || "상품명"}
                  </div>
                  <Stack direction={"horizontal"} gap={2}>
                    <span>{(order.totalPrice || 0).toLocaleString()}원</span>
                    <desc className={"fw-light text-muted"}>
                      {order.quantity || 1}개
                    </desc>
                  </Stack>
                </Stack>
                <Stack
                  gap={1}
                  style={{ flex: "0 0 auto" }}
                  className="align-items-end"
                >
                  <desc className={"text-gray-300"}>
                    주문일:{" "}
                    {order.createdAt
                      ? new Date(order.createdAt).toLocaleDateString("ko-KR")
                      : "2024.06.03"}
                  </desc>
                  {getBadge(order.status)}
                  <Stack
                    direction={"horizontal"}
                    gap={1}
                    className={"justify-content-end"}
                  >
                    <Button
                      variant={""}
                      className={`${styles.detailButton}`}
                      size={"sm"}
                      onClick={() =>
                        navigate(`/mypage/orders/${order.orderId}`)
                      }
                    >
                      상세보기
                    </Button>
                    {isDelivered(order.status) && (
                      <Button
                        variant={""}
                        className={`${styles.detailButton}`}
                        size={"sm"}
                      >
                        리뷰작성
                      </Button>
                    )}
                    {canCancelPayment(order.status) && (
                      <Button
                        variant={""}
                        className={`${styles.detailButton}`}
                        size={"sm"}
                        onClick={() => handlePaymentCancel(order.orderId)}
                      >
                        결제취소
                      </Button>
                    )}
                  </Stack>
                </Stack>
              </Stack>
              {index + 1 !== orders.length && <hr />}
            </React.Fragment>
          ))
        )}
      </Card.Body>
    </Card>
  );
};

export default MyPageOrders;
