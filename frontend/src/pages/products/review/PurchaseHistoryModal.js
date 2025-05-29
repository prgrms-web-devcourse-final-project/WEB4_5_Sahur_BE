"use client"
import { Modal, Button, Card, Stack, Badge } from "react-bootstrap"
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome"
import { faCalendarAlt, faShoppingCart } from "@fortawesome/free-solid-svg-icons"

const PurchaseHistoryModal = ({ show, onHide, purchaseHistories, onSelectHistory, isLoading }) => {
  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString("ko-KR", {
      year: "numeric",
      month: "long",
      day: "numeric",
    })
  }

  const formatPrice = (price) => {
    return new Intl.NumberFormat("ko-KR").format(price)
  }

  return (
    <Modal show={show} onHide={onHide} size="lg" centered>
      <Modal.Header closeButton>
        <Modal.Title>리뷰 작성할 구매내역 선택</Modal.Title>
      </Modal.Header>
      <Modal.Body style={{ maxHeight: "400px", overflowY: "auto" }}>
        {isLoading ? (
          <div className="text-center py-4">
            <div className="spinner-border" role="status">
              <span className="visually-hidden">Loading...</span>
            </div>
            <p className="mt-2">구매내역을 불러오는 중...</p>
          </div>
        ) : purchaseHistories && purchaseHistories.length > 0 ? (
          <Stack gap={3}>
            {purchaseHistories.map((history, index) => (
              <Card key={history.historyId || index} className="border">
                <Card.Body>
                  <Stack direction="horizontal" className="justify-content-between align-items-center">
                    <div className="flex-grow-1">
                      <Stack direction="horizontal" gap={2} className="mb-2 flex-wrap">
                        <FontAwesomeIcon icon={faShoppingCart} className="text-primary" />
                        <span className="fw-bold">주문번호: {history.order?.orderId || history.historyId}</span>
                        {history.writable && <Badge bg="success">리뷰 작성 가능</Badge>}
                      </Stack>
                      <Stack direction="horizontal" gap={3} className="text-muted small flex-wrap">
                        <span>
                          <FontAwesomeIcon icon={faCalendarAlt} className="me-1" />
                          구매일: {formatDate(history.order?.createdAt)}
                        </span>
                        <span>수량: {history.order?.quantity || 1}개</span>
                        <span>금액: {formatPrice(history.order?.totalPrice)}원</span>
                      </Stack>
                      {history.groupBuyId && (
                        <div className="text-muted small mt-1">공동구매 ID: {history.groupBuyId}</div>
                      )}
                    </div>
                    <Button
                      variant="primary"
                      size="sm"
                      onClick={() => onSelectHistory(history)}
                      disabled={!history.writable}
                      className="ms-3"
                    >
                      {history.writable ? "리뷰 작성" : "작성 불가"}
                    </Button>
                  </Stack>
                </Card.Body>
              </Card>
            ))}
          </Stack>
        ) : (
          <div className="text-center py-4">
            <p className="text-muted">리뷰 작성 가능한 구매내역이 없습니다.</p>
            <small className="text-muted">상품을 구매하고 배송이 완료된 후 리뷰를 작성할 수 있습니다.</small>
          </div>
        )}
      </Modal.Body>
      <Modal.Footer>
        <Button variant="secondary" onClick={onHide}>
          취소
        </Button>
      </Modal.Footer>
    </Modal>
  )
}

export default PurchaseHistoryModal
