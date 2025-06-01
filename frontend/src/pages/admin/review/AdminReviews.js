import { useNavigate } from "react-router-dom";
import { Button, Card, Pagination } from "react-bootstrap";
import { useState, useEffect } from "react";
import TableBackGroundCard from "../../../shared/TableBackGroundCard";
import FlexibleTable from "../../../shared/table/FlexibleTable";
import FilterButtonGroup from "./FilterButtonGroup";
import Rating from "react-rating";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faStar as faStarSolid } from "@fortawesome/free-solid-svg-icons";
import { faStar as faStarRegular } from "@fortawesome/free-regular-svg-icons";
import styles from "./AdminReviews.module.scss";

// API 기본 URL 설정 (환경변수 또는 기본값 사용)
const API_BASE_URL = process.env.REACT_APP_API_URL || "";

const AdminReviews = () => {
  const navigate = useNavigate();
  const [reviews, setReviews] = useState([]);
  const [loading, setLoading] = useState(true);
  const [currentSort, setCurrentSort] = useState("LATEST");
  const [pagination, setPagination] = useState({
    page: 0,
    size: 5,
    totalPages: 0,
    totalElements: 0,
  });

  // 리뷰 데이터 조회
  const fetchReviews = async (sortBy = "LATEST", page = 0, size = 5) => {
    try {
      setLoading(true);
      const response = await fetch(
        `${API_BASE_URL}/api/v1/reviews?sortBy=${sortBy}&page=${page}&size=${size}`,
        {
          method: "GET",
          credentials: "include", // 쿠키 포함
          headers: {
            "Content-Type": "application/json",
          },
        }
      );

      if (response.ok) {
        const data = await response.json();

        // 응답 구조에 맞게 데이터 추출
        if (data.success && data.data) {
          setReviews(data.data.content || []);
          setPagination({
            page: data.data.number,
            size: data.data.size,
            totalPages: data.data.totalPages,
            totalElements: data.data.totalElements,
          });
        } else {
          console.error("리뷰 조회 실패:", data.message || "알 수 없는 오류");
        }
      } else {
        console.error("리뷰 조회 실패");
      }
    } catch (error) {
      console.error("리뷰 조회 중 오류:", error);
    } finally {
      setLoading(false);
    }
  };

  // 리뷰 삭제
  const deleteReview = async (reviewId) => {
    if (!window.confirm("정말로 이 리뷰를 삭제하시겠습니까?")) {
      return;
    }

    try {
      const response = await fetch(
        `${API_BASE_URL}/api/v1/reviews/${reviewId}`,
        {
          method: "DELETE",
          credentials: "include", // 쿠키 포함
          headers: {
            "Content-Type": "application/json",
          },
        }
      );

      if (response.ok) {
        // 삭제 성공 시 현재 페이지 리로드
        fetchReviews(currentSort, pagination.page, pagination.size);
        alert("리뷰가 성공적으로 삭제되었습니다.");
      } else {
        alert("리뷰 삭제에 실패했습니다.");
      }
    } catch (error) {
      console.error("리뷰 삭제 중 오류:", error);
      alert("리뷰 삭제 중 오류가 발생했습니다.");
    }
  };

  // 정렬 변경 핸들러
  const handleSortChange = (sortType) => {
    setCurrentSort(sortType);
    fetchReviews(sortType, 0, pagination.size); // 정렬 변경 시 첫 페이지로 이동
  };

  // 페이지 변경 핸들러
  const handlePageChange = (page) => {
    fetchReviews(currentSort, page, pagination.size);
  };

  // 컴포넌트 마운트 시 초기 데이터 로드
  useEffect(() => {
    fetchReviews();
  }, []);

  // 날짜 포맷팅 함수
  const formatDate = (dateString) => {
    if (!dateString) return "-";
    const date = new Date(dateString);
    return date.toLocaleString("ko-KR", {
      year: "numeric",
      month: "2-digit",
      day: "2-digit",
      hour: "2-digit",
      minute: "2-digit",
    });
  };

  // 페이지네이션 컴포넌트
  const renderPagination = () => {
    const items = [];
    const currentPage = pagination.page;
    const totalPages = pagination.totalPages;

    // 표시할 페이지 범위 계산
    let startPage = Math.max(0, currentPage - 4);
    let endPage = Math.min(totalPages - 1, currentPage + 4);

    // 시작 페이지가 너무 뒤에 있으면 앞으로 조정
    if (endPage - startPage < 9 && totalPages > 10) {
      if (startPage === 0) {
        endPage = Math.min(totalPages - 1, 9);
      } else if (endPage === totalPages - 1) {
        startPage = Math.max(0, totalPages - 10);
      }
    }

    // 맨 처음 버튼
    if (currentPage > 0) {
      items.push(
        <Pagination.First key="first" onClick={() => handlePageChange(0)} />
      );
    }

    // 이전 버튼
    items.push(
      <Pagination.Prev
        key="prev"
        disabled={currentPage === 0}
        onClick={() => handlePageChange(currentPage - 1)}
      />
    );

    // 첫 페이지가 표시 범위에 없으면 첫 페이지와 ... 추가
    if (startPage > 0) {
      items.push(
        <Pagination.Item key={0} onClick={() => handlePageChange(0)}>
          1
        </Pagination.Item>
      );
      if (startPage > 1) {
        items.push(<Pagination.Ellipsis key="start-ellipsis" disabled />);
      }
    }

    // 페이지 버튼들
    for (let i = startPage; i <= endPage; i++) {
      items.push(
        <Pagination.Item
          key={i}
          active={i === currentPage}
          onClick={() => handlePageChange(i)}
        >
          {i + 1}
        </Pagination.Item>
      );
    }

    // 마지막 페이지가 표시 범위에 없으면 ... 과 마지막 페이지 추가
    if (endPage < totalPages - 1) {
      if (endPage < totalPages - 2) {
        items.push(<Pagination.Ellipsis key="end-ellipsis" disabled />);
      }
      items.push(
        <Pagination.Item
          key={totalPages - 1}
          onClick={() => handlePageChange(totalPages - 1)}
        >
          {totalPages}
        </Pagination.Item>
      );
    }

    // 다음 버튼
    items.push(
      <Pagination.Next
        key="next"
        disabled={currentPage >= totalPages - 1}
        onClick={() => handlePageChange(currentPage + 1)}
      />
    );

    // 맨 마지막 버튼
    if (currentPage < totalPages - 1) {
      items.push(
        <Pagination.Last
          key="last"
          onClick={() => handlePageChange(totalPages - 1)}
        />
      );
    }

    return (
      <Pagination className="mt-3 justify-content-center">{items}</Pagination>
    );
  };

  const initColumns = [
    {
      id: "reviewId",
      header: "리뷰 ID",
      cell: ({ row }) => `REV-${row.original.reviewId}`,
    },
    {
      id: "products",
      header: "상품",
      cell: ({ row }) => `상품 ID: ${row.original.productId}`,
    },
    {
      id: "creator",
      header: "작성자",
      cell: ({ row }) => row.original.member?.nickname || "작성자 없음",
    },
    {
      id: "rating",
      header: "평점",
      cell: ({ row }) => (
        <div className="d-flex align-items-center">
          <Rating
            initialRating={row.original.rate || 0}
            readonly
            fullSymbol={
              <FontAwesomeIcon icon={faStarSolid} color="#facc15" size="sm" />
            }
            emptySymbol={
              <FontAwesomeIcon icon={faStarRegular} color="#facc15" size="sm" />
            }
          />
          <span className="ms-2">({row.original.rate})</span>
        </div>
      ),
    },
    {
      id: "content",
      header: "내용",
      cell: ({ row }) => {
        const content = row.original.comment || "";
        return content.length > 50 ? `${content.substring(0, 50)}...` : content;
      },
    },
    {
      id: "createDate",
      header: "작성일",
      cell: ({ row }) => formatDate(row.original.createdAt),
    },
    {
      id: "adminButton",
      header: "처리",
      cell: ({ row }) => (
        <Button
          variant=""
          className={styles.detailButton}
          onClick={() => deleteReview(row.original.reviewId)}
        >
          삭제
        </Button>
      ),
    },
  ];

  return (
    <Card className={"px-10"}>
      <Card.Header className={"border-0"}>
        <h4>리뷰 관리</h4>
        <desc className={"text-gray-300"}>구매자 리뷰를 관리합니다.</desc>
        <div className="mt-2 text-muted">
          총 {pagination.totalElements}개의 리뷰 (페이지 {pagination.page + 1}/
          {pagination.totalPages})
        </div>
      </Card.Header>
      <Card.Body>
        <FilterButtonGroup onSortChange={handleSortChange} />
        <TableBackGroundCard>
          {loading ? (
            <div className="text-center py-4">로딩 중...</div>
          ) : (
            <>
              <FlexibleTable initColumns={initColumns} data={reviews} />
              {pagination.totalPages > 1 && renderPagination()}
            </>
          )}
        </TableBackGroundCard>
      </Card.Body>
    </Card>
  );
};

export default AdminReviews;
