import {useNavigate} from "react-router-dom";
import {Button, Stack} from "react-bootstrap";
import useConfirm from "../../../hooks/useConfirm";
import {useMutation} from "react-query";
import axios from "axios";
import {useEffect} from "react";
import Spinner from "../../../shared/Spinner";

const NavigationMenu = ({ menuItems }) => {
    const navigate = useNavigate();
    const { openConfirm } = useConfirm();

    useEffect(() => {
        userProfileMutation.mutate();
    }, [])
    const logoutMutation = useMutation(() => axios.post("/auth/logout", {}), {
        onSuccess: (param) => {
            localStorage.removeItem('token');
            openConfirm({
                title: "로그아웃 되었습니다."
                , callback: () => navigate("/login")
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
            console.log(param)
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
            <Button variant={""} className={"text-dark ms-auto"} onClick={() => navigate("/login")}>
                <span className={"ico-user"} />로그인/회원가입
            </Button>
            <Spinner show={logoutMutation.isLoading}/>
        </Stack>
    );
};

export default NavigationMenu;
