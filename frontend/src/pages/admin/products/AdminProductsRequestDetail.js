import {Button, Card, Stack} from "react-bootstrap";
import {
    ReactComponent as ArrowLeftIcon
} from "../../../assets/images/icon/arrow-left-black.svg";
import CreateReviewImageBox from "../../products/review/CreateReviewImageBox";
import {useState} from "react";
import styles from "./AdminProducts.module.scss"
import {useNavigate} from "react-router-dom";
import GroupBuyRegisterDenyModal from "./GroupBuyRegisterDenyModal";

const AdminProductsRequestDetail = () => {
    const [imageFileList, setImageFileList] = useState();
    const [modalOpen, setModalOpen] = useState(false);
    const navigate = useNavigate();

    return (
        <Card>
            <Card.Header>
                <ArrowLeftIcon width={18} height={18} style={{ marginRight: 10 }}/>돌아가기
                <h3 className={"mt-5"}>등록 상품 관리</h3>
            </Card.Header>
            <Card.Body className={"mx-4 mt-4 p-5 border rounded"}>
                <h4>기본 정보</h4>
                <Stack gap={4}>
                    <Stack>
                        <span className={"fw-semibold"}>상품명</span>
                        <span className={"text-gray-500"}>프리미엄 블루투스 이어폰 100</span>
                    </Stack>
                    <Stack direction={"horizontal"}>
                        <Stack className={"w-50"}>
                            <span className={"fw-semibold"}>카테고리</span>
                            <span className={"text-gray-500"}>디지털/가전</span>
                        </Stack>
                        <Stack className={"w-50"}>
                            <span className={"fw-semibold"}>세부카테고리</span>
                            <span className={"text-gray-500"}>이어폰</span>
                        </Stack>
                    </Stack>
                    <Stack>
                        <span className={"fw-semibold"}>URL</span>
                        <span className={"text-gray-500"}>https://www.naver.com/</span>
                    </Stack>
                </Stack>
            </Card.Body>
            <Card.Body className={"mx-4 mt-4 p-5 border rounded"}>
                <Stack gap={5}>
                    <h4>상품 설명</h4>
                    <desc style={{ background: "#F9FAFB" }} className={"p-6"}>고품질 블루투스 이어폰으로, 노이즈 캔슬링 기능과 최대 24시간 배터리 수명을 제공합니다.
                        인체공학적 디자인으로 장시간 차용해도 편안합니다.</desc>
                </Stack>
            </Card.Body>
            <Card.Body className={"mx-4 mt-4 p-5 border rounded"}>
                <Stack gap={3}>
                    <h4>상품 이미지</h4>
                    <CreateReviewImageBox imageFileList={imageFileList} setImageFileList={setImageFileList} />
                    <desc className={"text-gray-300"}>* 상품 이미지는 최대 5개까지 등록 가능합니다.</desc>
                </Stack>
            </Card.Body>
            <Card.Footer className={"mx-4 mt-4 p-5"}>
                <Stack className={"justify-content-end"} direction={"horizontal"} gap={3}>
                    <Button className={styles.detailButton} variant={""} onClick={() => setModalOpen(true)}>거부</Button>
                    <Button onClick={() => navigate('/admin/products/requests')}>승인</Button>
                </Stack>
            </Card.Footer>
            <GroupBuyRegisterDenyModal show={modalOpen} onHide={() => setModalOpen(false)} onConfirm={() => navigate('/admin/products/requests')} />
        </Card>
    );
}

export default AdminProductsRequestDetail;