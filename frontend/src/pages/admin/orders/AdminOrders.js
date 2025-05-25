import {Badge, Button, Card, Form, InputGroup, Stack} from "react-bootstrap";
import styles from "../products/AdminProducts.module.scss";
import FilterButtonGroup from "./FilterButtonGroup";
import FlexibleTable from "../../../shared/table/FlexibleTable";
import TableBackGroundCard from "../../../shared/TableBackGroundCard";
import {useNavigate} from "react-router-dom";
import {useEffect, useState} from "react";

const initData = [
    {orderId: 1, checked: false, status: 'BEFOREPAID'},
    {orderId: 2, checked: false, status: 'PAID'},
    {orderId: 3, checked: false, status: 'PAID'},
    {orderId: 4, checked: false, status: 'PAID'},
    {orderId: 5, checked: false, status: 'INDELIVERY'},
    {orderId: 6, checked: false, status: 'INDELIVERY'},
    {orderId: 7, checked: false, status: 'INDELIVERY'},
    {orderId: 8, checked: false, status: 'COMPLETED'},
    {orderId: 9, checked: false, status: 'CANCELED'},
];

const AdminOrders = () => {
    const navigate = useNavigate();
    const [data, setData] = useState(initData);
    const [checkedOrders, setCheckedOrders] = useState([]);
    const [activeFilter, setActiveFilter] = useState('ALL');
    const initColumns = [
        {
            id: "orderId",
            header: "주문 번호",
            size: 200,
            cell: ({ row }) => {
                return <Form className={"d-flex gap-2"}>
                    {(activeFilter === 'PAID' || activeFilter === 'INDELIVERY') ? <Form.Check
                        type={'checkbox'}
                        checked={row.original.checked}
                    /> : null}
                    ORD-2023122{row.original.orderId}
                </Form>
            }
        },
        {
            id: "orderPersonName",
            header: "주문자",
            cell: ({ row }) => (`구매자${row.index}`)
        },
        {
            id: "products",
            header: "상품",
            cell: () => ("프리미엄 블루투스 이어폰 외 1건")
        },
        {
            id: "price",
            header: "결제 금액",
            cell: () => ("₩47000")
        },
        {
            id: "orderDate",
            header: "주문 일시",
            cell: () => ("2025-12-19 17:00"),
        },
        {
            id: "status",
            header: "상태",
            cell: ({ row }) => {
                if (row.original.status === 'BEFOREPAID') {
                    return <Badge bg="" style={{ backgroundColor: "#E0E7FF", color: "#3730A3" }}>결제 대기중</Badge>
                } else if (row.original.status === 'PAID') {
                    return <Badge bg="" style={{ backgroundColor: "#DCFCE7", color: "#166534" }}>결제 완료</Badge>
                } else if (row.original.status === 'INDELIVERY') {
                    return <Badge bg="" style={{ backgroundColor: "#F3E8FF", color: "#6B21A8" }}>배송 중</Badge>
                } else if (row.original.status === 'COMPLETED') {
                    return <Badge bg="" style={{ backgroundColor: "#DFDFDF", color: "#000000" }}>배송 완료</Badge>
                } else if (row.original.status === 'CANCELED') {
                    return <Badge bg="" style={{ backgroundColor: "#FEE2E2", color: "#991B1B" }}>취소</Badge>
                }
            },
        },
        {
            id: "adminButton",
            header: "관리",
            cell: () => (<span className={"cursor-pointer"} onClick={() => navigate("1")}>상세</span>)
        },
    ];

    useEffect(() => {
        setCheckedOrders(data.filter(item => item.checked))
    }, [data])

    const handleRowClick = (row) => {
        if ((activeFilter === 'PAID' || activeFilter === 'INDELIVERY')) {
            const newData = data.map((item) => {
                if (item.orderId === row.original.orderId) {
                    return {...item, checked: !item.checked};
                } else {
                    return item;
                }
            })
            setData(newData);
        }
    }

    const handleClearChecked = () => {
        const newData = data.map((item) => {
            if (item.checked) {
                return {...item, checked: false};
            } else {
                return item;
            }
        })
        setData(newData);
        setCheckedOrders([]);
    }

    const handleFilterChange = (clickedFilter) => {
        setActiveFilter(clickedFilter.status);
        if (clickedFilter.status === 'ALL') {
            setData(initData);
        } else {
            setData(initData.filter(item => item.status === clickedFilter.status))
        }
    }

    const handleCheckedChangeButtonClick = () => {
        const newData = data.map(item => {
            const isChecked = checkedOrders.some(checked => checked.orderId === item.orderId);
            if (isChecked) {
                if (activeFilter === 'PAID') {
                    return { ...item, status: 'INDELIVERY' };
                } else {
                    return { ...item, status: 'COMPLETED' };
                }
            } else {
                return item;
            }
        });

        setData(newData);
    }

    return (
        <Card className={"px-10"}>
            <Card.Header className={"border-0"}>
                <Stack direction={"horizontal"} className={"justify-content-between"}>
                    <Stack>
                        <h4>주문 관리</h4>
                        <desc className={"text-gray-300"}>고객의 주문을 관리합니다.</desc>
                    </Stack>
                    <InputGroup className="me-3 gap-2" style={{ width: 360, height: 50 }}>
                        <Form.Control
                            type="text"
                            placeholder="주문번호 검색"
                        />
                        <Button className={styles.detailButton} variant="" type={"button"}>
                            검색
                        </Button>
                    </InputGroup>
                </Stack>
            </Card.Header>
            <Card.Body>
                <FilterButtonGroup activeFilter={activeFilter} handleChange={handleFilterChange} />
                {checkedOrders.length ? <Stack direction={"horizontal"} gap={3} className={"m-2 p-2"}
                        style={{background: "#F9FAFB"}}>
                    <span>{checkedOrders.length}개 선택됨</span>
                    <span className={"fw-bold cursor-pointer"} onClick={handleClearChecked}>선택 취소</span>
                    <Button variant={""} className={styles.detailButton} style={{ background: 'white' }}
                    onClick={handleCheckedChangeButtonClick}>
                        {activeFilter === 'PAID' ? '배송 중 처리' : '배송 완료 처리'}
                    </Button>
                </Stack> : null}
                <TableBackGroundCard>
                    <FlexibleTable initColumns={initColumns} data={data} rowProps={(row) => ({
                        onClick: () => handleRowClick(row),
                    })}/>
                </TableBackGroundCard>
            </Card.Body>
        </Card>
    );
}

export default AdminOrders;