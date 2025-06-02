import { useNavigate, useSearchParams } from "react-router-dom";
import { Button, Card, Form, Stack } from "react-bootstrap";
import CreateRequestImageBox from "./CreateRequestImageBox";
import { ReactComponent as QuestionIcon } from "../../../assets/images/icon/question.svg";
import { useState, useEffect } from "react";
import styles from "./MyPageRequests.module.scss";

// 하드코딩된 카테고리 데이터 (API가 작동하지 않을 경우를 대비)
const CATEGORY_DATA = [
  { categoryId: 1, categoryType: "FASHION_CLOTHES", keyword: "TSHIRT" },
  { categoryId: 2, categoryType: "FASHION_CLOTHES", keyword: "DRESS" },
  { categoryId: 3, categoryType: "FASHION_CLOTHES", keyword: "SHIRT" },
  { categoryId: 4, categoryType: "FASHION_CLOTHES", keyword: "OUTER" },
  { categoryId: 5, categoryType: "FASHION_CLOTHES", keyword: "PANTS" },
  { categoryId: 6, categoryType: "FASHION_ACCESSORY", keyword: "SHOES" },
  { categoryId: 7, categoryType: "FASHION_ACCESSORY", keyword: "BAG" },
  { categoryId: 8, categoryType: "FASHION_ACCESSORY", keyword: "WALLET" },
  { categoryId: 9, categoryType: "FASHION_ACCESSORY", keyword: "HAT" },
  { categoryId: 10, categoryType: "FASHION_ACCESSORY", keyword: "ACCESSORY" },
  { categoryId: 11, categoryType: "BEAUTY", keyword: "SKINCARE" },
  { categoryId: 12, categoryType: "BEAUTY", keyword: "MASK_PACK" },
  { categoryId: 13, categoryType: "BEAUTY", keyword: "MAKEUP" },
  { categoryId: 14, categoryType: "BEAUTY", keyword: "PERFUME" },
  { categoryId: 15, categoryType: "BEAUTY", keyword: "HAIR_CARE" },
  { categoryId: 16, categoryType: "DIGITAL_APPLIANCE", keyword: "SMARTPHONE" },
  { categoryId: 17, categoryType: "DIGITAL_APPLIANCE", keyword: "TABLET" },
  { categoryId: 18, categoryType: "DIGITAL_APPLIANCE", keyword: "LAPTOP" },
  { categoryId: 19, categoryType: "DIGITAL_APPLIANCE", keyword: "TV" },
  {
    categoryId: 20,
    categoryType: "DIGITAL_APPLIANCE",
    keyword: "REFRIGERATOR",
  },
  {
    categoryId: 21,
    categoryType: "DIGITAL_APPLIANCE",
    keyword: "WASHING_MACHINE",
  },
  { categoryId: 22, categoryType: "FURNITURE", keyword: "BED" },
  { categoryId: 23, categoryType: "FURNITURE", keyword: "SOFA" },
  { categoryId: 24, categoryType: "FURNITURE", keyword: "TABLE" },
  { categoryId: 25, categoryType: "FURNITURE", keyword: "CHAIR" },
  { categoryId: 26, categoryType: "FURNITURE", keyword: "LIGHTING" },
  { categoryId: 27, categoryType: "LIVING", keyword: "BODY_CARE" },
  { categoryId: 28, categoryType: "LIVING", keyword: "SUPPLEMENT" },
  { categoryId: 29, categoryType: "LIVING", keyword: "TOOTHPASTE" },
  { categoryId: 30, categoryType: "LIVING", keyword: "VACUUM_CLEANER" },
  { categoryId: 31, categoryType: "LIVING", keyword: "DAILY_GOODS" },
  { categoryId: 32, categoryType: "FOOD", keyword: "FRUIT" },
  { categoryId: 33, categoryType: "FOOD", keyword: "VEGETABLE" },
  { categoryId: 34, categoryType: "FOOD", keyword: "MEAT" },
  { categoryId: 35, categoryType: "FOOD", keyword: "SIDE_DISH" },
  { categoryId: 36, categoryType: "FOOD", keyword: "INSTANT_FOOD" },
  { categoryId: 37, categoryType: "FOOD", keyword: "BEVERAGE" },
  { categoryId: 38, categoryType: "SPORTS", keyword: "SPORTSWEAR" },
  { categoryId: 39, categoryType: "SPORTS", keyword: "SNEAKERS" },
  { categoryId: 40, categoryType: "SPORTS", keyword: "EQUIPMENT" },
  { categoryId: 41, categoryType: "SPORTS", keyword: "GOLF" },
  { categoryId: 42, categoryType: "SPORTS", keyword: "SWIMMING" },
  { categoryId: 43, categoryType: "CAR", keyword: "AUTO_ACCESSORY" },
  { categoryId: 44, categoryType: "CAR", keyword: "CAR_CARE" },
  { categoryId: 45, categoryType: "CAR", keyword: "TOOLS" },
  { categoryId: 46, categoryType: "CAR", keyword: "HAND_TOOL" },
  { categoryId: 47, categoryType: "CAR", keyword: "TIRE" },
  { categoryId: 48, categoryType: "BOOK", keyword: "NOVEL" },
  { categoryId: 49, categoryType: "BOOK", keyword: "SELF_DEVELOP" },
  { categoryId: 50, categoryType: "BOOK", keyword: "COMIC" },
  { categoryId: 51, categoryType: "BOOK", keyword: "ALBUM" },
  { categoryId: 52, categoryType: "BOOK", keyword: "DVD" },
  { categoryId: 53, categoryType: "KIDS", keyword: "BABY_CLOTHES" },
  { categoryId: 54, categoryType: "KIDS", keyword: "CHILD_CLOTHES" },
  { categoryId: 55, categoryType: "KIDS", keyword: "TOY" },
  { categoryId: 56, categoryType: "KIDS", keyword: "KIDS_BOOKS" },
  { categoryId: 57, categoryType: "KIDS", keyword: "BABY_GOODS" },
  { categoryId: 58, categoryType: "PET", keyword: "DOG_FOOD" },
  { categoryId: 59, categoryType: "PET", keyword: "CAT_FOOD" },
  { categoryId: 60, categoryType: "PET", keyword: "PET_SNACK" },
  { categoryId: 61, categoryType: "PET", keyword: "PET_TOY" },
  { categoryId: 62, categoryType: "PET", keyword: "PET_HYGIENE" },
];

