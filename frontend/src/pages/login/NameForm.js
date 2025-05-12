import {Form, Stack} from "react-bootstrap";

const NameForm = ({ name, setName, error, setError, setInputStatus }) => {
    const isValidName = (name) => {
        return /^[A-Za-z가-힣]+$/.test(name);
    };

    const handleNameChange = (e) => {
        setName(e.target.value);
        if (!isValidName(e.target.value)) {
            setError({...error, name: '숫자/특수문자는 사용할 수 없습니다.'});
            setInputStatus(prev => ({ ...prev, name: false }));
        } else {
            setError({...error, name: ''})
            setInputStatus(prev => ({ ...prev, name: true }));
        }
    }

    return (
        <Form.Group controlId={"forName"} >
            <Stack direction={"horizontal"} gap={4}>
                <Form.Label>이름</Form.Label>
                {error.name && <Form.Label style={{ color: 'red', fontSize: "13px" }}>{error.name}</Form.Label>}
            </Stack>
            <Form.Control
                type="text"
                placeholder="이름"
                value={name}
                onChange={handleNameChange}
            />
        </Form.Group>
    );
}

export default NameForm;