import React from "react";
import { Card } from 'react-bootstrap';

/**
 * 공통 카드 컴포넌트
 * @param {ReactNode} children - 카드 내용
 * @param {string} header - 카드 헤더 텍스트 (옵션)
 * @param {boolean} hasMarginBottom - 마진 바텀 적용 여부 (기본값: false)
 * @param {string | number} maxHeight - 카드의 최대 높이 설정 (옵션, 기본값 없음)
 */
const TableBackGroundCard = ({ children, header, hasMarginBottom = false, maxHeight }) => {
    return (
        <Card style={{ marginBottom: hasMarginBottom ? '30px' : '0px' }}>
            {header &&
                <Card.Header className="bg-white">
                    <h5 className="mb-0">{header}</h5>
                </Card.Header>}
            <Card.Body style={{ 
                overflow: 'auto',  // 스크롤 가능하도록 설정
                maxHeight: maxHeight || 'none',  // 최대 높이 옵션 적용
                width: '100%',  // 부모 크기에 맞춤
                display: 'flex',
                flexDirection: 'column'
            }}>
                <div className="kw-list" style={{ flexGrow: 1 }}>
                    {children}
                </div>
            </Card.Body>
        </Card>
    );
};

export default TableBackGroundCard;
