import { Badge, Card, Stack } from "react-bootstrap";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faClock, faCreditCard } from "@fortawesome/free-regular-svg-icons";
import {
  faCheck,
  faChevronLeft,
  faChevronRight,
  faXmark,
  faTruck,
  faBox,
} from "@fortawesome/free-solid-svg-icons";

const PaymentList = ({ orders, pagination, handleOpenClick, onPageChange }) => {
  const getStatusInfo = (status) => {
    switch (status) {
      case "PAID":
        return { bg: "success", icon: faCheck, text: "결제완료" };
      case "COMPLETED":
        return { bg: "primary", icon: faCheck, text: "완료" };
      case "INDELIVERY":
        return { bg: "warning", icon: faTruck, text: "배송중" };
      case "CANCELED":
        return { bg: "danger", icon: faXmark, text: "취소" };
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
      })
      .replace(/\./g, ".")
      .replace(/,/g, "");
  };

  const handlePrevPage = () => {
    if (pagination.currentPage > 0) {
      onPageChange(pagination.currentPage - 1);
    }
  };

  const handleNextPage = () => {
    if (pagination.currentPage < pagination.totalPages - 1) {
      onPageChange(pagination.currentPage + 1);
    }
  };

  if (!orders || orders.length === 0) {
    return (
      <div className="text-center py-4">
        <p>주문 내역이 없습니다.</p>
      </div>
    );
  }

  return (
    <div>
      {orders.map((order) => (
        <PaymentItem
          key={order.orderId}
          item={order}
          handleOpenClick={handleOpenClick}
          getStatusInfo={getStatusInfo}
          formatDate={formatDate}
        />
      ))}
      <Stack
        direction={"horizontal"}
        gap={1}
        className={"justify-content-center mt-4"}
      >
        <span
          style={{
            width: "25px",
            textAlign: "center",
            cursor: pagination.currentPage > 0 ? "pointer" : "not-allowed",
            opacity: pagination.currentPage > 0 ? 1 : 0.5,
          }}
          onClick={handlePrevPage}
        >
          <FontAwesomeIcon icon={faChevronLeft} />
        </span>
        <span>
          {pagination.currentPage + 1}/{pagination.totalPages}
        </span>
        <span
          style={{
            width: "25px",
            textAlign: "center",
            cursor:
              pagination.currentPage < pagination.totalPages - 1
                ? "pointer"
                : "not-allowed",
            opacity:
              pagination.currentPage < pagination.totalPages - 1 ? 1 : 0.5,
          }}
          onClick={handleNextPage}
        >
          <FontAwesomeIcon icon={faChevronRight} />
        </span>
      </Stack>
    </div>
  );
};

export default PaymentList;

const PaymentItem = ({ item, handleOpenClick, getStatusInfo, formatDate }) => {
  const statusInfo = getStatusInfo(item.status);

  return (
    <Card
      className={"border p-2 cursor-pointer mb-2"}
      onClick={() => handleOpenClick(item)}
    >
      <Card.Body>
        <Stack
          direction={"horizontal"}
          className={"justify-content-between mb-2"}
        >
          <span className={"fw-bold"}>
            {item.productTitle} {item.quantity}개
            <Badge className={"ms-2"} bg={statusInfo.bg}>
              <FontAwesomeIcon icon={statusInfo.icon} />
              {statusInfo.text}
            </Badge>
          </span>
          <span>₩{item.totalPrice.toLocaleString()}</span>
        </Stack>
        <Stack direction={"horizontal"} className={"justify-content-between"}>
          <span>
            <span className={"text-gray-300"}>
              <FontAwesomeIcon icon={faClock} /> {formatDate(item.createdAt)}
            </span>
            <span className={"ms-3 text-gray-300"}>
              <FontAwesomeIcon icon={faCreditCard} /> 주문번호: {item.orderId}
            </span>
          </span>
          <span className={"text-gray-300"}>상세보기 &gt;</span>
        </Stack>
      </Card.Body>
    </Card>
  );
};
