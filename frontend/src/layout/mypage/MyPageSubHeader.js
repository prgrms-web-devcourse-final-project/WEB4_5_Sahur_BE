const MyPageSubHeader = ({ pageId }) => {
    const title = {
        dashboard: '대시보드',
        orders: '주문 내역',
        payments: '결제 내역',
        requests: '상품 등록 요청',
        dibs: '관심 상품',
        reviews: '리뷰 관리',
    }
    return (
            <div className={"py-4"}>
                <h4 style={{ fontWeight: 600 }}>{title[pageId]}</h4>
            </div>
    );
}

export default MyPageSubHeader;