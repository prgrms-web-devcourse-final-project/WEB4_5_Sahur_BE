import axios from "axios";
import {useApiQuery} from "../../../hooks/useApiQuery";
import {Badge, Button, Image, Stack} from "react-bootstrap";
import styles from "./AdminProducts.module.scss";
import FlexibleTable from "../../../shared/table/FlexibleTable";
import TableBackGroundCard from "../../../shared/TableBackGroundCard";
import {useNavigate} from "react-router-dom";

const fetchProductsRequest = async () => {
    const response = await axios.get(`/api/v1/productRequests/list`);
    return response.data.data;
};

const ProductsRequestsTable = () => {
    const navigate = useNavigate();

    const { isLoading, isFetching, data } = useApiQuery(
        ['fetchProducts'],
        () => fetchProductsRequest(),
    );
    const initColumns = [
        {
            accessorKey: "imageUrls",
            header: "상품 이미지",
            cell: ({ getValue }) => (
                <div className={"d-flex justify-content-center"}>
                    <Image rounded width={50} height={50} src={"https://i.pravatar.cc/150?img=60.jpg"} />
                </div>
            ),
        },
        {
            accessorKey: "title",
            header: "상품명",
        },
        {
            accessorKey: "categoryId",
            header: "카테고리",
        },
        {
            accessorKey: "createdAt",
            header: "등록일",
            cell: () => ("2025-06-12")
        },
        {
            accessorKey: "status",
            header: "상태",
            cell: ({ row }) => (
                <Badge bg="warning">승인 대기</Badge>
            ),
        },
        {
            id: "adminButton",
            header: "관리",
            cell: ({ row }) => (
                <Button variant={""} className={styles.detailButton} onClick={() => navigate(`${row.original.productId}`)}>상세</Button>
            ),
        }
    ];

    return (
        <TableBackGroundCard>
            <Stack direction={"horizontal"} gap={2} className={"mb-2"} >
                <Button variant={""} className={styles.detailButton} >전체</Button>
                <Button variant={""} className={styles.detailButton} >승인대기</Button>
                <Button variant={""} className={styles.detailButton} >거부</Button>
                <Button variant={""} className={styles.detailButton} >승인</Button>
            </Stack>
            <FlexibleTable initColumns={initColumns} data={data?.content || []} isLoading={isLoading || isFetching} />
            {/*<Paging page={queryParam.page || 1} handlePageClick={handlePageClick}*/}
            {/*        totalCount={data?.totalCount || 0}*/}
            {/*        pageLimit={queryParam.limit || 10}*/}
            {/*        handlePageLimitChange={handlePageLimitChange}*/}
            {/*/>*/}
        </TableBackGroundCard>
    );
}

export default ProductsRequestsTable;