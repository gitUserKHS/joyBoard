package com.me.board.dao;

import com.me.board.VO.UserVo;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
public class UserMapperTest {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserMapper userMapper;

    @Test
    void testInsertUser(){
        UserVo vo = UserVo.builder()
                .name("james")
                .password(bCryptPasswordEncoder.encode("1234"))
                .role("USER")
                .build();

        userMapper.insertUser(vo);
    }

    @Test
    void testSelectUserByName() {
        UserVo vo = userMapper.selectUserByName("james");
        vo.toString();
    }

    @Test
    void testDeleteUser(){
        userMapper.deleteUser("james");
    }

}
