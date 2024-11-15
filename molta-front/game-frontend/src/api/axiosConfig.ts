// src/api/axiosConfig.ts
import axios from 'axios';

const instance = axios.create({
    baseURL: 'http://localhost:8412/api',
    headers: {
        'Content-Type': 'application/json',
    },
    withCredentials: false,  // 모든 요청에 대해 자격 증명 포함
});


const authInstance = axios.create({
    baseURL: 'http://localhost:8412/api',
    headers: {
        'Content-Type': 'application/json',
    },
    withCredentials: true,  // 자격 증명 포함
});

authInstance.interceptors.request.use((config) => {
    const token = localStorage.getItem('token');
    if (token) {
        config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
});


instance.interceptors.request.use((config) => {
    const token = localStorage.getItem('token');
    if (token) {
        config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
});

export default instance;
