import {Badge, Card} from "react-bootstrap";
import FilterToggle from "./FilterToggle";
import FlexibleTable from "../../../shared/table/FlexibleTable";
import TableBackGroundCard from "../../../shared/TableBackGroundCard";
import {useNavigate} from "react-router-dom";

const AdminGroupBuy = () => {
    const navigate = useNavigate();
    const initColumns = [
        {
            id: "title",
            header: "상품명",
            cell: () => ("프리미엄 블루투스 이어폰 100")
        },
        {
            id: "currentPerson",
            header: "현재 인원",
            cell: () => ("15명")
        },
        {
            id: "minimumPerson",
            header: "최소 인원",
            cell: () => ("30명")
        },
        {
            id: "deadline",
            header: "마감 일시",
            cell: () => ("2025-06-12 18:00")
        },
        {
            id: "status",
            header: "상태",
            cell: () => (<Badge bg="danger">모집중</Badge>),
        },
        {
            id: "adminButton",
            header: "관리",
            cell: () => (<span className={"cursor-pointer"} onClick={() => navigate("1")}>상세</span>)
        },
        {
            id: "productPage",
            header: "상품 페이지",
            cell: () => (<span className={"cursor-pointer"} onClick={() => navigate("1")}>이동</span>)
        },
    ];

    return (
        <Card className={"px-10"}>
            <Card.Header className={"border-0"}>
                <h4>공동 구매 관리</h4>
                <desc className={"text-gray-300"}>진행 중인 모든 공동구매를 관리합니다.</desc>
            </Card.Header>
            <Card.Body>
                <FilterToggle />
                <TableBackGroundCard>
                    <FlexibleTable initColumns={initColumns} data={[1,2,3,4,5,6,7,8]} />
                </TableBackGroundCard>
            </Card.Body>
        </Card>
    );
}

export default AdminGroupBuy;