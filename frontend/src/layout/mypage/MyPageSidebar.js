import { Card, Stack } from "react-bootstrap";
import { ReactComponent as DashboardIcon } from "../../assets/images/sidebar/dashboard.svg";
import { ReactComponent as LogoutIcon } from "../../assets/images/sidebar/logout.svg";
import { ReactComponent as GroupBuyIcon } from "../../assets/images/sidebar/groupbuy.svg";
import { ReactComponent as OrdersIcon } from "../../assets/images/sidebar/orders.svg";
import { ReactComponent as ReviewsIcon } from "../../assets/images/sidebar/reviews.svg";
import { ReactComponent as PaymentsIcon } from "../../assets/images/sidebar/payment-list.svg";
import { ReactComponent as DibsIcon } from "../../assets/images/sidebar/love-product.svg";
import { useNavigate } from "react-router-dom";
import axios from "axios";

const menuList = [
  { id: "dashboard", name: "대시보드" },
  { id: "orders", name: "주문 내역" },
  { id: "payments", name: "결제 내역" },
  { id: "requests", name: "상품 등록 요청" },
  { id: "dibs", name: "관심 상품" },
  { id: "reviews", name: "리뷰 관리" },
];

const activeStyles = { background: "#F3E8FF", color: "#581C87" };

const MyPageSidebar = ({ pageId }) => {
  const navigate = useNavigate();

  const handleLogout = async () => {
    try {
      await axios.post("/api/v1/auth/logout");
      // 로그아웃 성공 시 메인 페이지로 이동
      navigate("/main");
    } catch (error) {
      console.error("로그아웃 중 오류가 발생했습니다:", error);
      // 에러가 발생해도 메인 페이지로 이동 (선택사항)
      // navigate('/main');
    }
  };

  const renderMenu = (menu) => {
    switch (menu.id) {
      case "dashboard":
        return (
          <Stack
            key={menu.id}
            direction={"horizontal"}
            className={"p-2 cursor-pointer"}
            gap={2}
            style={menu.id === pageId ? activeStyles : null}
            onClick={() => navigate("dashboard")}
          >
            <DashboardIcon />
            {menu.name}
          </Stack>
        );
      case "orders":
        return (
          <Stack
            key={menu.id}
            direction={"horizontal"}
            className={"p-2 cursor-pointer"}
            gap={2}
            style={menu.id === pageId ? activeStyles : null}
            onClick={() => navigate("orders")}
          >
            <OrdersIcon />
            {menu.name}
          </Stack>
        );
      case "payments":
        return (
          <Stack
            key={menu.id}
            direction={"horizontal"}
            className={"p-2 cursor-pointer"}
            gap={2}
            style={menu.id === pageId ? activeStyles : null}
            onClick={() => navigate("payments")}
          >
            <PaymentsIcon />
            {menu.name}
          </Stack>
        );
      case "requests":
        return (
          <Stack
            key={menu.id}
            direction={"horizontal"}
            className={"p-2 cursor-pointer"}
            gap={2}
            style={menu.id === pageId ? activeStyles : null}
            onClick={() => navigate("requests")}
          >
            <GroupBuyIcon />
            {menu.name}
          </Stack>
        );
      case "dibs":
        return (
          <Stack
            key={menu.id}
            direction={"horizontal"}
            className={"p-2 cursor-pointer"}
            gap={2}
            style={menu.id === pageId ? activeStyles : null}
            onClick={() => navigate("dibs")}
          >
            <DibsIcon />
            {menu.name}
          </Stack>
        );
      case "reviews":
        return (
          <Stack
            key={menu.id}
            direction={"horizontal"}
            className={"p-2 cursor-pointer"}
            gap={2}
            style={menu.id === pageId ? activeStyles : null}
            onClick={() => navigate("reviews")}
          >
            <ReviewsIcon />
            {menu.name}
          </Stack>
        );
      default:
        return null;
    }
  };

  return (
    <Card className="p-3 mx-3 shadow">
      <Card.Body>
        <Stack className={"fs-4"} direction={"vertical"}>
          {menuList.map((menu) => {
            return renderMenu(menu);
          })}
          <Stack
            direction={"horizontal"}
            className={"p-2 cursor-pointer"}
            gap={2}
            onClick={handleLogout}
          >
            <LogoutIcon />
            로그아웃
          </Stack>
        </Stack>
      </Card.Body>
    </Card>
  );
};

export default MyPageSidebar;
