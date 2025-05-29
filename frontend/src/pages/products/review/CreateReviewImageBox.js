"use client"

import { ReactComponent as AddImage } from "../../../assets/images/add-image.svg"
import { useRef, useState, useEffect } from "react"
import "yet-another-react-lightbox/styles.css"
import ReviewImageBox from "./ReviewImageBox"

const CreateReviewImageBox = ({ imageFileList, setImageFileList, existingImages = [], onExistingImageRemove }) => {
  const [imageUrlList, setImageUrlList] = useState([])
  const [displayImages, setDisplayImages] = useState([])
  const fileInputRef = useRef(null)

  // 기존 이미지와 새로 추가한 이미지를 합쳐서 표시
  useEffect(() => {
    const newImageUrls = imageFileList.map((file) => URL.createObjectURL(file))
    setImageUrlList(newImageUrls)

    // 기존 이미지와 새 이미지를 합쳐서 표시
    const allImages = [
      ...existingImages.map((url) => ({ type: "existing", url })),
      ...newImageUrls.map((url) => ({ type: "new", url })),
    ]
    setDisplayImages(allImages)
  }, [imageFileList, existingImages])

  const handleImageAddClick = () => {
    fileInputRef.current.click()
  }

  const handleFileChange = (event) => {
    const files = event.target.files
    if (files.length === 0) return
    const fileList = Array.from(files)
    setImageFileList((prev) => [...prev, ...fileList])
  }

  const handleRemoveExistingImage = (imageUrl) => {
    if (onExistingImageRemove) {
      onExistingImageRemove(imageUrl)
    }
  }

  const handleRemoveNewImage = (imageUrl) => {
    const imageIndex = imageUrlList.indexOf(imageUrl)
    if (imageIndex !== -1) {
      const newFileList = [...imageFileList]
      newFileList.splice(imageIndex, 1)
      setImageFileList(newFileList)
    }
  }

  return (
    <>
      {displayImages.length === 0 ? (
        <span className="cursor-pointer px-3" onClick={handleImageAddClick}>
          <AddImage width={90} height={90} />
          <input
            type="file"
            accept="image/*"
            multiple
            ref={fileInputRef}
            style={{ display: "none" }}
            onChange={handleFileChange}
          />
        </span>
      ) : (
        <div>
          <ReviewImageBox imageList={displayImages.map((img) => img.url)} />

          {/* 이미지 관리 섹션 */}
          <div className="mt-3">
            <div className="d-flex flex-wrap gap-2">
              {displayImages.map((image, index) => (
                <div key={index} className="position-relative">
                  <img
                    src={image.url || "/placeholder.svg"}
                    alt={`이미지 ${index + 1}`}
                    style={{
                      width: "80px",
                      height: "80px",
                      objectFit: "cover",
                      borderRadius: "8px",
                      border: "1px solid #dee2e6",
                    }}
                  />
                  <button
                    type="button"
                    className="position-absolute bg-danger text-white border-0 rounded-circle d-flex align-items-center justify-content-center"
                    style={{
                      width: "20px",
                      height: "20px",
                      top: "-8px",
                      right: "-8px",
                      fontSize: "12px",
                      lineHeight: "1",
                      cursor: "pointer",
                      boxShadow: "0 2px 4px rgba(0,0,0,0.2)",
                    }}
                    onClick={() => {
                      if (image.type === "existing") {
                        handleRemoveExistingImage(image.url)
                      } else {
                        handleRemoveNewImage(image.url)
                      }
                    }}
                  >
                    ×
                  </button>
                </div>
              ))}

              {/* 이미지 추가 버튼 */}
              <div
                className="cursor-pointer d-flex align-items-center justify-content-center border border-2 border-dashed"
                style={{
                  width: "80px",
                  height: "80px",
                  borderRadius: "8px",
                  backgroundColor: "#f8f9fa",
                  borderColor: "#dee2e6",
                  transition: "all 0.2s ease",
                }}
                onClick={handleImageAddClick}
                onMouseEnter={(e) => {
                  e.target.style.backgroundColor = "#e9ecef"
                  e.target.style.borderColor = "#adb5bd"
                }}
                onMouseLeave={(e) => {
                  e.target.style.backgroundColor = "#f8f9fa"
                  e.target.style.borderColor = "#dee2e6"
                }}
              >
                <span style={{ fontSize: "28px", color: "#6c757d", fontWeight: "300" }}>+</span>
              </div>
            </div>

            <input
              type="file"
              accept="image/*"
              multiple
              ref={fileInputRef}
              style={{ display: "none" }}
              onChange={handleFileChange}
            />
          </div>
        </div>
      )}
    </>
  )
}

export default CreateReviewImageBox
