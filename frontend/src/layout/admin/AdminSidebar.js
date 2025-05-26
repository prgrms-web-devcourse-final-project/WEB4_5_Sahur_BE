import {Card, Stack} from "react-bootstrap";
import {
    ReactComponent as DashboardIcon
} from "../../assets/images/sidebar/dashboard.svg";
import {
    ReactComponent as ProductsIcon
} from "../../assets/images/sidebar/products.svg";
import {
    ReactComponent as GroupBuyIcon
} from "../../assets/images/sidebar/groupbuy.svg";
import {
    ReactComponent as OrdersIcon
} from "../../assets/images/sidebar/orders.svg";
import {
    ReactComponent as ReviewsIcon
} from "../../assets/images/sidebar/reviews.svg";
import {useNavigate} from "react-router-dom";

const menuList = [
    {id: 'dashboard', name: '대시보드'},
    {id: 'products', name: '상품 관리'},
    {id: 'groupBuy', name: '공동구매 관리'},
    {id: 'orders', name: '주문 관리'},
    {id: 'reviews', name: '리뷰 관리'},
];

const activeStyles = { background: "#F3E8FF", color: "#581C87" };

const AdminSidebar = ({ pageId }) => {
    const navigate = useNavigate();

    const renderMenu = (menu) => {
        switch (menu.id) {
            case  'dashboard':
                return <Stack key={menu.id} direction={"horizontal"} className={"p-2 cursor-pointer"}
                              gap={2} style={menu.id === pageId ? activeStyles : null}
                              onClick={() => navigate('dashboard')}><DashboardIcon />{menu.name}</Stack>
            case  'products':
                return <Stack key={menu.id} direction={"horizontal"} className={"p-2 cursor-pointer"}
                              gap={2} style={menu.id === pageId ? activeStyles : null}
                              onClick={() => navigate('products')}><ProductsIcon />{menu.name}</Stack>
            case  'groupBuy':
                return <Stack key={menu.id} direction={"horizontal"} className={"p-2 cursor-pointer"}
                              gap={2} style={menu.id === pageId ? activeStyles : null}
                              onClick={() => navigate('groupBuy')}><GroupBuyIcon />{menu.name}</Stack>
            case  'orders':
                return <Stack key={menu.id} direction={"horizontal"} className={"p-2 cursor-pointer"}
                              gap={2} style={menu.id === pageId ? activeStyles : null}
                              onClick={() => navigate('orders')}><OrdersIcon />{menu.name}</Stack>
            case  'reviews':
                return <Stack key={menu.id} direction={"horizontal"} className={"p-2 cursor-pointer"}
                              gap={2} style={menu.id === pageId ? activeStyles : null}
                              onClick={() => navigate('reviews')}><ReviewsIcon />{menu.name}</Stack>
            default:
                return null;
        }
    }
    return (
        <Card className="p-3" style={{ width: '240px', borderRadius: 0 }}>
            <Card.Header className={"px-0 py-2"}>
                <h4 style={{ fontWeight: 600 }}>퉁하자 관리자</h4>
            </Card.Header>
            <Card.Body>
                <Stack className={"fs-4"} direction={"vertical"}>
                    {menuList.map((menu) => {
                        return renderMenu(menu);
                    })}
                </Stack>
            </Card.Body>
        </Card>
    );
}

export default AdminSidebar;