import {useLocation, useSearchParams} from "react-router-dom";
import {useEffect} from "react";

const PaymentSuccess = () => {
    const [searchParams] = useSearchParams();

    useEffect(() => {
        const paymentKey = searchParams.get("paymentKey");
        const orderId = searchParams.get("orderId");
        const amount = searchParams.get("amount");
        console.log('결제 성공: ', paymentKey, orderId, amount);
        //여기에 백엔드 api 호출을 넣고 성공하면 결제 완료 페이지로 리다이렉트
    }, []);

    return (
        <div className="text-center mt-20">결제 완료 처리 중입니다...</div>
    );
}

export default PaymentSuccess;