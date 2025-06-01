import { Card, Badge, Button } from "react-bootstrap";
import styles from "./Dibs.module.scss";
import { ReactComponent as RedLikeIcon } from "../../../assets/images/icon/red-like.svg";

const ProductCard = ({ product, groupBuy, onRemove }) => {
  const handleRemoveFromWishlist = async (e) => {
    e.stopPropagation();

    try {
      const response = await fetch(
        `${process.env.REACT_APP_API_URL}/api/v1/dibs/products/${product.productId}/dibs`,
        {
          method: "DELETE",
          credentials: "include",
          headers: {
            "Content-Type": "application/json",
          },
        }
      );

      if (response.ok) {
        onRemove(product.productId);
      } else {
        console.error("Failed to remove from wishlist");
      }
    } catch (error) {
      console.error("Error removing from wishlist:", error);
    }
  };

  const handleViewDetails = () => {
    // 상세 페이지로 이동
    window.location.href = `/groupBuy/${
      groupBuy?.groupBuyId || product.productId
    }`;
  };

  return (
    <Card className={`${styles.groupBuyCardBorder}`}>
      <div className={styles.imageWrapper}>
        <img
          src={product.imageUrl[0] || "/placeholder.svg"}
          alt="썸네일"
          className={styles.img}
        />
        <Badge
          className={`${styles.badgeTopRight} cursor-pointer`}
          onClick={handleRemoveFromWishlist}
        >
          <RedLikeIcon />
        </Badge>
      </div>
      <Card.Body className={"p-2"}>
        <Card.Title>{product.title}</Card.Title>
        <div style={{ fontWeight: "700" }}>
          {product.price.toLocaleString()}원
        </div>
        {groupBuy && (
          <div className="text-muted small mb-2">
            참여자: {groupBuy.currentParticipantCount}/
            {groupBuy.targetParticipants}명
          </div>
        )}
        <Button className={"w-100 mt-2"} onClick={handleViewDetails}>
          상세보기
        </Button>
      </Card.Body>
    </Card>
  );
};

export default ProductCard;
