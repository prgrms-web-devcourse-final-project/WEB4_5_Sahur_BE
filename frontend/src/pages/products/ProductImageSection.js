import styles from "./GroupBuy.module.scss";
import {Col, Image, Row} from "react-bootstrap";
import {useEffect, useState} from "react";

const ProductImageSection = ({ imageUrlList }) => {
    const [titleImageUrl, setTitleImageUrl] = useState();

    useEffect(() => {
        if (imageUrlList) {
            setTitleImageUrl(imageUrlList[0])
        }
    }, [imageUrlList])
    return (
        <>
            <div style={{ height: '400px' }}>
                <img src={titleImageUrl} className={styles.mainImage} />
            </div>
            <Row className="justify-content-center gx-3 p-2">
                {imageUrlList?.map((imageUrl) => (
                    <Col key={imageUrl} style={{ flex: "1 0 20%", maxWidth: "20%" }}>
                        <Image src={imageUrl}
                               className={`${styles.subImage} cursor-pointer`}
                               rounded
                               style={{ height: '90px' }}
                               onClick={() => setTitleImageUrl(imageUrl)}
                        />
                    </Col>
                ))}
            </Row>
        </>
    );
}

export default ProductImageSection;