import {Button, Form} from "react-bootstrap";
import useConfirm from "../../hooks/useConfirm";
import {useState} from "react";
import ProfileImageZone from "./ProfileImageZone";
import LoginLayout from "./LoginLayout";
import EmailForm from "./EmailForm";
import PasswordForm from "./PasswordForm";
import NicknameForm from "./NicknameForm";
import {useMutation} from "react-query";
import axios from "axios";
import {useNavigate} from "react-router-dom";
import NameForm from "./NameForm";
import PhoneForm from "./PhoneForm";
import AddressForm from "./AddressForm";
import Spinner from "../../shared/Spinner";
import DetailAdrForm from "./DetailAdrForm";

const Signup = () => {
    const { openConfirm } = useConfirm();
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [name, setName] = useState("");
    const [phone, setPhone] = useState("");
    const [nickname, setNickname] = useState("");
    const [zipCode, setZipCode] = useState("");
    const [streetAdr, setStreetAdr] = useState("");
    const [detailAdr, setDetailAdr] = useState("");
    const [error, setError] = useState({
        email: '',
        password: '',
        name: '',
        phone: '',
        nickname: '',
        zipCode: '',
        streetAdr: '',
        detailAdr: ''
    })
    const [inputStatus, setInputStatus] = useState({
        email: false,
        password: false,
        name: false,
        phone: false,
        nickname: false,
        zipCode: false,
        streetAdr: false,
        detailAdr: false,
        privateInfo: false
    })
    const navigate = useNavigate();
    const [profileImageFile, setProfileImageFile] = useState(null);

    const handleAddressSelect = (data) => {
        setZipCode(data.zonecode);
        setStreetAdr(data.address);
        setError({ ...error, streetAdr: '' })
        setInputStatus(prev => ({ ...prev, zipCode: true, streetAdr: true }));
    }

    const handlePrivateInfoAgreeChange = (e) => {
        if (e.target.checked) {
            setInputStatus(prev => ({ ...prev, privateInfo: true }));
        } else {
            setInputStatus(prev => ({ ...prev, privateInfo: false }));
        }
    }

    const requestSignup = (sendData) => {
        // json 데이터 삽입
        const formData = new FormData();
        formData.append("memberData", new Blob([JSON.stringify(sendData)], {
            type: "application/json",
        }));
        // file 데이터 삽입
        formData.append("image", profileImageFile);

        // 생성 요청
        return axios.post('/api/v1/auth/signup', formData, {
            headers: {
                "Content-Type": "multipart/form-data",
            },
        });
    }

    const signUpMutation = useMutation(requestSignup, {
        onSuccess: (response) => {
            openConfirm({
                title: response.data.msg
                , showCancelButton: false
                , callback: () => navigate('/login')
            });
        }
        , onError: (error) => {
            console.log(error);
            openConfirm({
                title: '처리 중 오류가 발생했습니다.',
                html: error.response?.data?.msg || "에러: 관리자에게 문의바랍니다."
            });
        }
    });

    const handleSignupClick = (e) => {
        e.preventDefault();
        if (password.length === 0) {
            setError({ ...error, password: '비밀번호를 입력하세요.' });
            window.scrollTo(0, 500);
            return;
        }
        if (name.length === 0) {
            setError({ ...error, name: '이름을 입력하세요.' });
            window.scrollTo(0, 500);
            return;
        }
        if (phone.length === 0) {
            setError({ ...error, phone: '휴대폰번호를 입력하세요.' });
            window.scrollTo(0, 500);
            return;
        }
        if (nickname.length === 0) {
            setError({ ...error, nickname: '별명을 입력하세요.' });
            window.scrollTo(0, 500);
            return;
        }
        if (streetAdr.length === 0 || zipCode.length === 0) {
            setError({ ...error, streetAdr: '주소를 입력해주세요.' });
            window.scrollTo(0, 500);
            return;
        }
        if (detailAdr.length === 0) {
            setError({ ...error, detailAdr: '상세 주소를 입력해주세요.' });
            window.scrollTo(0, 500);
            return;
        }
        if (Object.values(inputStatus).every(Boolean)) {
            // 모든 입력 상태가 true일 때 실행됨
            signUpMutation.mutate({
                email,
                password,
                name,
                phone,
                nickname,
                zipCode,
                streetAdr,
                detailAdr
            });
        } else { //유효성 검사 실패
            openConfirm({
                title: "입력이 완료되지 않은 항목이 있습니다. 입력을 완료해주세요.",
                showCancelButton: false
            })
        }
    }

    return (
        <LoginLayout>
            <Form className="kw-login-input gap-2" onSubmit={handleSignupClick}>
                <Form.Label>프로필 이미지</Form.Label>
                <ProfileImageZone handleProfileImageChange={(file) => setProfileImageFile(file)}/>
                <EmailForm email={email} setEmail={setEmail} error={error} setError={setError} setInputStatus={setInputStatus} />
                <PasswordForm password={password} setPassword={setPassword} error={error} setError={setError} setInputStatus={setInputStatus} />
                <NameForm name={name} setName={setName} error={error} setError={setError} setInputStatus={setInputStatus} />
                <PhoneForm phone={phone} setPhone={setPhone} error={error} setError={setError} setInputStatus={setInputStatus} />
                <NicknameForm nickname={nickname} setNickname={setNickname} error={error} setError={setError} setInputStatus={setInputStatus} />
                <AddressForm zipCode={zipCode} streetAdr={streetAdr} handleAddressSelect={handleAddressSelect} error={error} />
                <DetailAdrForm detailAdr={detailAdr} setDetailAdr={setDetailAdr} error={error} setError={setError} setInputStatus={setInputStatus} />
                <Form.Check type={"checkbox"} label={"이용 약관 및 개인정보 처리방침에 동의합니다"} onChange={handlePrivateInfoAgreeChange} />
                <div className="kw-login-button">
                    <Button type="submit" variant={"primary"}>회원 가입</Button>
                </div>
            </Form>
            <Spinner show={signUpMutation.isLoading} />
        </LoginLayout>
    );
}

export default Signup;