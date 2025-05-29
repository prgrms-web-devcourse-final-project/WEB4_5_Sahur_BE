"use client"

import { Button, Card, Stack } from "react-bootstrap"
import styles from "../GroupBuy.module.scss"
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome"
import { faStar as faStarSolid, faXmark } from "@fortawesome/free-solid-svg-icons"
import { faStar as faStarRegular } from "@fortawesome/free-regular-svg-icons"
import Rating from "react-rating"
import { useState } from "react"
import CreateReviewImageBox from "./CreateReviewImageBox"
import ReviewTextarea from "./ReviewTextarea"
import axios from "axios"
import { useApiMutation } from "../../../hooks/useApiMutation"

const createReview = async (reviewData) => {
  const formData = new FormData()

  // 리뷰 데이터를 JSON으로 추가
  const reviewInfo = {
    historyId: reviewData.historyId,
    comment: reviewData.comment,
    rate: reviewData.rate,
  }

  formData.append(
    "review",
    new Blob([JSON.stringify(reviewInfo)], {
      type: "application/json",
    }),
  )

  // 이미지 파일들 추가
  if (reviewData.images && reviewData.images.length > 0) {
    reviewData.images.forEach((image, index) => {
      formData.append("images", image)
    })
  }

  const response = await axios.post("/api/v1/reviews", formData, {
    headers: {
      "Content-Type": "multipart/form-data",
    },
    withCredentials: true,
  })

  return response.data
}

const CreateReviewCard = ({ handleClose, selectedHistory, onReviewCreated }) => {
  const [rating, setRating] = useState(5)
  const [reviewImageFileList, setReviewFileImageList] = useState([])
  const [reviewText, setReviewText] = useState("")

  const { mutate: createReviewMutate, isLoading } = useApiMutation(createReview, {
    onSuccess: (data) => {
      console.log("리뷰 작성 성공:", data)
      if (onReviewCreated) {
        onReviewCreated()
      }
      handleClose()
    },
    onError: (error) => {
      console.error("리뷰 작성 실패:", error)
    },
  })

  const handleConfirmClick = () => {
    if (!selectedHistory) {
      console.error("선택된 구매내역이 없습니다.")
      return
    }

    if (!reviewText.trim()) {
      alert("리뷰 내용을 입력해주세요.")
      return
    }

    const reviewData = {
      historyId: selectedHistory.historyId,
      comment: reviewText.trim(),
      rate: rating,
      images: reviewImageFileList,
    }

    createReviewMutate(reviewData)
  }

  return (
    <Card className={styles.reviewCard}>
      <Card.Header>
        <Stack direction={"horizontal"} className={"justify-content-between align-items-center"}>
          <div></div>
          <h4>리뷰 작성</h4>
          <span className={"cursor-pointer"} onClick={handleClose}>
            <FontAwesomeIcon icon={faXmark}></FontAwesomeIcon>
          </span>
        </Stack>
      </Card.Header>
      <Card.Body>
        <Stack direction={"vertical"} gap={3} style={{ overflow: "auto", maxHeight: "280px" }}>
          {selectedHistory && (
            <div className="bg-light p-2 rounded">
              <small className="text-muted">선택된 구매내역</small>
              <div>주문번호: {selectedHistory.historyId}</div>
              <div className="text-muted small">
                구매일: {new Date(selectedHistory.createdAt || selectedHistory.orderDate).toLocaleDateString("ko-KR")}
              </div>
            </div>
          )}
          <div className={"d-flex justify-content-center flex-column text-center p-3"}>
            <span>상품은 어떠셨나요?</span>
            <span>상품에 대한 별점을 남겨주세요.</span>
          </div>
          <div className={"d-flex justify-content-center"}>
            <Rating
              initialRating={rating}
              onChange={setRating}
              fullSymbol={<FontAwesomeIcon icon={faStarSolid} color="#facc15" size="lg" />}
              emptySymbol={<FontAwesomeIcon icon={faStarRegular} color="#facc15" size="lg" />}
            />
          </div>
          <CreateReviewImageBox imageFileList={reviewImageFileList} setImageFileList={setReviewFileImageList} />
          <ReviewTextarea value={reviewText} onChange={(e) => setReviewText(e.target.value)} />
        </Stack>
      </Card.Body>
      <Card.Footer className={"m-3"}>
        <Stack direction={"horizontal"} gap={3} className={"d-flex justify-content-center"}>
          <Button variant={""} style={{ minWidth: "30px" }} className={styles.reviewButton} onClick={handleClose}>
            취소
          </Button>
          <Button style={{ minWidth: "30px" }} onClick={handleConfirmClick} disabled={isLoading}>
            {isLoading ? "작성 중..." : "완료"}
          </Button>
        </Stack>
      </Card.Footer>
    </Card>
  )
}

export default CreateReviewCard
