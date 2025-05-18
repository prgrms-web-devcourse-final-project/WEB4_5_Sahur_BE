import {Button, Card, Col, Row} from "react-bootstrap";
import {ReactComponent as KakaoIcon} from "../../assets/images/oauth/kakao_squer.svg";
import ShareLinkBox from "./ShareLinkBox";

const ShareCard = ({ onClose }) => {
    return (
        <Card style={{ minWidth: "500px" }} className={"border rounded"}>
            <Card.Header className={"m-2"}>
                <h4>공유</h4>
            </Card.Header>
            <Card.Body>
                <Row className={"m-2"}>
                    <Col xs={12} className={"d-flex justify-content-center"}>
                        <KakaoIcon />
                    </Col>
                </Row>
                <Row className={"m-2"}>
                    <Col xs={12} className={"d-flex justify-content-center"}>
                        <ShareLinkBox />
                    </Col>
                </Row>
            </Card.Body>
            <Card.Footer className={"d-flex justify-content-center m-3"}>
                <Button onClick={onClose}>닫기</Button>
            </Card.Footer>
        </Card>
    );
}

export default ShareCard;