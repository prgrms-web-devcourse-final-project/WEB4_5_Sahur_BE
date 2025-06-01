import {ReactComponent as AddImage} from "../../../assets/images/add-image.svg"
import {useRef, useState} from "react"
import "yet-another-react-lightbox/styles.css"
import RequestImageBox from "./RequestImageBox"

const CreateReviewImageBox = ({ imageFileList, setImageFileList }) => {
    const [imageUrlList, setImageUrlList] = useState([]);
    const fileInputRef = useRef(null);

    const handleImageAddClick = () => {
        fileInputRef.current.click(); // 버튼 클릭 시 숨겨진 파일 선택창 열기
    };

    const handleFileChange = (event) => {
        const files = event.target.files;
        if (files.length === 0) return;
        const fileList = [...files];
        setImageFileList(fileList);
        setImageUrlList(fileList.map((file) => {
            return URL.createObjectURL(file);
        }));
    };

    return (
        <>
            {imageUrlList?.length === 0 ? <span
                className="cursor-pointer px-3"
                onClick={handleImageAddClick}>
                    <AddImage width={90} height={90} />
                    <input type="file"
                           accept="image/*"
                           multiple
                           ref={fileInputRef}
                           style={{ display: 'none' }}
                           onChange={handleFileChange}
                    />
                </span> : <RequestImageBox imageList={imageUrlList} />
            }
        </>
    );
}

export default CreateReviewImageBox
