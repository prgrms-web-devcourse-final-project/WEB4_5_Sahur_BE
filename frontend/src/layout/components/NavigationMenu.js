import {Link, useLocation, useNavigate} from "react-router-dom";
import clsx from "clsx";
import {Button, Stack} from "react-bootstrap";

const NavigationMenu = ({ menuItems }) => {
    const location = useLocation();
    const navigate = useNavigate();
    return (
        <Stack direction={"horizontal"} style={{ boxShadow: 'inset 0px -5px 0px rgb(211, 211, 211)' }} >
        <span className={"ms-3 me-3 ico-menu"} />
            카테고리
            {menuItems.map((item) => {
                const isActive = item.menuUrl === location.pathname;

                return (
                    <div key={item.menuId}
                        className={clsx(
                            "kw-header-gnb-item",
                            { "active": isActive },
                        )}
                    >
                        <Link to={"#"}>{item.menuName}</Link>
                    </div>
                );
            })}
            <Button variant={""} className={"text-dark ms-auto"} onClick={() => navigate("/login")}>
                <span className={"ico-user"} />로그인/회원가입
            </Button>
        </Stack>
    );
};

export default NavigationMenu;
