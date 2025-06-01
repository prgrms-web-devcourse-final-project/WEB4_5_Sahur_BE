import {Badge, Card, Stack} from "react-bootstrap";
import {faBox, faCheck, faXmark} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faCalendar} from "@fortawesome/free-regular-svg-icons";

const OrderInfo = ({ selectedItem }) => {
    return (
        <Card className={"m-3 border"}>
            <Card.Header>
                <Stack direction={"horizontal"} className={"justify-content-between"}>
                    <h4>주문 정보</h4>
                    <Badge bg={selectedItem.status === "PAID" ? "success" : "danger"}>
                        {selectedItem.status === "PAID" ? <FontAwesomeIcon icon={faCheck} /> : <FontAwesomeIcon icon={faXmark} />}
                        {selectedItem.status === "PAID" ? "결제 완료" : "결제 취소"}
                    </Badge>
                </Stack>
                <div className={"text-gray-300"}>주문번호: 2025100520</div>
            </Card.Header>
            <Card.Body className={"p-3"}>
                <div><FontAwesomeIcon icon={faBox} />  견고한 철제 선반 1개</div>
                <Stack direction={"horizontal"} gap={2}>
                    <FontAwesomeIcon icon={faCalendar} size={"lg"}/>
                    <Stack>
                        <span className={"text-gray-300"}>결제 일시</span>
                        <span>2025.10.25 16:43:38</span>
                    </Stack>
                </Stack>
            </Card.Body>
            <Card.Footer className={"p-3 border-top"}>
                <Stack direction={"horizontal"} className={"justify-content-between"}>
                    <span>결제 금액</span>
                    <span style={{ fontWeight: 600, fontSize: "20px" }}>₩{selectedItem.price.toLocaleString()}</span>
                </Stack>
            </Card.Footer>
        </Card>
    );
}

export default OrderInfo;