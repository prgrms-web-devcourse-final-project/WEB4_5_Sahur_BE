import {Badge, Button, Overlay, ProgressBar, Stack} from "react-bootstrap";
import styles from "./ProductDetail.module.scss";
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
import CreateReviewCard from "./CreateReviewCard";
import ShareCard from "./ShareCard";

const ProductBuySection = () => {
    const [isZzim, setZzim] = useState(false)        ;
    const [show, setShow] = useState(false);
    const target = useRef(null);
    return (
        <Stack gap={3} >
            <Stack direction={"horizontal"} gap={3}>
                <h4>프리미엄 블루투스 이어폰 XS-500</h4>
                <Badge className={styles.roundBadge}>5회차</Badge>
            </Stack>
            <div>
                <Rating initialRating={4}
                        readonly
                        fullSymbol={<FontAwesomeIcon icon={faStarSolid} color="#facc15" size="lg" />}
                        emptySymbol={<FontAwesomeIcon icon={faStarRegular} color="#facc15" size="lg" />}
                />
                <small> 4.0 (128개 리뷰)</small>
            </div>
            <h3>39,000원</h3>
            <div className={"p-3"} style={{ background: "#FAF5FF", borderRadius: "8px" }}>
                <Stack direction={"horizontal"} className={"d-flex justify-content-between mb-1"} >
                    <span>공동 구매 현황</span>
                    <span style={{ color: "#9333EA" }}><ParticipationIcon /> 28/50명 참여</span>
                </Stack>
                <ProgressBar className={styles.customProgress} now={60} />
                <Stack direction={"horizontal"} className={"d-flex justify-content-between mb-1"} >
                    <small>목표 인원: 50명</small>
                    <span style={{ color: "#DC2626" }}><ClockIcon /> 2시간 남음</span>
                </Stack>
            </div>
            <div className={"p-3 border rounded"}>
                <div>수량 선택</div>
                <Stack direction={"horizontal"} className={"d-flex justify-content-between"} >
                    <span>카운터</span>
                    <span>총 39,000원</span>
                </Stack>
            </div>
            <Stack direction={"horizontal"} gap={2} >
                <Button className={"w-100"}>공동 구매 참여하기</Button>
                <span style={{ width: "42px", height: "100%", cursor: "pointer" }} onClick={() => setZzim(prev => !prev)}>
                    {isZzim ? <RedLikeIcon width={"100%"} height={"100%"} /> : <EmptyLikeIcon width={"100%"} height={"100%"}/>}
                </span>
                <span style={{ width: "42px",height: "100%", cursor: "pointer" }} ref={target} onClick={() => setShow(!show)}>
                    <ShareIcon width={"100%"} height={"100%"}/>
                </span>
                <Overlay target={target.current} show={show} placement="bottom"
                         popperConfig={{
                             modifiers: [{name: 'offset', options: {offset: [-50, 20]}}]
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