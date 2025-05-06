import {Button, Card, Col, Overlay, Row, Stack} from "react-bootstrap";
import ProductReviewItem from "./ProductReviewItem";
import styles from "./ProductDetail.module.scss"
import {useRef, useState} from "react";
import CreateReviewCard from "./CreateReviewCard";

const ProductReviewList = () => {
    const [show, setShow] = useState(false);
    const target = useRef(null);
    return (
        <Row className={"mt-10"}>
            <Col md={12} >
                <Stack direction="horizontal" className="justify-content-between align-items-center">
                    <h5>상품 리뷰</h5>
                    <Button variant={""} className={styles.reviewButton} size={"sm"}
                            ref={target} onClick={() => setShow(!show)}>리뷰 작성</Button>
                    <Overlay target={target.current} show={show} placement="left"
                             popperConfig={{
                                 modifiers: [{name: 'offset', options: {offset: [30, 10]}}]
                             }}>
                        <div>
                            <CreateReviewCard />
                        </div>
                    </Overlay>
                </Stack>
                <ProductReviewItem />
                <ProductReviewItem />
                <ProductReviewItem />
            </Col>
        </Row>
    );
}

export default ProductReviewList;