import {useNavigate} from "react-router-dom";
import {Button, Dropdown, Image, Stack, Badge} from "react-bootstrap";
import useConfirm from "../../../hooks/useConfirm";
import {useMutation, useQuery} from "react-query";
import axios from "axios";
import {useEffect, useState, useRef} from "react";
import Spinner from "../../../shared/Spinner";
import {useRecoilState} from "recoil";
import {userAtom} from "../../../state/atoms";
import {
    ReactComponent as EmptyLikeIcon
} from "../../../assets/images/icon/empty-like.svg";
import iconBell from "../../../assets/images/icon/icon_bell.svg";

const NavigationMenu = ({ menuItems }) => {
    const navigate = useNavigate();
    const { openConfirm } = useConfirm();
    const [loginUser, setLoginUser] = useRecoilState(userAtom);
    
    // 알림 관련 상태
    const [notifications, setNotifications] = useState([])
    const [unreadCount, setUnreadCount] = useState(0)
    const [notificationPage, setNotificationPage] = useState(0)
    const [totalNotificationPages, setTotalNotificationPages] = useState(0)
    const [isLoadingNotifications, setIsLoadingNotifications] = useState(false)
    const [hasMoreNotifications, setHasMoreNotifications] = useState(false)
    const [showNotificationDropdown, setShowNotificationDropdown] = useState(false)
    
    const notificationRef = useRef(null)

    console.log(loginUser);

    const API_BASE_URL = process.env.REACT_APP_API_URL || '';

    useEffect(() => {
        userProfileMutation.mutate();
    }, [])

    // 외부 클릭 감지
    useEffect(() => {
        const handleClickOutside = (event) => {
            if (notificationRef.current && !notificationRef.current.contains(event.target)) {
                setShowNotificationDropdown(false)
            }
        }

        document.addEventListener("mousedown", handleClickOutside)
        return () => {
            document.removeEventListener("mousedown", handleClickOutside)
        }
    }, [])

    // 읽지 않은 알림 개수 조회 API
    const fetchUnreadCount = async () => {
        try {
            const response = await axios.get(`/api/v1/notifications/member/unread-count`, {
                withCredentials: true
            });
            
            if (response.status === 200 && response.data.success) {
                setUnreadCount(response.data.data || 0);
                console.log("읽지 않은 알림 개수:", response.data.data);
            }
        } catch (error) {
            console.error("읽지 않은 알림 개수 조회 실패:", error);
            // API가 없는 경우 fallback으로 읽지 않은 알림만 조회
            try {
                const response = await axios.get(`/api/v1/notifications/member/list`, {
                    params: { page: 0, size: 100, isRead: false }, // 읽지 않은 알림만 조회
                    withCredentials: true
                });
                
                if (response.status === 200 && response.data.success) {
                    setUnreadCount(response.data.data.totalElements || 0);
                }
            } catch (fallbackError) {
                console.error("읽지 않은 알림 조회 fallback 실패:", fallbackError);
            }
        }
    };

    // 알림 조회 API
    const fetchNotifications = async (page = 0, reset = false) => {
        try {
            setIsLoadingNotifications(true)
            console.log(`알림 조회 시작: page=${page}, reset=${reset}`)
            
            // axios 사용하여 API 호출 (baseURL 자동 적용)
            const response = await axios.get(`/api/v1/notifications/member/list`, {
                params: { page, size: 5 },
                withCredentials: true
            });
            
            console.log('알림 API 응답:', response);
            
            if (response.status === 200 && response.data.success) {
                const notificationData = response.data.data;
                console.log('알림 데이터:', notificationData);

                if (reset) {
                    setNotifications(notificationData.content || []);
                } else {
                    setNotifications((prev) => [...prev, ...(notificationData.content || [])]);
                }

                setNotificationPage(page);
                setTotalNotificationPages(notificationData.totalPages || 0);
                setHasMoreNotifications(!notificationData.last);

                console.log("알림 조회 성공:", {
                    page,
                    totalElements: notificationData.totalElements,
                    totalPages: notificationData.totalPages,
                    hasMore: !notificationData.last,
                    contentLength: notificationData.content?.length || 0
                });
            } else {
                console.error("알림 API 응답 오류:", response);
            }
        } catch (error) {
            console.error("알림 조회 실패:", error);
        } finally {
            setIsLoadingNotifications(false);
        }
    };

    // 더보기 버튼 클릭
    const handleLoadMoreNotifications = () => {
        if (hasMoreNotifications && !isLoadingNotifications) {
            fetchNotifications(notificationPage + 1, false)
        }
    }

    // 알림 아이콘 클릭
    const handleNotificationIconClick = () => {
        console.log("알림 아이콘 클릭됨!")
        setShowNotificationDropdown(!showNotificationDropdown)
        if (!showNotificationDropdown && notifications.length === 0) {
            fetchNotifications(0, true) // 첫 페이지 로드
        }
    }

    // 알림 타입별 아이콘 반환
    const getNotificationIcon = (type) => {
        switch (type) {
            case "ORDER":
                return "🛒"
            case "EVENT":
                return "🎉"
            case "ETC":
                return "📢"
            default:
                return "🔔"
        }
    }

    // 알림 클릭 처리
    const handleNotificationClick = async (notification) => {
        try {
            // 읽지 않은 알림인 경우 읽음 처리
            if (!notification.isRead) {
                // PATCH /api/v1/notifications/{id} 사용
                await axios.patch(`/api/v1/notifications/${notification.notificationId}`, {}, {
                    withCredentials: true
                });

                // 로컬 상태 업데이트
                setNotifications((prev) =>
                    prev.map((n) => (n.notificationId === notification.notificationId ? { ...n, isRead: true } : n)),
                )
                setUnreadCount((prev) => Math.max(0, prev - 1))
            }

            // 드롭다운 닫기
            setShowNotificationDropdown(false)

            // URL이 있으면 해당 페이지로 이동
            if (notification.url) {
                navigate(notification.url)
            }
        } catch (error) {
            console.error("알림 읽음 처리 실패:", error)
        }
    }

    // 시간 포맷팅
    const formatNotificationTime = (createdAt) => {
        const now = new Date()
        const notificationTime = new Date(createdAt)
        const diffInMinutes = Math.floor((now - notificationTime) / (1000 * 60))

        if (diffInMinutes < 1) return "방금 전"
        if (diffInMinutes < 60) return `${diffInMinutes}분 전`

        const diffInHours = Math.floor(diffInMinutes / 60)
        if (diffInHours < 24) return `${diffInHours}시간 전`

        const diffInDays = Math.floor(diffInHours / 24)
        if (diffInDays < 7) return `${diffInDays}일 전`

        return notificationTime.toLocaleDateString("ko-KR")
    }
    
    const logoutMutation = useMutation(() => axios.post("/api/v1/auth/logout", {}), {
        onSuccess: (param) => {
            setLoginUser({ isLoggedIn: false })
            openConfirm({
                title: "로그아웃 되었습니다."
                , callback: () => navigate("/main")
                , showCancelButton: false
            });
        }
        , onError: (error) => {
            openConfirm({
                title: '처리 중 오류가 발생했습니다.',
                html: error.response?.data?.message || "에러: 관리자에게 문의바랍니다."
            });
        }
    });

    const userProfileMutation = useMutation(() => axios.get("/api/v1/members/me", {withCredentials: true}), {
        onSuccess: (param) => {
            setLoginUser(param.data.data);
            // 로그인 성공 시 읽지 않은 알림 개수 조회
            if (param.data.data.isLoggedIn) {
                fetchUnreadCount();
            }
        }
        , onError: (error) => {
            openConfirm({
                title: '처리 중 오류가 발생했습니다.',
                html: error.response?.data?.message || "에러: 관리자에게 문의바랍니다."
            });
        }
    });
    // 이미지 URL 처리 함수
    const getImageUrl = (imageUrl) => {
        if (!imageUrl) {
            // 기본 프로필 이미지는 API 서버에서 제공
            return `${API_BASE_URL}/images/default-profile.png`;
        }
        
        // 외부 URL인 경우 (소셜 로그인)
        if (imageUrl.startsWith('http://') || imageUrl.startsWith('https://')) {
            return imageUrl;
        }
        
        // 내부 경로인 경우 API 베이스 URL과 결합
        const cleanPath = imageUrl.startsWith('/') ? imageUrl : `/${imageUrl}`;
        return `${API_BASE_URL}${cleanPath}`;
    };
    return (
        <Stack direction={"horizontal"} style={{ boxShadow: 'inset 0px -5px 0px rgb(211, 211, 211)' }} >
        <span className={"ms-3 me-3 ico-menu"} />
            카테고리
            {loginUser.isLoggedIn ? <Stack direction={"horizontal"} gap={3} className={"ms-auto"} >
                    공동구매 요청하기
                    <EmptyLikeIcon width={"20"} height={"20"}/>
                    
                    {/* 알림 아이콘 */}
                    <div className="position-relative" ref={notificationRef}>
                        <div
                            style={{
                                cursor: "pointer",
                                padding: "8px",
                                border: "1px solid transparent",
                                borderRadius: "4px",
                                display: "flex",
                                alignItems: "center",
                                justifyContent: "center",
                                position: "relative"
                            }}
                            onClick={(e) => {
                                e.preventDefault()
                                e.stopPropagation()
                                handleNotificationIconClick()
                            }}
                            onMouseEnter={(e) => {
                                e.target.style.backgroundColor = "#f8f9fa"
                            }}
                            onMouseLeave={(e) => {
                                e.target.style.backgroundColor = "transparent"
                            }}
                        >
                            <img
                                src={iconBell || "/placeholder.svg"}
                                alt="알림"
                                style={{
                                    width: "20px",
                                    height: "20px",
                                    pointerEvents: "none",
                                }}
                            />
                            {unreadCount > 0 && (
                                <div
                                    style={{
                                        position: "absolute",
                                        top: "2px",
                                        right: "2px",
                                        backgroundColor: "#dc3545",
                                        color: "white",
                                        borderRadius: "50%",
                                        width: "18px",
                                        height: "18px",
                                        display: "flex",
                                        alignItems: "center",
                                        justifyContent: "center",
                                        fontSize: "10px",
                                        fontWeight: "bold",
                                        border: "2px solid white",
                                        boxShadow: "0 1px 3px rgba(0,0,0,0.3)",
                                        pointerEvents: "none",
                                        minWidth: "18px"
                                    }}
                                >
                                    {unreadCount > 99 ? "99+" : unreadCount}
                                </div>
                            )}
                        </div>

                        {/* 알림 드롭다운 */}
                        {showNotificationDropdown && (
                            <div
                                className="position-absolute bg-white border rounded shadow-lg"
                                style={{
                                    top: "100%",
                                    right: "0",
                                    width: "350px",
                                    maxHeight: "500px",
                                    overflowY: "auto",
                                    zIndex: 1050,
                                    marginTop: "8px",
                                }}
                            >
                                {/* 헤더 */}
                                <div className="d-flex justify-content-between align-items-center p-3 border-bottom">
                                    <span className="fw-bold">알림</span>
                                    {unreadCount > 0 && (
                                        <Badge bg="primary" className="rounded-pill">
                                            {unreadCount}
                                        </Badge>
                                    )}
                                </div>

                                {/* 알림 목록 */}
                                {notifications.length > 0 ? (
                                    <>
                                        {notifications.map((notification) => (
                                            <div
                                                key={notification.notificationId}
                                                onClick={() => handleNotificationClick(notification)}
                                                className={`p-3 border-bottom ${!notification.isRead ? "bg-light" : ""}`}
                                                style={{ cursor: "pointer" }}
                                            >
                                                <div className="d-flex">
                                                    <div className="me-2" style={{ fontSize: "20px" }}>
                                                        {getNotificationIcon(notification.type)}
                                                    </div>
                                                    <div className="flex-grow-1">
                                                        <div className="d-flex justify-content-between align-items-start mb-1">
                                                            <h6 className="mb-0 fw-bold" style={{ fontSize: "14px" }}>
                                                                {notification.title}
                                                            </h6>
                                                            {!notification.isRead && (
                                                                <div
                                                                    className="bg-primary rounded-circle"
                                                                    style={{ width: "8px", height: "8px", minWidth: "8px" }}
                                                                />
                                                            )}
                                                        </div>
                                                        <p className="mb-1 text-muted" style={{ fontSize: "13px" }}>
                                                            {notification.message}
                                                        </p>
                                                        <small className="text-muted">{formatNotificationTime(notification.createdAt)}</small>
                                                    </div>
                                                </div>
                                            </div>
                                        ))}

                                        {hasMoreNotifications && (
                                            <div className="text-center p-2">
                                                <button
                                                    className="btn btn-outline-primary btn-sm"
                                                    onClick={handleLoadMoreNotifications}
                                                    disabled={isLoadingNotifications}
                                                >
                                                    {isLoadingNotifications ? (
                                                        <>
                                                            <span className="spinner-border spinner-border-sm me-2" role="status" />
                                                            로딩중...
                                                        </>
                                                    ) : (
                                                        "더보기"
                                                    )}
                                                </button>
                                            </div>
                                        )}
                                    </>
                                ) : (
                                    <div className="text-center py-4 text-muted">
                                        {isLoadingNotifications ? "로딩중..." : "새로운 알림이 없습니다"}
                                    </div>
                                )}
                            </div>
                        )}
                    </div>
                    
                    <Dropdown>
                        <Dropdown.Toggle id="dropdown-custom-components" variant={""} className={"text-black"}>
                            <Image src={getImageUrl(loginUser.imageUrl)} width={25} height={25} roundedCircle onError={(e) => {
                                    e.target.src = `${API_BASE_URL}/images/default-profile.png`;
                                }}
                            />{loginUser.nickname}
                        </Dropdown.Toggle>
                        <Dropdown.Menu>
                            <Dropdown.Item href="/mypage">마이페이지</Dropdown.Item>
                            <Dropdown.Item href="/admin">관리자페이지</Dropdown.Item>
                            <Dropdown.Item onClick={() => logoutMutation.mutate()}>로그아웃</Dropdown.Item>
                        </Dropdown.Menu>
                    </Dropdown>
                </Stack> :
                    <Button variant={""} className={"text-dark ms-auto p-0"} onClick={() => navigate("/login")}>
                        <span className={"ico-user"} />로그인/회원가입
                    </Button>
                }
            <Spinner show={logoutMutation.isLoading}/>
        </Stack>
    );
};

export default NavigationMenu;