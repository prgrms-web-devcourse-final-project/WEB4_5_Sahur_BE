import {Form, Stack} from "react-bootstrap";

const PcccForm = ({ pccc, setPccc, error, setError, setInputStatus }) => {
    const handlePcccChange = (e) => {
        setPccc(e.target.value);
        setError({...error, pccc: ''});
        setInputStatus(prev => ({ ...prev, pccc: true }));
    }
    return (
        <Form.Group controlId={"forDetailAdr"} >
            <Stack direction={"horizontal"} gap={4}>
                <Form.Label>개인통관고유번호</Form.Label>
                {error.pccc && <Form.Label style={{ color: 'red', fontSize: "13px" }}>{error.pccc}</Form.Label>}
            </Stack>
            <Form.Control
                type="text"
                placeholder=" 000000000000"
                value={pccc}
                onChange={handlePcccChange}
            />
        </Form.Group>
    );
}

export default PcccForm;