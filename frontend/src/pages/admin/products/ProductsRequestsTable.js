import axios from "axios";
import {useApiQuery} from "../../../hooks/useApiQuery";
import {Button} from "react-bootstrap";
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
            accessorKey: "title",
            header: "상품명",
        },
        {
            accessorKey: "categoryId",
            header: "카테고리",
        },
        {
            accessorKey: "price",
            header: "가격",
        },
        {
            accessorKey: "createdAt",
            header: "등록일",
        },
        {
            accessorKey: "status",
            header: "상태",
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