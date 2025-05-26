import {useNavigate} from "react-router-dom";
import {Button, Card, Form, Image, Stack} from "react-bootstrap";
import styles from "./AdminOrders.module.scss";
import React, {useState} from "react";
import backImg from "../../../assets/images/icon/back.png"
import ThemedSelect from "../../../shared/ThemedSelect";

const options = [
    { value: 'chocolate', label: '배송 준비' },
    { value: 'strawberry', label: '배송 중' },
    { value: 'vanilla', label: '배송 완료' }
]

const AdminOrdersDetail = () => {
    const navigate = useNavigate();

    return (
        <Stack direction={"vertical"} gap={2} className={"m-3"}>
            <Card>
                <Card.Body className={"m-4"}>
                    <Stack direction={"horizontal"} gap={4}>
                        <span className={"cursor-pointer"} onClick={() => navigate('/admin/orders')}>
                            <Image src={backImg} width={50} height={50} />
                        </span>
                        <Stack>
                            <h3>고객 주문 관리</h3>
                            <desc className={"text-gray-300"}>고객 주문에 대한 처리를 합니다.</desc>
                        </Stack>
                    </Stack>

                </Card.Body>
            </Card>
            <Card>
                <Card.Header className={"border-0 mx-4"}>
                    <Stack direction={"horizontal"} gap={4} >
                        <h4>주문 정보</h4>
                        <desc className={"text-gray-300"} >주문 번호: ORD-20231201</desc>
                    </Stack>
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
                        <Stack direction={"horizontal"} className={"justify-content-between"}>
                            <span className="fw-bold">주문 금액</span>
                            <span className="fw-bold">39,000원</span>
                        </Stack>
                        <hr/>
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
                            <Form.Label>운송장 번호</Form.Label>
                            <Form.Control type="text"/>
                        </Form.Group>
                    </Stack>
                </Card.Body>
                <Card.Body className={"mx-5 mb-5"}>
                    <Stack direction={"horizontal"} gap={3} className={"border rounded p-5 justify-content-between"}>
                        <Button variant={"danger"} className={"border-0 rounded"} style={{ background: "#FF5555" }}>주문 취소</Button>
                        <Stack direction={"horizontal"} gap={2}>
                            <span>배송 상태:</span>
                            <div style={{ width: '215px' }}>
                                <ThemedSelect options={options}/>
                            </div>
                        </Stack>
                        <Stack direction={"horizontal"} gap={2}>
                            <Button variant={""} className={styles.detailButton}>취소</Button>
                            <Button>수정</Button>
                        </Stack>
                    </Stack>
                </Card.Body>
            </Card>
        </Stack>
    );
}

export default AdminOrdersDetail;