import {Navigate, Route, Routes} from "react-router-dom";
import Login from "../pages/login/Login";
import Error404 from "../pages/error/Error404";
import Signup from "../pages/login/Signup";
import Main from "../pages/main/Main";
import Layout from "../layout/Layout";
import PasswordReset from "../pages/login/PasswordReset";
import GroupBuy from "../pages/products/GroupBuy";
import Payment from "../pages/payment/Payment";

const BaseRoutes = () => {
    return (
        <Routes>
            <Route exact path="/login" element={<Login />} />
            <Route exact path="/signup" element={<Signup />} />
            <Route exact path="/password-reset" element={<PasswordReset />} />
            <Route path="/" element={<Layout />} >
                {/* 메인 도메인만 입력 시 main으로 이동*/}
                <Route index element={<Navigate to="/main" replace />} />
                <Route path="/main" element={<Main />} />
                <Route path="/groupBuy/:groupBuyId" element={<GroupBuy />} />
                <Route path="/groupBuy/:groupBuyId/payment" element={<Payment />} />
            </Route>
            {/* 에러페이지 */}
            <Route path={"*"} element={<Error404 />} />
        </Routes>
    );
}

export default BaseRoutes;