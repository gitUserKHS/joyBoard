package com.me.board.dao;

import com.me.board.VO.PostVo;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import java.util.Date;
import java.util.List;

@SpringBootTest
@Log4j2
public class PostMapperTest {

    @Autowired
    private PostMapper postMapper;

    @Test
    void testInsert(){
        Date currentDate = new Date();

        PostVo vo = PostVo.builder()
                .title("test title")
                .content("test content")
                .writtenAt(currentDate)
                .build();

        postMapper.insertPost(vo);

    }

    @Test
    void testSelectPostByIdWithCommentsAndReplies(){
        PostVo vo = postMapper.selectPostByIdWithCommentsAndReplies(2);
        log.info(vo.toString());
    }


    @Test
    void testCount(){
        int count = postMapper.count();
        log.info("testCount result: " + count);
    }

    @Test
    void insertManyPosts(){
        for(int i = 1; i <= 100; i++){
            var currentDate = new Date();

            PostVo vo = PostVo.builder()
                    .title("greeting" + i)
                    .content("hello world" + i)
                    .writtenAt(currentDate)
                    .build();

            postMapper.insertPost(vo);
        }
    }

    @Test
    void testGetPage(){
        StopWatch stopWatch = new StopWatch();

        stopWatch.start();
        List<PostVo> vos = postMapper.selectPostsByOffsetAndSize(9900, 5);
        stopWatch.stop();

        log.info(stopWatch.prettyPrint());
    }

}
