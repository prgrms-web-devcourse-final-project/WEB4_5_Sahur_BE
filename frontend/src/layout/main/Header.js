"use client";

import { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import { Dropdown, Stack } from "react-bootstrap";
import HeaderSearchBox from "./components/HeaderSearchBox";
import ShoppingKeyword from "./components/ShoppingKeyword";
import NavigationMenu from "./components/NavigationMenu";
import logo from "../../assets/images/tung.png";

import icoAll from "../../assets/images/category/icons_ALL.png";
import icoBeauty from "../../assets/images/category/icons_BEAUTY.png";
import icoBook from "../../assets/images/category/icons_BOOK.png";
import icoCar from "../../assets/images/category/icons_CAR.png";
import icoDigitalAppliance from "../../assets/images/category/icons_DIGITAL_APPLIANCE.png";
import icoFashionAccessory from "../../assets/images/category/icons_FASHION_ACCESSORY.png";
import icoFashionClothes from "../../assets/images/category/icons_FASHION_CLOTHES.png";
import icoFood from "../../assets/images/category/icons_FOOD.png";
import icoFurniture from "../../assets/images/category/icons_FURNITURE.png";
import icoKids from "../../assets/images/category/icons_KIDS.png";
import icoLiving from "../../assets/images/category/icons_LIVING.png";
import icoPet from "../../assets/images/category/icons_PET.png";
import icoSports from "../../assets/images/category/icons_SPORTS.png";
import style from "./Header.module.scss";

// 파일 상단에 이미지 import 추가
import emptyLike from "../../assets/images/icon/empty-like.svg";

const Header = () => {
  const navigate = useNavigate();
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [userInfo, setUserInfo] = useState(null);
  const [categories, setCategories] = useState([]);
  const [isLoadingCategories, setIsLoadingCategories] = useState(true);

  // 카테고리 목록 조회 API
  const fetchCategories = async () => {
    try {
      setIsLoadingCategories(true);
      const response = await fetch("/api/v1/categories", {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
        },
      });

      // 응답 상태 확인
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      // Content-Type 확인 (JSON인지 확인)
      const contentType = response.headers.get("content-type");
      if (!contentType || !contentType.includes("application/json")) {
        throw new Error("Response is not JSON");
      }

      const result = await response.json();
      console.log("API 응답:", result); // 디버깅용 로그
      console.log("받은 데이터 개수:", result.data?.length); // 데이터 개수 확인

      if (result.success && result.data) {
        // 백엔드 데이터에서 중복 제거 (categoryType 기준)
        const uniqueCategories = result.data.reduce((acc, item) => {
          if (
            !acc.find((existing) => existing.categoryType === item.categoryType)
          ) {
            acc.push(item);
          }
          return acc;
        }, []);

        console.log("중복 제거 후 데이터 개수:", uniqueCategories.length); // 중복 제거 후 개수 확인

        // 백엔드 데이터 구조에 맞춰 변환
        const transformedCategories = uniqueCategories.map((item) => ({
          id: item.categoryType,
          name: getCategoryName(item.categoryType),
          icon: getIconForCategory(item.categoryType),
          productCount: 0,
          categoryId: item.categoryId,
          keyword: item.keyword,
        }));

        // 전체 카테고리를 맨 앞에 추가
        const finalCategories = [
          { id: "ALL", name: "전체", icon: icoAll, productCount: 0 },
          ...transformedCategories,
        ];

        setCategories(finalCategories);
      } else {
        throw new Error("Invalid response structure");
      }
    } catch (error) {
      console.error("카테고리 조회 API 호출 실패:", error);
      // 에러 시 기본 카테고리 사용
      setCategories(getDefaultCategories());
    } finally {
      setIsLoadingCategories(false);
    }
  };

  // 카테고리 타입에 따른 한글 이름 반환
  const getCategoryName = (categoryType) => {
    const nameMap = {
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
    };
    return nameMap[categoryType] || categoryType;
  };

  // 아이콘 매핑 함수
  const getIconForCategory = (categoryId) => {
    const iconMap = {
      ALL: icoAll,
      FASHION_CLOTHES: icoFashionClothes,
      FASHION_ACCESSORY: icoFashionAccessory,
      BEAUTY: icoBeauty,
      DIGITAL_APPLIANCE: icoDigitalAppliance,
      FURNITURE: icoFurniture,
      LIVING: icoLiving,
      FOOD: icoFood,
      SPORTS: icoSports,
      CAR: icoCar,
      BOOK: icoBook,
      KIDS: icoKids,
      PET: icoPet,
    };
    return iconMap[categoryId] || "/placeholder.svg";
  };

  // 기본 카테고리 반환 함수
  const getDefaultCategories = () => {
    return [
      { id: "ALL", name: "전체", icon: icoAll, productCount: 0 },
      {
        id: "FASHION_CLOTHES",
        name: "패션의류",
        icon: icoFashionClothes,
        productCount: 0,
      },
      {
        id: "FASHION_ACCESSORY",
        name: "패션잡화",
        icon: icoFashionAccessory,
        productCount: 0,
      },
      { id: "BEAUTY", name: "뷰티", icon: icoBeauty, productCount: 0 },
      {
        id: "DIGITAL_APPLIANCE",
        name: "디지털/가전",
        icon: icoDigitalAppliance,
        productCount: 0,
      },
      {
        id: "FURNITURE",
        name: "가구/인테리어",
        icon: icoFurniture,
        productCount: 0,
      },
      { id: "LIVING", name: "생활/건강", icon: icoLiving, productCount: 0 },
      { id: "FOOD", name: "식품", icon: icoFood, productCount: 0 },
      { id: "SPORTS", name: "스포츠레저", icon: icoSports, productCount: 0 },
      { id: "CAR", name: "자동차/공구", icon: icoCar, productCount: 0 },
      { id: "BOOK", name: "도서/음반/DVD", icon: icoBook, productCount: 0 },
      { id: "KIDS", name: "유아동/출산", icon: icoKids, productCount: 0 },
      { id: "PET", name: "반려동물", icon: icoPet, productCount: 0 },
    ];
  };

  // 사용자 정보 조회 API
  useEffect(() => {
    const checkLoginStatus = async () => {
      try {
        const token = localStorage.getItem("accessToken");
        if (token) {
          const response = await fetch("/api/user/me", {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          });
          if (response.ok) {
            const userData = await response.json();
            setIsLoggedIn(true);
            setUserInfo(userData);
          } else {
            localStorage.removeItem("accessToken");
            setIsLoggedIn(false);
          }
        }
      } catch (error) {
        console.error("사용자 정보 조회 실패:", error);
        setIsLoggedIn(false);
      }
    };

    checkLoginStatus();
    fetchCategories(); // 카테고리 조회 추가
  }, []);

  // 로그아웃 처리
  const handleLogout = async () => {
    try {
      const token = localStorage.getItem("accessToken");
      await fetch("/api/auth/logout", {
        method: "POST",
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
    } catch (error) {
      console.error("로그아웃 API 호출 실패:", error);
    } finally {
      localStorage.removeItem("accessToken");
      setIsLoggedIn(false);
      setUserInfo(null);
      navigate("/login");
    }
  };

  // 카테고리 클릭 처리
  const handleCategoryClick = (category) => {
    if (category.id === "ALL") {
      navigate("/main");
    } else {
      navigate(`/main?categoryType=${category.id}`);
    }
  };

  return (
    <header className="kw-header">
      <div className="kw-header-gnb">
        <div className="kw-header-gnb-brand">
          <Link to={"/main"}>
            <img
              src={logo || "/placeholder.svg"}
              style={{ width: "120px" }}
              alt="퉁하자"
            />
          </Link>
        </div>
        <HeaderSearchBox />
        <ShoppingKeyword />
      </div>
      <NavigationMenu />
      <Stack direction={"horizontal"} className={"mt-2 justify-content-center"}>
        {isLoadingCategories ? (
          <div
            className="d-flex justify-content-center align-items-center"
            style={{ height: "80px" }}
          >
            <div
              className="spinner-border spinner-border-sm text-primary"
              role="status"
            >
              <span className="visually-hidden">Loading...</span>
            </div>
          </div>
        ) : (
          categories.map((item) => {
            // 아이콘 매핑 (API에서 아이콘 URL을 제공하지 않는 경우 대비)
            const getIconForCategory = (categoryId) => {
              const iconMap = {
                ALL: icoAll,
                FASHION_CLOTHES: icoFashionClothes,
                FASHION_ACCESSORY: icoFashionAccessory,
                BEAUTY: icoBeauty,
                DIGITAL_APPLIANCE: icoDigitalAppliance,
                FURNITURE: icoFurniture,
                LIVING: icoLiving,
                FOOD: icoFood,
                SPORTS: icoSports,
                CAR: icoCar,
                BOOK: icoBook,
                KIDS: icoKids,
                PET: icoPet,
              };
              return iconMap[categoryId] || "/placeholder.svg";
            };

            return (
              <div
                key={item.id}
                className={`ms-4 me-4 d-flex flex-column align-items-center ${style.category}`}
                onClick={() => handleCategoryClick(item)}
                style={{ cursor: "pointer" }}
              >
                <img
                  src={item.iconUrl || getIconForCategory(item.id)}
                  style={{ width: "40px" }}
                  alt={item.name}
                />
                <div className="text-center">
                  <div>{item.name}</div>
                </div>
              </div>
            );
          })
        )}
      </Stack>
    </header>
  );
};

export default Header;
