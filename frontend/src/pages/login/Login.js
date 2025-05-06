import kakao from "../../assets/images/oauth/kakao.png"
import naver from "../../assets/images/oauth/naver.png"
import google from "../../assets/images/oauth/google.png"
import logo from "../../assets/images/tung.png"
import arrowLeft from "../../assets/images/icon/arrow-left.png"
import {useState} from "react";
import {Link, useNavigate} from "react-router-dom";
import {isEmptyOrNull, useEnterKeySubmit} from "../../utils/utils";
import Spinner from "../../shared/Spinner";
import useConfirm from "../../hooks/useConfirm";
import axios from "axios";
import {Button, Card, Form, Stack} from "react-bootstrap";

const Login = () => {
    const navigate = useNavigate();
    const { openConfirm } = useConfirm();
    const [loginId, setLoginId] = useState("");
    const [password, setPassword] = useState("");
    const [isLoading, setIsLoading] = useState(false);

    const handleClickLogin = () => {
        let sendData = undefined;

        if (isEmptyOrNull(loginId)) {
            openConfirm({
                title: '로그인 중 오류가 발생했습니다.',
                html: '사용자 계정을 입력하세요'
            });
            return false;
        }

        if (isEmptyOrNull(password)) {
            openConfirm({
                title: '로그인 중 오류가 발생했습니다.',
                html: '사용자 비밀번호를 입력하세요'
            });
            return false;
        }

        sendData = {email : loginId, password};
        requestLogin(sendData);
    }

    const requestLogin = async (sendData) => {
        try {
            setIsLoading(true);
            const response = await axios.post('/auth/login', sendData);
            const authHeader = response.headers["authorization"];
            //const authHeader = response.headers.get("Authorization");
            if (authHeader && authHeader.startsWith("Bearer ")) {
                const token = authHeader.substring(7);
                localStorage.setItem("token", token);
            }
            navigate('/main')
        } catch (error) {
            console.log("error login api: ", error);
            openConfirm({
                title: '데이터를 불러오는 중 오류가 발생했습니다.',
                html: error.response?.data?.message || "에러: 관리자에게 문의바랍니다."
            });
        }
        finally {
            setIsLoading(false);
        }
    }

    // 엔터 키를 눌렀을 때 로그인 버튼 클릭 동작을 위한 훅
    const handleEnterKey = useEnterKeySubmit(handleClickLogin);

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
                <div className="kw-login-form p-1 my-3" style={{ background: "var(--BackGround, #F1F5F9)" }}>
                    <Stack direction={"horizontal"} className="justify-content-center align-items-center">
                        <Button variant={""} className={`w-100 bg-white`} style={{ color: "#020817" }}>로그인</Button>
                        <Button variant={""} className={"w-100"} style={{ color: "#64748B" }} onClick={() => navigate("/signup")}>회원가입</Button>
                    </Stack>
                </div>
            </div>
            <div className="kw-login">
                <div className="kw-login-form">
                    <div className={"text-center"}>
                        <h3>퉁하자 로그인</h3>
                        <address className={"text-gray-300"}>계정 정보를 입력하여 로그인하세요</address>
                    </div>
                    <Form className="kw-login-input gap-2" onSubmit={handleClickLogin}>
                        <Form.Label >이메일</Form.Label>
                        <Form.Control
                            type="text"
                            placeholder="이메일 주소"
                            value={loginId}
                            onChange={(event) => setLoginId(event.target.value)}
                            onKeyDown={handleEnterKey}
                        />
                        <Form.Label>비밀번호</Form.Label>
                        <Form.Control
                            type="password"
                            placeholder="비밀번호"
                            value={password}
                            onChange={(event) => setPassword(event.target.value)}
                            onKeyDown={handleEnterKey}
                        />
                        <Form.Check type={"checkbox"} label={"로그인 상태 유지"}></Form.Check>
                        <div className="kw-login-button">
                            <Button type="submit" variant={"primary"}>로그인</Button>
                        </div>
                    </Form>
                    <div className="kw-login-text d-flex justify-content-center">
                        <Link to={"#"} className={"mx-5"}><img src={naver} /></Link>
                        <Link to={"#"} className={"mx-5"}><img src={kakao} /></Link>
                        <Link to={"#"} className={"mx-5"}><img src={google} /></Link>
                    </div>
                </div>
            </div>
            <Spinner show={isLoading}/>
        </>
    );
}

export default Login;