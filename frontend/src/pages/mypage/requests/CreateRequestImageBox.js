import { useRef, useState, useEffect } from "react";
import "yet-another-react-lightbox/styles.css";

const CreateRequestImageBox = ({
  imageFileList,
  setImageFileList,
  existingImageUrls = [],
}) => {
  const [imageUrlList, setImageUrlList] = useState([]);
  const fileInputRefs = useRef([]);

  // 기존 이미지 URL들을 초기 상태로 설정
  useEffect(() => {
    if (existingImageUrls && existingImageUrls.length > 0) {
      setImageUrlList(existingImageUrls);
    }
  }, [existingImageUrls]);

  const handleImageAddClick = (index) => {
    if (fileInputRefs.current[index]) {
      fileInputRefs.current[index].click();
    }
  };

  const handleFileChange = (event, index) => {
    const files = event.target.files;
    if (files.length === 0) return;

    const file = files[0];
    const newImageUrl = URL.createObjectURL(file);

    // 새로운 이미지 URL 배열 생성
    const newImageUrls = [...imageUrlList];
    newImageUrls[index] = newImageUrl;

    setImageUrlList(newImageUrls);

    // 파일 리스트 업데이트
    const newFileList = [...(imageFileList || [])];
    newFileList[index] = file;
    setImageFileList(newFileList);
  };

  const removeImage = (index) => {
    const newImageUrls = [...imageUrlList];
    newImageUrls.splice(index, 1);
    setImageUrlList(newImageUrls);

    const newFileList = [...(imageFileList || [])];
    newFileList.splice(index, 1);
    setImageFileList(newFileList);
  };

  // 초기 4개 슬롯 렌더링
  const renderInitialSlots = () => {
    const slots = [];
    const initialSlots = 4;

    for (let i = 0; i < initialSlots; i++) {
      const hasImage = imageUrlList[i];

      slots.push(
        <div
          key={i}
          style={{
            width: "150px",
            height: "150px",
            border: "2px dashed #dee2e6",
            borderRadius: "8px",
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
            justifyContent: "center",
            cursor: "pointer",
            backgroundColor: "#f8f9fa",
            position: "relative",
          }}
          onClick={() => handleImageAddClick(i)}
        >
          {hasImage ? (
            <>
              <img
                src={imageUrlList[i] || "/placeholder.svg"}
                alt={`상품 이미지 ${i + 1}`}
                style={{
                  width: "100%",
                  height: "100%",
                  objectFit: "cover",
                  borderRadius: "6px",
                }}
              />
              <button
                type="button"
                onClick={(e) => {
                  e.stopPropagation();
                  removeImage(i);
                }}
                style={{
                  position: "absolute",
                  top: "5px",
                  right: "5px",
                  background: "rgba(0,0,0,0.5)",
                  color: "white",
                  border: "none",
                  borderRadius: "50%",
                  width: "24px",
                  height: "24px",
                  cursor: "pointer",
                  fontSize: "12px",
                }}
              >
                ×
              </button>
              {i === 0 && (
                <div
                  style={{
                    position: "absolute",
                    bottom: "5px",
                    left: "5px",
                    background: "rgba(0,0,0,0.7)",
                    color: "white",
                    padding: "2px 6px",
                    borderRadius: "4px",
                    fontSize: "10px",
                  }}
                >
                  대표 이미지
                </div>
              )}
            </>
          ) : (
            <>
              {i === 0 ? (
                // 첫 번째 슬롯 - 대표 이미지
                <>
                  <div
                    style={{
                      fontSize: "24px",
                      color: "#dee2e6",
                      marginBottom: "8px",
                    }}
                  >
                    <svg
                      width="24"
                      height="24"
                      viewBox="0 0 24 24"
                      fill="currentColor"
                    >
                      <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z" />
                    </svg>
                  </div>
                  <div
                    style={{
                      fontSize: "12px",
                      color: "#6c757d",
                      textAlign: "center",
                    }}
                  >
                    대표 이미지
                  </div>
                </>
              ) : (
                // 나머지 슬롯들 - + 아이콘
                <div style={{ fontSize: "48px", color: "#dee2e6" }}>+</div>
              )}
            </>
          )}
          <input
            type="file"
            accept="image/*"
            ref={(el) => (fileInputRefs.current[i] = el)}
            style={{ display: "none" }}
            onChange={(e) => handleFileChange(e, i)}
          />
        </div>
      );
    }

    return slots;
  };

  // 추가 이미지 슬롯들 (4개 이후)
  const renderAdditionalSlots = () => {
    if (imageUrlList.length <= 4) return null;

    const additionalSlots = [];
    const maxSlots = 10;

    for (let i = 4; i < Math.min(imageUrlList.length + 1, maxSlots); i++) {
      const hasImage = imageUrlList[i];

      additionalSlots.push(
        <div
          key={i}
          style={{
            width: "150px",
            height: "150px",
            border: "2px dashed #dee2e6",
            borderRadius: "8px",
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
            justifyContent: "center",
            cursor: "pointer",
            backgroundColor: hasImage ? "transparent" : "#f8f9fa",
            position: "relative",
          }}
          onClick={() => !hasImage && handleImageAddClick(i)}
        >
          {hasImage ? (
            <>
              <img
                src={imageUrlList[i] || "/placeholder.svg"}
                alt={`상품 이미지 ${i + 1}`}
                style={{
                  width: "100%",
                  height: "100%",
                  objectFit: "cover",
                  borderRadius: "6px",
                }}
              />
              <button
                type="button"
                onClick={(e) => {
                  e.stopPropagation();
                  removeImage(i);
                }}
                style={{
                  position: "absolute",
                  top: "5px",
                  right: "5px",
                  background: "rgba(0,0,0,0.5)",
                  color: "white",
                  border: "none",
                  borderRadius: "50%",
                  width: "24px",
                  height: "24px",
                  cursor: "pointer",
                  fontSize: "12px",
                }}
              >
                ×
              </button>
            </>
          ) : (
            <div style={{ fontSize: "48px", color: "#dee2e6" }}>+</div>
          )}
          <input
            type="file"
            accept="image/*"
            ref={(el) => (fileInputRefs.current[i] = el)}
            style={{ display: "none" }}
            onChange={(e) => handleFileChange(e, i)}
          />
        </div>
      );
    }

    return additionalSlots;
  };

  return (
    <div>
      {/* 초기 4개 슬롯 */}
      <div
        style={{
          display: "grid",
          gridTemplateColumns: "repeat(4, 150px)",
          gap: "15px",
          justifyContent: "start",
          marginBottom: imageUrlList.length > 4 ? "20px" : "0",
        }}
      >
        {renderInitialSlots()}
      </div>

      {/* 추가 이미지 슬롯들 */}
      {imageUrlList.length > 4 && (
        <div
          style={{
            display: "grid",
            gridTemplateColumns: "repeat(auto-fill, minmax(150px, 1fr))",
            gap: "15px",
          }}
        >
          {renderAdditionalSlots()}
        </div>
      )}
    </div>
  );
};

export default CreateRequestImageBox;
