import React, { useState } from 'react';
import axiosInstance from '../api/authApi';
import { API_SERVER_HOST, boardApiPrefix } from '../api/api';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

// 시간 포맷팅 함수
const formatDate = (dateString) => {
  const options = { year: 'numeric', month: 'long', day: 'numeric', hour: '2-digit', minute: '2-digit' };
  return new Date(dateString).toLocaleDateString(undefined, options);
};

const Comment = ({ comment, currentUser, isLoggedIn, onUpdateComment, onDeleteComment, onReply, onUpdateReply, onDeleteReply }) => {
  const [isEditing, setIsEditing] = useState(false);
  const [newContent, setNewContent] = useState(comment.content);
  const [replyContent, setReplyContent] = useState('');

  // 댓글 수정
  const handleUpdate = () => {
    onUpdateComment(comment.id, newContent);
    setIsEditing(false);
  };

  // 댓글 삭제
  const handleDelete = () => {
    onDeleteComment(comment.id);
  };

  // 대댓글 추가
  const handleReply = () => {
    onReply(comment.id, replyContent);
    setReplyContent('');
  };

  return (
    <div className="ml-5 border-l border-gray-300 pl-4 mb-4">
      <p>
        <strong>{comment.userName}</strong> commented: {isEditing ? (
          <input
            type="text"
            value={newContent}
            onChange={(e) => setNewContent(e.target.value)}
            className="ml-2 border"
          />
        ) : (
          comment.content
        )}
      </p>
      <p className="text-sm text-gray-500">
        Written at: {formatDate(comment.writtenAt)}
        {comment.updatedAt && ` (Updated at: ${formatDate(comment.updatedAt)})`}
      </p>

      {isLoggedIn && currentUser === comment.userName && (
        <div>
          {isEditing ? (
            <>
              <button onClick={handleUpdate} className="mr-2 bg-blue-500 text-white px-2 py-1 rounded">Save</button>
              <button onClick={() => setIsEditing(false)} className="bg-gray-500 text-white px-2 py-1 rounded">Cancel</button>
            </>
          ) : (
            <>
              <button onClick={() => setIsEditing(true)} className="mr-2 bg-blue-500 text-white px-2 py-1 rounded">Edit</button>
              <button onClick={handleDelete} className="bg-red-500 text-white px-2 py-1 rounded">Delete</button>
            </>
          )}
        </div>
      )}

      {isLoggedIn && (
        <div className="mt-2">
          <input
            type="text"
            value={replyContent}
            onChange={(e) => setReplyContent(e.target.value)}
            placeholder="Write a reply..."
            className="border px-2 py-1 mr-2"
          />
          <button onClick={handleReply} className="bg-green-500 text-white px-2 py-1 rounded">Reply</button>
        </div>
      )}

      {/* 대댓글 리스트 */}
      {comment.replyList && comment.replyList.length > 0 && (
        <div className="mt-2">
          <strong>Replies:</strong>
          {comment.replyList.map(reply => (
            <Reply
              key={reply.id}
              reply={reply}
              currentUser={currentUser}
              isLoggedIn={isLoggedIn}
              onUpdateReply={onUpdateReply}
              onDeleteReply={onDeleteReply}
            />
          ))}
        </div>
      )}
    </div>
  );
};

// 대댓글 컴포넌트
const Reply = ({ reply, currentUser, isLoggedIn, onUpdateReply, onDeleteReply }) => {
  const [isEditingReply, setIsEditingReply] = useState(false);
  const [newReplyContent, setNewReplyContent] = useState(reply.content);

  // 대댓글 수정
  const handleUpdateReply = () => {
    onUpdateReply(reply.id, newReplyContent);
    setIsEditingReply(false);
  };

  // 대댓글 삭제
  const handleDeleteReply = () => {
    onDeleteReply(reply.id);
  };

  return (
    <div className="ml-5 mt-2">
      <p>
        <strong>{reply.userName}</strong> replied: {isEditingReply ? (
          <input
            type="text"
            value={newReplyContent}
            onChange={(e) => setNewReplyContent(e.target.value)}
            className="ml-2 border"
          />
        ) : (
          reply.content
        )}
      </p>
      <p className="text-sm text-gray-500">
        Written at: {formatDate(reply.writtenAt)}
        {reply.updatedAt && ` (Updated at: ${formatDate(reply.updatedAt)})`}
      </p>

      {isLoggedIn && currentUser === reply.userName && (
        <div>
          {isEditingReply ? (
            <>
              <button onClick={handleUpdateReply} className="mr-2 bg-blue-500 text-white px-2 py-1 rounded">Save</button>
              <button onClick={() => setIsEditingReply(false)} className="bg-gray-500 text-white px-2 py-1 rounded">Cancel</button>
            </>
          ) : (
            <>
              <button onClick={() => setIsEditingReply(true)} className="mr-2 bg-blue-500 text-white px-2 py-1 rounded">Edit</button>
              <button onClick={handleDeleteReply} className="bg-red-500 text-white px-2 py-1 rounded">Delete</button>
            </>
          )}
        </div>
      )}
    </div>
  );
};

