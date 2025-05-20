import {Outlet} from "react-router-dom";
import Header from "./Header";
import Footer from "./Footer";
import {Card} from "react-bootstrap";

const Layout = () => {
  return (
    <div className={"kw"}>
      <Header />
      <Card className={"p-4"} style={{ marginBottom: "65px" }}>
        <Card.Body className={"px-5"} style={{ background: "#F9FAFB" }}>
          <Outlet />
          <Footer />
        </Card.Body>
      </Card>
    </div>
  );
};

export default Layout;
