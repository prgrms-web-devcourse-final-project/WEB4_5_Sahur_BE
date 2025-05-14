import sampleImg from "../../assets/images/sample.png";
import styles from "./GroupBuy.module.scss";
import {Col, Image, Row} from "react-bootstrap";

const ProductImageSection = () => {
    const subImages = [1,2,3,4,5];

    return (
        <>
            <div>
                <img src={sampleImg} className={styles.mainImage} />
            </div>
            <Row className="justify-content-center gx-3 p-2">
                {subImages.map((num) => (
                    <Col key={num} style={{ flex: "1 0 20%", maxWidth: "20%" }}>
                        <Image src={sampleImg}
                               className={styles.subImage}
                               rounded
                        />
                    </Col>
                ))}
            </Row>
        </>
    );
}

export default ProductImageSection;