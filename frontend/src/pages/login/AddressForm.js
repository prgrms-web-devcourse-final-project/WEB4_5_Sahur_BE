import {Button, Form, Stack} from "react-bootstrap";
import {useDaumPostcodePopup} from "react-daum-postcode";

const AddressForm = ({ zipCode, streetAdr, handleAddressSelect, error  }) => {
    const open = useDaumPostcodePopup();

    const handleSearchAddressClick = () => {
        open({ onComplete: handleAddressSelect });
    }

    return (
        <>
            <Form.Group controlId={"forZoneCode"} >
                <Stack direction={"horizontal"} gap={4}>
                    <Form.Label>우편번호</Form.Label>
                    {error.streetAdr && <Form.Label style={{ color: 'red', fontSize: "13px" }}>{error.streetAdr}</Form.Label>}
                </Stack>
                <div className={"d-flex gap-2"} >
                    <Form.Control
                        readOnly
                        type="text"
                        placeholder="우편번호"
                        value={zipCode}
                    />
                    <Button type={"button"} variant={"dark"} onClick={handleSearchAddressClick}>주소 찾기</Button>
                </div>
            </Form.Group>
            <Form.Group controlId={"forStreetAdr"} >
                <Form.Label >주소</Form.Label>
                <Form.Control
                    readOnly
                    type="text"
                    placeholder="주소"
                    value={streetAdr}
                />
            </Form.Group>
        </>
    );
}

export default AddressForm;