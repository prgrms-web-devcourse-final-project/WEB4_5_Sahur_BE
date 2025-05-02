import React from "react";
import { Table } from 'react-bootstrap';
/**
 * 공통 테이블 컴포넌트
 * @param {Array} columns - 테이블 헤더 목록
 * @param {ReactNode} children - 테이블의 `<tbody>`를 직접 정의할 때 사용
 */
const SimpleTable = ({ columns=[], children }) => {
    return (
        <div className="kw-list px-0">
            <div className="kw-list-table">
                <div className="kw-list-table-scroll">
                    <Table className="table">
                        <thead>
                            <tr>
                                {columns.map((col, index) => (
                                    <th key={index}>
                                        <div className="kw-list-table-title">{col}</div>
                                    </th>
                                ))}
                            </tr>
                        </thead>
                        {children}
                    </Table>
                </div>
            </div>
        </div>
    );
};

export default SimpleTable;