const PostDetail = ({ post, postId, _isLoggedIn, userName }) => {
    const [comments, setComments] = useState(post.commentList);
    const [newComment, setNewComment] = useState('');
    const [isEditingPost, setIsEditingPost] = useState(false);
    const [postContent, setPostContent] = useState(post.content);
    const [postTitle, setPostTitle] = useState(post.title); // 제목 상태 추가
  
    // 로그인 상태 및 현재 사용자 설정
    const [isLoggedIn] = useState(_isLoggedIn);  // 로그인 여부 설정
    const [currentUser] = useState(userName);    // 로그인된 사용자 이름 설정

    const navigate = useNavigate();
  
    // 게시글 수정 함수
    const handleUpdatePost = async () => {
      try {
        // 게시글 제목과 내용 함께 수정
        await axiosInstance.put(`${boardApiPrefix}/post/${postId}`, {
          title: postTitle,  // 제목도 함께 보냄
          content: postContent,
        });
        setIsEditingPost(false);
      } catch (error) {
        console.error('Failed to update post:', error);
      }
    };
  
    // 게시글 삭제 함수
    const handleDeletePost = async () => {
      try {
        await axiosInstance.delete(`${boardApiPrefix}/post/${postId}`);
        console.log("Post deleted");
        navigate("/");
      } catch (error) {
        console.error('Failed to delete post:', error);
      }
    };
  
    // 댓글 추가 함수
    const handleAddComment = async () => {
      const newCommentObj = {
        content: newComment,
        postId: postId
      };
  
      await axiosInstance.post(`${boardApiPrefix}/comment`, newCommentObj);
  
      newCommentObj.id = comments.length + 1;
      newCommentObj.userName = currentUser;
      newCommentObj.replyList = [];
      newCommentObj.writtenAt = new Date().toISOString();
  
      setComments([...comments, newCommentObj]);
      setNewComment('');
    };
  
    // 댓글 수정 함수
    const handleUpdateComment = async (commentId, newContent) => {
      await axiosInstance.put(`${boardApiPrefix}/comment/${commentId}`, { content: newContent, postId: postId });
  
      setComments(comments.map(comment =>
        comment.id === commentId ? { ...comment, content: newContent, updatedAt: new Date().toISOString() } : comment
      ));
    };
  
    // 댓글 삭제 함수
    const handleDeleteComment = async (commentId) => {
      await axiosInstance.delete(`${boardApiPrefix}/comment/${commentId}`);
  
      setComments(comments.filter(comment => comment.id !== commentId));
    };
  
    // 댓글에 대한 답글 함수
    const handleReplyToComment = async (commentId, replyContent) => {
      const response = await axiosInstance.post(`${boardApiPrefix}/reply`, { content: replyContent, commentId: commentId });
      const newReply = response.data;

      setComments(comments.map(comment =>
        comment.id === commentId ? {
          ...comment,
          replyList: [...comment.replyList, {
            ...newReply,
            userName: currentUser,
            writtenAt: new Date().toISOString(),
          }]
        } : comment
      ));
    };

    const handleUpdateReply = async(replyId, newContent) => {
        await axiosInstance.put(`${boardApiPrefix}/reply/${replyId}`, { content: newContent});
        setComments(comments.map(comment => ({
            ...comment,
            replyList: comment.replyList.map(reply => 
                reply.id === replyId ? { ...reply, content: newContent, updatedAt: new Date().toISOString() } : reply
            )
        })));
    }

    const handleDeleteReply = async(replyId) => {
        await axiosInstance.delete(`${boardApiPrefix}/reply/${replyId}`);
        setComments(comments.map(comment => ({
            ...comment,
            replyList: comment.replyList.filter(reply => reply.id !== replyId)
        })));
    }
  
    return (
      <div className="max-w-3xl mx-auto p-6 bg-white shadow-lg rounded-lg">
        {isEditingPost ? (
          <>
            {/* 제목 수정 필드 */}
            <input
              type="text"
              value={postTitle}
              onChange={(e) => setPostTitle(e.target.value)}
              className="w-full p-2 border mb-2"
              placeholder="Edit the title"
            />
            {/* 내용 수정 필드 */}
            <textarea
              value={postContent}
              onChange={(e) => setPostContent(e.target.value)}
              className="w-full p-2 border"
            />
            <button onClick={handleUpdatePost} className="bg-blue-500 text-white px-4 py-2 mt-2 rounded">Save</button>
          </>
        ) : (
          <>
            <h1 className="text-3xl font-bold mb-4">{postTitle}</h1>
            <p className="mb-4">{postContent}</p>
            <p className="text-sm text-gray-500">
              <strong>{post.userName}</strong> wrote this post on {formatDate(post.writtenAt)}
              {post.updatedAt && ` (Updated at: ${formatDate(post.updatedAt)})`}
            </p>
            {isLoggedIn && currentUser === post.userName && (
              <>
                <button onClick={() => setIsEditingPost(true)} className="mr-2 bg-blue-500 text-white px-2 py-1 rounded">Edit Post</button>
                <button onClick={handleDeletePost} className="bg-red-500 text-white px-2 py-1 rounded">Delete Post</button>
              </>
            )}
          </>
        )}
  
        <h2 className="text-2xl font-semibold mt-6 mb-4">Comments</h2>
        {comments.length > 0 ? (
          comments.map(comment => (
            <Comment
              key={comment.id}
              comment={comment}
              currentUser={currentUser}
              isLoggedIn={isLoggedIn}
              onUpdateComment={handleUpdateComment}
              onDeleteComment={handleDeleteComment}
              onReply={handleReplyToComment}
              onUpdateReply={handleUpdateReply}
              onDeleteReply={handleDeleteReply}
            />
          ))
        ) : (
          <p className="text-gray-500">No comments yet.</p>
        )}
  
        {/* 댓글 추가 기능 */}
        {isLoggedIn && (
          <div className="mt-4">
            <textarea
              value={newComment}
              onChange={(e) => setNewComment(e.target.value)}
              placeholder="Write a comment..."
              className="w-full p-2 border"
            />
            <button onClick={handleAddComment} className="bg-green-500 text-white px-4 py-2 mt-2 rounded">Add Comment</button>
          </div>
        )}
      </div>
    );
  };

export default PostDetail;
