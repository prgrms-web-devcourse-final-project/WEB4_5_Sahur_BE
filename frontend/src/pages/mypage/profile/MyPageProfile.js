import { useState, useEffect } from "react";
import { Card, Form, Button, Container, Row, Col } from "react-bootstrap";
import ProfileImageZone from "../../login/ProfileImageZone";
import NameForm from "../../login/NameForm";
import NicknameForm from "../../login/NicknameForm";
import EmailForm from "../../login/EmailForm";
import PhoneForm from "../../login/PhoneForm";
import AddressForm from "../../login/AddressForm";
import DetailAdrForm from "../../login/DetailAdrForm";
import axios from "axios";

const MyPageProfile = () => {
  // 상태 관리
  const [profileImage, setProfileImage] = useState(null);
  const [name, setName] = useState("");
  const [nickname, setNickname] = useState("");
  const [email, setEmail] = useState("");
  const [phoneNumber, setPhoneNumber] = useState("");
  const [zipCode, setZipCode] = useState("");
  const [streetAdr, setStreetAdr] = useState("");
  const [detailAdr, setDetailAdr] = useState("");
  const [imageUrl, setImageUrl] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);

  // 에러 상태
  const [error, setError] = useState({
    name: "",
    nickname: "",
    email: "",
    phoneNumber: "",
    streetAdr: "",
    detailAdr: "",
  });

  // 입력 상태 (유효성 검사 통과 여부)
  const [inputStatus, setInputStatus] = useState({
    name: false,
    nickname: false,
    email: false,
    phoneNumber: false,
    address: false,
    detailAdr: false,
  });

  // API URL 설정
  const API_URL = process.env.REACT_APP_API_URL || "https://api.devapi.store";

  // axios 기본 설정 - 모든 요청에 쿠키 포함
  axios.defaults.withCredentials = true;

  // 사용자 정보 가져오기
  useEffect(() => {
    const fetchUserProfile = async () => {
      try {
        const response = await axios.get(`${API_URL}/api/v1/members/me`, {
          withCredentials: true, // 쿠키 포함
        });

        if (response.data && response.data.data) {
          const user = response.data.data;
          setName(user.name || "");
          setNickname(user.nickname || "");
          setEmail(user.email || "");
          setPhoneNumber(user.phoneNumber || "");
          setZipCode(user.zipCode || "");
          setStreetAdr(user.streetAdr || "");
          setDetailAdr(user.detailAdr || "");
          setImageUrl(user.imageUrl || "");

          // 모든 필드가 유효하다고 가정
          setInputStatus({
            name: true,
            nickname: true,
            email: true,
            phoneNumber: true,
            address: true,
            detailAdr: true,
          });
        }
      } catch (error) {
        console.error("사용자 정보 로딩 오류:", error);
      }
    };

    fetchUserProfile();
  }, [API_URL]);

  // 프로필 이미지 변경 핸들러
  const handleProfileImageChange = (file) => {
    setProfileImage(file);
  };

  // 주소 선택 핸들러 (다음 우편번호 API)
  const handleAddressSelect = (data) => {
    setZipCode(data.zonecode);
    setStreetAdr(data.address);
    setError({ ...error, streetAdr: "" });
    setInputStatus((prev) => ({ ...prev, address: true }));
  };

  // 폼 제출 핸들러
  const handleSubmit = async (e) => {
    e.preventDefault();

    // 유효성 검사
    const isValid = Object.values(inputStatus).every(
      (status) => status === true
    );

    if (!isValid) {
      alert("모든 필드를 올바르게 입력해주세요.");
      return;
    }

    setIsSubmitting(true);

    try {
      // FormData 생성
      const formData = new FormData();

      // memberData 객체 생성 (PatchMemberReqDto 형식)
      const memberData = {
        email,
        nickname,
        name,
        phoneNumber,
        zipCode,
        streetAdr,
        detailAdr,
      };

      // memberData를 JSON 문자열로 변환하여 FormData에 추가
      const memberDataBlob = new Blob([JSON.stringify(memberData)], {
        type: "application/json",
      });
      formData.append("memberData", memberDataBlob);

      // 프로필 이미지가 있으면 추가
      if (profileImage) {
        formData.append("image", profileImage);
      }

      // API 호출
      const response = await axios.patch(
        `${API_URL}/api/v1/members/modify`,
        formData,
        {
          withCredentials: true, // 쿠키 포함 (인증 토큰)
          headers: {
            "Content-Type": "multipart/form-data",
          },
        }
      );

      console.log("프로필 업데이트 성공:", response.data);
      alert("프로필이 성공적으로 업데이트되었습니다.");

      // 성공 후 프로필 이미지 상태 초기화 (선택사항)
      setProfileImage(null);
    } catch (error) {
      console.error("프로필 업데이트 오류:", error);

      // 에러 메시지 처리
      let errorMessage = "프로필 업데이트 중 오류가 발생했습니다.";
      if (error.response?.data?.message) {
        errorMessage = error.response.data.message;
      } else if (error.response?.data?.msg) {
        errorMessage = error.response.data.msg;
      }

      alert(errorMessage);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <Container className="py-4">
      <Row className="justify-content-center">
        <Col md={8} lg={6}>
          <Card>
            <Card.Body className="p-4">
              <h4 className="mb-4 text-center">프로필 정보</h4>

              <Form onSubmit={handleSubmit}>
                {/* 프로필 이미지 */}
                <div className="text-center mb-4">
                  <ProfileImageZone
                    handleProfileImageChange={handleProfileImageChange}
                    initialImageUrl={imageUrl}
                  />
                </div>

                {/* 이름 */}
                <div className="mb-3">
                  <NameForm
                    name={name}
                    setName={setName}
                    error={error}
                    setError={setError}
                    setInputStatus={setInputStatus}
                  />
                </div>

                {/* 별명 */}
                <div className="mb-3">
                  <NicknameForm
                    nickname={nickname}
                    setNickname={setNickname}
                    error={error}
                    setError={setError}
                    setInputStatus={setInputStatus}
                  />
                </div>

                {/* 이메일 */}
                <div className="mb-3">
                  <EmailForm
                    email={email}
                    setEmail={setEmail}
                    error={error}
                    setError={setError}
                    setInputStatus={setInputStatus}
                  />
                </div>

                {/* 휴대폰 번호 */}
                <div className="mb-3">
                  <PhoneForm
                    phoneNumber={phoneNumber}
                    setPhoneNumber={setPhoneNumber}
                    error={error}
                    setError={setError}
                    setInputStatus={setInputStatus}
                  />
                </div>

                {/* 주소 */}
                <div className="mb-3">
                  <AddressForm
                    zipCode={zipCode}
                    streetAdr={streetAdr}
                    handleAddressSelect={handleAddressSelect}
                    error={error}
                  />
                </div>

                {/* 상세 주소 */}
                <div className="mb-4">
                  <DetailAdrForm
                    detailAdr={detailAdr}
                    setDetailAdr={setDetailAdr}
                    error={error}
                    setError={setError}
                    setInputStatus={setInputStatus}
                  />
                </div>

                {/* 저장 버튼 */}
                <div className="d-grid">
                  <Button
                    type="submit"
                    variant="primary"
                    size="lg"
                    disabled={isSubmitting}
                    style={{
                      background:
                        "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
                      border: "none",
                      borderRadius: "8px",
                      padding: "12px",
                    }}
                  >
                    {isSubmitting ? "저장 중..." : "저장"}
                  </Button>
                </div>
              </Form>
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </Container>
  );
};

export default MyPageProfile;
