import React, { useState, useEffect } from 'react';
import { getList } from '../api/api';
import { Link, useNavigate } from 'react-router-dom'; // Add useNavigate for navigation

const PostList = ({ isLoggedIn }) => { // Add isLoggedIn prop
    const [posts, setPosts] = useState([]);
    const [currentPage, setCurrentPage] = useState(1);
    const [totalPages, setTotalPages] = useState(0);
    const [loading, setLoading] = useState(true);
    //const [_isLoggedIn, setIsLoggedIn] = useState(false);
    const navigate = useNavigate(); // For navigating to new pages

    const fetchPosts = async (page) => {
        setLoading(true);
        try {
            const data = await getList(page);
            setPosts(data.dtoList);
            setTotalPages(data.totalCount); // Adjust based on your pagination logic
        } catch (error) {
            console.error("Error fetching posts:", error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchPosts(currentPage);
    }, [currentPage]);

    const handleNextPage = () => {
        if (currentPage < Math.ceil(totalPages / 10)) {
            setCurrentPage(currentPage + 1);
        }
    };

    const handlePrevPage = () => {
        if (currentPage > 1) {
            setCurrentPage(currentPage - 1);
        }
    };

    const handleAddPost = () => {
        navigate('/add-post'); // Navigate to the "Add Post" page
    };

    return (
        <div className="max-w-3xl mx-auto p-6 bg-gray-50 min-h-screen">
            <h1 className="text-2xl font-bold mb-4">Post List</h1>
            {isLoggedIn && ( // Display "Add Post" button if the user is logged in
                <button 
                    onClick={handleAddPost} 
                    className="mb-4 px-4 py-2 bg-green-500 text-white rounded-lg"
                >
                    Add New Post
                </button>
            )}
            {loading ? (
                <p className="text-center text-gray-500">Loading...</p>
            ) : (
                <ul className="space-y-4">
                    {posts.map(post => (
                        <li key={post.id} className="p-4 bg-white rounded-lg shadow-md">
                            <Link to={`/post/${post.id}`}>
                                <h2 className="text-xl font-semibold">{post.title}</h2>
                            </Link>
                            <p className="text-gray-600">Written by: {post.userName || 'Anonymous'}</p>
                            <p className="text-gray-500 text-sm">Written at: {new Date(post.writtenAt).toLocaleString()}</p>
                        </li>
                    ))}
                </ul>
            )}
            <div className="flex justify-between mt-6">
                <button 
                    onClick={handlePrevPage} 
                    disabled={currentPage === 1} 
                    className={`px-4 py-2 bg-blue-500 text-white rounded-lg ${currentPage === 1 ? 'opacity-50 cursor-not-allowed' : ''}`}
                >
                    Previous
                </button>
                <button 
                    onClick={handleNextPage} 
                    disabled={currentPage >= Math.ceil(totalPages / 10)} 
                    className={`px-4 py-2 bg-blue-500 text-white rounded-lg ${currentPage >= Math.ceil(totalPages / 10) ? 'opacity-50 cursor-not-allowed' : ''}`}
                >
                    Next
                </button>
            </div>
        </div>
    );
};

export default PostList;
