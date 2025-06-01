import { Modal, Button, Stack, Image, Form, Badge } from "react-bootstrap";
import styles from "./AdminGroupBuy.module.scss";
import { useState } from "react";
import axios from "axios";

function CloseRecruitModal({ show, onHide, onConfirm, productInfo }) {
  const [adminMemo, setAdminMemo] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleCloseGroupBuy = async () => {
    if (isSubmitting) return;

    try {
      setIsSubmitting(true);

      // 디버깅을 위한 로그 추가
      console.log("productInfo:", productInfo);
      console.log("groupBuyId:", productInfo?.groupBuyId);

      // groupBuyId 검증
      if (!productInfo?.groupBuyId) {
        alert(
          "공동구매 ID가 없습니다. 페이지를 새로고침 후 다시 시도해주세요."
        );
        return;
      }

      // 1. 공동구매 종료 API 호출 (withCredentials: true 추가)
      console.log("1단계: 공동구매 종료 API 호출");
      await axios.patch(
        `/api/v1/groupBuy/${productInfo.groupBuyId}/close`,
        {},
        {
          withCredentials: true,
        }
      );
      console.log("1단계 완료: 공동구매 종료 성공");

      // 2. 알림 발송 API 호출 (관리자 메모를 메시지로 전송)
      console.log("2단계: 알림 발송 API 호출");
      const notificationMessage =
        adminMemo.trim() || "공동구매가 관리자에 의해 종료되었습니다.";
      await axios.post(
        `/api/v1/notifications/groupBuy/close/${productInfo.groupBuyId}`,
        notificationMessage,
        {
          headers: {
            "Content-Type": "text/plain",
          },
        }
      );
      console.log("2단계 완료: 알림 발송 성공");

      alert("공동 구매가 종료되었습니다.");
      onConfirm();
    } catch (error) {
      console.error("공동 구매 종료 실패:", error);
      console.error("Error response:", error.response?.data);

      // 어느 단계에서 실패했는지 구분하여 에러 메시지 표시
      let errorMessage = "공동 구매 종료에 실패했습니다";

      if (
        error.config?.url?.includes("/close") &&
        !error.config?.url?.includes("/notifications/")
      ) {
        errorMessage = "공동 구매 종료에 실패했습니다";
      } else if (error.config?.url?.includes("/notifications/")) {
        errorMessage = "공동 구매는 종료되었지만 알림 발송에 실패했습니다";
      }

      alert(
        `${errorMessage}: ${error.response?.data?.message || error.message}`
      );
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <Modal show={show} onHide={onHide} centered>
      <Modal.Header>
        <Modal.Title>
          <h4>모집 종료</h4>
        </Modal.Title>
        <Button variant="" className={styles.detailButton} onClick={onHide}>
          취소
        </Button>
      </Modal.Header>

      <Modal.Body className="border-bottom">
        <Stack direction={"horizontal"} gap={2}>
          <Image
            width={50}
            height={50}
            src={
              productInfo?.imageUrl || "https://i.pravatar.cc/150?img=49.jpg"
            }
            style={{ objectFit: "cover", borderRadius: "4px" }}
          />
          <Stack gap={1}>
            <Stack
              direction={"horizontal"}
              className={"justify-content-between"}
            >
              <span className="fw-semibold">
                {productInfo?.title || "프리미엄 블루투스 이어폰 100"}
              </span>
              <desc className={"fw-light text-muted"}>
                마감일: {productInfo?.deadline || "2025.2.14"}
              </desc>
            </Stack>
            <div>
              <span className={"fw-semibold me-2"}>
                {productInfo?.price
                  ? `${productInfo.price.toLocaleString()}원`
                  : "35,000원"}
              </span>
              <desc className={"fw-light text-muted"}>1개</desc>
            </div>
            <Stack
              direction={"horizontal"}
              className={"justify-content-between"}
            >
              <span>
                <Badge>{productInfo?.round || "1"}회차</Badge>
              </span>
              <Button
                variant=""
                className={styles.detailButton}
                onClick={() =>
                  window.open(`/groupbuy/${productInfo?.groupBuyId}`, "_blank")
                }
              >
                상세보기
              </Button>
            </Stack>
          </Stack>
        </Stack>
      </Modal.Body>

      <Modal.Body className="border-bottom">
        <Form>
          <Form.Control
            as="textarea"
            rows={4}
            placeholder="공동 구매 종료 사유를 입력하세요 (참가자들에게 알림으로 전송됩니다)"
            className={`h-25`}
            value={adminMemo}
            onChange={(e) => setAdminMemo(e.target.value)}
          />
        </Form>
      </Modal.Body>

      <Modal.Footer className="justify-content-center border-0">
        <Button
          variant=""
          className={styles.detailButton}
          onClick={handleCloseGroupBuy}
          disabled={isSubmitting}
        >
          {isSubmitting ? "처리 중..." : "공동 구매 삭제하기"}
        </Button>
      </Modal.Footer>
    </Modal>
  );
}

export default CloseRecruitModal;
