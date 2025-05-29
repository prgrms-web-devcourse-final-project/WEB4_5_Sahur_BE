import { Image, Stack } from "react-bootstrap"
import sampleImg from "../../../assets/images/sample.png"
import Rating from "react-rating"
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome"
import { faStar as faStarSolid } from "@fortawesome/free-solid-svg-icons"
import { faStar as faStarRegular } from "@fortawesome/free-regular-svg-icons"
import ReviewImageBox from "./ReviewImageBox"

const ProductReviewItem = ({ review }) => {
  if (!review) return null

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
        <Rating
          initialRating={review?.rate || 0}
          readonly
          fullSymbol={<FontAwesomeIcon icon={faStarSolid} color="#facc15" size="lg" />}
          emptySymbol={<FontAwesomeIcon icon={faStarRegular} color="#facc15" size="lg" />}
        />
      </Stack>
      <div className={"ms-3"}>{review?.comment || "리뷰 내용이 없습니다."}</div>
      {review?.imageUrl && review.imageUrl.length > 0 ? <ReviewImageBox imageList={review.imageUrl} /> : null}
    </>
  )
}

export default ProductReviewItem
