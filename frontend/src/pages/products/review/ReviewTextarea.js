import React from 'react';
import { Form } from 'react-bootstrap';
import styles from "../GroupBuy.module.scss"

function ReviewTextarea({ value, onChange }) {
    return (
        <Form.Group className={"p-3"}>
            <Form.Control
                as="textarea"
                rows={4}
                placeholder="최소 10자 이상 작성해주세요"
                value={value}
                onChange={onChange}
                className={`${styles.reviewTextarea} h-25`}
            />
        </Form.Group>
    );
}

export default ReviewTextarea;
