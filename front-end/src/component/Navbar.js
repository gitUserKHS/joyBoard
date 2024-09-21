import React, { useEffect, useState } from 'react';
import { Link, useLocation } from 'react-router-dom'; // useLocation 훅 추가
import { isLoggedIn } from '../util/loginUitl';
import axiosInstance from '../api/authApi';
import { API_SERVER_HOST } from '../api/api';

function Navbar({_isLoggedIn, onLogout}) {
    return (
        <nav className="bg-gray-800 p-4">
            <ul className="flex space-x-4">
                {_isLoggedIn ? (
                    <li>
                        <button 
                            onClick={onLogout} 
                            className="text-white hover:text-gray-300"
                        >
                            Logout
                        </button>
                    </li>
                ) : (
                    <>
                        <li>
                            <Link to="/login" className="text-white hover:text-gray-300">
                                Login
                            </Link>
                        </li>
                        <li>
                            <Link to="/signup" className="text-white hover:text-gray-300">
                                Sign Up
                            </Link>
                        </li>
                    </>
                )}
            </ul>
        </nav>
    );
}

export default Navbar;
