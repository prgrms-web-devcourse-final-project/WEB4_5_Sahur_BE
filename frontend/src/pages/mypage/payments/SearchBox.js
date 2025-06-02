import { Dropdown, Form, Stack, Button } from "react-bootstrap";
import ThemedSelect from "../../../shared/ThemedSelect";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faFilter,
  faRefresh,
  faSearch,
} from "@fortawesome/free-solid-svg-icons";
import { useState } from "react";

const sortOptions = [
  { label: "최신순", value: "latest" },
  { label: "오래된순", value: "oldest" },
  { label: "금액 높은순", value: "high" },
  { label: "금액 낮은순", value: "low" },
];

const SearchBox = ({
  onSearch,
  onStatusFilter,
  onSortChange,
  searchQuery,
  statusFilter,
  sortOption,
}) => {
  const [inputValue, setInputValue] = useState(searchQuery);

  const handleSearchSubmit = (e) => {
    e.preventDefault();
    onSearch(inputValue);
  };

  const handleInputChange = (e) => {
    setInputValue(e.target.value);
  };

  const handleStatusSelect = (status) => {
    onStatusFilter(status);
  };

  const handleSortSelect = (selectedOption) => {
    onSortChange(selectedOption.value);
  };

  const handleClearAll = () => {
    setInputValue("");
    onSearch("");
    onStatusFilter("");
    onSortChange("latest");
  };

  const getStatusFilterText = () => {
    switch (statusFilter) {
      case "PAID":
        return "결제 완료";
      case "CANCELED":
        return "결제 취소";
      case "COMPLETED":
        return "주문 완료";
      case "INDELIVERY":
        return "배송 중";
      default:
        return "전체";
    }
  };

  const getCurrentSortOption = () => {
    return (
      sortOptions.find((option) => option.value === sortOption) ||
      sortOptions[0]
    );
  };

  return (
    <Stack gap={2} direction={"horizontal"} className="align-items-center">
      <Form
        className="kw-form-search"
        style={{ width: "380px" }}
        onSubmit={handleSearchSubmit}
      >
        <Form.Control
          type="text"
          className="form-control"
          placeholder="주문번호, 상품명으로 검색"
          value={inputValue}
          onChange={handleInputChange}
        />
        <button type="submit">
          <FontAwesomeIcon icon={faSearch} /> 검색
        </button>
      </Form>

      <Dropdown className={"border"} style={{ minWidth: "120px" }}>
        <Dropdown.Toggle
          id="dropdown-status-filter"
          variant={"outline-secondary"}
          className={"text-dark"}
          style={{ width: "100%" }}
        >
          <FontAwesomeIcon icon={faFilter} /> {getStatusFilterText()}
        </Dropdown.Toggle>
        <Dropdown.Menu style={{ width: "100%" }}>
          <Dropdown.Item
            onClick={() => handleStatusSelect("")}
            active={statusFilter === ""}
          >
            전체
          </Dropdown.Item>
          <Dropdown.Item
            onClick={() => handleStatusSelect("PAID")}
            active={statusFilter === "PAID"}
          >
            결제 완료
          </Dropdown.Item>
          <Dropdown.Item
            onClick={() => handleStatusSelect("CANCELED")}
            active={statusFilter === "CANCELED"}
          >
            결제 취소
          </Dropdown.Item>
          <Dropdown.Item
            onClick={() => handleStatusSelect("COMPLETED")}
            active={statusFilter === "COMPLETED"}
          >
            주문 완료
          </Dropdown.Item>
          <Dropdown.Item
            onClick={() => handleStatusSelect("INDELIVERY")}
            active={statusFilter === "INDELIVERY"}
          >
            배송 중
          </Dropdown.Item>
        </Dropdown.Menu>
      </Dropdown>

      <div style={{ minWidth: "140px" }}>
        <ThemedSelect
          options={sortOptions}
          value={getCurrentSortOption()}
          onChange={handleSortSelect}
        />
      </div>
    </Stack>
  );
};

export default SearchBox;
