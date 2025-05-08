import {Col, Image, Row, Stack} from "react-bootstrap";
import sampleImg from "../../assets/images/sample.png"
const ProductDescription = () => {
    return (
        <Row className={"mt-10"}>
            <Col md={12} >
                <Stack gap={3}>
                    <h5 >상품 설명</h5>
                    <desc className={"text-gray-300"}>상품 설명 블라블라블라....</desc>
                    <Image src={sampleImg} />
                </Stack>
            </Col>
        </Row>
    );
}

export default ProductDescription;