"use client"

import { useState, useEffect } from "react"
import { useNavigate } from "react-router-dom"
import { useQueryParam } from "../../../hooks/QueryParam"
import { isEmptyOrNull } from "../../../utils/utils"
import { Form } from "react-bootstrap"

const HeaderSearchBox = () => {
  const navigate = useNavigate()
  const [queryParam, setQueryParam] = useQueryParam()
  const [keyword, setKeyword] = useState(queryParam.keyword || "")

  useEffect(() => {
    if (isEmptyOrNull(queryParam.query)) {
      handleReset()
    }
  }, [queryParam.query])

  const handleReset = () => {
    setKeyword("")
    setQueryParam({}) // URL 쿼리스트링 초기화
  }

  // 검색 실행 로직을 별도 함수로 분리
  const executeSearch = () => {
    console.log("🔍 검색 실행:", keyword) // 디버깅용 로그
    if (!isEmptyOrNull(keyword)) {
      setQueryParam({ query: keyword, type: "groupbuy" })
    }
  }

  // 버튼 클릭 이벤트 처리
  const handleSearchClick = (e) => {
    console.log("🔍 검색 버튼 클릭됨") // 디버깅용 로그
    e.preventDefault()
    e.stopPropagation()
    executeSearch()
  }

  // 엔터키 이벤트 처리
  const handleKeyDown = (e) => {
    if (e.key === "Enter") {
      console.log("🔍 엔터키 눌림") // 디버깅용 로그
      e.preventDefault()
      executeSearch()
    }
  }

  return (
    <div className="search-container position-relative" style={{ marginBottom: "20px", zIndex: 1000 }}>
      <div style={{ display: "flex", alignItems: "center", gap: "8px" }}>
        <div>
          <Form.Label className="form-label"></Form.Label>
          <div className="kw-form-search shadow" style={{ width: "380px", position: "relative" }}>
            <Form.Control
              type="text"
              className="form-control"
              placeholder="상품명 혹은 브랜드명으로 검색"
              value={keyword}
              onChange={(e) => setKeyword(e.target.value)}
              onKeyDown={handleKeyDown}
            />
            <button
              type="button"
              onClick={handleSearchClick}
              style={{
                position: "absolute",
                right: "5px",
                top: "50%",
                transform: "translateY(-50%)",
                border: "none",
                background: "transparent",
                padding: "5px 10px",
                borderRadius: "4px",
                cursor: "pointer",
                zIndex: 1001,
              }}
              onMouseDown={(e) => e.stopPropagation()}
            >
              검색
            </button>
          </div>
        </div>
      </div>
    </div>
  )
}

export default HeaderSearchBox
