import {atom} from "recoil";

export const toastAtom = atom({
    key: 'toastAtom', // 고유 키
    default: {
        isOpen: false,
        message: '',
        type : ''
    },
});

export const confirmAtom = atom({
    key: 'confirmAtom',
    default: {
        isOpen: false // 모달 열림 여부
        , title: 'modal title' // 모달 타이틀
        , html: 'modal content' //모달 설명
        , icon: 'modal-icon' //모달 아이콘
        , confirmButtonText: '확인' //확인버튼 이름
        , callback: null //'확인' 버튼 클릭 시 콜백 함수, close메소드는 자동 호출한다.
        , onCancel : null
    },
});
