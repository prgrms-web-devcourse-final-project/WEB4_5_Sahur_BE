import {Card, Col, Row} from "react-bootstrap";
import ProductCard from "./ProductCard";

const PopularProductSection = () => {
    return (
        <Card className={"p-2"}>
            <Card.Body>
                <h4>실시간 인기 공동구매</h4>
                <Row>
                    <Col md={4}>
                        <ProductCard />
                    </Col>
                    <Col md={4}>
                        <ProductCard />
                    </Col>
                    <Col md={4}>
                        <ProductCard />
                    </Col>
                </Row>
            </Card.Body>
        </Card>
    );
}

export default PopularProductSection;