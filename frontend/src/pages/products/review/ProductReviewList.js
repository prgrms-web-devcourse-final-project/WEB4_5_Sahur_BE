"use client"

import { Button, Col, Overlay, Row, Stack, Dropdown } from "react-bootstrap"
import ProductReviewItem from "./ProductReviewItem"
import styles from "../GroupBuy.module.scss"
import { useRef, useState, useEffect } from "react"
import CreateReviewCard from "./CreateReviewCard"
import PurchaseHistoryModal from "./PurchaseHistoryModal"
import axios from "axios"
import { useApiMutation } from "../../../hooks/useApiMutation"
import { useQuery } from "react-query"

const fetchReviews = async ({ productId, page = 0, sortBy = "LATEST" }) => {
  console.log(`Fetching reviews for productId: ${productId}, page: ${page}, sortBy: ${sortBy}`)
  try {
    const response = await axios.get(`/api/v1/reviews/product/${productId}/list`, {
      params: { page, sortBy },
    })
    console.log("Review API response:", response.data)
    return response.data.data
  } catch (error) {
    console.error("Error fetching reviews:", error)
    throw error
  }
}

const fetchWritableHistories = async (productId) => {
  const response = await axios.get(`/api/v1/histories/products/${productId}/writable-histories`, {
    withCredentials: true,
  })
  return response.data.data
}

