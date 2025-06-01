import {Card, Col, Row} from "react-bootstrap";
import ProductCard from "./ProductCard";

const sampleProduct = {
    "productId": 62,
    "title": "차량용 방향제",
    "description": "장시간 지속되는 차량용 방향제.",
    "imageUrl": [
        "https://i.pravatar.cc/150?img=61.jpg"
    ],
    "price": 527000,
    "dibCount": 4,
    "createdAt": "2025-05-28T06:16:29.142298",
    "category": {
        "categoryId": 62,
        "categoryType": "PET",
        "keyword": "PET_HYGIENE"
    }
};

const MyPageDibs = () => {
    const data = [sampleProduct, sampleProduct, sampleProduct, sampleProduct, sampleProduct, sampleProduct, sampleProduct, sampleProduct, sampleProduct];
    return (
        <>
            {data.map((item, index) => (
                index % 3 === 0 && (
                    <Row className="mt-3" key={index}>
                        {data.slice(index, index + 3).map((item, i) => (
                            <Col md={4} key={i}>
                                <ProductCard product={item}/>
                            </Col>
                        ))}
                    </Row>
                )
            ))}
        </>
    );
}

export default MyPageDibs;