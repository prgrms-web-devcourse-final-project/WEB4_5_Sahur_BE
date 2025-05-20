import {Image, Stack} from "react-bootstrap";
import Lightbox from 'yet-another-react-lightbox';
import "yet-another-react-lightbox/styles.css";
import styles from "../GroupBuy.module.scss";
import {
    ReactComponent as OtherImage
} from "../../../assets/images/other-image.svg";
import {useState} from "react";

const ReviewImageBox = ({ imageList }) => {
    const [lightboxIndex, setLightboxIndex] = useState(null); //클릭한 이미지 인덱스
    return (
        <>
            <Stack direction={"horizontal"} gap={3} className={"px-3"}>
                {imageList.slice(0, 3).map((item, index) => (
                    <Image
                        key={index}
                        src={item}
                        className={`${styles.subImage} cursor-pointer`}
                        rounded
                        style={{ width: '90px', height: '90px' }}
                        onClick={() => setLightboxIndex(index)}
                    />
                ))}
                {imageList?.length > 3 && <span
                    className="cursor-pointer"
                    onClick={() => setLightboxIndex(0)} // 전체 보기 → 첫 이미지부터
                >
                        <OtherImage width={90} height={90} />
                    </span>
                }
            </Stack>
            <Lightbox
                open={lightboxIndex !== null}
                close={() => setLightboxIndex(null)}
                slides={imageList.map((item) => ({ src: item }))}
                index={lightboxIndex ?? 0} // fallback to 0
            />
        </>
    );
}

export default ReviewImageBox;