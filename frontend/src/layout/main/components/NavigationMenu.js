import {useNavigate} from "react-router-dom";
import {Button, Dropdown, Image, Stack} from "react-bootstrap";
import useConfirm from "../../../hooks/useConfirm";
import {useMutation} from "react-query";
import axios from "axios";
import {useEffect} from "react";
import Spinner from "../../../shared/Spinner";
import {useRecoilState} from "recoil";
import {userAtom} from "../../../state/atoms";
import {
    ReactComponent as EmptyLikeIcon
} from "../../../assets/images/icon/empty-like.svg";
import {
    ReactComponent as EmptyBellIcon
} from "../../../assets/images/icon/empty-bell.svg";

const NavigationMenu = ({ menuItems }) => {
    const navigate = useNavigate();
    const { openConfirm } = useConfirm();
    const [loginUser, setLoginUser] = useRecoilState(userAtom);

    useEffect(() => {
        userProfileMutation.mutate();
    }, [])
    const logoutMutation = useMutation(() => axios.post("/api/v1/auth/logout", {}), {
        onSuccess: (param) => {
            setLoginUser({ isLoggedIn: false })
            openConfirm({
                title: "로그아웃 되었습니다."
                , callback: () => navigate("/main")
                , showCancelButton: false
            });
        }
        , onError: (error) => {
            openConfirm({
                title: '처리 중 오류가 발생했습니다.',
                html: error.response?.data?.message || "에러: 관리자에게 문의바랍니다."
            });
        }
    });

    const userProfileMutation = useMutation(() => axios.get("/api/v1/members/me", {withCredentials: true}), {
        onSuccess: (param) => {
            setLoginUser(param.data.data);
        }
        , onError: (error) => {
            openConfirm({
                title: '처리 중 오류가 발생했습니다.',
                html: error.response?.data?.message || "에러: 관리자에게 문의바랍니다."
            });
        }
    });
    return (
        <Stack direction={"horizontal"} style={{ boxShadow: 'inset 0px -5px 0px rgb(211, 211, 211)' }} >
        <span className={"ms-3 me-3 ico-menu"} />
            카테고리
            {loginUser.isLoggedIn ? <Stack direction={"horizontal"} gap={3} className={"ms-auto"} >
                    공동구매 요청하기
                    <EmptyLikeIcon width={"20"} height={"20"}/>
                    <EmptyBellIcon width={"20"} height={"20"}/>
                    <Dropdown>
                        <Dropdown.Toggle id="dropdown-custom-components" variant={""} className={"text-black"}>
                            <Image src={loginUser.imageUrl} width={25} height={25} roundedCircle />{loginUser.nickname}
                        </Dropdown.Toggle>
                        <Dropdown.Menu>
                            <Dropdown.Item href="/mypage">마이페이지</Dropdown.Item>
                            <Dropdown.Item onClick={() => logoutMutation.mutate()}>로그아웃</Dropdown.Item>
                        </Dropdown.Menu>
                    </Dropdown>
                </Stack> :
                    <Button variant={""} className={"text-dark ms-auto p-0"} onClick={() => navigate("/login")}>
                        <span className={"ico-user"} />로그인/회원가입
                    </Button>
                }
            <Spinner show={logoutMutation.isLoading}/>
        </Stack>
    );
};

export default NavigationMenu;
