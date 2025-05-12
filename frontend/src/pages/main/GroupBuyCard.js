import {Badge, Card, ProgressBar, Stack} from "react-bootstrap";
import sampleImg from "../../assets/images/sample.png"
import {useNavigate} from "react-router-dom";
import { ReactComponent as ParticipationIcon } from "../../assets/images/icon/participation.svg"
import { ReactComponent as ClockIcon } from "../../assets/images/icon/clock.svg"
import styles from "./Main.module.scss"

const GroupBuyCard = () => {
    const navigate = useNavigate();
    return (
        <Card className={`p-2 m-1 cursor-pointer ${styles.groupBuyCardBorder}`} onClick={() => navigate("/groupBuy/1")}>
            <div className={styles.imageWrapper}>
                <img src={sampleImg} alt="썸네일" className={styles.img} />
                <Badge className={styles.badgeTopRight}>마감임박</Badge>
            </div>
            <Card.Body className={"p-2"}>
                <Card.Title>프리미엄 블푸투스 이어폰</Card.Title>
                <Card.Text>
                    <small style={{ color: "#6B7280" }}>해외직구 | 무료배송</small>
                    <div style={{ fontWeight: "700" }}>39,000</div>
                    <Stack direction={"horizontal"} className={"d-flex justify-content-between mb-1"} >
                        <span style={{ color: "#9333EA" }}><ParticipationIcon /> 28/50명 참여</span>
                        <span style={{ color: "#DC2626" }}><ClockIcon /> 2시간 남음</span>
                    </Stack>
                    <ProgressBar className={styles.customProgress} now={60} />
                </Card.Text>
            </Card.Body>
        </Card>
    );
}

export default GroupBuyCard;