import {Button, Card, Tab, Tabs} from "react-bootstrap";
import TableBackGroundCard from "../../../shared/TableBackGroundCard";
import FlexibleTable from "../../../shared/table/FlexibleTable";
import Paging from "../../../shared/Paging";
import {useQueryParam} from "../../../hooks/QueryParam";
import {useApiQuery} from "../../../hooks/useApiQuery";
import axios from "axios";
import {Link, useLocation, useNavigate} from "react-router-dom";
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
                <Tabs
                    activeKey={activeTab}
                    onSelect={handleTabSelect}
                    id="uncontrolled-tab-example"
                    className="mb-3"
                >
                    <Tab eventKey="products" title="등록된 상품 목록">
                        {activeTab === 'products' && (
                            <ProductsTable />
                        )}
                    </Tab>
                    <Tab eventKey="productsRequests" title="상품 등록 요청">
                        {activeTab === 'productsRequests' && (
                            <ProductsRequestsTable />
                        )}
                    </Tab>
                </Tabs>
            </Card.Body>
        </Card>
    );
}

export default AdminProducts;