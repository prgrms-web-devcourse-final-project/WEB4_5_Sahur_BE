import {Button, Form, Stack} from "react-bootstrap";
import {useMutation} from "react-query";
import axios from "axios";
import {useState} from "react";
import Spinner from "../../shared/Spinner";
import useConfirm from "../../hooks/useConfirm";
import {isEmptyOrNull} from "../../utils/utils";

const NicknameForm = ({nickname, setNickname, error, setError, setInputStatus}) => {
    const {openConfirm} = useConfirm();
    const [isChecked, setChecked] = useState(false);
    const nicknameCheckMutation = useMutation((sendData) => axios.post('/api/v1/members/nickname/check', sendData), {
        onSuccess: (response) => {
            setError({ ...error, nickname: '' });
            setChecked(true);
            setInputStatus(prev => ({ ...prev, nickname: true }));
            openConfirm({
                title: response.data.msg
                , showCancelButton: false
            });
        }
        , onError: (error) => {
            console.log(error)
            openConfirm({
                title: '처리 중 오류가 발생했습니다.',
                html: error.response?.data?.msg || "에러: 관리자에게 문의바랍니다."
            });
        }
    });

    const isValidLength = (value) => {
        return /^.{2,16}$/.test(value.trim());
    };

    const handleNicknameCheckClick = () => {
        setChecked(false);
        setInputStatus(prev => ({ ...prev, nickname: false }));
        if (isEmptyOrNull(nickname)) {
            setError({ ...error, nickname: "별명을 먼저 입력해 주세요." });
            return;
        }
        if (!isValidLength(nickname)) {
            setError({ ...error, nickname: "별명은 2~16자 사이로 입력해주세요." });
            return;
        }
        nicknameCheckMutation.mutate({ nickname })
    }

    const handleNicknameChange = (e) => {
        setNickname(e.target.value);
        setChecked(false);
        setError({ ...error, nickname: '' })
        setInputStatus(prev => ({ ...prev, nickname: false }));
    }

    return (
        <Form.Group controlId={"forNickname"} >
            <Stack direction={"horizontal"} gap={4}>
                <Form.Label>별명</Form.Label>
                {error.nickname && <Form.Label style={{ color: 'red', fontSize: "13px" }}>{error.nickname}</Form.Label>}
                {isChecked && <Form.Label style={{ color: 'blue', fontSize: "13px" }}>{'사용 가능한 별명입니다.'}</Form.Label>}
            </Stack>
            <div className={"d-flex gap-2"} >
                <Form.Control
                    type="text"
                    placeholder="별명"
                    value={nickname}
                    onChange={handleNicknameChange}
                />
                <Button type={"button"} variant={"dark"} onClick={handleNicknameCheckClick} >중복 확인</Button>
            </div>
            <Spinner show={nicknameCheckMutation.isLoading} />
        </Form.Group>
    );
}

export default NicknameForm;