const MyPageRequest = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const productRequestId = searchParams.get("edit"); // URL 쿼리에서 edit 파라미터 가져오기
  const [productTitle, setProductTitle] = useState("");
  const [productDesc, setProductDesc] = useState("");
  const [productUrl, setProductUrl] = useState("");
  const [reviewImageFileList, setReviewFileImageList] = useState([]);
  const [imageUrls, setImageUrls] = useState([]);
  const [category, setCategory] = useState(null);
  const [selectedCategory, setSelectedCategory] = useState(null);
  const [selectedKeyword, setSelectedKeyword] = useState(null);
  const [categories, setCategories] = useState(CATEGORY_DATA); // 기본값으로 하드코딩된 데이터 사용
  const [categoryOptions, setCategoryOptions] = useState([]);
  const [keywordOptions, setKeywordOptions] = useState([]);
  const [loading, setLoading] = useState(false);
  const [isEditMode, setIsEditMode] = useState(false);

  // 쿠키에서 토큰을 가져오는 함수
  const getTokenFromCookie = (name) => {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) {
      return parts.pop()?.split(";").shift() || null;
    }
    return null;
  };

  // 카테고리 타입을 한글로 변환
  const getCategoryTypeLabel = (categoryType) => {
    const categoryMap = {
      FASHION_CLOTHES: "패션의류",
      FASHION_ACCESSORY: "패션잡화",
      BEAUTY: "뷰티",
      DIGITAL_APPLIANCE: "디지털가전",
      FURNITURE: "가구",
      LIVING: "생활용품",
      FOOD: "식품",
      SPORTS: "스포츠",
      CAR: "자동차용품",
      BOOK: "도서",
      KIDS: "키즈",
      PET: "반려동물",
    };
    return categoryMap[categoryType] || categoryType;
  };

  // 키워드를 한글로 변환
  const getKeywordLabel = (keyword) => {
    const keywordMap = {
      // 패션의류
      TSHIRT: "티셔츠",
      DRESS: "원피스",
      SHIRT: "셔츠",
      OUTER: "아우터",
      PANTS: "바지",
      // 패션잡화
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
      // 디지털가전
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
      // 생활용품
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
      // 자동차용품
      AUTO_ACCESSORY: "자동차액세서리",
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
      BABY_CLOTHES: "유아의류",
      CHILD_CLOTHES: "아동의류",
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
    return keywordMap[keyword] || keyword;
  };

  // 카테고리 데이터 초기화 및 옵션 생성
  const initializeCategories = (data) => {
    // 카테고리 타입별로 그룹화
    const categoryTypes = [...new Set(data.map((cat) => cat.categoryType))];
    const categoryTypeOptions = categoryTypes.map((type) => ({
      value: type,
      label: getCategoryTypeLabel(type),
    }));
    setCategoryOptions(categoryTypeOptions);
  };

  // 카테고리 데이터 불러오기
  const fetchCategories = async () => {
    try {
      const baseUrl =
        process.env.REACT_APP_API_URL || "https://api.devapi.store";
      const url = `${baseUrl}/api/v1/categories`;

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
        credentials: "include",
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const result = await response.json();

      if (result.success && result.data) {
        setCategories(result.data);
        initializeCategories(result.data);
      }
    } catch (error) {
      console.error("카테고리 데이터 불러오기 실패:", error);
      console.log("하드코딩된 데이터를 사용합니다.");
      // API 호출 실패 시 하드코딩된 데이터 사용
      initializeCategories(CATEGORY_DATA);
    }
  };

  // 카테고리 타입 변경 시 키워드 옵션 업데이트
  const updateKeywordOptions = (categoryType) => {
    if (!categoryType) {
      setKeywordOptions([]);
      return;
    }

    // 선택된 categoryType에 해당하는 항목들 필터링
    const filteredCategories = categories.filter(
      (cat) => cat.categoryType === categoryType
    );

    // 키워드 옵션 생성
    const keywordOpts = filteredCategories.map((cat) => ({
      value: cat.keyword,
      label: getKeywordLabel(cat.keyword),
      categoryId: cat.categoryId,
    }));

    setKeywordOptions(keywordOpts);
  };

  // 상품 요청 데이터 불러오기
  const fetchRequestData = async (requestId) => {
    try {
      setLoading(true);

      const baseUrl =
        process.env.REACT_APP_API_URL || "https://api.devapi.store";
      const url = `${baseUrl}/api/v1/productRequests/${requestId}`;

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
        credentials: "include",
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const result = await response.json();

      if (result.success && result.data) {
        const data = result.data;
        console.log("불러온 요청 데이터:", data);

        // 폼 필드에 데이터 설정
        setProductTitle(data.title || "");
        setProductDesc(data.description || "");
        setProductUrl(data.productUrl || "");
        setCategory(data.category);
        setImageUrls(data.imageUrls || []);
        setIsEditMode(true);

        // 수정 모드일 때 카테고리 설정 (읽기 전용으로 표시)
        if (data.category) {
          const categoryOption = {
            value: data.category.categoryType,
            label: getCategoryTypeLabel(data.category.categoryType),
          };
          setSelectedCategory(categoryOption);

          const keywordOption = {
            value: data.category.keyword,
            label: getKeywordLabel(data.category.keyword),
            categoryId: data.category.categoryId,
          };
          setSelectedKeyword(keywordOption);
        }
      }
    } catch (error) {
      console.error("요청 데이터 불러오기 실패:", error);
      alert("요청 데이터를 불러오는데 실패했습니다.");
      navigate("/mypage/requests"); // 실패 시 목록으로 돌아가기
    } finally {
      setLoading(false);
    }
  };

  // 상품 요청 생성 함수
  const createProductRequest = async () => {
    try {
      // 필수 필드 검증
      if (!productTitle.trim()) {
        alert("상품명을 입력해주세요.");
        return;
      }

      if (!selectedCategory) {
        alert("카테고리를 선택해주세요.");
        return;
      }

      if (!selectedKeyword) {
        alert("세부 카테고리를 선택해주세요.");
        return;
      }

      if (!productUrl.trim()) {
        alert("상품 URL을 입력해주세요.");
        return;
      }

      if (!productDesc.trim()) {
        alert("상품 설명을 입력해주세요.");
        return;
      }

      setLoading(true);

      const baseUrl =
        process.env.REACT_APP_API_URL || "https://api.devapi.store";
      const url = `${baseUrl}/api/v1/productRequests`;

      const token =
        getTokenFromCookie("authToken") ||
        getTokenFromCookie("token") ||
        getTokenFromCookie("accessToken");

      // FormData 생성
      const formData = new FormData();

      // request 데이터 생성
      const requestData = {
        categoryId: selectedKeyword?.categoryId,
        title: productTitle.trim(),
        productUrl: productUrl.trim(),
        description: productDesc.trim(),
      };

      // request를 JSON 문자열로 변환하여 Blob으로 추가
      const requestBlob = new Blob([JSON.stringify(requestData)], {
        type: "application/json",
      });
      formData.append("request", requestBlob);

      // 이미지 파일들 추가
      if (reviewImageFileList && reviewImageFileList.length > 0) {
        reviewImageFileList.forEach((file, index) => {
          if (file) {
            formData.append("images", file);
          }
        });
      }

      const headers = {};
      if (token) {
        headers.Authorization = `Bearer ${token}`;
      }

      const response = await fetch(url, {
        method: "POST",
        headers: headers,
        body: formData,
        credentials: "include",
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(
          errorData.message || `HTTP error! status: ${response.status}`
        );
      }

      const result = await response.json();

      if (result.success) {
        alert("상품 등록 요청이 성공적으로 생성되었습니다.");
        navigate("/mypage/requests");
      } else {
        throw new Error(result.message || "요청 생성에 실패했습니다.");
      }
    } catch (error) {
      console.error("상품 요청 생성 중 오류 발생:", error);
      alert(`상품 요청 생성 중 오류가 발생했습니다: ${error.message}`);
    } finally {
      setLoading(false);
    }
  };

  // 상품 요청 수정 함수
  const updateProductRequest = async () => {
    try {
      // 필수 필드 검증
      if (!productTitle.trim()) {
        alert("상품명을 입력해주세요.");
        return;
      }

      if (!productDesc.trim()) {
        alert("상품 설명을 입력해주세요.");
        return;
      }

      if (!productUrl.trim()) {
        alert("상품 URL을 입력해주세요.");
        return;
      }

      setLoading(true);

      const baseUrl =
        process.env.REACT_APP_API_URL || "https://api.devapi.store";
      const url = `${baseUrl}/api/v1/productRequests/${productRequestId}`;

      const token =
        getTokenFromCookie("authToken") ||
        getTokenFromCookie("token") ||
        getTokenFromCookie("accessToken");

      // FormData 생성
      const formData = new FormData();

      // request 데이터 생성 (수정용 DTO 구조에 맞게)
      const requestData = {
        categoryId: category?.categoryId || null, // 기존 카테고리 ID 유지
        title: productTitle.trim(),
        productUrl: productUrl.trim(),
        description: productDesc.trim(),
        imageUrls: imageUrls || [], // 기존 이미지 URL들 유지
      };

      console.log("수정 요청 데이터:", requestData);

      // request를 JSON 문자열로 변환하여 Blob으로 추가
      const requestBlob = new Blob([JSON.stringify(requestData)], {
        type: "application/json",
      });
      formData.append("request", requestBlob);

      // 새로 추가된 이미지 파일들 추가
      if (reviewImageFileList && reviewImageFileList.length > 0) {
        reviewImageFileList.forEach((file, index) => {
          if (file) {
            console.log(`새 이미지 파일 ${index + 1} 추가:`, file.name);
            formData.append("images", file);
          }
        });
      }

      const headers = {};
      if (token) {
        headers.Authorization = `Bearer ${token}`;
      }

      console.log("수정 API 호출:", url);

      const response = await fetch(url, {
        method: "PATCH",
        headers: headers,
        body: formData,
        credentials: "include",
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(
          errorData.message || `HTTP error! status: ${response.status}`
        );
      }

      const result = await response.json();

      if (result.success) {
        alert("상품 등록 요청이 성공적으로 수정되었습니다.");
        navigate("/mypage/requests");
      } else {
        throw new Error(result.message || "요청 수정에 실패했습니다.");
      }
    } catch (error) {
      console.error("상품 요청 수정 중 오류 발생:", error);
      alert(`상품 요청 수정 중 오류가 발생했습니다: ${error.message}`);
    } finally {
      setLoading(false);
    }
  };

  // 컴포넌트 마운트 시 초기화
  useEffect(() => {
    // 하드코딩된 데이터로 초기화
    initializeCategories(CATEGORY_DATA);
    // API 호출 시도
    fetchCategories();
  }, []);

  // 수정 모드인지 확인하고 데이터 불러오기
  useEffect(() => {
    if (productRequestId) {
      fetchRequestData(productRequestId);
    }
  }, [productRequestId]);

  // 카테고리 타입 선택 핸들러
  const handleCategoryTypeChange = (selectedOption) => {
    const categoryType = selectedOption?.value;
    setSelectedCategory({
      value: categoryType,
      label: getCategoryTypeLabel(categoryType),
    });
    setSelectedKeyword(null);
    updateKeywordOptions(categoryType);
  };

  // 키워드 선택 핸들러
  const handleKeywordChange = (selectedOption) => {
    setSelectedKeyword(selectedOption);
  };

  // 폼 제출 핸들러
  const handleSubmit = () => {
    if (isEditMode) {
      updateProductRequest();
    } else {
      createProductRequest();
    }
  };

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

  return (
    <Stack direction={"vertical"} gap={2} className={"m-3"}>
      <h3>{isEditMode ? "상품 등록 요청 수정" : "상품 등록 요청"}</h3>

      <Card>
        <Card.Header>
          <h4>기본 정보</h4>
        </Card.Header>
        <Card.Body className={"m-3"}>
          <Form.Group controlId={"productTitle"} className="mb-3">
            <Form.Label>
              상품명<span style={{ color: "red" }}>*</span>
            </Form.Label>
            <Form.Control
              type="text"
              value={productTitle}
              onChange={(e) => setProductTitle(e.target.value)}
              placeholder="상품명을 입력하세요"
            />
          </Form.Group>

          <Stack direction={"horizontal"} gap={3} className="mb-3">
            <Form.Group controlId={"category"} className="flex-fill">
              <Form.Label>
                카테고리<span style={{ color: "red" }}>*</span>
              </Form.Label>
              {isEditMode && category ? (
                <Form.Control
                  type="text"
                  value={getCategoryTypeLabel(category.categoryType)}
                  readOnly
                />
              ) : (
                <Form.Select
                  value={selectedCategory?.value || ""}
                  onChange={(e) => {
                    const selectedValue = e.target.value;
                    const categoryOption = categoryOptions.find(
                      (opt) => opt.value === selectedValue
                    );
                    console.log(
                      "카테고리 선택 변경:",
                      selectedValue,
                      categoryOption
                    );
                    setSelectedCategory(categoryOption);
                    setSelectedKeyword(null); // 세부 카테고리 초기화
                    updateKeywordOptions(selectedValue);
                  }}
                >
                  <option value="">카테고리 선택</option>
                  {categoryOptions.map((option) => (
                    <option key={option.value} value={option.value}>
                      {option.label}
                    </option>
                  ))}
                </Form.Select>
              )}
            </Form.Group>
            <Form.Group controlId={"keyword"} className="flex-fill">
              <Form.Label>
                세부 카테고리<span style={{ color: "red" }}>*</span>
              </Form.Label>
              {isEditMode && category ? (
                <Form.Control
                  type="text"
                  value={getKeywordLabel(category.keyword)}
                  readOnly
                />
              ) : (
                <Form.Select
                  value={selectedKeyword?.value || ""}
                  onChange={(e) => {
                    const selectedValue = e.target.value;
                    const keywordOption = keywordOptions.find(
                      (opt) => opt.value === selectedValue
                    );
                    console.log(
                      "키워드 선택 변경:",
                      selectedValue,
                      keywordOption
                    );
                    setSelectedKeyword(keywordOption);
                  }}
                  disabled={!selectedCategory}
                >
                  <option value="">세부 카테고리 선택</option>
                  {keywordOptions.map((option) => (
                    <option key={option.value} value={option.value}>
                      {option.label}
                    </option>
                  ))}
                </Form.Select>
              )}
            </Form.Group>
          </Stack>

          <Form.Group controlId={"productUrl"} className="mb-3">
            <Form.Label>
              URL
              <span style={{ color: "#6c757d", fontSize: "0.875rem" }}>
                {" "}
                상품 구매를 위한 상품의 URL을 추가해주세요
              </span>
            </Form.Label>
            <Form.Control
              type="url"
              value={productUrl}
              onChange={(e) => setProductUrl(e.target.value)}
              placeholder="https://example.com/product"
            />
          </Form.Group>

          <Form.Group controlId={"productDesc"}>
            <Form.Label>
              상품 설명<span style={{ color: "red" }}>*</span>
            </Form.Label>
            <Form.Control
              as="textarea"
              rows={6}
              placeholder="상품에 대한 상세 설명을 입력하세요"
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
          <CreateRequestImageBox
            imageFileList={reviewImageFileList}
            setImageFileList={setReviewFileImageList}
            existingImageUrls={imageUrls}
          />
          <div
            className="mt-3"
            style={{ color: "#6c757d", fontSize: "0.875rem" }}
          >
            *상품 등록 참고를 위한 이미지 입니다. 최대 10장까지 등록 가능합니다.
          </div>
        </Card.Body>
      </Card>

      <div className={styles.groupBuyDesc}>
        <div className={styles.groupBuyDescTitle}>
          <QuestionIcon /> 공동구매 진행 안내
        </div>
        <desc className={styles.groupBuyDescSubTitle}>
          설정한 마감 일시까지 최소 주문 수량이 모이지 않을 경우 공동구매가
          성사되지 않습니다. 성사된 경우 자동으로 결제가 진행되며, 미 달성 시
          자동으로 결제가 취소됩니다.
        </desc>
      </div>

      <Card style={{ backgroundColor: "transparent" }}>
        <Card.Body className={"mx-3 ms-auto"}>
          <Stack direction={"horizontal"} className={"justify-content-between"}>
            <Stack direction={"horizontal"} gap={5}>
              <Button
                className={styles.detailButton}
                variant={""}
                onClick={() => navigate("/mypage/requests")}
              >
                취소
              </Button>
              <Button
                variant={"primary"}
                onClick={handleSubmit}
                disabled={loading}
              >
                {loading ? "처리중..." : isEditMode ? "수정" : "완료"}
              </Button>
            </Stack>
          </Stack>
        </Card.Body>
      </Card>
    </Stack>
  );
};

export default MyPageRequest;
