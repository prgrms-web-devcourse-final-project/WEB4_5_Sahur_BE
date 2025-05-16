// 도커 컴포즈를 이용한 부하테스트 실행
// docker-compose run k6 run /scripts/k6-test.js

import http from 'k6/http';
import { check } from 'k6';

export const options = {
    scenarios: {
        scenarios_example: {
            executor: "per-vu-iterations",
            vus: 100,
            iterations: 10,
            maxDuration: "30s",
        },
    },
};

export default function () {
    const res = http.get('http://host.docker.internal:8080/api/v1/categories');

    check(res, {
        'status is 200': (r) => r.status === 200,
    });
}