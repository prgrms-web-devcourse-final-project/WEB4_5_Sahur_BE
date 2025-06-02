import { useEffect, useRef, useState } from "react";
import noImage from "../../assets/images/no-image.png";

function ProfileImageZone({ handleProfileImageChange, initialImageUrl }) {
  const fileInputRef = useRef(null);
  const [previewUrl, setPreviewUrl] = useState(null);

  // 초기 이미지 URL이 있으면 설정
  useEffect(() => {
    if (initialImageUrl) {
      setPreviewUrl(initialImageUrl);
    }
  }, [initialImageUrl]);

  const handleButtonClick = () => {
    fileInputRef.current.click(); // 버튼 클릭 시 숨겨진 파일 선택창 열기
  };

  const handleFileChange = (event) => {
    const file = event.target.files[0];
    if (!file) return;

    const imageUrl = URL.createObjectURL(file);
    setPreviewUrl(imageUrl);
    handleProfileImageChange(file);
  };

  return (
    <div>
      <img
        src={previewUrl || noImage}
        alt="프로필"
        style={
          previewUrl
            ? {
                width: "210px",
                height: "210px",
                objectFit: "cover",
                borderRadius: "50%",
                display: "block",
                margin: "auto",
                cursor: "pointer",
              }
            : {
                width: "210px",
                height: "210px",
                objectFit: "cover",
                display: "block",
                margin: "auto",
                cursor: "pointer",
              }
        }
        onClick={handleButtonClick}
        className={"cursor-pointer"}
      />
      <br />

      <input
        type="file"
        accept="image/*"
        ref={fileInputRef}
        style={{ display: "none" }}
        onChange={handleFileChange}
      />
    </div>
  );
}

export default ProfileImageZone;
