import { useState } from 'react';
import { Button, InputGroup, FormControl } from 'react-bootstrap';
import styles from "./GroupBuy.module.scss"
import {normalizeNumber} from "../../utils/utils";

function Counter({ min = 1, max = 99, initial = 1, onChange }) {
    const [count, setCount] = useState(initial);

    const handleChange = (newCount) => {
        if (newCount === '') {
            setCount('');
        }
        newCount = normalizeNumber(newCount)
        if (newCount < min || newCount > max) return;
        setCount(newCount);
        onChange?.(newCount);
    };

    return (
        <InputGroup size="sm" style={{ display: 'flex', flexDirection: 'row' }}>
            <Button
                variant="outline-dark"
                onClick={() => handleChange(count - 1)}
                disabled={count <= min}
                className={styles.countButton}
                style={{ minWidth: '40px', height: '30px', color: 'black', borderColor: "#E2E8F0"}}
            >
                −
            </Button>
            <FormControl
                value={count}
                // readOnly
                onChange={(e) => handleChange(e.target.value)}
                className="text-center"
                style={{ width: '50px', height: '30px', fontWeight: '500' }}
            />
            <Button
                variant="outline-dark"
                onClick={() => handleChange(count + 1)}
                disabled={count >= max}
                className={styles.countButton}
                style={{ minWidth: '40px', height: '30px', color: 'black', borderColor: "#E2E8F0"}}
            >
                ＋
            </Button>
        </InputGroup>
    );
}

export default Counter;