import {Form, Stack} from "react-bootstrap";

const DeliveryMemoForm = ({ deliveryMemo, setDeliveryMemo, error, setError, setInputStatus }) => {
    const handleDeliveryMemoChange = (e) => {
        setDeliveryMemo(e.target.value);
        setError({...error, detailAdr: ''});
        setInputStatus(prev => ({ ...prev, detailAdr: true }));
    }
    return (
        <Form.Group controlId={"forDetailAdr"} >
            <Stack direction={"horizontal"} gap={4}>
                <Form.Label>배송 요청사항</Form.Label>
                {error.deliveryMemo && <Form.Label style={{ color: 'red', fontSize: "13px" }}>{error.deliveryMemo}</Form.Label>}
            </Stack>
            <Form.Control
                type="text"
                placeholder="배송시 요청사항을 입력하세요."
                value={deliveryMemo}
                onChange={handleDeliveryMemoChange}
            />
        </Form.Group>
    );
}

export default DeliveryMemoForm;