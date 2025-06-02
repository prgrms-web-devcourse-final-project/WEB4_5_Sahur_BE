import { Badge, Card, Stack } from "react-bootstrap";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faSackDollar } from "@fortawesome/free-solid-svg-icons";
import { faCreditCard } from "@fortawesome/free-regular-svg-icons";

const PaymentDetail = ({ selectedItem }) => {
  if (!selectedItem) return null;

  return (
    <Card className={"m-3 border"}>
      <Card.Header>
        <h4>결제 상세</h4>
      </Card.Header>
      <Card.Body className={"p-3"}>
        <Stack gap={2}>
          <Stack
            className={"text-gray-300 justify-content-between"}
            direction={"horizontal"}
          >
            <span>결제 방식</span>
            <span>
              <FontAwesomeIcon icon={faCreditCard} /> 카드
            </span>
          </Stack>
          <Stack
            className={"text-gray-300 justify-content-between"}
            direction={"horizontal"}
          >
            <span>주문 ID</span>
            <span>{selectedItem.orderId}</span>
          </Stack>
          <Stack
            className={"text-gray-300 justify-content-between"}
            direction={"horizontal"}
          >
            <span>상품 ID</span>
            <span>{selectedItem.productId}</span>
          </Stack>
          <Stack
            className={"text-gray-300 justify-content-between"}
            direction={"horizontal"}
          >
            <span>공동구매 ID</span>
            <span>{selectedItem.groupBuyId}</span>
          </Stack>
        </Stack>
      </Card.Body>
      <Card.Footer className={"p-3 border-top"}>
        <Stack direction={"horizontal"} gap={2}>
          <FontAwesomeIcon icon={faSackDollar} size={"lg"} />
          <Stack>
            <span className={"text-gray-300"}>주문자 정보</span>
            <span>주문자: {selectedItem.nickname}</span>
            <span>수량: {selectedItem.quantity}개</span>
          </Stack>
          <Badge className={"ms-auto"} bg="primary">
            주문완료
          </Badge>
        </Stack>
      </Card.Footer>
    </Card>
  );
};

export default PaymentDetail;
