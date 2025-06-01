import { Button, Card, Image, Stack } from "react-bootstrap";
import styles from "./MyPageLayout.module.scss";
import { useNavigate } from "react-router-dom";
import { useMutation } from "react-query";
import axios from "axios";
import { useEffect, useState } from "react";

const MyPageProfileCard = () => {
  const navigate = useNavigate();
  const [member, setMember] = useState(null);

  const API_BASE_URL = process.env.REACT_APP_API_URL || "";

  // 회원 정보 조회 API
  const userProfileMutation = useMutation(
    () => axios.get("/api/v1/members/me", { withCredentials: true }),
    {
      onSuccess: (response) => {
        console.log("회원 정보 조회 성공:", response.data);
        setMember(response.data.data);
      },
      onError: (error) => {
        console.error("회원 정보 조회 실패:", error);
        setMember(null);
      },
    }
  );

  // 이미지 URL 처리 함수
  const getImageUrl = (imageUrl) => {
    if (!imageUrl) {
      return `${API_BASE_URL}/images/default-profile.png`;
    }

    if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
      return imageUrl;
    }

    const cleanPath = imageUrl.startsWith("/") ? imageUrl : `/${imageUrl}`;
    return `${API_BASE_URL}${cleanPath}`;
  };

  useEffect(() => {
    userProfileMutation.mutate();
  }, []);

  if (userProfileMutation.isLoading) {
    return (
      <Card className="p-3 m-3 shadow">
        <Card.Body>
          <div className="text-center">로딩 중...</div>
        </Card.Body>
      </Card>
    );
  }

  if (userProfileMutation.isError) {
    return (
      <Card className="p-3 m-3 shadow">
        <Card.Body>
          <div className="text-center text-danger">
            회원 정보를 불러올 수 없습니다.
            <Button
              variant="link"
              size="sm"
              onClick={() => userProfileMutation.mutate()}
              className="d-block mx-auto mt-2"
            >
              다시 시도
            </Button>
          </div>
        </Card.Body>
      </Card>
    );
  }

  return (
    <Card className="p-3 m-3 shadow">
      <Card.Body>
        <Stack direction={"horizontal"} gap={2}>
          <Image
            src={getImageUrl(member?.imageUrl) || "/placeholder.svg"}
            roundedCircle
            style={{ width: "40px", height: "40px" }}
            onError={(e) => {
              e.target.src = `${API_BASE_URL}/images/default-profile.png`;
            }}
          />
          <Stack>
            <h5>{member?.nickname || member?.name || "사용자"}</h5>
            <desc className={"text-gray-300"}>
              {member?.role === "ADMIN" ? "관리자" : "일반 회원"}
            </desc>
          </Stack>
        </Stack>
        <Button
          variant={""}
          className={`w-100 mt-3 ${styles.whiteButton}`}
          onClick={() => navigate("profile")}
        >
          프로필 수정
        </Button>
      </Card.Body>
    </Card>
  );
};

export default MyPageProfileCard;
