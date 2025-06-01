import {Outlet, useLocation} from "react-router-dom";
import MyPageHeader from "./MyPageHeader";
import MyPageSidebar from "./MyPageSidebar";
import MyPageSubHeader from "./MyPageSubHeader";
import {Stack} from "react-bootstrap";
import MyPageProfileCard from "./MyPageProfileCard";

const MyPageLayout = () => {
    const location = useLocation();
    const pathSegments = location.pathname.split('/').filter(Boolean);
    const pageId = pathSegments[1];
    return (
        <Stack className="vh-100">
            <MyPageHeader />
            <Stack direction="horizontal">
                <Stack style={{ flex: "0 0 auto", width: "270px" }}>
                    <MyPageProfileCard />
                    <MyPageSidebar pageId={pageId}/>
                </Stack>
                <Stack className="flex-grow-1">
                    <MyPageSubHeader pageId={pageId}/>
                    <Outlet />
                </Stack>
            </Stack>
        </Stack>
    );
}

export default MyPageLayout;