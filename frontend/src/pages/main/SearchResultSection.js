"use client"

import { Card, Col, Row, Stack } from "react-bootstrap"
import ProductCard from "./GroupBuyCard"
import { useState, useEffect } from "react"
import axios from "axios"
import { buildQuery } from "../../utils/utils"
import { useQuery } from "react-query"
import useConfirm from "../../hooks/useConfirm"
import Spinner from "../../shared/Spinner"
import { useQueryParam } from "../../hooks/QueryParam"
import { isEmptyOrNull } from "../../utils/utils"

const fetchGroupBuySearch = async (params) => {
  console.log("🔍 검색 API 호출:", params) // 디버깅용 로그
  const response = await axios.get(`/api/v1/groupBuy/search${buildQuery(params)}`)
  console.log("🔍 검색 API 응답:", response.data) // 디버깅용 로그
  return response.data // 전체 응답 반환
}

const SearchResultSection = () => {
  const [queryParam] = useQueryParam()
  const { openConfirm } = useConfirm()
  const [page, setPage] = useState(0)

  // 검색어가 변경될 때 페이지를 0으로 리셋
  useEffect(() => {
    setPage(0)
  }, [queryParam.query])

  const { isLoading, isFetching, data, error } = useQuery(
    ["SearchResultSection", page, queryParam.query],
    () => fetchGroupBuySearch({ page, keyword: queryParam.query }),
    {
      enabled: !isEmptyOrNull(queryParam.query) && queryParam.type === "groupbuy",
      keepPreviousData: true,
      refetchOnWindowFocus: false,
      onError: (e) => {
        console.log("❌ 검색 API 에러:", e)
        openConfirm({
          title: "검색 중 오류가 발생했습니다.",
          html: e.response?.data?.message || "에러: 관리자에게 문의바랍니다.",
        })
      },
    },
  )

  const handlePageClick = (page) => {
    setPage(page - 1)
  }

  // 검색어가 없으면 컴포넌트를 렌더링하지 않음
  if (isEmptyOrNull(queryParam.query) || queryParam.type !== "groupbuy") {
    console.log("🔍 검색 조건 불충족:", { query: queryParam.query, type: queryParam.type })
    return null
  }

  console.log("🔍 SearchResultSection 렌더링:", { query: queryParam.query, type: queryParam.type, data })

  return (
    <Card className={"mt-3 p-2"}>
      <Card.Body className={"p-2"}>
        <Stack direction={"horizontal"} className={"d-flex justify-content-between"}>
          <h4>'{queryParam.query}' 검색 결과</h4>
          {data?.data && <span className="text-muted">총 {data.data.length}개</span>}
        </Stack>

        {data?.data && data.data.length > 0 ? (
          <>
            {data.data.map(
              (item, index) =>
                index % 4 === 0 && (
                  <Row className="mt-3" key={index}>
                    {data.data.slice(index, index + 4).map((item, i) => (
                      <Col md={3} key={i}>
                        <ProductCard product={item} />
                      </Col>
                    ))}
                  </Row>
                ),
            )}
          </>
        ) : (
          !isLoading &&
          !isFetching && (
            <div className="text-center py-5">
              <p className="text-muted">'{queryParam.query}'에 대한 검색 결과가 없습니다.</p>
            </div>
          )
        )}
      </Card.Body>
      <Spinner show={isLoading || isFetching} />
    </Card>
  )
}

export default SearchResultSection
