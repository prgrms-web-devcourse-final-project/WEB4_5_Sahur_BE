import { Button } from 'react-bootstrap';
import styles from './AdminOrders.module.scss';

const filterList = [
    {status: 'ALL', button: '전체'},
    {status: 'PAID', button: '결제 완료'},
    {status: 'INDELIVERY', button: '배송 중'},
    {status: 'COMPLETED', button: '배송 완료'},
    {status: 'CANCELED', button: '취소'}
];

function FilterButtonGroup({ activeFilter, handleChange }) {

    return (
        <div className={styles.filterWrapper}>
            {filterList.map(item => (
                <Button key={item.status}
                    variant="light"
                    className={`${styles.filterButton} ${activeFilter === item.status ? styles.active : ''}`}
                    onClick={() => handleChange(item)}
                >
                    {item.button}
                </Button>
            ))}
        </div>
    );
}

export default FilterButtonGroup;
