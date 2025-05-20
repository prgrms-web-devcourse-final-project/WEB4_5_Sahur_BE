import {Button, Col, Row, Stack} from "react-bootstrap";
import styles from "./GroupBuy.module.scss"
import GroupBuyCard from "../main/GroupBuyCard";
import {useState} from "react";
import ProductDescription from "./ProductDescription";
import clsx from "clsx";
import ProductWrapper from "./ProductWrapper";
import ProductImageSection from "./ProductImageSection";
import ProductBuySection from "./ProductBuySection";
import axios from "axios";
import {useParams} from "react-router-dom";
import {useQuery} from "react-query";
import useConfirm from "../../hooks/useConfirm";
import ProductReviewList from "./review/ProductReviewList";

const fetchGroupBuyById = async (groupBuyId) => {
    const response = await axios.get(`/api/v1/groupBuy/${groupBuyId}`);
    return response.data.data;
}

const fetchRelatedGroupBuy = async (groupBuyId) => {
    const response = await axios.get(`/api/v1/groupBuy/${groupBuyId}/related`);
    return response.data.data;
}

const GroupBuy = () => {
    const { groupBuyId } = useParams();
    const {openConfirm} = useConfirm();
    const [currentView, setCurrentView] = useState('description'); //description, review 둘 중 하나

    const { data: groupBuyInfo, isLoading: isLoadingGroupBuy } = useQuery(
        ['GroupBuy', groupBuyId],
        () => fetchGroupBuyById(groupBuyId),
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
        }
    );

    const { data: relatedGroupBuyInfo, isLoading: isLoadingRelatedGroupBuy } = useQuery(['RelatedGroupBuy', groupBuyId],
        () => fetchRelatedGroupBuy(groupBuyId),
        {
            keepPreviousData: true,
            refetchOnWindowFocus: false,
            onError: (e) => {
                console.log("error fetchDataRoom: ", e);
                openConfirm({
                    title: "데이터를 불러오는 중 오류가 발생했습니다.",
                    html: e.response?.data?.message || "에러: 관리자에게 문의바랍니다."
                });
            },
        }
    );

    return (
        <ProductWrapper>
            <Row className={styles.sectionDivider}>
                <Col md={6}>
                    <ProductImageSection imageUrlList={groupBuyInfo?.product.imageUrl}/>
                </Col>
                <Col md={6}>
                    <ProductBuySection groupBuyInfo={groupBuyInfo}/>
                </Col>
            </Row>{/* 상품 이미지 및 구매 섹션 로우 끝 */}
            <Row>
                <h4>진행중인 다른 공동구매</h4>
                <Row>
                    {relatedGroupBuyInfo?.map((item, index) => (
                        <Col md={4} key={index}>
                            <GroupBuyCard product={item}/>
                        </Col>
                    ))}
                </Row>
                <Row className={"mt-10"}>
                    <Col md={12}>
                        <Stack direction={"horizontal"} className="justify-content-center align-items-center p-2" style={{ background: "var(--BackGround, #F1F5F9)" }}>
                            <Button variant={""} className={clsx(styles.view, currentView === 'description' && styles.active)} onClick={() => setCurrentView('description')}>상품상세</Button>
                            <Button variant={""} className={clsx(styles.view, currentView === 'review' && styles.active)} onClick={() => setCurrentView('review')}>리뷰 ({groupBuyInfo?.reviewCount})</Button>
                        </Stack>
                    </Col>
                </Row> {/* 상품상세, 리뷰 버튼 로우 끝 */}
                {currentView === 'description' ? <ProductDescription product={groupBuyInfo?.product} /> : <ProductReviewList />}
            </Row>
        </ProductWrapper>
    );
}

export default GroupBuy;