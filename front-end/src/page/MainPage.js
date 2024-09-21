import React, { useEffect, useState } from 'react';
import { useLocation } from 'react-router-dom'; // useLocation 가져오기
import Navbar from '../component/Navbar';
import PostList from '../component/PostList';
import axiosInstance from '../api/authApi';
import { API_SERVER_HOST } from '../api/api';
import { isLoggedIn } from '../util/loginUitl';

function MainPage(props) {
    const location = useLocation(); // 현재 위치 가져오기
    const [_isLoggedIn, setIsLoggedIn] = useState(false);
    const [logout, setLogout] = useState(false);

    const onLogout = async () => {
        await axiosInstance.post(`${API_SERVER_HOST}/api/log-out`);
        localStorage.removeItem('accessToken');
        setLogout(!logout);
    };

    useEffect(() => {
        const checkLoginStatus = async () => {
            setIsLoggedIn(await isLoggedIn());
        };
        checkLoginStatus();
    }, [logout, location]); // location을 의존성에 추가

    return (
        <div>
            <Navbar
                _isLoggedIn={_isLoggedIn}
                onLogout={onLogout}
            />
            <PostList isLoggedIn={_isLoggedIn} />
        </div>
    );
}

export default MainPage;
