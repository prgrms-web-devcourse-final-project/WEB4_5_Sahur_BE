import {Button, Card, Tab, Tabs} from "react-bootstrap";
import {useState} from "react";
import RequestCard from "./RequestCard";
import {useNavigate} from "react-router-dom";

const initData = [
    {productId: 1, status: 'waiting'},
    {productId: 2, status: 'rejected'},
    {productId: 3, status: 'approved'},
];

const MyPageRequests = () => {
    const [activeTab, setActiveTab] = useState('all');
    const navigate = useNavigate();

    const handleTabSelect = (tabKey) => {
        setActiveTab(tabKey);
    };

    return (
        <>
            <Button className={"ms-auto mb-2 me-2"} onClick={() => navigate('/mypage/request')}>상품 등록 요청</Button>
            <Card className={"me-2"}>
                <Card.Body>
                    <Tabs
                        activeKey={activeTab}
                        onSelect={handleTabSelect}
                        id="uncontrolled-tab-example"
                    >
                        <Tab eventKey="all" title="전체">
                            <div style={{ backgroundColor: '#F3F4F6' }} className={"p-3"}>승인 대기 상품</div>
                            <RequestCard item={initData[0]} />
                            <hr />
                            <RequestCard item={initData[0]} />
                            <div style={{ backgroundColor: '#F3F4F6' }} className={"p-3"}>승인된 상품</div>
                            <RequestCard item={initData[2]} />
                            <hr />
                            <RequestCard item={initData[2]} />
                            <div style={{ backgroundColor: '#F3F4F6' }} className={"p-3"}>거절된 상품</div>
                            <RequestCard item={initData[1]} />
                            <hr />
                            <RequestCard item={initData[1]} />
                        </Tab>
                        <Tab eventKey="waiting" title="승인대기" >
                            <div style={{ backgroundColor: '#F3F4F6' }} className={"p-3"}>승인 대기 상품</div>
                            <RequestCard item={initData[0]} />
                            <hr />
                            <RequestCard item={initData[0]} />
                            <hr />
                            <RequestCard item={initData[0]} />
                            <hr />
                            <RequestCard item={initData[0]} />
                            <hr />
                            <RequestCard item={initData[0]} />
                            <hr />
                            <RequestCard item={initData[0]} />
                            <hr />
                            <RequestCard item={initData[0]} />
                        </Tab>
                        <Tab eventKey="rejected" title="거절" >
                            <div style={{ backgroundColor: '#F3F4F6' }} className={"p-3"}>거절된 상품</div>
                            <RequestCard item={initData[1]} />
                            <hr />
                            <RequestCard item={initData[1]} />
                            <hr />
                            <RequestCard item={initData[1]} />
                            <hr />
                            <RequestCard item={initData[1]} />
                            <hr />
                            <RequestCard item={initData[1]} />
                            <hr />
                            <RequestCard item={initData[1]} />
                            <hr />
                            <RequestCard item={initData[1]} />
                        </Tab>
                        <Tab eventKey="approved" title="승인" >
                            <div style={{ backgroundColor: '#F3F4F6' }} className={"p-3"}>승인된 상품</div>
                            <RequestCard item={initData[2]} />
                            <hr />
                            <RequestCard item={initData[2]} />
                            <hr />
                            <RequestCard item={initData[2]} />
                            <hr />
                            <RequestCard item={initData[2]} />
                            <hr />
                            <RequestCard item={initData[2]} />
                            <hr />
                            <RequestCard item={initData[2]} />
                            <hr />
                            <RequestCard item={initData[2]} />
                        </Tab>
                    </Tabs>
                </Card.Body>
            </Card>
        </>

    );
}

export default MyPageRequests;