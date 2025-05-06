import logo from "../assets/images/tung.png"
import {Link, useLocation, useNavigate} from "react-router-dom";
import useConfirm from "../hooks/useConfirm";
import {Stack} from "react-bootstrap";
import axios from "axios";
import icoAll from "../assets/images/category/icons_ALL.png"
import icoBeauty from "../assets/images/category/icons_BEAUTY.png"
import icoBook from "../assets/images/category/icons_BOOK.png"
import icoCar from "../assets/images/category/icons_CAR.png"
import icoDigitalAppliance
    from "../assets/images/category/icons_DIGITAL_APPLIANCE.png"
import icoFashionAccessory
    from "../assets/images/category/icons_FASHION_ACCESSORY.png"
import icoFashionClothes
    from "../assets/images/category/icons_FASHION_CLOTHES.png"
import icoFood from "../assets/images/category/icons_FOOD.png"
import icoFurniture from "../assets/images/category/icons_FURNITURE.png"
import icoKids from "../assets/images/category/icons_KIDS.png"
import icoLiving from "../assets/images/category/icons_LIVING.png"
import icoPet from "../assets/images/category/icons_PET.png"
import icoSports from "../assets/images/category/icons_SPORTS.png"
import {useMutation} from "react-query";
import style from "./Header.module.scss"
import NavigationMenu from "./components/NavigationMenu";
import HeaderSearchBox from "./components/HeaderSearchBox";
import Spinner from "../shared/Spinner";
import ShoppingKeyword from "./components/ShoppingKeyword";

const Header = () => {
    const navigate = useNavigate();
    const { openConfirm } = useConfirm();
    const { pathname } = useLocation();

    const handleLogoutClick = async () => {
        openConfirm({
            title: "로그아웃 하시겠습니까?",
            callback: () => logoutMutation.mutate()
        })
    }

    const logoutMutation = useMutation(() => axios.post("/auth/logout", {}), {
        onSuccess: (param) => {
            localStorage.removeItem('token');
            openConfirm({
                title: "로그아웃 되었습니다."
                , callback: () => navigate("/login")
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

    const menuItems = [
        { menuId: 1, menuUrl: "/main", menuName: "홈"},
        { menuId: 2, menuUrl: "/popular", menuName: "인기"},
        { menuId: 3, menuUrl: "/latest", menuName: "신규"},
        { menuId: 4, menuUrl: "/closing", menuName: "마감임박"},
    ];
    
    const categoryIcon = [
        {name: "전체", icon: icoAll },
        {name: "패션의류", icon: icoFashionClothes },
        {name: "패션잡화", icon: icoFashionAccessory },
        {name: "뷰티", icon: icoBeauty },
        {name: "디지털/가전", icon: icoDigitalAppliance },
        {name: "가구/인테리어", icon: icoFurniture },
        {name: "생활/건강", icon: icoLiving },
        {name: "식품", icon: icoFood },
        {name: "스포츠레저", icon: icoSports },
        {name: "자동차/공구", icon: icoCar },
        {name: "도서/음반/DVD", icon: icoBook },
        {name: "유아동/출산", icon: icoKids },
        {name: "반려동물", icon: icoPet },
    ]

    return (
        <header className="kw-header">
            <div className="kw-header-gnb">
                <div className="kw-header-gnb-brand">
                    <Link to={"/main"}>
                        <img src={logo} style={{ width: "120px" }} alt="퉁하자"/>
                    </Link>
                </div>
                <HeaderSearchBox />
                <ShoppingKeyword />
            </div>
            <NavigationMenu menuItems={menuItems} />
            <Stack direction={"horizontal"} className={"mt-2 justify-content-center"}>
                {categoryIcon.map((item) => {
                    return  <div key={item.name}
                                 className={`ms-4 me-4 d-flex flex-column align-items-center ${style.category}`}>
                        <img src={item.icon} style={{ width: "40px" }} alt={item.name} />
                        <div>{item.name}</div>
                    </div>
                })}
            </Stack>
            <Spinner show={logoutMutation.isLoading}/>
        </header>
    );
}

export default Header;