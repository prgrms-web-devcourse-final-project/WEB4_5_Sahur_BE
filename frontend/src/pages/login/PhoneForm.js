import {Form, Stack} from "react-bootstrap";

const PhoneForm = ({ phone, setPhone, error, setError, setInputStatus }) => {
    const isValidPhoneNumber = (phone) => {
        return /^\d{11}$/.test(phone);
    };

    const handlePhoneChange = (e) => {
        setPhone(e.target.value);
        if (!isValidPhoneNumber(e.target.value)) {
            setError({...error, phone: '올바른 휴대폰 번호를 입력해 주세요.'});
            setInputStatus(prev => ({ ...prev, phone: false }));
        } else {
            setError({...error, phone: ''})
            setInputStatus(prev => ({ ...prev, phone: true }));
        }
    }
    return (
        <Form.Group controlId={"forPhone"} >
            <Stack direction={"horizontal"} gap={4}>
                <Form.Label>휴대폰 번호</Form.Label>
                {error.phone && <Form.Label style={{ color: 'red', fontSize: "13px" }}>{error.phone}</Form.Label>}
            </Stack>
            <Form.Control
                type="text"
                placeholder="휴대폰 번호 (-없이 입력)"
                value={phone}
                onChange={handlePhoneChange}
            />
        </Form.Group>
    );
}

export default PhoneForm;