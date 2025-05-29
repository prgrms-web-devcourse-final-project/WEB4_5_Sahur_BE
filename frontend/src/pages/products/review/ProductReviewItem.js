"use client"

import { Image, Stack, Dropdown } from "react-bootstrap"
import sampleImg from "../../../assets/images/sample.png"
import Rating from "react-rating"
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome"
import { faStar as faStarSolid, faEllipsisV } from "@fortawesome/free-solid-svg-icons"
import { faStar as faStarRegular } from "@fortawesome/free-regular-svg-icons"
import ReviewImageBox from "./ReviewImageBox"
import { useState, useEffect } from "react"

const ProductReviewItem = ({ review, currentUser, onEdit, onDelete }) => {
  const [isMyReview, setIsMyReview] = useState(false)

  useEffect(() => {
    // 현재 로그인한 사용자와 리뷰 작성자가 같은지 확인
    if (currentUser && review?.member) {
      setIsMyReview(currentUser.memberId === review.member.memberId)
    }
  }, [currentUser, review])

  if (!review) return null

  const handleEdit = () => {
    if (onEdit) {
      onEdit(review)
    }
  }

  const handleDelete = () => {
    if (onDelete) {
      const confirmDelete = window.confirm("정말로 이 리뷰를 삭제하시겠습니까?")
      if (confirmDelete) {
        onDelete(review.reviewId)
      }
    }
  }

  return (
    <>
      <Stack direction="horizontal" className="justify-content-between align-items-center p-3">
        <Stack direction={"horizontal"} gap={2}>
          <Image src={review?.member?.imageUrl || sampleImg} roundedCircle style={{ width: "40px", height: "40px" }} />
          <Stack>
            <h5>{review?.member?.nickname || "닉네임"}</h5>
            <desc className={"text-gray-300"}>
              {review?.createdAt ? new Date(review.createdAt).toLocaleDateString() : "2025.05.08"}
            </desc>
          </Stack>
        </Stack>
        <Stack direction="horizontal" gap={2} className="align-items-center">
          <Rating
            initialRating={review?.rate || 0}
            readonly
            fullSymbol={<FontAwesomeIcon icon={faStarSolid} color="#facc15" size="lg" />}
            emptySymbol={<FontAwesomeIcon icon={faStarRegular} color="#facc15" size="lg" />}
          />
          {/* 내가 작성한 리뷰인 경우에만 수정/삭제 버튼 표시 */}
          {isMyReview && (
            <Dropdown>
              <Dropdown.Toggle
                variant="link"
                size="sm"
                style={{
                  border: "none",
                  boxShadow: "none",
                  color: "#6c757d",
                  padding: "0.25rem 0.5rem",
                }}
              >
                <FontAwesomeIcon icon={faEllipsisV} />
              </Dropdown.Toggle>
              <Dropdown.Menu>
                <Dropdown.Item onClick={handleEdit}>수정</Dropdown.Item>
                <Dropdown.Item onClick={handleDelete} className="text-danger">
                  삭제
                </Dropdown.Item>
              </Dropdown.Menu>
            </Dropdown>
          )}
        </Stack>
      </Stack>
      <div className={"ms-3"}>{review?.comment || "리뷰 내용이 없습니다."}</div>
      {review?.imageUrl && review.imageUrl.length > 0 ? <ReviewImageBox imageList={review.imageUrl} /> : null}
    </>
  )
}

export default ProductReviewItem
