import { Button, Card, Form, Stack, Image } from "react-bootstrap";
import styles from "./AdminProducts.module.scss";
import { useState, useEffect } from "react";
import { ReactComponent as InfoBrownIcon } from "../../../assets/images/icon/info-brown.svg";
import { ReactComponent as QuestionIcon } from "../../../assets/images/icon/question.svg";
import { ReactComponent as ArrowLeftIcon } from "../../../assets/images/icon/arrow-left-black.svg";
import { useNavigate, useParams } from "react-router-dom";
import GroupBuyRegisterModal from "./GroupBuyRegisterModal";
import axios from "axios";

const categoryOptions = [
  { value: "FASHION_CLOTHES", label: "패션 의류" },
  { value: "FASHION_ACCESSORY", label: "패션 액세서리" },
  { value: "BEAUTY", label: "뷰티" },
  { value: "DIGITAL_APPLIANCE", label: "디지털/가전" },
  { value: "FURNITURE", label: "가구" },
  { value: "LIVING", label: "리빙" },
  { value: "FOOD", label: "식품" },
  { value: "SPORTS", label: "스포츠" },
  { value: "CAR", label: "자동차" },
  { value: "BOOK", label: "도서" },
  { value: "KIDS", label: "키즈" },
  { value: "PET", label: "반려동물" },
];

const keywordOptions = [
  // 패션 의류
  { value: "TSHIRT", label: "티셔츠" },
  { value: "DRESS", label: "드레스" },
  { value: "SHIRT", label: "셔츠" },
  { value: "OUTER", label: "아우터" },
  { value: "PANTS", label: "바지" },

  // 패션 액세서리
  { value: "SHOES", label: "신발" },
  { value: "BAG", label: "가방" },
  { value: "WALLET", label: "지갑" },
  { value: "HAT", label: "모자" },
  { value: "ACCESSORY", label: "액세서리" },

  // 뷰티
  { value: "SKINCARE", label: "스킨케어" },
  { value: "MASK_PACK", label: "마스크팩" },
  { value: "MAKEUP", label: "메이크업" },
  { value: "PERFUME", label: "향수" },
  { value: "HAIR_CARE", label: "헤어케어" },

  // 디지털/가전
  { value: "SMARTPHONE", label: "스마트폰" },
  { value: "TABLET", label: "태블릿" },
  { value: "LAPTOP", label: "노트북" },
  { value: "TV", label: "TV" },
  { value: "REFRIGERATOR", label: "냉장고" },
  { value: "WASHING_MACHINE", label: "세탁기" },

  // 가구
  { value: "BED", label: "침대" },
  { value: "SOFA", label: "소파" },
  { value: "TABLE", label: "테이블" },
  { value: "CHAIR", label: "의자" },
  { value: "LIGHTING", label: "조명" },

  // 리빙
  { value: "BODY_CARE", label: "바디케어" },
  { value: "SUPPLEMENT", label: "건강보조식품" },
  { value: "TOOTHPASTE", label: "치약" },
  { value: "VACUUM_CLEANER", label: "청소기" },
  { value: "DAILY_GOODS", label: "생활용품" },

  // 식품
  { value: "FRUIT", label: "과일" },
  { value: "VEGETABLE", label: "채소" },
  { value: "MEAT", label: "육류" },
  { value: "SIDE_DISH", label: "반찬" },
  { value: "INSTANT_FOOD", label: "즉석식품" },
  { value: "BEVERAGE", label: "음료" },

  // 스포츠
  { value: "SPORTSWEAR", label: "스포츠웨어" },
  { value: "SNEAKERS", label: "운동화" },
  { value: "EQUIPMENT", label: "운동기구" },
  { value: "GOLF", label: "골프" },
  { value: "SWIMMING", label: "수영" },

  // 자동차
  { value: "AUTO_ACCESSORY", label: "자동차용품" },
  { value: "CAR_CARE", label: "자동차관리" },
  { value: "TOOLS", label: "공구" },
  { value: "HAND_TOOL", label: "수공구" },
  { value: "TIRE", label: "타이어" },

  // 도서
  { value: "NOVEL", label: "소설" },
  { value: "SELF_DEVELOP", label: "자기계발" },
  { value: "COMIC", label: "만화" },
  { value: "ALBUM", label: "앨범" },
  { value: "DVD", label: "DVD" },

  // 키즈
  { value: "BABY_CLOTHES", label: "유아복" },
  { value: "CHILD_CLOTHES", label: "아동복" },
  { value: "TOY", label: "장난감" },
  { value: "KIDS_BOOKS", label: "아동도서" },
  { value: "BABY_GOODS", label: "육아용품" },

  // 반려동물
  { value: "DOG_FOOD", label: "강아지사료" },
  { value: "CAT_FOOD", label: "고양이사료" },
  { value: "PET_SNACK", label: "반려동물간식" },
  { value: "PET_TOY", label: "반려동물장난감" },
  { value: "PET_HYGIENE", label: "반려동물위생용품" },
];

