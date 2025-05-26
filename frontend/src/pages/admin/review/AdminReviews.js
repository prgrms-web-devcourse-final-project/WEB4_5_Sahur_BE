import {useNavigate} from "react-router-dom";
import {Badge, Button, Card, Form, InputGroup, Stack} from "react-bootstrap";
import TableBackGroundCard from "../../../shared/TableBackGroundCard";
import FlexibleTable from "../../../shared/table/FlexibleTable";
import FilterButtonGroup from "./FilterButtonGroup";
import Rating from "react-rating";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faStar as faStarSolid} from "@fortawesome/free-solid-svg-icons";
import {faStar as faStarRegular} from "@fortawesome/free-regular-svg-icons";
import styles from './AdminReviews.module.scss'

const AdminReviews = () => {
    const navigate = useNavigate();
    const initColumns = [
        {
            id: "reviewId",
            header: "리뷰 ID",
            cell: ({ row }) => (`REV-2023122${row.index}`)
        },
        {
            id: "products",
            header: "상품",
            cell: ({ row }) => (`프리미엄 블루투스 이어폰-${row.index}`)
        },
        {
            id: "creator",
            header: "작성자",
            cell: ({ row }) => (`구매자${row.index}`)
        },
        {
            id: "rating",
            header: "평점",
            cell: () => (<Rating initialRating={3.0}
                                 readonly
                                 fullSymbol={<FontAwesomeIcon icon={faStarSolid} color="#facc15" size="lg" />}
                                 emptySymbol={<FontAwesomeIcon icon={faStarRegular} color="#facc15" size="lg" />}
            />  )
        },
        {
            id: "content",
            header: "내용",
            cell: () => ("음질이 좋고 배터리 수명이 오래갑니다."),
        },
        {
            id: "createDate",
            header: "작성일",
            cell: () => ("2025-12-19 17:00"),
        },
        {
            id: "adminButton",
            header: "처리",
            cell: () => (<Button variant={""} className={styles.detailButton} onClick={() => navigate("1")}>삭제</Button>)
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