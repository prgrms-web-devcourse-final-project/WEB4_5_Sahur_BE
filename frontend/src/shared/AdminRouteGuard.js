import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import Spinner from "./Spinner";
import axios from "axios";

const AdminRouteGuard = ({ children }) => {
  const [isLoading, setIsLoading] = useState(true);
  const [isAuthorized, setIsAuthorized] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    checkAdminAuth();
  }, []);

  const checkAdminAuth = async () => {
    try {
      console.log("관리자 권한 확인 시작...");

      // NavigationMenu와 동일한 방식으로 axios 사용
      const response = await axios.get("/api/v1/members/me", {
        withCredentials: true,
      });

      console.log("API 응답 상태:", response.status);
      console.log("API 응답 데이터:", response.data);

      if (response.status === 200 && response.data.success) {
        const userData = response.data.data;
        console.log("사용자 데이터:", userData);
        console.log("로그인 상태:", userData.isLoggedIn);
        console.log("사용자 역할:", userData.role);

        // 로그인 상태 확인
        if (!userData.isLoggedIn) {
          console.log("로그인되지 않은 상태 - 로그인 페이지로 이동");
          alert("로그인이 필요합니다.");
          navigate("/login", { replace: true });
          return;
        }

        // 관리자 권한 확인
        if (userData.role === "ADMIN") {
          console.log("관리자 권한 확인됨");
          setIsAuthorized(true);
        } else {
          console.log("관리자 권한 없음 - 현재 역할:", userData.role);
          alert("관리자만 접근할 수 있는 페이지입니다.");
          navigate("/", { replace: true });
        }
      } else {
        console.log(
          "API 응답 실패:",
          response.data.message || response.data.msg
        );
        throw new Error(
          response.data.message || response.data.msg || "권한 확인 실패"
        );
      }
    } catch (error) {
      console.error("관리자 권한 확인 실패:", error);

      // axios 에러 처리
      if (error.response) {
        const status = error.response.status;
        const message = error.response.data?.message || error.message;

        if (status === 401) {
          alert("로그인이 만료되었습니다. 다시 로그인해주세요.");
          navigate("/login", { replace: true });
        } else if (status === 403) {
          alert("접근 권한이 없습니다.");
          navigate("/", { replace: true });
        } else {
          alert(`오류가 발생했습니다: ${message}`);
          navigate("/login", { replace: true });
        }
      } else {
        // 네트워크 에러 등
        alert(`연결 오류가 발생했습니다: ${error.message}`);
        navigate("/login", { replace: true });
      }
    } finally {
      setIsLoading(false);
    }
  };

  // 로딩 중일 때
  if (isLoading) {
    return <Spinner />;
  }

  // 권한이 있을 때만 children 렌더링
  if (isAuthorized) {
    return children;
  }

  // 권한이 없으면 null 반환 (이미 navigate로 리다이렉트됨)
  return null;
};

export default AdminRouteGuard;
