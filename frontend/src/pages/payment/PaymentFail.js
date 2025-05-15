import {useSearchParams} from "react-router-dom";

const PaymentFail = () => {
    const [searchParams] = useSearchParams();
    const code = searchParams.get("code") || "UNKNOWN_ERROR";
    const message = decodeURIComponent(searchParams.get("message") || "결제가 실패했습니다.");

    return (
        <div className="text-center mt-20">
            <h1 className="text-2xl font-bold text-red-500">❌ 결제 실패</h1>
            <p className="mt-4 text-gray-600">{message}</p>
            <p className="mt-2 text-sm text-gray-400">({code})</p>
            <p className="mt-6 text-sm text-gray-500">
                잠시 후 다시 결제 페이지로 이동합니다...
            </p>
        </div>
    );
}

export default PaymentFail;