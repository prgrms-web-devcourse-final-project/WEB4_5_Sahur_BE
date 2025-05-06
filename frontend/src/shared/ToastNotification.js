import { Toast, ToastContainer } from "react-bootstrap";
import { useRecoilState } from "recoil";
import { toastAtom } from "../state/atoms";

const ToastNotification = () => {
    const [toast, setToast] = useRecoilState(toastAtom);

    // Toast 닫기 함수
    const closeToast = () => {
        setToast({ ...toast, isOpen: false });
    };

    // type 값에 따라 Bootstrap 배경 클래스 매핑
    const getToastBg = (type) => {
        switch (type) {
            case "success":
                return "primary";
            case "error":
                return "danger";
            case "warning":
                return "warning";
            case "info":
                return "info"; 
            default:
                return "primary"; 
        }
    };

    return (
        <ToastContainer
            position="top-end"
            className="p-3"
            style={{
                zIndex: 1111,
                position: "fixed"
            }}
        >
            <Toast show={toast.isOpen} onClose={closeToast}
                autohide={true}
                delay={4000}
                bg={getToastBg(toast.type)}
            >
                <Toast.Body className={'text-white'}>
                    {toast.message}
                </Toast.Body>
            </Toast>
        </ToastContainer>
    )
}

export default ToastNotification;