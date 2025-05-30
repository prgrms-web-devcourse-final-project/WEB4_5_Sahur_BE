import PopularProductSection from "./PopularProductSection"
import CategoryProductSection from "./CategoryProductSection"
import SearchResultSection from "./SearchResultSection"
import CategoryFilterSection from "./CategoryFilterSection"
import { useQueryParam } from "../../hooks/QueryParam"
import { isEmptyOrNull } from "../../utils/utils"

const Main = () => {
  const [queryParam] = useQueryParam()

  // 검색 상태인지 확인
  const isSearchMode = !isEmptyOrNull(queryParam.query) && queryParam.type === "groupbuy"

  // 카테고리 필터링 상태인지 확인
  const isCategoryFilterMode = !isEmptyOrNull(queryParam.categoryType)

  return (
    <>
      {isSearchMode ? (
        // 검색 모드: 검색 결과만 표시
        <SearchResultSection />
      ) : isCategoryFilterMode ? (
        // 카테고리 필터 모드: 선택된 카테고리 상품만 표시
        <>
          <CategoryFilterSection categoryType={queryParam.categoryType} />
        </>
      ) : (
        // 일반 모드: 기존 메인 페이지 표시
        <>
          <PopularProductSection />
          <CategoryProductSection />
        </>
      )}
    </>
  )
}

export default Main
