import {Card, Col, Row} from "react-bootstrap";
import ProductCard from "./GroupBuyCard";
import axios from "axios";
import {buildQuery} from "../../utils/utils";
import {useQuery} from "react-query";
import useConfirm from "../../hooks/useConfirm";

const fetchGroupBuyPopular = async () => {
    const response = await axios.get(`/api/v1/groupBuy/popular`);
    console.log(response.data)
    return response.data.data;
}

const PopularProductSection = () => {
    const {openConfirm} = useConfirm();
    const { isLoading, isFetching, data } = useQuery(['PopularProductSection']
        ,() => fetchGroupBuyPopular(),
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

    return (
        <Card className={"p-2"}>
            <Card.Body>
                <h4>실시간 인기 공동구매</h4>
                <Row>
                    <Col md={4}>
                        <ProductCard product={data?.[0]}/>
                    </Col>
                    <Col md={4}>
                        <ProductCard product={data?.[1]} />
                    </Col>
                    <Col md={4}>
                        <ProductCard product={data?.[2]} />
                    </Col>
                </Row>
            </Card.Body>
        </Card>
    );
}

export default PopularProductSection;