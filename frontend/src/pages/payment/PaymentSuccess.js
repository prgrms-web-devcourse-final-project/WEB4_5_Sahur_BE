"use client"

import { useSearchParams, useNavigate } from "react-router-dom"
import { useEffect, useState } from "react"
import { Container, Row, Col, Card, Button, Stack, Badge } from "react-bootstrap"

const PaymentSuccess = () => {
  const [searchParams] = useSearchParams()
  const navigate = useNavigate()
  const [paymentInfo, setPaymentInfo] = useState(null)

  useEffect(() => {
    // 이미 결제 정보가 설정되어 있으면 다시 설정하지 않음
    if (paymentInfo) return

    // URL에서 결제 정보 파라미터 가져오기
    const paymentKey = searchParams.get("paymentKey")
    const orderId = searchParams.get("orderId")
    const amount = searchParams.get("amount")

    console.log("모든 URL 파라미터:", Object.fromEntries(searchParams.entries()))
    console.log("결제 성공 - paymentKey:", paymentKey, "orderId:", orderId, "amount:", amount)

    // 결제 정보 설정 (한 번만 설정됨)
    if (orderId || amount) {
      setPaymentInfo({
        orderId: orderId || "주문ID 없음",
        amount: amount ? Number(amount).toLocaleString() : "금액 정보 없음",
        paymentDate: new Date().toLocaleString("ko-KR"),
        paymentMethod: "카드결제",
      })
    }

    // 여기에 백엔드 API 호출을 추가할 수 있습니다
    // 결제 검증 API 호출 등
  }, [searchParams, paymentInfo])

  const handleGoToMain = () => {
    navigate("/main")
  }

  const handleGoToMypage = () => {
    navigate("/mypage")
  }

  if (!paymentInfo) {
    return (
      <Container className="d-flex justify-content-center align-items-center" style={{ minHeight: "100vh" }}>
        <div className="text-center">
          <div className="spinner-border text-primary mb-3" role="status">
            <span className="visually-hidden">Loading...</span>
          </div>
          <p>결제 완료 처리 중입니다...</p>
        </div>
      </Container>
    )
  }

  return (
    <Container className="py-5" style={{ minHeight: "100vh", backgroundColor: "#f8f9fa" }}>
      <Row className="justify-content-center">
        <Col md={8} lg={6}>
          {/* 결제 완료 헤더 */}
          <div className="text-center mb-4">
            <div
              className="d-inline-flex align-items-center justify-content-center bg-success text-white rounded-circle mb-3"
              style={{ width: "80px", height: "80px", fontSize: "40px" }}
            >
              ✓
            </div>
            <h2 className="text-success fw-bold">결제가 완료되었습니다!</h2>
            <p className="text-muted">공동구매 참여가 성공적으로 완료되었습니다.</p>
          </div>

          {/* 결제 정보 카드 */}
          <Card className="shadow-sm mb-4">
            <Card.Header className="bg-primary">
              <h5 className="mb-0 text-white">📄 결제 정보</h5>
            </Card.Header>
            <Card.Body>
              <Stack gap={3}>
                <div className="d-flex justify-content-between align-items-center">
                  <span className="text-muted">결제ID</span>
                  <span className="fw-bold text-dark" style={{ fontSize: "14px", fontFamily: "monospace" }}>
                    {paymentInfo.orderId}
                  </span>
                </div>
                <div className="d-flex justify-content-between align-items-center">
                  <span className="text-muted">결제금액</span>
                  <span className="fw-bold text-primary fs-5">{paymentInfo.amount}원</span>
                </div>
                <div className="d-flex justify-content-between align-items-center">
                  <span className="text-muted">결제방법</span>
                  <Badge bg="secondary">{paymentInfo.paymentMethod}</Badge>
                </div>
                <div className="d-flex justify-content-between align-items-center">
                  <span className="text-muted">결제일시</span>
                  <span>{paymentInfo.paymentDate}</span>
                </div>
              </Stack>
            </Card.Body>
          </Card>

          {/* 안내 메시지 */}
          <Card className="border-info mb-4">
            <Card.Body className="text-center">
              <h6 className="text-info mb-3">ℹ️ 공동구매 안내</h6>
              <p className="mb-2 text-muted">• 공동구매 목표 인원이 달성되면 상품이 발송됩니다.</p>
              <p className="mb-2 text-muted">• 목표 인원 미달 시 자동으로 전액 환불됩니다.</p>
              <p className="mb-0 text-muted">• 진행 상황은 마이페이지에서 확인하실 수 있습니다.</p>
            </Card.Body>
          </Card>

          {/* 액션 버튼 */}
          <div className="d-grid gap-2">
            <Button variant="primary" size="lg" onClick={handleGoToMypage} className="mb-2">
              👤 마이페이지에서 주문 확인하기
            </Button>
            <Button
              variant="secondary"
              size="lg"
              onClick={handleGoToMain}
              style={{
                backgroundColor: "#6c757d",
                borderColor: "#6c757d",
                color: "white",
              }}
            >
              🏠 메인으로 돌아가기
            </Button>
          </div>
        </Col>
      </Row>
    </Container>
  )
}

export default PaymentSuccess