const ProductReviewList = ({ product }) => {
  const [showReviewCard, setShowReviewCard] = useState(false)
  const [showHistoryModal, setShowHistoryModal] = useState(false)
  const [selectedHistory, setSelectedHistory] = useState(null)
  const [sortBy, setSortBy] = useState("LATEST")
  const [allReviews, setAllReviews] = useState([])
  const [currentPage, setCurrentPage] = useState(0)
  const [refreshKey, setRefreshKey] = useState(0) // 리뷰 목록 새로고침용 키
  const target = useRef(null)

  // 디버깅용 콘솔 로그
  console.log("ProductReviewList - product:", product)

  const productId = product?.productId
  console.log("ProductReviewList - productId:", productId)

  const {
    data: reviewData,
    isLoading,
    refetch: refetchReviews,
    error,
  } = useQuery(
    ["productReviews", productId, currentPage, sortBy, refreshKey],
    () =>
      fetchReviews({
        productId,
        page: currentPage,
        sortBy,
      }),
    {
      enabled: Boolean(productId),
      onSuccess: (data) => {
        console.log("Review data fetched successfully:", data)
        if (currentPage === 0) {
          setAllReviews(data.content || [])
        } else {
          setAllReviews((prev) => [...prev, ...(data.content || [])])
        }
      },
      onError: (err) => {
        console.error("Error in review query:", err)
      },
    },
  )

  // 구매내역 조회
  const {
    mutate: fetchWritableHistoriesMutate,
    isLoading: isLoadingHistories,
    data: purchaseHistories,
  } = useApiMutation(fetchWritableHistories, {
    onSuccess: (data) => {
      console.log("Writable histories:", data)
      if (data && data.length > 0) {
        setShowHistoryModal(true)
      } else {
        alert("리뷰 작성 가능한 구매내역이 없습니다.")
      }
    },
    onError: (err) => {
      console.error("Error fetching writable histories:", err)
      alert("구매내역을 불러오는 중 오류가 발생했습니다.")
    },
  })

  // 에러 로깅
  useEffect(() => {
    if (error) {
      console.error("Review query error:", error)
    }
  }, [error])

  const handleCreateReview = () => {
    if (productId) {
      fetchWritableHistoriesMutate(productId)
    } else {
      console.error("Cannot create review: productId is undefined")
    }
  }

  const handleSelectHistory = (history) => {
    setSelectedHistory(history)
    setShowHistoryModal(false)
    setShowReviewCard(true)
  }

  const handleCloseReviewCard = () => {
    setShowReviewCard(false)
    setSelectedHistory(null)
  }

  const handleReviewCreated = () => {
    // 리뷰 작성 완료 후 리뷰 목록 완전 새로고침
    setCurrentPage(0)
    setAllReviews([])
    setRefreshKey((prev) => prev + 1) // 새로고침 키 증가로 완전 새로고침
  }

  const handleSortChange = (newSortBy) => {
    setSortBy(newSortBy)
    setCurrentPage(0)
    setAllReviews([])
    setRefreshKey((prev) => prev + 1) // 정렬 변경 시에도 완전 새로고침
  }

  const handleLoadMore = () => {
    setCurrentPage((prev) => prev + 1)
  }

  const getSortLabel = (sortValue) => {
    switch (sortValue) {
      case "LATEST":
        return "최신순"
      case "RATE":
        return "평점순"
      case "OLDEST":
        return "오래된순"
      default:
        return "최신순"
    }
  }

  const hasMoreReviews = reviewData && !reviewData.last

  // 고유한 key 생성 함수
  const generateUniqueKey = (review, index) => {
    return `review-${review.reviewId || "no-id"}-${index}-${refreshKey}`
  }

  return (
    <>
      <Row className={"mt-10"}>
        <Col md={12}>
          <Stack direction="horizontal" className="justify-content-between align-items-center mb-3">
            <h5>상품 리뷰 ({reviewData?.totalElements || 0})</h5>
            <Stack direction="horizontal" gap={2}>
              {/* 정렬 드롭다운 스타일 수정 */}
              <Dropdown>
                <Dropdown.Toggle
                  variant="outline-secondary"
                  size="sm"
                  style={{
                    backgroundColor: "#fff",
                    border: "1px solid #ced4da",
                    color: "#212529",
                    opacity: 1,
                  }}
                >
                  {getSortLabel(sortBy)}
                </Dropdown.Toggle>
                <Dropdown.Menu>
                  <Dropdown.Item onClick={() => handleSortChange("LATEST")}>최신순</Dropdown.Item>
                  <Dropdown.Item onClick={() => handleSortChange("RATE")}>평점순</Dropdown.Item>
                  <Dropdown.Item onClick={() => handleSortChange("OLDEST")}>오래된순</Dropdown.Item>
                </Dropdown.Menu>
              </Dropdown>
              <Button
                variant={""}
                className={styles.reviewButton}
                size={"sm"}
                ref={target}
                onClick={handleCreateReview}
                style={{ opacity: 1 }}
                disabled={isLoadingHistories}
              >
                {isLoadingHistories ? "확인 중..." : "리뷰 작성"}
              </Button>
            </Stack>
            <Overlay
              target={target.current}
              show={showReviewCard}
              placement="left"
              popperConfig={{
                modifiers: [{ name: "offset", options: { offset: [30, 10] } }],
              }}
            >
              <div>
                <CreateReviewCard
                  handleClose={handleCloseReviewCard}
                  selectedHistory={selectedHistory}
                  onReviewCreated={handleReviewCreated}
                />
              </div>
            </Overlay>
          </Stack>

          {isLoading ? (
            <div className="text-center py-4">리뷰를 불러오는 중...</div>
          ) : error ? (
            <div className="text-center py-4 text-danger">리뷰를 불러오는 중 오류가 발생했습니다.</div>
          ) : allReviews && allReviews.length > 0 ? (
            <>
              {allReviews.map((review, index) => (
                <ProductReviewItem key={generateUniqueKey(review, index)} review={review} />
              ))}
              {/* 더보기 버튼 중앙 정렬 및 스타일 개선 */}
              {hasMoreReviews && (
                <div className="d-flex justify-content-center mt-4 mb-3">
                  <Button
                    variant="outline-primary"
                    onClick={handleLoadMore}
                    disabled={isLoading}
                    style={{
                      minWidth: "120px",
                      opacity: 1,
                      backgroundColor: "transparent",
                      borderColor: "#0d6efd",
                      color: "#0d6efd",
                    }}
                    onMouseEnter={(e) => {
                      e.target.style.backgroundColor = "#0d6efd"
                      e.target.style.color = "#fff"
                    }}
                    onMouseLeave={(e) => {
                      e.target.style.backgroundColor = "transparent"
                      e.target.style.color = "#0d6efd"
                    }}
                  >
                    {isLoading ? "로딩 중..." : "더보기"}
                  </Button>
                </div>
              )}
            </>
          ) : (
            <div className="text-center py-4 text-muted">아직 작성된 리뷰가 없습니다.</div>
          )}
        </Col>
      </Row>

      {/* 구매내역 선택 모달 */}
      <PurchaseHistoryModal
        show={showHistoryModal}
        onHide={() => setShowHistoryModal(false)}
        purchaseHistories={purchaseHistories}
        onSelectHistory={handleSelectHistory}
        isLoading={isLoadingHistories}
      />
    </>
  )
}

export default ProductReviewList
