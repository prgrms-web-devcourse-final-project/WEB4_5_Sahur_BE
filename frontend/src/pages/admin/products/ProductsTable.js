import FlexibleTable from "../../../shared/table/FlexibleTable";
import TableBackGroundCard from "../../../shared/TableBackGroundCard";
import { useApiQuery } from "../../../hooks/useApiQuery";
import { Button } from "react-bootstrap";
import styles from "./AdminProducts.module.scss";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { dateFormat } from "../../../utils/utils";

const fetchProducts = async (
  category = null,
  keyword = null,
  page = 0,
  size = 1000
) => {
  let url = `/api/v1/products?page=${page}&size=${size}`;

  // 카테고리 파라미터 추가 (categoryType 또는 keyword로 사용)
  if (category && category.trim()) {
    // 메인 카테고리인지 하위 카테고리인지 구분
    const mainCategories = [
      "FASHION_CLOTHES",
      "FASHION_ACCESSORY",
      "BEAUTY",
      "DIGITAL_APPLIANCE",
      "FURNITURE",
      "LIVING",
      "FOOD",
      "SPORTS",
      "CAR",
      "BOOK",
      "KIDS",
      "PET",
    ];

    if (mainCategories.includes(category.trim().toUpperCase())) {
      // 메인 카테고리로 검색
      url += `&category=${encodeURIComponent(category.trim())}`;
    } else {
      // 하위 카테고리(키워드)로 검색
      url += `&keyword=${encodeURIComponent(category.trim())}`;
    }
  }

  // 키워드 파라미터 추가
  if (keyword && keyword.trim()) {
    url += `&keyword=${encodeURIComponent(keyword.trim())}`;
  }

  const response = await axios.get(url);
  return response.data.data;
};

const ProductsTable = ({ searchQuery = "", category = "" }) => {
  const navigate = useNavigate();

  // 검색어나 카테고리가 변경될 때마다 API 재호출
  const { isLoading, isFetching, data, refetch } = useApiQuery(
    ["fetchProducts", category, searchQuery],
    () => fetchProducts(category, searchQuery),
    {
      enabled: true,
    }
  );

  const initColumns = [
    {
      accessorKey: "title",
      header: "상품명",
    },
    {
      accessorKey: "category",
      header: "카테고리",
      cell: ({ row }) => (
        <div>
          <div className="fw-semibold">
            {row.original.category.categoryType}
          </div>
          <div className="text-muted">{row.original.category.keyword}</div>
        </div>
      ),
    },
    {
      accessorKey: "price",
      header: "가격",
      cell: ({ getValue, row }) => row.original.price.toLocaleString() + "원",
    },
    {
      accessorKey: "dibCount",
      header: "찜 수",
      cell: ({ row }) => row.original.dibCount,
    },
    {
      accessorKey: "createdAt",
      header: "등록일",
      cell: ({ getValue, row }) =>
        dateFormat(row.original.createdAt, "yyyy-MM-dd"),
    },
    {
      id: "adminButton",
      header: "관리",
      cell: ({ row }) => (
        <Button
          variant={""}
          className={styles.detailButton}
          onClick={() => navigate(`${row.original.productId}`)}
        >
          상세
        </Button>
      ),
    },
  ];

  return (
    <TableBackGroundCard>
      <FlexibleTable
        initColumns={initColumns}
        data={data?.content || []}
        isLoading={isLoading || isFetching}
      />

      {/* 검색 결과 표시 */}
      {data && data.content && (
        <div className="d-flex justify-content-center mt-3">
          <div className="text-muted">
            {searchQuery || category ? (
              <>
                검색 결과: {data.totalElements}개의 상품
                {searchQuery && <span> (키워드: "{searchQuery}")</span>}
                {category && (
                  <span>
                    {" "}
                    (
                    {[
                      "FASHION_CLOTHES",
                      "FASHION_ACCESSORY",
                      "BEAUTY",
                      "DIGITAL_APPLIANCE",
                      "FURNITURE",
                      "LIVING",
                      "FOOD",
                      "SPORTS",
                      "CAR",
                      "BOOK",
                      "KIDS",
                      "PET",
                    ].includes(category.toUpperCase())
                      ? "카테고리"
                      : "하위카테고리"}
                    : "{category}")
                  </span>
                )}
              </>
            ) : (
              `총 ${data.totalElements}개의 상품이 있습니다.`
            )}
          </div>
        </div>
      )}
    </TableBackGroundCard>
  );
};

export default ProductsTable;
