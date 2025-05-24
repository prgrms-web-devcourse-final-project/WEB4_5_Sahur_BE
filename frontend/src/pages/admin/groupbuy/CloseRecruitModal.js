import {Modal, Button, Stack, Image, Form, Badge} from 'react-bootstrap';
import styles from "./AdminGroupBuy.module.scss"

function CloseRecruitModal({ show, onHide, onConfirm }) {
    return (
        <Modal show={show} onHide={onHide} centered>
            <Modal.Header>
                <Modal.Title><h4>모집 종료</h4></Modal.Title>
                <Button variant="" className={styles.detailButton} onClick={onHide}>
                    취소
                </Button>
            </Modal.Header>

            <Modal.Body className="border-bottom">
                <Stack direction={"horizontal"} gap={2} >
                    <Image width={50} height={50} src={"https://i.pravatar.cc/150?img=49.jpg"} />
                    <Stack gap={1} >
                        <Stack direction={"horizontal"} className={"justify-content-between"}>
                            <span className="fw-semibold">프리미엄 블루투스 이어폰 100</span>
                            <desc className={"fw-light text-muted"}>마감일: 2025.2.14</desc>
                        </Stack>
                        <div>
                            <span className={"fw-semibold me-2"}>35000원</span>
                            <desc className={"fw-light text-muted"}>1개</desc>
                        </div>
                        <Stack direction={"horizontal"} className={"justify-content-between"}>
                            <span><Badge>1회차</Badge></span>
                            <Button variant="" className={styles.detailButton}>상세보기</Button>
                        </Stack>
                    </Stack>
                </Stack>
            </Modal.Body>
            <Modal.Body className="border-bottom">
                <Form>
                    <Form.Control
                        as="textarea"
                        rows={4}
                        placeholder="공동 구매 종료 - 관리자 메모"
                        className={`h-25`}
                    />
                </Form>
            </Modal.Body>

            <Modal.Footer className="justify-content-center border-0">
                <Button variant="" className={styles.detailButton} onClick={onConfirm}>
                    공동 구매 삭제하기
                </Button>
            </Modal.Footer>
        </Modal>
    );
}

export default CloseRecruitModal;
