import LoginLayout from "./LoginLayout";
import {Button, Form} from "react-bootstrap";
import EmailForm from "./EmailForm";
import PasswordForm from "./PasswordForm";
import {useState} from "react";
import {useMutation} from "react-query";
import axios from "axios";
import useConfirm from "../../hooks/useConfirm";
import {useNavigate} from "react-router-dom";

const PasswordReset = () => {
    const { openConfirm } = useConfirm();
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState({
        email: '',
        password: ''
    });
    const [inputStatus, setInputStatus] = useState({
        email: false,
        password: false
    });
    const navigate = useNavigate();


    const handlePasswordChangeClick = (e) => {
        e.preventDefault();
        if (Object.values(inputStatus).every(Boolean)) {
            passwordResetMutation.mutate({ email, password })
        }
    }

    const passwordResetMutation = useMutation((sendData) => axios.post('/api/v1/members/password/reset', sendData), {
        onSuccess: (response) => {
            openConfirm({
                title: response.data.msg
                , showCancelButton: false
                , callback: () => navigate('/login')
            });
        }
        , onError: (error) => {
            console.log(error)
            openConfirm({
                title: '처리 중 오류가 발생했습니다.',
                html: error.response?.data?.msg || "에러: 관리자에게 문의바랍니다."
            });
        }
    });

    return (
        <LoginLayout>
            <Form className="kw-login-input gap-4 mt-5" onSubmit={handlePasswordChangeClick}>
                <EmailForm email={email} setEmail={setEmail} error={error} setError={setError} setInputStatus={setInputStatus} />
                {inputStatus.email && <PasswordForm password={password} setPassword={setPassword} error={error} setError={setError} setInputStatus={setInputStatus} />}
                {inputStatus.email && <div className="kw-login-button mt-10">
                    <Button type="submit" variant={"primary"}>비밀 번호 변경</Button>
                </div>}
            </Form>
        </LoginLayout>
    );
}

export default PasswordReset;