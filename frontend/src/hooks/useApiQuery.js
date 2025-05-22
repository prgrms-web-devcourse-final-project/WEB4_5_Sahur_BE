import { useQuery } from 'react-query';
import useConfirm from "./useConfirm.js";

/**
 * useApiQuery
 *
 * react-query의 useQuery를 래핑한 커스텀 훅입니다.
 * 반복되는 에러 처리, 기본 옵션 설정을 통일하여 일관된 API 호출 처리를 도와줍니다.
 *
 * @param {Array|string} queryKey - 쿼리 고유 식별 키 (['user', id] 형태 권장)
 * @param {Function} queryFn - 데이터를 fetch하는 함수 (Promise 반환)
 * @param {Object} options - useQuery의 기본 옵션과 사용자 지정 확장 옵션
 *
 * ✅ 기본 내장 기능:
 * - `retry: false`: 실패 시 자동 재시도 비활성화
 * - `keepPreviousData: true`: 키 변경 시 이전 데이터 유지 (페이징에 유용)
 * - `refetchOnWindowFocus: false`: 포커스 복귀 시 자동 refetch 비활성화
 * - `onError`: 오류 발생 시 confirm 모달 자동 출력
 *
 * @option {Function} [onError] - 사용자 정의 에러 처리 함수 (기본 핸들러 override 가능)
 *
 * @example
 * const { data, isLoading } = useApiQuery(['user', userId], () => fetchUser(userId));
 *
 * @example
 * useApiQuery(['userList'], fetchUsers, {
 *   onSuccess: (data) => {
 *     console.log("유저 목록 불러옴:", data);
 *   },
 *   onError: (err) => {
 *     toast.error("유저 목록 로딩 실패");
 *   }
 * });
 */

export function useApiQuery(queryKey, queryFn, options = {}) {
    const { openConfirm } = useConfirm();

    // 기본 에러 핸들러
    const defaultOnError = (error) => {
        console.error("useQuery Error :", error);
        openConfirm({
            title: '데이터를 불러오는 중 오류가 발생했습니다.',
            html: error.response?.data?.message || "에러: 관리자에게 문의바랍니다."
        });
    };

    return useQuery(queryKey, queryFn, {
        retry: false,
        keepPreviousData: true,
        refetchOnWindowFocus: false,
        ...options,
        onError: options.onError ?? defaultOnError,
    });
}
