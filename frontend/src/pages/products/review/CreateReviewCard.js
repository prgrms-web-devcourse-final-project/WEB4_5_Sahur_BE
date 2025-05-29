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

// 방법 1: FormData 방식 (멀티파트)
const createReviewWithFormData = async (reviewData) => {
  console.log("FormData 방식으로 리뷰 데이터 전송:", reviewData)

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

// 방법 2: Base64 방식 (JSON)
const createReviewWithBase64 = async (reviewData) => {
  console.log("Base64 방식으로 리뷰 데이터 전송:", reviewData)

  // 이미지 파일을 Base64로 변환
  const imagePromises = reviewData.images
    ? reviewData.images.map((file) => {
        return new Promise((resolve, reject) => {
          const reader = new FileReader()
          reader.readAsDataURL(file)
          reader.onload = () => {
            // Base64 문자열에서 "data:image/jpeg;base64," 부분 제거
            const base64String = reader.result.split(",")[1]
            resolve(base64String)
          }
          reader.onerror = (error) => reject(error)
        })
      })
    : []

  // 모든 이미지 변환이 완료될 때까지 대기
  const imageBase64List = reviewData.images.length > 0 ? await Promise.all(imagePromises) : []

  // API 요청 본문 구성
  const requestBody = {
    historyId: reviewData.historyId,
    comment: reviewData.comment,
    rate: reviewData.rate,
    imageUrl: imageBase64List,
  }

  console.log("API 요청 본문:", requestBody)

  const response = await axios.post("/api/v1/reviews", requestBody, {
    headers: {
      "Content-Type": "application/json",
    },
    withCredentials: true,
  })

  return response.data
}

// 방법 3: 이미지 URL 방식 (JSON)
const createReviewWithImageUrls = async (reviewData) => {
  console.log("이미지 URL 방식으로 리뷰 데이터 전송:", reviewData)

  // 이미지가 있으면 URL로 변환 (임시로 빈 배열)
  const imageUrls = reviewData.images ? reviewData.images.map((file) => URL.createObjectURL(file)) : []

  const requestBody = {
    historyId: reviewData.historyId,
    comment: reviewData.comment,
    rate: reviewData.rate,
    imageUrl: imageUrls,
  }

  console.log("API 요청 본문:", requestBody)

  const response = await axios.post("/api/v1/reviews", requestBody, {
    headers: {
      "Content-Type": "application/json",
    },
    withCredentials: true,
  })

  return response.data
}

const CreateReviewCard = ({ handleClose, selectedHistory, onReviewCreated }) => {
  const [rating, setRating] = useState(5)
  const [reviewImageFileList, setReviewFileImageList] = useState([])
  const [reviewText, setReviewText] = useState("")
  const [uploadMethod, setUploadMethod] = useState("formdata") // 업로드 방식 선택

  const { mutate: createReviewMutate, isLoading } = useApiMutation(
    (reviewData) => {
      switch (uploadMethod) {
        case "formdata":
          return createReviewWithFormData(reviewData)
        case "base64":
          return createReviewWithBase64(reviewData)
        case "imageurl":
          return createReviewWithImageUrls(reviewData)
        default:
          return createReviewWithFormData(reviewData)
      }
    },
    {
      onSuccess: (data) => {
        console.log("리뷰 작성 성공:", data)
        alert("리뷰가 성공적으로 등록되었습니다!")
        if (onReviewCreated) {
          onReviewCreated()
        }
        handleClose()
      },
      onError: (error) => {
        console.error("리뷰 작성 실패:", error)
        console.error("오류 상세:", error.response?.data)

        // 다른 방식으로 재시도
        if (uploadMethod === "formdata") {
          console.log("FormData 실패, Base64 방식으로 재시도...")
          setUploadMethod("base64")
          setTimeout(() => {
            handleConfirmClick()
          }, 100)
        } else if (uploadMethod === "base64") {
          console.log("Base64 실패, 이미지 없이 재시도...")
          setUploadMethod("imageurl")
          setTimeout(() => {
            handleConfirmClick()
          }, 100)
        } else {
          alert(`리뷰 작성에 실패했습니다: ${error.response?.data?.message || error.message}`)
        }
      },
    },
  )

  const handleConfirmClick = () => {
    if (!selectedHistory) {
      console.error("선택된 구매내역이 없습니다.")
      alert("구매내역을 선택해주세요.")
      return
    }

    if (!reviewText.trim()) {
      alert("리뷰 내용을 입력해주세요.")
      return
    }

    console.log("리뷰 작성 시작:", {
      historyId: selectedHistory.historyId,
      comment: reviewText.trim(),
      rate: rating,
      imageCount: reviewImageFileList.length,
      method: uploadMethod,
    })

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

          {/* 디버깅용 업로드 방식 표시 */}
          <div className="text-center">
            <small className="text-muted">
              업로드 방식: {uploadMethod} | 이미지 개수: {reviewImageFileList.length}
            </small>
          </div>
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
