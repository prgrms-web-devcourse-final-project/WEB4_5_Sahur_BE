"use client"

import { Accordion, AccordionContext, Card, ListGroup, Stack, useAccordionButton } from "react-bootstrap"
import style from "../Header.module.scss"
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome"
import { faXmark } from "@fortawesome/free-solid-svg-icons"
import { useContext, useEffect, useState } from "react"
import clsx from "clsx"
import { useQuery } from "react-query"
import axios from "axios"

function CustomToggle({ eventKey, keywordList }) {
  const decoratedOnClick = useAccordionButton(eventKey)
  // 현재 열린 아코디언 키를 가져옴
  const currentEventKey = useContext(AccordionContext)
  const [index, setIndex] = useState(0)

  useEffect(() => {
    if (keywordList.length === 0) return

    const timer = setInterval(() => {
      setIndex((prev) => (prev + 1) % keywordList.length)
    }, 5000) // 5초마다 변경

    return () => clearInterval(timer) // 컴포넌트 unmount 시 정리
  }, [keywordList.length])

  // 키워드가 없으면 로딩 표시
  if (keywordList.length === 0) {
    return (
      <Stack direction={"horizontal"} className={style.shoppingKeywordText} onClick={decoratedOnClick}>
        <span style={{ color: "#A855F7", fontWeight: "900", marginRight: "10px" }}>-</span>
        로딩중...
        <span
          className={clsx("ms-10", currentEventKey.activeEventKey === null ? "ico-collaps-down" : "ico-collaps-up")}
        />
      </Stack>
    )
  }

  return (
    <Stack direction={"horizontal"} className={style.shoppingKeywordText} onClick={decoratedOnClick}>
      <span style={{ color: "#A855F7", fontWeight: "900", marginRight: "10px" }}>{keywordList[index].order}.</span>
      {keywordList[index].keyword}
      <span
        className={clsx("ms-10", currentEventKey.activeEventKey === null ? "ico-collaps-down" : "ico-collaps-up")}
      />
    </Stack>
  )
}

const ShoppingKeyword = () => {
  const [activeKey, setActiveKey] = useState(null)

  // 키워드 번역 함수 - 모든 KeywordType 매핑
  const translateKeyword = (keyword) => {
    const keywordMap = {
      // 패션의류
      TSHIRT: "티셔츠",
      DRESS: "원피스",
      SHIRT: "셔츠",
      OUTER: "아우터",
      PANTS: "팬츠",

      // 패션잡화
      SHOES: "신발",
      BAG: "가방",
      WALLET: "지갑",
      HAT: "모자",
      ACCESSORY: "악세사리",

      // 뷰티
      SKINCARE: "스킨케어",
      MASK_PACK: "마스크팩",
      MAKEUP: "메이크업",
      PERFUME: "향수",
      HAIR_CARE: "헤어케어",

      // 디지털/가전
      SMARTPHONE: "스마트폰",
      TABLET: "태블릿",
      LAPTOP: "노트북",
      TV: "TV",
      REFRIGERATOR: "냉장고",
      WASHING_MACHINE: "세탁기",

      // 가구/인테리어
      BED: "침대",
      SOFA: "소파",
      TABLE: "테이블",
      CHAIR: "의자",
      LIGHTING: "조명",

      // 생활/건강
      BODY_CARE: "바디케어",
      SUPPLEMENT: "영양제",
      TOOTHPASTE: "치약",
      VACUUM_CLEANER: "청소기",
      DAILY_GOODS: "생활용품",

      // 식품
      FRUIT: "과일",
      VEGETABLE: "채소",
      MEAT: "정육",
      SIDE_DISH: "반찬/간편식",
      INSTANT_FOOD: "즉석식품",
      BEVERAGE: "음료",

      // 스포츠/레저
      SPORTSWEAR: "운동복",
      SNEAKERS: "운동화",
      EQUIPMENT: "용품",
      GOLF: "골프",
      SWIMMING: "수영",

      // 자동차/공구
      AUTO_ACCESSORY: "자동차용품",
      CAR_CARE: "세차용품",
      TOOLS: "공구장비",
      HAND_TOOL: "수공구",
      TIRE: "타이어",

      // 도서/음반/DVD
      NOVEL: "소설",
      SELF_DEVELOP: "자기계발",
      COMIC: "만화/웹툰",
      ALBUM: "음반",
      DVD: "DVD",

      // 유아동/완구
      BABY_CLOTHES: "유아의류",
      CHILD_CLOTHES: "아동의류",
      TOY: "장난감",
      KIDS_BOOKS: "도서용품",
      BABY_GOODS: "유아용품",

      // 반려동물
      DOG_FOOD: "강아지 사료",
      CAT_FOOD: "고양이 사료",
      PET_SNACK: "간식",
      PET_TOY: "장난감",
      PET_HYGIENE: "위생용품",
    }

    return keywordMap[keyword] || keyword
  }

  // API로 인기 검색어 조회
  const {
    data: apiData,
    isLoading,
    error,
  } = useQuery(
    "popularKeywords",
    async () => {
      const response = await axios.get("/api/v1/keywords/popular/hourly", {
        withCredentials: true,
      })
      console.log("인기 검색어 API 응답:", response.data)
      return response.data
    },
    {
      refetchInterval: 5 * 60 * 1000, // 5분마다 자동 새로고침
      staleTime: 5 * 60 * 1000, // 5분간 캐시 유지
      onError: (error) => {
        console.error("인기 검색어 조회 실패:", error)
      },
    },
  )

  // API 응답을 기존 형태로 변환
  const keywordList = apiData
    ? apiData.map((item, index) => ({
        order: index + 1,
        keyword: translateKeyword(item.keyword),
      }))
    : []

  return (
    <div style={{ position: "relative" }}>
      <Accordion activeKey={activeKey} onSelect={(key) => setActiveKey(key)} style={{ width: "350px" }}>
        <Card style={{ boxShadow: "none" }}>
          <Card.Header style={{ border: "none" }}>
            <CustomToggle eventKey={"0"} keywordList={keywordList} />
          </Card.Header>
        </Card>
        <Accordion.Collapse eventKey="0">
          <Card.Body className={style.floatingBody}>
            <Stack direction={"horizontal"}>
              <span className={style.keywordBold}>실시간 쇼핑 검색어</span>
              <span className={"ms-auto cursor-pointer"} onClick={() => setActiveKey(null)}>
                <FontAwesomeIcon icon={faXmark}></FontAwesomeIcon>
              </span>
            </Stack>
            <ListGroup>
              <ListGroup.Item className="border-0">
                {isLoading ? (
                  <div className="text-center py-3">
                    <div className="spinner-border spinner-border-sm me-2" role="status" />
                    로딩중...
                  </div>
                ) : error ? (
                  <div className="text-center py-3 text-muted">검색어를 불러올 수 없습니다</div>
                ) : keywordList.length === 0 ? (
                  <div className="text-center py-3 text-muted">인기 검색어가 없습니다</div>
                ) : (
                  keywordList.map((item) => {
                    return (
                      <Stack key={item.order} className={"p-1"} direction={"horizontal"}>
                        <span style={{ color: "red", fontWeight: 700 }}>{item.order}</span>
                        <span className={"ms-2"}>{item.keyword}</span>
                      </Stack>
                    )
                  })
                )}
              </ListGroup.Item>
            </ListGroup>
          </Card.Body>
        </Accordion.Collapse>
      </Accordion>
    </div>
  )
}

export default ShoppingKeyword
