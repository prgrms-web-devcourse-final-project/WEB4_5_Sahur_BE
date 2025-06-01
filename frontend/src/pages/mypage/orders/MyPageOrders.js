import {Badge, Button, Card, Image, Stack} from "react-bootstrap";
import FilterButtonGroup from "./FilterButtonGroup";
import {useNavigate} from "react-router-dom";
import React, {useState} from "react";
import styles from "./MyPageOrders.module.scss"

const initData = [
    {orderId: 1, status: 'BEFOREPAID'},
    {orderId: 2, status: 'INDELIVERY'},
    {orderId: 3, status: 'COMPLETED'},
    {orderId: 4, status: 'CANCELED'},
    {orderId: 5, status: 'PAID'},
];

const MyPageOrders = () => {
    const navigate = useNavigate();
    const [activeFilter, setActiveFilter] = useState('ALL');

    const handleFilterChange = (clickedFilter) => {
        setActiveFilter(clickedFilter.status);
    }

    const getBadge = (status) => {
        if (status === 'BEFOREPAID') {
            return <Badge bg="" style={{ backgroundColor: "#E0E7FF", color: "#3730A3" }}>결제 대기중</Badge>
        } else if (status === 'PAID') {
            return <Badge bg="" style={{ backgroundColor: "#DCFCE7", color: "#166534" }}>결제 완료</Badge>
        } else if (status === 'INDELIVERY') {
            return <Badge bg="" style={{ backgroundColor: "#F3E8FF", color: "#6B21A8" }}>배송 중</Badge>
        } else if (status === 'COMPLETED') {
            return <Badge bg="" style={{ backgroundColor: "#DFDFDF", color: "#000000" }}>배송 완료</Badge>
        } else if (status === 'CANCELED') {
            return <Badge bg="" style={{ backgroundColor: "#FEE2E2", color: "#991B1B" }}>취소</Badge>
        }
    }

    return (
        <Card className={"me-4 shadow my-3"}>
            <Card.Body>
                <FilterButtonGroup activeFilter={activeFilter} handleChange={handleFilterChange} />
                {initData.map((item, index) => (
                    <>
                        <Stack direction={"horizontal"} gap={2} className={"p-4"}>
                            <Image width={50} height={50} src={"https://i.pravatar.cc/150?img=49.jpg"} rounded/>
                            <Stack gap={1} >
                                <div className="fw-semibold">프리미엄 블루투스 이어폰 100</div>
                                <Stack direction={"horizontal"} gap={2}>
                                    <span>35,000원</span>
                                    <desc className={"fw-light text-muted"}>1개</desc>
                                </Stack>
                            </Stack>
                            <Stack gap={1} style={{ flex: "0 0 auto" }} className="align-items-end" >
                                <desc className={"text-gray-300"}>주문일: 2024.06.03</desc>
                                {getBadge(item.status)}
                                <Stack direction={"horizontal"} gap={1} className={"justify-content-end"}>
                                    <Button variant={""} className={`${styles.detailButton}`}
                                            size={"sm"} onClick={() => navigate(item.orderId.toString())}>
                                        상세보기
                                    </Button>
                                    {item.status === 'COMPLETED' &&
                                        <Button variant={""} className={`${styles.detailButton}`} size={"sm"}>
                                        리뷰작성
                                    </Button>}
                                    {item.status === 'PAID' &&
                                        <Button variant={""} className={`${styles.detailButton}`} size={"sm"}>
                                            결제취소
                                        </Button>}
                                </Stack>
                            </Stack>
                        </Stack>
                        {index + 1 !== initData.length && <hr/>}
                    </>
                ))}

            </Card.Body>
        </Card>
    )
};

export default MyPageOrders;