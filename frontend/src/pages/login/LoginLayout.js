import {Button, Card, Stack} from "react-bootstrap";
import arrowLeft from "../../assets/images/icon/arrow-left.png";
import logo from "../../assets/images/tung.png";
import {useLocation, useNavigate} from "react-router-dom";
import clsx from "clsx";

const LoginLayout = ({ children }) => {
    const navigate = useNavigate();
    const { pathname } = useLocation();

    const getTitle = () => {
        let title = '';
        let desc = '';
        if (pathname === '/login') {
            title = '로그인'
            desc = '계정 정보를 입력하여 로그인하세요';
        } else if (pathname === '/signup') {
            title = '회원가입';
            desc = '퉁하자의 회원이 되어 다양한 혜택을 누리세요';
        } else if (pathname === '/password-reset') {
            title = '비밀번호 찾기';
            desc = '';
        }
        return <>
            <h3>{title}</h3>
            <small className={"text-gray-300"}>{desc}</small>
        </>
    }

    return (
        <>
            <Card>
                <Card.Body className={"ps-5"} style={{ background: "#E9D5FF" }}>
                    <span className={"cursor-pointer"} onClick={() => navigate("/main")}>
                        <img src={arrowLeft} style={{ width: "20px" }}/>
                        <img src={logo} style={{ width: "120px" }}/>
                    </span>
                </Card.Body>
            </Card>
            <div className="kw-login">
                <div style={pathname !== '/password-reset' ? {} : {visibility: "hidden"}}>
                    <div className="kw-login-form p-1 my-3"
                         style={{background: "var(--BackGround, #F1F5F9)"}}>
                        <Stack direction={"horizontal"}
                               className="justify-content-center align-items-center">
                            <Button variant={""}
                                    className={clsx("w-100",
                                        {'bg-white': pathname === '/login'})}
                                    style={pathname === '/login'
                                        ? {color: "#020817"} : {color: "#64748B"}}
                                    onClick={() => navigate("/login")}>로그인</Button>
                            <Button variant={""}
                                    className={clsx("w-100",
                                        {'bg-white': pathname === '/signup'})}
                                    style={pathname === '/login'
                                        ? {color: "#64748B"} : {color: "#020817"}}
                                    onClick={() => navigate(
                                        "/signup")}>회원가입</Button>
                        </Stack>
                    </div>
                </div>
            </div>
            <div className="kw-login">
                <div className="kw-login-form">
                    <div className={"text-center"}>
                        {getTitle()}
                    </div>
                    {children}
                </div>
            </div>
        </>
    );
}

export default LoginLayout;