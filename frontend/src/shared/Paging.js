import { Form, Pagination } from "react-bootstrap";
import React from "react";
import { PAGE_LIMIT as DEFAULT_PAGE_LIMIT } from "../utils/enum";

const Paging = ({ page, handlePageClick, totalCount, pageLimit = DEFAULT_PAGE_LIMIT,
    handlePageLimitChange, pageLimitOptions = [10, 20, 30], showPageLimitSelect = true, useShowAllData = false }) => {
    const totalPages = Math.ceil(totalCount / pageLimit); //총 페이지 수
    const visiblePages = 5; //화면에 보이는 페이지버튼 갯수
    const startPage = Math.max(1, Math.floor((page - 1) / visiblePages) * visiblePages + 1);
    const endPage = Math.min(totalPages, startPage + visiblePages - 1);

    const renderPageButtonList = () => {
        return Array.from(
            { length: endPage - startPage + 1 },
            (_, index) => startPage + index
        ).map((i) => {
            return (
                <Pagination.Item key={i} active={Number(page) === i} onClick={() => handlePageClick(i)}>
                    {i}
                </Pagination.Item>
            );
        });
    };

    // pageLimit과 일치하는 pageLimitOption 데이터 확인
    const getDefaultPageLimit = pageLimitOptions.includes(pageLimit)
        ? pageLimit
        : pageLimitOptions[0]; // 일치하는 데이터가 없으면 첫 번째 데이터 사용

    // n개씩 보기 렌더링
    const renderPageLimitOptions = () => {
        return (
            <>
                {pageLimitOptions.map((limit) => (
                        <option key={limit} value={limit}>
                            {`${limit}개씩 보기`}
                        </option>
                ))}
                {useShowAllData &&
                    <option key={1000} value={1000}>
                        {'전체보기 (최대 1000개)'}
                    </option>
                }
            </>
        )
    };

    const hasPrevGroup = startPage > 1;
    const hasNextGroup = endPage < totalPages;

    return (
        <div className="kw-list-foot mt-4">
            <div className="kw-list-foot-start">
                <h6>Result : {totalCount}건</h6>
            </div>
            <div className="kw-list-foot-center">
                <nav>
                    <Pagination>
                        {hasPrevGroup && (
                            <>
                                <Pagination.First
                                    className={"page-item-first"}
                                    onClick={() => handlePageClick(1)}
                                    disabled={Number(page) === 1}
                                />
                                <Pagination.Prev
                                    className={"page-item-prev"}
                                    onClick={() => handlePageClick(startPage - 1)}
                                    disabled={startPage === 1}
                                />
                            </>
                        )}
                        {renderPageButtonList()}
                        {hasNextGroup && (
                            <>
                                <Pagination.Next
                                    className={"page-item-next"}
                                    onClick={() => handlePageClick(endPage + 1)}
                                    disabled={endPage === totalPages}
                                />
                                <Pagination.Last
                                    className={"page-item-last"}
                                    onClick={() => handlePageClick(totalPages)}
                                    disabled={Number(page) === totalPages}
                                />
                            </>
                        )}
                    </Pagination>
                </nav>
            </div>
            <div className="kw-list-foot-end">
                {showPageLimitSelect && (
                    <div>
                        <Form.Select defaultValue={getDefaultPageLimit} onChange={handlePageLimitChange}>
                            {renderPageLimitOptions()}
                        </Form.Select>
                    </div>
                )}
            </div>
        </div>
    );
}

export default Paging;