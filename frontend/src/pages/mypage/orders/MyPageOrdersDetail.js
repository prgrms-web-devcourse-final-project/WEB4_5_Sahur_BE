import { useNavigate, useParams } from "react-router-dom";
import {
  Button,
  Card,
  Col,
  Form,
  Image,
  Row,
  Stack,
  Alert,
  Spinner,
} from "react-bootstrap";
import styles from "./MyPageOrders.module.scss";
import { useState, useEffect } from "react";

const MyPageOrdersDetail = () => {
  const navigate = useNavigate();
  const { orderId } = useParams();
  const [orderDetail, setOrderDetail] = useState(null);
  const [deliveryInfo, setDeliveryInfo] = useState(null);
  const [groupBuyInfo, setGroupBuyInfo] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isEditingDelivery, setIsEditingDelivery] = useState(false);
  const [deliveryForm, setDeliveryForm] = useState({
    recipientName: "",
    contact: "",
    zipCode: "",
    address: "",
    detailAddress: "",
    pccc: "",
    shipping: "",
  });
  const [isSaving, setIsSaving] = useState(false);

  // 여러 API 동시 호출
  const fetchOrderDetails = async () => {
    try {
      setLoading(true);

      // 주문 상세 조회
      const orderResponse = await fetch(
        `${process.env.REACT_APP_API_URL}/api/v1/orders/${orderId}`,
        {
          method: "GET",
          headers: { "Content-Type": "application/json" },
          credentials: "include",
        }
      );

      if (!orderResponse.ok) {
        throw new Error("주문 정보를 불러오는데 실패했습니다.");
      }

      const orderData = await orderResponse.json();
      setOrderDetail(orderData.data);

      // 배송 정보 조회
      try {
        const deliveryResponse = await fetch(
          `${process.env.REACT_APP_API_URL}/api/v1/deliveries/order/${orderId}`,
          {
            method: "GET",
            headers: { "Content-Type": "application/json" },
            credentials: "include",
          }
        );

        if (deliveryResponse.ok) {
          const deliveryData = await deliveryResponse.json();
          setDeliveryInfo({
            ...deliveryData.data,
            deliveryId: deliveryData.data.deliveryId || orderId, // deliveryId 저장
          });

          // 주소 분리
          const { mainAddress, detailAddress } = parseAddress(
            deliveryData.data.address
          );

          setDeliveryForm({
            recipientName: orderData.data.nickname || "홍길동",
            contact: deliveryData.data.contact || "",
            zipCode: deliveryData.data.zipCode || "12345678",
            address: mainAddress || "",
            detailAddress: detailAddress || "",
            pccc: deliveryData.data.pccc?.toString() || "",
            shipping: deliveryData.data.shipping || "",
          });
        }
      } catch (err) {
        console.log("배송 정보 조회 실패:", err);
      }

      // 공동구매 정보 조회 (groupBuyId가 있는 경우)
      if (orderData.data.groupBuyId) {
        try {
          const groupBuyResponse = await fetch(
            `${process.env.REACT_APP_API_URL}/api/v1/groupBuy/${orderData.data.groupBuyId}`,
            {
              method: "GET",
              headers: { "Content-Type": "application/json" },
              credentials: "include",
            }
          );

          if (groupBuyResponse.ok) {
            const groupBuyData = await groupBuyResponse.json();
            setGroupBuyInfo(groupBuyData.data);
          }
        } catch (err) {
          console.log("공동구매 정보 조회 실패:", err);
        }
      }

      setError(null);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  // 배송 정보 수정
  const handleDeliveryUpdate = async () => {
    try {
      setIsSaving(true);

      // 필수 필드 유효성 검사
      if (!deliveryForm.zipCode.trim()) {
        alert("우편번호는 필수 입력 항목입니다.");
        return;
      }
      if (!deliveryForm.address.trim()) {
        alert("도로명/지번 주소는 필수 입력 항목입니다.");
        return;
      }
      if (!deliveryForm.detailAddress.trim()) {
        alert("상세 주소는 필수 입력 항목입니다.");
        return;
      }
      if (!deliveryForm.contact.trim()) {
        alert("연락처는 필수입니다.");
        return;
      }

      const updateData = {
        zipCode: deliveryForm.zipCode.trim(),
        streetAdr: deliveryForm.address.trim(),
        detailAdr: deliveryForm.detailAddress.trim(),
        pccc: deliveryForm.pccc ? Number.parseInt(deliveryForm.pccc) : null,
        contact: deliveryForm.contact.trim(),
      };

      // deliveryId를 얻기 위해 배송 정보에서 ID를 가져와야 합니다
      // 만약 deliveryInfo에 ID가 없다면 orderId를 사용
      const deliveryId = deliveryInfo?.deliveryId || orderId;
      const status = deliveryInfo?.status || "PREPARING";

      const response = await fetch(
        `${process.env.REACT_APP_API_URL}/api/v1/deliveries/${deliveryId}?status=${status}`,
        {
          method: "PUT",
          headers: { "Content-Type": "application/json" },
          credentials: "include",
          body: JSON.stringify(updateData),
        }
      );

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || "배송 정보 수정에 실패했습니다.");
      }

      const updatedData = await response.json();

      // 성공 시 deliveryInfo 업데이트
      const fullAddress =
        `${deliveryForm.address} ${deliveryForm.detailAddress}`.trim();
      setDeliveryInfo({
        ...deliveryInfo,
        contact: deliveryForm.contact,
        address: fullAddress,
        pccc: deliveryForm.pccc ? Number.parseInt(deliveryForm.pccc) : 0,
      });

      setIsEditingDelivery(false);
      alert("배송 정보가 수정되었습니다.");
    } catch (err) {
      console.error("배송 정보 수정 오류:", err);
      alert(err.message);
    } finally {
      setIsSaving(false);
    }
  };

  // 마감 시간 계산
  const getTimeRemaining = (deadline) => {
    if (!deadline) return "정보 없음";

    const now = new Date();
    const deadlineDate = new Date(deadline);
    const diff = deadlineDate - now;

    if (diff <= 0) return "마감됨";

    const hours = Math.floor(diff / (1000 * 60 * 60));
    const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));

    return `${hours}시간 ${minutes}분 남음`;
  };

  // 주소를 분리하는 함수
  const parseAddress = (fullAddress) => {
    if (!fullAddress) return { mainAddress: "", detailAddress: "" };

    // 간단한 주소 분리 로직 (실제로는 더 정교한 로직이 필요할 수 있음)
    const parts = fullAddress.split(" ");
    if (parts.length > 3) {
      const mainAddress = parts.slice(0, -2).join(" ");
      const detailAddress = parts.slice(-2).join(" ");
      return { mainAddress, detailAddress };
    }
    return { mainAddress: fullAddress, detailAddress: "" };
  };

  useEffect(() => {
    if (orderId) {
      fetchOrderDetails();
    }
  }, [orderId]);

  // 상태에 따른 배송 상태 텍스트 반환
  const getDeliveryStatus = (status) => {
    switch (status) {
      case "BEFOREPAID":
      case "PAYMENT_PENDING":
        return "결제 대기";
      case "PAID":
      case "PAYMENT_COMPLETED":
        return "결제 완료";
      case "PREPARING":
        return "배송 준비";
      case "INDELIVERY":
      case "SHIPPING":
        return "배송 중";
      case "COMPLETED":
      case "DELIVERED":
        return "배송 완료";
      case "CANCELED":
        return "취소됨";
      default:
        return status;
    }
  };

  // 배송 정보 수정 가능 여부 (배송 준비 상태일 때만)
  const canEditDelivery = (status) => {
    return status === "PREPARING";
  };

  if (loading) {
    return (
      <div className="text-center p-5">
        <Spinner animation="border" role="status">
          <span className="visually-hidden">Loading...</span>
        </Spinner>
        <div className="mt-2">주문 상세 정보를 불러오는 중...</div>
      </div>
    );
  }

  if (error) {
    return (
      <Stack direction={"vertical"} gap={2} className={"m-3"}>
        <Alert variant="danger">{error}</Alert>
        <Button
          variant="outline-primary"
          onClick={() => navigate("/mypage/orders")}
        >
          주문 목록으로 돌아가기
        </Button>
      </Stack>
    );
  }

  if (!orderDetail) {
    return (
      <Stack direction={"vertical"} gap={2} className={"m-3"}>
        <Alert variant="warning">주문 정보를 찾을 수 없습니다.</Alert>
        <Button
          variant="outline-primary"
          onClick={() => navigate("/mypage/orders")}
        >
          주문 목록으로 돌아가기
        </Button>
      </Stack>
    );
  }

  return (
    <Stack direction={"vertical"} gap={2} className={"m-3"}>
      {/* 주문 정보 */}
      <Card>
        <Card.Header className={"border-0 mx-4"}>
          <Stack
            direction={"horizontal"}
            className="justify-content-between align-items-center"
          >
            <h4>주문 정보</h4>
          </Stack>
        </Card.Header>
        <Card.Body className={"mx-5 mb-5"}>
          <Stack gap={3} className={"border rounded p-5"}>
            <Stack direction={"horizontal"} gap={2}>
              <Image
                width={80}
                height={80}
                src={
                  groupBuyInfo?.product?.imageUrl?.[0] ||
                  orderDetail.productImage ||
                  "https://i.pravatar.cc/150?img=49.jpg" ||
                  "/placeholder.svg" ||
                  "/placeholder.svg" ||
                  "/placeholder.svg" ||
                  "/placeholder.svg" ||
                  "/placeholder.svg" ||
                  "/placeholder.svg" ||
                  "/placeholder.svg"
                }
              />
              <Stack gap={1}>
                <div className="fw-semibold">
                  {orderDetail.productTitle ||
                    groupBuyInfo?.product?.title ||
                    "프리미엄 블루투스 이어폰"}
                </div>
                <div className="text-muted">XS-500</div>
                <div className="text-muted">{orderDetail.quantity || 1}개</div>
              </Stack>
            </Stack>
            <hr />
            <Row className={`text-muted`}>
              <Col xs={12} className={"d-flex justify-content-between"}>
                <span>상품 금액</span>
                <span>
                  {(
                    orderDetail.totalPrice ||
                    groupBuyInfo?.product?.price ||
                    39000
                  ).toLocaleString()}
                  원
                </span>
              </Col>
              <Col xs={12} className={"d-flex justify-content-between"}>
                <span>배송비</span>
                <span>무료</span>
              </Col>
            </Row>
            <hr />
            <Stack
              direction={"horizontal"}
              className={"justify-content-between"}
            >
              <span className="fw-bold">총 결제 금액</span>
              <span className="fw-bold">
                {(
                  orderDetail.totalPrice ||
                  groupBuyInfo?.product?.price ||
                  39000
                ).toLocaleString()}
                원
              </span>
            </Stack>

            {/* 공동 구매 정보 - Hover 시에만 표시 */}
            {groupBuyInfo && (
              <div className={styles.hoverSection}>
                <div className={styles.hoverTrigger}>공동구매 정보</div>
                <Stack
                  gap={1}
                  className={`${styles.hoverContent} p-4 rounded-3`}
                  style={{ background: "#EFF6FF", color: "#1D4ED8" }}
                >
                  <div className={"fw-semibold"} style={{ color: "#1E40AF" }}>
                    공동구매 정보
                  </div>
                  <Stack
                    direction={"horizontal"}
                    className={"justify-content-between"}
                  >
                    <span>현재 참여 인원</span>
                    <span>
                      {groupBuyInfo.currentParticipantCount || 0}/
                      {groupBuyInfo.targetParticipants || 0}명
                    </span>
                  </Stack>
                  <Stack
                    direction={"horizontal"}
                    className={"justify-content-between"}
                  >
                    <span>최소 인원</span>
                    <span>{groupBuyInfo.targetParticipants || 0}명</span>
                  </Stack>
                  <Stack
                    direction={"horizontal"}
                    className={"justify-content-between"}
                  >
                    <span>마감 시간</span>
                    <span>{getTimeRemaining(groupBuyInfo.deadline)}</span>
                  </Stack>
                </Stack>
              </div>
            )}

            {/* 결제 정보 - Hover 시에만 표시 */}
            <div className={styles.hoverSection}>
              <div className={styles.hoverTrigger}>결제 정보</div>
              <Stack
                gap={1}
                className={`${styles.hoverContent} p-4 rounded-3`}
                style={{ background: "#F3E8FF", color: "#A855F7" }}
              >
                <div className={"fw-semibold"} style={{ color: "#6B21A8" }}>
                  결제 정보
                </div>
                <Stack
                  direction={"horizontal"}
                  className={"justify-content-between"}
                >
                  <span>주문 번호</span>
                  <span>{orderDetail.orderId}</span>
                </Stack>
                <Stack
                  direction={"horizontal"}
                  className={"justify-content-between"}
                >
                  <span>카드 번호</span>
                  <span>1234-****-5678-****</span>
                </Stack>
                <Stack
                  direction={"horizontal"}
                  className={"justify-content-between"}
                >
                  <span>결제 방식</span>
                  <span>간편결제</span>
                </Stack>
                <Stack
                  direction={"horizontal"}
                  className={"justify-content-between"}
                >
                  <span>결제 시간</span>
                  <span>
                    {orderDetail.createdAt
                      ? new Date(orderDetail.createdAt).toLocaleString("ko-KR")
                      : "2025. 6. 1. 오후 2:28:29"}
                  </span>
                </Stack>
              </Stack>
            </div>

            <Button variant={""} className={styles.paymentStatusComplete}>
              결제 완료
            </Button>
          </Stack>
        </Card.Body>
      </Card>

      {/* 배송 정보 */}
      <Card>
        <Card.Header className={"border-0 mx-4"}>
          <Stack
            direction={"horizontal"}
            className="justify-content-between align-items-center"
          >
            <h4>배송 정보</h4>
            {canEditDelivery(deliveryInfo?.status || orderDetail.status) && (
              <Button
                variant="outline-primary"
                size="sm"
                onClick={() => {
                  if (isEditingDelivery) {
                    handleDeliveryUpdate();
                  } else {
                    setIsEditingDelivery(true);
                  }
                }}
                disabled={isSaving}
              >
                {isSaving ? (
                  <>
                    <Spinner
                      as="span"
                      animation="border"
                      size="sm"
                      role="status"
                      aria-hidden="true"
                    />
                    <span className="ms-1">저장 중...</span>
                  </>
                ) : isEditingDelivery ? (
                  "저장"
                ) : (
                  "수정"
                )}
              </Button>
            )}
          </Stack>
        </Card.Header>
        <Card.Body className={"mx-5 mb-5"}>
          <Stack gap={3} className={"border rounded p-5"}>
            <Form.Group>
              <Form.Label>받는분</Form.Label>
              <Form.Control
                type="text"
                value={deliveryForm.recipientName}
                readOnly
                style={{ backgroundColor: "#f8f9fa" }}
              />
            </Form.Group>
            <Form.Group>
              <Form.Label>
                연락처 <span className="text-danger">*</span>
              </Form.Label>
              <Form.Control
                type="text"
                value={deliveryForm.contact}
                readOnly={!isEditingDelivery}
                onChange={(e) =>
                  setDeliveryForm({ ...deliveryForm, contact: e.target.value })
                }
                required
              />
            </Form.Group>
            <Form.Group className={"w-25"}>
              <Form.Label>
                우편번호 <span className="text-danger">*</span>
              </Form.Label>
              <Form.Control
                type="text"
                value={deliveryForm.zipCode}
                readOnly={!isEditingDelivery}
                onChange={(e) =>
                  setDeliveryForm({ ...deliveryForm, zipCode: e.target.value })
                }
                style={!isEditingDelivery ? { backgroundColor: "#f8f9fa" } : {}}
                required
              />
            </Form.Group>
            <Form.Group>
              <Form.Label>
                주소 <span className="text-danger">*</span>
              </Form.Label>
              <Form.Control
                type="text"
                value={deliveryForm.address}
                readOnly={!isEditingDelivery}
                onChange={(e) =>
                  setDeliveryForm({ ...deliveryForm, address: e.target.value })
                }
                required
              />
            </Form.Group>
            <Form.Group>
              <Form.Label>
                상세 주소 <span className="text-danger">*</span>
              </Form.Label>
              <Form.Control
                type="text"
                value={deliveryForm.detailAddress}
                readOnly={!isEditingDelivery}
                onChange={(e) =>
                  setDeliveryForm({
                    ...deliveryForm,
                    detailAddress: e.target.value,
                  })
                }
                required
              />
            </Form.Group>
            <Form.Group>
              <Form.Label>고유 통관 번호 (PCCC)</Form.Label>
              <Form.Control
                type="text"
                value={deliveryForm.pccc}
                readOnly={!isEditingDelivery}
                onChange={(e) =>
                  setDeliveryForm({ ...deliveryForm, pccc: e.target.value })
                }
              />
            </Form.Group>
            <Form.Group>
              <Form.Label>송장 번호</Form.Label>
              <Form.Control
                type="text"
                value={deliveryForm.shipping}
                readOnly
                style={{ backgroundColor: "#f8f9fa" }}
              />
            </Form.Group>
          </Stack>
        </Card.Body>
        {/* 배송 상태 표시 */}
        <Button variant={""} className={`${styles.deliveryStatus} mx-5 mb-5`}>
          배송 준비
        </Button>
      </Card>
    </Stack>
  );
};

export default MyPageOrdersDetail;
