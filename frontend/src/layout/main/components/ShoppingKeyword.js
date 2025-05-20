import {
    Accordion, AccordionContext,
    Card,
    ListGroup,
    Stack,
    useAccordionButton
} from "react-bootstrap";
import style from "../Header.module.scss";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {faMinus, faXmark} from "@fortawesome/free-solid-svg-icons";
import {useContext, useEffect, useState} from "react";
import clsx from "clsx";

function CustomToggle({ eventKey, keywordList }) {
    const decoratedOnClick = useAccordionButton(eventKey);
    // 현재 열린 아코디언 키를 가져옴
    const currentEventKey = useContext(AccordionContext);
    const [index, setIndex] = useState(0);

    useEffect(() => {
        const timer = setInterval(() => {
            setIndex((prev) => (prev + 1) % keywordList.length);
        }, 5000); // 5초마다 변경

        return () => clearInterval(timer); // 컴포넌트 unmount 시 정리
    }, []);
    return (
        <Stack direction={"horizontal"} className={style.shoppingKeywordText} onClick={decoratedOnClick}>
            <span style={{color: "#A855F7", fontWeight: "900", marginRight: "10px"}}>{keywordList[index].order}.</span>
            {keywordList[index].keyword}
            <span className={clsx("ms-10", currentEventKey.activeEventKey === null ? "ico-collaps-down" : "ico-collaps-up")} />
        </Stack>
    );
}

const ShoppingKeyword = () => {
    const [activeKey, setActiveKey] = useState(null);
    const keywordList = [
        {order: 1, keyword: '소비기한임박', status: 'keep'},
        {order: 2, keyword: '르무통운동화', status: 'keep'},
        {order: 3, keyword: '짜먹는 설빙', status: 'keep'},
        {order: 4, keyword: '월드콘', status: 'keep'},
        {order: 5, keyword: '알파CD', status: 'keep'},
        {order: 6, keyword: '트레킹화', status: 'up'},
        {order: 7, keyword: '핸드폰케이스', status: 'up'},
        {order: 8, keyword: '고시히카리 10KG', status: 'down'},
        {order: 9, keyword: '엘르선글라스', status: 'up'},
        {order: 10, keyword: '연세우유', status: 'down'},
    ];

    const statusIcon = (status) => {
        if (status === 'up') {
            return <span className={"ms-auto ico-arrow-up-primary"} />
        } else if (status === 'down') {
            return <span className={"ms-auto ico-arrow-down-primary"} />
        }
        return <span className={"ms-auto me-1"}>
            <FontAwesomeIcon icon={faMinus} />
        </span>
    }
    return (
        <div style={{ position: "relative"}}>
            <Accordion activeKey={activeKey} onSelect={(key) => setActiveKey(key)}  style={{ width: "350px" }}>
                <Card style={{ boxShadow: 'none' }}>
                    <Card.Header style={{ border: 'none' }}>
                        <CustomToggle eventKey={"0"} keywordList={keywordList} />
                    </Card.Header>
                </Card>
                <Accordion.Collapse eventKey="0">
                    <Card.Body className={style.floatingBody}>
                        <Stack direction={"horizontal"}>
                            <span className={style.keywordBold}>실시간 쇼핑 검색어</span>
                            <small className={"ms-3"}>2025.04.25 12:20 기준</small>
                            <span className={"ms-auto cursor-pointer"} onClick={() => setActiveKey(null)}>
                                <FontAwesomeIcon icon={faXmark}></FontAwesomeIcon>
                            </span>
                        </Stack>
                        <ListGroup>
                            <ListGroup.Item className="border-0">
                                {keywordList.map((item) => {
                                    return <Stack key={item.order} className={"p-1"} direction={"horizontal"}>
                                        <span style={{ color: 'red', fontWeight: 700 }}>{item.order}</span>
                                        <span className={"ms-2"}>{item.keyword}</span>
                                        {statusIcon(item.status)}
                                    </Stack>
                                })}
                            </ListGroup.Item>
                        </ListGroup>
                    </Card.Body>
                </Accordion.Collapse>
            </Accordion>
        </div>
    );
}

export default ShoppingKeyword;