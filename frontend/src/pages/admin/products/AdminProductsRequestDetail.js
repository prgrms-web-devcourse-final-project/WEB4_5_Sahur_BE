import { Button, Card, Stack, Image } from "react-bootstrap";
import { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import GroupBuyRegisterDenyModal from "./GroupBuyRegisterDenyModal";
import { ReactComponent as ArrowLeftIcon } from "../../../assets/images/icon/arrow-left-black.svg";
import axios from "axios";

const AdminProductsRequestDetail = () => {
  const navigate = useNavigate();
  const params = useParams();

  // requestId가 없으면 productId를 사용 (라우팅 문제 해결)
  const requestId = params.requestId || params.productId;

  // 디버깅을 위한 로그 추가
  console.log("전체 params:", params);
  console.log("requestId from params:", params.requestId);
  console.log("productId from params:", params.productId);
  console.log("최종 사용할 requestId:", requestId);
  console.log("현재 URL:", window.location.pathname);

  const [loading, setLoading] = useState(true);
  const [requestData, setRequestData] = useState(null);
  const [modalOpen, setModalOpen] = useState(false);
  const [processing, setProcessing] = useState(false);

  // 카테고리 타입 한글 변환
  const getCategoryLabel = (categoryType) => {
    const categoryMap = {
      PET: "반려동물",
      KIDS: "키즈",
      BOOK: "도서",
      CAR: "자동차",
      SPORTS: "스포츠",
      DIGITAL: "디지털/가전",
      FASHION: "패션",
      HOME: "홈/리빙",
      BEAUTY: "뷰티",
      FOOD: "식품",
    };
    return categoryMap[categoryType] || categoryType;
  };

  // 상품 요청 상세 정보 조회
  useEffect(() => {
    const fetchRequestDetail = async () => {
      try {
        setLoading(true);
        console.log("요청 ID:", requestId, typeof requestId);

        if (!requestId) {
          console.error("requestId가 없습니다. params:", params);
          console.error("현재 URL:", window.location.pathname);

          // URL에서 직접 추출 시도
          const pathParts = window.location.pathname.split("/");
          const urlRequestId = pathParts[pathParts.length - 1];
          console.log("URL에서 추출한 ID:", urlRequestId);

          if (urlRequestId && !isNaN(urlRequestId)) {
            console.log("URL에서 직접 추출한 ID 사용:", urlRequestId);
            // 직접 추출한 ID로 API 호출
            const apiUrl = `/api/v1/productRequests/${urlRequestId}`;
            console.log("API 호출 URL:", apiUrl);

            const response = await axios.get(apiUrl);
            console.log("API 응답:", response.data);

            if (response.data.success && response.data.data) {
              setRequestData(response.data.data);
            } else if (response.data) {
              setRequestData(response.data);
            }
            return;
          }

          alert("요청 ID가 없습니다.");
          navigate("/admin/products");
          return;
        }

        // API 호출 전 콘솔에 URL 출력
        const apiUrl = `/api/v1/productRequests/${requestId}`;
        console.log("API 호출 URL:", apiUrl);

        // API 엔드포인트 수정 - 실제 API 구조에 맞게
        const response = await axios.get(apiUrl);
        console.log("API 응답:", response.data);

        if (response.data.success && response.data.data) {
          setRequestData(response.data.data);
        } else {
          console.error("API 응답 구조가 예상과 다릅니다:", response.data);

          // 응답 데이터가 있지만 구조가 다른 경우 직접 설정
          if (response.data) {
            setRequestData(response.data);
          } else {
            alert("상품 요청 정보를 불러올 수 없습니다.");
            navigate("/admin/products");
          }
        }
      } catch (error) {
        console.error("상품 요청 정보를 가져오는데 실패했습니다:", error);

        // 에러 상세 정보 출력
        if (error.response) {
          console.error("응답 상태:", error.response.status);
          console.error("응답 데이터:", error.response.data);

          if (error.response.status === 404) {
            alert("존재하지 않는 상품 요청입니다.");
          } else {
            alert(
              `상품 요청 정보를 가져오는데 실패했습니다. (${error.response.status})`
            );
          }
        } else if (error.request) {
          console.error("요청은 보냈지만 응답이 없습니다:", error.request);
          alert("서버에서 응답이 없습니다. 네트워크 연결을 확인해주세요.");
        } else {
          console.error("요청 설정 중 오류:", error.message);
          alert("네트워크 오류가 발생했습니다.");
        }

        navigate("/admin/products");
      } finally {
        setLoading(false);
      }
    };

    fetchRequestDetail();
  }, [requestId, navigate, params]);

  // 승인 처리
  const handleApprove = async () => {
    try {
      setProcessing(true);
      // API 호출 - PATCH /api/v1/productRequests/{productRequestId}/approve
      await axios.patch(`/api/v1/productRequests/${requestId}/approve`);
      alert("상품 요청이 승인되었습니다.");
      navigate("/admin/products");
    } catch (error) {
      console.error("승인 처리에 실패했습니다:", error);
      alert("승인 처리에 실패했습니다.");
    } finally {
      setProcessing(false);
    }
  };

  // 거부 처리 성공 시 호출되는 콜백
  const handleRejectSuccess = () => {
    setModalOpen(false);
    navigate("/admin/products");
  };

  if (loading) {
    return (
      <div
        className="d-flex justify-content-center align-items-center"
        style={{ height: "400px" }}
      >
        <div className="spinner-border" role="status">
          <span className="visually-hidden">Loading...</span>
        </div>
        <div className="ms-3">상품 요청 정보를 불러오는 중...</div>
      </div>
    );
  }

  if (!requestData) {
    return (
      <Card>
        <Card.Body className="text-center p-5">
          <h4>상품 요청을 찾을 수 없습니다.</h4>
          <p className="text-muted">요청 ID: {requestId}</p>
          <Button onClick={() => navigate("/admin/products")} className="mt-3">
            목록으로 돌아가기
          </Button>
        </Card.Body>
      </Card>
    );
  }

  return (
    <Card>
      <Card.Header>
        <div
          onClick={() => navigate("/admin/products")}
          style={{
            cursor: "pointer",
            display: "inline-flex",
            alignItems: "center",
          }}
        >
          <ArrowLeftIcon width={18} height={18} style={{ marginRight: 10 }} />
          돌아가기
        </div>
        <h3 className={"mt-5"}>등록 상품 관리</h3>
      </Card.Header>

      {/* 기본 정보 */}
      <Card.Body className={"mx-4 mt-4 p-5 border rounded"}>
        <h4>기본 정보</h4>
        <Stack gap={4}>
          <Stack>
            <span className={"fw-semibold"}>상품명</span>
            <span className={"text-gray-500"}>
              {requestData.title || "상품명 없음"}
            </span>
          </Stack>
          <Stack direction={"horizontal"}>
            <Stack className={"w-50"}>
              <span className={"fw-semibold"}>카테고리</span>
              <span className={"text-gray-500"}>
                {getCategoryLabel(requestData.category?.categoryType) ||
                  "카테고리 없음"}
              </span>
            </Stack>
            <Stack className={"w-50"}>
              <span className={"fw-semibold"}>세부카테고리</span>
              <span className={"text-gray-500"}>
                {requestData.category?.keyword || "세부카테고리 없음"}
              </span>
            </Stack>
          </Stack>
          <Stack>
            <span className={"fw-semibold"}>URL</span>
            <span className={"text-gray-500"}>
              {requestData.url || "URL 없음"}
            </span>
          </Stack>
          <Stack>
            <span className={"fw-semibold"}>요청일</span>
            <span className={"text-gray-500"}>
              {requestData.createdAt
                ? new Date(requestData.createdAt).toLocaleDateString()
                : "날짜 없음"}
            </span>
          </Stack>
          <Stack>
            <span className={"fw-semibold"}>현재 상태</span>
            <span className={"text-gray-500"}>
              {requestData.status === "WAITING" && "승인 대기"}
              {requestData.status === "APPROVED" && "승인"}
              {requestData.status === "REJECTED" && "거부"}
            </span>
          </Stack>
        </Stack>
      </Card.Body>

      {/* 상품 설명 */}
      <Card.Body className={"mx-4 mt-4 p-5 border rounded"}>
        <Stack gap={5}>
          <h4>상품 설명</h4>
          <div style={{ background: "#F9FAFB" }} className={"p-4 rounded"}>
            {requestData.description ||
              "상품 설명이 없습니다. 요청자가 상품에 대한 설명을 제공하지 않았습니다."}
          </div>
        </Stack>
      </Card.Body>

      {/* 상품 이미지 */}
      <Card.Body className={"mx-4 mt-4 p-5 border rounded"}>
        <Stack gap={3}>
          <h4>상품 이미지</h4>
          <div className="d-flex flex-wrap gap-3">
            {/* 기존 이미지 표시 */}
            {requestData.imageUrls &&
              requestData.imageUrls.length > 0 &&
              requestData.imageUrls.map((imageUrl, index) => (
                <div
                  key={`existing-${index}`}
                  style={{
                    width: "200px",
                    height: "200px",
                    border: "1px solid #ddd",
                    borderRadius: "8px",
                    overflow: "hidden",
                    position: "relative",
                  }}
                >
                  <Image
                    src={imageUrl || "/placeholder.svg"}
                    alt={`상품 이미지 ${index + 1}`}
                    style={{
                      width: "100%",
                      height: "100%",
                      objectFit: "cover",
                    }}
                  />
                  {index === 0 && (
                    <div
                      className="position-absolute bottom-0 start-0 bg-success text-white px-2 py-1 m-1"
                      style={{ fontSize: "12px", zIndex: 5 }}
                    >
                      대표 이미지
                    </div>
                  )}
                </div>
              ))}

            {/* 기존 이미지가 없을 때 기본 이미지 표시 */}
            {(!requestData.imageUrls || requestData.imageUrls.length === 0) && (
              <>
                <div
                  style={{
                    width: "200px",
                    height: "200px",
                    border: "1px solid #ddd",
                    borderRadius: "8px",
                    overflow: "hidden",
                  }}
                >
                  <Image
                    src="/placeholder.svg?height=200&width=200"
                    alt="상품 이미지 1"
                    style={{
                      width: "100%",
                      height: "100%",
                      objectFit: "cover",
                    }}
                  />
                </div>
                <div
                  style={{
                    width: "200px",
                    height: "200px",
                    border: "1px solid #ddd",
                    borderRadius: "8px",
                    overflow: "hidden",
                  }}
                >
                  <Image
                    src="/placeholder.svg?height=200&width=200"
                    alt="상품 이미지 2"
                    style={{
                      width: "100%",
                      height: "100%",
                      objectFit: "cover",
                    }}
                  />
                </div>
                <div
                  style={{
                    width: "200px",
                    height: "200px",
                    border: "1px solid #ddd",
                    borderRadius: "8px",
                    overflow: "hidden",
                  }}
                >
                  <Image
                    src="/placeholder.svg?height=200&width=200"
                    alt="상품 이미지 3"
                    style={{
                      width: "100%",
                      height: "100%",
                      objectFit: "cover",
                    }}
                  />
                </div>
              </>
            )}
          </div>
          <small className={"text-muted"}>
            * 상품 이미지는 최대 5개까지 등록 가능합니다.
          </small>
        </Stack>
      </Card.Body>

      {/* 하단 버튼 - 승인 대기 상태일 때만 표시 */}
      {requestData.status === "WAITING" && (
        <Card.Footer className={"mx-4 mt-4 p-5"}>
          <Stack
            className={"justify-content-end"}
            direction={"horizontal"}
            gap={3}
          >
            <Button
              variant="danger"
              onClick={() => setModalOpen(true)}
              disabled={processing}
            >
              거부
            </Button>
            <Button
              variant="primary"
              onClick={handleApprove}
              disabled={processing}
            >
              승인
            </Button>
          </Stack>
        </Card.Footer>
      )}

      {/* 이미 처리된 요청인 경우 상태 표시 */}
      {requestData.status !== "WAITING" && (
        <Card.Footer className={"mx-4 mt-4 p-5 text-center"}>
          <div className="alert alert-info">
            이 요청은 이미{" "}
            <strong>
              {requestData.status === "APPROVED" && "승인"}
              {requestData.status === "REJECTED" && "거부"}
            </strong>
            되었습니다.
          </div>
        </Card.Footer>
      )}

      <GroupBuyRegisterDenyModal
        show={modalOpen}
        onHide={() => setModalOpen(false)}
        onConfirm={handleRejectSuccess}
        requestData={requestData}
        requestId={requestId}
      />
    </Card>
  );
};

export default AdminProductsRequestDetail;
