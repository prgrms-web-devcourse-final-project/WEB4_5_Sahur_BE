import {Button, Card} from "react-bootstrap";

const ShareCard = ({ onClose }) => {
    return (
        <Card>
            <Card.Header>
                공유
            </Card.Header>
            <Card.Body>

            </Card.Body>
            <Card.Footer>
                <Button onClick={onClose}>닫기</Button>
            </Card.Footer>
        </Card>
    );
}

export default ShareCard;