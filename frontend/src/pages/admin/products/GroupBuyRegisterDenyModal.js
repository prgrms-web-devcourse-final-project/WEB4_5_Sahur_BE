import {Modal, Button, Stack, Image, Form} from 'react-bootstrap';
import styles from "./AdminProducts.module.scss"

function GroupBuyRegisterDenyModal({ show, onHide, onConfirm }) {
    return (
        <Modal show={show} onHide={onHide} centered>
            <Modal.Header>
                <Modal.Title><h4>상품 등록 요청 거부</h4></Modal.Title>
                <Button variant="" className={styles.detailButton} onClick={onHide}>
                    취소
                </Button>
            </Modal.Header>

            <Modal.Body className="border-bottom">
                <Stack direction={"horizontal"} gap={2} >
                    <Image width={50} height={50} src={"https://i.pravatar.cc/150?img=49.jpg"} />
                    <Stack gap={1} >
                        <div className="fw-semibold">프리미엄 블루투스 이어폰 100</div>
                        <div>
                            <span className={"fw-semibold me-2"}>디지털 가전</span>
                            <desc className={"fw-light text-muted"}>이어폰</desc>
                        </div>
                    </Stack>
                </Stack>
            </Modal.Body>
            <Modal.Body className="border-bottom">
                <Form>
                    <Form.Control
                        as="textarea"
                        rows={4}
                        placeholder="상품 등록 요청 거부 사유"
                        className={`h-25`}
                    />
                </Form>
            </Modal.Body>

            <Modal.Footer className="justify-content-center border-0">
                <Button variant="" className={styles.detailButton} onClick={onConfirm}>
                    요청 거부
                </Button>
            </Modal.Footer>
        </Modal>
    );
}

export default GroupBuyRegisterDenyModal;
