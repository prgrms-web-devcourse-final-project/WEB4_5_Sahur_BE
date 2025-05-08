import {
    Badge,
    Button,
    Card,
    Col,
    Image,
    ProgressBar,
    Row,
    Stack
} from "react-bootstrap";
import styles from "./ProductDetail.module.scss"
import ProductCard from "../main/ProductCard";
import {useState} from "react";
import ProductDescription from "./ProductDescription";
import ProductReviewList from "./ProductReviewList";
import clsx from "clsx";
import ProductWrapper from "./ProductWrapper";
import ProductImageSection from "./ProductImageSection";
import ProductBuySection from "./ProductBuySection";

const ProductDetail = () => {
    const [currentView, setCurrentView] = useState('description'); //description, review 둘 중 하나
    return (
        <ProductWrapper>
            <Row className={styles.sectionDivider}>
                <Col md={6}>
                    <ProductImageSection />
                </Col>
                <Col md={6}>
                    <ProductBuySection />
                </Col>
            </Row>{/* 상품 이미지 및 구매 섹션 로우 끝 */}
            <Row>
                <h4>진행중인 다른 공동구매</h4>
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
                </Row>{/* 진행중인 다른 공동구매 로우 끝 */}
                <Row className={"mt-10"}>
                    <Col md={12}>
                        <Stack direction={"horizontal"} className="justify-content-center align-items-center p-2" style={{ background: "var(--BackGround, #F1F5F9)" }}>
                            <Button variant={""} className={clsx(styles.view, currentView === 'description' && styles.active)} onClick={() => setCurrentView('description')}>상품상세</Button>
                            <Button variant={""} className={clsx(styles.view, currentView === 'review' && styles.active)} onClick={() => setCurrentView('review')}>리뷰 (100)</Button>
                        </Stack>
                    </Col>
                </Row> {/* 상품상세, 리뷰 버튼 로우 끝 */}
                {currentView === 'description' ? <ProductDescription /> : <ProductReviewList />}
            </Row>
        </ProductWrapper>
    );
}

export default ProductDetail;