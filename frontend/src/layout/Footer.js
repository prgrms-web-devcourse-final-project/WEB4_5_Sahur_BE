import {Col, Row, Stack} from "react-bootstrap";

const Footer = () => {
    return (
        <footer className="kw-footer mt-5 p-5">
            <Stack direction={"horizontal"}>
                <div className={"ms-8"}>
                    <h5>퉁하자</h5>
                    <address>함께 모여 더 저렴하게 구매하는 실시간 공동구매 플랫폼</address>
                </div>
                <div className="ms-auto me-8">
                    <address>&copy; 2023 퉁하자. All rights reserved.</address>
                    <address>고객센터: 1588-1234 (평일 09:00-18:00)</address>
                </div>
            </Stack>
        </footer>
    );
}

export default Footer;