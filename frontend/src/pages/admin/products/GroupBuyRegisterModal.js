import { Modal, Button } from 'react-bootstrap';
import styles from "./AdminProducts.module.scss"

function GroupBuyRegisterModal({ show, onHide, onConfirm }) {
    return (
        <Modal show={show} onHide={onHide} centered>
            <Modal.Header>
                <Modal.Title><h4>공동 구매 등록</h4></Modal.Title>
                <Button variant="" className={styles.detailButton} onClick={onHide}>
                    취소
                </Button>
            </Modal.Header>

            <Modal.Body className="border-bottom">
                <div className="mb-1 fw-semibold">프리미엄 블루투스 이어폰 100</div>
                <div className="fw-bold fs-5 mt-2">
                    해당 상품의 공동 구매를 진행합니다.
                </div>
            </Modal.Body>

            <Modal.Footer className="justify-content-center border-0">
                <Button variant="" className={styles.detailButton} onClick={onConfirm}>
                    공동 구매 등록
                </Button>
            </Modal.Footer>
        </Modal>
    );
}

export default GroupBuyRegisterModal;
