import {Button, Card, Col, Image, Row, Stack} from "react-bootstrap";
import sampleImg from "../../assets/images/sample.png"
import styles from "./Payment.module.scss"

const OrderSection = () => {
    return (
        <Card>
            <Card.Body className={"p-3"}>
                <h3>주문 정보</h3>
                <Row className={`p-3 ${styles.sectionDivider}`}>
                    <Col xs={3}>
                        <Image src={sampleImg} fluid/>
                    </Col>
                    <Col xs={9}>
                        <Stack direction={"vertical"}>
                            <h5>프리미엄 블루투스 이어폰 슈퍼 에디션</h5>
                            <desc>1개</desc>
                        </Stack>
                    </Col>
                </Row>
                <Row className={`p-3 ${styles.sectionDivider}`}>
                    <Col xs={12} className={'d-flex justify-content-between'}>
                        <span>상품금액</span>
                        <span>39,000</span>
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
                            <h4>39,000</h4>
                        </div>
                        <div className={styles.blueCard}>
                            <div style={{ color: "#1E40AF", fontSize: "18px" }}>공동구매 정보</div>
                            <div className={'d-flex justify-content-between'}>
                                <span>현재 참여 인원</span>
                                <span>28/50명</span>
                            </div>
                            <div className={'d-flex justify-content-between'}>
                                <span>최소 인원</span>
                                <span>30명</span>
                            </div>
                            <div className={'d-flex justify-content-between'}>
                                <span>마감 시간</span>
                                <span>2시간 12분 남음</span>
                            </div>
                        </div>
                        <div style={{ marginTop: "10px" }}>
                            <Button className={"w-100"}>결제하기</Button>
                        </div>
                        <div style={{ marginLeft: "auto", marginRight: "auto" }}>
                            <desc>공동 구매는 즉시 결재 후, <br/>
                                인원 미달 시 전액 환불됩니다.</desc>
                        </div>
                    </Col>
                </Row>
            </Card.Body>
        </Card>
    )
}

export default OrderSection;