const roundOptions = [
  { value: "1", label: "1회차" },
  { value: "2", label: "2회차" },
  { value: "3", label: "3회차" },
  { value: "4", label: "4회차" },
  { value: "5", label: "5회차" },
];

const AdminProductsDetail = () => {
  const navigate = useNavigate();
  const { productId } = useParams();
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [product, setProduct] = useState(null);
  const [productTitle, setProductTitle] = useState("");
  const [selectedCategory, setSelectedCategory] = useState(null);
  const [selectedKeyword, setSelectedKeyword] = useState(null);
  const [selectedRound, setSelectedRound] = useState(null);
  const [productPrice, setProductPrice] = useState("");
  const [productDesc, setProductDesc] = useState("");
  const [productImages, setProductImages] = useState([]);
  const [newImageFiles, setNewImageFiles] = useState([]); // 새로 업로드된 파일들
  const [endDate, setEndDate] = useState("");
  const [endTime, setEndTime] = useState("");
  const [minimumPerson, setMinimumPerson] = useState("");
  const [modalOpen, setModalOpen] = useState(false);

  // API 호출하여 상품 정보 가져오기
  useEffect(() => {
    const fetchProduct = async () => {
      try {
        setLoading(true);
        const response = await axios.get(`/api/v1/products/${productId}`);
        const productData = response.data.data;
        setProduct(productData);

        // 폼 필드에 데이터 설정
        setProductTitle(productData.title || "");
        setProductPrice(productData.price?.toString() || "");
        setProductDesc(productData.description || "");

        // 카테고리 설정 - 디버깅 로그 추가
        if (productData.category) {
          console.log("=== 카테고리 설정 디버깅 ===");
          console.log("API에서 받은 전체 응답:", response.data);
          console.log("API에서 받은 카테고리 데이터:", productData.category);
          console.log("categoryType:", productData.category.categoryType);
          console.log("keyword:", productData.category.keyword);

          // 메인 카테고리 찾기
          const category = categoryOptions.find((opt) => {
            console.log(
              `비교: ${opt.value} === ${productData.category.categoryType}`
            );
            return opt.value === productData.category.categoryType;
          });
          console.log("찾은 메인 카테고리:", category);

          if (category) {
            console.log("메인 카테고리 설정 시도:", category);
            setSelectedCategory(category);

            // 설정 후 확인
            setTimeout(() => {
              console.log("메인 카테고리 설정 후 상태:", selectedCategory);
            }, 100);
          } else {
            console.error(
              "메인 카테고리를 찾을 수 없습니다:",
              productData.category.categoryType
            );
            console.log("사용 가능한 카테고리 옵션들:", categoryOptions);
          }

          // 세부 카테고리 찾기
          const keyword = keywordOptions.find((opt) => {
            console.log(
              `키워드 비교: ${opt.value} === ${productData.category.keyword}`
            );
            return opt.value === productData.category.keyword;
          });
          console.log("찾은 세부 카테고리:", keyword);

          if (keyword) {
            console.log("세부 카테고리 설정 시도:", keyword);
            setSelectedKeyword(keyword);

            // 설정 후 확인
            setTimeout(() => {
              console.log("세부 카테고리 설정 후 상태:", selectedKeyword);
            }, 100);
          } else {
            console.error(
              "세부 카테고리를 찾을 수 없습니다:",
              productData.category.keyword
            );
            console.log("사용 가능한 키워드 옵션들:", keywordOptions);
          }
        } else {
          console.log("상품에 카테고리 정보가 없습니다.");
        }

        // 이미지 설정
        if (productData.imageUrl && productData.imageUrl.length > 0) {
          setProductImages(productData.imageUrl);
        }

        // 공동구매 정보가 있다면 설정
        if (productData.groupBuy) {
          setMinimumPerson(
            productData.groupBuy.minimumPeople?.toString() || ""
          );
          if (productData.groupBuy.endDate) {
            const endDateTime = new Date(productData.groupBuy.endDate);
            setEndDate(endDateTime.toISOString().split("T")[0]);
            setEndTime(
              endDateTime.toTimeString().split(" ")[0].substring(0, 5)
            );
          }
        }
      } catch (error) {
        console.error("상품 정보를 가져오는데 실패했습니다:", error);
        alert("상품 정보를 가져오는데 실패했습니다.");
        navigate("/admin/products");
      } finally {
        setLoading(false);
      }
    };

    if (productId) {
      fetchProduct();
    }
  }, [productId, navigate]);

  // 선택된 카테고리 상태 모니터링을 위한 useEffect 추가 (fetchProduct useEffect 다음에 추가)
  useEffect(() => {
    console.log("=== 카테고리 상태 변경 ===");
    console.log("selectedCategory:", selectedCategory);
    console.log("selectedKeyword:", selectedKeyword);
  }, [selectedCategory, selectedKeyword]);

  // 상품 정보 저장(수정) - PATCH /products/{productId}
  const handleSave = async () => {
    try {
      setSaving(true);

      // FormData 생성
      const formData = new FormData();

      // request 데이터를 JSON으로 추가
      const requestData = {
        title: productTitle,
        price: Number.parseInt(productPrice),
        description: productDesc,
        category: {
          categoryType: selectedCategory?.value,
          keyword: selectedKeyword?.value,
        },
      };

      // JSON 데이터를 Blob으로 변환하여 추가
      formData.append(
        "request",
        new Blob([JSON.stringify(requestData)], { type: "application/json" })
      );

      // 새로 업로드된 이미지 파일들 추가
      if (newImageFiles.length > 0) {
        newImageFiles.forEach((file) => {
          formData.append("images", file);
        });
      }

      // PATCH API 호출 (multipart/form-data)
      await axios.patch(`/api/v1/products/${productId}`, formData, {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      });
      alert("상품 정보가 저장되었습니다.");
    } catch (error) {
      console.error("상품 저장에 실패했습니다:", error);

      // 에러 메시지 개선
      if (error.response?.data?.message) {
        alert(`상품 저장에 실패했습니다: ${error.response.data.message}`);
      } else {
        alert("상품 저장에 실패했습니다.");
      }
    } finally {
      setSaving(false);
    }
  };

  // 공동구매 등록 성공 시 호출되는 콜백
  const handleGroupBuySuccess = () => {
    setModalOpen(false);
    navigate("/admin/products");
  };

  // 공구 진행 버튼 클릭 시 유효성 검사
  const handleGroupBuyClick = () => {
    // 디버깅을 위한 상세 로그
    console.log("=== 공구 진행 버튼 클릭 ===");
    console.log("selectedRound:", selectedRound);
    console.log("selectedRound?.value:", selectedRound?.value);
    console.log("minimumPerson:", minimumPerson);
    console.log("endDate:", endDate);
    console.log("endTime:", endTime);

    // 필수 필드 검증
    if (!selectedRound || !selectedRound.value) {
      console.log("진행 회차 검증 실패");
      alert("진행 회차를 선택해주세요.");
      return;
    }
    if (!minimumPerson || minimumPerson.trim() === "") {
      console.log("최소 참여 인원 검증 실패");
      alert("최소 참여 인원을 입력해주세요.");
      return;
    }
    if (!endDate || !endTime) {
      console.log("마감 일시 검증 실패");
      alert("마감 일시를 입력해주세요.");
      return;
    }

    console.log("모든 검증 통과!");
    setModalOpen(true);
  };

  // 이미지 업로드 핸들러
  const handleImageUpload = (e) => {
    const files = Array.from(e.target.files);
    if (files.length > 0) {
      // 새로 업로드된 파일들을 상태에 저장
      setNewImageFiles([...newImageFiles, ...files]);

      // 미리보기를 위해 URL 생성
      const newImages = files.map((file) => URL.createObjectURL(file));
      setProductImages([...productImages, ...newImages]);
    }
  };

  // 이미지 삭제 핸들러
  const handleRemoveImage = (index) => {
    const newImages = [...productImages];
    const removedImage = newImages.splice(index, 1)[0];
    setProductImages(newImages);

    // 새로 업로드된 파일인 경우 newImageFiles에서도 제거
    if (removedImage.startsWith("blob:")) {
      const fileIndex = newImageFiles.findIndex(
        (file) => URL.createObjectURL(file) === removedImage
      );
      if (fileIndex !== -1) {
        const newFiles = [...newImageFiles];
        newFiles.splice(fileIndex, 1);
        setNewImageFiles(newFiles);
      }
    }
  };

  // 진행 회차 선택 핸들러 - Form.Select용으로 수정
  const handleRoundChange = (e) => {
    const selectedValue = e.target.value;
    console.log("진행 회차 변경 - 선택된 값:", selectedValue);

    if (selectedValue) {
      const selectedOption = roundOptions.find(
        (option) => option.value === selectedValue
      );
      console.log("찾은 옵션:", selectedOption);
      setSelectedRound(selectedOption);
    } else {
      setSelectedRound(null);
    }
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
      </div>
    );
  }

  if (!product) {
    return (
      <Card>
        <Card.Body className="text-center p-5">
          <h4>상품을 찾을 수 없습니다.</h4>
          <Button onClick={() => navigate("/admin/products")} className="mt-3">
            목록으로 돌아가기
          </Button>
        </Card.Body>
      </Card>
    );
  }

  return (
    <Stack direction={"vertical"} gap={2} className={"m-3"}>
      <Card>
        <Card.Body className={"m-4"}>
          <div
            onClick={() => navigate("/admin/products")}
            style={{
              cursor: "pointer",
              display: "inline-flex",
              alignItems: "center",
              marginBottom: "15px",
            }}
          >
            <ArrowLeftIcon width={18} height={18} style={{ marginRight: 10 }} />
            돌아가기
          </div>
          <h3>등록 상품 관리</h3>
          <desc className={"text-gray-300"}>
            등록된 상품에 대한 처리를 합니다.
          </desc>
        </Card.Body>
      </Card>

      <Card>
        <Card.Header>
          <h4>기본 정보</h4>
        </Card.Header>
        <Card.Body className={"m-3"}>
          <Form.Group controlId={"productTitle"} className="mb-3">
            <Form.Label>상품명</Form.Label>
            <Form.Control
              type="text"
              value={productTitle}
              onChange={(e) => setProductTitle(e.target.value)}
            />
          </Form.Group>

          <Stack direction={"horizontal"} gap={3} className="mb-3">
            <Form.Group controlId={"category"} className="flex-fill">
              <Form.Label>카테고리</Form.Label>
              {/* 임시로 Form.Select 사용 */}
              <Form.Select
                value={selectedCategory?.value || ""}
                onChange={(e) => {
                  const selectedValue = e.target.value;
                  const category = categoryOptions.find(
                    (opt) => opt.value === selectedValue
                  );
                  console.log("카테고리 선택 변경:", selectedValue, category);
                  setSelectedCategory(category);
                }}
              >
                <option value="">카테고리 선택</option>
                {categoryOptions.map((option) => (
                  <option key={option.value} value={option.value}>
                    {option.label}
                  </option>
                ))}
              </Form.Select>
            </Form.Group>
            <Form.Group controlId={"keyword"} className="flex-fill">
              <Form.Label>세부 카테고리</Form.Label>
              {/* 임시로 Form.Select 사용 */}
              <Form.Select
                value={selectedKeyword?.value || ""}
                onChange={(e) => {
                  const selectedValue = e.target.value;
                  const keyword = keywordOptions.find(
                    (opt) => opt.value === selectedValue
                  );
                  console.log("키워드 선택 변경:", selectedValue, keyword);
                  setSelectedKeyword(keyword);
                }}
              >
                <option value="">세부 카테고리 선택</option>
                {keywordOptions.map((option) => (
                  <option key={option.value} value={option.value}>
                    {option.label}
                  </option>
                ))}
              </Form.Select>
            </Form.Group>
          </Stack>

          <Stack direction={"horizontal"} gap={3} className="mb-3">
            <Form.Group controlId={"round"} className="flex-fill">
              <Form.Label>진행 회차</Form.Label>
              {/* ThemedSelect 대신 일반 Form.Select 사용 */}
              <Form.Select
                value={selectedRound?.value || ""}
                onChange={handleRoundChange}
              >
                <option value="">회차 선택</option>
                {roundOptions.map((option) => (
                  <option key={option.value} value={option.value}>
                    {option.label}
                  </option>
                ))}
              </Form.Select>
            </Form.Group>
            <Form.Group controlId={"price"} className="flex-fill">
              <Form.Label>판매가</Form.Label>
              <div style={{ position: "relative" }}>
                <Form.Control
                  type="text"
                  value={productPrice}
                  onChange={(e) => setProductPrice(e.target.value)}
                />
                <span className={styles.textInControl}>원</span>
              </div>
            </Form.Group>
          </Stack>

          <Form.Group className={"mb-3"}>
            <Form.Label>상품 설명</Form.Label>
            <Form.Control
              as="textarea"
              rows={4}
              placeholder="상품에 대한 상세 설명을 입력하세요."
              value={productDesc}
              onChange={(e) => setProductDesc(e.target.value)}
            />
          </Form.Group>
        </Card.Body>
      </Card>

      <Card>
        <Card.Header>
          <h4>상품 이미지</h4>
        </Card.Header>
        <Card.Body className={"m-3"}>
          <div className="d-flex flex-wrap gap-3">
            {/* 이미지 표시 영역 */}
            {productImages.map((imageUrl, index) => (
              <div
                key={index}
                className="position-relative"
                style={{
                  width: "150px",
                  height: "150px",
                  border: "1px dashed #ccc",
                  borderRadius: "4px",
                }}
              >
                <Image
                  src={imageUrl || "/placeholder.svg"}
                  alt={`상품 이미지 ${index + 1}`}
                  style={{ width: "100%", height: "100%", objectFit: "cover" }}
                />
                <Button
                  variant="danger"
                  size="sm"
                  className="position-absolute top-0 end-0 m-1"
                  onClick={() => handleRemoveImage(index)}
                >
                  X
                </Button>
                {index === 0 && (
                  <div
                    className="position-absolute bottom-0 start-0 bg-primary text-white px-2 py-1 m-1"
                    style={{ fontSize: "12px" }}
                  >
                    대표 이미지
                  </div>
                )}
              </div>
            ))}

            {/* 이미지 추가 버튼 */}
            {productImages.length < 10 && (
              <label
                htmlFor="image-upload"
                className="d-flex justify-content-center align-items-center"
                style={{
                  width: "150px",
                  height: "150px",
                  border: "1px dashed #ccc",
                  borderRadius: "4px",
                  cursor: "pointer",
                }}
              >
                <div className="text-center">
                  <div className="fs-1">+</div>
                  <div className="text-muted">이미지 추가</div>
                </div>
                <input
                  id="image-upload"
                  type="file"
                  accept="image/*"
                  multiple
                  onChange={handleImageUpload}
                  style={{ display: "none" }}
                />
              </label>
            )}
          </div>
        </Card.Body>
        <Card.Footer className={"mx-3"}>
          <desc className={"text-gray-300"}>
            첫번째 이미지가 대표 이미지로 사용됩니다. 최대 10장까지 등록
            가능합니다.
          </desc>
        </Card.Footer>
      </Card>

      <Card>
        <Card.Header>
          <h4>공동구매 설정</h4>
        </Card.Header>
        <Card.Body className={"m-3"}>
          <Stack direction={"horizontal"} gap={3} className={"mb-3"}>
            <Form.Group controlId={"endDateTime"} className="flex-fill">
              <Form.Label>마감 일시</Form.Label>
              <Stack direction={"horizontal"} gap={2}>
                <Form.Control
                  type="date"
                  value={endDate}
                  onChange={(e) => setEndDate(e.target.value)}
                />
                <Form.Control
                  type="time"
                  value={endTime}
                  onChange={(e) => setEndTime(e.target.value)}
                />
              </Stack>
            </Form.Group>
            <Form.Group controlId={"minimumPerson"} className="flex-fill">
              <Form.Label>최소 참여 인원</Form.Label>
              <div style={{ position: "relative" }}>
                <Form.Control
                  type="text"
                  value={minimumPerson}
                  onChange={(e) => setMinimumPerson(e.target.value)}
                />
                <span className={styles.textInControl}>명</span>
              </div>
            </Form.Group>
          </Stack>

          <div className={styles.groupBuyDesc}>
            <div className={styles.groupBuyDescTitle}>
              <QuestionIcon style={{ marginRight: "8px" }} />
              공동구매 진행 안내
            </div>
            <desc className={styles.groupBuyDescSubTitle}>
              상품의 마감 일시까지 설정한 최소 참여인원에 도달해야 공동구매가
              성사됩니다. 설정한 마감 일시까지 최소 참여인원에 도달하지 못하면
              공동구매는 무산되며, 미결제 시 자동으로 환불됩니다.
            </desc>
          </div>
        </Card.Body>
      </Card>

      <Card>
        <Card.Body className={"mx-3"}>
          <Stack direction={"horizontal"} className={"justify-content-between"}>
            <Stack direction={"horizontal"} gap={2}>
              <Button variant={"primary"} onClick={handleGroupBuyClick}>
                공구 진행
              </Button>
              <div
                className={styles.groupBuyDesc}
                style={{
                  background: "#FEE2E2",
                  margin: 0,
                  padding: "10px 15px",
                }}
              >
                <div
                  className={styles.groupBuyDescSubTitle}
                  style={{ color: "#991B1B", fontSize: "12px" }}
                >
                  <InfoBrownIcon
                    style={{ color: "#F04343", marginRight: "5px" }}
                  />
                  반드시 공동구매 진행전 상품 정보를 저장해주세요.
                </div>
              </div>
            </Stack>
            <Stack direction={"horizontal"} gap={2}>
              <Button
                className={styles.detailButton}
                variant={""}
                onClick={() => navigate("/admin/products")}
              >
                취소
              </Button>
              <Button
                variant={"primary"}
                onClick={handleSave}
                disabled={saving}
              >
                {saving ? "저장 중..." : "저장"}
              </Button>
            </Stack>
          </Stack>
        </Card.Body>
      </Card>

      <GroupBuyRegisterModal
        show={modalOpen}
        onHide={() => setModalOpen(false)}
        onConfirm={handleGroupBuySuccess}
        productTitle={productTitle}
        productId={productId}
        endDate={endDate}
        endTime={endTime}
        minimumPerson={minimumPerson}
        round={selectedRound?.value}
      />
    </Stack>
  );
};

export default AdminProductsDetail;
