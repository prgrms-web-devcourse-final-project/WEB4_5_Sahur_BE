import {Badge, Button, Overlay, ProgressBar, Stack} from "react-bootstrap";
import styles from "./GroupBuy.module.scss";
import Rating from "react-rating";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faStar as faStarSolid} from "@fortawesome/free-solid-svg-icons";
import {faStar as faStarRegular} from "@fortawesome/free-regular-svg-icons";
import {
    ReactComponent as ParticipationIcon
} from "../../assets/images/icon/participation.svg";
import {ReactComponent as ClockIcon} from "../../assets/images/icon/clock.svg";
import {
    ReactComponent as RedLikeIcon
} from "../../assets/images/icon/red-like.svg";
import {
    ReactComponent as EmptyLikeIcon
} from "../../assets/images/icon/empty-like.svg";
import {ReactComponent as ShareIcon} from "../../assets/images/icon/share.svg";
import {useRef, useState} from "react";
import ShareCard from "./ShareCard";
import {useNavigate} from "react-router-dom";
import DeadlineTimer from "../main/DeadlineTimer";
import Count from "./Count";

const ProductBuySection = ({ groupBuyInfo }) => {
    const [isZzim, setZzim] = useState(false)        ;
    const [show, setShow] = useState(false);
    const target = useRef(null);
    const navigate = useNavigate();
    const currentParticipantCount = groupBuyInfo?.currentParticipantCount ?? 0;
    const targetParticipants = groupBuyInfo?.targetParticipants ?? 0;
    const percent = targetParticipants > 0 ? Math.round((currentParticipantCount / targetParticipants) * 100) : 0;
    return (
        <Stack gap={3} >
            <Stack direction={"horizontal"} gap={3}>
                <h4>{groupBuyInfo?.product.title}</h4>
                <Badge className={styles.roundBadge}>{groupBuyInfo?.round}회차</Badge>
            </Stack>
            <div>
                <Rating initialRating={groupBuyInfo?.averageRate}
                        readonly
                        fullSymbol={<FontAwesomeIcon icon={faStarSolid} color="#facc15" size="lg" />}
                        emptySymbol={<FontAwesomeIcon icon={faStarRegular} color="#facc15" size="lg" />}
                />
                <small> {groupBuyInfo?.averageRate.toFixed(1)} ({groupBuyInfo?.reviewCount}개 리뷰)</small>
            </div>
            <h3>{groupBuyInfo?.product.price.toLocaleString()}원</h3>
            <div className={"p-3"} style={{ background: "#FAF5FF", borderRadius: "8px" }}>
                <Stack direction={"horizontal"} className={"d-flex justify-content-between mb-1"} >
                    <span>공동 구매 현황</span>
                    <span style={{ color: "#9333EA" }}><ParticipationIcon /> {`${currentParticipantCount}/${targetParticipants}명 참여`}</span>
                </Stack>
                <ProgressBar className={styles.customProgress} now={percent} />
                <Stack direction={"horizontal"} className={"d-flex justify-content-between mb-1"} >
                    <small>목표 인원: {targetParticipants}명</small>
                    <span style={{ color: "#DC2626" }}>
                        <ClockIcon /><DeadlineTimer deadline={groupBuyInfo?.deadline} />
                    </span>
                </Stack>
            </div>
            <div className={"p-3 border rounded"}>
                <div>수량 선택</div>
                <Stack direction={"horizontal"} className={"d-flex justify-content-between"} >
                    <span><Count /></span>
                    <span>총 {groupBuyInfo?.product.price.toLocaleString()}원</span>
                </Stack>
            </div>
            <Stack direction={"horizontal"} gap={2} >
                <Button className={"w-100"} onClick={() => navigate('payment')}>공동 구매 참여하기</Button>
                <span style={{ width: "42px", height: "100%", cursor: "pointer" }} onClick={() => setZzim(prev => !prev)}>
                    {isZzim ? <RedLikeIcon width={"100%"} height={"100%"} /> : <EmptyLikeIcon width={"100%"} height={"100%"}/>}
                </span>
                <span style={{ width: "42px",height: "100%", cursor: "pointer" }} ref={target} onClick={() => setShow(!show)}>
                    <ShareIcon width={"100%"} height={"100%"}/>
                </span>
                <Overlay target={target.current} show={show} placement="bottom-start"
                         popperConfig={{
                             modifiers: [{name: 'offset', options: {offset: [-390, 20]}}]
                         }}>
                    <div>
                        <ShareCard onClose={() => setShow(false)}/>
                    </div>
                </Overlay>
            </Stack>
        </Stack>
    );
}

export default ProductBuySection;