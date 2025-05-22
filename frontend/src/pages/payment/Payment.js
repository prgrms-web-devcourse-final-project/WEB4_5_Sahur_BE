import {Card, Col, Row, Stack} from "react-bootstrap";
import DeliverySection from "./DeliverySection";
import OrderSection from "./OrderSection";
import arrowLeft from "../../assets/images/icon/arrow-left.png";
import {useLocation, useNavigate} from "react-router-dom";
import styles from "./Payment.module.scss"
import { ReactComponent as InfoBrownIcon } from "../../assets/images/icon/info-brown.svg"
import useConfirm from "../../hooks/useConfirm";
import {useEffect, useState} from "react";
import {isEmptyOrNull} from "../../utils/utils";

const Payment = () => {
    const {openConfirm} = useConfirm();
    const navigate = useNavigate();
    const location = useLocation();
    const [deliveryInfo, setDeliveryInfo] = useState({ ready: false });
    const {groupBuyInfo, count} = location.state || {};

    useEffect(() => {
        console.log(groupBuyInfo)
        if (isEmptyOrNull(groupBuyInfo)) {
            openConfirm({
                title: "만료된 세션입니다.",
                showCancelButton: false,
                callback: () => {
                    const parentPath = location.pathname.split('/').slice(0, -1).join('/');
                    navigate(parentPath);
                }
            })
        }
    }, [groupBuyInfo])
    return (
        <>
            <Card>
                <Card.Body className={"ps-5 d-flex justify-content-between"} >
                    <span className={"cursor-pointer"} onClick={() => navigate("/groupBuy/1")}>
                        <img src={arrowLeft} style={{ width: "20px" }}/>
                    </span>
                    <div className={"d-flex justify-content-center"}>
                        <h4 style={{ marginRight: '30px', height: '40px' }}>결제 정보</h4>
                    </div>
                    <span/>
                </Card.Body>
            </Card>
            <div className={styles.paymentBody}>
                <div>
                    <Row className={styles.paymentDesc}>
                        <Col md={12}>
                            <div className={styles.paymentDescTitle}>
                                <InfoBrownIcon/>  공동구매 결제 안내</div>
                            <desc className={styles.paymentDescSubTitle}>공동구매는 주문 시점에 결제가 즉시 진행되며, 마감까지 인원이 부족할 경우 결제는 자동으로 취소됩니다. <br/>
                                공동구매가 성사되면 상품이 정상 배송됩니다.
                            </desc>
                        </Col>
                    </Row>
                    <Row className={"m-3 align-items-stretch"}>
                        <Col md={7}>
                            <Card className="h-100">
                                <Card.Body className={"p-3"}>
                                    <DeliverySection setDeliveryInfo={setDeliveryInfo} />
                                </Card.Body>
                            </Card>
                        </Col>
                        <Col md={5}>
                            <Card className="h-100">
                                <Card.Body className={"p-3"}>
                                    <OrderSection groupBuyInfo={groupBuyInfo} count={count} deliveryInfo={deliveryInfo}/>
                                </Card.Body>
                            </Card>
                        </Col>
                    </Row>
                </div>
            </div>
        </>
    );
}

export default Payment;