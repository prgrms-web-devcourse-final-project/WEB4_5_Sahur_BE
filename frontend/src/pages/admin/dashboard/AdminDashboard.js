import { useState, useEffect, useRef, useCallback } from "react";
import { useNavigate } from "react-router-dom"; // Add this import
import { Card, Stack, Spinner, Alert, ListGroup } from "react-bootstrap";
import axios from "axios";

const AdminDashboard = () => {
  const navigate = useNavigate(); // Add this line
  const [monthlySales, setMonthlySales] = useState(0);
  const [ongoingGroupBuys, setOngoingGroupBuys] = useState(0);
  const [pendingProducts, setPendingProducts] = useState(0);
  const [deliveryCount, setDeliveryCount] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // 승인 대기 상품 관련 상태
  const [pendingProductsList, setPendingProductsList] = useState([]);
  const [currentPage, setCurrentPage] = useState(1);
  const [isLoadingMore, setIsLoadingMore] = useState(false);
  const [hasMoreProducts, setHasMoreProducts] = useState(true);
  const scrollContainerRef = useRef(null);

  // 오늘 마감 예정 공동 구매 관련 상태
  const [closingGroupBuysList, setClosingGroupBuysList] = useState([]);
  const [closingCurrentPage, setClosingCurrentPage] = useState(1);
  const [isLoadingMoreClosing, setIsLoadingMoreClosing] = useState(false);
  const [hasMoreClosingGroupBuys, setHasMoreClosingGroupBuys] = useState(true);
  const closingScrollContainerRef = useRef(null);

  // 숫자를 한국 원화 형식으로 포맷
  const formatCurrency = (amount) => {
    return new Intl.NumberFormat("ko-KR", {
      style: "currency",
      currency: "KRW",
    }).format(amount);
  };

  // 이번 달 총 매출 가져오기
  const fetchMonthlySales = async () => {
    try {
      const response = await axios.get("/api/v1/orders/monthly-sales", {
        withCredentials: true,
      });

      console.log("Monthly sales data:", response.data);

      if (response.data.success && response.data.data) {
        setMonthlySales(response.data.data);
      } else {
        setMonthlySales(0);
      }
    } catch (err) {
      console.error("Monthly sales fetch error:", err);
      throw err;
    }
  };

  // 진행 중인 공동구매 수 가져오기
  const fetchOngoingGroupBuys = async () => {
    try {
      const response = await axios.get("/api/v1/groupBuy/list/onGoing", {
        withCredentials: true,
      });

      console.log("Ongoing group buys data:", response.data);

      if (
        response.data.success &&
        response.data.data &&
        response.data.data.content
      ) {
        setOngoingGroupBuys(response.data.data.content.length);
      } else {
        setOngoingGroupBuys(0);
      }
    } catch (err) {
      console.error("Ongoing group buys fetch error:", err);
      throw err;
    }
  };

  // 승인 대기 상품 수 가져오기
  const fetchPendingProducts = async () => {
    try {
      const response = await axios.get(
        "/api/v1/productRequests/list?status=WAITING",
        {
          withCredentials: true,
        }
      );

      console.log("Pending products data:", response.data);

      if (
        response.data.success &&
        response.data.data &&
        response.data.data.content
      ) {
        // 실제 API 응답에 맞춰 data.content 배열의 길이를 사용
        setPendingProducts(response.data.data.content.length);
      } else {
        setPendingProducts(0);
      }
    } catch (err) {
      console.error("Pending products fetch error:", err);
      throw err;
    }
  };

  // 승인 대기 상품 목록 가져오기 (페이지네이션)
  const fetchPendingProductsList = async (page = 1, reset = false) => {
    try {
      if (page === 1) setIsLoadingMore(true);

      const response = await axios.get(
        `/api/v1/productRequests/list?status=WAITING&page=${page}&limit=5`,
        {
          withCredentials: true,
        }
      );

      console.log("Pending products list data:", response.data);

      if (response.data.success && response.data.data) {
        // API 응답 구조에 따라 처리 (content 배열이 있는 경우와 직접 배열인 경우 모두 대응)
        const productsList = response.data.data.content || response.data.data;

        if (Array.isArray(productsList)) {
          if (reset || page === 1) {
            setPendingProductsList(productsList);
          } else {
            setPendingProductsList((prev) => [...prev, ...productsList]);
          }

          // 더 이상 불러올 데이터가 없는지 확인
          setHasMoreProducts(productsList.length === 5);
        } else {
          if (reset || page === 1) {
            setPendingProductsList([]);
          }
          setHasMoreProducts(false);
        }
      } else {
        if (reset || page === 1) {
          setPendingProductsList([]);
        }
        setHasMoreProducts(false);
      }
    } catch (err) {
      console.error("Pending products list fetch error:", err);
      if (reset || page === 1) {
        setPendingProductsList([]);
      }
      setHasMoreProducts(false);
    } finally {
      setIsLoadingMore(false);
    }
  };

  // 오늘 마감 예정 공동 구매 목록 가져오기 (페이지네이션)
  const fetchClosingGroupBuysList = async (page = 1, reset = false) => {
    try {
      if (page === 1) setIsLoadingMoreClosing(true);

      const response = await axios.get(
        `/api/v1/groupBuy/closing?page=${page}&limit=5`,
        {
          withCredentials: true,
        }
      );

      console.log("Closing group buys list data:", response.data);

      if (response.data.success && response.data.data) {
        // API 응답 구조에 맞춰 data.content 배열을 사용
        const groupBuysList = response.data.data.content || [];

        if (Array.isArray(groupBuysList)) {
          if (reset || page === 1) {
            setClosingGroupBuysList(groupBuysList);
          } else {
            setClosingGroupBuysList((prev) => [...prev, ...groupBuysList]);
          }

          // 더 이상 불러올 데이터가 없는지 확인 (API 응답의 last 필드 또는 배열 길이로 판단)
          const isLast = response.data.data.last || groupBuysList.length < 5;
          setHasMoreClosingGroupBuys(!isLast);
        } else {
          if (reset || page === 1) {
            setClosingGroupBuysList([]);
          }
          setHasMoreClosingGroupBuys(false);
        }
      } else {
        if (reset || page === 1) {
          setClosingGroupBuysList([]);
        }
        setHasMoreClosingGroupBuys(false);
      }
    } catch (err) {
      console.error("Closing group buys list fetch error:", err);
      if (reset || page === 1) {
        setClosingGroupBuysList([]);
      }
      setHasMoreClosingGroupBuys(false);
    } finally {
      setIsLoadingMoreClosing(false);
    }
  };

  // 배송 중인 주문 수 가져오기
  const fetchDeliveryCount = async () => {
    try {
      const response = await axios.get("/api/v1/deliveries/count", {
        withCredentials: true,
      });

      console.log("Delivery count data:", response.data);

      if (response.data.success && response.data.data) {
        setDeliveryCount(response.data.data);
      } else {
        setDeliveryCount(0);
      }
    } catch (err) {
      console.error("Delivery count fetch error:", err);
      throw err;
    }
  };

  // 최근 주문 내역 관련 상태
  const [recentOrdersList, setRecentOrdersList] = useState([]);
  const [ordersCurrentPage, setOrdersCurrentPage] = useState(1);
  const [isLoadingMoreOrders, setIsLoadingMoreOrders] = useState(false);
  const [hasMoreOrders, setHasMoreOrders] = useState(true);
  const ordersScrollContainerRef = useRef(null);

  // 최근 주문 내역 가져오기 (페이지네이션)
  const fetchRecentOrdersList = async (page = 1, reset = false) => {
    try {
      if (page === 1) setIsLoadingMoreOrders(true);

      const response = await axios.get(
        `/api/v1/orders?page=${page}&limit=5&sort=createdAt&order=desc`,
        {
          withCredentials: true,
        }
      );

      console.log("Recent orders list data:", response.data);

      if (response.data.success && response.data.data) {
        // API 응답 구조에 맞춰 data.content 배열을 사용
        const ordersList = response.data.data.content || [];

        if (Array.isArray(ordersList)) {
          if (reset || page === 1) {
            setRecentOrdersList(ordersList);
          } else {
            setRecentOrdersList((prev) => [...prev, ...ordersList]);
          }

          // 더 이상 불러올 데이터가 없는지 확인 (API 응답의 last 필드 사용)
          const isLast = response.data.data.last || ordersList.length < 5;
          setHasMoreOrders(!isLast);
        } else {
          if (reset || page === 1) {
            setRecentOrdersList([]);
          }
          setHasMoreOrders(false);
        }
      } else {
        if (reset || page === 1) {
          setRecentOrdersList([]);
        }
        setHasMoreOrders(false);
      }
    } catch (err) {
      console.error("Recent orders list fetch error:", err);
      if (reset || page === 1) {
        setRecentOrdersList([]);
      }
      setHasMoreOrders(false);
    } finally {
      setIsLoadingMoreOrders(false);
    }
  };

  // 주문 상태 표시 함수 - 실제 API 응답에 맞춰 수정
  const getOrderStatusDisplay = (status) => {
    switch (status) {
      case "PAID":
        return { text: "결제완료", color: "primary" };
      case "COMPLETED":
        return { text: "주문완료", color: "success" };
      case "INDELIVERY":
        return { text: "배송중", color: "info" };
      case "DELIVERED":
        return { text: "배송완료", color: "success" };
      case "CANCELED":
        return { text: "취소됨", color: "danger" };
      case "REFUNDED":
        return { text: "환불됨", color: "warning" };
      default:
        return { text: "확인필요", color: "secondary" };
    }
  };

  // 주문 상세 페이지로 이동하는 함수 (누락된 함수 추가)
  const handleOrderDetail = (orderId) => {
    // 관리자 주문 상세 페이지로 이동
    window.location.href = `/admin/orders/${orderId}`;
    // 또는 React Router 사용시: navigate(`/admin/orders/${orderId}`);
  };

  // 모든 데이터 가져오기
  const fetchAllData = async () => {
    try {
      setLoading(true);
      setError(null);

      // 모든 API를 병렬로 호출
      await Promise.all([
        fetchMonthlySales(),
        fetchOngoingGroupBuys(),
        fetchPendingProducts(),
        fetchDeliveryCount(),
        fetchRecentOrdersList(),
      ]);

      // 승인 대기 상품 목록도 가져오기
      await fetchPendingProductsList(1, true);

      // 오늘 마감 예정 공동 구매 목록도 가져오기
      await fetchClosingGroupBuysList(1, true);
    } catch (err) {
      console.error("Data fetch error:", err);
      setError(err.response?.data?.message || err.message);
    } finally {
      setLoading(false);
    }
  };

  // 무한 스크롤 핸들러 (승인 대기 상품)
  const handleScroll = useCallback(() => {
    if (!scrollContainerRef.current || isLoadingMore || !hasMoreProducts)
      return;

    const { scrollTop, scrollHeight, clientHeight } =
      scrollContainerRef.current;

    if (scrollTop + clientHeight >= scrollHeight - 10) {
      const nextPage = currentPage + 1;
      setCurrentPage(nextPage);
      fetchPendingProductsList(nextPage);
    }
  }, [currentPage, isLoadingMore, hasMoreProducts]);

  // 무한 스크롤 핸들러 (오늘 마감 예정 공동 구매)
  const handleClosingScroll = useCallback(() => {
    if (
      !closingScrollContainerRef.current ||
      isLoadingMoreClosing ||
      !hasMoreClosingGroupBuys
    )
      return;

    const { scrollTop, scrollHeight, clientHeight } =
      closingScrollContainerRef.current;

    if (scrollTop + clientHeight >= scrollHeight - 10) {
      const nextPage = closingCurrentPage + 1;
      setClosingCurrentPage(nextPage);
      fetchClosingGroupBuysList(nextPage);
    }
  }, [closingCurrentPage, isLoadingMoreClosing, hasMoreClosingGroupBuys]);

  // 상품 상세 페이지로 이동
  const handleProductDetail = (productId) => {
    // 관리자 - 공동 구매 요청 처리 페이지로 이동
    // React Router를 사용하는 경우
    if (typeof navigate !== "undefined") {
      navigate(`/admin/products/requests/${productId}`);
    } else {
      // window.location을 사용하는 경우
      window.location.href = `/admin/products/requests/${productId}`;
    }
  };

  // 공동 구매 상품 페이지로 이동
  const handleGroupBuyDetail = (groupBuyId) => {
    // 공동 구매 상품 페이지로 이동
    window.location.href = `/groupbuy/${groupBuyId}`;
    // 또는 React Router 사용시: navigate(`/groupbuy/${groupBuyId}`);
  };

  // 컴포넌트 마운트 시 데이터 로드
  useEffect(() => {
    fetchAllData();
  }, []);

  // 스크롤 이벤트 리스너 등록
  useEffect(() => {
    const scrollContainer = scrollContainerRef.current;
    const closingScrollContainer = closingScrollContainerRef.current;

    if (scrollContainer) {
      scrollContainer.addEventListener("scroll", handleScroll);
    }
    if (closingScrollContainer) {
      closingScrollContainer.addEventListener("scroll", handleClosingScroll);
    }

    return () => {
      if (scrollContainer) {
        scrollContainer.removeEventListener("scroll", handleScroll);
      }
      if (closingScrollContainer) {
        closingScrollContainer.removeEventListener(
          "scroll",
          handleClosingScroll
        );
      }
    };
  }, [handleScroll, handleClosingScroll]);

  if (loading) {
    return (
      <div
        className="d-flex justify-content-center align-items-center"
        style={{ height: "400px" }}
      >
        <Spinner animation="border" role="status">
          <span className="visually-hidden">로딩 중...</span>
        </Spinner>
      </div>
    );
  }

  if (error) {
    return (
      <Alert variant="danger" className="m-3">
        데이터를 불러오는 중 오류가 발생했습니다: {error}
        <br />
        <span
          className="text-dark mt-2"
          style={{ cursor: "pointer" }}
          onClick={fetchAllData}
        >
          다시 시도
        </span>
      </Alert>
    );
  }

  return (
    <Stack direction={"vertical"}>
      <Stack direction={"horizontal"} gap={3} className={"m-3"}>
        <Card className={"w-25"}>
          <Card.Body className="p-4">
            <div className="d-flex justify-content-between align-items-center">
              <div>
                <h6 className="text-black mb-0">이번 달 총 매출</h6>
                <h4 className="mb-0 text-black">
                  {formatCurrency(monthlySales)}
                </h4>
              </div>
            </div>
          </Card.Body>
        </Card>
        <Card className={"w-25"}>
          <Card.Body className="p-4">
            <div className="d-flex justify-content-between align-items-center">
              <div>
                <h6 className="text-black mb-0">진행 중인 공동구매</h6>
                <h4 className="mb-0 text-black">{ongoingGroupBuys}</h4>
              </div>
            </div>
          </Card.Body>
        </Card>
        <Card className={"w-25"}>
          <Card.Body className="p-4">
            <div className="d-flex justify-content-between align-items-center">
              <div>
                <h6 className="text-black mb-0">승인 대기 상품</h6>
                <h4 className="mb-0 text-black">{pendingProducts}</h4>
              </div>
            </div>
          </Card.Body>
        </Card>
        <Card className={"w-25"}>
          <Card.Body className="p-4">
            <div className="d-flex justify-content-between align-items-center">
              <div>
                <h6 className="text-black mb-0">배송 중</h6>
                <h4 className="mb-0 text-black">{deliveryCount}</h4>
              </div>
            </div>
          </Card.Body>
        </Card>
      </Stack>
      <Stack direction={"horizontal"} gap={3} className={"m-3"}>
        <Card style={{ width: "60%" }}>
          <Card.Header>
            <h5 className="mb-0">승인 대기 상품</h5>
          </Card.Header>
          <Card.Body
            style={{ maxHeight: "400px", overflowY: "auto" }}
            ref={scrollContainerRef}
          >
            {pendingProductsList.length === 0 ? (
              <div className="text-center text-muted py-4">
                승인 대기 중인 상품이 없습니다.
              </div>
            ) : (
              <>
                <ListGroup variant="flush">
                  {pendingProductsList.map((product, index) => (
                    <ListGroup.Item
                      key={product.productRequestId || index}
                      className="d-flex justify-content-between align-items-center"
                    >
                      <div>
                        <h6 className="mb-1">
                          {product.title || `상품 요청 ${index + 1}`}
                        </h6>
                        <small className="text-muted">
                          카테고리: {product.category?.categoryType || "미분류"}
                          {product.category?.keyword &&
                            ` (${product.category.keyword})`}{" "}
                          | 요청일:{" "}
                          {product.createdAt
                            ? new Date(product.createdAt).toLocaleDateString(
                                "ko-KR"
                              )
                            : "정보 없음"}
                        </small>
                        <div className="mt-1">
                          <small className="text-secondary">
                            상태:{" "}
                            <span
                              className={`badge ${
                                product.status === "WAITING"
                                  ? "bg-warning"
                                  : product.status === "APPROVED"
                                  ? "bg-success"
                                  : "bg-danger"
                              }`}
                            >
                              {product.status === "WAITING"
                                ? "승인 대기"
                                : product.status === "APPROVED"
                                ? "승인됨"
                                : "거절됨"}
                            </span>
                          </small>
                        </div>
                      </div>
                      <span
                        className="text-dark"
                        style={{ cursor: "pointer" }}
                        onClick={() =>
                          handleProductDetail(product.productRequestId)
                        }
                      >
                        상세
                      </span>
                    </ListGroup.Item>
                  ))}
                </ListGroup>
                {isLoadingMore && (
                  <div className="text-center py-3">
                    <Spinner animation="border" size="sm">
                      <span className="visually-hidden">로딩 중...</span>
                    </Spinner>
                  </div>
                )}
                {!hasMoreProducts && pendingProductsList.length > 0 && (
                  <div className="text-center text-muted py-3">
                    <small>모든 상품을 불러왔습니다.</small>
                  </div>
                )}
              </>
            )}
          </Card.Body>
        </Card>
        <Card style={{ width: "40%" }}>
          <Card.Header>
            <h5 className="mb-0">오늘 마감 예정 공동 구매</h5>
          </Card.Header>
          <Card.Body
            style={{ maxHeight: "400px", overflowY: "auto" }}
            ref={closingScrollContainerRef}
          >
            {closingGroupBuysList.length === 0 ? (
              <div className="text-center text-muted py-4">
                오늘 마감 예정인 공동 구매가 없습니다.
              </div>
            ) : (
              <>
                <div className="d-flex flex-column gap-3">
                  {closingGroupBuysList.map((groupBuy, index) => (
                    <div
                      key={groupBuy.id || index}
                      className="d-flex align-items-center p-2 border rounded cursor-pointer"
                      style={{ cursor: "pointer" }}
                      onClick={() => handleGroupBuyDetail(groupBuy.id)}
                    >
                      <div
                        className="me-3"
                        style={{ minWidth: "60px", height: "60px" }}
                      >
                        <img
                          src={
                            groupBuy.imageUrl ||
                            groupBuy.productImage ||
                            "/api/placeholder/60/60" ||
                            "/placeholder.svg"
                          }
                          alt={
                            groupBuy.name ||
                            groupBuy.productName ||
                            "상품 이미지"
                          }
                          className="img-fluid rounded"
                          style={{
                            width: "60px",
                            height: "60px",
                            objectFit: "cover",
                            backgroundColor: "#f8f9fa",
                          }}
                          onError={(e) => {
                            e.target.src = "/api/placeholder/60/60";
                          }}
                        />
                      </div>
                      <div className="flex-grow-1">
                        <h6 className="mb-1 text-truncate">
                          {groupBuy.name ||
                            groupBuy.productName ||
                            `공동구매 ${index + 1}`}
                        </h6>
                        <small className="text-muted d-block">
                          마감:{" "}
                          {groupBuy.endDate
                            ? new Date(groupBuy.endDate).toLocaleString("ko-KR")
                            : "오늘"}
                        </small>
                        <small className="text-success">
                          참여:{" "}
                          {groupBuy.currentParticipants ||
                            groupBuy.participantCount ||
                            0}
                          명 /
                          {groupBuy.targetParticipants ||
                            groupBuy.maxParticipants ||
                            0}
                          명
                        </small>
                        {groupBuy.price && (
                          <div className="mt-1">
                            <small className="text-primary fw-bold">
                              {new Intl.NumberFormat("ko-KR").format(
                                groupBuy.price
                              )}
                              원
                            </small>
                          </div>
                        )}
                      </div>
                    </div>
                  ))}
                </div>
                {isLoadingMoreClosing && (
                  <div className="text-center py-3">
                    <Spinner animation="border" size="sm">
                      <span className="visually-hidden">로딩 중...</span>
                    </Spinner>
                  </div>
                )}
                {!hasMoreClosingGroupBuys &&
                  closingGroupBuysList.length > 0 && (
                    <div className="text-center text-muted py-3">
                      <small>모든 공동구매를 불러왔습니다.</small>
                    </div>
                  )}
              </>
            )}
          </Card.Body>
        </Card>
      </Stack>
      <Card className={"m-3"} style={{ width: "calc(100% - 1.5rem)" }}>
        <Card.Header>
          <h5 className="mb-0">최근 주문 내역</h5>
        </Card.Header>
        <Card.Body
          style={{ maxHeight: "500px", overflowY: "auto", padding: "0" }}
          ref={ordersScrollContainerRef}
        >
          {recentOrdersList.length === 0 ? (
            <div className="text-center text-muted py-4">
              최근 주문 내역이 없습니다.
            </div>
          ) : (
            <>
              <div className="table-responsive">
                <table className="table table-hover mb-0">
                  <thead className="table-light sticky-top">
                    <tr>
                      <th scope="col">주문번호</th>
                      <th scope="col">구매자</th>
                      <th scope="col">상품</th>
                      <th scope="col">결제금액</th>
                      <th scope="col">주문일시</th>
                      <th scope="col">상태</th>
                      <th scope="col">관리</th>
                    </tr>
                  </thead>
                  <tbody>
                    {recentOrdersList.map((order, index) => {
                      const statusDisplay = getOrderStatusDisplay(order.status);
                      return (
                        <tr key={order.orderId || index}>
                          <td>
                            <span className="fw-bold text-primary">
                              {order.orderId ||
                                `ORD-${new Date().getFullYear()}${String(
                                  index + 1
                                ).padStart(3, "0")}`}
                            </span>
                          </td>
                          <td>{order.nickname || "구매자" + (index + 1)}</td>
                          <td>
                            <div style={{ maxWidth: "200px" }}>
                              <span className="text-truncate d-block">
                                {order.productTitle || "상품명"}
                              </span>
                              {order.quantity > 1 && (
                                <small className="text-muted">
                                  수량: {order.quantity}개
                                </small>
                              )}
                            </div>
                          </td>
                          <td>
                            <span className="fw-bold">
                              ₩
                              {new Intl.NumberFormat("ko-KR").format(
                                order.totalPrice || 0
                              )}
                            </span>
                          </td>
                          <td>
                            <div>
                              {order.createdAt
                                ? new Date(order.createdAt).toLocaleDateString(
                                    "ko-KR"
                                  )
                                : new Date().toLocaleDateString("ko-KR")}
                            </div>
                            <small className="text-muted">
                              {order.createdAt
                                ? new Date(order.createdAt).toLocaleTimeString(
                                    "ko-KR",
                                    {
                                      hour: "2-digit",
                                      minute: "2-digit",
                                    }
                                  )
                                : new Date().toLocaleTimeString("ko-KR", {
                                    hour: "2-digit",
                                    minute: "2-digit",
                                  })}
                            </small>
                          </td>
                          <td>
                            <span className={`badge bg-${statusDisplay.color}`}>
                              {statusDisplay.text}
                            </span>
                          </td>
                          <td>
                            <span
                              className="text-dark"
                              style={{ cursor: "pointer" }}
                              onClick={() => handleOrderDetail(order.orderId)}
                            >
                              상세
                            </span>
                          </td>
                        </tr>
                      );
                    })}
                  </tbody>
                </table>
              </div>
              {isLoadingMoreOrders && (
                <div className="text-center py-3">
                  <Spinner animation="border" size="sm">
                    <span className="visually-hidden">로딩 중...</span>
                  </Spinner>
                </div>
              )}
              {!hasMoreOrders && recentOrdersList.length > 0 && (
                <div className="text-center text-muted py-3">
                  <small>모든 주문을 불러왔습니다.</small>
                </div>
              )}
            </>
          )}
        </Card.Body>
      </Card>
    </Stack>
  );
};

export default AdminDashboard;
