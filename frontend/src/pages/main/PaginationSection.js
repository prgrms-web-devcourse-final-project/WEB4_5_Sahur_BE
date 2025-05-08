import {Pagination} from "react-bootstrap";
import {useState} from "react";
import {ReactComponent as ArrowRightIcon} from "../../assets/images/icon/arrow-right.svg"
import {ReactComponent as ArrowLeftIcon} from "../../assets/images/icon/arrow-left-black.svg"

const PaginationSection = () => {
    const [currentPage, setCurrentPage] = useState(1);
    const totalPages = 9;

    const handleClick = (page) => {
        setCurrentPage(page);
    };

    const items = [];

    // 페이지 번호
    for (let page = 1; page <= totalPages; page++) {
        items.push(
            <Pagination.Item
                key={page}
                active={page === currentPage}
                onClick={() => handleClick(page)}
                linkClassName={page === currentPage && "text-white"}
            >
                {page}
            </Pagination.Item>
        );
    }
    return (
        <Pagination className={"d-flex justify-content-center m-3"}>
            <Pagination.Prev onClick={() => handleClick(currentPage - 1)} linkClassName={"text-black"}>
                <ArrowLeftIcon width={18} height={18} /> Prev
            </Pagination.Prev>
            {items}
            <Pagination.Next onClick={() => handleClick(currentPage + 1)} linkClassName={"text-black"}>
                Next <ArrowRightIcon width={18} height={18} />
            </Pagination.Next>
        </Pagination>
    );
}

export default PaginationSection;