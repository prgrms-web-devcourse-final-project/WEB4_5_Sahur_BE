// 도커 컴포즈를 이용한 부하테스트 실행
// docker-compose run --rm k6
import http from 'k6/http';
import { check } from 'k6';

export const options = {
    scenarios: {
        search_stress_test: {
            executor: 'per-vu-iterations',
            // Virtual Users (가상 사용자) 수
            vus: 100,
            // 각 가상 사용자가 몇 번 반복할지 설정
            iterations: 100,
            // 전체 시나리오가 30초를 초과하지 않도록 제한
            maxDuration: '30s',
        },
    },
};

export default function () {

    // 검색할 키워드 설정
    const keyword = '선반';

    // 테스트할 경로 선택 **서버에 부하테스트 할 경우 부하 설정을 너무 크게하지 말아주세요**
    // const url = `https://api.devapi.store/api/v1/groupBuy/search?keyword=${encodeURIComponent(keyword)}`;
    // const url = `http://host.docker.internal:8080/api/v1/groupBuy/search?keyword=${encodeURIComponent(keyword)}`;
    const url = `http://host.docker.internal:8080/api/v1/groupBuy/db-search?keyword=${encodeURIComponent(keyword)}`;

    const res = http.get(url);

    check(res, {
        'status is 200': (r) => r.status === 200,
        'response has expected structure': (r) => {
            const body = r.json();
            return body && Array.isArray(body.data);
        },
    });
}