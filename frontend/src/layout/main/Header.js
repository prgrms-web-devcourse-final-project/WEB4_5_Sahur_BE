"use client"

import { useState, useEffect } from "react"
import { Link, useNavigate } from "react-router-dom"
import { Dropdown, Stack } from "react-bootstrap"
import HeaderSearchBox from "./components/HeaderSearchBox"
import ShoppingKeyword from "./components/ShoppingKeyword"
import NavigationMenu from "./components/NavigationMenu"
import logo from "../../assets/images/tung.png"

import icoAll from "../../assets/images/category/icons_ALL.png"
import icoBeauty from "../../assets/images/category/icons_BEAUTY.png"
import icoBook from "../../assets/images/category/icons_BOOK.png"
import icoCar from "../../assets/images/category/icons_CAR.png"
import icoDigitalAppliance from "../../assets/images/category/icons_DIGITAL_APPLIANCE.png"
import icoFashionAccessory from "../../assets/images/category/icons_FASHION_ACCESSORY.png"
import icoFashionClothes from "../../assets/images/category/icons_FASHION_CLOTHES.png"
import icoFood from "../../assets/images/category/icons_FOOD.png"
import icoFurniture from "../../assets/images/category/icons_FURNITURE.png"
import icoKids from "../../assets/images/category/icons_KIDS.png"
import icoLiving from "../../assets/images/category/icons_LIVING.png"
import icoPet from "../../assets/images/category/icons_PET.png"
import icoSports from "../../assets/images/category/icons_SPORTS.png"
import style from "./Header.module.scss"

