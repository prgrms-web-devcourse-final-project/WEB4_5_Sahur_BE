import {Button, Card, Stack} from "react-bootstrap";
import styles from "./AdminProducts.module.scss";

const AdminProductsDetail = () => {
    return (
        <Stack direction={"vertical"} gap={2} >
            <Card>
                <Card.Body>
                    등록 상품 관리
                </Card.Body>
            </Card>
            <Card>
                <Card.Header>
                    기본 정보
                </Card.Header>
            </Card>
            <Card>
                <Card.Header>
                    상품 이미지
                </Card.Header>
            </Card>
            <Card>
                <Card.Header>
                    공동 구매 설정
                </Card.Header>
            </Card>
            <Card>
                <Card.Body>
                    <Button className={styles.detailButton} variant={""}>취소</Button>
                    <Button className={styles.detailButton} variant={"primary"}>완료</Button>
                </Card.Body>
            </Card>
        </Stack>
    );
}

export default AdminProductsDetail;