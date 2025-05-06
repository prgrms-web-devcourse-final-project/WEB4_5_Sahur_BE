import React, {useEffect, useRef, useState} from 'react';
import noImage from "../../assets/images/no-image.png"
import {Button} from "react-bootstrap";

function ProfileImageZone({ profileImageUrl, handleProfileImageChange }) {
    const fileInputRef = useRef(null);
    const [previewUrl, setPreviewUrl] = useState();

    useEffect(() => {
        if (profileImageUrl) {
            setPreviewUrl(profileImageUrl)
        }
    }, [profileImageUrl])

    const handleButtonClick = () => {
        fileInputRef.current.click(); // 버튼 클릭 시 숨겨진 파일 선택창 열기
    };

    const handleFileChange = (event) => {
        const file = event.target.files[0];
        if (!file) return;

        const imageUrl = URL.createObjectURL(file);
        setPreviewUrl(imageUrl);
        handleProfileImageChange(file)
    };

    return (
        <div>
            <img src={previewUrl ? previewUrl : noImage}
                 alt="프로필"
                 style={previewUrl ? {
                     width: "210px",
                     height: "210px",
                     objectFit: 'cover',
                     borderRadius: '50%',
                     display: 'block',
                     margin: 'auto' } :
                     { width: "100%", objectFit: "cover" }}
                 onClick={handleButtonClick}
                 className={"cursor-pointer"}
            />
            <br />

            <input type="file"
                   accept="image/*"
                   ref={fileInputRef}
                   style={{ display: 'none' }}
                   onChange={handleFileChange}
            />
        </div>
    );
}

export default ProfileImageZone;


