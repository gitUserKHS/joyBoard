package com.me.board.service;

import com.me.board.VO.UserVo;
import com.me.board.dao.UserMapper;
import com.me.board.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserVo vo = userMapper.selectUserByName(username);

        if(vo == null)
            throw new UsernameNotFoundException("user not found");

        return CustomUserDetails.builder().userVo(vo).build();
    }
}
