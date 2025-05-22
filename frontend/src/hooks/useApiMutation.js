import {useMutation, useQueryClient} from 'react-query';
import useConfirm from "./useConfirm.js";

/**
 * useApiMutation
 *
 * react-query의 useMutation을 래핑한 커스텀 훅입니다.
 * 공통 에러 처리와 성공 후 query invalidate 기능을 포함하고 있습니다.
 *
 * @param {Function} mutationFn - axios 등 실제 요청 함수 (예: () => axios.post(...))
 * @param {Object} options - useMutation의 옵션과 확장 옵션을 포함합니다.
 *
 * ✅ 기본 지원 기능:
 * - mutation 실패 시 useConfirm을 통한 에러 메시지 처리
 * - mutation 성공 시 사용자 정의 onSuccess 콜백 실행
 * - `invalidateKeys` 옵션을 통해 관련 useQuery 캐시 자동 무효화
 *
 * @option {Array<Array>} [invalidateKeys]
 *   - 무효화할 query key들을 담은 배열의 배열입니다.
 *   - 쿼리 키는 react-query에서 흔히 사용하는 `[key, id]` 형태이며,
 *     여러 개를 전달할 수 있기 때문에 이중 배열 구조를 사용합니다.
 *
 *   ✅ 옳은 예: invalidateKeys: [['fetchCase', id], ['userProfile', userId]]
 *   ❌ 잘못된 예: invalidateKeys: ['fetchCase', id] ← 이건 단일 키로 오인됨
 *
 *   👉 왜 invalidateKeys가 이중 배열인가?
 *   → react-query의 `invalidateQueries()`는 하나의 queryKey만 처리합니다.
 *     따라서 여러 쿼리를 무효화하려면 각각의 queryKey를 배열로 전달해야 하므로
 *     `Array<Array>` 구조를 사용합니다.
 *     예를 들어 여러 키를 한번에 무효화 해야하는 경우 아래와 같이 파라미터를 줄 수 있습니다.
 *     invalidateKeys: [
 *         ['fetchCase', id],
 *         ['userList'],
 *         ['notifications']
 *     ]
 *
 * @example
 * const { mutate } = useApiMutation(updatePost, {
 *   invalidateKeys: [['fetchPost', postId]],
 *   onSuccess: () => {
 *     showToast('수정 완료');
 *     navigate('/post');
 *   }
 * });
 */

export function useApiMutation(mutationFn, options = {}) {
    const { openConfirm } = useConfirm();
    const queryClient = useQueryClient();

    const defaultOnError = (error) => {
        console.error("useMutation Error :", error);
        openConfirm({
            title: '데이터 처리중 오류가 발생했습니다.',
            html: error.response?.data?.message || "에러: 관리자에게 문의바랍니다."
        });
    };

    const wrappedOnSuccess = async (...args) => {
        // 사용자 정의 onSuccess 먼저 실행
        if (options.onSuccess) {
            await options.onSuccess(...args);
        }

        // 그 후 invalidate 처리
        if (options.invalidateKeys && Array.isArray(options.invalidateKeys)) {
            options.invalidateKeys.forEach((key) => {
                queryClient.invalidateQueries(key);
            });
        }
    };

    return useMutation(mutationFn, {
        retry: false,
        ...options,
        onError: options.onError ?? defaultOnError,
        onSuccess: wrappedOnSuccess,
    });
}
