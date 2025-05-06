import {Tab, Tabs} from "react-bootstrap";

const TabLayout = ({ children, currentTabKey, tabMenu, handleTabChange }) => {
    return (
        <Tabs defaultActiveKey={currentTabKey} onSelect={handleTabChange}>
            {tabMenu.map((item) => {
                return <Tab eventKey={item.key} title={item.title}>
                    {children}
                </Tab>
            })}

        </Tabs>
    );
}

export default TabLayout;