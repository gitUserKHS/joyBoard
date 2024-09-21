package com.me.board.service;

import com.me.board.VO.UserVo;
import com.me.board.dao.UserMapper;
import com.me.board.dto.LoginRequestDto;
import com.me.board.dto.RefreshTokenDto;
import com.me.board.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{

    private final UserMapper userMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;


    @Override
    public void join(UserVo userVo) throws Exception{
        userVo.setPassword(bCryptPasswordEncoder.encode(userVo.getPassword()));

        if(null != userMapper.selectUserByName(userVo.getName()))
            throw new Exception();

        userMapper.insertUser(userVo);
    }

    @Override
    public String reIssue(String authStr, RefreshTokenDto refreshTokenDto) {
        Authentication authentication = jwtUtil.getAuthenticationWrapper(authStr, false);
        UserVo userVo = (UserVo) authentication.getPrincipal();

        if(null == redisTemplate.opsForValue().get(userVo.getName()))
            return null;

        if(!redisTemplate.opsForValue().get(userVo.getName()).equals(refreshTokenDto.getRefreshToken()))
            return null;

        return jwtUtil.createAccessToken(userVo.getName(), userVo.getRole());
    }

    @Override
    public boolean logout(String authStr) {
        Authentication auth = jwtUtil.getAuthenticationWrapper(authStr, true);
        if(auth == null)
            return false;

        UserVo userVo = (UserVo) auth.getPrincipal();

        if (redisTemplate.opsForValue().get(userVo.getName())!= null){
            redisTemplate.delete(auth.getName());
        }

        Long expiration = jwtUtil.getExpiration(jwtUtil.getTokenString(authStr));
        if(expiration == null)
            return false;

        redisTemplate.opsForValue().set(jwtUtil.getTokenString(authStr),"logout", expiration, TimeUnit.MILLISECONDS);
        return true;
    }

}
