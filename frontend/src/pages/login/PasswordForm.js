import {Form, Stack} from "react-bootstrap";
import {useState} from "react";
import styles from "./Login.module.scss";
import clsx from "clsx";

const PasswordForm = ({ password, setPassword, error, setError, setInputStatus }) => {
    const [passwordConfirm, setPasswordConfirm] = useState("");
    const [passwordViewToggle, setPasswordViewToggle] = useState({
        password: false,
        passwordConfirm: false
    });

    const isValidPassword = (password) => {
        return /^(?=.*[A-Za-z])(?=.*\d)(?=.*[!@#$%^&*()_+[\]{};':"\\|,.<>/?`~\-])[A-Za-z\d!@#$%^&*()_+[\]{};':"\\|,.<>/?`~\-]{8,20}$/.test(password);
    };
    const handlePasswordChange = (e) => {
        const value = e.target.value;
        setPassword(value);

        if (password.length < 8) {
            setError({...error, password: '비밀번호는 8자 이상이어야 합니다.'});
            setInputStatus(prev => ({ ...prev, password: false }));
        } else if (!isValidPassword(value)) {
            setError({...error, password: '영문/숫자/특수문자를 포함해야 합니다.'});
            setInputStatus(prev => ({ ...prev, password: false }));
        } else if (e.target.value !== passwordConfirm) {
            setError({...error, password: '비밀번호가 일치하지 않습니다.'});
            setInputStatus(prev => ({ ...prev, password: false }));
        } else {
            setError({...error, password: ''});
            setInputStatus(prev => ({ ...prev, password: true }));
        }
    }

    const handlePasswordConfirmChange = (e) => {
        setPasswordConfirm(e.target.value);
        if (e.target.value !== password) {
            setError({...error, password: '비밀번호가 일치하지 않습니다.'})
            setInputStatus(prev => ({ ...prev, password: false }));
        } else {
            setError({...error, password: ''});
            setInputStatus(prev => ({ ...prev, password: true }));
        }
    }

    return (
        <>
            <Form.Group controlId="forPassword">
                <Stack direction={"horizontal"} gap={4}>
                    <Form.Label>비밀번호</Form.Label>
                    {error.password && <Form.Label style={{ color: 'red', fontSize: "13px" }}>{error.password}</Form.Label>}
                </Stack>
                <div className={`${styles.inputWithPasswordView} flex-grow-1`}>
                    <Form.Control
                        type={passwordViewToggle.password ? "text" : "password"}
                        placeholder="비밀번호 (8자 이상)"
                        value={password}
                        onChange={handlePasswordChange}
                    />
                    <span className={clsx(styles.passwordView, {[styles.active]: passwordViewToggle.password})}
                          onClick={() => setPasswordViewToggle({ ...passwordViewToggle, password: !passwordViewToggle.password })} />
                </div>
            </Form.Group>
            <Form.Group controlId={"forPasswordConfirm"} >
                <Stack direction={"horizontal"} gap={4}>
                    <Form.Label>비밀번호 확인</Form.Label>
                    {error.passwordConfirm && <Form.Label style={{ color: 'red', fontSize: "13px" }}>{error.passwordConfirm}</Form.Label>}
                </Stack>
                <div className={`${styles.inputWithPasswordView} flex-grow-1`}>
                <Form.Control
                    type={passwordViewToggle.passwordConfirm ? "text" : "password"}
                    placeholder="비밀번호 확인"
                    value={passwordConfirm}
                    onChange={handlePasswordConfirmChange}
                />
                    <span className={clsx(styles.passwordView, {[styles.active]: passwordViewToggle.passwordConfirm})}
                          onClick={() => setPasswordViewToggle({ ...passwordViewToggle, passwordConfirm: !passwordViewToggle.passwordConfirm })} />
                </div>
            </Form.Group>
        </>
    );
}

export default PasswordForm;