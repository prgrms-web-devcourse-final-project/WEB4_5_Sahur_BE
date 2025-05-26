import { useState } from 'react';
import { Button } from 'react-bootstrap';
import styles from './AdminReviews.module.scss';

const buttonList = ['최신순', '오래된순', '별점순'];

function FilterButtonGroup() {
    const [active, setActive] = useState('최신순');

    return (
        <div className={styles.filterWrapper}>
            {buttonList.map(item => (
                <Button
                    variant="light"
                    className={`${styles.filterButton} ${active === item ? styles.active : ''}`}
                    onClick={() => setActive(item)}
                >
                    {item}
                </Button>
            ))}
        </div>
    );
}

export default FilterButtonGroup;
