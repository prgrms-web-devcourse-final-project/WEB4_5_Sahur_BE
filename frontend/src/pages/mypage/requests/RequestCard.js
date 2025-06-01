import {Badge, Button, Image, Stack} from "react-bootstrap";
import styles from "../orders/MyPageOrders.module.scss";
import {useNavigate} from "react-router-dom";

const RequestCard = ({item}) => {
    const navigate = useNavigate();

    const getBadge = (status) => {
        if (status === 'waiting') {
            return <Badge bg="" style={{ backgroundColor: "#E0E7FF", color: "#3730A3" }}>승인 대기</Badge>
        } else if (status === 'approved') {
            return <Badge bg="" style={{ backgroundColor: "#DCFCE7", color: "#166534" }}>승인 완료</Badge>
        } else if (status === 'rejected') {
            return <Badge bg="" style={{ backgroundColor: "#FEE2E2", color: "#991B1B" }}>승인 거절</Badge>
        }
    }

    return (
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
                    {item.status === 'waiting' &&
                        <Button variant={""} className={`${styles.detailButton}`} size={"sm"}>
                            취소하기
                        </Button>}
                    {item.status === 'waiting' &&
                        <Button variant={""} className={`${styles.detailButton}`}
                                size={"sm"} onClick={() => navigate('/mypage/request/patch')}>
                            수정하기
                        </Button>}
                </Stack>
            </Stack>
        </Stack>
    );
}

export default RequestCard;