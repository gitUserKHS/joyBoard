import { API_SERVER_HOST } from "../api/api";
import axiosInstance from "../api/authApi";

// 로그인 상태 확인 함수
const isLoggedIn = async () => {
    const accessToken = localStorage.getItem('accessToken');
    const refreshToken = localStorage.getItem('refreshToken');
    
    // 액세스 토큰이 존재하는지 확인
    if (!accessToken) {
        return false;
    }

    // JWT 토큰의 유효성 검사
    const payload = accessToken.split('.')[1];
    if (!payload) return false;

    const decodedPayload = JSON.parse(atob(payload));
    
    // 만료 시간 체크 (토큰의 'exp' 필드 사용)
    const currentTime = Date.now() / 1000; // 초 단위로 변환

    if (decodedPayload.exp <= currentTime){
        console.log("check!")
        const response = await axiosInstance.get(`${API_SERVER_HOST}/api/check`);
        if(response.status !== 200)
            return false;
    }

    return true;
};

// Base64 디코딩 함수
const parseJwt = (token) => {
    try {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const jsonPayload = decodeURIComponent(
            atob(base64)
                .split('')
                .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
                .join('')
        );
        return JSON.parse(jsonPayload);
    } catch (error) {
        console.error('Invalid token', error);
        return null;
    }
};

// 로컬 스토리지에서 accessToken을 가져와서 userName 추출
const getUserName = () => {
    const accessToken = localStorage.getItem('accessToken');
    if (!accessToken) {
        console.log('Access token not found');
        return null;
    }

    const decodedToken = parseJwt(accessToken);
    if (decodedToken && decodedToken.username) {
        return decodedToken.username;
    } else {
        console.log('userName not found in token');
        return null;
    }
};
export {isLoggedIn, getUserName};
