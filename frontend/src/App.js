import './assets/scss/common.scss';
import {RecoilRoot} from "recoil";
import BaseRoutes from "./routes/BaseRoutes";
import ToastNotification from "./shared/ToastNotification";
import Confirm from "./shared/Confirm";
import ScrollToTop from "./shared/ScrollToTop";

function App() {
    return (
        <RecoilRoot >
            <ToastNotification/>
            <Confirm/>
            <ScrollToTop />
            <BaseRoutes />
        </RecoilRoot>
    );
}

export default App;
