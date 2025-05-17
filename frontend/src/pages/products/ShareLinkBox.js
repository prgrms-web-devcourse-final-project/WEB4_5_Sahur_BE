import React, {useState} from 'react';
import { InputGroup, FormControl, Button } from 'react-bootstrap';

function ShareLinkBox() {
    const url = window.location.href;
    const [copied, setCopied] = useState(false);

    const handleCopy = () => {
        navigator.clipboard.writeText(url);
        setCopied(true);
        setTimeout(() => setCopied(false), 2000); // 2초 후 사라짐
    };

    return (
        <div className={" w-75"}>
        <InputGroup className="rounded-pill border shadow-sm" style={{ overflow: 'hidden' }}>
            <FormControl
                readOnly
                value={url}
                style={{
                    border: 'none',
                    backgroundColor: '#f8f8f8',
                    fontSize: '14px',
                }}
            />
            <Button
                variant="light"
                onClick={handleCopy}
                style={{
                    border: 'none',
                    color: 'blueviolet',
                    fontWeight: 'bold',
                    borderRadius: 0, // 오른쪽 끝만 살림
                    fontSize: '14px',
                    minWidth: '20px'
                }}
            >
                복사
            </Button>
        </InputGroup>
            {copied && (
                <div style={{ marginTop: '8px', fontSize: '13px', color: 'blueviolet' }}>
                    링크가 복사되었습니다.
                </div>
            )}
        </div>
    );
}

export default ShareLinkBox;
