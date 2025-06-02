"use client";

import { Card, Stack } from "react-bootstrap";
import SearchBox from "./SearchBox";
import PaymentList from "./PaymentList";
import OrderInfo from "./OrderInfo";
import PaymentDetail from "./PaymentDetail";
import styles from "./MyPagePayments.module.scss";
import { useState, useEffect } from "react";

const MyPagePayments = () => {
  const [open, setOpen] = useState(false);
  const [selectedItem, setSelectedItem] = useState();
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchQuery, setSearchQuery] = useState("");
  const [statusFilter, setStatusFilter] = useState("");
  const [sortOption, setSortOption] = useState("latest");
  const [pagination, setPagination] = useState({
    currentPage: 0,
    totalPages: 0,
    totalElements: 0,
    size: 5,
  });

  // 정렬 옵션을 Spring Boot Pageable 형식으로 변환
  const getSortParam = (sortOption) => {
    switch (sortOption) {
      case "latest":
        return "orderId,desc";
      case "oldest":
        return "orderId,asc";
      case "high":
        return "totalPrice,desc";
      case "low":
        return "totalPrice,asc";
      default:
        return "orderId,desc";
    }
  };

  const fetchOrders = async (
    page = 0,
    size = 5,
    search = "",
    status = "",
    sort = "latest"
  ) => {
    try {
      setLoading(true);
      const apiUrl = process.env.REACT_APP_API_URL || "http://localhost:8080";

      // 쿼리 파라미터 구성
      const params = new URLSearchParams({
        page: page.toString(),
        size: size.toString(),
        sort: getSortParam(sort),
      });

      if (status) {
        params.append("status", status);
      }

      // 검색어가 있으면 추가 (API가 지원하는지 확인 필요)
      if (search.trim()) {
        params.append("search", search.trim());
      }

      const url = `${apiUrl}/api/v1/orders/me?${params.toString()}`;
      console.log("API 요청 URL:", url);

      const response = await fetch(url, {
        method: "GET",
        credentials: "include", // 쿠키를 통한 토큰 전송
        headers: {
          "Content-Type": "application/json",
        },
      });

      if (!response.ok) {
        if (response.status === 401) {
          throw new Error("로그인이 필요합니다.");
        } else if (response.status === 403) {
          throw new Error("접근 권한이 없습니다.");
        } else {
          throw new Error(
            `서버 오류 (${response.status}): 주문 목록을 가져오는데 실패했습니다.`
          );
        }
      }

      const result = await response.json();
      console.log("API 응답:", result);

      if (result.success) {
        // API 응답에서 주문 목록 가져오기
        const orderList = result.data.content || [];

        // 검색어가 있고 API가 검색을 지원하지 않는 경우 클라이언트에서 필터링
        let filteredOrders = orderList;
        if (search.trim() && !params.has("search")) {
          const searchLower = search.toLowerCase();
          filteredOrders = orderList.filter(
            (order) =>
              order.orderId.toString().includes(searchLower) ||
              (order.productTitle &&
                order.productTitle.toLowerCase().includes(searchLower))
          );
        }

        setOrders(filteredOrders);
        setPagination({
          currentPage: result.data.number || 0,
          totalPages: result.data.totalPages || 0,
          totalElements: result.data.totalElements || 0,
          size: result.data.size || 5,
        });
        setError(null);
      } else {
        throw new Error(
          result.message || "주문 목록을 가져오는데 실패했습니다."
        );
      }
    } catch (err) {
      setError(err.message);
      console.error("주문 목록 조회 오류:", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchOrders(0, pagination.size, searchQuery, statusFilter, sortOption);
  }, []);

  const handleOpenClick = (item) => {
    setOpen((prev) => !prev);
    setSelectedItem(item);
  };

  const handlePageChange = (page) => {
    fetchOrders(page, pagination.size, searchQuery, statusFilter, sortOption);
  };

  const handleSearch = (query) => {
    setSearchQuery(query);
    setPagination((prev) => ({ ...prev, currentPage: 0 }));
    fetchOrders(0, pagination.size, query, statusFilter, sortOption);
  };

  const handleStatusFilter = (status) => {
    setStatusFilter(status);
    setPagination((prev) => ({ ...prev, currentPage: 0 }));
    fetchOrders(0, pagination.size, searchQuery, status, sortOption);
  };

  const handleSortChange = (sort) => {
    setSortOption(sort);
    fetchOrders(
      pagination.currentPage,
      pagination.size,
      searchQuery,
      statusFilter,
      sort
    );
  };

  const handleRefresh = () => {
    fetchOrders(
      pagination.currentPage,
      pagination.size,
      searchQuery,
      statusFilter,
      sortOption
    );
  };

  if (loading) {
    return (
      <Card className={"p-4"}>
        <Card.Body>
          <div className="text-center">
            <div className="spinner-border" role="status">
              <span className="visually-hidden">로딩 중...</span>
            </div>
            <p className="mt-2">주문 내역을 불러오는 중...</p>
          </div>
        </Card.Body>
      </Card>
    );
  }

  if (error) {
    return (
      <Card className={"p-4"}>
        <Card.Body>
          <div className="text-center text-danger">
            <h5>오류가 발생했습니다</h5>
            <p>{error}</p>
            <div className="mt-3">
              <button className="btn btn-primary me-2" onClick={handleRefresh}>
                다시 시도
              </button>
              <button
                className="btn btn-outline-secondary"
                onClick={() => {
                  setSearchQuery("");
                  setStatusFilter("");
                  setSortOption("latest");
                  fetchOrders(0, 5, "", "", "latest");
                }}
              >
                초기화
              </button>
            </div>
          </div>
        </Card.Body>
      </Card>
    );
  }

  return (
    <Card className={"p-4"}>
      <Card.Body>
        <Stack direction={"horizontal"} gap={5}>
          <Stack
            gap={2}
            className={`${styles.main} ${open ? styles.mainShrink : ""}`}
          >
            <SearchBox
              onSearch={handleSearch}
              onStatusFilter={handleStatusFilter}
              onSortChange={handleSortChange}
              onRefresh={handleRefresh}
              searchQuery={searchQuery}
              statusFilter={statusFilter}
              sortOption={sortOption}
            />
            <PaymentList
              orders={orders}
              pagination={pagination}
              handleOpenClick={handleOpenClick}
              onPageChange={handlePageChange}
            />
          </Stack>
          {open && (
            <Stack className={styles.sidebar} style={{ background: "#fff" }}>
              <OrderInfo selectedItem={selectedItem} />
              <PaymentDetail selectedItem={selectedItem} />
            </Stack>
          )}
        </Stack>
      </Card.Body>
    </Card>
  );
};

export default MyPagePayments;
