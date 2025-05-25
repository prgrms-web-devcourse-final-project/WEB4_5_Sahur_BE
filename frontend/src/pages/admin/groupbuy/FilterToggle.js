import { useState } from 'react';
import { Button } from 'react-bootstrap';
import styles from './AdminGroupBuy.module.scss';

function FilterToggle() {
    const [active, setActive] = useState('전체');

    return (
        <div className={styles.filterWrapper}>
            <Button
                variant="light"
                className={`${styles.filterButton} ${active === '전체' ? styles.active : ''}`}
                onClick={() => setActive('전체')}
            >
                전체
            </Button>
            <Button
                variant="light"
                className={`${styles.filterButton} ${active === '모집 중' ? styles.active : ''}`}
                onClick={() => setActive('모집 중')}
            >
                모집 중
            </Button>
        </div>
    );
}

export default FilterToggle;
