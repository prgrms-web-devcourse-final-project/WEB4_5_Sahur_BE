import {Card, Form, Stack} from "react-bootstrap";
import NameForm from "../login/NameForm";
import {useEffect, useState} from "react";
import PhoneForm from "../login/PhoneForm";
import AddressForm from "../login/AddressForm";
import DetailAdrForm from "../login/DetailAdrForm";
import styles from "./Payment.module.scss"
import DeliveryMemoForm from "./DeliveryMemoForm";
import {useRecoilValue} from "recoil";
import {userAtom} from "../../state/atoms";
import PcccForm from "./PcccForm";

const DeliverySection = ({setDeliveryInfo}) => {
    const loginUser = useRecoilValue(userAtom);
    const [name, setName] = useState(loginUser.name);
    const [phone, setPhone] = useState(loginUser.phoneNumber);
    const [zipCode, setZipCode] = useState(loginUser.zipCode);
    const [streetAdr, setStreetAdr] = useState(loginUser.streetAdr);
    const [detailAdr, setDetailAdr] = useState(loginUser.detailAdr);
    const [deliveryMemo, setDeliveryMemo] = useState("");
    const [pccc, setPccc] = useState("P");

    const [error, setError] = useState({
        name: '',
        phone: '',
        zipCode: '',
        streetAdr: '',
        detailAdr: '',
        deliveryMemo: '',
        pccc: ''
    })
    const [inputStatus, setInputStatus] = useState({
        name: true,
        phone: true,
        zipCode: true,
        streetAdr: true,
        detailAdr: true,
        deliveryMemo: true,
        pccc: false
    });

    //필수정보가 불완전하면 결제를 막기 위한 사이드이펙트
    useEffect(() => {
        if (Object.values(inputStatus).every(Boolean)) {
            setDeliveryInfo({
                ready: true,
                name,
                phone,
                zipCode,
                streetAdr,
                detailAdr,
                deliveryMemo,
                pccc
            });
        } else {
            setDeliveryInfo(prev => {
                return {
                    ...prev,
                    ready: false
                }
            });
        }
    }, [inputStatus])

    const handleAddressSelect = (data) => {
        setZipCode(data.zonecode);
        setStreetAdr(data.address);
        setError({ ...error, streetAdr: '' })
        setInputStatus(prev => ({ ...prev, zipCode: true, streetAdr: true }));
    }

    return (
        <>
            <Form className={styles.deliveryForm} onSubmit={e => e.preventDefault()}>
                <Stack direction={'vertical'}>
                    <h3>배송 정보</h3>
                    <desc className={"text-gray-300"}>상품을 받으실 주소를 입력해주세요.</desc>
                </Stack>
                <NameForm name={name} setName={setName} error={error} setError={setError} setInputStatus={setInputStatus} />
                <PhoneForm phone={phone} setPhone={setPhone} error={error} setError={setError} setInputStatus={setInputStatus} />
                <AddressForm zipCode={zipCode} streetAdr={streetAdr} handleAddressSelect={handleAddressSelect} error={error} />
                <DetailAdrForm detailAdr={detailAdr} setDetailAdr={setDetailAdr} error={error} setError={setError} setInputStatus={setInputStatus} />
                <DeliveryMemoForm deliveryMemo={deliveryMemo} setDeliveryMemo={setDeliveryMemo} error={error} setError={setError} setInputStatus={setInputStatus} />
                <PcccForm pccc={pccc} setPccc={setPccc} error={error} setError={setError} setInputStatus={setInputStatus} />
            </Form>
        </>
    )
}

export default DeliverySection;