import {Badge, Button, Card, Stack} from "react-bootstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faBox, faSackDollar} from "@fortawesome/free-solid-svg-icons";
import {faCalendar, faCreditCard} from "@fortawesome/free-regular-svg-icons";

const PaymentDetail = ({ selectedItem }) => {
    return (
        <Card className={"m-3 border"}>
            <Card.Header>
                <h4>결제 상세</h4>
            </Card.Header>
            <Card.Body className={"p-3"}>
                <Stack gap={2}>
                    <Stack className={"text-gray-300 justify-content-between"} direction={"horizontal"}>
                        <span>결제 방식</span>
                        <span><FontAwesomeIcon icon={faCreditCard} />카드</span>
                    </Stack>
                    <Stack className={"text-gray-300 justify-content-between"} direction={"horizontal"}>
                        <span>결제 키</span>
                        <span>tviva202**********RST5</span>
                    </Stack>
                </Stack>
            </Card.Body>
            <Card.Footer className={"p-3 border-top"}>
                <Stack direction={"horizontal"} gap={2}>
                    <FontAwesomeIcon icon={faSackDollar} size={"lg"}/>
                    <Stack>
                        <span className={"text-gray-300"}>카드 정보</span>
                        <span>카드 번호: 55943345****3930</span>
                    </Stack>
                    <Badge className={"ms-auto"}>신한 카드</Badge>
                </Stack>
            </Card.Footer>
        </Card>
    );
}

export default PaymentDetail;