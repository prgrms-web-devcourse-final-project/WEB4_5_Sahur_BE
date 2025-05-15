import { useEffect, useState } from 'react';

export default function DeadlineTimer({ deadline }) {
    const [remaining, setRemaining] = useState(getRemaining(deadline));

    useEffect(() => {
        const timer = setInterval(() => {
            setRemaining(getRemaining(deadline));
        }, 1000);

        return () => clearInterval(timer); // 컴포넌트 언마운트 시 정리
    }, [deadline]);

    return (
        <span>{remaining} 남음</span>
    );
}

function getRemaining(deadlineStr) {
    const deadline = new Date(deadlineStr);
    const now = new Date();
    const diffMs = deadline - now;

    if (diffMs <= 0) return "마감";

    const diffSec = Math.floor(diffMs / 1000);
    const days = Math.floor(diffSec / 86400);
    const hours = Math.floor((diffSec % 86400) / 3600);
    const minutes = Math.floor((diffSec % 3600) / 60);
    const seconds = diffSec % 60;

    if (days > 0) return `${days}일`;
    if (hours > 0) return `${pad(hours)}시간`;
    if (minutes > 0) return `${pad(minutes)}분`;
    return `${pad(seconds)}초`;
}

function pad(n) {
    return n.toString().padStart(2, '0');
}
