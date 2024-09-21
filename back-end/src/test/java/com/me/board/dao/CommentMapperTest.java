package com.me.board.dao;

import com.me.board.VO.CommentVo;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
@Log4j2
public class CommentMapperTest {

    @Autowired
    private CommentMapper commentMapper;

    @Test
    void testInsertComment(){
        Date currentDate = new Date();

        CommentVo vo = CommentVo.builder()
                .postId(1)
                .content("test comment2")
                .writtenAt(currentDate)
                .build();

        commentMapper.insertComment(vo);
    }
}
