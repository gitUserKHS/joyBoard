package com.me.board.service;

import com.me.board.VO.CommentVo;
import com.me.board.VO.PostVo;
import com.me.board.VO.ReplyVo;
import com.me.board.VO.UserVo;
import com.me.board.dao.CommentMapper;
import com.me.board.dao.PostMapper;
import com.me.board.dao.ReplyMapper;
import com.me.board.dto.PageRequestDto;
import com.me.board.dto.PageResponseDto;
import com.me.board.dto.board.CommentRequestDto;
import com.me.board.dto.board.ReplyRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final PostMapper postMapper;
    private final CommentMapper commentMapper;
    private final ReplyMapper replyMapper;

    @Override
    public PageResponseDto<PostVo> getPostList(PageRequestDto pageRequestDto) {
        int offset = (pageRequestDto.getPage() - 1) * pageRequestDto.getSize();

        List<PostVo> postVos = postMapper.selectPostsByOffsetAndSize(
                offset, pageRequestDto.getSize());

        long totalCount = postMapper.count();

        return new PageResponseDto<PostVo>(postVos, pageRequestDto, totalCount);

    }

    @Override
    public PostVo getSinglePost(int postId) {
        return postMapper.selectPostByIdWithCommentsAndReplies(postId);
    }

    @Override
    public void addPost(PostVo postVo) {
        postVo.setWrittenAt(new Date());
        postMapper.insertPost(postVo);
    }

    @Override
    public boolean updatePost(int postId, PostVo postVo, UserVo userVo) {
        postVo.setId(postId);
        postVo.setUpdatedAt(new Date());

        String userName = postMapper.getUsernameById(postId);

        if(userName != null && !userName.equals(userVo.getName()))
            return false;

        postMapper.updatePost(postVo);
        return true;
    }

    @Override
    public boolean deletePost(int postId, UserVo userVo) {
        String userName = postMapper.getUsernameById(postId);
        if(userName != null && !userName.equals(userVo.getName()))
            return false;

        postMapper.deletePost(postId);
        return true;
    }

    @Override
    public void addComment(CommentRequestDto commentRequestDto, UserVo userVo) {
        commentMapper.insertComment(CommentVo.builder()
                .content(commentRequestDto.getContent())
                .postId(commentRequestDto.getPostId())
                .writtenAt(new Date())
                .userName(userVo.getName())
                .build());
    }

    @Override
    public boolean updateComment(long commentId, CommentRequestDto commentRequestDto, UserVo userVo) {
        String userName = commentMapper.getUsernameById(commentId);
        if(userName != null && !userName.equals(userVo.getName()))
            return false;

        commentMapper.updateComment(CommentVo.builder()
                .id(commentId)
                .updatedAt(new Date())
                .content(commentRequestDto.getContent())
                .build());
        return true;
    }

    @Override
    public boolean deleteComment(long commentId, UserVo userVo) {
        String userName = commentMapper.getUsernameById(commentId);
        if(userName != null && !userName.equals(userVo.getName()))
            return false;

        commentMapper.deleteComment(commentId);
        return true;
    }

    @Override
    public void addReply(ReplyRequestDto replyRequestDto, UserVo userVo) {
        replyMapper.insertReply(ReplyVo.builder()
                .content(replyRequestDto.getContent())
                .writtenAt(new Date())
                .commentId(replyRequestDto.getCommentId())
                .userName(userVo.getName())
                .build());
    }

    @Override
    public boolean updateReply(long replyId, ReplyRequestDto replyRequestDto, UserVo userVo) {
        String userName = replyMapper.getUsernameById(replyId);
        if(userName != null && !userName.equals(userVo.getName()))
            return false;

        replyMapper.updateReply(ReplyVo.builder()
                .id(replyId)
                .updatedAt(new Date())
                .content(replyRequestDto.getContent())
                .build());
        return true;
    }

    @Override
    public boolean deleteReply(long replyId, UserVo userVo) {
        String userName = replyMapper.getUsernameById(replyId);
        if(userName != null && !userName.equals(userVo.getName()))
            return false;

        replyMapper.deleteReply(replyId);
        return true;
    }
}
