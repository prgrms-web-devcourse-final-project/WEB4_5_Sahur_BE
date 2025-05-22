import {Button, Col, Image, Row, Stack} from "react-bootstrap";
import styles from "./Payment.module.scss"
import {loadTossPayments} from "@tosspayments/payment-sdk";
import DeadlineTimer from "../main/DeadlineTimer";
import axios from "axios";
import {useApiMutation} from "../../hooks/useApiMutation";
import {useRecoilValue} from "recoil";
import {userAtom} from "../../state/atoms";

const createOrder = (params) => axios.post(`/api/v1/orders`, params);
const createDelivery = (params) => axios.post(`/api/v1/deliveries/order/${params.orderId}`, params.sendData);

const OrderSection = ({ groupBuyInfo, count, deliveryInfo }) => {
    const loginUser = useRecoilValue(userAtom);
    console.log(deliveryInfo)
    const currentParticipantCount = groupBuyInfo?.currentParticipantCount ?? 0;
    const targetParticipants = groupBuyInfo?.targetParticipants ?? 0;

    const { mutateAsync: createOrderMutate } = useApiMutation(createOrder);

    const { mutateAsync: createDeliveryMutate } = useApiMutation(createDelivery, {
        onSuccess: (data) => {
            console.log(data);
        },
    });

    const handlePaymentClick = async () => {
        const orderResponse = await createOrderMutate({
            memberId: loginUser.memberId,
            groupBuyId: groupBuyInfo?.groupBuyId,
            productId: groupBuyInfo?.product.productId,
            quantity: count,
        });
        console.log(orderResponse.data);
        const deliveryResponse = await createDelivery({
            orderId: orderResponse.data.data.orderId,
            sendData: deliveryInfo
        });
        const client_id = 'test_ck_5OWRapdA8dP5dB4NPlyB8o1zEqZK';
        const random = new Date().getTime() + Math.random(); //난수생성
        const randomId = btoa(random); //난수를 btoa(base64)로 인코딩한 orderID
        loadTossPayments(client_id).then(tossPayments => {
            tossPayments.requestPayment("카드", {
                amount: 38000, //주문가격
                orderId: `${randomId}`, //문자열 처리를 위한 ``사용
                orderName: "결제 이름", //결제 이름(여러건일 경우 복수처리)
                customerName: '테스트', //판매자, 판매처 이름
                successUrl: 'http://localhost:3000/payment/success',
                failUrl: 'http://localhost:3000/payment/fail',
            })
        });

    }
    return (
        <>
            <h3>주문 정보</h3>
            <Row className={`p-3 ${styles.sectionDivider}`}>
                <Col xs={3}>
                    <Image src={groupBuyInfo?.product.imageUrl[0]} fluid/>
                </Col>
                <Col xs={9}>
                    <Stack direction={"vertical"}>
                        <h5>{groupBuyInfo?.product.title}</h5>
                        <desc>{count}개</desc>
                    </Stack>
                </Col>
            </Row>
            <Row className={`p-3 ${styles.sectionDivider} text-gray-300`}>
                <Col xs={12} className={'d-flex justify-content-between'}>
                    <span>상품금액</span>
                    <span>{groupBuyInfo?.product.price.toLocaleString()}원</span>
                </Col>
                <Col xs={12} className={'d-flex justify-content-between'}>
                    <span>배송비</span>
                    <span>무료</span>
                </Col>
            </Row>
            <Row className={`p-3`}>
                <Col xs={12} className={styles.orderBottom}>
                    <div className={'d-flex justify-content-between'}>
                        <h4>총 결재금액</h4>
                        <h4>{(groupBuyInfo?.product.price * (Number(count) || 1)).toLocaleString()}원</h4>
                    </div>
                    <div className={styles.blueCard}>
                        <div style={{ color: "#1E40AF", fontSize: "18px" }}>공동구매 정보</div>
                        <div className={'d-flex justify-content-between'}>
                            <span>현재 참여 인원</span>
                            <span>{`${currentParticipantCount}/${targetParticipants}명`}</span>
                        </div>
                        <div className={'d-flex justify-content-between'}>
                            <span>최소 인원</span>
                            <span>30명</span>
                        </div>
                        <div className={'d-flex justify-content-between'}>
                            <span>마감 시간</span>
                            <span><DeadlineTimer deadline={groupBuyInfo?.deadline} /></span>
                        </div>
                    </div>
                    <div style={{ marginTop: "10px" }}>
                        <Button className={"w-100"} disabled={!deliveryInfo.ready} onClick={handlePaymentClick}>결제하기</Button>
                    </div>
                    <div style={{ marginLeft: "auto", marginRight: "auto" }}>
                        <desc className={"text-gray-300"}>공동 구매는 즉시 결재 후, <br/>
                            인원 미달 시 전액 환불됩니다.</desc>
                    </div>
                </Col>
            </Row>
        </>
    )
}

export default OrderSection;