import axios from 'axios';
import { API_SERVER_HOST } from './api';

const API_URL = API_SERVER_HOST; // API URL

const axiosInstance = axios.create({
    baseURL: API_URL,
});

// 토큰 재발급 함수
const refreshAccessToken = async (refreshToken) => {
    try {
        const response = await axios.post(`${API_SERVER_HOST}/api/reissue`, { refreshToken }, {
            headers: {
                Authorization: `Bearer ${localStorage.getItem('accessToken')}` // 헤더에 refreshToken 포함
            }
        });
        // 새로운 액세스 토큰을 로컬 스토리지에 저장
        localStorage.setItem('accessToken', response.headers.get('Authorization').replace('Bearer ', ''));
        return response.headers.get('Authorization').replace('Bearer ', '');
    } catch (error) {
        console.error('Error refreshing access token:', error);
        throw error;
    }
};

// 요청 인터셉터 추가
axiosInstance.interceptors.request.use(
    async (config) => {
        const accessToken = localStorage.getItem('accessToken');
        const refreshToken = localStorage.getItem('refreshToken');

        if (accessToken) {
            config.headers['Authorization'] = `Bearer ${accessToken}`;
        }

        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// 응답 인터셉터 추가
axiosInstance.interceptors.response.use(
    (response) => {
        return response;
    },
    async (error) => {
        const originalRequest = error.config;
        const refreshToken = localStorage.getItem('refreshToken');

        // 액세스 토큰 만료 시
        if (error.response.status === 401 && refreshToken) {
            try {
                const newAccessToken = await refreshAccessToken(refreshToken);
                originalRequest.headers['Authorization'] = `Bearer ${newAccessToken}`;
                return axiosInstance(originalRequest); // 원래 요청 재전송
            } catch (err) {
                console.error('Could not refresh access token:', err);
                return Promise.reject(err);
            }
        }

        return Promise.reject(error);
    }
);

export default axiosInstance;
