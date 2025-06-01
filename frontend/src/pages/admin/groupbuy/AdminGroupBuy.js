import { useState } from "react";

import { Badge, Card } from "react-bootstrap";
import FilterToggle from "./FilterToggle";
import FlexibleTable from "../../../shared/table/FlexibleTable";
import TableBackGroundCard from "../../../shared/TableBackGroundCard";
import { useNavigate } from "react-router-dom";
import { useApiQuery } from "../../../hooks/useApiQuery";
import axios from "axios";
import { dateFormat } from "../../../utils/utils";

// API 호출 함수
const fetchGroupBuyList = async (filter = null) => {
  let url = `/api/v1/groupBuy/list?size=1000`; // 모든 데이터를 가져오기 위해 큰 size 값 설정

  // 필터 적용
  if (filter && filter !== "전체") {
    const status = filter === "모집중" ? "ONGOING" : "CLOSED";
    url += `&status=${status}`;
  }

  const response = await axios.get(url);
  return response.data.data;
};

const AdminGroupBuy = () => {
  const navigate = useNavigate();
  const [activeFilter, setActiveFilter] = useState("전체");

  // useApiQuery를 사용하여 API 호출
  const { isLoading, data } = useApiQuery(
    ["groupBuyList", activeFilter],
    () => fetchGroupBuyList(activeFilter),
    {
      enabled: true,
    }
  );

  // 필터 변경 핸들러
  const handleFilterChange = (filter) => {
    setActiveFilter(filter);
  };

  // 상세 페이지 이동 핸들러
  const handleViewDetail = (id) => {
    navigate(`/admin/groupBuy/${id}`);
  };

  // 상품 페이지 이동 핸들러
  const handleGoToProductPage = (id) => {
    navigate(`/groupbuy/${id}`, "_blank");
  };

  // 상태 표시 변환 함수
  const getStatusDisplay = (status) => {
    if (status === "ONGOING") {
      return { text: "모집중", color: "danger" };
    } else if (status === "CLOSED") {
      return { text: "모집 종료", color: "success" };
    }
    return { text: status, color: "secondary" };
  };

  const initColumns = [
    {
      accessorKey: "product.title",
      header: "상품명",
      cell: ({ row }) => row.original.product?.title || "",
    },
    {
      accessorKey: "currentParticipantCount",
      header: "현재 인원",
      cell: ({ row }) => `${row.original.currentParticipantCount || 0}명`,
    },
    {
      accessorKey: "targetParticipants",
      header: "최소 인원",
      cell: ({ row }) => `${row.original.targetParticipants || 0}명`,
    },
    {
      accessorKey: "deadline",
      header: "마감 일시",
      cell: ({ row }) =>
        dateFormat(row.original.deadline, "yyyy-MM-dd HH:mm") || "",
    },
    {
      accessorKey: "status",
      header: "상태",
      cell: ({ row }) => {
        const status = getStatusDisplay(row.original.status);
        return <Badge bg={status.color}>{status.text}</Badge>;
      },
    },
    {
      id: "adminButton",
      header: "관리",
      cell: ({ row }) => (
        <span
          className={"cursor-pointer"}
          onClick={() => handleViewDetail(row.original.groupBuyId)}
        >
          상세
        </span>
      ),
    },
    {
      id: "productPage",
      header: "상품 페이지",
      cell: ({ row }) => (
        <span
          className={"cursor-pointer"}
          onClick={() => handleGoToProductPage(row.original.product?.productId)}
        >
          이동
        </span>
      ),
    },
  ];

  // 필터링된 데이터
  const filteredData =
    data?.content?.filter((item) => {
      if (activeFilter === "전체") return true;
      if (activeFilter === "모집중") return item.status === "ONGOING";
      if (activeFilter === "모집완료") return item.status === "CLOSED";
      return true;
    }) || [];

  return (
    <Card className={"px-10"}>
      <Card.Header className={"border-0"}>
        <h4>공동 구매 관리</h4>
        <desc className={"text-gray-300"}>
          진행 중인 모든 공동구매를 관리합니다.
        </desc>
      </Card.Header>
      <Card.Body>
        <div className="d-flex justify-content-start align-items-center mb-3">
          <FilterToggle active={activeFilter} setActive={handleFilterChange} />
        </div>
        <TableBackGroundCard>
          <FlexibleTable
            initColumns={initColumns}
            data={filteredData}
            isLoading={isLoading}
          />

          {/* 검색 결과 표시 */}
          {data && data.content && (
            <div className="d-flex justify-content-center mt-3">
              <div className="text-muted">
                {activeFilter !== "전체" ? (
                  <>
                    필터 결과: {filteredData.length}개의 공동구매 (상태: "
                    {activeFilter}")
                  </>
                ) : (
                  `총 ${data.totalElements}개의 공동구매가 있습니다.`
                )}
              </div>
            </div>
          )}
        </TableBackGroundCard>
      </Card.Body>
    </Card>
  );
};

export default AdminGroupBuy;
