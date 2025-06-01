import {Card} from "react-bootstrap";
import arrowLeft from "../../assets/images/icon/arrow-left.png";
import logo from "../../assets/images/tung.png";
import {useNavigate} from "react-router-dom";

const MyPageHeader = () => {
    const navigate = useNavigate();
    return (
        <Card>
            <Card.Body className={"ps-5"} style={{ background: "#E9D5FF" }}>
                    <span className={"cursor-pointer"} onClick={() => navigate("/main")}>
                        <img src={arrowLeft} style={{ width: "20px" }}/>
                        <img src={logo} style={{ width: "120px" }}/>
                    </span>
            </Card.Body>
        </Card>
    );
}

export default MyPageHeader;