import {Outlet, Navigate, useLocation} from "react-router-dom";
import {useEffect, useState} from "react";
import Header from "./Header";
import Footer from "./Footer";

const Layout = () => {
    const [isChecked, setIsChecked] = useState(false);
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const location = useLocation();

    useEffect(() => {
        const token = localStorage.getItem("token");
        setIsAuthenticated(!!token);
        setIsChecked(true);
    }, [location]);

    if (!isChecked) return null;

    // /login 또는 /signup 접근 시엔 그대로 진행
    if (!isAuthenticated && !["/login", "/signup"].includes(location.pathname)) {
        return <Navigate to="/login" replace />;
    }

    return (
        <div className={"kw"}>
            <Header />
            <Outlet />
            <Footer />
        </div>
    );
}

export default Layout;