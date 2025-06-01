import {Badge, Button, Card} from "react-bootstrap";
import {useNavigate} from "react-router-dom";
import styles from "./Dibs.module.scss"
import {
    ReactComponent as RedLikeIcon
} from "../../../assets/images/icon/red-like.svg";

const ProductCard = ({ product }) => {
    return (
        <Card className={`${styles.groupBuyCardBorder}`}>
            <div className={styles.imageWrapper}>
                <img src={product.imageUrl[0]} alt="썸네일" className={styles.img} />
                <Badge className={`${styles.badgeTopRight} cursor-pointer`}><RedLikeIcon /></Badge>
            </div>
            <Card.Body className={"p-2"}>
                <Card.Title>{product.title}</Card.Title>
                <div style={{ fontWeight: "700" }}>{product.price.toLocaleString()}원</div>
                <Button className={"w-100 mt-10"}>상세보기</Button>
            </Card.Body>
        </Card>
    );
}

export default ProductCard;