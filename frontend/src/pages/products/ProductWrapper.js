import {Card} from "react-bootstrap";

const ProductWrapper = ({children}) => {
    return (
        <Card className={"p-4"} style={{ marginBottom: "65px" }}>
            <Card.Body className={"px-2 pb-10"} style={{ background: "var(--Gray-100, #e5e7ea)" }}>
                <Card className={"p-2"}>
                    <Card.Body>
                        {children}
                    </Card.Body>
                </Card>
            </Card.Body>
        </Card>
    );
}

export default ProductWrapper;