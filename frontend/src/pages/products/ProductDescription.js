import {Col, Image, Row, Stack} from "react-bootstrap";
const ProductDescription = ({product}) => {
    return (
        <Row className={"mt-10"}>
            <Col md={12} >
                <Stack gap={3}>
                    <h5 >상품 설명</h5>
                    <desc className={"text-gray-300"}>{product?.description}</desc>
                    {product?.imageUrl.map((item) => {
                        return  <Image key={item} src={item} />
                    })}
                </Stack>
            </Col>
        </Row>
    );
}

export default ProductDescription;