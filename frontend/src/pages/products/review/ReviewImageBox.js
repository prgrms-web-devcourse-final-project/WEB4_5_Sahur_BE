"use client"

import { useState } from "react"
import Lightbox from "yet-another-react-lightbox"
import "yet-another-react-lightbox/styles.css"

const ReviewImageBox = ({ imageList }) => {
  const [isOpen, setIsOpen] = useState(false)
  const [photoIndex, setPhotoIndex] = useState(0)

  const handleImageClick = (index) => {
    setPhotoIndex(index)
    setIsOpen(true)
  }

  const handleMoreImagesClick = () => {
    setPhotoIndex(3) // 4번째 이미지부터 시작
    setIsOpen(true)
  }

  return (
    <>
      <div className="d-flex gap-2">
        {imageList?.slice(0, 3).map((image, index) => (
          <img
            key={index}
            src={image || "/placeholder.svg"}
            alt={`리뷰 이미지 ${index + 1}`}
            style={{
              width: "90px",
              height: "90px",
              objectFit: "cover",
              borderRadius: "8px",
              cursor: "pointer",
              border: "1px solid #dee2e6",
            }}
            onClick={() => handleImageClick(index)}
          />
        ))}

        {/* 3개 이상일 때 "그 외 이미지" 버튼 */}
        {imageList?.length > 3 && (
          <div
            style={{
              width: "90px",
              height: "90px",
              backgroundColor: "#f8f9fa",
              borderRadius: "8px",
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              cursor: "pointer",
              border: "1px solid #dee2e6",
              fontSize: "14px",
              fontWeight: "500",
              color: "#6c757d",
            }}
            onClick={handleMoreImagesClick}
          >
            +{imageList.length - 3}
          </div>
        )}
      </div>

      {/* Lightbox */}
      <Lightbox
        open={isOpen}
        close={() => setIsOpen(false)}
        index={photoIndex}
        slides={imageList?.map((image) => ({ src: image })) || []}
        on={{
          view: ({ index }) => setPhotoIndex(index),
        }}
      />
    </>
  )
}

export default ReviewImageBox
