import { useState, useEffect, useRef } from "react";
import { Card, Nav, Button, Modal, Alert, Form } from "react-bootstrap";
import "./MyPageReviews.css";

const MyPageReviews = () => {
  const [activeTab, setActiveTab] = useState("writable");
  const [writableReviews, setWritableReviews] = useState([]);
  const [writtenReviews, setWrittenReviews] = useState([]);
  const [loading, setLoading] = useState(false);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [selectedReviewId, setSelectedReviewId] = useState(null);
  const [error, setError] = useState("");

  // 리뷰 작성/수정 모달 관련 상태
  const [showReviewModal, setShowReviewModal] = useState(false);
  const [modalMode, setModalMode] = useState("create"); // "create" 또는 "edit"
  const [selectedItem, setSelectedItem] = useState(null);
  const [reviewForm, setReviewForm] = useState({
    rating: 0,
    comment: "",
    images: [],
    selectedFiles: [], // 선택된 파일들
  });
  const [reviewLoading, setReviewLoading] = useState(false);
  const fileInputRef = useRef(null);

  const API_BASE_URL = process.env.REACT_APP_API_URL;

  // 작성 가능한 리뷰 조회
  const fetchWritableReviews = async () => {
    try {
      setLoading(true);
      setError("");

      const response = await fetch(
        `${API_BASE_URL}/api/v1/histories/writable`,
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
          },
          credentials: "include",
        }
      );

      if (response.ok) {
        const result = await response.json();

        if (result.success && result.data && result.data.content) {
          const transformedData = result.data.content.map((item) => ({
            id: item.historyId,
            historyId: item.historyId,
            productId: item.productDto.productId,
            productName: item.productDto.title,
            productDescription: item.productDto.description,
            imageUrl:
              item.productDto.imageUrl && item.productDto.imageUrl.length > 0
                ? item.productDto.imageUrl[0]
                : "/placeholder.svg?height=140&width=140",
            price: item.productDto.price,
            purchaseDate: new Date(item.order.createdAt)
              .toLocaleDateString("ko-KR", {
                year: "numeric",
                month: "2-digit",
                day: "2-digit",
              })
              .replace(/\./g, ".")
              .replace(/\s/g, ""),
            orderId: item.order.orderId,
            quantity: item.order.quantity,
            totalPrice: item.order.totalPrice,
            orderStatus: item.order.status,
            groupBuyId: item.groupBuyId,
            writable: item.writable,
          }));

          setWritableReviews(transformedData);
        } else {
          setError("작성 가능한 리뷰 데이터를 불러오는데 실패했습니다.");
        }
      } else {
        const errorData = await response.json().catch(() => ({}));
        setError(
          errorData.message || "작성 가능한 리뷰를 불러오는데 실패했습니다."
        );
      }
    } catch (error) {
      console.error("작성 가능한 리뷰 조회 실패:", error);
      setError("네트워크 오류가 발생했습니다. 다시 시도해주세요.");
    } finally {
      setLoading(false);
    }
  };

  // 작성한 리뷰 조회
  const fetchWrittenReviews = async () => {
    try {
      setLoading(true);
      setError("");

      const response = await fetch(
        `${API_BASE_URL}/api/v1/reviews/member/list`,
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
          },
          credentials: "include",
        }
      );

      if (response.ok) {
        const result = await response.json();

        if (result.success && result.data && result.data.content) {
          const transformedData = result.data.content.map((item) => ({
            id: item.reviewId,
            reviewId: item.reviewId,
            productId: item.productId,
            historyId: item.historyId,
            productName: `상품 ID: ${item.productId}`,
            rating: item.rate,
            content: item.comment,
            createdDate: new Date(item.createdAt)
              .toLocaleDateString("ko-KR", {
                year: "numeric",
                month: "2-digit",
                day: "2-digit",
              })
              .replace(/\./g, ".")
              .replace(/\s/g, ""),
            imageUrl:
              item.imageUrl && item.imageUrl.length > 0
                ? item.imageUrl[0]
                : "/placeholder.svg?height=140&width=140",
            reviewImages: item.imageUrl || [],
            member: {
              memberId: item.member.memberId,
              nickname: item.member.nickname,
              imageUrl: item.member.imageUrl,
            },
          }));

          setWrittenReviews(transformedData);
        } else {
          setError("작성한 리뷰 데이터를 불러오는데 실패했습니다.");
        }
      } else {
        const errorData = await response.json().catch(() => ({}));
        setError(errorData.message || "작성한 리뷰를 불러오는데 실패했습니다.");
      }
    } catch (error) {
      console.error("작성한 리뷰 조회 실패:", error);
      setError("네트워크 오류가 발생했습니다. 다시 시도해주세요.");
    } finally {
      setLoading(false);
    }
  };

  // 리뷰 상세 조회 (수정용)
  const fetchReviewDetail = async (reviewId) => {
    try {
      setReviewLoading(true);

      const response = await fetch(
        `${API_BASE_URL}/api/v1/reviews/${reviewId}`,
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
          },
          credentials: "include",
        }
      );

      if (response.ok) {
        const result = await response.json();
        if (result.success && result.data) {
          setReviewForm({
            rating: result.data.rate || 0,
            comment: result.data.comment || "",
            images: result.data.imageUrl || [],
            selectedFiles: [],
          });
        }
      } else {
        setError("리뷰 정보를 불러오는데 실패했습니다.");
      }
    } catch (error) {
      console.error("리뷰 상세 조회 실패:", error);
      setError("리뷰 정보를 불러오는데 실패했습니다.");
    } finally {
      setReviewLoading(false);
    }
  };

  // 이미지 업로드 함수 (실제 구현 시 서버 업로드 API 사용)
  const uploadImages = async (files) => {
    // 실제로는 서버에 이미지를 업로드하고 URL을 받아와야 합니다
    // 여기서는 임시로 placeholder URL을 생성합니다
    const uploadedUrls = [];

    for (let i = 0; i < files.length; i++) {
      // 실제 구현 시:
      // const formData = new FormData()
      // formData.append('image', files[i])
      // const response = await fetch('/api/v1/upload', { method: 'POST', body: formData })
      // const result = await response.json()
      // uploadedUrls.push(result.imageUrl)

      // 임시 URL 생성
      uploadedUrls.push(
        `https://example.com/review/img_${Date.now()}_${i}.jpg`
      );
    }

    return uploadedUrls;
  };

  // 리뷰 작성
  const createReview = async () => {
    try {
      setReviewLoading(true);

      // 선택된 파일들을 업로드
      let imageUrls = [...reviewForm.images];
      if (reviewForm.selectedFiles.length > 0) {
        const uploadedUrls = await uploadImages(reviewForm.selectedFiles);
        imageUrls = [...imageUrls, ...uploadedUrls];
      }

      const requestBody = {
        historyId: selectedItem.historyId,
        rate: reviewForm.rating,
        comment: reviewForm.comment,
        imageUrl: imageUrls,
      };

      const response = await fetch(`${API_BASE_URL}/api/v1/reviews`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        credentials: "include",
        body: JSON.stringify(requestBody),
      });

      if (response.ok) {
        setShowReviewModal(false);
        resetReviewForm();
        // 작성 가능한 리뷰 목록 새로고침
        fetchWritableReviews();
        // 작성한 리뷰 목록도 새로고침
        if (activeTab === "written") {
          fetchWrittenReviews();
        }
      } else {
        const errorData = await response.json().catch(() => ({}));
        setError(errorData.message || "리뷰 작성에 실패했습니다.");
      }
    } catch (error) {
      console.error("리뷰 작성 실패:", error);
      setError("리뷰 작성에 실패했습니다.");
    } finally {
      setReviewLoading(false);
    }
  };

  // 리뷰 수정
  const updateReview = async () => {
    try {
      setReviewLoading(true);

      // 선택된 파일들을 업로드
      let imageUrls = [...reviewForm.images];
      if (reviewForm.selectedFiles.length > 0) {
        const uploadedUrls = await uploadImages(reviewForm.selectedFiles);
        imageUrls = [...imageUrls, ...uploadedUrls];
      }

      const requestBody = {
        rate: reviewForm.rating,
        comment: reviewForm.comment,
        imageUrl: imageUrls,
      };

      const response = await fetch(
        `${API_BASE_URL}/api/v1/reviews/${selectedItem.reviewId}`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
          },
          credentials: "include",
          body: JSON.stringify(requestBody),
        }
      );

      if (response.ok) {
        setShowReviewModal(false);
        resetReviewForm();
        // 작성한 리뷰 목록 새로고침
        fetchWrittenReviews();
      } else {
        const errorData = await response.json().catch(() => ({}));
        setError(errorData.message || "리뷰 수정에 실패했습니다.");
      }
    } catch (error) {
      console.error("리뷰 수정 실패:", error);
      setError("리뷰 수정에 실패했습니다.");
    } finally {
      setReviewLoading(false);
    }
  };

  // 리뷰 삭제
  const deleteReview = async (reviewId) => {
    try {
      const response = await fetch(
        `${API_BASE_URL}/api/v1/reviews/${reviewId}`,
        {
          method: "DELETE",
          headers: {
            "Content-Type": "application/json",
          },
          credentials: "include",
        }
      );

      if (response.ok) {
        setWrittenReviews((prev) =>
          prev.filter((review) => review.id !== reviewId)
        );
        setShowDeleteModal(false);
        setSelectedReviewId(null);
      } else {
        setError("리뷰 삭제에 실패했습니다.");
      }
    } catch (error) {
      console.error("리뷰 삭제 실패:", error);
      setError("리뷰 삭제에 실패했습니다.");
    }
  };

  // 파일 선택 핸들러
  const handleFileSelect = (event) => {
    const files = Array.from(event.target.files);
    if (files.length > 0) {
      setReviewForm((prev) => ({
        ...prev,
        selectedFiles: [...prev.selectedFiles, ...files],
      }));
    }
  };

  // 선택된 이미지 제거
  const removeSelectedImage = (index) => {
    setReviewForm((prev) => ({
      ...prev,
      selectedFiles: prev.selectedFiles.filter((_, i) => i !== index),
    }));
  };

  // 기존 이미지 제거
  const removeExistingImage = (index) => {
    setReviewForm((prev) => ({
      ...prev,
      images: prev.images.filter((_, i) => i !== index),
    }));
  };

  // 리뷰 작성 모달 열기
  const handleWriteReview = (item) => {
    setModalMode("create");
    setSelectedItem(item);
    resetReviewForm();
    setShowReviewModal(true);
  };

  // 리뷰 수정 모달 열기
  const handleEditReview = async (review) => {
    setModalMode("edit");
    setSelectedItem(review);
    setShowReviewModal(true);
    await fetchReviewDetail(review.reviewId);
  };

  // 삭제 확인 모달 열기
  const handleDeleteClick = (reviewId) => {
    setSelectedReviewId(reviewId);
    setShowDeleteModal(true);
  };

  // 삭제 확인
  const handleConfirmDelete = () => {
    if (selectedReviewId) {
      deleteReview(selectedReviewId);
    }
  };

  // 리뷰 폼 초기화
  const resetReviewForm = () => {
    setReviewForm({
      rating: 0,
      comment: "",
      images: [],
      selectedFiles: [],
    });
  };

  // 별점 클릭 핸들러
  const handleStarClick = (rating) => {
    setReviewForm((prev) => ({ ...prev, rating }));
  };

  // 리뷰 제출 핸들러
  const handleSubmitReview = () => {
    if (reviewForm.rating === 0) {
      setError("별점을 선택해주세요.");
      return;
    }
    if (reviewForm.comment.length < 10) {
      setError("리뷰는 최소 10자 이상 작성해주세요.");
      return;
    }

    if (modalMode === "create") {
      createReview();
    } else {
      updateReview();
    }
  };

  // 별점 렌더링
  const renderStars = (rating) => {
    const stars = [];
    for (let i = 1; i <= 5; i++) {
      stars.push(
        <span key={i} className={`star ${i <= rating ? "filled" : "empty"}`}>
          ★
        </span>
      );
    }
    return stars;
  };

  // 별점 선택 렌더링 (모달용)
  const renderStarSelector = () => {
    const stars = [];
    for (let i = 1; i <= 5; i++) {
      stars.push(
        <span
          key={i}
          className={`star-selector ${
            i <= reviewForm.rating ? "filled" : "empty"
          }`}
          onClick={() => handleStarClick(i)}
          style={{ cursor: "pointer", fontSize: "32px", margin: "0 4px" }}
        >
          ★
        </span>
      );
    }
    return stars;
  };

  // 탭 변경 시 데이터 로드
  useEffect(() => {
    if (activeTab === "writable") {
      fetchWritableReviews();
    } else {
      fetchWrittenReviews();
    }
  }, [activeTab]);

  // 컴포넌트 마운트 시 초기 데이터 로드
  useEffect(() => {
    fetchWritableReviews();
  }, []);

  return (
    <div className="mypage-reviews">
      <h2 className="page-title">리뷰 관리</h2>

      {error && (
        <Alert variant="danger" onClose={() => setError("")} dismissible>
          {error}
        </Alert>
      )}

      <Card className="groupBuyCardBorder">
        <Card.Header className="p-0">
          <Nav variant="tabs" className="review-tabs">
            <Nav.Item>
              <Nav.Link
                active={activeTab === "writable"}
                onClick={() => setActiveTab("writable")}
                className={`tab-link ${
                  activeTab === "writable" ? "active" : ""
                }`}
              >
                작성 가능한 리뷰
              </Nav.Link>
            </Nav.Item>
            <Nav.Item>
              <Nav.Link
                active={activeTab === "written"}
                onClick={() => setActiveTab("written")}
                className={`tab-link ${
                  activeTab === "written" ? "active" : ""
                }`}
              >
                작성한 리뷰
              </Nav.Link>
            </Nav.Item>
          </Nav>
        </Card.Header>

        <Card.Body className="p-0">
          {loading ? (
            <div className="text-center p-4">
              <div className="spinner-border" role="status">
                <span className="visually-hidden">Loading...</span>
              </div>
            </div>
          ) : (
            <>
              {activeTab === "writable" && (
                <div className="review-list">
                  {writableReviews.length === 0 ? (
                    <div className="empty-state">
                      <p>작성 가능한 리뷰가 없습니다.</p>
                    </div>
                  ) : (
                    writableReviews.map((item) => (
                      <div key={item.id} className="review-item">
                        <div className="product-info">
                          <div className="product-image">
                            <img
                              src={item.imageUrl || "/placeholder.svg"}
                              alt={item.productName}
                            />
                          </div>
                          <div className="product-details">
                            <h5 className="product-name">{item.productName}</h5>
                            <p className="purchase-date">
                              구매일: {item.purchaseDate}
                            </p>
                            <p
                              className="product-description"
                              style={{
                                fontSize: "16px",
                                color: "#666",
                                marginTop: "8px",
                              }}
                            >
                              {item.productDescription}
                            </p>
                            <p
                              style={{
                                fontSize: "16px",
                                color: "#a855f7",
                                fontWeight: "500",
                                marginTop: "8px",
                              }}
                            >
                              {item.price?.toLocaleString()}원 (수량:{" "}
                              {item.quantity}개)
                            </p>
                          </div>
                        </div>
                        <div className="action-buttons">
                          <Button
                            variant="primary"
                            className="write-review-btn"
                            onClick={() => handleWriteReview(item)}
                          >
                            리뷰 작성
                          </Button>
                        </div>
                      </div>
                    ))
                  )}
                </div>
              )}

              {activeTab === "written" && (
                <div className="review-list">
                  {writtenReviews.length === 0 ? (
                    <div className="empty-state">
                      <p>작성한 리뷰가 없습니다.</p>
                    </div>
                  ) : (
                    writtenReviews.map((review) => (
                      <div key={review.id} className="review-item written">
                        <div className="product-info">
                          <div className="product-image">
                            <img
                              src={
                                review.reviewImages.length > 0
                                  ? review.reviewImages[0]
                                  : "/placeholder.svg"
                              }
                              alt={review.productName}
                            />
                          </div>
                          <div className="product-details">
                            <h5 className="product-name">
                              {review.productName}
                            </h5>
                            <div className="rating">
                              {renderStars(review.rating)}
                            </div>
                            <p className="review-content">{review.content}</p>
                            <p className="created-date">
                              작성일: {review.createdDate}
                            </p>
                            <p
                              style={{
                                fontSize: "14px",
                                color: "#666",
                                marginTop: "8px",
                              }}
                            >
                              작성자: {review.member.nickname}
                            </p>
                          </div>
                        </div>
                        <div className="action-buttons">
                          <Button
                            variant="outline-secondary"
                            size="sm"
                            className="me-2"
                            onClick={() => handleEditReview(review)}
                          >
                            수정
                          </Button>
                          <Button
                            variant="outline-danger"
                            size="sm"
                            onClick={() => handleDeleteClick(review.id)}
                          >
                            삭제
                          </Button>
                        </div>
                      </div>
                    ))
                  )}
                </div>
              )}
            </>
          )}
        </Card.Body>
      </Card>

      {/* 리뷰 작성/수정 모달 */}
      <Modal
        show={showReviewModal}
        onHide={() => setShowReviewModal(false)}
        size="lg"
        centered
      >
        <Modal.Header closeButton>
          <Modal.Title>
            {modalMode === "create" ? "리뷰 작성" : "리뷰 수정"}
          </Modal.Title>
        </Modal.Header>
        <Modal.Body className="p-4">
          {reviewLoading ? (
            <div className="text-center p-4">
              <div className="spinner-border" role="status">
                <span className="visually-hidden">Loading...</span>
              </div>
            </div>
          ) : (
            <>
              <div className="text-center mb-4">
                <h5 className="mb-2">상품은 어떠셨나요?</h5>
                <p className="text-muted">상품에대한 별점을 남겨주세요</p>
                <div className="star-rating-selector mb-4">
                  {renderStarSelector()}
                </div>
              </div>

              <div className="mb-4">
                <input
                  type="file"
                  ref={fileInputRef}
                  onChange={handleFileSelect}
                  accept="image/*"
                  multiple
                  style={{ display: "none" }}
                />

                {/* 기존 이미지들 표시 */}
                {reviewForm.images.length > 0 && (
                  <div className="existing-images mb-3">
                    <h6>기존 이미지</h6>
                    <div className="image-preview-container">
                      {reviewForm.images.map((imageUrl, index) => (
                        <div key={index} className="image-preview-item">
                          <img
                            src={imageUrl || "/placeholder.svg"}
                            alt={`기존 이미지 ${index + 1}`}
                          />
                          <button
                            type="button"
                            className="remove-image-btn"
                            onClick={() => removeExistingImage(index)}
                          >
                            ×
                          </button>
                        </div>
                      ))}
                    </div>
                  </div>
                )}

                {/* 새로 선택된 이미지들 표시 */}
                {reviewForm.selectedFiles.length > 0 && (
                  <div className="selected-images mb-3">
                    <h6>새로 선택된 이미지</h6>
                    <div className="image-preview-container">
                      {reviewForm.selectedFiles.map((file, index) => (
                        <div key={index} className="image-preview-item">
                          <img
                            src={
                              URL.createObjectURL(file) || "/placeholder.svg"
                            }
                            alt={`선택된 이미지 ${index + 1}`}
                          />
                          <button
                            type="button"
                            className="remove-image-btn"
                            onClick={() => removeSelectedImage(index)}
                          >
                            ×
                          </button>
                        </div>
                      ))}
                    </div>
                  </div>
                )}

                <div
                  className="image-upload-area"
                  onClick={() => fileInputRef.current?.click()}
                  style={{
                    border: "2px dashed #ddd",
                    borderRadius: "8px",
                    padding: "40px",
                    textAlign: "center",
                    backgroundColor: "#f8f9fa",
                    cursor: "pointer",
                  }}
                >
                  <div
                    style={{
                      fontSize: "24px",
                      color: "#ccc",
                      marginBottom: "8px",
                    }}
                  >
                    +
                  </div>
                  <div style={{ color: "#666" }}>사진 추가</div>
                </div>
              </div>

              <Form.Group>
                <Form.Control
                  as="textarea"
                  rows={6}
                  placeholder="최소 10자 이상 작성해주세요"
                  value={reviewForm.comment}
                  onChange={(e) =>
                    setReviewForm((prev) => ({
                      ...prev,
                      comment: e.target.value,
                    }))
                  }
                  style={{ fontSize: "16px", resize: "none" }}
                />
              </Form.Group>
            </>
          )}
        </Modal.Body>
        <Modal.Footer className="justify-content-center">
          <Button
            variant="primary"
            onClick={handleSubmitReview}
            disabled={reviewLoading}
            style={{
              backgroundColor: "#a855f7",
              borderColor: "#a855f7",
              padding: "12px 40px",
              fontSize: "16px",
              fontWeight: "500",
            }}
          >
            {reviewLoading ? "처리중..." : "완료"}
          </Button>
        </Modal.Footer>
      </Modal>

      {/* 삭제 확인 모달 */}
      <Modal
        show={showDeleteModal}
        onHide={() => setShowDeleteModal(false)}
        centered
      >
        <Modal.Header closeButton>
          <Modal.Title>리뷰 삭제</Modal.Title>
        </Modal.Header>
        <Modal.Body>정말 삭제하시겠습니까?</Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setShowDeleteModal(false)}>
            취소
          </Button>
          <Button variant="danger" onClick={handleConfirmDelete}>
            확인
          </Button>
        </Modal.Footer>
      </Modal>
    </div>
  );
};

export default MyPageReviews;
