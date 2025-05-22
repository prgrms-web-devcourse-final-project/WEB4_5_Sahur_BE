import {Button, Col, Overlay, Row, Stack} from "react-bootstrap";
import ProductReviewItem from "./ProductReviewItem";
import styles from "../GroupBuy.module.scss"
import {useRef, useState} from "react";
import CreateReviewCard from "./CreateReviewCard";
import axios from "axios";
import {useApiMutation} from "../../../hooks/useApiMutation";

const checkWritableReview = async (productId) => {
    const response = await axios.get(`/api/v1/histories/products/${productId}/writable-histories`, {withCredentials: true});
    return response.data.data;
}

const ProductReviewList = ({ product }) => {
    const [show, setShow] = useState(false);
    const target = useRef(null);
    const { mutate: checkWritableReviewMutate } = useApiMutation(checkWritableReview, {
        onSuccess: (data) => {
            console.log(data);
        }
    });
    const handleCreateReview = () => {
        checkWritableReviewMutate(product?.productId);
        setShow(prev => !prev)
    }

    return (
        <Row className={"mt-10"}>
            <Col md={12} >
                <Stack direction="horizontal" className="justify-content-between align-items-center">
                    <h5>상품 리뷰</h5>
                    <Button variant={""} className={styles.reviewButton} size={"sm"}
                            ref={target} onClick={handleCreateReview}>리뷰 작성
                    </Button>
                    <Overlay target={target.current} show={show} placement="left"
                             popperConfig={{
                                 modifiers: [{name: 'offset', options: {offset: [30, 10]}}]
                             }}>
                        <div>
                            <CreateReviewCard handleClose={() => setShow(false)}/>
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