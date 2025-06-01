import { Button, Card, Form, Stack, Row, Col, Image } from "react-bootstrap";
import { ReactComponent as QuestionIcon } from "../../../assets/images/icon/question.svg";
import { useNavigate, useParams } from "react-router-dom";
import { useState, useEffect } from "react";
import CloseRecruitModal from "./CloseRecruitModal";
import axios from "axios";

const roundOptions = [
  { value: "1", label: "1회차" },
  { value: "2", label: "2회차" },
  { value: "3", label: "3회차" },
  { value: "4", label: "4회차" },
  { value: "5", label: "5회차" },
];

// categoryType을 한글로 변환하는 함수
const getCategoryTypeLabel = (categoryType) => {
  const labels = {
    FASHION_CLOTHES: "패션 의류",
    FASHION_ACCESSORY: "패션 액세서리",
    BEAUTY: "뷰티",
    DIGITAL_APPLIANCE: "디지털/가전",
    FURNITURE: "가구",
    LIVING: "리빙",
    FOOD: "식품",
    SPORTS: "스포츠",
    CAR: "자동차",
    BOOK: "도서",
    KIDS: "키즈",
    PET: "반려동물",
  };
  return labels[categoryType] || categoryType;
};

// keyword를 ��글로 변환하는 함수
const getKeywordLabel = (keyword) => {
  const labels = {
    // 패션 의류
    TSHIRT: "티셔츠",
    DRESS: "드레스",
    SHIRT: "셔츠",
    OUTER: "아우터",
    PANTS: "바지",
    // 패션 액세서리
    SHOES: "신발",
    BAG: "가방",
    WALLET: "지갑",
    HAT: "모자",
    ACCESSORY: "액세서리",
    // 뷰티
    SKINCARE: "스킨케어",
    MASK_PACK: "마스크팩",
    MAKEUP: "메이크업",
    PERFUME: "향수",
    HAIR_CARE: "헤어케어",
    // 디지털/가전
    SMARTPHONE: "스마트폰",
    TABLET: "태블릿",
    LAPTOP: "노트북",
    TV: "TV",
    REFRIGERATOR: "냉장고",
    WASHING_MACHINE: "세탁기",
    // 가구
    BED: "침대",
    SOFA: "소파",
    TABLE: "테이블",
    CHAIR: "의자",
    LIGHTING: "조명",
    // 리빙
    BODY_CARE: "바디케어",
    SUPPLEMENT: "건강식품",
    TOOTHPASTE: "치약",
    VACUUM_CLEANER: "청소기",
    DAILY_GOODS: "생활용품",
    // 식품
    FRUIT: "과일",
    VEGETABLE: "채소",
    MEAT: "육류",
    SIDE_DISH: "반찬",
    INSTANT_FOOD: "즉석식품",
    BEVERAGE: "음료",
    // 스포츠
    SPORTSWEAR: "스포츠웨어",
    SNEAKERS: "운동화",
    EQUIPMENT: "운동기구",
    GOLF: "골프",
    SWIMMING: "수영",
    // 자동차
    AUTO_ACCESSORY: "자동차용품",
    CAR_CARE: "자동차관리",
    TOOLS: "공구",
    HAND_TOOL: "수공구",
    TIRE: "타이어",
    // 도서
    NOVEL: "소설",
    SELF_DEVELOP: "자기계발",
    COMIC: "만화",
    ALBUM: "앨범",
    DVD: "DVD",
    // 키즈
    BABY_CLOTHES: "유아복",
    CHILD_CLOTHES: "아동복",
    TOY: "장난감",
    KIDS_BOOKS: "아동도서",
    BABY_GOODS: "육아용품",
    // 반려동물
    DOG_FOOD: "강아지사료",
    CAT_FOOD: "고양이사료",
    PET_SNACK: "반려동물간식",
    PET_TOY: "반려동물장난감",
    PET_HYGIENE: "반려동물위생용품",
  };
  return labels[keyword] || keyword;
};

