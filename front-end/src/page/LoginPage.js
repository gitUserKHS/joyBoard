import React, { useState } from 'react';
import { API_SERVER_HOST } from '../api/api';
import { useNavigate } from 'react-router-dom';

const LoginPage = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState(null);
    const navigate = useNavigate();

    const handleLogin = async (e) => {
        e.preventDefault();

        let un, pw;

        un = username.trim();
        un = un.replace('\n', '');
        pw = password.trim();
        pw = pw.replace('\n', '');

        try {
            const response = await fetch(`${API_SERVER_HOST}/api/login`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ id: un, password: pw }),
            });

            if (!response.ok) {
                throw new Error('Login failed');
            }

            const data = await response.json();
            let accessToken = response.headers.get('Authorization');
            accessToken = accessToken.replace('Bearer ', '');
            console.log(accessToken, data.refreshToken);
            // 액세스 토큰과 리프레시 토큰을 로컬 스토리지에 저장
            localStorage.setItem('accessToken', accessToken);
            localStorage.setItem('refreshToken', data.refreshToken);
            // 성공적으로 로그인한 후의 동작을 정의
            alert('Login successful');
            navigate("/");
        } catch (error) {
            setError(error.message);
        }
    };

    return (
        <div className="flex items-center justify-center min-h-screen bg-gray-100">
            <div className="bg-white p-8 rounded-lg shadow-md w-full max-w-sm">
                <h1 className="text-2xl font-bold mb-6 text-center">Login</h1>
                <form onSubmit={handleLogin}>
                    <div className="mb-4">
                        <label className="block text-sm font-medium text-gray-700">
                            Username:
                            <input
                                type="text"
                                value={username}
                                onChange={(e) => setUsername(e.target.value)}
                                required
                                className="mt-1 block w-full p-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
                            />
                        </label>
                    </div>
                    <div className="mb-4">
                        <label className="block text-sm font-medium text-gray-700">
                            Password:
                            <input
                                type="password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                required
                                className="mt-1 block w-full p-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
                            />
                        </label>
                    </div>
                    <button 
                        type="submit" 
                        className="w-full py-2 px-4 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
                    >
                        Login
                    </button>
                    {error && <p className="mt-4 text-red-500">{error}</p>}
                </form>
            </div>
        </div>
    );
};

export default LoginPage;
