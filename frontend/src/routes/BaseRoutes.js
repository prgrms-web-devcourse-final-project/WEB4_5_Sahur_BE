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
import AdminLayout from "../layout/admin/AdminLayout";
import Layout from "../layout/main/Layout";
import AdminProducts from "../pages/admin/products/AdminProducts";
import AdminGroupBuy from "../pages/admin/groupbuy/AdminGroupBuy";
import AdminOrders from "../pages/admin/orders/AdminOrders";
import AdminReviews from "../pages/admin/review/AdminReviews";
import AdminProductsDetail from "../pages/admin/products/AdminProductsDetail";
import AdminProductsRequestDetail
    from "../pages/admin/products/AdminProductsRequestDetail";
import AdminGroupBuyDetail from "../pages/admin/groupbuy/AdminGroupBuyDetail";
import AdminOrdersDetail from "../pages/admin/orders/AdminOrdersDetail";
import MyPageLayout from "../layout/mypage/MyPageLayout";
import MyPageDashboard from "../pages/mypage/dashboard/MyPageDashboard";
import MyPageOrders from "../pages/mypage/orders/MyPageOrders";
import MyPagePayments from "../pages/mypage/payments/MyPagePayments";
import MyPageRequests from "../pages/mypage/requests/MyPageRequests";
import MyPageDibs from "../pages/mypage/dibs/MyPageDibs";
import MyPageReviews from "../pages/mypage/reviews/MyPageReviews";
import MyPageProfile from "../pages/mypage/profile/MyPageProfile";
import MyPageOrdersDetail from "../pages/mypage/orders/MyPageOrdersDetail";

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
                <Route path="products/requests" element={<AdminProducts />} />
                <Route path="products/:productId" element={<AdminProductsDetail />} />
                <Route path="products/requests/:productId" element={<AdminProductsRequestDetail />} />
                <Route path="groupBuy" element={<AdminGroupBuy />} />
                <Route path="groupBuy/:groupBuyId" element={<AdminGroupBuyDetail />} />
                <Route path="orders" element={<AdminOrders />} />
                <Route path="orders/:orderId" element={<AdminOrdersDetail />} />
                <Route path="reviews" element={<AdminReviews />} />
            </Route>
            <Route path="/mypage" element={<MyPageLayout />}>
                <Route index element={<Navigate to="dashboard" replace />} />
                <Route path="dashboard" element={<MyPageDashboard />} />
                <Route path="orders" element={<MyPageOrders />} />
                <Route path="orders/:orderId" element={<MyPageOrdersDetail />} />
                <Route path="payments" element={<MyPagePayments />} />
                <Route path="requests" element={<MyPageRequests />} />
                <Route path="dibs" element={<MyPageDibs />} />
                <Route path="reviews" element={<MyPageReviews />} />
                <Route path="profile" element={<MyPageProfile />} />
            </Route>
            {/* 에러페이지 */}
            <Route path={"*"} element={<Error404 />} />
        </Routes>
    );
}

export default BaseRoutes;