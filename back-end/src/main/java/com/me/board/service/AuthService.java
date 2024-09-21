package com.me.board.service;

import com.me.board.VO.UserVo;
import com.me.board.dto.LoginRequestDto;
import com.me.board.dto.RefreshTokenDto;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface AuthService {

    void join(UserVo userVo) throws Exception;

    String reIssue(String authStr, RefreshTokenDto refreshTokenDto);

    boolean logout(String authStr);
}
