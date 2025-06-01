import { useState } from "react";
import { Button, Badge, Tabs, Tab } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import styles from "./AdminProducts.module.scss";
import axios from "axios";
import FlexibleTable from "../../../shared/table/FlexibleTable";
import TableBackGroundCard from "../../../shared/TableBackGroundCard";
import { useApiQuery } from "../../../hooks/useApiQuery";
import { dateFormat } from "../../../utils/utils";

const fetchProductRequests = async (status = null) => {
  let url = `/api/v1/productRequests/list?page=0&size=1000`;
  if (status) {
    url += `&status=${status}`;
  }

  const response = await axios.get(url);
  return response.data.data;
};

const ProductsRequestsTable = () => {
  const navigate = useNavigate();
  const [activeStatusTab, setActiveStatusTab] = useState("all");

  // API 쿼리 훅 사용
  const { isLoading, isFetching, data, refetch } = useApiQuery(
    ["fetchProductRequests", activeStatusTab],
    () => {
      switch (activeStatusTab) {
        case "waiting":
          return fetchProductRequests("WAITING");
        case "approved":
          return fetchProductRequests("APPROVED");
        case "rejected":
          return fetchProductRequests("REJECTED");
        default:
          return fetchProductRequests();
      }
    },
    {
      enabled: true,
    }
  );

  // 카테고리 타입 한글 변환
  const getCategoryLabel = (categoryType) => {
    const categoryMap = {
      FASHION_CLOTHES: "패션 의류",
      FASHION_ACCESSORY: "패션 액세서리",
      BEAUTY: "뷰티",
      DIGITAL_APPLIANCE: "디지털/가전",
      FURNITURE: "가구",
      LIVING: "리빙",
      FOOD: "식품",
      SPORTS: "스포츠",
      CAR: "자동차",
      BOOK: "도서",
      KIDS: "키즈",
      PET: "반려동물",
    };
    return categoryMap[categoryType] || categoryType;
  };

  // 상태에 따른 배지 색상 및 텍스트
  const getStatusBadge = (status) => {
    switch (status) {
      case "WAITING":
        return (
          <Badge bg="warning" text="dark">
            승인대기
          </Badge>
        );
      case "APPROVED":
        return <Badge bg="success">승인</Badge>;
      case "REJECTED":
        return <Badge bg="danger">거부</Badge>;
      default:
        return <Badge bg="secondary">알 수 없음</Badge>;
    }
  };

  // 탭 변경 핸들러
  const handleStatusTabSelect = (key) => {
    setActiveStatusTab(key);
  };

  // 상세 페이지로 이동
  const handleDetailClick = (requestId) => {
    console.log("상세 페이지로 이동: requestId =", requestId);
    if (!requestId) {
      console.error("요청 ID가 없습니다!");
      alert("요청 ID가 없어 상세 페이지로 이동할 수 없습니다.");
      return;
    }
    navigate(`/admin/products/requests/${requestId}`);
  };

  // 테이블 컬럼 정의
  const initColumns = [
    {
      accessorKey: "productRequestId",
      header: "번호",
      cell: ({ row }) => {
        const id = row.original.productRequestId;
        console.log("행 데이터:", row.original);
        return id || "ID 없음";
      },
    },
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
            {getCategoryLabel(row.original.category?.categoryType)}
          </div>
          <div className="text-muted small">
            {row.original.category?.keyword}
          </div>
        </div>
      ),
    },
    {
      accessorKey: "createdAt",
      header: "등록일",
      cell: ({ row }) => dateFormat(row.original.createdAt, "yyyy-MM-dd"),
    },
    {
      accessorKey: "status",
      header: "상태",
      cell: ({ row }) => getStatusBadge(row.original.status),
    },
    {
      id: "adminButton",
      header: "관리",
      cell: ({ row }) => (
        <Button
          variant=""
          className={styles.detailButton}
          onClick={() => handleDetailClick(row.original.productRequestId)}
        >
          상세
        </Button>
      ),
    },
  ];

  return (
    <div>
      {/* 상태별 필터 탭 */}
      <div className="mb-4">
        <Tabs activeKey={activeStatusTab} onSelect={handleStatusTabSelect}>
          <Tab eventKey="all" title="전체" />
          <Tab eventKey="waiting" title="승인 대기" />
          <Tab eventKey="rejected" title="거부" />
          <Tab eventKey="approved" title="승인" />
        </Tabs>
      </div>

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
              {activeStatusTab !== "all" ? (
                <>
                  {activeStatusTab === "waiting" && "승인 대기"}
                  {activeStatusTab === "approved" && "승인"}
                  {activeStatusTab === "rejected" && "거부"} 상태:{" "}
                  {data.totalElements}개의 요청
                </>
              ) : (
                `총 ${data.totalElements}개의 상품 요청이 있습니다.`
              )}
            </div>
          </div>
        )}
      </TableBackGroundCard>
    </div>
  );
};

export default ProductsRequestsTable;
