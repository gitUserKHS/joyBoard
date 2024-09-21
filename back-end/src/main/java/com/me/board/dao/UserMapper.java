package com.me.board.dao;

import com.me.board.VO.UserVo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    void insertUser(UserVo userVo);

    UserVo selectUserByName(String name);

    void deleteUser(String name);
}
