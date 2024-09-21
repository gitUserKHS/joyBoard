package com.me.board.jwt;

import com.me.board.VO.UserVo;
import com.me.board.dto.CustomUserDetails;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authorization = request.getHeader("Authorization");

        if(authorization == null || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorization.split(" ")[1];

        try{
            boolean expired = jwtUtil.isExpired((token));
        }catch (ExpiredJwtException e){
            response.setStatus(401);
            return;
        }

//        if(jwtUtil.isExpired(token)){
//            filterChain.doFilter(request, response);
//            return;
//        }

        UserVo userVo = UserVo.builder()
                .name(jwtUtil.getUserName(token))
                .password("temp")
                .role(jwtUtil.getRole(token))
                .build();

        CustomUserDetails userDetails = CustomUserDetails.builder().userVo(userVo).build();

        Authentication authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}
