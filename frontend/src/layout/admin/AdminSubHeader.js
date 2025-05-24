import {Card} from "react-bootstrap";

const AdminSubHeader = ({ pageId }) => {
    console.log(pageId)
    const title = {
        dashboard: '대시보드',
        groupBuy: '공동 구매 관리',
        products: '상품 관리',
        productsRequests: '상품 관리',
        orders: '주문 관리',
        reviews: '리뷰 관리',
    }
    return (
        <Card style={{ borderRadius: 0 }}>
            <Card.Body className={"py-4"}>
                <h4 style={{ fontWeight: 600, textAlign: 'center' }}>{title[pageId]}</h4>
            </Card.Body>
        </Card>
    );
}

export default AdminSubHeader;