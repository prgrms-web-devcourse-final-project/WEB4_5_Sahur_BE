import {useNavigate} from "react-router-dom";
import {Badge, Button, Card, Form, InputGroup, Stack} from "react-bootstrap";
import TableBackGroundCard from "../../../shared/TableBackGroundCard";
import FlexibleTable from "../../../shared/table/FlexibleTable";
import FilterButtonGroup from "./FilterButtonGroup";

const AdminReviews = () => {
    const navigate = useNavigate();
    const initColumns = [
        {
            id: "orderId",
            header: "주문 번호",
            cell: ({ row }) => (`ORD-2023122${row.index}`)
        },
        {
            id: "orderPersonName",
            header: "주문자",
            cell: ({ row }) => (`구매자${row.index}`)
        },
        {
            id: "products",
            header: "상품",
            cell: () => ("프리미엄 블루투스 이어폰 외 1건")
        },
        {
            id: "price",
            header: "결제 금액",
            cell: () => ("₩47000")
        },
        {
            id: "orderDate",
            header: "주문 일시",
            cell: () => ("2025-12-19 17:00"),
        },
        {
            id: "status",
            header: "상태",
            cell: () => (<Badge bg="danger">취소</Badge>),
        },
        {
            id: "adminButton",
            header: "관리",
            cell: () => (<span className={"cursor-pointer"} onClick={() => navigate("1")}>상세</span>)
        },
    ];

    return (
        <Card className={"px-10"}>
            <Card.Header className={"border-0"}>
                <h4>리뷰 관리</h4>
                <desc className={"text-gray-300"}>구매자 리뷰를 관리합니다.</desc>
            </Card.Header>
            <Card.Body>
                <FilterButtonGroup />
                <TableBackGroundCard>
                    <FlexibleTable initColumns={initColumns} data={[1,2,3,4,5,6,7,8]} />
                </TableBackGroundCard>
            </Card.Body>
        </Card>
    );
}

export default AdminReviews;