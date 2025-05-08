import {Navigate, Route, Routes} from "react-router-dom";
import Login from "../pages/login/Login";
import Error404 from "../pages/error/Error404";
import Signup from "../pages/login/Signup";
import Main from "../pages/main/Main";
import Layout from "../layout/Layout";
import ProductDetail from "../pages/products/ProductDetail";

const BaseRoutes = () => {
    return (
        <Routes>
            <Route exact path="login" element={<Login />} />
            <Route exact path="signup" element={<Signup />} />
            <Route path="/" element={<Layout />} >
                {/* 메인 도메인만 입력 시 로그인으로 이동*/}
                <Route index element={<Navigate to="/main" replace />} />
                <Route path="/main" element={<Main />} />
                <Route path="/products/:productId" element={<ProductDetail />} />
            </Route>
            {/* 에러페이지 */}
            <Route path={"*"} element={<Error404 />} />
        </Routes>
    );
}

export default BaseRoutes;