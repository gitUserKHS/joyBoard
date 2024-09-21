package com.me.board.dao;

import com.me.board.VO.CommentVo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentMapper {

    void insertComment(CommentVo commentVo);

    String getUsernameById(long commentId);

    void updateComment(CommentVo commentVo);

    void deleteComment(long commentId);

}