const AdminGroupBuyDetail = () => {
  const navigate = useNavigate();
  const params = useParams();

  const id = params.id || params.groupBuyId || params.groupId;

  const [productTitle, setProductTitle] =
    useState("프리미엄 블루투스 이어폰 100");
  const [productPrice, setProductPrice] = useState("30,000");
  const [productDesc, setProductDesc] = useState(
    "상품에 대한 상세 설명을 입력하세요."
  );
  const [minimumPerson, setMinimumPerson] = useState("30");
  const [deadline, setDeadline] = useState("2025-06-03");
  const [deadlineTime, setDeadlineTime] = useState("23:59");
  const [modalOpen, setModalOpen] = useState(false);
  const [selectedRound, setSelectedRound] = useState(null);
  const [isSaving, setIsSaving] = useState(false);
  const [isLoading, setIsLoading] = useState(true);

  // 카테고리 정보 (읽기 전용)
  const [categoryType, setCategoryType] = useState("");
  const [categoryKeyword, setCategoryKeyword] = useState("");

  const [productImages, setProductImages] = useState([
    "https://via.placeholder.com/300x300?text=상품이미지1",
    "https://via.placeholder.com/300x300?text=상품이미지2",
    "https://via.placeholder.com/300x300?text=상품이미지3",
  ]);

  const [isGroupBuyClosed, setIsGroupBuyClosed] = useState(false);
  const [showCloseModal, setShowCloseModal] = useState(false);
  const [isCancelling, setIsCancelling] = useState(false);

  // 초기 데이터 로딩
  useEffect(() => {
    if (!id) {
      console.error("ID가 없습니다. URL을 확인해주세요.");
      return;
    }

    const loadProductData = async () => {
      try {
        setIsLoading(true);
        const response = await axios.get(`/api/v1/groupBuy/${id}`);
        const data = response.data.data;

        // 상품 기본 정보 설정
        setProductTitle(data.product?.title || "");
        setProductPrice(data.product?.price?.toLocaleString() || "0");
        setProductDesc(data.product?.description || "");
        setMinimumPerson(data.targetParticipants?.toString() || "0");
        setProductImages(data.product?.imageUrl || []);

        // 마감일 설정
        if (data.deadline) {
          const deadlineDate = new Date(data.deadline);
          setDeadline(deadlineDate.toISOString().split("T")[0]);
          setDeadlineTime(deadlineDate.toTimeString().slice(0, 5));
        }

        // 회차 설정
        if (data.round) {
          const round = roundOptions.find(
            (r) => r.value === data.round.toString()
          );
          setSelectedRound(round || null);
        }

        // 카테고리 정보 설정 (읽기 전용)
        if (data.product?.category) {
          const productCategory = data.product.category;
          setCategoryType(getCategoryTypeLabel(productCategory.categoryType));
          setCategoryKeyword(getKeywordLabel(productCategory.keyword));
        }

        // 공동구매 상태 설정 (CLOSED인 경우 isGroupBuyClosed를 true로 설정)
        if (data.status === "CLOSED") {
          setIsGroupBuyClosed(true);
        } else {
          setIsGroupBuyClosed(false);
        }
      } catch (error) {
        console.error("상품 데이터 로딩 실패:", error);
      } finally {
        setIsLoading(false);
      }
    };

    loadProductData();
  }, [id]);

  // 진행 회차 선택 핸들러
  const handleRoundChange = (e) => {
    const selectedValue = e.target.value;
    if (selectedValue) {
      const selectedOption = roundOptions.find(
        (option) => option.value === selectedValue
      );
      setSelectedRound(selectedOption);
    } else {
      setSelectedRound(null);
    }
  };

  // 모집 종료 핸들러
  const handleCloseRecruit = () => {
    if (!id) {
      alert("공동구매 ID를 찾을 수 없습니다. 페이지를 새로고침해주세요.");
      return;
    }
    setShowCloseModal(true);
  };

  // 모집 종료 확인 핸들러
  const handleConfirmClose = () => {
    setIsGroupBuyClosed(true);
    setShowCloseModal(false);
    // 페이지 새로고침하여 최신 상태 반영
    window.location.reload();
  };

  // 주문 취소 핸들러
  const handleCancelOrder = async () => {
    if (!window.confirm("정말로 주문을 취소하시겠습니까?")) {
      return;
    }

    try {
      setIsCancelling(true);

      await axios.post(`/payments/groupBuy/${id}/cancel`);
      await axios.delete(`/orders/${id}/cancel`);

      alert("주문이 성공적으로 취소되었습니다.");
      navigate("/admin/groupBuy");
    } catch (error) {
      console.error("주문 취소 실패:", error);
      alert(
        `주문 취소에 실패했습니다: ${
          error.response?.data?.message || error.message
        }`
      );
    } finally {
      setIsCancelling(false);
    }
  };

  // productInfo 객체 생성
  const productInfo = {
    groupBuyId: id,
    title: productTitle,
    price: Number.parseInt(productPrice.replace(/,/g, "")) || 0,
    deadline: `${deadline} ${deadlineTime}`,
    round: selectedRound?.value || "1",
    imageUrl: productImages[0] || null,
  };

  // handleSave 함수
  const handleSave = async () => {
    if (!id) {
      alert("공동구매 ID를 찾을 수 없습니다.");
      return;
    }

    try {
      setIsSaving(true);

      // 데이터 변환 및 검증
      const targetParticipants = Number.parseInt(minimumPerson);
      const round = selectedRound?.value
        ? Number.parseInt(selectedRound.value)
        : null;

      // 필수 필드 검증
      if (!productTitle.trim()) {
        alert("상품명을 입력해주세요.");
        setIsSaving(false);
        return;
      }

      if (isNaN(targetParticipants) || targetParticipants <= 0) {
        alert("최소 참여 인원을 올바르게 입력해주세요.");
        setIsSaving(false);
        return;
      }

      if (!round || isNaN(round) || round <= 0) {
        alert("진행 회차를 선택해주세요.");
        setIsSaving(false);
        return;
      }

      if (!deadline || !deadlineTime) {
        alert("마감 일시를 입력해주세요.");
        setIsSaving(false);
        return;
      }

      // ISO 형식의 날짜 생성 및 검증
      const deadlineISO = `${deadline}T${deadlineTime}:00`;
      const deadlineDate = new Date(deadlineISO);

      if (isNaN(deadlineDate.getTime())) {
        alert("올바른 마감 일시를 입력해주세요.");
        setIsSaving(false);
        return;
      }

      // 요청 데이터 준비
      const requestData = {
        targetParticipants: targetParticipants,
        currentParticipantCount: null,
        round: round,
        deadline: deadlineISO,
        status: isGroupBuyClosed ? "CLOSED" : "ONGOING",
      };

      const response = await axios.patch(
        `/api/v1/groupBuy/${id}`,
        requestData,
        {
          headers: {
            "Content-Type": "application/json",
          },
          withCredentials: true,
          timeout: 10000,
        }
      );

      alert("공동구매 정보가 성공적으로 저장되었습니다.");

      // 페이지 새로고침
      window.location.reload();
    } catch (error) {
      let errorMessage = "알 수 없는 오류가 발생했습니다.";

      if (error.response) {
        const status = error.response.status;
        const data = error.response.data;

        if (status === 400) {
          errorMessage = "요청 데이터가 올바르지 않습니다.";
          if (data?.message) {
            errorMessage += ` (${data.message})`;
          }
        } else if (status === 401) {
          errorMessage = "인증이 필요합니다.";
        } else if (status === 403) {
          errorMessage = "권한이 없습니다.";
        } else if (status === 404) {
          errorMessage = "해당 공동구매를 찾을 수 없습니다.";
        } else if (status === 405) {
          errorMessage = "지원하지 않는 HTTP 메서드입니다.";
        } else if (status === 500) {
          errorMessage = "서버 오류가 발생했습니다.";
          if (data?.message) {
            errorMessage += ` (${data.message})`;
          }
        }
      } else if (error.request) {
        errorMessage = "서버에 연결할 수 없습니다.";
      } else {
        errorMessage = error.message;
      }

      alert(`저장에 실패했습니다: ${errorMessage}`);
    } finally {
      setIsSaving(false);
    }
  };

  return (
    <Stack direction={"vertical"} gap={3} className={"m-3"}>
      {/* 헤더 */}
      <Card style={{ border: "none", boxShadow: "0 1px 3px rgba(0,0,0,0.1)" }}>
        <Card.Body className={"p-4"}>
          <h4 className="mb-2">등록된 공동 구매 상품 관리</h4>
          <p className="text-muted mb-0">등록된 상품에 대한 수정을 합니다.</p>
        </Card.Body>
      </Card>

      {/* 기본 정보 */}
      <Card style={{ border: "none", boxShadow: "0 1px 3px rgba(0,0,0,0.1)" }}>
        <Card.Header
          style={{
            backgroundColor: "white",
            borderBottom: "1px solid #e9ecef",
          }}
        >
          <h5 className="mb-0">기본 정보</h5>
        </Card.Header>
        <Card.Body className={"p-4"}>
          <Form.Group className="mb-4">
            <Form.Label className="fw-semibold">상품명</Form.Label>
            <Form.Control
              type="text"
              value={productTitle}
              onChange={(e) => setProductTitle(e.target.value)}
              style={{ borderRadius: "6px", border: "1px solid #d1d5db" }}
            />
          </Form.Group>

          <Row className="mb-4">
            <Col md={6}>
              <Form.Group>
                <Form.Label className="fw-semibold">카테고리</Form.Label>
                <Form.Control
                  type="text"
                  value={categoryType}
                  readOnly
                  disabled
                  style={{
                    borderRadius: "6px",
                    border: "1px solid #d1d5db",
                    backgroundColor: "#f9fafb",
                  }}
                />
              </Form.Group>
            </Col>
            <Col md={6}>
              <Form.Group>
                <Form.Label className="fw-semibold">세부카테고리</Form.Label>
                <Form.Control
                  type="text"
                  value={categoryKeyword}
                  readOnly
                  disabled
                  style={{
                    borderRadius: "6px",
                    border: "1px solid #d1d5db",
                    backgroundColor: "#f9fafb",
                  }}
                />
              </Form.Group>
            </Col>
          </Row>

          <Row className="mb-4">
            <Col md={6}>
              <Form.Group>
                <Form.Label className="fw-semibold">
                  진행 회차
                  <small className="text-muted ms-1">
                    몇 번째 진행인지 번호를 입력하여 진행 회차를 선택하세요
                  </small>
                </Form.Label>
                <Form.Select
                  value={selectedRound?.value || ""}
                  onChange={handleRoundChange}
                  style={{ borderRadius: "6px", border: "1px solid #d1d5db" }}
                >
                  <option value="">회차 선택</option>
                  {roundOptions.map((option) => (
                    <option key={option.value} value={option.value}>
                      {option.label}
                    </option>
                  ))}
                </Form.Select>
              </Form.Group>
            </Col>
            <Col md={6}>
              <Form.Group>
                <Form.Label className="fw-semibold">판매가</Form.Label>
                <div style={{ position: "relative" }}>
                  <Form.Control
                    type="text"
                    value={productPrice}
                    onChange={(e) => setProductPrice(e.target.value)}
                    style={{
                      borderRadius: "6px",
                      border: "1px solid #d1d5db",
                      paddingRight: "30px",
                    }}
                  />
                  <span
                    style={{
                      position: "absolute",
                      right: "12px",
                      top: "50%",
                      transform: "translateY(-50%)",
                      color: "#6b7280",
                    }}
                  >
                    원
                  </span>
                </div>
              </Form.Group>
            </Col>
          </Row>

          <Form.Group>
            <Form.Label className="fw-semibold">상품 설명</Form.Label>
            <Form.Control
              as="textarea"
              rows={4}
              placeholder="상품에 대한 상세 설명을 입력하세요."
              value={productDesc}
              onChange={(e) => setProductDesc(e.target.value)}
              style={{ borderRadius: "6px", border: "1px solid #d1d5db" }}
            />
          </Form.Group>
        </Card.Body>
      </Card>

      {/* 상품 이미지 */}
      <Card style={{ border: "none", boxShadow: "0 1px 3px rgba(0,0,0,0.1)" }}>
        <Card.Header
          style={{
            backgroundColor: "white",
            borderBottom: "1px solid #e9ecef",
          }}
        >
          <h5 className="mb-0">상품 이미지</h5>
        </Card.Header>
        <Card.Body className={"p-4"}>
          <div className="d-flex flex-wrap gap-3 mb-3">
            {productImages.length > 0 ? (
              productImages.map((imageUrl, index) => (
                <div
                  key={index}
                  className="position-relative"
                  style={{
                    width: "120px",
                    height: "120px",
                    border: "1px solid #d1d5db",
                    borderRadius: "8px",
                    overflow: "hidden",
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
                      className="position-absolute bottom-0 start-0 bg-primary text-white px-2 py-1 m-1"
                      style={{
                        fontSize: "10px",
                        borderRadius: "4px",
                      }}
                    >
                      대표 이미지
                    </div>
                  )}
                </div>
              ))
            ) : (
              <div
                className="d-flex align-items-center justify-content-center"
                style={{
                  width: "120px",
                  height: "120px",
                  border: "2px dashed #d1d5db",
                  borderRadius: "8px",
                  color: "#6b7280",
                }}
              >
                <span className="small">이미지 없음</span>
              </div>
            )}
          </div>
          <p className="text-muted small mb-0">
            * 등록된 상품 이미지입니다. 이미지 수정은 상품 등록 페이지에서
            가능합니다.
          </p>
        </Card.Body>
      </Card>

      {/* 공동구매 설정 */}
      <Card style={{ border: "none", boxShadow: "0 1px 3px rgba(0,0,0,0.1)" }}>
        <Card.Header
          style={{
            backgroundColor: "white",
            borderBottom: "1px solid #e9ecef",
          }}
        >
          <h5 className="mb-0">공동구매 설정</h5>
        </Card.Header>
        <Card.Body className={"p-4"}>
          <Row className="mb-4">
            <Col md={6}>
              <Form.Group>
                <Form.Label className="fw-semibold">마감 일시</Form.Label>
                <div className="d-flex gap-2">
                  <Form.Control
                    type="date"
                    value={deadline}
                    onChange={(e) => setDeadline(e.target.value)}
                    style={{ borderRadius: "6px", border: "1px solid #d1d5db" }}
                  />
                  <Form.Control
                    type="time"
                    value={deadlineTime}
                    onChange={(e) => setDeadlineTime(e.target.value)}
                    style={{ borderRadius: "6px", border: "1px solid #d1d5db" }}
                  />
                </div>
              </Form.Group>
            </Col>
            <Col md={6}>
              <Form.Group>
                <Form.Label className="fw-semibold">최소 참여 인원</Form.Label>
                <div style={{ position: "relative" }}>
                  <Form.Control
                    type="text"
                    value={minimumPerson}
                    onChange={(e) => setMinimumPerson(e.target.value)}
                    style={{
                      borderRadius: "6px",
                      border: "1px solid #d1d5db",
                      paddingRight: "30px",
                    }}
                  />
                  <span
                    style={{
                      position: "absolute",
                      right: "12px",
                      top: "50%",
                      transform: "translateY(-50%)",
                      color: "#6b7280",
                    }}
                  >
                    명
                  </span>
                </div>
              </Form.Group>
            </Col>
          </Row>

          <div
            style={{
              backgroundColor: "#fef3c7",
              border: "1px solid #f59e0b",
              borderRadius: "8px",
              padding: "16px",
              marginBottom: "20px",
            }}
          >
            <div className="d-flex align-items-start">
              <QuestionIcon style={{ marginRight: "8px", marginTop: "2px" }} />
              <div>
                <h6 className="mb-2">공동구매 결제 안내</h6>
                <p className="mb-0 small">
                  공동구매는 마감 일시까지 최소 참여 인원이 모이면 공동구매가
                  성사됩니다. 성사된 경우 결제가 진행되며, 마감까지 인원이
                  부족할 경우 결제는 자동으로 취소됩니다.
                </p>
              </div>
            </div>
          </div>
        </Card.Body>
      </Card>

      {/* 하단 버튼 */}
      <Card style={{ border: "none", boxShadow: "0 1px 3px rgba(0,0,0,0.1)" }}>
        <Card.Body className={"p-4"}>
          <div className="d-flex justify-content-between align-items-center">
            <div className="d-flex gap-2">
              {isGroupBuyClosed ? (
                <Button
                  variant="danger"
                  onClick={handleCancelOrder}
                  disabled={isCancelling}
                  style={{
                    borderRadius: "6px",
                    padding: "10px 20px",
                    fontWeight: "600",
                    fontSize: "14px",
                    backgroundColor: "#dc3545",
                    borderColor: "#dc3545",
                    boxShadow: "0 2px 4px rgba(220, 53, 69, 0.2)",
                  }}
                  onMouseEnter={(e) => {
                    if (!isCancelling) {
                      e.target.style.backgroundColor = "#c82333";
                      e.target.style.borderColor = "#bd2130";
                      e.target.style.transform = "translateY(-1px)";
                      e.target.style.boxShadow =
                        "0 4px 8px rgba(220, 53, 69, 0.3)";
                    }
                  }}
                  onMouseLeave={(e) => {
                    if (!isCancelling) {
                      e.target.style.backgroundColor = "#dc3545";
                      e.target.style.borderColor = "#dc3545";
                      e.target.style.transform = "translateY(0)";
                      e.target.style.boxShadow =
                        "0 2px 4px rgba(220, 53, 69, 0.2)";
                    }
                  }}
                >
                  {isCancelling ? "취소 처리 중..." : "🚫 주문 취소"}
                </Button>
              ) : (
                <Button
                  variant="danger"
                  onClick={handleCloseRecruit}
                  style={{ borderRadius: "6px", padding: "8px 24px" }}
                >
                  모집 종료
                </Button>
              )}
            </div>
            <Button
              variant="primary"
              onClick={handleSave}
              disabled={isSaving}
              style={{ borderRadius: "6px", padding: "8px 24px" }}
            >
              {isSaving ? "저장 중..." : "저장"}
            </Button>
          </div>
        </Card.Body>
      </Card>

      {/* 모집 종료 모달 */}
      <CloseRecruitModal
        show={showCloseModal}
        onHide={() => setShowCloseModal(false)}
        onConfirm={handleConfirmClose}
        productInfo={productInfo}
      />
    </Stack>
  );
};

export default AdminGroupBuyDetail;
