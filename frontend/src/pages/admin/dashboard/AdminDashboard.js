import {Card, Stack} from "react-bootstrap";

const AdminDashboard = () => {
    return (
        <Stack direction={"vertical"}>
            <Stack direction={"horizontal"} gap={3} className={"m-3"}>
                <Card className={"w-25"}>
                    <Card.Body>
                        이번 달 총 매출
                    </Card.Body>
                </Card>
                <Card className={"w-25"}>
                    <Card.Body>
                        진행 중인 공동 구매
                    </Card.Body>
                </Card>
                <Card className={"w-25"}>
                    <Card.Body>
                        승인 대기 상품
                    </Card.Body>
                </Card>
                <Card className={"w-25"}>
                    <Card.Body>
                        배송 중
                    </Card.Body>
                </Card>
            </Stack>
            <Stack direction={"horizontal"} gap={3} className={"m-3"}>
                <Card style={{ width: "60%" }}>
                    <Card.Body>
                        승인 대기 상품
                    </Card.Body>
                </Card>
                <Card style={{ width: "40%" }}>
                    <Card.Body>
                        오늘 마감 예정 공동 구매
                    </Card.Body>
                </Card>
            </Stack>
            <Card className={"m-3 w-100"}>
                <Card.Body>
                    최근 주문 내역
                </Card.Body>
            </Card>
        </Stack>
    );
}

export default AdminDashboard;