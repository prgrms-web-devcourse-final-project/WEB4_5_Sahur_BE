import {Badge, Card, Stack} from "react-bootstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faClock, faCreditCard} from "@fortawesome/free-regular-svg-icons";
import {
    faCheck,
    faChevronLeft, faChevronRight,
    faXmark
} from "@fortawesome/free-solid-svg-icons";

const initData = [
    { status: "PAID", title: "견고한 철제 선반 1개", price: 223000 },
    { status: "PAID", title: "차량용 방향제 1개", price: 3000 },
    { status: "PAID", title: "스마트폰 무선 이어폰", price: 28000 },
    { status: "CANCELED", title: "프리미엄 커피원두 300g", price: 123000 },
    { status: "PAID", title: "스마트 홈 조명 세트", price: 2000 },
];

const PaymentList = ({ handleOpenClick }) => {
    return (
        <div>
            {initData.map(item => (
                <PaymentItem item={item} handleOpenClick={handleOpenClick}/>
            ))}
            <Stack direction={"horizontal"} gap={1} className={"justify-content-center mt-4"}>
                <span style={{ width: "25px", textAlign: "center" }}><FontAwesomeIcon icon={faChevronLeft} /></span>
                    1/2
                <span style={{ width: "25px", textAlign: "center" }}><FontAwesomeIcon icon={faChevronRight} /></span>
            </Stack>
        </div>
    );
}

export default PaymentList;

const PaymentItem = ({item, handleOpenClick}) => {
    return (
        <Card className={"border p-2 cursor-pointer "} onClick={() => handleOpenClick(item)}>
            <Card.Body>
                <Stack direction={"horizontal"} className={"justify-content-between mb-2"}>
                    <span className={"fw-bold"}>
                        {item.title}<Badge className={"ms-2"} bg={item.status === "PAID" ? "success" : "danger"}>
                        {item.status === "PAID" ? <FontAwesomeIcon icon={faCheck} /> : <FontAwesomeIcon icon={faXmark} />}
                        {item.status === "PAID" ? "완료" : "취소"}
                        </Badge>
                    </span>
                    <span>₩{item.price.toLocaleString()}</span>
                </Stack>
                <Stack direction={"horizontal"} className={"justify-content-between"}>
                    <span>
                        <span className={"text-gray-300"}><FontAwesomeIcon icon={faClock} />2024.05.30 15:59</span>
                        <span className={"ms-3 text-gray-300"}><FontAwesomeIcon icon={faCreditCard} />현대 카드</span>
                    </span>
                    <span className={"text-gray-300"}>상세보기 ></span>
                </Stack>
            </Card.Body>
        </Card>
    )
}