const Header = () => {
  const navigate = useNavigate()
  const [isLoggedIn, setIsLoggedIn] = useState(false)
  const [userInfo, setUserInfo] = useState(null)
  const [notifications, setNotifications] = useState([])
  const [unreadCount, setUnreadCount] = useState(0)

  const categoryIcon = [
    { name: "전체", icon: icoAll },
    { name: "패션의류", icon: icoFashionClothes },
    { name: "패션잡화", icon: icoFashionAccessory },
    { name: "뷰티", icon: icoBeauty },
    { name: "디지털/가전", icon: icoDigitalAppliance },
    { name: "가구/인테리어", icon: icoFurniture },
    { name: "생활/건강", icon: icoLiving },
    { name: "식품", icon: icoFood },
    { name: "스포츠레저", icon: icoSports },
    { name: "자동차/공구", icon: icoCar },
    { name: "도서/음반/DVD", icon: icoBook },
    { name: "유아동/출산", icon: icoKids },
    { name: "반려동물", icon: icoPet },
  ]

  // 사용자 정보 조회 API
  useEffect(() => {
    const checkLoginStatus = async () => {
      try {
        const token = localStorage.getItem("accessToken")
        if (token) {
          const response = await fetch("/api/user/me", {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          })
          if (response.ok) {
            const userData = await response.json()
            setIsLoggedIn(true)
            setUserInfo(userData)
            fetchNotifications()
          } else {
            localStorage.removeItem("accessToken")
            setIsLoggedIn(false)
          }
        }
      } catch (error) {
        console.error("사용자 정보 조회 실패:", error)
        setIsLoggedIn(false)
      }
    }

    checkLoginStatus()
  }, [])

  // 알림 조회 API
  const fetchNotifications = async () => {
    try {
      const token = localStorage.getItem("accessToken")
      const response = await fetch("/api/notifications", {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })
      if (response.ok) {
        const notificationData = await response.json()
        setNotifications(notificationData.notifications || [])
        setUnreadCount(notificationData.unreadCount || 0)
      }
    } catch (error) {
      console.error("알림 조회 실패:", error)
    }
  }

  // 로그아웃 처리
  const handleLogout = async () => {
    try {
      const token = localStorage.getItem("accessToken")
      await fetch("/api/auth/logout", {
        method: "POST",
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })
    } catch (error) {
      console.error("로그아웃 API 호출 실패:", error)
    } finally {
      localStorage.removeItem("accessToken")
      setIsLoggedIn(false)
      setUserInfo(null)
      setNotifications([])
      setUnreadCount(0)
      navigate("/login")
    }
  }

  // 알림 읽음 처리
  const markNotificationAsRead = async (notificationId) => {
    try {
      const token = localStorage.getItem("accessToken")
      await fetch(`/api/notifications/${notificationId}/read`, {
        method: "PATCH",
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })
      fetchNotifications() // 알림 목록 새로고침
    } catch (error) {
      console.error("알림 읽음 처리 실패:", error)
    }
  }

  return (
    <header className="kw-header">
      <div className="kw-header-gnb">
        <div className="kw-header-gnb-brand">
          <Link to={"/main"}>
            <img src={logo || "/placeholder.svg"} style={{ width: "120px" }} alt="퉁하자" />
          </Link>
        </div>
        <HeaderSearchBox />
        <ShoppingKeyword />

        {/* 사용자 메뉴 영역 추가 */}
        <div className="d-flex align-items-center gap-3">
          {isLoggedIn ? (
            <>
              {/* 알림 아이콘 */}
              <Dropdown>
                <Dropdown.Toggle variant="link" className="p-0 border-0 position-relative">
                  <img
                    src="/src/assets/images/icon/icon_bell.svg"
                    alt="알림"
                    style={{ width: "24px", height: "24px" }}
                  />
                  {unreadCount > 0 && (
                    <span
                      className="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger"
                      style={{ fontSize: "10px" }}
                    >
                      {unreadCount > 99 ? "99+" : unreadCount}
                    </span>
                  )}
                </Dropdown.Toggle>
                <Dropdown.Menu style={{ width: "300px", maxHeight: "400px", overflowY: "auto" }}>
                  <Dropdown.Header>알림</Dropdown.Header>
                  {notifications.length > 0 ? (
                    notifications.map((notification) => (
                      <Dropdown.Item
                        key={notification.id}
                        onClick={() => markNotificationAsRead(notification.id)}
                        className={!notification.isRead ? "bg-light" : ""}
                      >
                        <div className="d-flex flex-column">
                          <small className="text-muted">{notification.type}</small>
                          <span className="fw-bold">{notification.title}</span>
                          <small>{notification.message}</small>
                          <small className="text-muted">{new Date(notification.createdAt).toLocaleDateString()}</small>
                        </div>
                      </Dropdown.Item>
                    ))
                  ) : (
                    <Dropdown.Item disabled>새로운 알림이 없습니다</Dropdown.Item>
                  )}
                </Dropdown.Menu>
              </Dropdown>

              {/* 사용자 메뉴 */}
              <Dropdown>
                <Dropdown.Toggle
                  variant="link"
                  className="p-0 border-0 d-flex align-items-center text-decoration-none text-dark"
                >
                  <img
                    src={userInfo?.profileImage || "/src/assets/images/icon/ico_user.svg"}
                    alt="프로필"
                    style={{ width: "32px", height: "32px", borderRadius: "50%" }}
                    className="me-2"
                  />
                  <span>{userInfo?.nickname || userInfo?.name}</span>
                </Dropdown.Toggle>
                <Dropdown.Menu>
                  <Dropdown.Item onClick={() => navigate("/mypage")}>
                    <img src="/src/assets/images/icon/ico_user.svg" alt="" className="me-2" style={{ width: "16px" }} />
                    마이페이지
                  </Dropdown.Item>
                  <Dropdown.Item onClick={() => navigate("/mypage/orders")}>
                    <img
                      src="/src/assets/images/sidebar/orders.svg"
                      alt=""
                      className="me-2"
                      style={{ width: "16px" }}
                    />
                    주문내역
                  </Dropdown.Item>
                  <Dropdown.Item onClick={() => navigate("/mypage/groupbuy")}>
                    <img
                      src="/src/assets/images/sidebar/groupbuy.svg"
                      alt=""
                      className="me-2"
                      style={{ width: "16px" }}
                    />
                    공동구매
                  </Dropdown.Item>
                  <Dropdown.Item onClick={() => navigate("/mypage/wishlist")}>
                    <img
                      src="/src/assets/images/sidebar/love-product.svg"
                      alt=""
                      className="me-2"
                      style={{ width: "16px" }}
                    />
                    찜한상품
                  </Dropdown.Item>
                  <Dropdown.Divider />
                  <Dropdown.Item onClick={() => navigate("/mypage/settings")}>
                    <img
                      src="/src/assets/images/sidebar/config.svg"
                      alt=""
                      className="me-2"
                      style={{ width: "16px" }}
                    />
                    설정
                  </Dropdown.Item>
                  <Dropdown.Item onClick={handleLogout}>
                    <img
                      src="/src/assets/images/sidebar/logout.svg"
                      alt=""
                      className="me-2"
                      style={{ width: "16px" }}
                    />
                    로그아웃
                  </Dropdown.Item>
                </Dropdown.Menu>
              </Dropdown>
            </>
          ) : (
            <div className="d-flex gap-2">
              <button className="btn btn-outline-primary btn-sm" onClick={() => navigate("/login")}>
                로그인
              </button>
              <button className="btn btn-primary btn-sm" onClick={() => navigate("/signup")}>
                회원가입
              </button>
            </div>
          )}
        </div>
      </div>
      <NavigationMenu />
      <Stack direction={"horizontal"} className={"mt-2 justify-content-center"}>
        {categoryIcon.map((item) => {
          return (
            <div key={item.name} className={`ms-4 me-4 d-flex flex-column align-items-center ${style.category}`}>
              <img src={item.icon || "/placeholder.svg"} style={{ width: "40px" }} alt={item.name} />
              <div>{item.name}</div>
            </div>
          )
        })}
      </Stack>
    </header>
  )
}

export default Header
