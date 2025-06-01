import { useState, useEffect } from "react";
import { Card, Stack } from "react-bootstrap";

// API 기본 URL 설정
const API_BASE_URL = process.env.REACT_APP_API_URL || "http://localhost:8080";

// API 요청 헬퍼 함수
const apiRequest = async (endpoint) => {
  const response = await fetch(`${API_BASE_URL}${endpoint}`, {
    method: "GET",
    credentials: "include", // 쿠키 포함
    headers: {
      "Content-Type": "application/json",
    },
  });
  if (!response.ok) {
    throw new Error(`API 요청 실패: ${response.status}`);
  }
  return response.json();
};

const MyPageDashboard = () => {
  const [groupBuyData, setGroupBuyData] = useState([]);
  const [orderData, setOrderData] = useState([]);
  const [activeTab, setActiveTab] = useState("전체");
  const [dashboardStats, setDashboardStats] = useState({
    participatingGroupBuys: 0,
    ordersInProgress: 0,
    writtenReviews: 0,
  });

  useEffect(() => {
    // 실제 API 호출로 대체 예정
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    try {
      // 공동구매 데이터 가져오기
      const groupBuyResult = await apiRequest("/api/v1/groupBuy/member");

      // 주문 데이터 가져오기
      const orderResult = await apiRequest("/api/v1/orders/me");

      if (groupBuyResult.success) {
        setGroupBuyData(groupBuyResult.data.content);
        setDashboardStats((prev) => ({
          ...prev,
          participatingGroupBuys: groupBuyResult.data.totalElements,
        }));
      }

      if (orderResult.success) {
        setOrderData(orderResult.data.content);
        const inProgressCount = orderResult.data.content.filter(
          (order) => order.status === "PAID" || order.status === "INDELIVERY"
        ).length;
        setDashboardStats((prev) => ({
          ...prev,
          ordersInProgress: inProgressCount,
          writtenReviews: 5, // 임시값, 실제 API에서 가져와야 함
        }));
      }
    } catch (error) {
      console.error("데이터 로딩 실패:", error);
      // Mock data fallback
      setDashboardStats({
        participatingGroupBuys: 3,
        ordersInProgress: 2,
        writtenReviews: 5,
      });
    }
  };

  const handleGroupBuyDetail = (groupBuyId) => {
    window.location.href = `/groupBuy/${groupBuyId}`;
  };

  const handleAllGroupBuys = () => {
    window.location.href = "/mypage/groupBuy";
  };

  const handleOrderDetail = (orderId) => {
    window.location.href = `/mypage/orders/${orderId}`;
  };

  const handleAllOrders = () => {
    window.location.href = "/mypage/orders";
  };

  const handleWriteReview = (groupBuyId) => {
    window.location.href = `/groupBuy/${groupBuyId}`;
  };

  const getStatusBadge = (status) => {
    const statusMap = {
      PAID: { text: "결제 완료", class: "bg-primary" },
      INDELIVERY: { text: "배송중", class: "bg-warning" },
      COMPLETED: { text: "배송 완료", class: "bg-success" },
      CANCELED: { text: "취소", class: "bg-danger" },
    };

    const statusInfo = statusMap[status] || {
      text: "알 수 없음",
      class: "bg-secondary",
    };
    return (
      <span className={`badge ${statusInfo.class} text-white`}>
        {statusInfo.text}
      </span>
    );
  };

  const filterOrdersByStatus = (status) => {
    if (status === "전체") return orderData;
    if (status === "진행중")
      return orderData.filter(
        (order) => order.status === "PAID" || order.status === "INDELIVERY"
      );
    if (status === "완료")
      return orderData.filter((order) => order.status === "COMPLETED");
    if (status === "취소")
      return orderData.filter((order) => order.status === "CANCELED");
    return orderData;
  };

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleDateString("ko-KR", {
      year: "numeric",
      month: "2-digit",
      day: "2-digit",
    });
  };

  const calculateProgress = (current, target) => {
    return Math.round((current / target) * 100);
  };

  const getTimeLeft = (deadline) => {
    const now = new Date();
    const deadlineDate = new Date(deadline);
    const diff = deadlineDate - now;

    if (diff <= 0) return "마감";

    const days = Math.floor(diff / (1000 * 60 * 60 * 24));
    const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));

    if (days > 0) return `${days}일 ${hours}시간 남음`;
    return `${hours}시간 남음`;
  };

  return (
    <div className="container-fluid p-4">
      {/* Header */}
      <div className="mb-4">
        <h1 className="h2 fw-bold">마이페이지</h1>
      </div>

      {/* Stats Cards */}
      <Stack direction={"horizontal"} gap={3} className={"mb-4"}>
        <Card className={"flex-fill text-center"}>
          <Card.Body>
            <div className="text-muted small mb-2">참여 중인 공동구매</div>
            <div className="h3 fw-bold text-primary">
              {dashboardStats.participatingGroupBuys}
            </div>
          </Card.Body>
        </Card>
        <Card className={"flex-fill text-center"}>
          <Card.Body>
            <div className="text-muted small mb-2">진행 중인 주문</div>
            <div className="h3 fw-bold text-warning">
              {dashboardStats.ordersInProgress}
            </div>
          </Card.Body>
        </Card>
        <Card className={"flex-fill text-center"}>
          <Card.Body>
            <div className="text-muted small mb-2">작성한 리뷰</div>
            <div className="h3 fw-bold text-success">
              {dashboardStats.writtenReviews}
            </div>
          </Card.Body>
        </Card>
      </Stack>

      {/* 참여 중인 공동구매 */}
      <Card className={"mb-4"}>
        <Card.Header className="d-flex justify-content-between align-items-center">
          <h5 className="mb-0">참여 중인 공동구매</h5>
          <button
            className="btn btn-primary"
            onClick={handleAllGroupBuys}
            style={{ minWidth: "160px", fontSize: "14px", fontWeight: "600" }}
          >
            모든 참여 공동구매 보기
          </button>
        </Card.Header>
        <Card.Body>
          {groupBuyData.length > 0 ? (
            groupBuyData.map((item) => {
              const progress = calculateProgress(
                item.currentParticipantCount,
                item.targetParticipants
              );
              return (
                <div
                  key={item.groupBuyId}
                  className="d-flex align-items-center p-3 border rounded mb-3"
                >
                  <img
                    src={
                      item.product?.imageUrl?.[0] ||
                      "/placeholder.svg?height=80&width=80"
                    }
                    alt={item.product?.title}
                    className="me-3"
                    style={{
                      width: "80px",
                      height: "80px",
                      objectFit: "cover",
                      borderRadius: "8px",
                    }}
                  />
                  <div className="flex-grow-1 me-3">
                    <h6 className="fw-medium mb-1">{item.product?.title}</h6>
                    <div className="h5 fw-bold mb-2">
                      {item.product?.price?.toLocaleString()}원
                    </div>
                    <div className="text-muted small mb-2">
                      {item.currentParticipantCount}/{item.targetParticipants}명
                      참여중
                    </div>
                    <div className="progress" style={{ height: "8px" }}>
                      <div
                        className="progress-bar bg-primary"
                        style={{ width: `${progress}%` }}
                      ></div>
                    </div>
                  </div>
                  <div className="text-end">
                    <div className="text-muted small mb-1">
                      마감: {formatDate(item.deadline)}
                    </div>
                    <div className="text-danger small mb-1">
                      {getTimeLeft(item.deadline)}
                    </div>
                    <div className="small mb-2">{progress}%</div>
                    <button
                      className="btn btn-primary"
                      onClick={() => handleGroupBuyDetail(item.groupBuyId)}
                      style={{
                        minWidth: "80px",
                        fontSize: "13px",
                        fontWeight: "600",
                        padding: "6px 12px",
                      }}
                    >
                      상세보기
                    </button>
                  </div>
                </div>
              );
            })
          ) : (
            <div className="text-center py-4 text-muted">
              참여 중인 공동구매가 없습니다.
            </div>
          )}
        </Card.Body>
      </Card>

      {/* 최근 주문 내역 */}
      <Card>
        <Card.Header className="d-flex justify-content-between align-items-center">
          <h5 className="mb-0">최근 주문 내역</h5>
          <button
            className="btn btn-primary"
            onClick={handleAllOrders}
            style={{ minWidth: "140px", fontSize: "14px", fontWeight: "600" }}
          >
            모든 주문 내역 보기
          </button>
        </Card.Header>
        <Card.Body>
          {/* Tabs */}
          <ul className="nav nav-tabs mb-3">
            {["전체", "진행중", "완료", "취소"].map((status) => (
              <li key={status} className="nav-item">
                <button
                  className={`nav-link ${activeTab === status ? "active" : ""}`}
                  onClick={() => setActiveTab(status)}
                  style={{
                    border: "none",
                    background: "none",
                    color: activeTab === status ? "#0d6efd" : "#495057",
                    fontWeight: activeTab === status ? "bold" : "600",
                    fontSize: "14px",
                    padding: "8px 16px",
                  }}
                >
                  {status}
                </button>
              </li>
            ))}
          </ul>

          {/* Tab Content */}
          <div>
            {filterOrdersByStatus(activeTab).length > 0 ? (
              filterOrdersByStatus(activeTab).map((order) => (
                <div
                  key={order.orderId}
                  className="d-flex align-items-center p-3 border rounded mb-3"
                >
                  <img
                    src="/placeholder.svg?height=80&width=80"
                    alt={order.productTitle}
                    className="me-3"
                    style={{
                      width: "80px",
                      height: "80px",
                      objectFit: "cover",
                      borderRadius: "8px",
                    }}
                  />
                  <div className="flex-grow-1 me-3">
                    <h6 className="fw-medium mb-1">{order.productTitle}</h6>
                    <div className="h5 fw-bold mb-2">
                      {order.totalPrice?.toLocaleString()}원 {order.quantity}개
                    </div>
                    <div className="mb-2">{getStatusBadge(order.status)}</div>
                  </div>
                  <div className="text-end">
                    <div className="text-muted small mb-2">
                      주문일: {formatDate(order.createdAt)}
                    </div>
                    <div className="d-flex gap-2">
                      <button
                        className="btn btn-primary"
                        onClick={() => handleOrderDetail(order.orderId)}
                        style={{
                          minWidth: "80px",
                          fontSize: "13px",
                          fontWeight: "600",
                          padding: "6px 12px",
                          boxShadow: "0 2px 4px rgba(0, 0, 0, 0.1)",
                        }}
                      >
                        상세보기
                      </button>
                      {order.status === "COMPLETED" && (
                        <button
                          className="btn btn-success"
                          onClick={() => handleWriteReview(order.groupBuyId)}
                          style={{
                            minWidth: "80px",
                            fontSize: "13px",
                            fontWeight: "600",
                            padding: "6px 12px",
                          }}
                        >
                          리뷰 작성
                        </button>
                      )}
                    </div>
                  </div>
                </div>
              ))
            ) : (
              <div className="text-center py-4 text-muted">
                {activeTab} 주문이 없습니다.
              </div>
            )}
          </div>
        </Card.Body>
      </Card>
    </div>
  );
};

export default MyPageDashboard;
