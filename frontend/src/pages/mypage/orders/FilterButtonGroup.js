import {Button, Stack} from 'react-bootstrap';
import styles from './MyPageOrders.module.scss';

const filterList = [
    {status: 'ALL', button: '전체'},
    {status: 'IN_PROGRESS', button: '진행 중'},
    {status: 'DONE', button: '완료'},
    {status: 'CANCELED', button: '취소'},
];

function FilterButtonGroup({ activeFilter, handleChange }) {

    return (
        <Stack direction={"horizontal"} className={`m-2 rounded p-2`} style={{ backgroundColor: "#f1f4f8" }}>
            {filterList.map(item => (
                <Button key={item.status}
                    variant="light"
                    className={`${styles.filterButton} flex-fill ${activeFilter === item.status ? styles.active : ''}`}
                    onClick={() => handleChange(item)}
                >
                    {item.button}
                </Button>
            ))}
        </Stack>
    );
}

export default FilterButtonGroup;
