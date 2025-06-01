import { Modal, Button, Stack, Image, Form } from "react-bootstrap";
import { useState } from "react";
import styles from "./AdminProducts.module.scss";
import axios from "axios";

function GroupBuyRegisterDenyModal({
  show,
  onHide,
  onConfirm,
  requestData,
  requestId,
}) {
  const [rejectMessage, setRejectMessage] = useState("");
  const [loading, setLoading] = useState(false);

  const handleReject = async () => {
    try {
      setLoading(true);

      // API 호출 - PATCH /api/v1/productRequests/{productRequestId}/reject
      await axios.patch(
        `/api/v1/productRequests/${requestId}/reject`,
        rejectMessage,
        {
          headers: {
            "Content-Type": "text/plain",
          },
        }
      );

      alert("상품 요청이 거부되었습니다.");
      setRejectMessage(""); // 메시지 초기화
      onConfirm(); // 부모 컴포넌트에 성공 알림
    } catch (error) {
      console.error("거부 처리에 실패했습니다:", error);

      // 에러 메시지 개선
      if (error.response?.data?.message) {
        alert(`거부 처리에 실패했습니다: ${error.response.data.message}`);
      } else if (error.response?.data?.msg) {
        alert(`거부 처리에 실패했습니다: ${error.response.data.msg}`);
      } else {
        alert("거부 처리에 실패했습니다.");
      }
    } finally {
      setLoading(false);
    }
  };

  const handleClose = () => {
    setRejectMessage(""); // 모달 닫을 때 메시지 초기화
    onHide();
  };

  return (
    <Modal show={show} onHide={handleClose} centered>
      <Modal.Header>
        <Modal.Title>
          <h4>상품 등록 요청 거부</h4>
        </Modal.Title>
        <Button
          variant=""
          className={styles.detailButton}
          onClick={handleClose}
        >
          취소
        </Button>
      </Modal.Header>

      <Modal.Body className="border-bottom">
        <Stack direction={"horizontal"} gap={2}>
          <Image
            width={50}
            height={50}
            src={
              requestData?.imageUrls && requestData.imageUrls.length > 0
                ? requestData.imageUrls[0]
                : "/placeholder.svg?height=50&width=50"
            }
            style={{ objectFit: "cover", borderRadius: "4px" }}
          />
          <Stack gap={1}>
            <div className="fw-semibold">
              {requestData?.title || "프리미엄 블루투스 이어폰 100"}
            </div>
            <div>
              <span className={"fw-semibold me-2"}>
                {requestData?.category?.categoryType === "FASHION_CLOTHES" &&
                  "패션 의류"}
                {requestData?.category?.categoryType === "FASHION_ACCESSORY" &&
                  "패션 액세서리"}
                {requestData?.category?.categoryType === "BEAUTY" && "뷰티"}
                {requestData?.category?.categoryType === "DIGITAL_APPLIANCE" &&
                  "디지털/가전"}
                {requestData?.category?.categoryType === "FURNITURE" && "가구"}
                {requestData?.category?.categoryType === "LIVING" && "리빙"}
                {requestData?.category?.categoryType === "FOOD" && "식품"}
                {requestData?.category?.categoryType === "SPORTS" && "스포츠"}
                {requestData?.category?.categoryType === "CAR" && "자동차"}
                {requestData?.category?.categoryType === "BOOK" && "도서"}
                {requestData?.category?.categoryType === "KIDS" && "키즈"}
                {requestData?.category?.categoryType === "PET" && "반려동물"}
                {!requestData?.category?.categoryType && "카테고리 없음"}
              </span>
              <desc className={"fw-light text-muted"}>
                {requestData?.category?.keyword || "세부카테고리 없음"}
              </desc>
            </div>
          </Stack>
        </Stack>
      </Modal.Body>

      <Modal.Body className="border-bottom">
        <Form>
          <Form.Label className="fw-semibold mb-2">거부 사유</Form.Label>
          <Form.Control
            as="textarea"
            rows={4}
            placeholder="상품 등록 요청 거부 사유를 입력해주세요."
            className={`h-25`}
            value={rejectMessage}
            onChange={(e) => setRejectMessage(e.target.value)}
          />
          <small className="text-muted mt-1">
            거부 사유는 요청자에게 전달됩니다.
          </small>
        </Form>
      </Modal.Body>

      <Modal.Footer className="justify-content-center border-0">
        <Button
          variant=""
          className={styles.detailButton}
          onClick={handleReject}
          disabled={loading || !rejectMessage.trim()}
        >
          {loading ? "처리 중..." : "요청 거부"}
        </Button>
      </Modal.Footer>
    </Modal>
  );
}

export default GroupBuyRegisterDenyModal;
