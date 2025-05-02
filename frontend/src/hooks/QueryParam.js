import {useNavigate, useSearchParams} from 'react-router-dom';
import {isEmptyOrNull} from "../utils/utils";

export const useQueryParam = () => {
    const [searchParams, setSearchParams] = useSearchParams();
    const navigate = useNavigate();

    // URL 쿼리스트링에서 특정 값 또는 모든 값을 가져오는 함수
    const getQueryParam = () => {
        const params = {};
        // 모든 쿼리 파라미터를 순회하며 객체로 변환
        for (let [key, value] of searchParams) {
            // 각 값에 대해 디코딩을 적용
            if (params[key]) {
                if (Array.isArray(params[key])) {
                    params[key].push(decodeURIComponent(value));
                } else {
                    params[key] = [params[key], decodeURIComponent(value)];
                }
            } else {
                params[key] = decodeURIComponent(value);
            }
        }
        return params;
    };

    // URL 쿼리스트링에 값을 설정하는 함수 (하나 또는 여러 개)
    // options.replace - 브라우저에 히스토리 스택을 쌓지 않게 하는 옵션. 기본값 false
    const setQueryParam = (params, options = { replace: false }) => {
        if (isEmptyOrNull(params)) { //params가 없는 경우 쿼리스트링을 reset한다.
            if (options.replace) {
                navigate(window.location.pathname, { replace: true });
            } else {
                setSearchParams();
            }
        } else { //params가 있는경우 쿼리스트링을 변화시킨다.
            const newSearchParams = new URLSearchParams();

            // 새로운 파라미터들을 추가/업데이트
            for (const [key, value] of Object.entries(params)) {
                if (Array.isArray(value)) {
                    value.forEach(v => {
                        newSearchParams.append(key, encodeURIComponent(v.toString()));
                    });
                } else if (value == null) { // null 또는 undefined 값을 체크
                    newSearchParams.delete(key); // 해당 키 삭제
                } else {
                    newSearchParams.set(key, encodeURIComponent(value.toString())); // 값을 설정
                }
            }

            // replace 옵션이 트루일 경우 navigate의 replace 옵션을 통해 브라우저 히스토리 스택을 쌓지 않음
            const newUrl = `${window.location.pathname}?${newSearchParams.toString()}`;
            if (options.replace) {
                navigate(newUrl, { replace: true });
            } else {
                setSearchParams(newSearchParams);
            }
        }
    };

    return [getQueryParam(), setQueryParam];
};
