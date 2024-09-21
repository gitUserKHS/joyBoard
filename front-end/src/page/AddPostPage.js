import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axiosInstance from '../api/authApi';
import { boardApiPrefix } from '../api/api'; // Import the correct API prefix

const AddPostPage = () => {
    const [title, setTitle] = useState('');
    const [content, setContent] = useState('');
    const navigate = useNavigate();

    const handleAddPost = async () => {
        try {
            // Call API to add a new post
            await axiosInstance.post(`${boardApiPrefix}/post`, {
                title,
                content
            });
            navigate('/'); // Redirect to post list after adding the post
        } catch (error) {
            console.error('Failed to add post:', error);
        }
    };

    return (
        <div className="max-w-3xl mx-auto p-6 bg-white shadow-lg rounded-lg">
            <h1 className="text-3xl font-bold mb-4">Add New Post</h1>
            <div className="mb-4">
                <input
                    type="text"
                    value={title}
                    onChange={(e) => setTitle(e.target.value)}
                    className="w-full p-2 border mb-2"
                    placeholder="Post Title"
                />
                <textarea
                    value={content}
                    onChange={(e) => setContent(e.target.value)}
                    className="w-full p-2 border"
                    placeholder="Post Content"
                />
            </div>
            <button 
                onClick={handleAddPost} 
                className="bg-green-500 text-white px-4 py-2 rounded"
            >
                Submit
            </button>
        </div>
    );
};

export default AddPostPage;
