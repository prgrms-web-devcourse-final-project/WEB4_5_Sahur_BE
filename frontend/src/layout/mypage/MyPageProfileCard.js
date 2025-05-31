import {Button, Card, Image, Stack} from "react-bootstrap";
import styles from "./MyPageLayout.module.scss"
import {useNavigate} from "react-router-dom";

const MyPageProfileCard = () => {
    const navigate = useNavigate();
    return (
        <Card className="p-3 m-3 shadow">
            <Card.Body>
                <Stack direction={"horizontal"} gap={2}>
                    <Image src={"https://api.devapi.store/images/default-profile.png"} roundedCircle style={{ width: "40px", height: "40px" }} />
                    <Stack>
                        <h5>수민쨩</h5>
                        <desc className={"text-gray-300"}>
                            일반 회원
                        </desc>
                    </Stack>
                </Stack>
                <Button variant={""} className={`w-100 mt-3 ${styles.whiteButton}`}
                onClick={() => navigate('profile')}>프로필 수정</Button>
            </Card.Body>
        </Card>
    );
}

export default MyPageProfileCard;