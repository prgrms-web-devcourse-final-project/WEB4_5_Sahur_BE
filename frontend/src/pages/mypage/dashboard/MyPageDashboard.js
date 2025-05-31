import {Card, Stack} from "react-bootstrap";

const MyPageDashboard = () => {
    return (
        <Stack direction={"vertical"}>
            <Stack direction={"horizontal"} gap={3} className={"m-3"}>
                <Card className={"flex-fill"}>
                    <Card.Body>
                        참여 중인 공동 구매
                    </Card.Body>
                </Card>
                <Card className={"flex-fill"}>
                    <Card.Body>
                        진행 중인 주문
                    </Card.Body>
                </Card>
                <Card className={"flex-fill"}>
                    <Card.Body>
                        작성한 리뷰
                    </Card.Body>
                </Card>
            </Stack>
            <Card className={"m-3 w-100"}>
                <Card.Body>
                    참여 중인 공동 구매
                </Card.Body>
            </Card>
            <Card className={"m-3 w-100"}>
                <Card.Body>
                    최근 주문 내역
                </Card.Body>
            </Card>
        </Stack>
    );
}

export default MyPageDashboard