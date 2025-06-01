import {Col, Dropdown, Form, Row, Stack} from "react-bootstrap";
import ThemedSelect from "../../../shared/ThemedSelect";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faFilter} from "@fortawesome/free-solid-svg-icons";

const sortOptions = [
    { label: "최신순", value: "latest" },
    { label: "오래된순", value: "oldest" },
    { label: "금액 높은순", value: "high" },
    { label: "금액 낮은순", value: "row" },
]
const SearchBox = () => {
    return (
        <Stack gap={2} direction={"horizontal"}>
            <Form className="kw-form-search" style={{ width: "380px" }}>
                <Form.Control
                    type="text"
                    className="form-control"
                    placeholder="주문명, 주문번호 검색"
                />
                <button>검색</button>
            </Form>
            <Dropdown className={"border"} style={{ width: "80px" }}>
                <Dropdown.Toggle id="dropdown-custom-components" variant={""} className={"text-black"} style={{ borderColor: "#fff" }}>
                    <FontAwesomeIcon icon={faFilter} />필터
                </Dropdown.Toggle>
                <Dropdown.Menu style={{ width: "100%" }}>
                    <Dropdown.Item>모든 결제</Dropdown.Item>
                    <Dropdown.Item>결제 완료</Dropdown.Item>
                    <Dropdown.Item>결제 취소</Dropdown.Item>
                </Dropdown.Menu>
            </Dropdown>
            <ThemedSelect options={sortOptions}
                          defaultValue={sortOptions[0]}
            />
        </Stack>
    );
}

export default SearchBox;