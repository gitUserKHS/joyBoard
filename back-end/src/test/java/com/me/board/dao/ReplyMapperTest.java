package com.me.board.dao;

import com.me.board.VO.ReplyVo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
public class ReplyMapperTest {

    @Autowired
    private ReplyMapper replyMapper;

    @Test
    void testInsertReply(){
        Date currentDate = new Date();

        ReplyVo vo = ReplyVo.builder()
                .content("test reply")
                .writtenAt(currentDate)
                .commentId(1)
                .build();

        replyMapper.insertReply(vo);
    }

}
