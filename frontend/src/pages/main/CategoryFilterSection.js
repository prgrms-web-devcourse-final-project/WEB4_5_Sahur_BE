"use client"

import { useState } from "react"
import { Button, Card, Col, Row, Stack } from "react-bootstrap"
import clsx from "clsx"
import styles from "./Main.module.scss"
import ProductCard from "./GroupBuyCard"
import PaginationSection from "./PaginationSection"
import axios from "axios"
import { buildQuery } from "../../utils/utils"
import { useQuery } from "react-query"
import useConfirm from "../../hooks/useConfirm"
import Spinner from "../../shared/Spinner"

const sortOptions = [
  { name: "최신순", value: "LATEST" },
  { name: "인기순", value: "POPULAR" },
  { name: "마감임박순", value: "DEADLINE_SOON" },
]

const fetchCategoryProducts = async (categoryType, params) => {
  const response = await axios.get(`/api/v1/groupBuy/category-type/${categoryType}/onGoing${buildQuery(params)}`)
  return response.data.data
}

const CategoryFilterSection = ({ categoryType }) => {
  const [sortField, setSortField] = useState("LATEST")
  const { openConfirm } = useConfirm()
  const [page, setPage] = useState(0)

  // 카테고리 이름 매핑
  const getCategoryName = (type) => {
    const categoryMap = {
      ALL: "전체",
      FASHION_CLOTHES: "패션의류",
      FASHION_ACCESSORY: "패션잡화",
      BEAUTY: "뷰티",
      DIGITAL_APPLIANCE: "디지털/가전",
      FURNITURE: "가구/인테리어",
      LIVING: "생활/건강",
      FOOD: "식품",
      SPORTS: "스포츠레저",
      CAR: "자동차/공구",
      BOOK: "도서/음반/DVD",
      KIDS: "유아동/출산",
      PET: "반려동물",
    }
    return categoryMap[type] || type
  }

  const { isLoading, isFetching, data } = useQuery(
    ["CategoryFilterSection", categoryType, page, sortField],
    () => fetchCategoryProducts(categoryType, { page, sortField }),
    {
      keepPreviousData: true,
      refetchOnWindowFocus: false,
      onError: (e) => {
        console.log("error fetchCategoryProducts: ", e)
        openConfirm({
          title: "데이터를 불러오는 중 오류가 발생했습니다.",
          html: e.response?.data?.message || "에러: 관리자에게 문의바랍니다.",
        })
      },
    },
  )

  const handlePageClick = (page) => {
    setPage(page - 1)
  }

  return (
    <Card className={"mt-3 p-2"}>
      <Card.Body className={"p-2"}>
        <Stack direction={"horizontal"} className={"d-flex justify-content-between"}>
          <h4>{getCategoryName(categoryType)} 공동 구매</h4>
          <Stack direction={"horizontal"} gap={3}>
            {sortOptions.map((item) => (
              <Button
                key={item.value}
                bsPrefix={clsx(styles.sortButton, { [styles.active]: sortField === item.value })}
                onClick={() => setSortField(item.value)}
              >
                {item.name}
              </Button>
            ))}
          </Stack>
        </Stack>
        {data?.content && data.content.length > 0 ? (
          <>
            {data.content.map(
              (item, index) =>
                index % 4 === 0 && (
                  <Row className="mt-3" key={index}>
                    {data.content.slice(index, index + 4).map((item, i) => (
                      <Col md={3} key={i}>
                        <ProductCard product={item} />
                      </Col>
                    ))}
                  </Row>
                ),
            )}
          </>
        ) : (
          <div className="text-center py-5">
            <p className="mb-0">해당 카테고리에 상품이 없습니다.</p>
          </div>
        )}
        {data?.totalPages > 1 && (
          <PaginationSection currentPage={page + 1} totalPages={data.totalPages} handlePageClick={handlePageClick} />
        )}
      </Card.Body>
      <Spinner show={isLoading || isFetching} />
    </Card>
  )
}

export default CategoryFilterSection
