import {Navigate, Route, Routes} from "react-router-dom";
import Login from "../pages/login/Login";
import Error404 from "../pages/error/Error404";
import Signup from "../pages/login/Signup";
import Main from "../pages/main/Main";
import PasswordReset from "../pages/login/PasswordReset";
import GroupBuy from "../pages/products/GroupBuy";
import Payment from "../pages/payment/Payment";
import PaymentSuccess from "../pages/payment/PaymentSuccess";
import PaymentFail from "../pages/payment/PaymentFail";
import AdminDashboard from "../pages/admin/dashboard/AdminDashboard";
import MypageDashboard from "../pages/mypage/MypageDashboard";
import AdminLayout from "../layout/admin/AdminLayout";
import Layout from "../layout/main/Layout";
import AdminProducts from "../pages/admin/products/AdminProducts";
import AdminGroupBuy from "../pages/admin/groupbuy/AdminGroupBuy";
import AdminOrders from "../pages/admin/orders/AdminOrders";
import AdminReviews from "../pages/admin/review/AdminReviews";
import AdminProductsDetail from "../pages/admin/products/AdminProductsDetail";
import AdminProductsRequestDetail
    from "../pages/admin/products/AdminProductsRequestDetail";

const BaseRoutes = () => {
    return (
        <Routes>
            <Route exact path="/login" element={<Login />} />
            <Route exact path="/signup" element={<Signup />} />
            <Route exact path="/password-reset" element={<PasswordReset />} />
            <Route path="/" element={<Layout />}>
                <Route index element={<Navigate to="main" replace />} />
                <Route path="main" element={<Main />} />
                <Route path="groupBuy/:groupBuyId" element={<GroupBuy />} />
                <Route path="groupBuy/:groupBuyId/payment" element={<Payment />} />
                <Route path="payment/success" element={<PaymentSuccess />} />
                <Route path="payment/fail" element={<PaymentFail />} />
            </Route>
            <Route path="/admin" element={<AdminLayout />}>
                <Route index element={<Navigate to="dashboard" replace />} />
                <Route path="dashboard" element={<AdminDashboard />} />
                <Route path="products" element={<AdminProducts />} />
                <Route path="productsRequests" element={<AdminProducts />} />
                <Route path="products/:productId" element={<AdminProductsDetail />} />
                <Route path="productsRequests/:productId" element={<AdminProductsRequestDetail />} />
                <Route path="groupBuy" element={<AdminGroupBuy />} />
                <Route path="orders" element={<AdminOrders />} />
                <Route path="reviews" element={<AdminReviews />} />
            </Route>
            <Route path="/mypage" element={<Layout />}>
                <Route index element={<Navigate to="dashboard" replace />} />
                <Route path="dashboard" element={<MypageDashboard />} />
            </Route>
            {/* 에러페이지 */}
            <Route path={"*"} element={<Error404 />} />
        </Routes>
    );
}

export default BaseRoutes;