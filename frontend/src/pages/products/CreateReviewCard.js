import {Button, Card, Stack} from "react-bootstrap";
import styles from './GroupBuy.module.scss'

const CreateReviewCard = () => {
    return (
        <Card className={styles.reviewCard}>
            <Card.Header>
                <Stack direction={"horizontal"} className={"justify-content-between align-items-center"} >
                    <span>리뷰 작성</span>
                    <Button variant={""} className={styles.reviewButton} size={"sm"}>취소</Button>
                </Stack>
            </Card.Header>
            <Card.Body>
                바디
            </Card.Body>
            <Card.Footer>
                <Button >완료</Button>
            </Card.Footer>
        </Card>
    );
}

export default CreateReviewCard;