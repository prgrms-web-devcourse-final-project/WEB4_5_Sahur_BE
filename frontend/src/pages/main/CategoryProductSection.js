import {Button, Card, Col, Pagination, Row, Stack} from "react-bootstrap";
import clsx from "clsx";
import styles from "./Main.module.scss";
import ProductCard from "./GroupBuyCard";
import {useState} from "react";
import PaginationSection from "./PaginationSection";
import axios from "axios";
import {buildQuery} from "../../utils/utils";
import {useQuery} from "react-query";
import useConfirm from "../../hooks/useConfirm";
import Spinner from "../../shared/Spinner";

const sortOptions = [
    { name: '최신순', value: 'LATEST' },
    { name: '인기순', value: 'POPULAR' },
    { name: '마감임박순', value: 'DEADLINE_SOON' }
];

const fetchGroupBuyListOngoing = async (params) => {
    const response = await axios.get(`/api/v1/groupBuy/list/onGoing${buildQuery(params)}`);
    console.log(response.data)
    return response.data.data;
}

const CategoryProductSection = () => {
    const [sortField, setSortField] = useState('LATEST');
    const {openConfirm} = useConfirm();
    const [page, setPage] = useState(0);
    const { isLoading, isFetching, data } = useQuery(['CategoryProductSection', page, sortField]
        ,() => fetchGroupBuyListOngoing({ page, sortField }),
        {
            keepPreviousData: true,
            refetchOnWindowFocus: false,
            onError: (e) => {
                console.log("error fetchDataRoom: ", e);
                openConfirm({
                    title: "데이터를 불러오는 중 오류가 발생했습니다.",
                    html: e.response?.data?.message || "에러: 관리자에게 문의바랍니다."
                });
            }
        });

    const handlePageClick = (page) => {
        setPage(page -1);
    }
    return (
        <Card className={"mt-3 p-2"}>
            <Card.Body className={"p-2"}>
                <Stack direction={"horizontal"} className={"d-flex justify-content-between"}>
                    <h4>반려동물 공동 구매</h4>
                    <Stack direction={"horizontal"} gap={3}>
                        {sortOptions.map((item) => (
                            <Button key={item.value}
                                    bsPrefix={clsx(styles.sortButton, {[styles.active]: sortField === item.value})}
                                    onClick={() => setSortField(item.value)}
                            >
                                {item.name}
                            </Button>
                        ))}
                    </Stack>
                </Stack>
                <>
                    {data?.content.map((item, index) => (
                        index % 4 === 0 && (
                            <Row className="mt-3" key={index}>
                                {data?.content.slice(index, index + 4).map((item, i) => (
                                    <Col md={3} key={i}>
                                        <ProductCard product={item}/>
                                    </Col>
                                ))}
                            </Row>
                        )
                    ))}
                </>
                <PaginationSection currentPage={page + 1}
                                   totalPages={data?.totalPages}
                                   handlePageClick={handlePageClick} />
            </Card.Body>
            <Spinner show={isLoading || isFetching} />
        </Card>
    );
}

export default CategoryProductSection;