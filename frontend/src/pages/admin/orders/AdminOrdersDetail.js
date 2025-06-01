"use client";

import { useNavigate, useParams } from "react-router-dom";
import { Button, Card, Form, Image, Stack } from "react-bootstrap";
import styles from "./AdminOrders.module.scss";
import { useState, useEffect } from "react";
import backImg from "../../../assets/images/icon/back.png";

const API_BASE_URL =
  process.env.REACT_APP_API_URL ||
  process.env.REACT_APP_SERVER_URL ||
  "http://localhost:8080";

const options = [
  { value: "BEFOREPAID", label: "결제 대기중" },
  { value: "PAID", label: "배송 준비" },
  { value: "INDELIVERY", label: "배송 중" },
  { value: "COMPLETED", label: "배송 완료" },
  { value: "CANCELED", label: "취소됨" },
];

const AdminOrdersDetail = () => {
  const navigate = useNavigate();
  const { orderId } = useParams(); // URL에서 orderId 가져오기
  const [orderDetail, setOrderDetail] = useState(null);
  const [groupBuyDetail, setGroupBuyDetail] = useState(null);
  const [paymentDetail, setPaymentDetail] = useState(null);
  const [deliveryDetail, setDeliveryDetail] = useState(null);
  const [deliveryStatus, setDeliveryStatus] = useState("PAID");
  const [quantity, setQuantity] = useState(1); // 수량 상태 추가
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // 모든 API 호출을 병렬로 처리하는 함수
  const fetchAllData = async () => {
    setLoading(true);
    setError(null);

    try {
      // 1. 주문 상세 조회 API
      const orderResponse = await fetch(
        `${API_BASE_URL}/api/v1/orders/${orderId}`,
        {
          credentials: "include",
        }
      );

      if (!orderResponse.ok) {
        throw new Error(`주문 정보 조회 실패: ${orderResponse.status}`);
      }

      const orderResult = await orderResponse.json();

      if (!orderResult.success) {
        throw new Error(
          orderResult.message || "주문 정보 조회에 실패했습니다."
        );
      }

      const orderData = orderResult.data;
      setOrderDetail(orderData);
      setDeliveryStatus(orderData.status);
      setQuantity(orderData.quantity || 1); // 주문 수량 설정

      // 2. 공동 구매 정보 조회 API (groupBuyId가 있는 경우에만)
      if (orderData.groupBuyId) {
        try {
          const groupBuyResponse = await fetch(
            `${API_BASE_URL}/api/v1/groupBuy/${orderData.groupBuyId}`,
            {
              credentials: "include",
            }
          );

          if (groupBuyResponse.ok) {
            const groupBuyResult = await groupBuyResponse.json();
            if (groupBuyResult.success) {
              setGroupBuyDetail(groupBuyResult.data);
            }
          }
        } catch (error) {
          console.warn("공동구매 정보 조회 실패:", error);
        }
      }

      // 3. 결제 정보 조회 API
      try {
        const paymentResponse = await fetch(
          `${API_BASE_URL}/api/v1/payment/order/${orderId}`,
          {
            credentials: "include",
          }
        );

        if (paymentResponse.ok) {
          const paymentResult = await paymentResponse.json();
          if (paymentResult.success) {
            setPaymentDetail(paymentResult.data);
          }
        }
      } catch (error) {
        console.warn("결제 정보 조회 실패:", error);
      }

      // 4. 배송 정보 조회 API
      try {
        const deliveryResponse = await fetch(
          `${API_BASE_URL}/api/v1/deliveries/order/${orderId}`,
          {
            credentials: "include",
          }
        );

        if (deliveryResponse.ok) {
          const deliveryResult = await deliveryResponse.json();
          if (deliveryResult.success) {
            const deliveryData = deliveryResult.data;
            setDeliveryDetail({
              ...deliveryData,
              zipCode: deliveryData.pccc, // pccc를 zipCode로 매핑
            });
            // 배송 정보의 상태로 배송 상태 설정 (주문 상태보다 우선)
            if (deliveryData.status) {
              console.log("배송 상태 설정:", deliveryData.status); // 디버깅용
              setDeliveryStatus(deliveryData.status);
            }
          }
        }
      } catch (error) {
        console.warn("배송 정보 조회 실패:", error);
      }
    } catch (error) {
      console.error("데이터 조회 실패:", error);
      setError(error.message);
    } finally {
      setLoading(false);
    }
  };

  // 컴포넌트 마운트 시 데이터 로드
  useEffect(() => {
    if (orderId) {
      fetchAllData();
    }
  }, [orderId]);

  // 배송 정보 입력 핸들러
  const handleShippingInfoChange = (field, value) => {
    setDeliveryDetail((prev) => ({
      ...prev,
      [field]: value,
    }));
  };

  // 주문 취소
  const handleCancelOrder = async () => {
    // 취소 사유 입력받기
    const cancelReason = window.prompt(
      "취소 사유를 입력해주세요:",
      "관리자 취소"
    );

    if (!cancelReason || cancelReason.trim() === "") {
      alert("취소 사유는 필수입니다.");
      return;
    }

    if (
      window.confirm(
        `취소 사유: ${cancelReason}\n\n정말로 이 주문을 취소하시겠습니까?`
      )
    ) {
      try {
        // 1. 결제 취소 API 호출
        const paymentCancelResponse = await fetch(
          `${API_BASE_URL}/api/v1/payments/order/${orderId}/cancel`,
          {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
            },
            credentials: "include",
            body: JSON.stringify({
              cancelReason: cancelReason.trim(),
            }),
          }
        );

        if (!paymentCancelResponse.ok) {
          const errorData = await paymentCancelResponse
            .json()
            .catch(() => ({}));
          throw new Error(
            errorData.message ||
              `결제 취소 실패: ${paymentCancelResponse.status}`
          );
        }

        const paymentResult = await paymentCancelResponse.json();
        console.log("결제 취소 성공:", paymentResult);

        // 2. 주문 취소 API 호출
        const orderCancelResponse = await fetch(
          `${API_BASE_URL}/api/v1/orders/${orderId}`,
          {
            method: "DELETE",
            credentials: "include",
          }
        );

        if (!orderCancelResponse.ok) {
          const errorData = await orderCancelResponse.json().catch(() => ({}));
          throw new Error(
            errorData.message || `주문 취소 실패: ${orderCancelResponse.status}`
          );
        }

        const orderResult = await orderCancelResponse.json();
        console.log("주문 취소 성공:", orderResult);

        alert("주문이 성공적으로 취소되었습니다.");
        navigate("/admin/orders"); // 주문 목록 페이지로 이동
      } catch (error) {
        console.error("주문 취소 처리 실패:", error);
        alert(`주문 취소 중 오류가 발생했습니다: ${error.message}`);
      }
    }
  };

  // 주문 정보 수정
  const handleUpdateOrder = async () => {
    try {
      // 수량 검증
      if (!quantity || quantity < 1) {
        alert("수량은 1 이상이어야 합니다.");
        return;
      }

      // 1. 주문 정보 수정 API 호출 (수량 수정)
      const orderUpdateResponse = await fetch(
        `${API_BASE_URL}/api/v1/orders/${orderId}`,
        {
          method: "PATCH",
          headers: {
            "Content-Type": "application/json",
          },
          credentials: "include",
          body: JSON.stringify({
            quantity: Number.parseInt(quantity),
          }),
        }
      );

      if (!orderUpdateResponse.ok) {
        const errorData = await orderUpdateResponse.json().catch(() => ({}));
        throw new Error(errorData.message || "주문 정보 수정 실패");
      }

      const orderUpdateResult = await orderUpdateResponse.json();
      console.log("주문 정보 수정 성공:", orderUpdateResult);

      // 2. 주문 상태 업데이트
      const statusResponse = await fetch(
        `${API_BASE_URL}/api/v1/orders/${orderId}/status`,
        {
          method: "PATCH",
          headers: {
            "Content-Type": "application/json",
          },
          credentials: "include",
          body: JSON.stringify({ status: deliveryStatus }),
        }
      );

      if (!statusResponse.ok) {
        console.warn("주문 상태 업데이트 실패");
      }

      // 3. 배송 정보 업데이트 (배송 정보가 있는 경우에만)
      if (deliveryDetail) {
        const deliveryResponse = await fetch(
          `${API_BASE_URL}/api/v1/deliveries/order/${orderId}`,
          {
            method: "PATCH",
            headers: {
              "Content-Type": "application/json",
            },
            credentials: "include",
            body: JSON.stringify({
              address: deliveryDetail.address,
              pccc: deliveryDetail.zipCode, // zipCode를 pccc로 변환하여 전송
              contact: deliveryDetail.contact,
              shipping: deliveryDetail.shipping,
              status: deliveryStatus, // 배송 상태도 함께 업데이트
            }),
          }
        );

        if (!deliveryResponse.ok) {
          console.warn("배송 정보 업데이트 실패");
        }
      }

      alert("주문 정보가 성공적으로 수정되었습니다.");
      fetchAllData(); // 데이터 다시 로드
    } catch (error) {
      console.error("주문 정보 수정 실패:", error);
      alert(`주문 정보 수정 중 오류가 발생했습니다: ${error.message}`);
    }
  };

  if (loading) {
    return (
      <Stack direction={"vertical"} gap={2} className={"m-3"}>
        <Card>
          <Card.Body className={"text-center p-5"}>
            <div>주문 정보를 불러오는 중...</div>
          </Card.Body>
        </Card>
      </Stack>
    );
  }

  if (error) {
    return (
      <Stack direction={"vertical"} gap={2} className={"m-3"}>
        <Card>
          <Card.Body className={"text-center p-5"}>
            <div>오류가 발생했습니다: {error}</div>
            <Button
              variant="outline"
              onClick={() => navigate("/admin/orders")}
              className="mt-3"
            >
              목록으로 돌아가기
            </Button>
          </Card.Body>
        </Card>
      </Stack>
    );
  }

  if (!orderDetail) {
    return (
      <Stack direction={"vertical"} gap={2} className={"m-3"}>
        <Card>
          <Card.Body className={"text-center p-5"}>
            <div>주문 정보를 찾을 수 없습니다.</div>
            <Button
              variant="outline"
              onClick={() => navigate("/admin/orders")}
              className="mt-3"
            >
              목록으로 돌아가기
            </Button>
          </Card.Body>
        </Card>
      </Stack>
    );
  }

  // 상태에 따른 배지 색상 설정
  const getStatusBadgeStyle = (status) => {
    switch (status) {
      case "BEFOREPAID":
        return { bg: "#E0E7FF", color: "#3730A3" };
      case "PAID":
        return { bg: "#DCFCE7", color: "#166534" };
      case "INDELIVERY":
        return { bg: "#F3E8FF", color: "#6B21A8" };
      case "COMPLETED":
        return { bg: "#DFDFDF", color: "#000000" };
      case "CANCELED":
        return { bg: "#FEE2E2", color: "#991B1B" };
      default:
        return { bg: "#F1F5F9", color: "#475569" };
    }
  };

  // 상태 텍스트 변환
  const getStatusText = (status) => {
    switch (status) {
      case "BEFOREPAID":
        return "결제 대기중";
      case "PAID":
        return "결제 완료";
      case "INDELIVERY":
        return "배송 중";
      case "COMPLETED":
        return "배송 완료";
      case "CANCELED":
        return "취소";
      default:
        return "알 수 없음";
    }
  };

  // 마감 시간 계산
  const getDeadlineText = (deadline) => {
    if (!deadline) return "정보 없음";

    const now = new Date();
    const deadlineDate = new Date(deadline);
    const diff = deadlineDate - now;

    if (diff <= 0) return "마감";

    const days = Math.floor(diff / (1000 * 60 * 60 * 24));
    const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
    const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));

    if (days > 0) return `${days}일 ${hours}시간 남음`;
    if (hours > 0) return `${hours}시간 ${minutes}분 남음`;
    return `${minutes}분 남음`;
  };

  return (
    <Stack direction={"vertical"} gap={2} className={"m-3"}>
      {/* 헤더 */}
      <Card>
        <Card.Body className={"m-4"}>
          <Stack direction={"horizontal"} gap={4}>
            <span
              className={"cursor-pointer"}
              onClick={() => navigate("/admin/orders")}
            >
              <Image
                src={backImg || "/placeholder.svg"}
                width={50}
                height={50}
              />
            </span>
            <Stack>
              <h3>고객 주문 관리</h3>
              <desc className={"text-gray-300"}>
                고객 주문에 대한 처리를 합니다.
              </desc>
            </Stack>
          </Stack>
        </Card.Body>
      </Card>

      {/* 주문 정보 */}
      <Card>
        <Card.Header className={"border-0 mx-4"}>
          <Stack direction={"horizontal"} gap={4}>
            <h4>주문 정보</h4>
            <desc className={"text-gray-300"}>
              주문 번호: {orderDetail.orderId}
            </desc>
          </Stack>
        </Card.Header>
        <Card.Body className={"mx-5 mb-5"}>
          <Stack gap={3} className={"border rounded p-5"}>
            {/* 상품 정보 */}
            <Stack direction={"horizontal"} gap={2}>
              <Image
                width={50}
                height={50}
                src={
                  orderDetail.productImage &&
                  orderDetail.productImage.length > 0
                    ? orderDetail.productImage[0]
                    : "https://i.pravatar.cc/150?img=49.jpg"
                }
              />
              <Stack gap={1}>
                <div className="fw-semibold">
                  {orderDetail.productTitle || "상품명"}
                </div>
                <div>
                  <desc className={"fw-light text-muted"}>
                    {orderDetail.quantity || 1}개
                  </desc>
                </div>
              </Stack>
              <div className="ms-auto">
                <desc className={"text-muted"}>
                  {new Date(orderDetail.createdAt).toLocaleString("ko-KR", {
                    year: "numeric",
                    month: "2-digit",
                    day: "2-digit",
                    hour: "2-digit",
                    minute: "2-digit",
                  })}
                </desc>
              </div>
            </Stack>
            <hr />

            {/* 수량 수정 */}
            <Stack
              direction={"horizontal"}
              className={"justify-content-between align-items-center"}
            >
              <span className="fw-bold">주문 수량</span>
              <div style={{ width: "100px" }}>
                <Form.Control
                  type="number"
                  min="1"
                  value={quantity}
                  onChange={(e) => setQuantity(e.target.value)}
                  disabled={orderDetail.status === "CANCELED"}
                />
              </div>
            </Stack>
            <hr />

            {/* 주문 금액 */}
            <Stack
              direction={"horizontal"}
              className={"justify-content-between"}
            >
              <span className="fw-bold">주문 금액</span>
              <span className="fw-bold">
                ₩{(orderDetail.totalPrice || 0).toLocaleString()}
              </span>
            </Stack>
            <hr />

            {/* 공동 구매 정보 */}
            {groupBuyDetail && (
              <>
                <Stack
                  gap={1}
                  className={"p-4 rounded-3"}
                  style={{ background: "#F0F9FF", color: "#0369A1" }}
                >
                  <div className={"fw-semibold"} style={{ color: "#0369A1" }}>
                    공동 구매 정보
                  </div>
                  <Stack
                    direction={"horizontal"}
                    className={"justify-content-between"}
                  >
                    <span>현재 참여 인원</span>
                    <span>
                      {groupBuyDetail.currentParticipantCount}/
                      {groupBuyDetail.targetParticipants}명
                    </span>
                  </Stack>
                  <Stack
                    direction={"horizontal"}
                    className={"justify-content-between"}
                  >
                    <span>최소 인원</span>
                    <span>{groupBuyDetail.targetParticipants}명</span>
                  </Stack>
                  <Stack
                    direction={"horizontal"}
                    className={"justify-content-between"}
                  >
                    <span>마감 시간</span>
                    <span>{getDeadlineText(groupBuyDetail.deadline)}</span>
                  </Stack>
                </Stack>
                <hr />
              </>
            )}

            {/* 결제 정보 */}
            <Stack
              gap={1}
              className={"p-4 rounded-3"}
              style={{ background: "#F3E8FF", color: "#A855F7" }}
            >
              <div className={"fw-semibold"} style={{ color: "#6B21A8" }}>
                결제 정보
              </div>
              {paymentDetail ? (
                <>
                  <Stack
                    direction={"horizontal"}
                    className={"justify-content-between"}
                  >
                    <span>결제 키</span>
                    <span style={{ fontSize: "0.8rem" }}>
                      {paymentDetail.paymentKey || "-"}
                    </span>
                  </Stack>
                  <Stack
                    direction={"horizontal"}
                    className={"justify-content-between"}
                  >
                    <span>결제 방식</span>
                    <span>{paymentDetail.method || "-"}</span>
                  </Stack>
                  <Stack
                    direction={"horizontal"}
                    className={"justify-content-between"}
                  >
                    <span>결제 금액</span>
                    <span>
                      ₩{(paymentDetail.totalAmount || 0).toLocaleString()}
                    </span>
                  </Stack>
                  <Stack
                    direction={"horizontal"}
                    className={"justify-content-between"}
                  >
                    <span>결제 시간</span>
                    <span>
                      {paymentDetail.approvedAt
                        ? new Date(paymentDetail.approvedAt).toLocaleString(
                            "ko-KR"
                          )
                        : "-"}
                    </span>
                  </Stack>
                  {paymentDetail.cardNumber && (
                    <Stack
                      direction={"horizontal"}
                      className={"justify-content-between"}
                    >
                      <span>카드 번호</span>
                      <span>{paymentDetail.cardNumber}</span>
                    </Stack>
                  )}
                </>
              ) : (
                <div className="text-center text-muted">
                  결제 정보를 불러오는 중...
                </div>
              )}
            </Stack>

            <Button
              variant={""}
              className={styles.paymentStatus}
              style={{
                backgroundColor: getStatusBadgeStyle(orderDetail.status).bg,
                color: getStatusBadgeStyle(orderDetail.status).color,
              }}
            >
              {getStatusText(orderDetail.status)}
            </Button>
          </Stack>
        </Card.Body>
      </Card>

      {/* 배송 정보 */}
      <Card>
        <Card.Header className={"border-0 mx-4"}>
          <Stack direction={"horizontal"} gap={4}>
            <h4>배송 정보</h4>
          </Stack>
        </Card.Header>
        <Card.Body className={"mx-5 mb-5"}>
          <Stack gap={3} className={"border rounded p-5"}>
            <Form.Group controlId={"ordersDetailForm1"}>
              <Form.Label>받는분</Form.Label>
              <Form.Control
                type="text"
                value={orderDetail.nickname || ""}
                readOnly
                style={{ backgroundColor: "#f8f9fa" }}
              />
            </Form.Group>
            <Form.Group controlId={"ordersDetailForm2"}>
              <Form.Label>연락처</Form.Label>
              <Form.Control
                type="text"
                value={deliveryDetail?.contact || ""}
                onChange={(e) =>
                  handleShippingInfoChange("contact", e.target.value)
                }
              />
            </Form.Group>
            <Form.Group controlId={"ordersDetailForm3"} className={"w-25"}>
              <Form.Label>우편번호</Form.Label>
              <Form.Control
                type="text"
                value={deliveryDetail?.zipCode || ""}
                onChange={(e) =>
                  handleShippingInfoChange("zipCode", e.target.value)
                }
              />
            </Form.Group>
            <Form.Group controlId={"ordersDetailForm4"}>
              <Form.Label>주소</Form.Label>
              <Form.Control
                type="text"
                value={deliveryDetail?.address || ""}
                onChange={(e) =>
                  handleShippingInfoChange("address", e.target.value)
                }
              />
            </Form.Group>
            <Form.Group controlId={"ordersDetailForm7"}>
              <Form.Label>운송장 번호</Form.Label>
              <Form.Control
                type="text"
                value={deliveryDetail?.shipping || ""}
                onChange={(e) =>
                  handleShippingInfoChange("shipping", e.target.value)
                }
              />
            </Form.Group>
          </Stack>
        </Card.Body>
        <Card.Body className={"mx-5 mb-5"}>
          <Stack
            direction={"horizontal"}
            gap={3}
            className={"border rounded p-5 justify-content-between"}
          >
            <Button
              variant={"danger"}
              className={"border-0 rounded"}
              style={{ background: "#FF5555" }}
              onClick={handleCancelOrder}
              disabled={orderDetail.status === "CANCELED"}
            >
              주문 취소
            </Button>
            <Stack direction={"horizontal"} gap={2}>
              <span>배송 상태:</span>
              <div style={{ width: "215px" }}>
                <Form.Select
                  value={deliveryStatus}
                  onChange={(e) => setDeliveryStatus(e.target.value)}
                  disabled={orderDetail.status === "CANCELED"}
                >
                  {options.map((option) => (
                    <option key={option.value} value={option.value}>
                      {option.label}
                    </option>
                  ))}
                </Form.Select>
              </div>
            </Stack>
            <Stack direction={"horizontal"} gap={2}>
              <Button
                variant={""}
                className={styles.detailButton}
                onClick={() => navigate("/admin/orders")}
              >
                취소
              </Button>
              <Button
                onClick={handleUpdateOrder}
                disabled={orderDetail.status === "CANCELED"}
              >
                수정
              </Button>
            </Stack>
          </Stack>
        </Card.Body>
      </Card>
    </Stack>
  );
};

export default AdminOrdersDetail;
