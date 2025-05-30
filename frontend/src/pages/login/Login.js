import kakao from "../../assets/images/oauth/kakao.png"
import naver from "../../assets/images/oauth/naver.png"
import google from "../../assets/images/oauth/google.png"
import {useState, useEffect} from "react";
import {Link, useNavigate, useSearchParams} from "react-router-dom";
import {isEmptyOrNull} from "../../utils/utils";
import Spinner from "../../shared/Spinner";
import useConfirm from "../../hooks/useConfirm";
import axios from "axios";
import {Button, Form} from "react-bootstrap";
import LoginLayout from "./LoginLayout";
import {useMutation} from "react-query";
import styles from "./Login.module.scss";
import clsx from "clsx";

const Login = () => {
    const navigate = useNavigate();
    const [searchParams] = useSearchParams();
    const { openConfirm } = useConfirm();
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [passwordViewToggle, setPasswordViewToggle] = useState(false);
    const [rememberMe, setRememberMe] = useState(false);
    const [error, setError] = useState({
        email: '',
        password: ''
    })

    const handleLoginRememberChange = (e) => {
        const checked = e.target.checked;
        setRememberMe(checked);
    }

    const handleLoginClick = (e) => {
        e.preventDefault();
        if (isEmptyOrNull(email)) {
            setError({ ...error, email: '이메일을 입력해주세요.' })
            return false;
        }

        if (isEmptyOrNull(password)) {
            setError({ ...error, password: '비밀번호를 입력해주세요.' })
            return false;
        }
        setError({ email: '', password: '' })
        loginMutation.mutate({email, password, rememberMe});
    }

    const loginMutation = useMutation((sendData) => axios.post('/api/v1/auth/login', sendData, {skipAuthInterceptor: true}), {
        onSuccess: (response) => {
            navigate('/main');
        }
        , onError: (error) => {
            console.log(error)
            openConfirm({
                title: '처리 중 오류가 발생했습니다.',
                html: error.response?.data?.message || "에러: 관리자에게 문의바랍니다."
            });
        }
    });

    // 소셜 로그인 결과 처리
    useEffect(() => {
        const authStatus = searchParams.get('auth_status');
        const authInfo = searchParams.get('info');
        
        if (authStatus === 'success') {
            navigate('/main');
        } else if (authInfo === 'auth_info') {
            openConfirm({
                title: '로그인 처리 중 오류가 발생했습니다.',
                html: '다시 시도해주세요.',
                showCancelButton: false
            });
        }
    }, [searchParams, navigate, openConfirm]);

    return (
        <LoginLayout>
            <Form className="kw-login-input gap-2" onSubmit={handleLoginClick}>
                <Form.Group controlId="formEmail">
                    <Form.Label >이메일</Form.Label>
                    <Form.Control
                        type="text"
                        placeholder="이메일 주소"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                    />
                    {error.email && <Form.Label style={{ color: 'red', fontSize: "13px" }}>{error.email}</Form.Label>}
                </Form.Group>
                <Form.Group controlId="forPassword" className={"mt-2"}>
                    <div className={"d-flex justify-content-between"} >
                        <Form.Label>비밀번호</Form.Label>
                        <span style={{ color: "#9333EA", cursor: "pointer" }} onClick={() => navigate("/password-reset")}>비밀번호 찾기</span>
                    </div>
                    <div className={`${styles.inputWithPasswordView} flex-grow-1`}>
                        <Form.Control
                            type={passwordViewToggle ? "text" : "password"}
                            placeholder="비밀번호"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                        />
                        <span className={clsx(styles.passwordView, {[styles.active]: passwordViewToggle})}
                              onClick={() => setPasswordViewToggle(prev => !prev)} />
                    </div>
                    {error.password && <Form.Label style={{ color: 'red', fontSize: "13px" }}>{error.password}</Form.Label>}
                </Form.Group>
                <Form.Check type={"checkbox"} label={"로그인 상태 유지"} onChange={handleLoginRememberChange}></Form.Check>
                <div className="kw-login-button">
                    <Button type="submit" variant={"primary"}>로그인</Button>
                </div>
            </Form>
            <div className="kw-login-text d-flex justify-content-center">
                <Link to={"https://api.devapi.store/oauth2/authorization/naver"} className={"mx-5"}><img src={naver} /></Link>
                <Link to={"https://api.devapi.store/oauth2/authorization/kakao"} className={"mx-5"}><img src={kakao} /></Link>
                <Link to={"https://api.devapi.store/oauth2/authorization/google"} className={"mx-5"}><img src={google} /></Link>
            </div>
            <Spinner show={loginMutation.isLoading} />
        </LoginLayout>
    );
}

export default Login;