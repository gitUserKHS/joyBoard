package com.me.board.dao;

import com.me.board.VO.PostVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PostMapper {

    int count();

    List<PostVo> selectPostsByOffsetAndSize(@Param("offset") int offset, @Param("size") int size);

    PostVo selectPostByIdWithCommentsAndReplies(int postId);

    void insertPost(PostVo postVo);

    String getUsernameById(int postId);

    void updatePost(PostVo postVo);

    void deletePost(int postId);
}
