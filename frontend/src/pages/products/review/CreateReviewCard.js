import {Button, Card, Stack} from "react-bootstrap";
import styles from '../GroupBuy.module.scss'
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {
    faStar as faStarSolid,
    faXmark
} from "@fortawesome/free-solid-svg-icons";
import {faStar as faStarRegular} from "@fortawesome/free-regular-svg-icons";
import Rating from "react-rating";
import {useState} from "react";
import CreateReviewImageBox from "./CreateReviewImageBox";
import ReviewTextarea from "./ReviewTextarea";

const CreateReviewCard = ({ handleClose }) => {
    const [rating, setRating] = useState(5);
    const [reviewImageFileList, setReviewFileImageList] = useState();
    const [reviewText, setReviewText] = useState();

    const handleConfirmClick = () => {
        console.log(rating, reviewImageFileList, reviewText)
        handleClose();
    }
    return (
        <Card className={styles.reviewCard}>
            <Card.Header>
                <Stack direction={"horizontal"} className={"justify-content-between align-items-center"} >
                    <black></black>
                    <h4>리뷰 작성</h4>
                    <span className={"cursor-pointer"} onClick={handleClose}>
                        <FontAwesomeIcon icon={faXmark}></FontAwesomeIcon>
                    </span>
                </Stack>
            </Card.Header>
            <Card.Body>
                <Stack direction={"vertical"} gap={3} style={{ overflow: 'auto', maxHeight: "280px" }}>
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
                    <ReviewTextarea value={reviewText} onChange={(e) => reviewText(e.target.value)} />
                </Stack>
            </Card.Body>
            <Card.Footer className={"m-3"}>
                <Stack direction={"horizontal"} gap={3} className={"d-flex justify-content-center"}>
                    <Button variant={""} style={{ minWidth: "30px" }} className={styles.reviewButton} onClick={handleClose}>취소</Button>
                    <Button style={{ minWidth: "30px" }} onClick={handleConfirmClick}>완료</Button>
                </Stack>
            </Card.Footer>
        </Card>
    );
}

export default CreateReviewCard;