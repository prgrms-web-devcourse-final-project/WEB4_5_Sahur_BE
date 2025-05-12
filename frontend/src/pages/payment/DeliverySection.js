import {Card, Form} from "react-bootstrap";
import NameForm from "../login/NameForm";
import {useState} from "react";
import PhoneForm from "../login/PhoneForm";
import AddressForm from "../login/AddressForm";
import DetailAdrForm from "../login/DetailAdrForm";
import styles from "./Payment.module.scss"
import DeliveryMemoForm from "./DeliveryMemoForm";

const DeliverySection = () => {
    const [name, setName] = useState("");
    const [phone, setPhone] = useState("");
    const [zipCode, setZipCode] = useState("");
    const [streetAdr, setStreetAdr] = useState("");
    const [detailAdr, setDetailAdr] = useState("");
    const [deliveryMemo, setDeliveryMemo] = useState("");

    const [error, setError] = useState({
        name: '',
        phone: '',
        zipCode: '',
        streetAdr: '',
        detailAdr: '',
        deliveryMemo: ''
    })
    const [inputStatus, setInputStatus] = useState({
        name: false,
        phone: false,
        zipCode: false,
        streetAdr: false,
        detailAdr: false,
        deliveryMemo: false
    });

    const handleAddressSelect = (data) => {
        setZipCode(data.zonecode);
        setStreetAdr(data.address);
        setError({ ...error, streetAdr: '' })
        setInputStatus(prev => ({ ...prev, zipCode: true, streetAdr: true }));
    }

    return (
        <Card>
            <Card.Body>
                <Form className={styles.deliveryForm} onSubmit={e => e.preventDefault()}>
                    <h3>배송 정보</h3>
                    <desc>상품을 받으실 주소를 입력해주세요.</desc>
                    <NameForm name={name} setName={setName} error={error} setError={setError} setInputStatus={setInputStatus} />
                    <PhoneForm phone={phone} setPhone={setPhone} error={error} setError={setError} setInputStatus={setInputStatus} />
                    <AddressForm zipCode={zipCode} streetAdr={streetAdr} handleAddressSelect={handleAddressSelect} error={error} />
                    <DetailAdrForm detailAdr={detailAdr} setDetailAdr={setDetailAdr} error={error} setError={setError} setInputStatus={setInputStatus} />
                    <DeliveryMemoForm deliveryMemo={deliveryMemo} setDeliveryMemo={setDeliveryMemo} error={error} setError={setError} setInputStatus={setInputStatus} />
                </Form>
            </Card.Body>
        </Card>
    )
}

export default DeliverySection;