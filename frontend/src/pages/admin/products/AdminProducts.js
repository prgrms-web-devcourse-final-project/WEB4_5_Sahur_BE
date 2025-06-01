import { useState } from "react";
import { Card, Tabs, Tab, InputGroup, Form, Button } from "react-bootstrap";
import ProductsTable from "./ProductsTable";
import ProductsRequestsTable from "./ProductsRequestsTable";
import styles from "./AdminProducts.module.scss";
import { Routes, Route } from "react-router-dom";
import AdminProductsDetail from "./AdminProductsDetail";
import AdminProductsRequestDetail from "./AdminProductsRequestDetail";

function AdminProducts() {
  const [activeTab, setActiveTab] = useState("products");
  const [searchInput, setSearchInput] = useState("");
  const [searchQuery, setSearchQuery] = useState("");
  const [searchCategory, setSearchCategory] = useState("");

  const handleTabSelect = (key) => {
    setActiveTab(key);
    setSearchInput("");
    setSearchQuery("");
    setSearchCategory("");
  };

  const handleSearch = () => {
    const trimmedInput = searchInput.trim();
    if (trimmedInput) {
      // Check if the input contains a comma
      if (trimmedInput.includes(",")) {
        // Split the input by comma and use the first part as category and the second as query
        const [category, query] = trimmedInput
          .split(",")
          .map((item) => item.trim());
        setSearchCategory(category);
        setSearchQuery(query);
      } else if (trimmedInput.toUpperCase() === trimmedInput) {
        // If the input is all uppercase, treat it as a category
        setSearchCategory(trimmedInput);
        setSearchQuery("");
      } else {
        // Otherwise, treat it as a query
        setSearchQuery(trimmedInput);
        setSearchCategory("");
      }
    } else {
      // If the input is empty, clear both category and query
      setSearchQuery("");
      setSearchCategory("");
    }
  };

  const handleClearSearch = () => {
    setSearchInput("");
    setSearchQuery("");
    setSearchCategory("");
  };

  const handleKeyPress = (e) => {
    if (e.key === "Enter") {
      handleSearch();
    }
  };

  return (
    <Routes>
      <Route
        path="/"
        element={
          <Card className={"px-10"}>
            <Card.Header className={"border-0"}>
              <h4>상품 관리</h4>
              <p className="text-muted mb-0">
                등록된 상품 및 상품 요청을 관리합니다.
              </p>
            </Card.Header>
            <Card.Body>
              <div className="d-flex justify-content-between mx-3 mb-4">
                <Tabs
                  activeKey={activeTab}
                  onSelect={handleTabSelect}
                  id="uncontrolled-tab-example"
                >
                  <Tab eventKey="products" title="등록된 상품 목록" />
                  <Tab eventKey="requests" title="상품 등록 요청" />
                </Tabs>

                {/* 오른쪽 검색창 */}
                {activeTab === "products" && (
                  <div className="d-flex align-items-center gap-2">
                    <InputGroup style={{ width: 360, height: 50 }}>
                      <Form.Control
                        type="text"
                        placeholder="카테고리 or 키워드 (예: PET, PET_TOY, 이어폰)"
                        value={searchInput}
                        onChange={(e) => setSearchInput(e.target.value)}
                        onKeyPress={handleKeyPress}
                      />
                      <Button
                        className={styles.detailButton}
                        variant=""
                        type={"button"}
                        onClick={handleSearch}
                      >
                        검색
                      </Button>
                    </InputGroup>

                    {/* 검색 초기화 버튼 - 기존 버튼과 동일한 스타일 적용 */}
                    {(searchQuery || searchCategory) && (
                      <Button
                        variant=""
                        className={styles.detailButton}
                        onClick={handleClearSearch}
                      >
                        초기화
                      </Button>
                    )}
                  </div>
                )}
              </div>

              {/* 현재 검색 조건 표시 */}
              {activeTab === "products" && (searchQuery || searchCategory) && (
                <div className="mx-3 mb-3">
                  <div className="alert alert-info py-2">
                    <small>
                      현재 검색 조건:
                      {searchCategory && (
                        <span className="fw-bold">
                          {" "}
                          카테고리 "{searchCategory}"
                        </span>
                      )}
                      {searchQuery && (
                        <span className="fw-bold"> 키워드 "{searchQuery}"</span>
                      )}
                    </small>
                  </div>
                </div>
              )}

              {/* 탭 콘텐츠 */}
              {activeTab === "products" && (
                <ProductsTable
                  searchQuery={searchQuery}
                  category={searchCategory}
                />
              )}
              {activeTab === "requests" && <ProductsRequestsTable />}
            </Card.Body>
          </Card>
        }
      />
      {/* 더 구체적인 경로를 먼저 배치 */}
      <Route
        path="/requests/:requestId"
        element={<AdminProductsRequestDetail />}
      />
      <Route path="/:productId" element={<AdminProductsDetail />} />
    </Routes>
  );
}

export default AdminProducts;
