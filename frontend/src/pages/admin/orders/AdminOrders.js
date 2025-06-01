import { Badge, Button, Card, Form, InputGroup, Stack } from "react-bootstrap";
import styles from "./AdminOrders.module.scss";
import FilterButtonGroup from "./FilterButtonGroup";
import FlexibleTable from "../../../shared/table/FlexibleTable";
import TableBackGroundCard from "../../../shared/TableBackGroundCard";
import { useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";

const API_BASE_URL =
  process.env.REACT_APP_API_URL ||
  process.env.REACT_APP_SERVER_URL ||
  "http://localhost:8080";

const AdminOrders = () => {
  const navigate = useNavigate();
  const [data, setData] = useState([]);
  const [checkedOrders, setCheckedOrders] = useState([]);
  const [activeFilter, setActiveFilter] = useState("ALL");
  const [searchTerm, setSearchTerm] = useState("");
  const [loading, setLoading] = useState(false);
  const [totalElements, setTotalElements] = useState(0);

  const initColumns = [
    {
      id: "orderId",
      header: "주문 번호",
      size: 200,
      cell: ({ row }) => {
        return (
          <Form className={"d-flex gap-2"}>
            {activeFilter === "PAID" || activeFilter === "INDELIVERY" ? (
              <Form.Check type={"checkbox"} checked={row.original.checked} />
            ) : null}
            {row.original.orderId}
          </Form>
        );
      },
    },
    {
      id: "orderPersonName",
      header: "주문자",
      cell: ({ row }) => row.original.customerName || `구매자${row.index}`,
    },
    {
      id: "products",
      header: "상품",
      cell: ({ row }) => (
        <div>
          <div className="fw-semibold">{row.original.products}</div>
          <small className="text-muted">{row.original.quantity}개</small>
        </div>
      ),
    },
    {
      id: "price",
      header: "결제 금액",
      cell: ({ row }) => `₩${(row.original.amount || 47000).toLocaleString()}`,
    },
    {
      id: "memberId",
      header: "회원 ID",
      cell: ({ row }) => row.original.memberId || "-",
    },
    {
      id: "groupBuyId",
      header: "공동구매 ID",
      cell: ({ row }) => row.original.groupBuyId || "-",
    },
    {
      id: "productId",
      header: "상품 ID",
      cell: ({ row }) => row.original.productId || "-",
    },
    {
      id: "orderDate",
      header: "주문 일시",
      cell: ({ row }) => row.original.orderDate || "2025-12-19 17:00",
    },
    {
      id: "status",
      header: "상태",
      cell: ({ row }) => {
        if (row.original.status === "BEFOREPAID") {
          return (
            <Badge
              bg=""
              style={{ backgroundColor: "#E0E7FF", color: "#3730A3" }}
            >
              결제 대기중
            </Badge>
          );
        } else if (row.original.status === "PAID") {
          return (
            <Badge
              bg=""
              style={{ backgroundColor: "#DCFCE7", color: "#166534" }}
            >
              결제 완료
            </Badge>
          );
        } else if (row.original.status === "INDELIVERY") {
          return (
            <Badge
              bg=""
              style={{ backgroundColor: "#F3E8FF", color: "#6B21A8" }}
            >
              배송 중
            </Badge>
          );
        } else if (row.original.status === "COMPLETED") {
          return (
            <Badge
              bg=""
              style={{ backgroundColor: "#DFDFDF", color: "#000000" }}
            >
              배송 완료
            </Badge>
          );
        } else if (row.original.status === "CANCELED") {
          return (
            <Badge
              bg=""
              style={{ backgroundColor: "#FEE2E2", color: "#991B1B" }}
            >
              취소
            </Badge>
          );
        }
      },
    },
    {
      id: "adminButton",
      header: "관리",
      cell: ({ row }) => (
        <span
          onClick={(e) => {
            e.stopPropagation(); // 행 클릭 이벤트 전파 방지
            navigate(`/admin/orders/${row.original.orderId}`);
          }}
          style={{
            color: "#000000",
            cursor: "pointer",
            fontSize: "14px",
          }}
        >
          상세
        </span>
      ),
    },
  ];

  // API 호출 함수들 - 모든 데이터 조회
  const fetchOrders = async (status = null, orderId = null) => {
    setLoading(true);
    try {
      let url = `${API_BASE_URL}/api/v1/orders`;
      const params = new URLSearchParams();

      if (status && status !== "ALL") {
        params.append("status", status);
      }

      if (orderId) {
        params.append("orderId", orderId);
      }

      // 모든 데이터를 가져오기 위해 큰 size 값 설정
      params.append("size", "10000");
      params.append("sort", "createdAt,DESC");

      url += `?${params.toString()}`;

      const response = await fetch(url, {
        credentials: "include",
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const result = await response.json();

      // API 응답이 성공인지 확인
      if (result.success && result.data && result.data.content) {
        // API 응답 데이터를 기존 형식에 맞게 변환
        const formattedData = result.data.content.map((order) => ({
          orderId: order.orderId,
          checked: false,
          status: order.status,
          customerName: order.nickname,
          products: order.productTitle,
          amount: order.totalPrice,
          orderDate: new Date(order.createdAt).toLocaleString("ko-KR", {
            year: "numeric",
            month: "2-digit",
            day: "2-digit",
            hour: "2-digit",
            minute: "2-digit",
          }),
          memberId: order.memberId,
          groupBuyId: order.groupBuyId,
          productId: order.productId,
          quantity: order.quantity,
          deliveryId: order.deliveryId, // 배송 ID 추가
        }));

        setData(formattedData);
        setTotalElements(result.data.totalElements);
      } else {
        throw new Error(result.message || "데이터 조회에 실패했습니다.");
      }
    } catch (error) {
      console.error("주문 데이터 조회 실패:", error);
      alert(`데이터 조회 실패: ${error.message}`);
      setData([]);
      setTotalElements(0);
    } finally {
      setLoading(false);
    }
  };

  // 컴포넌트 마운트 시 데이터 로드
  useEffect(() => {
    fetchOrders();
  }, []);

  useEffect(() => {
    setCheckedOrders(data.filter((item) => item.checked));
  }, [data]);

  const handleRowClick = (row) => {
    if (activeFilter === "PAID" || activeFilter === "INDELIVERY") {
      const newData = data.map((item) => {
        if (item.orderId === row.original.orderId) {
          return { ...item, checked: !item.checked };
        } else {
          return item;
        }
      });
      setData(newData);
    }
  };

  const handleClearChecked = () => {
    const newData = data.map((item) => {
      if (item.checked) {
        return { ...item, checked: false };
      } else {
        return item;
      }
    });
    setData(newData);
    setCheckedOrders([]);
  };

  const handleFilterChange = (clickedFilter) => {
    setActiveFilter(clickedFilter.status);
    setCheckedOrders([]);

    // API 호출로 필터링된 데이터 가져오기
    fetchOrders(clickedFilter.status);
  };

  const handleSearch = () => {
    if (searchTerm.trim()) {
      // 주문번호 검색 API 호출
      fetchOrders(activeFilter, searchTerm.trim());
    } else {
      // 검색어가 없으면 현재 필터 상태로 전체 조회
      fetchOrders(activeFilter);
    }
  };

  const handleSearchInputChange = (e) => {
    setSearchTerm(e.target.value);
    // 엔터키 처리
    if (e.key === "Enter") {
      handleSearch();
    }
  };

  // 배송 상태 일괄 변경 함수
  const handleCheckedChangeButtonClick = async () => {
    try {
      // 선택된 주문들의 deliveryId 추출
      const deliveryIds = checkedOrders
        .filter((order) => order.deliveryId) // deliveryId가 있는 주문만 필터링
        .map((order) => order.deliveryId);

      if (deliveryIds.length === 0) {
        alert("배송 정보가 없는 주문입니다.");
        return;
      }

      // 배송 상태 일괄 변경 API 호출
      const response = await fetch(`${API_BASE_URL}/api/v1/deliveries/batch`, {
        method: "PATCH",
        headers: {
          "Content-Type": "application/json",
        },
        credentials: "include",
        body: JSON.stringify({
          deliveryIds: deliveryIds,
        }),
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(
          errorData.message || `배송 상태 변경 실패: ${response.status}`
        );
      }

      const result = await response.json();
      console.log("배송 상태 일괄 변경 성공:", result);

      // 성공 시 현재 데이터 다시 로드
      fetchOrders(activeFilter, searchTerm || null);
      setCheckedOrders([]);

      alert(`${deliveryIds.length}개 주문의 배송 상태가 변경되었습니다.`);
    } catch (error) {
      console.error("배송 상태 변경 실패:", error);
      alert(`배송 상태 변경 중 오류가 발생했습니다: ${error.message}`);
    }
  };

  return (
    <Card className={"px-10"}>
      <Card.Header className={"border-0"}>
        <Stack direction={"horizontal"} className={"justify-content-between"}>
          <Stack>
            <h4>주문 관리</h4>
            <desc className={"text-gray-300"}>고객의 주문을 관리합니다.</desc>
          </Stack>
          <InputGroup className="me-3 gap-2" style={{ width: 360, height: 50 }}>
            <Form.Control
              type="text"
              placeholder="주문번호 검색"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              onKeyPress={handleSearchInputChange}
            />
            <Button
              className={styles.detailButton}
              variant=""
              type={"button"}
              onClick={handleSearch}
              disabled={loading}
            >
              {loading ? "검색 중..." : "검색"}
            </Button>
          </InputGroup>
        </Stack>
      </Card.Header>
      <Card.Body>
        <FilterButtonGroup
          activeFilter={activeFilter}
          handleChange={handleFilterChange}
        />

        {/* 총 개수 표시 */}
        <div className="mb-3 text-muted">
          총 {totalElements}개의 주문이 있습니다. {data.length}개 표시 중
        </div>

        {checkedOrders.length ? (
          <Stack
            direction={"horizontal"}
            gap={3}
            className={"m-2 p-2"}
            style={{ background: "#F9FAFB" }}
          >
            <span>{checkedOrders.length}개 선택됨</span>
            <span
              className={"fw-bold cursor-pointer"}
              onClick={handleClearChecked}
            >
              선택 취소
            </span>
            <Button
              variant={""}
              className={styles.detailButton}
              style={{ background: "white" }}
              onClick={handleCheckedChangeButtonClick}
            >
              {activeFilter === "PAID" ? "배송 중 처리" : "배송 완료 처리"}
            </Button>
          </Stack>
        ) : null}

        <TableBackGroundCard>
          {loading ? (
            <div className="text-center py-4">
              <div>데이터를 불러오는 중...</div>
            </div>
          ) : data.length === 0 ? (
            <div className="text-center py-4">
              <div>조회된 주문이 없습니다.</div>
            </div>
          ) : (
            <FlexibleTable
              initColumns={initColumns}
              data={data}
              rowProps={(row) => ({
                onClick: () => handleRowClick(row),
                style: { cursor: "pointer" },
              })}
            />
          )}
        </TableBackGroundCard>
      </Card.Body>
    </Card>
  );
};

export default AdminOrders;
