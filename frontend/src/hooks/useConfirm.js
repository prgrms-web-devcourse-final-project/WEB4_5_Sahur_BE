import {useSetRecoilState} from "recoil";
import {confirmAtom} from "../state/atoms";

const useConfirm = () => {
    const setRecoilState = useSetRecoilState(confirmAtom);

    const openConfirm = ({ title, html, icon, confirmButtonText, callback, showCancelButton = true, onCancel }) => {
        setRecoilState({
            isOpen: true
            , title
            , html: html || ''
            , icon: icon || 'exclamation-circle'
            , confirmButtonText: confirmButtonText || '확인'
            , callback //callback을 파라미터로 안받으면 단순 정보제공 Alert로 인식하여 확인버튼만 있는 모달이 뜸
            , showCancelButton: showCancelButton //callback이 정의되어 있지만 취소 버튼이 필요 없는경우 false로 하면 취소버튼이 안보인다.
            , onCancel
        });
    };

    return { openConfirm };
}

export default useConfirm;