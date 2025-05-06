import React, {StrictMode} from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import App from './App';
import {BrowserRouter} from "react-router-dom";
import {RecoilRoot} from "recoil";
import {QueryClient, QueryClientProvider} from "react-query";
import axios from "axios";

// react-qeury사용을 위해 선언
const queryClient = new QueryClient({
    defaultOptions: {
        queries: {
            retry: false // Disable retries globally
        },
    },
});

// Axios 요청 인터셉터 - 요청 전에 실행됨
axios.interceptors.request.use(config => {
    const token = localStorage.getItem('token');

    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }

    // Content-Type이 없으면 기본적으로 application/json으로 설정
    if (!config.headers['Content-Type']) {
        config.headers['Content-Type'] = 'application/json';
    }
    return config;
}, error => {
    return Promise.reject(error);
});

// Axios 응답 인터셉터 - 응답 후에 실행됨
axios.interceptors.response.use(response => {
    const token = response.headers['authorization'];
    if (token) {
        localStorage.setItem('token', token);
    }
    return response;
}, error => {
    // 401 또는 403 에러인 경우 로그인 페이지로 리다이렉트
    if (error.response && (error.response.status === 401 || error.response.status === 403)) {
        //이 부분은 원래는 navigate로 핸들링 해야하지만 현재 리액트 포팅 진행중이므로 window객체에 직접 접근함
        //추후 navigate로 포팅되어야 함
        window.location.href = "/login";
    }
    return Promise.reject(error);
});

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
    <BrowserRouter>
        <StrictMode>
            <RecoilRoot>
                <QueryClientProvider client={queryClient}>
                    <App />
                </QueryClientProvider>
            </RecoilRoot>
        </StrictMode>
    </BrowserRouter>
);

