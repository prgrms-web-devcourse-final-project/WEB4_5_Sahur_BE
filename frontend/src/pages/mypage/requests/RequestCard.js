import { Badge, Button, Image, Stack } from "react-bootstrap";
import styles from "../orders/MyPageOrders.module.scss";
import { useNavigate } from "react-router-dom";
import { useState } from "react";

const RequestCard = ({ item, onRefresh }) => {
  const navigate = useNavigate();
  const [isDeleting, setIsDeleting] = useState(false);

  // 쿠키에서 토큰을 가져오는 함수
  const getTokenFromCookie = (name) => {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) {
      return parts.pop()?.split(";").shift() || null;
    }
    return null;
  };

  // 요청 취소 함수
  const handleCancel = async () => {
    // 사용자에게 확인
    if (!window.confirm("정말로 이 요청을 취소하시겠습니까?")) {
      return;
    }

    try {
      setIsDeleting(true);

      const baseUrl =
        process.env.REACT_APP_API_URL || "https://api.devapi.store";
      const url = `${baseUrl}/api/v1/productRequests/${item.id}`;

      // 쿠키에서 토큰 가져오기
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
        method: "DELETE",
        headers: headers,
        credentials: "include", // 쿠키 포함
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      // 성공 메시지
      alert("요청이 성공적으로 취소되었습니다.");

      // 부모 컴포넌트에 알려서 목록 새로고침
      if (onRefresh && typeof onRefresh === "function") {
        onRefresh();
      }
    } catch (error) {
      console.error("요청 취소 중 오류 발생:", error);
      alert("요청 취소 중 오류가 발생했습니다. 다시 시도해주세요.");
    } finally {
      setIsDeleting(false);
    }
  };

  const getBadge = (status) => {
    if (status === "waiting") {
      return (
        <Badge bg="" style={{ backgroundColor: "#E0E7FF", color: "#3730A3" }}>
          승인 대기
        </Badge>
      );
    } else if (status === "approved") {
      return (
        <Badge bg="" style={{ backgroundColor: "#DCFCE7", color: "#166534" }}>
          승인 완료
        </Badge>
      );
    } else if (status === "rejected") {
      return (
        <Badge bg="" style={{ backgroundColor: "#FEE2E2", color: "#991B1B" }}>
          승인 거절
        </Badge>
      );
    }
  };

  // 날짜 포맷팅 함수
  const formatDate = (dateString) => {
    if (!dateString) return "2024.06.03";

    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");

    return `${year}.${month}.${day}`;
  };

  // 카테고리 표시 함수
  const getCategoryDisplay = (category) => {
    if (!category) return "기타";
    return category.categoryType || "기타";
  };

  return (
    <Stack direction={"horizontal"} gap={2} className={"p-4"}>
      <Image
        width={50}
        height={50}
        src={"https://i.pravatar.cc/150?img=49.jpg"}
        rounded
      />
      <Stack gap={1}>
        <div className="fw-semibold">{item.productName || "상품명 없음"}</div>
        <Stack direction={"horizontal"} gap={2}>
          <span>{getCategoryDisplay(item.category)}</span>
          <desc className={"fw-light text-muted"}>요청</desc>
        </Stack>
      </Stack>
      <Stack gap={1} style={{ flex: "0 0 auto" }} className="align-items-end">
        <desc className={"text-gray-300"}>
          요청일: {formatDate(item.createdAt)}
        </desc>
        {getBadge(item.status)}
        <Stack
          direction={"horizontal"}
          gap={1}
          className={"justify-content-end"}
        >
          {item.status === "waiting" && (
            <Button
              variant={""}
              className={`${styles.detailButton}`}
              size={"sm"}
              onClick={handleCancel}
              disabled={isDeleting}
            >
              {isDeleting ? "처리중..." : "취소하기"}
            </Button>
          )}
          {item.status === "waiting" && (
            <Button
              variant={""}
              className={`${styles.detailButton}`}
              size={"sm"}
              onClick={() => navigate(`/mypage/request?edit=${item.id}`)}
            >
              수정하기
            </Button>
          )}
        </Stack>
      </Stack>
    </Stack>
  );
};

export default RequestCard;
