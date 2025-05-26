import {Button, Card, Form, Stack} from "react-bootstrap";
import ThemedSelect from "../../../shared/ThemedSelect";
import styles from "../products/AdminProducts.module.scss";
import CreateReviewImageBox from "../../products/review/CreateReviewImageBox";
import {
    ReactComponent as QuestionIcon
} from "../../../assets/images/icon/question.svg";
import {useNavigate} from "react-router-dom";
import {useState} from "react";
import CloseRecruitModal from "./CloseRecruitModal";

const options = [
    { value: 'chocolate', label: 'Chocolate' },
    { value: 'strawberry', label: 'Strawberry' },
    { value: 'vanilla', label: 'Vanilla' }
]

const AdminGroupBuyDetail = () => {
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
                <Card.Body className={"m-4"}>
                    <h3>등록된 공동 구매 상품 관리</h3>
                    <desc className={"text-gray-300"}>등록된 상품에 대한 수정을 합니다.</desc>
                </Card.Body>
            </Card>
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
                        <CreateReviewImageBox imageFileList={reviewImageFileList} setImageFileList={setReviewFileImageList} />
                    </Stack>
                </Card.Body>
                <Card.Footer className={"mx-3"}>
                    <desc className={"text-gray-300"}>첫번째 이미지가 대표 이미지로 사용됩니다. 최대 10장까지 등록 가능합니다.</desc>
                </Card.Footer>
            </Card>
            <Card>
                <Card.Header>
                    <h4>공동 구매 설정</h4>
                </Card.Header>
                <Card.Body className={"m-3"}>
                    <Stack direction={"horizontal"} gap={10} className={"justify-content-center"}>
                        <Form.Group controlId={"forDateTime"} >
                            <Form.Label>마감 일시</Form.Label>
                            <Stack direction={"horizontal"} gap={4}>
                                <Form.Control type="date"/>
                                <Form.Control type="time"/>
                            </Stack>
                        </Form.Group>
                        <Form.Group controlId={"productTitle"} >
                            <Form.Label>최소 참여 인원</Form.Label>
                            <div style={{ position: 'relative' }}>
                                <Form.Control
                                    type="text"
                                    value={minimunPerson}
                                    onChange={(e) => setMinimunPerson(e.target.value)}
                                />
                                <span className={styles.textInControl}>명</span>
                            </div>
                        </Form.Group>
                    </Stack>
                    <div className={styles.groupBuyDesc}>
                        <div className={styles.groupBuyDescTitle}>
                            <QuestionIcon/>  공동구매 결제 안내</div>
                        <desc className={styles.groupBuyDescSubTitle}>공동구매는 주문 시점에 결제가 즉시 진행되며, 마감까지 인원이 부족할 경우 결제는 자동으로 취소됩니다. <br/>
                            공동구매가 성사되면 상품이 정상 배송됩니다.
                        </desc>
                    </div>
                </Card.Body>
            </Card>
            <Card>
                <Card.Body className={"m-7"}>
                    <Stack direction={"horizontal"} className={"justify-content-between"}>
                        <Button variant={"primary"} onClick={() => setModalOpen(true)}>모집 종료</Button>
                        <Button className={styles.detailButton} variant={""} onClick={() => navigate('/admin/groupBuy')}>저장</Button>
                    </Stack>
                </Card.Body>
            </Card>
            <CloseRecruitModal show={modalOpen} onHide={() => setModalOpen(false)} onConfirm={() => navigate('/admin/groupBuy')} />
        </Stack>
    );
};

export default AdminGroupBuyDetail;