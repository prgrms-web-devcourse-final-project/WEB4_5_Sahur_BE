import { Form, Stack } from "react-bootstrap";

const PhoneForm = ({
  phoneNumber,
  setPhoneNumber,
  error,
  setError,
  setInputStatus,
}) => {
  const isValidPhoneNumber = (phoneNumber) => {
    return /^\d{11}$/.test(phoneNumber);
  };

  const handlePhoneChange = (e) => {
    setPhoneNumber(e.target.value);
    if (!isValidPhoneNumber(e.target.value)) {
      setError({
        ...error,
        phoneNumber: "올바른 휴대폰 번호를 입력해 주세요.",
      });
      setInputStatus((prev) => ({ ...prev, phoneNumber: false }));
    } else {
      setError({ ...error, phoneNumber: "" });
      setInputStatus((prev) => ({ ...prev, phoneNumber: true }));
    }
  };
  return (
    <Form.Group controlId={"forPhone"}>
      <Stack direction={"horizontal"} gap={4}>
        <Form.Label>휴대폰 번호</Form.Label>
        {error.phoneNumber && (
          <Form.Label style={{ color: "red", fontSize: "13px" }}>
            {error.phoneNumber}
          </Form.Label>
        )}
      </Stack>
      <Form.Control
        type="text"
        placeholder="휴대폰 번호 (-없이 입력)"
        value={phoneNumber}
        onChange={handlePhoneChange}
      />
    </Form.Group>
  );
};

export default PhoneForm;
