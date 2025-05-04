import {Button, Card, ProgressBar} from "react-bootstrap";
import sampleImg from "../../assets/images/sample.png"

const ProductCard = () => {
    return (
        <Card className={"p-2 m-1"}>
            <Card.Img variant="top" src={sampleImg} />
            <Card.Body>
                <Card.Title>프리미엄 블푸투스 이어폰</Card.Title>
                <Card.Text>
                    내용
                </Card.Text>
                <ProgressBar now={60} />
            </Card.Body>
        </Card>
    );
}

export default ProductCard