import { Badge, Card, Stack } from "react-bootstrap";
import {
  faBox,
  faCheck,
  faXmark,
  faTruck,
} from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faCalendar } from "@fortawesome/free-regular-svg-icons";

const OrderInfo = ({ selectedItem }) => {
  if (!selectedItem) return null;

  const getStatusInfo = (status) => {
    switch (status) {
      case "PAID":
        return { bg: "success", icon: faCheck, text: "결제완료" };
      case "COMPLETED":
        return { bg: "primary", icon: faCheck, text: "주문완료" };
      case "INDELIVERY":
        return { bg: "warning", icon: faTruck, text: "배송중" };
      case "CANCELED":
        return { bg: "danger", icon: faXmark, text: "주문취소" };
      default:
        return { bg: "secondary", icon: faBox, text: status };
    }
  };

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date
      .toLocaleDateString("ko-KR", {
        year: "numeric",
        month: "2-digit",
        day: "2-digit",
        hour: "2-digit",
        minute: "2-digit",
        second: "2-digit",
      })
      .replace(/\./g, ".")
      .replace(/,/g, "");
  };

  const statusInfo = getStatusInfo(selectedItem.status);

  return (
    <Card className={"m-3 border"}>
      <Card.Header>
        <Stack direction={"horizontal"} className={"justify-content-between"}>
          <h4>주문 정보</h4>
          <Badge bg={statusInfo.bg}>
            <FontAwesomeIcon icon={statusInfo.icon} />
            {statusInfo.text}
          </Badge>
        </Stack>
        <div className={"text-gray-300"}>주문번호: {selectedItem.orderId}</div>
      </Card.Header>
      <Card.Body className={"p-3"}>
        <div>
          <FontAwesomeIcon icon={faBox} /> {selectedItem.productTitle}{" "}
          {selectedItem.quantity}개
        </div>
        <Stack direction={"horizontal"} gap={2} className={"mt-3"}>
          <FontAwesomeIcon icon={faCalendar} size={"lg"} />
          <Stack>
            <span className={"text-gray-300"}>주문 일시</span>
            <span>{formatDate(selectedItem.createdAt)}</span>
          </Stack>
        </Stack>
      </Card.Body>
      <Card.Footer className={"p-3 border-top"}>
        <Stack direction={"horizontal"} className={"justify-content-between"}>
          <span>결제 금액</span>
          <span style={{ fontWeight: 600, fontSize: "20px" }}>
            ₩{selectedItem.totalPrice.toLocaleString()}
          </span>
        </Stack>
      </Card.Footer>
    </Card>
  );
};

export default OrderInfo;
