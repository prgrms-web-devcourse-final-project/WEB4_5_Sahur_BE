import {Button, Card, Form, InputGroup, Tab, Tabs} from "react-bootstrap";
import {useQueryParam} from "../../../hooks/QueryParam";
import {useLocation, useNavigate} from "react-router-dom";
import styles from "./AdminProducts.module.scss"
import {useEffect, useState} from "react";
import ProductsTable from "./ProductsTable";
import ProductsRequestsTable from "./ProductsRequestsTable";

const AdminProducts = () => {
    const [queryParam, setQueryParam] = useQueryParam();
    const location = useLocation();
    const navigate = useNavigate();

    // 마지막 경로 segment를 탭 키로 사용
    const pathSegments = location.pathname.split('/');
    const lastSegment = pathSegments[pathSegments.length - 1];
    const [activeTab, setActiveTab] = useState(lastSegment || 'products');

    // 탭 변경 → URL 변경
    const handleTabSelect = (tabKey) => {
        setActiveTab(tabKey);
        // 현재 경로에서 마지막 segment만 변경
        const newPath = [...pathSegments.slice(0, -1), tabKey].join('/');
        navigate(newPath);
    };

    // URL이 바뀔 때 activeTab도 반영
    useEffect(() => {
        setActiveTab(lastSegment);
    }, [lastSegment]);

    return (
        <Card className={"px-10"}>
            <Card.Header className={"border-0"}>
                <h4>등록된 상품 목록</h4>
            </Card.Header>
            <Card.Body>
                <div className="d-flex justify-content-between mx-3">
                    <Tabs
                        activeKey={activeTab}
                        onSelect={handleTabSelect}
                        id="uncontrolled-tab-example"
                    >
                        <Tab eventKey="products" title="등록된 상품 목록" />
                        <Tab eventKey="productsRequests" title="상품 등록 요청" />
                    </Tabs>
                    {/* 오른쪽 검색창 */}
                    {activeTab === 'products' && (
                        <InputGroup className="me-3" style={{ width: 360, height: 50 }}>
                            <Form.Control
                                type="text"
                                placeholder="카테고리 or 키워드"
                            />
                            <Button className={styles.detailButton} variant="" type={"button"}>
                                검색
                            </Button>
                        </InputGroup>
                    )}
                </div>

                {/* 탭과 검색 컴포넌트를 같은 라인에 두기 위해 탭 콘텐츠는 따로 분리해서 렌더링 */}
                {activeTab === 'products' && <ProductsTable />}
                {activeTab === 'productsRequests' && <ProductsRequestsTable />}
            </Card.Body>
        </Card>
    );
}

export default AdminProducts;