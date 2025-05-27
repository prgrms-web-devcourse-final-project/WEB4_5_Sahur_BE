// 도커 컴포즈를 이용한 부하테스트 실행
// docker-compose run --rm k6
import http from 'k6/http';
import { check } from 'k6';

export const options = {
    scenarios: {
        scenarios_example: {
            executor: 'per-vu-iterations',
            vus: 100,
            iterations: 10,
            maxDuration: '30s',
        },
    },
};

export default function () {
    const keyword = '선반';
    const url = `https://api.devapi.store/api/v1/groupBuy/search?keyword=${encodeURIComponent(keyword)}`;
    const res = http.get(url);

    check(res, {
        'status is 200': (r) => r.status === 200,
        'response has expected structure': (r) => {
            const body = r.json();
            return body && Array.isArray(body.data);
        },
    });
}