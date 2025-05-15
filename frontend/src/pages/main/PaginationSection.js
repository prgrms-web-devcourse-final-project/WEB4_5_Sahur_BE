import {Pagination} from "react-bootstrap";
import {useState} from "react";
import {ReactComponent as ArrowRightIcon} from "../../assets/images/icon/arrow-right.svg"
import {ReactComponent as ArrowLeftIcon} from "../../assets/images/icon/arrow-left-black.svg"

const PaginationSection = ({currentPage, totalPages, handlePageClick}) => {
    const items = [];

    // 페이지 번호
    for (let page = 1; page <= totalPages; page++) {
        items.push(
            <Pagination.Item
                key={page}
                active={page === currentPage}
                onClick={() => handlePageClick(page)}
                linkClassName={page === currentPage && "text-white"}
            >
                {page}
            </Pagination.Item>
        );
    }
    return (
        <Pagination className={"d-flex justify-content-center m-3"}>
            {currentPage > 1 && <Pagination.Prev onClick={() => handlePageClick(currentPage - 1)} linkClassName={"text-black"}>
                <ArrowLeftIcon width={18} height={18} /> Prev
            </Pagination.Prev>}
            {items}
            {currentPage < totalPages && <Pagination.Next onClick={() => handlePageClick(currentPage + 1)} linkClassName={"text-black"}>
                Next <ArrowRightIcon width={18} height={18} />
            </Pagination.Next>}
        </Pagination>
    );
}

export default PaginationSection;