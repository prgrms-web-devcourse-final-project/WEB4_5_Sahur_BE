import {Form} from "react-bootstrap";
import React, {useEffect, useState} from 'react';
import {useNavigate} from "react-router-dom";
import {useQueryParam} from "../../../hooks/QueryParam";
import {isEmptyOrNull, useEnterKeySubmit} from "../../../utils/utils";

const HeaderSearchBox = () => {
    const navigate = useNavigate();
    const [queryParam, setQueryParam] = useQueryParam();
    const [keyword, setKeyword] = useState(queryParam.keyword || "");
    useEffect(() => {
        if (isEmptyOrNull(queryParam.query)) {
            handleReset();
        }
    }, [queryParam.query]);

    const handleReset = () => {
        setKeyword('');
        setQueryParam({}); // URL 쿼리스트링 초기화
    };

    const handleSearchClick = ()  => {
        if (!isEmptyOrNull(keyword)) {
            setQueryParam({ query: keyword, type: 'posts' }); // keyword 변경 시 쿼리 파라미터 업데이트
        }
    }

    // 엔터 키를 눌렀을 때 저장 버튼 클릭 동작을 위한 훅
    const handleEnterKey = useEnterKeySubmit(handleSearchClick);


    return (
        <div className="d-flex align-items-center justify-content-between" style={{ marginBottom: '20px' }}>
            <div style={{ display: "flex", alignItems: "center", gap: "8px" }}>
                <div>
                    <Form.Label className="form-label"></Form.Label>
                    <div className="kw-form-search shadow" style={{ width: "380px" }}>
                        <Form.Control
                            type="text"
                            className="form-control"
                            placeholder="상품명 혹은 브랜드명으로 검색"
                            value={keyword}
                            onChange={(e) => setKeyword(e.target.value)}
                            onKeyDown={handleEnterKey}
                        />
                        <button onClick={handleSearchClick}>검색</button>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default HeaderSearchBox;
