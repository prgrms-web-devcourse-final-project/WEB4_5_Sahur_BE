import {Button, Card, Col, ProgressBar, Row} from "react-bootstrap";
import ProductCard from "./ProductCard";


const Main = () => {

    return (
        <Card className={"p-4"} style={{ marginBottom: "65px" }}>
            <Card.Body className={"px-2 pb-10"} style={{ background: "var(--Gray-100, #e5e7ea)" }}>
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
                <Card className={"mt-3 p-2"}>
                    <Card.Body className={"p-2"}>
                        <h4>반려동물 공동 구매</h4>
                        <Row className={"mt-3"}>
                            <Col md={3}>
                                <ProductCard />
                            </Col>
                            <Col md={3}>
                                <ProductCard />
                            </Col>
                            <Col md={3}>
                                <ProductCard />
                            </Col>
                            <Col md={3}>
                                <ProductCard />
                            </Col>
                        </Row>
                    </Card.Body>
                </Card>
            </Card.Body>
        </Card>
    )

}

export default Main;