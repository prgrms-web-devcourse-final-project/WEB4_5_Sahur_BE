import {Button, Form, Stack} from "react-bootstrap";
import styles from "./Login.module.scss";
import {useEffect, useState} from "react";
import {useMutation} from "react-query";
import axios from "axios";
import useConfirm from "../../hooks/useConfirm";
import {isEmptyOrNull} from "../../utils/utils";
import Spinner from "../../shared/Spinner";
import {useLocation} from "react-router-dom";

const EmailForm = ({ email, setEmail , error, setError, setInputStatus}) => {
    const { openConfirm } = useConfirm();
    const { pathname } = useLocation();
    const [emailAuthStep, setEmailAuthStep] = useState('prepare');
    const [authCode, setAuthCode] = useState('');
    const [timeLeft, setTimeLeft] = useState(0); // 초 단위
    const authCodeSendMutation = useMutation((sendData) => axios.post(pathname === '/signup' ? '/api/v1/auth/email/send' : '/api/v1/auth/password/email/send', sendData), {
        onSuccess: (response) => {
            setTimeLeft(180); // 3분
            setEmailAuthStep('pending');
            openConfirm({
                title: response.data.data.message
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

    const authCodeVerifyMutation = useMutation((sendData) => axios.post(pathname === '/signup' ? '/api/v1/auth/email/verify' : '/api/v1/auth/password/email/verify', sendData), {
        onSuccess: (response) => {
            setEmailAuthStep('finish');
            setError({ ...error, email: '' });
            setInputStatus(prev => ({ ...prev, email: true }))
            openConfirm({
                title: response.data.data.message
                , showCancelButton: false
            });
        }
        , onError: (error) => {
            console.log(error)
            openConfirm({
                title: '처리 중 오류가 발생했습니다.',
                html: error.response?.data?.message || "에러: 관리자에게 문의바랍니다."
            });
        }
    });

    const isValidEmail = (email) => {
        return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
    };

    const handleEmailChange = (e) => {
        const value = e.target.value;
        setEmail(value);

        if (!isValidEmail(value)) {
            setError({...error, email: '유효한 이메일 주소를 입력하세요.'});
        } else {
            setError({...error, email: ''});
        }
    }

    const handleAuthCodeSendClick = () => {
        if (isEmptyOrNull(email)) {
            setError({ ...error, email: '이메일을 입력하세요.' });
            return;
        }
        if (!isEmptyOrNull(error.email)) {
            setError({ ...error, email: error.email });
            return;
        }
        authCodeSendMutation.mutate({ email });
    };

    // 타이머 감소
    useEffect(() => {
        if (timeLeft === 0) {
            setEmailAuthStep('prepare');
            return;
        }

        const timer = setInterval(() => {
            setTimeLeft((prev) => {
                if (prev <= 1) clearInterval(timer); // 0이면 정지
                return prev - 1;
            });
        }, 1000);


        return () => clearInterval(timer);
    }, [timeLeft]);

    // 시간 포맷 MM:SS
    const formatTime = (seconds) => {
        const m = String(Math.floor(seconds / 60)).padStart(2, '0');
        const s = String(seconds % 60).padStart(2, '0');
        return `${m}:${s}`;
    };

    const handleAuthCodeVerifyClick = () => {
        authCodeVerifyMutation.mutate({email, authCode})
    }

    return (
        <Form.Group controlId="formEmail">
            <Stack direction={"horizontal"} gap={4}>
                <Form.Label>이메일</Form.Label>
                {error.email && <Form.Label style={{ color: 'red', fontSize: "13px" }}>{error.email}</Form.Label>}
            </Stack>
            <div className={"d-flex gap-2"} >
                <Form.Control
                    type="text"
                    placeholder="이메일 주소"
                    value={email}
                    readOnly={emailAuthStep === 'finish'}
                    onChange={handleEmailChange}
                />
                {emailAuthStep === 'finish' ? <span className={styles.emailAuthFinish}>인증 완료</span> :
                    <Button type={"button"} variant={"dark"} onClick={handleAuthCodeSendClick}>
                        {emailAuthStep === 'prepare' ? '인증번호 전송' : '재전송'}
                    </Button>}
            </div>
            {emailAuthStep === 'pending' && (
                <div className="d-flex gap-2 mt-2 align-items-center">
                    {/* 인증번호 입력창 + 타이머 */}
                    <div className={`${styles.inputWithTimer} flex-grow-1`}>
                        <Form.Control
                            type="text"
                            placeholder="인증 번호"
                            value={authCode}
                            onChange={(e) => setAuthCode(e.target.value)}
                            style={{ paddingRight: '60px' }} // 타이머 공간 확보
                        />
                        <div className={styles.timerOverlay}>
                            {formatTime(timeLeft)}
                        </div>
                    </div>

                    {/* 확인 버튼 */}
                    <Button
                        type="button"
                        variant="dark"
                        style={{ minWidth: '90px' }}
                        onClick={handleAuthCodeVerifyClick}
                    >
                        확인
                    </Button>
                </div>
            )}
        <Spinner show={authCodeSendMutation.isLoading} />
        </Form.Group>
    );
}

export default EmailForm;