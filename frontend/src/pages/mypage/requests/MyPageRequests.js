import { Button, Card, Tab, Tabs } from "react-bootstrap";
import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import RequestCard from "./RequestCard";

const MyPageRequests = () => {
  const [activeTab, setActiveTab] = useState("all");
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  // 쿠키에서 토큰을 가져오는 함수
  const getTokenFromCookie = (name) => {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) {
      return parts.pop()?.split(";").shift() || null;
    }
    return null;
  };

  // API 호출 함수
  const fetchRequests = async () => {
    try {
      setLoading(true);
      setError(null);

      const baseUrl =
        process.env.REACT_APP_API_URL || "https://api.devapi.store";
      const url = `${baseUrl}/api/v1/productRequests/me`;

      // 쿠키에서 토큰 가져오기 (토큰 이름은 실제 사용하는 이름으로 변경해주세요)
      const token =
        getTokenFromCookie("authToken") ||
        getTokenFromCookie("token") ||
        getTokenFromCookie("accessToken");

      const headers = {
        "Content-Type": "application/json",
      };

      if (token) {
        headers.Authorization = `Bearer ${token}`;
      }

      const response = await fetch(url, {
        method: "GET",
        headers: headers,
        credentials: "include", // 쿠키 포함
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();
      if (data.success && data.data && data.data.content) {
        setRequests(data.data.content);
      } else {
        setRequests([]);
      }
    } catch (err) {
      setError("요청 목록을 불러오는데 실패했습니다.");
      console.error("Failed to fetch requests:", err);
    } finally {
      setLoading(false);
    }
  };

  // 컴포넌트 마운트 시 API 호출
  useEffect(() => {
    fetchRequests();
  }, []);

  // 상태별 요청 필터링
  const getRequestsByStatus = (status) => {
    const statusMap = {
      waiting: "WAITING",
      approved: "APPROVED",
      rejected: "REJECTED",
    };
    return requests.filter((request) => request.status === statusMap[status]);
  };

  const waitingRequests = getRequestsByStatus("waiting");
  const approvedRequests = getRequestsByStatus("approved");
  const rejectedRequests = getRequestsByStatus("rejected");

  const handleTabSelect = (key) => {
    setActiveTab(key);
  };

  // 로딩 상태
  if (loading) {
    return (
      <div
        className="d-flex justify-content-center align-items-center"
        style={{ height: "200px" }}
      >
        <div className="spinner-border" role="status">
          <span className="visually-hidden">Loading...</span>
        </div>
      </div>
    );
  }

  // 에러 상태
  if (error) {
    return (
      <Card className="me-2">
        <Card.Body className="text-center">
          <div className="text-danger mb-3">{error}</div>
          <Button variant="outline-primary" onClick={fetchRequests}>
            다시 시도
          </Button>
        </Card.Body>
      </Card>
    );
  }

  return (
    <>
      <Button
        className={"ms-auto mb-2 me-2"}
        onClick={() => navigate("/mypage/request")}
      >
        상품 등록 요청
      </Button>
      <Card className={"me-2"}>
        <Card.Body>
          <Tabs
            activeKey={activeTab}
            onSelect={handleTabSelect}
            id="uncontrolled-tab-example"
          >
            <Tab eventKey="all" title="전체">
              {waitingRequests.length > 0 && (
                <>
                  <div style={{ backgroundColor: "#F3F4F6" }} className={"p-3"}>
                    승인 대기 상품 ({waitingRequests.length})
                  </div>
                  {waitingRequests.map((request, index) => (
                    <div key={request.productRequestId}>
                      <RequestCard
                        item={{
                          id: request.productRequestId,
                          productId: request.productRequestId,
                          status: request.status.toLowerCase(),
                          productName: request.title,
                          createdAt: request.createdAt,
                          category: request.category,
                        }}
                        onRefresh={fetchRequests}
                      />
                      {index < waitingRequests.length - 1 && <hr />}
                    </div>
                  ))}
                </>
              )}

              {approvedRequests.length > 0 && (
                <>
                  <div style={{ backgroundColor: "#F3F4F6" }} className={"p-3"}>
                    승인된 상품 ({approvedRequests.length})
                  </div>
                  {approvedRequests.map((request, index) => (
                    <div key={request.productRequestId}>
                      <RequestCard
                        item={{
                          id: request.productRequestId,
                          productId: request.productRequestId,
                          status: request.status.toLowerCase(),
                          productName: request.title,
                          createdAt: request.createdAt,
                          category: request.category,
                        }}
                        onRefresh={fetchRequests}
                      />
                      {index < approvedRequests.length - 1 && <hr />}
                    </div>
                  ))}
                </>
              )}

              {rejectedRequests.length > 0 && (
                <>
                  <div style={{ backgroundColor: "#F3F4F6" }} className={"p-3"}>
                    거절된 상품 ({rejectedRequests.length})
                  </div>
                  {rejectedRequests.map((request, index) => (
                    <div key={request.productRequestId}>
                      <RequestCard
                        item={{
                          id: request.productRequestId,
                          productId: request.productRequestId,
                          status: request.status.toLowerCase(),
                          productName: request.title,
                          createdAt: request.createdAt,
                          category: request.category,
                        }}
                        onRefresh={fetchRequests}
                      />
                      {index < rejectedRequests.length - 1 && <hr />}
                    </div>
                  ))}
                </>
              )}

              {requests.length === 0 && (
                <div className="text-center py-5 text-muted">
                  등록된 요청이 없습니다.
                </div>
              )}
            </Tab>

            <Tab
              eventKey="waiting"
              title={`승인대기 (${waitingRequests.length})`}
            >
              {waitingRequests.length > 0 ? (
                <>
                  <div style={{ backgroundColor: "#F3F4F6" }} className={"p-3"}>
                    승인 대기 상품
                  </div>
                  {waitingRequests.map((request, index) => (
                    <div key={request.productRequestId}>
                      <RequestCard
                        item={{
                          id: request.productRequestId,
                          productId: request.productRequestId,
                          status: request.status.toLowerCase(),
                          productName: request.title,
                          createdAt: request.createdAt,
                          category: request.category,
                        }}
                        onRefresh={fetchRequests}
                      />
                      {index < waitingRequests.length - 1 && <hr />}
                    </div>
                  ))}
                </>
              ) : (
                <div className="text-center py-5 text-muted">
                  승인 대기 중인 요청이 없습니다.
                </div>
              )}
            </Tab>

            <Tab
              eventKey="rejected"
              title={`거절 (${rejectedRequests.length})`}
            >
              {rejectedRequests.length > 0 ? (
                <>
                  <div style={{ backgroundColor: "#F3F4F6" }} className={"p-3"}>
                    거절된 상품
                  </div>
                  {rejectedRequests.map((request, index) => (
                    <div key={request.productRequestId}>
                      <RequestCard
                        item={{
                          id: request.productRequestId,
                          productId: request.productRequestId,
                          status: request.status.toLowerCase(),
                          productName: request.title,
                          createdAt: request.createdAt,
                          category: request.category,
                        }}
                        onRefresh={fetchRequests}
                      />
                      {index < rejectedRequests.length - 1 && <hr />}
                    </div>
                  ))}
                </>
              ) : (
                <div className="text-center py-5 text-muted">
                  거절된 요청이 없습니다.
                </div>
              )}
            </Tab>

            <Tab
              eventKey="approved"
              title={`승인 (${approvedRequests.length})`}
            >
              {approvedRequests.length > 0 ? (
                <>
                  <div style={{ backgroundColor: "#F3F4F6" }} className={"p-3"}>
                    승인된 상품
                  </div>
                  {approvedRequests.map((request, index) => (
                    <div key={request.productRequestId}>
                      <RequestCard
                        item={{
                          id: request.productRequestId,
                          productId: request.productRequestId,
                          status: request.status.toLowerCase(),
                          productName: request.title,
                          createdAt: request.createdAt,
                          category: request.category,
                        }}
                        onRefresh={fetchRequests}
                      />
                      {index < approvedRequests.length - 1 && <hr />}
                    </div>
                  ))}
                </>
              ) : (
                <div className="text-center py-5 text-muted">
                  승인된 요청이 없습니다.
                </div>
              )}
            </Tab>
          </Tabs>
        </Card.Body>
      </Card>
    </>
  );
};

export default MyPageRequests;
