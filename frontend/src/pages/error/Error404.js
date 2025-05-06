import {useNavigate} from "react-router-dom";

const Error404 = () => {
    const navigate = useNavigate();

    return (
        <div className="kw-error">
            <div className="kw-error__inner">
                <i className="ico-exclamation-danger"></i>
                <strong> 404 Error page </strong>
                <p>
                    페이지를 찾을 수 없습니다.<br/>
                    페이지가 존재하지 않거나, 사용할 수 없는 페이지입니다.
                </p>
                <div className="kw-login-button">
                    <button type="button" className="btn btn-primary btn-lg"
                            onClick={() => navigate("/post/list")}>
                        홈으로
                    </button>
                </div>
            </div>
        </div>
    );
}

export default Error404;