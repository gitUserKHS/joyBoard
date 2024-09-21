import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import PostDetail from '../component/PostDetail';
import { getOne } from '../api/api';
import {getUserName, isLoggedIn} from './../util/loginUitl';

const initState = {
    id: 0,
    title: "",
    content: "",
    writtenAt: "",
    updatedAt: "",
    commentList: []
}

function PostPage(props) {
    const { postId } = useParams();
    const [post, setPost] = useState(initState);
    const [loaded, setLoaded] = useState(false);

    useEffect(() => {
        getOne(postId).then(data => {
            setPost(data);
            setLoaded(true);
        });
    }, [postId]);
    

    return (
        <div className="max-w-3xl mx-auto p-6 bg-gray-50 min-h-screen">
            {loaded ? 
            <PostDetail 
            post={post}
            postId={postId}
            _isLoggedIn={isLoggedIn()} 
            userName={getUserName()}
            /> : (
                <p className="text-center text-gray-500">Loading...</p>
            )}
        </div>
    );
}

export default PostPage;