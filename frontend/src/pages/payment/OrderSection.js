"use client"

import { Button, Col, Image, Row, Stack } from "react-bootstrap"
import styles from "./Payment.module.scss"
import { loadTossPayments } from "@tosspayments/payment-sdk"
import DeadlineTimer from "../main/DeadlineTimer"
import axios from "axios"
import { useApiMutation } from "../../hooks/useApiMutation"
import { useRecoilValue } from "recoil"
import { userAtom } from "../../state/atoms"
import { useState } from "react"
import { useToast } from "../../hooks/useToast"

const createOrder = (params) => axios.post(`/api/v1/orders`, params)
const createDelivery = (params) => axios.post(`/api/v1/deliveries/order/${params.orderId}`, params.sendData)

const OrderSection = ({ groupBuyInfo, count, deliveryInfo }) => {
  const loginUser = useRecoilValue(userAtom)
  const currentParticipantCount = groupBuyInfo?.currentParticipantCount ?? 0
  const targetParticipants = groupBuyInfo?.targetParticipants ?? 0
  const [isProcessing, setIsProcessing] = useState(false)
  const showToast = useToast()

  const { mutateAsync: createOrderMutate } = useApiMutation(createOrder)

  const { mutateAsync: createDeliveryMutate } = useApiMutation(createDelivery, {
    onSuccess: (data) => {
      console.log("배송 정보 생성 성공:", data)
    },
  })

  const handlePaymentClick = async () => {
    if (isProcessing) return

    try {
      setIsProcessing(true)

      // 필수 데이터 검증
      if (!groupBuyInfo?.groupBuyId || !groupBuyInfo?.product?.productId) {
        showToast("상품 정보가 유효하지 않습니다.", "error")
        return
      }

      if (!count || count <= 0) {
        showToast("수량을 확인해주세요.", "error")
        return
      }

      if (!deliveryInfo || !deliveryInfo.ready) {
        showToast("배송 정보를 모두 입력해주세요.", "error")
        return
      }

      // 스웨거와 동일한 형식으로 요청 (memberId 제거)
      const orderData = {
        groupBuyId: groupBuyInfo.groupBuyId,
        productId: groupBuyInfo.product.productId,
        quantity: count,
      }

      console.log("주문 생성 요청:", orderData)

      // 1. 주문 생성
      const orderResponse = await createOrderMutate(orderData)

      console.log("주문 생성 응답:", orderResponse)

      if (!orderResponse?.data?.data?.orderId) {
        showToast("주문 생성에 실패했습니다.", "error")
        return
      }

      const orderId = orderResponse.data.data.orderId

      // 2. 배송 정보 생성 - 원본 deliveryInfo 로깅 추가
      console.log("원본 deliveryInfo:", deliveryInfo)

      // pccc 값 안전하게 처리
      let pcccValue = 0
      if (deliveryInfo.pccc !== undefined && deliveryInfo.pccc !== null && deliveryInfo.pccc !== "") {
        // 문자열인 경우 숫자로 변환 시도
        if (typeof deliveryInfo.pccc === "string") {
          const parsed = Number.parseInt(deliveryInfo.pccc, 10)
          pcccValue = isNaN(parsed) ? 0 : parsed
        } else if (typeof deliveryInfo.pccc === "number") {
          pcccValue = deliveryInfo.pccc
        }
      }

      const deliveryData = {
        zipCode: deliveryInfo.zipCode || "",
        streetAdr: deliveryInfo.streetAdr || "",
        detailAdr: deliveryInfo.detailAdr || "",
        pccc: pcccValue,
        contact: deliveryInfo.phone || "",
      }

      console.log("변환된 배송 데이터:", deliveryData)
      console.log("각 필드 타입 확인:", {
        zipCode: typeof deliveryData.zipCode,
        streetAdr: typeof deliveryData.streetAdr,
        detailAdr: typeof deliveryData.detailAdr,
        pccc: typeof deliveryData.pccc,
        contact: typeof deliveryData.contact,
      })

      const deliveryResponse = await createDeliveryMutate({
        orderId,
        sendData: deliveryData,
      })

      console.log("배송 정보 생성 응답:", deliveryResponse)

      // 3. 토스페이먼츠 결제 요청
      const client_id = "test_ck_5OWRapdA8dP5dB4NPlyB8o1zEqZK"
      const random = new Date().getTime() + Math.random()
      const randomId = btoa(random)

      // 상품 가격 계산
      const totalAmount = groupBuyInfo.product.price * count

      loadTossPayments(client_id).then((tossPayments) => {
        tossPayments.requestPayment("카드", {
          amount: totalAmount,
          orderId: `${randomId}`,
          orderName: `${groupBuyInfo.product.title} ${count > 1 ? `외 ${count - 1}건` : ""}`,
          customerName: loginUser.nickname || "고객",
          successUrl: `${window.location.origin}/payment/success`,
          failUrl: `${window.location.origin}/payment/fail`,
        })
      })
    } catch (error) {
      console.error("결제 처리 중 오류 발생:", error)

      // 에러 응답 상세 로깅
      if (error.response) {
        console.error("에러 응답 데이터:", error.response.data)
        console.error("에러 응답 상태:", error.response.status)
        console.error("에러 응답 헤더:", error.response.headers)

        // 서버에서 에러 메시지를 제공하는 경우
        const errorMessage = error.response.data?.message || "결제 처리 중 오류가 발생했습니다."
        showToast(errorMessage, "error")
      } else if (error.request) {
        // 요청은 보냈지만 응답을 받지 못한 경우
        console.error("응답을 받지 못했습니다:", error.request)
        showToast("서버 응답이 없습니다. 네트워크 연결을 확인해주세요.", "error")
      } else {
        // 요청 설정 중 오류 발생
        console.error("요청 설정 중 오류:", error.message)
        showToast("결제 요청 중 오류가 발생했습니다.", "error")
      }
    } finally {
      setIsProcessing(false)
    }
  }

  return (
    <>
      <h3>주문 정보</h3>
      <Row className={`p-3 ${styles.sectionDivider}`}>
        <Col xs={3}>
          <Image src={groupBuyInfo?.product.imageUrl[0] || "/placeholder.svg"} fluid />
        </Col>
        <Col xs={9}>
          <Stack direction={"vertical"}>
            <h5>{groupBuyInfo?.product.title}</h5>
            <desc>{count}개</desc>
          </Stack>
        </Col>
      </Row>
      <Row className={`p-3 ${styles.sectionDivider} text-gray-300`}>
        <Col xs={12} className={"d-flex justify-content-between"}>
          <span>상품금액</span>
          <span>{groupBuyInfo?.product.price.toLocaleString()}원</span>
        </Col>
        <Col xs={12} className={"d-flex justify-content-between"}>
          <span>배송비</span>
          <span>무료</span>
        </Col>
      </Row>
      <Row className={`p-3`}>
        <Col xs={12} className={styles.orderBottom}>
          <div className={"d-flex justify-content-between"}>
            <h4>총 결제금액</h4>
            <h4>{(groupBuyInfo?.product.price * (Number(count) || 1)).toLocaleString()}원</h4>
          </div>
          <div className={styles.blueCard}>
            <div style={{ color: "#1E40AF", fontSize: "18px" }}>공동구매 정보</div>
            <div className={"d-flex justify-content-between"}>
              <span>현재 참여 인원</span>
              <span>{`${currentParticipantCount}/${targetParticipants}명`}</span>
            </div>
            <div className={"d-flex justify-content-between"}>
              <span>최소 인원</span>
              <span>30명</span>
            </div>
            <div className={"d-flex justify-content-between"}>
              <span>마감 시간</span>
              <span>
                <DeadlineTimer deadline={groupBuyInfo?.deadline} />
              </span>
            </div>
          </div>
          <div style={{ marginTop: "10px" }}>
            <Button className={"w-100"} disabled={!deliveryInfo.ready || isProcessing} onClick={handlePaymentClick}>
              {isProcessing ? "처리 중..." : "결제하기"}
            </Button>
          </div>
          <div style={{ marginLeft: "auto", marginRight: "auto" }}>
            <desc className={"text-gray-300"}>
              공동 구매는 즉시 결제 후, <br />
              인원 미달 시 전액 환불됩니다.
            </desc>
          </div>
        </Col>
      </Row>
    </>
  )
}

export default OrderSection
