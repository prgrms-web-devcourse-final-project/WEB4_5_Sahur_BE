import { Form, Stack } from "react-bootstrap";

const DetailAdrForm = ({
  detailAdr,
  setDetailAdr,
  error,
  setError,
  setInputStatus,
}) => {
  const handleDetailAdrChange = (e) => {
    setDetailAdr(e.target.value);
    setError({ ...error, detailAdr: "" });
    setInputStatus((prev) => ({ ...prev, detailAdr: true }));
  };
  return (
    <Form.Group controlId={"forDetailAdr"}>
      <Stack direction={"horizontal"} gap={4}>
        <Form.Label>상세 주소</Form.Label>
        {error.detailAdr && (
          <Form.Label style={{ color: "red", fontSize: "13px" }}>
            {error.detailAdr}
          </Form.Label>
        )}
      </Stack>
      <Form.Control
        type="text"
        placeholder="상세 주소를 입력하세요."
        value={detailAdr}
        onChange={handleDetailAdrChange}
      />
    </Form.Group>
  );
};

export default DetailAdrForm;
