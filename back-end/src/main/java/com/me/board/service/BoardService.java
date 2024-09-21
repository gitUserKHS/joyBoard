package com.me.board.service;

import com.me.board.VO.PostVo;
import com.me.board.VO.UserVo;
import com.me.board.dto.PageRequestDto;
import com.me.board.dto.PageResponseDto;
import com.me.board.dto.board.CommentRequestDto;
import com.me.board.dto.board.ReplyRequestDto;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface BoardService {

    PageResponseDto<PostVo> getPostList(PageRequestDto pageRequestDto);

    PostVo getSinglePost(int postId);

    void addPost(PostVo postVo);

    boolean updatePost(int postId, PostVo postVo, UserVo userVo);

    boolean deletePost(int postId, UserVo userVo);

    void addComment(CommentRequestDto commentRequestDto, UserVo userVo);

    boolean updateComment(long commentId, CommentRequestDto commentRequestDto, UserVo userVo);

    boolean deleteComment(long commentId, UserVo userVo);

    void addReply(ReplyRequestDto replyRequestDto, UserVo userVo);

    boolean updateReply(long replyId, ReplyRequestDto replyRequestDto, UserVo userVo);

    boolean deleteReply(long replyId, UserVo userVo);
}
