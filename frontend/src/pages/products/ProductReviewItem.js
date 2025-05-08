import {Image, Stack} from "react-bootstrap";
import sampleImg from "../../assets/images/sample.png";
import Rating from "react-rating";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faStar as faStarSolid } from "@fortawesome/free-solid-svg-icons";
import { faStar as faStarRegular } from "@fortawesome/free-regular-svg-icons";
import {useState} from "react";

const StarRating = () => {
    const [rating, setRating] = useState(3);
    return (
        <Rating
            initialRating={rating}
            onChange={setRating}
            fullSymbol={<FontAwesomeIcon icon={faStarSolid} color="#facc15" size="lg" />}
            emptySymbol={<FontAwesomeIcon icon={faStarRegular} color="#facc15" size="lg" />}
        />
    );
};


const ProductReviewItem = () => {
    return (
        <>
            <Stack direction="horizontal" className="justify-content-between align-items-center p-3">
                <Stack direction={"horizontal"} gap={2}>
                    <Image src={sampleImg} roundedCircle style={{ width: "40px", height: "40px" }}/>
                    <Stack>
                        <h5>닉네임</h5>
                        <desc className={"text-gray-300"}>2025.05.08</desc>
                    </Stack>
                </Stack>
                <StarRating />
            </Stack>
            <div className={"ms-3"}>리뷰 내용 리뷰 내용 리뷰 내용 리뷰 내용 리뷰 리뷰 내용 리뷰 내용 리뷰 내용 리뷰 내용 리뷰 리뷰 내용 리뷰 내용 리뷰 내용 리뷰 내용 리뷰  </div>
        </>
    );
}

export default ProductReviewItem;