import {useNavigate} from "react-router-dom";
import {Button, Card, Col, Form, Image, Row, Stack} from "react-bootstrap";
import styles from "./MyPageOrders.module.scss";
import React from "react";
import ThemedSelect from "../../../shared/ThemedSelect";

const options = [
    { value: 'chocolate', label: '배송 준비' },
    { value: 'strawberry', label: '배송 중' },
    { value: 'vanilla', label: '배송 완료' }
]

const MyPageOrdersDetail = () => {
    const navigate = useNavigate();

    return (
        <Stack direction={"vertical"} gap={2} className={"m-3"}>
            <Card>
                <Card.Header className={"border-0 mx-4"}>
                    <h4>주문 정보</h4>
                </Card.Header>
                <Card.Body className={"mx-5 mb-5"}>
                    <Stack gap={3} className={"border rounded p-5"}>
                        <Stack direction={"horizontal"} gap={2} >
                            <Image width={50} height={50} src={"https://i.pravatar.cc/150?img=49.jpg"} />
                            <Stack gap={1} >
                                <div className="fw-semibold">프리미엄 블루투스 이어폰 100</div>
                                <div>
                                    <desc className={"fw-light text-muted"}>1개</desc>
                                </div>
                            </Stack>
                        </Stack>
                        <hr/>
                        <Row className={`text-gray-300`}>
                            <Col xs={12} className={"d-flex justify-content-between"}>
                                <span>상품금액</span>
                                <span>{"39000".toLocaleString()}원</span>
                            </Col>
                            <Col xs={12} className={"d-flex justify-content-between"}>
                                <span>배송비</span>
                                <span>무료</span>
                            </Col>
                        </Row>
                        <hr/>
                        <Stack direction={"horizontal"} className={"justify-content-between"}>
                            <span className="fw-bold">총 결제 금액</span>
                            <span className="fw-bold">39,000원</span>
                        </Stack>
                        <Stack gap={1} className={"p-4 rounded-3"} style={{ background: "#EFF6FF", color: "#1D4ED8" }}>
                            <div className={"fw-semibold"} style={{ color: "#1E40AF" }}>공동 구매 정보</div>
                            <Stack direction={"horizontal"} className={"justify-content-between"}>
                                <span>현재 참여 인원</span>
                                <span>28/50명</span>
                            </Stack>
                            <Stack direction={"horizontal"} className={"justify-content-between"}>
                                <span>최소 인원</span>
                                <span>30명</span>
                            </Stack>
                            <Stack direction={"horizontal"} className={"justify-content-between"}>
                                <span>마감 시간</span>
                                <span>2시간 13분 남음</span>
                            </Stack>
                        </Stack>
                        <hr/>
                        <Stack gap={1} className={"p-4 rounded-3"} style={{ background: "#F3E8FF", color: "#A855F7" }}>
                            <div className={"fw-semibold"} style={{ color: "#6B21A8" }}>결제 정보</div>
                            <Stack direction={"horizontal"} className={"justify-content-between"}>
                                <span>결제 키</span>
                                <span>tviva202505141652388R2H9</span>
                            </Stack>
                            <Stack direction={"horizontal"} className={"justify-content-between"}>
                                <span>결제 방식</span>
                                <span>간편 결제</span>
                            </Stack>
                            <Stack direction={"horizontal"} className={"justify-content-between"}>
                                <span>결제 시간</span>
                                <span>2025-12-05 16:38:58</span>
                            </Stack>
                        </Stack>
                        <Button variant={""} className={styles.paymentStatus}>결제 완료</Button>
                    </Stack>
                </Card.Body>
            </Card>
            <Card>
                <Card.Header className={"border-0 mx-4"}>
                    <Stack direction={"horizontal"} gap={4} >
                        <h4>배송 정보</h4>
                    </Stack>
                </Card.Header>
                <Card.Body className={"mx-5 mb-5"}>
                    <Stack gap={3} className={"border rounded p-5"}>
                        <Form.Group controlId={"ordersDetailForm1"} >
                            <Form.Label>받는분</Form.Label>
                            <Form.Control type="text"/>
                        </Form.Group>
                        <Form.Group controlId={"ordersDetailForm2"} >
                            <Form.Label>연락처</Form.Label>
                            <Form.Control type="text"/>
                        </Form.Group>
                        <Form.Group controlId={"ordersDetailForm3"} className={"w-25"} >
                            <Form.Label>우편번호</Form.Label>
                            <Form.Control type="text"/>
                        </Form.Group>
                        <Form.Group controlId={"ordersDetailForm4"} >
                            <Form.Label>주소</Form.Label>
                            <Form.Control type="text"/>
                        </Form.Group>
                        <Form.Group controlId={"ordersDetailForm5"} >
                            <Form.Label>상세 주소</Form.Label>
                            <Form.Control type="text"/>
                        </Form.Group>
                        <Form.Group controlId={"ordersDetailForm6"} >
                            <Form.Label>고유 통관 번호</Form.Label>
                            <Form.Control type="text"/>
                        </Form.Group>
                        <Form.Group controlId={"ordersDetailForm7"} >
                            <Form.Label>송장 번호</Form.Label>
                            <Form.Control type="text"/>
                        </Form.Group>
                    </Stack>
                </Card.Body>
                <Button variant={""} className={`${styles.paymentStatus} mx-5 mb-5`}>배송 준비</Button>
            </Card>
        </Stack>
    );
}

export default MyPageOrdersDetail;