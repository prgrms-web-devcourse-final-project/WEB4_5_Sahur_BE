import { useSetRecoilState } from 'recoil';
import {toastAtom} from "../state/atoms";

export const useToast = () => {
    const setToast = useSetRecoilState(toastAtom);

    return ( message, type) => {
        setToast({ isOpen: true,  message, type });
    };
};