import {Outlet, useLocation, useNavigate} from "react-router-dom";
import AdminHeader from "./AdminHeader";
import AdminSidebar from "./AdminSidebar";
import AdminSubHeader from "./AdminSubHeader";

const AdminLayout = () => {
    const location = useLocation();
    const pathSegments = location.pathname.split('/').filter(Boolean);
    const pageId = pathSegments[1];
    return (
        <div className="d-flex flex-column vh-100">
            <AdminHeader />
            <div className="d-flex flex-grow-1">
                <AdminSidebar pageId={pageId}/>
                <main className="flex-grow-1">
                    <AdminSubHeader pageId={pageId}/>
                    <Outlet />
                </main>
            </div>
        </div>
    );
}

export default AdminLayout;