package com.me.board.dao;

import com.me.board.VO.ReplyVo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ReplyMapper {

    void insertReply(ReplyVo replyVo);

    String getUsernameById(long replyId);

    void updateReply(ReplyVo replyVo);

    void deleteReply(long replyId);
}
