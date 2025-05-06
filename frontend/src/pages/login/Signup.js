import {useNavigate} from "react-router-dom";
import {Button, Card, Form, Stack} from "react-bootstrap";
import Spinner from "../../shared/Spinner";
import axios from "axios";
import useConfirm from "../../hooks/useConfirm";
import {isEmptyOrNull, useEnterKeySubmit} from "../../utils/utils";
import {useState} from "react";
import arrowLeft from "../../assets/images/icon/arrow-left.png";
import logo from "../../assets/images/tung.png";
import ProfileImageZone from "./ProfileImageZone";

const Signup = () => {
    const navigate = useNavigate();
    const { openConfirm } = useConfirm();
    const [loginId, setLoginId] = useState("");
    const [password, setPassword] = useState("");
    const [nickname, setNickName] = useState("");
    const [name, setName] = useState("");
    const [isLoading, setIsLoading] = useState(false);
    const [profileImageFile, setProfileImageFile] = useState();


    const handleClickLogin = () => {
        // 이메일 공백 + 형식 체크
        if (isEmptyOrNull(loginId)) {
            openConfirm({
                title: '회원가입 중 오류가 발생했습니다.',
                html: '이메일은 필수 항목입니다.'
            });
            return false;
        }
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(loginId)) {
            openConfirm({
                title: '회원가입 중 오류가 발생했습니다.',
                html: '올바른 이메일 형식이 아닙니다.'
            });
            return false;
        }

        // 비밀번호 공백 + 길이 + 특수문자 포함
        if (isEmptyOrNull(password)) {
            openConfirm({
                title: '회원가입 중 오류가 발생했습니다.',
                html: '비밀번호는 필수 항목입니다.'
            });
            return false;
        }
        if (password.length < 8) {
            openConfirm({
                title: '회원가입 중 오류가 발생했습니다.',
                html: '비밀번호는 8자 이상이어야 합니다.'
            });
            return false;
        }
        const passwordRegex = /^(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;
        if (!passwordRegex.test(password)) {
            openConfirm({
                title: '회원가입 중 오류가 발생했습니다.',
                html: '비밀번호는 특수문자를 포함해야 합니다.'
            });
            return false;
        }

        // 닉네임 공백 + 길이 체크
        if (isEmptyOrNull(nickname)) {
            openConfirm({
                title: '회원가입 중 오류가 발생했습니다.',
                html: '닉네임은 필수 항목입니다.'
            });
            return false;
        }
        if (nickname.length < 3 || nickname.length > 20) {
            openConfirm({
                title: '회원가입 중 오류가 발생했습니다.',
                html: '닉네임은 3자 이상 20자 이하로 입력해주세요.'
            });
            return false;
        }

        // 이름 공백 체크
        if (isEmptyOrNull(name)) {
            openConfirm({
                title: '회원가입 중 오류가 발생했습니다.',
                html: '이름은 필수 항목입니다.'
            });
            return false;
        }

        // 모든 검증 통과 시 백엔드로 전송
        const sendData = {
            email: loginId,
            password,
            nickname,
            name
        };
        requestSignup(sendData);
    };

    const requestSignup = async (sendData) => {
        try {
            setIsLoading(true);
            const response = await axios.post('/auth/signup', sendData);
            // localStorage.setItem("token", response.data);
            navigate('/login')
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
                        <Button variant={""} className={"w-100"} style={{ color: "#64748B" }} onClick={() => navigate("/login")}>로그인</Button>
                        <Button variant={""} className={`w-100 bg-white`} style={{ color: "#020817" }}>회원가입</Button>
                    </Stack>
                </div>
            </div>
            <div className="kw-login">
                <div className="kw-login-form">
                    <div className={"text-center"}>
                        <h3>회원가입</h3>
                        <address className={"text-gray-300"}>퉁하자의 회원이 되어 다양한 혜택을 누리세요</address>
                    </div>
                    <Form className="kw-login-input gap-2" onSubmit={handleClickLogin}>
                        <Form.Label>프로필 이미지</Form.Label>
                        <ProfileImageZone profileImageUrl={""} handleProfileImageChange={(file) => setProfileImageFile(file)}/>
                        <Form.Group controlId="formEmail">
                            <Form.Label>이메일</Form.Label>
                            <div className={"d-flex gap-2"} >
                                <Form.Control
                                    type="text"
                                    placeholder="이메일 주소"
                                    value={loginId}
                                    onChange={(event) => setLoginId(event.target.value)}
                                    onKeyDown={handleEnterKey}
                                />
                                <Button type={"button"} variant={"dark"}>인증번호 전송</Button>
                            </div>
                        </Form.Group>
                        <Form.Group controlId="forPassword">
                            <Form.Label>비밀번호</Form.Label>
                            <Form.Control
                                type="password"
                                placeholder="비밀번호 (8자 이상)"
                                value={password}
                                onChange={(event) => setPassword(event.target.value)}
                                onKeyDown={handleEnterKey}
                            />
                        </Form.Group>
                        <Form.Group controlId={"forPasswordConfirm"} >
                            <Form.Label>비밀번호 확인</Form.Label>
                            <Form.Control
                                type="password"
                                placeholder="비밀번호 확인"
                                value={password}
                                onChange={(event) => setPassword(event.target.value)}
                                onKeyDown={handleEnterKey}
                            />
                        </Form.Group>
                        <Form.Group controlId={"forName"} >
                            <Form.Label >이름</Form.Label>
                            <Form.Control
                                type="text"
                                placeholder="이름"
                                value={loginId}
                                onChange={(event) => setLoginId(event.target.value)}
                                onKeyDown={handleEnterKey}
                            />
                        </Form.Group>
                        <Form.Group controlId={"forPhone"} >
                            <Form.Label >휴대폰 번호</Form.Label>
                            <Form.Control
                                type="text"
                                placeholder="휴대폰 번호 (-없이 입력)"
                                value={loginId}
                                onChange={(event) => setLoginId(event.target.value)}
                                onKeyDown={handleEnterKey}
                            />
                        </Form.Group>
                        <Form.Group controlId={"forAlias"} >
                            <Form.Label >별명</Form.Label>
                            <div className={"d-flex gap-2"} >
                                <Form.Control
                                    type="text"
                                    placeholder="별명"
                                    value={loginId}
                                    onChange={(event) => setLoginId(event.target.value)}
                                    onKeyDown={handleEnterKey}
                                />
                                <Button type={"button"} variant={"dark"}>중복 확인</Button>
                            </div>
                        </Form.Group>
                        <Form.Group controlId={"forPostNumber"} >
                            <Form.Label >우편번호</Form.Label>
                            <div className={"d-flex gap-2"} >
                                <Form.Control
                                    type="text"
                                    placeholder="우편번호"
                                    value={loginId}
                                    onChange={(event) => setLoginId(event.target.value)}
                                    onKeyDown={handleEnterKey}
                                />
                                <Button type={"button"} variant={"dark"}>주소 찾기</Button>
                            </div>
                        </Form.Group>
                        <Form.Group controlId={"forAddress"} >
                            <Form.Label >주소</Form.Label>
                            <Form.Control
                                type="text"
                                placeholder="주소"
                                value={loginId}
                                onChange={(event) => setLoginId(event.target.value)}
                                onKeyDown={handleEnterKey}
                            />
                        </Form.Group>
                        <Form.Group controlId={"forAddressDetail"} >
                            <Form.Label >상세 주소</Form.Label>
                            <Form.Control
                                type="text"
                                placeholder="상세 주소를 입력하세요"
                                value={loginId}
                                onChange={(event) => setLoginId(event.target.value)}
                                onKeyDown={handleEnterKey}
                            />
                        </Form.Group>
                        <Form.Check type={"checkbox"} label={"이용 약관 및 개인정보 처리방침에 동의합니다"}></Form.Check>
                        <div className="kw-login-button">
                            <Button type="submit" variant={"primary"}>회원 가입</Button>
                        </div>
                    </Form>
                </div>
            </div>
            <Spinner show={isLoading}/>
        </>
    );
}

export default Signup;