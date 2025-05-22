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
import {useEffect, useRef, useState} from "react";
import {useNavigate} from "react-router-dom";
import DeadlineTimer from "../main/DeadlineTimer";
import Count from "./Count";
import {normalizeNumber} from "../../utils/utils";
import ShareCard from "./share/ShareCard";
import {useRecoilValue} from "recoil";
import {userAtom} from "../../state/atoms";
import useConfirm from "../../hooks/useConfirm";
import axios from "axios";
import {useApiMutation} from "../../hooks/useApiMutation";
import {useToast} from "../../hooks/useToast";

const createDips = (productId) => axios.post(`/api/v1/dibs/products/${productId}`);
const deleteDips = (productId) => axios.delete(`/api/v1/dibs/products/${productId}`);

const ProductBuySection = ({ groupBuyInfo }) => {
    const [isZzim, setZzim] = useState(false);
    const [show, setShow] = useState(false);
    const showToast = useToast();
    const {openConfirm} = useConfirm();
    const target = useRef(null);
    const navigate = useNavigate();
    const currentParticipantCount = groupBuyInfo?.currentParticipantCount ?? 0;
    const targetParticipants = groupBuyInfo?.targetParticipants ?? 0;
    const percent = targetParticipants > 0 ? Math.round((currentParticipantCount / targetParticipants) * 100) : 0;
    const maxCount = targetParticipants - currentParticipantCount;
    const [count, setCount] = useState(1);
    const loginUser = useRecoilValue(userAtom);
    useEffect(() => {
        setCount(1);
        if (groupBuyInfo) {
            setZzim(groupBuyInfo.dibs);
        }
    }, [groupBuyInfo])

    const { mutate: createMutate } = useApiMutation(createDips, {
        onSuccess: () => {
            setZzim(true);
            showToast("찜 등록이 완료되었습니다.", "success");
        },
    });

    const { mutate: deleteMutate } = useApiMutation(deleteDips, {
        onSuccess: () => {
            setZzim(false);
            showToast("찜 등록이 해제되었습니다.", "success");
        },
    });

    const handleCountChange = (newCount) => {
        if (newCount === '') {
            setCount('');
        }
        newCount = normalizeNumber(newCount)
        if (newCount < 1 || newCount > maxCount) return;
        setCount(newCount);
    };

    const handleDibsClick = () => {
        if (loginUser.isLoggedIn) { // 로그인 된 경우

            if (groupBuyInfo.dibs) { //이미 찜한 경우
                deleteMutate(groupBuyInfo.product.productId);
            } else { //아직 찜하지 않으 경우
                createMutate(groupBuyInfo.product.productId);
            }
        } else { //로그인 안된 경우
            openConfirm({
                title: '로그인이 필요합니다.'
            })
        }
    }

    const handleParticipationClick = () => {
        if (loginUser.isLoggedIn) {
            navigate('payment', { state: {groupBuyInfo, count} });
        } else {
            navigate('/login');
        }
    }

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
                    <span style={{ color: "#9333EA" }}><ParticipationIcon /> {`${currentParticipantCount}/${targetParticipants}개 주문중`}</span>
                </Stack>
                <ProgressBar className={styles.customProgress} now={percent} />
                <Stack direction={"horizontal"} className={"d-flex justify-content-between mb-1"} >
                    <small>목표 수량: {targetParticipants}개</small>
                    <span style={{ color: "#DC2626" }}>
                        <ClockIcon /><DeadlineTimer deadline={groupBuyInfo?.deadline} />
                    </span>
                </Stack>
            </div>
            <div className={"p-3 border rounded"}>
                <div>수량 선택</div>
                <Stack direction={"horizontal"} className={"d-flex justify-content-between"} >
                    <span><Count count={count} handleChange={handleCountChange} max={maxCount} /></span>
                    <span>총 {(groupBuyInfo?.product.price * (Number(count) || 1)).toLocaleString()}원</span>
                </Stack>
            </div>
            <Stack direction={"horizontal"} gap={2} >
                <Button className={"w-100"} onClick={handleParticipationClick}>공동 구매 참여하기</Button>
                <span style={{ width: "42px", height: "100%", cursor: "pointer" }} onClick={handleDibsClick}>
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