import {Button, FormControl, InputGroup} from 'react-bootstrap';
import styles from "./GroupBuy.module.scss"

function Counter({ count, handleChange, max }) {
    return (
        <InputGroup size="sm" style={{ display: 'flex', flexDirection: 'row' }}>
            <Button
                variant="outline-dark"
                onClick={() => handleChange(count - 1)}
                disabled={count <= 1}
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