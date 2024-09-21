# Mybatis로 게시판 서버 구현하기
개요: 스프링 부트 + MySQL + Redis를 활용하여 만든 게시판 서버입니다. 

### 개발 기간 
---
2024.08.31 ~ 2024.09.12 

### 개발 인원수
---
1인

### 개발 환경
---
- **JDK Version**: 21
- **IDE**: Intellij IDEA Community
- **Database Tool**: DBeaver

### 구현된 기능들
---
1. CRUD: 글(post), 댓글(comment), 대댓글(reply) 모두 쓰기, 읽기, 수정, 삭제 기능을 넣었습니다.
2. 페이지네이션: MySQL의 LIMIT문법을 활용하여 페이지에 해당하는 위치에서 페이지 크기만큼 데이터를 가져오도록 하였습니다.
3. JWT를 이용한 로그인 기능 구현: 로그인 시 accessToken과 refreshToken을 전송하여 클라이언트 측에서 refreshToken을 이용하여 accessToken을 재발급 받을 수 있도록 하였습니다.

### 포스트맨 실행 내용

페이지네이션 (1페이지 불러오기)
![getlist](https://github.com/user-attachments/assets/e446a10f-9e5f-4a44-b01f-57e5450b648b)

<details>
<summary>전체 응답 내용</summary>

```json
{
    "dtoList": [
        {
            "id": 112,
            "title": "hello!",
            "content": null,
            "writtenAt": "2020-09-24T20:09:23.000+00:00",
            "updatedAt": null,
            "commentList": null,
            "userName": "tester"
        },
     
     (....... 생략 .......)

        {
            "id": 103,
            "title": "hi!",
            "content": null,
            "writtenAt": "2010-09-24T10:09:23.000+00:00",
            "updatedAt": null,
            "commentList": null,
            "userName": null
        },
        {
            "id": 102,
            "title": "greeting100",
            "content": null,
            "writtenAt": "2024-09-08T13:19:58.000+00:00",
            "updatedAt": null,
            "commentList": null,
            "userName": null
        }
    ],
    "pageRequestDTO": {
        "page": 1,
        "size": 10
    },
    "prev": false,
    "next": true,
    "totalCount": 111,
    "prevPage": 0,
    "nextPage": 11,
    "totalPage": 0,
    "current": 1
}
```
</details>

<br>
로그인 (accessToken과 refreshToken 획득)

![login](https://github.com/user-attachments/assets/ad4eec25-3614-4297-81b8-2bd7e2077b2b)

<br>
accessToken 재발급

![reissue](https://github.com/user-attachments/assets/9cc0c07c-979b-4812-bb8d-57503b9770e5)
 
### ERD
---
1차 (로그인 구현 이전: user테이블 만들기 전)

![erd1차](https://github.com/user-attachments/assets/b4ac7463-00f1-4513-b86a-a9d2331d09ee)

2차 (로그인 구현 이후: user테이블 만든 후)

![erd2차](https://github.com/user-attachments/assets/145194a3-2568-4c9e-b6ab-79190ec6186f)

### API 설계

![api설계](https://github.com/user-attachments/assets/973f0b42-e39d-4440-a648-cccdc40e1aa3)

### ⭐문제상황 및 어려웠던 점
---
1. mybatis의 mapper xml파일에서 1:N 관계의 데이터를 어떻게 처리할 것인가:<br> post-mapper.xml파일에서 글(post)과 글에 달린 댓글(comment)과 대댓글(reply)들을 어떻게 함께 불러오게끔 할 수 있을까에 대한 고민을 하였습니다.<br> 그 결과, resultMap안에 collection을 넣어 자식 테이블의 데이터들을 담을 수 있게 하였으며, sql의 join문법을 두 번 활용함으로써 댓글과 대댓글을 불러올 수 있었습니다.
<details>
<summary>post-mapper.xml</summary>

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="com.me.board.dao.PostMapper">

    <resultMap id="reply" type="ReplyVo">
        <id property="id" column="reply_id"/>
        <result property="content" column="reply_content"/>
        <result property="writtenAt" column="reply_written_at"/>
        <result property="updatedAt" column="reply_updated_at"/>
        <result property="commentId" column="reply_comment_id"/>
        <result property="userName" column="reply_user_name"/>
    </resultMap>
    
    <resultMap id="comment" type="CommentVo">
        <id property="id" column="comment_id"/>
        <result property="content" column="comment_content"/>
        <result property="writtenAt" column="comment_written_at"/>
        <result property="updatedAt" column="comment_updated_at"/>
        <result property="postId" column="comment_post_id"/>
        <result property="userName" column="comment_user_name"/>
        <collection property="replyList" column="commentId" resultMap="reply"/>
    </resultMap>

    <resultMap id="postWithComments" type="PostVo">
        <id property="id" column="post_id"/>
        <result property="title" column="post_title"/>
        <result property="content" column="post_content"/>
        <result property="writtenAt" column="post_written_at"/>
        <result property="updatedAt" column="post_updated_at"/>
        <result property="userName" column="post_user_name"/>
        <collection property="commentList" column="postId" resultMap="comment"/>
    </resultMap>

    <select id="count" resultType="integer">
        SELECT COUNT(*) FROM post
    </select>

    <select id="selectPostsByOffsetAndSize" parameterType="map" resultType="PostVo">
        SELECT id, title, written_at, user_name
        FROM post
        ORDER BY id DESC
        LIMIT #{offset}, #{size}
    </select>

    <select id="selectPostByIdWithCommentsAndReplies" resultMap="postWithComments">
        SELECT
        p.id as post_id,
        p.title as post_title,
        p.content as post_content,
        p.written_at as post_written_at,
        p.updated_at as post_updated_at,
        p.user_name as post_user_name,
        c.id as comment_id,
        c.content as comment_content,
        c.written_at as comment_written_at,
        c.updated_at as comment_updated_at,
        c.post_id as comment_post_id,
        c.user_name as comment_user_name,
        r.id as reply_id,
        r.content as reply_content,
        r.written_at as reply_written_at,
        r.updated_at as reply_updated_at,
        r.comment_id as reply_comment_id,
        r.user_name as reply_user_name
        FROM post p
        LEFT JOIN comment c ON p.id = c.post_id
        LEFT JOIN reply r ON c.id = r.comment_id
        WHERE p.id = #{postId}
    </select>

    <select id="getUsernameById" parameterType="int" resultType="String">
        SELECT user_name FROM post WHERE id = #{postId}
    </select>

    <insert id="insertPost" parameterType="PostVo">
        INSERT INTO post (title, content, written_at, updated_at, user_name)
        VALUES (#{title}, #{content}, #{writtenAt}, #{updatedAt}, #{userName, jdbcType=VARCHAR})
    </insert>

    <update id="updatePost" parameterType="PostVo">
        UPDATE post SET title = #{title}, content = #{content}, updated_at = #{updatedAt}
        WHERE id = #{id}
    </update>

    <delete id="deletePost" parameterType="int">
        DELETE FROM post WHERE id = #{postId}
    </delete>

</mapper>
```
</details>
<br>

2. 로그인을 구현할 때 세션 방식과 토큰 방식 중 어느 방식을 선택할 것인가: <br>
세션 방식의 경우 요청이 들어올 때마다 세션 id값으로 데이터베이스에 매번 확인을 해야하기 때문에, 토큰의 유효성 확인 과정만 거치면 되는 토큰 방식을 선택하였습니다.

3. accessToken이 탈취당할 경우는 어떻게 대비할 것인가: <br> accessToken의 유효시간을 30분으로 짧게 설정하고 refreshToken의 유효시간을 24시간으로 길게 설정하였으며, accessToken이 만료되었을 때, 클라이언트 측에서 accessToken 재발급 요청을 하게끔 함으로써 요청에 담긴 refreshToken값을 서버측의 redis에 저장된 refreshToken값과 비교하여 일치하면 accessToken을 재발급 하는 방식으로 구현하였습니다.
<details>
<summary>AuthServiceImpl.java</summary>

```java
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
```
</details>
<details>
    <summary>LoginFilter.java</summary>

```java
package com.me.board.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.me.board.dto.CustomUserDetails;
import com.me.board.dto.LoginRequestDto;
import com.me.board.dto.RefreshTokenDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

@RequiredArgsConstructor
@Log4j2
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        ObjectMapper om = new ObjectMapper();
        LoginRequestDto loginRequestDto = null;

        try {
            loginRequestDto = om.readValue(request.getInputStream(), LoginRequestDto.class);
        }catch (Exception e) {
            e.printStackTrace();
        }

        String userName = loginRequestDto.getId();
        String password = loginRequestDto.getPassword();

//        log.info("username " + userName);
//        log.info("password " + password);

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                userName, password, null
        );

        return authenticationManager.authenticate(usernamePasswordAuthenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        CustomUserDetails customUserDetails = (CustomUserDetails) authResult.getPrincipal();
        String userName = customUserDetails.getUsername();

        Collection<? extends GrantedAuthority> authorities = authResult.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        String accessToken = jwtUtil.createAccessToken(userName, role);
        String refreshToken = jwtUtil.createRefreshToken(userName, role);

        response.addHeader("Authorization", "Bearer " + accessToken);

        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        RefreshTokenDto dto = RefreshTokenDto.builder().refreshToken(refreshToken).build();
        String dtoStr = objectMapper.writeValueAsString(dto);
        response.getWriter().write(dtoStr);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        response.setStatus(401);
    }
}

```
</details>