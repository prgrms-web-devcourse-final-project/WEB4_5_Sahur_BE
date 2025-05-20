import {Badge, Card, ProgressBar, Stack} from "react-bootstrap";
import {useNavigate} from "react-router-dom";
import { ReactComponent as ParticipationIcon } from "../../assets/images/icon/participation.svg"
import { ReactComponent as ClockIcon } from "../../assets/images/icon/clock.svg"
import styles from "./Main.module.scss"
import DeadlineTimer from "./DeadlineTimer"

const GroupBuyCard = ({ product }) => {
    const navigate = useNavigate();
    const current = product?.currentParticipantCount ?? 0;
    const target = product?.targetParticipants ?? 0;
    const percent = target > 0 ? Math.round((current / target) * 100) : 0;
    return (
        <Card className={`p-2 m-1 cursor-pointer ${styles.groupBuyCardBorder}`}
              onClick={() => navigate(`/groupBuy/${product?.groupBuyId}`)}>
            <div className={styles.imageWrapper}>
                <img src={product?.product.imageUrl[0]} alt="썸네일" className={styles.img} />
                {product?.deadlineToday && <Badge className={styles.badgeTopRight}>마감임박</Badge>}
            </div>
            <Card.Body className={"p-2"}>
                <Card.Title>{product?.product.title}</Card.Title>
                <Card.Text>
                    <small style={{ color: "#6B7280" }}>해외직구 | 무료배송</small>
                    <div style={{ fontWeight: "700" }}>{product?.product.price.toLocaleString()}원</div>
                    <Stack direction={"horizontal"} className={"d-flex justify-content-between mb-1"} >
                        <span style={{ color: "#9333EA" }}><ParticipationIcon />
                            {`${current} / ${target}명 참여`}
                        </span>
                        <span style={{ color: "#DC2626" }}>
                            <ClockIcon /><DeadlineTimer deadline={product?.deadline} />
                        </span>
                    </Stack>
                    <ProgressBar className={styles.customProgress} now={percent} />
                </Card.Text>
            </Card.Body>
        </Card>
    );
}

export default GroupBuyCard;