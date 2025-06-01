import {Card, Stack} from "react-bootstrap";
import SearchBox from "./SearchBox";
import PaymentList from "./PaymentList";
import OrderInfo from "./OrderInfo";
import PaymentDetail from "./PaymentDetail";
import styles from "./MyPagePayments.module.scss"
import {useState} from "react";

const MyPagePayments = () => {
    const [open, setOpen] = useState(false);
    const [selectedItem, setSelectedItem] = useState();
    const handleOpenClick = (item) => {
        setOpen(prev => !prev);
        setSelectedItem(item);
    }
    return (
        <Card className={"p-4"}>
            <Card.Body>
                <Stack direction={"horizontal"} gap={5}>
                    <Stack gap={2} className={`${styles.main} ${open ? styles.mainShrink : ''}`}>
                        <SearchBox />
                        <PaymentList handleOpenClick={handleOpenClick}/>
                    </Stack>
                    {open && <Stack className={styles.sidebar} style={{ background: "#fff" }}>
                        <OrderInfo selectedItem={selectedItem} />
                        <PaymentDetail selectedItem={selectedItem}/>
                    </Stack>}
                </Stack>
            </Card.Body>
        </Card>
    )
};

export default MyPagePayments;