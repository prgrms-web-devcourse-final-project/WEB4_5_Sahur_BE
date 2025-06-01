import {useNavigate} from "react-router-dom";
import {Button, Card, Form, Stack} from "react-bootstrap";
import ThemedSelect from "../../../shared/ThemedSelect";
import CreateReviewImageBox from "../../products/review/CreateReviewImageBox";
import {
    ReactComponent as QuestionIcon
} from "../../../assets/images/icon/question.svg";
import {
    ReactComponent as InfoBrownIcon
} from "../../../assets/images/icon/info-brown.svg";
import React, {useState} from "react";
import styles from "./MyPageRequests.module.scss"
import CreateRequestImageBox from "./CreateRequestImageBox";

const options = [
    { value: 'chocolate', label: 'Chocolate' },
    { value: 'strawberry', label: 'Strawberry' },
    { value: 'vanilla', label: 'Vanilla' }
]

const MyPageRequest = () => {
    const navigate = useNavigate();
    const [productTitle, setProductTitle] = useState('');
    const [productPrice, setProductPrice] = useState(0);
    const [productDesc, setProductDesc] = useState('');
    const [reviewImageFileList, setReviewFileImageList] = useState();
    const [minimunPerson, setMinimunPerson] = useState(0)
    const [modalOpen, setModalOpen] = useState(false);

    return (
        <Stack direction={"vertical"} gap={2} className={"m-3"}>
            <Card>
                <Card.Header>
                    <h4>기본 정보</h4>
                </Card.Header>
                <Card.Body className={"m-3"}>
                    <Form.Group controlId={"productTitle"} >
                        <Form.Label>상품명</Form.Label>
                        <Form.Control
                            type="text"
                            value={productTitle}
                            onChange={(e) => setProductTitle(e.target.value)}
                        />
                    </Form.Group>
                    <Stack direction={"vertical"} gap={3} className={"mt-3"}>
                        <Stack direction={"horizontal"} gap={3} className="justify-content-center">
                            <Form.Group controlId={"productTitle"} >
                                <Form.Label>카테고리</Form.Label>
                                <div style={{ width: '215px' }}>
                                    <ThemedSelect options={options}/>
                                </div>
                            </Form.Group>
                            <Form.Group controlId={"productTitle"} >
                                <Form.Label>세부카테고리</Form.Label>
                                <div style={{ width: '215px' }}>
                                    <ThemedSelect options={options}/>
                                </div>
                            </Form.Group>
                        </Stack>
                        <Stack direction={"horizontal"} gap={3} className="justify-content-center">
                            <Form.Group controlId={"productTitle"} >
                                <Form.Label>진행회차</Form.Label>
                                <div style={{ width: '215px' }}>
                                    <ThemedSelect options={options}/>
                                </div>
                            </Form.Group>
                            <Form.Group controlId={"productTitle"} >
                                <Form.Label>판매가</Form.Label>
                                <div style={{ position: 'relative' }}>
                                    <Form.Control
                                        type="text"
                                        value={productPrice}
                                        onChange={(e) => setProductPrice(e.target.value)}
                                    />
                                    <span className={styles.textInControl}>원</span>
                                </div>
                            </Form.Group>
                        </Stack>
                        <Form.Group className={"p-3"}>
                            <Form.Label>상품 설명</Form.Label>
                            <Form.Control
                                as="textarea"
                                rows={4}
                                placeholder="상품에 대한 상세 설명을 입력하세요."
                                value={productDesc}
                                onChange={(e) => setProductDesc(e.target.value)}
                                className={`h-25`}
                            />
                        </Form.Group>
                    </Stack>
                </Card.Body>
            </Card>
            <Card>
                <Card.Header>
                    <h4>상품 이미지</h4>
                </Card.Header>
                <Card.Body className={"m-3"}>
                    <Stack direction={"horizontal"} className={"justify-content-center"}>
                        <CreateRequestImageBox imageFileList={reviewImageFileList} setImageFileList={setReviewFileImageList} />
                    </Stack>
                </Card.Body>
                <Card.Footer className={"mx-3"}>
                    <desc className={"text-gray-300"}>첫번째 이미지가 대표 이미지로 사용됩니다. 최대 10장까지 등록 가능합니다.</desc>
                </Card.Footer>
            </Card>
            <div className={styles.groupBuyDesc}>
                <div className={styles.groupBuyDescTitle}>
                    <QuestionIcon/>  공동구매 결제 안내</div>
                <desc className={styles.groupBuyDescSubTitle}>공동구매는 주문 시점에 결제가 즉시 진행되며, 마감까지 인원이 부족할 경우 결제는 자동으로 취소됩니다. <br/>
                    공동구매가 성사되면 상품이 정상 배송됩니다.
                </desc>
            </div>
            <Card style={{ backgroundColor: "transparent" }}>
                <Card.Body className={"mx-3 ms-auto"}>
                    <Stack direction={"horizontal"} className={"justify-content-between"}>
                        <Stack direction={"horizontal"} gap={5}>
                            <Button className={styles.detailButton} variant={""} onClick={() => navigate('/mypage/requests')}>취소</Button>
                            <Button variant={"primary"}>완료</Button>
                        </Stack>
                    </Stack>
                </Card.Body>
            </Card>
        </Stack>
    );
}

export default MyPageRequest;