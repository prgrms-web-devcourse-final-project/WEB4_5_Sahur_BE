import {Modal} from "react-bootstrap";

const Spinner = ({
                     show = true,       // 스피너 출력 여부
                     isTable = false,    // 출력할 대상이 테이블인지 여부(true일 경우 스피너가 모달에 출력되는게 아닌 화면에 직접 출력 됨)
                     isSmall = false,    // 스피너 크기 축소 여부
                     customStyle = {}       //스피너에 inline으로 들어갈
}) => {
    return (
        <>
            {show && !isTable ?
                <Modal show={show} contentClassName={"kw-load-modal"} backdrop={"static"} centered>
                    <div className="kw-load">
                        <hr/>
                        <hr/>
                        <hr/>
                        <hr/>
                        <hr/>
                    </div>
                </Modal>
                :
                show && isTable &&
                <div className={`kw-load ${isSmall ? "small" : ""}`} style={customStyle}>
                    <hr/>
                    <hr/>
                    <hr/>
                    <hr/>
                    <hr/>
                </div>
            }
        </>

    );
}

export default Spinner;