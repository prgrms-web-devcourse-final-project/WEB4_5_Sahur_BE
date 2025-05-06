import {Button, Card, Col, Pagination, Row, Stack} from "react-bootstrap";
import clsx from "clsx";
import styles from "./Main.module.scss";
import ProductCard from "./ProductCard";
import {useState} from "react";
import PaginationSection from "./PaginationSection";

const sortOptions = ['최신순', '인기순', '마감임박순'];

const CategoryProductSection = () => {
    const [activeSort, setActiveSort] = useState('최신순');

    return (
        <Card className={"mt-3 p-2"}>
            <Card.Body className={"p-2"}>
                <Stack direction={"horizontal"} className={"d-flex justify-content-between"}>
                    <h4>반려동물 공동 구매</h4>
                    <Stack direction={"horizontal"} gap={3}>
                        {sortOptions.map((label) => (
                            <Button key={label}
                                    bsPrefix={clsx(styles.sortButton, {[styles.active]: activeSort === label,})}
                                    onClick={() => setActiveSort(label)}
                            >
                                {label}
                            </Button>
                        ))}
                    </Stack>
                </Stack>
                <Row className={"mt-3"}>
                    <Col md={3}>
                        <ProductCard />
                    </Col>
                    <Col md={3}>
                        <ProductCard />
                    </Col>
                    <Col md={3}>
                        <ProductCard />
                    </Col>
                    <Col md={3}>
                        <ProductCard />
                    </Col>
                </Row>
                <Row className={"mt-3"}>
                    <Col md={3}>
                        <ProductCard />
                    </Col>
                    <Col md={3}>
                        <ProductCard />
                    </Col>
                    <Col md={3}>
                        <ProductCard />
                    </Col>
                    <Col md={3}>
                        <ProductCard />
                    </Col>
                </Row>
                <PaginationSection />
            </Card.Body>
        </Card>
    );
}

export default CategoryProductSection;