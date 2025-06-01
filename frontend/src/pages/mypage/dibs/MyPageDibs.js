import { useState, useEffect } from "react";
import { Col, Row, Spinner, Pagination } from "react-bootstrap";
import ProductCard from "./ProductCard";

const MyPageDibs = () => {
  const [dibsItems, setDibsItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const pageSize = 6;

  const fetchDibsData = async (page = 0) => {
    setLoading(true);
    try {
      const response = await fetch(
        `${process.env.REACT_APP_API_URL}/api/v1/dibs?page=${page}&size=${pageSize}`,
        {
          method: "GET",
          credentials: "include",
          headers: {
            "Content-Type": "application/json",
          },
        }
      );

      if (response.ok) {
        const data = await response.json();
        setDibsItems(data.data.content);
        setTotalPages(data.data.totalPages);
        setCurrentPage(data.data.number);
      } else {
        console.error("Failed to fetch dibs data");
      }
    } catch (error) {
      console.error("Error fetching dibs data:", error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDibsData();
  }, []);

  const handleRemoveItem = (productId) => {
    setDibsItems((prev) =>
      prev.filter((item) => item.product.productId !== productId)
    );
  };

  const handlePageChange = (page) => {
    if (page >= 0 && page < totalPages) {
      fetchDibsData(page);
    }
  };

  if (loading) {
    return (
      <div className="d-flex justify-content-center align-items-center p-5">
        <Spinner animation="border" role="status" className="me-2">
          <span className="visually-hidden">Loading...</span>
        </Spinner>
        <span>관심 상품을 불러오는 중...</span>
      </div>
    );
  }

  return (
    <>
      <div className="mb-4">
        <h2 className="fw-bold mb-3">관심 상품</h2>
      </div>

      {dibsItems.length === 0 ? (
        <div className="text-center py-5">
          <p className="fs-5 mb-2">관심 상품이 없습니다.</p>
          <p className="text-muted">
            마음에 드는 상품을 찾아 관심 상품에 추가해보세요.
          </p>
        </div>
      ) : (
        <>
          {/* 상품 그리드 - 기존 코드 구조 유지 */}
          {dibsItems.map(
            (item, index) =>
              index % 3 === 0 && (
                <Row className="mt-3" key={index}>
                  {dibsItems.slice(index, index + 3).map((dibsItem, i) => (
                    <Col md={4} key={i} className="mb-3">
                      <ProductCard
                        product={dibsItem.product}
                        groupBuy={dibsItem.groupBuy}
                        onRemove={handleRemoveItem}
                      />
                    </Col>
                  ))}
                </Row>
              )
          )}

          {/* 페이지네이션 */}
          {totalPages > 1 && (
            <div className="d-flex justify-content-center mt-4">
              <Pagination>
                <Pagination.Prev
                  disabled={currentPage === 0}
                  onClick={() => handlePageChange(currentPage - 1)}
                />

                {Array.from({ length: totalPages }, (_, i) => (
                  <Pagination.Item
                    key={i}
                    active={currentPage === i}
                    onClick={() => handlePageChange(i)}
                  >
                    {i + 1}
                  </Pagination.Item>
                ))}

                <Pagination.Next
                  disabled={currentPage === totalPages - 1}
                  onClick={() => handlePageChange(currentPage + 1)}
                />
              </Pagination>
            </div>
          )}
        </>
      )}
    </>
  );
};

export default MyPageDibs;
