import React, { useState } from 'react';
import axiosInstance from '../api/authApi';
import { API_SERVER_HOST } from '../api/api';
import { useNavigate } from 'react-router-dom'; // useNavigate 훅 가져오기
import axios from 'axios';

const SignupPage = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [email, setEmail] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate(); // navigate 함수 초기화

    const handleSignUp = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');

        try {
            const response = await axios.post(`${API_SERVER_HOST}/api/join`, {
                name: username,
                password: password,
            });

            if (response.data.join === "success") {
                // 회원가입 성공 시 메인 페이지로 이동
                navigate('/'); // 원하는 경로로 변경 가능
            }
            else{
                setError('회원가입에 실패했습니다. 다시 시도해주세요.');
            }
        } catch (err) {
            setError('회원가입에 실패했습니다. 다시 시도해주세요.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="max-w-md mx-auto p-6 bg-white shadow-lg rounded-lg">
            <h2 className="text-2xl font-bold mb-4">Sign up</h2>
            {error && <p className="text-red-500">{error}</p>}
            <form onSubmit={handleSignUp}>
                <div className="mb-4">
                    <label className="block text-sm font-medium mb-1">user name</label>
                    <input
                        type="text"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        required
                        className="w-full border px-3 py-2 rounded"
                    />
                </div>
                <div className="mb-4">
                    <label className="block text-sm font-medium mb-1">password</label>
                    <input
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                        className="w-full border px-3 py-2 rounded"
                    />
                </div>
                <button
                    type="submit"
                    disabled={loading}
                    className={`w-full bg-blue-500 text-white px-4 py-2 rounded ${loading ? 'opacity-50 cursor-not-allowed' : ''}`}
                >
                    {loading ? '가입 중...' : '회원가입'}
                </button>
            </form>
        </div>
    );
};

export default SignupPage;
