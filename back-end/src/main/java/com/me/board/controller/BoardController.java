package com.me.board.controller;

import com.me.board.VO.PostVo;
import com.me.board.VO.UserVo;
import com.me.board.dto.PageRequestDto;
import com.me.board.dto.PageResponseDto;
import com.me.board.dto.board.CommentRequestDto;
import com.me.board.dto.board.ReplyRequestDto;
import com.me.board.jwt.JwtUtil;
import com.me.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("api/board")
public class BoardController {

    private final BoardService boardService;
    private final JwtUtil jwtUtil;

    private final String authorizationHeader = "Authorization";

    private UserVo getUserVo(String authStr){
        Authentication auth = jwtUtil.getAuthenticationWrapper(authStr, true);
        if(auth == null)
            return null;

        return (UserVo) auth.getPrincipal();
    }

    // 글 관련

    @GetMapping("list")
    public PageResponseDto<PostVo> getPosts(PageRequestDto pageRequestDto){
        return boardService.getPostList(pageRequestDto);
    }

    @GetMapping("post/{postId}")
    public PostVo getSinglePost(@PathVariable("postId") int postId){
        return boardService.getSinglePost(postId);
    }

    @PostMapping("post")
    public Map<String, String> addPost(@RequestHeader Map<String, String> headerMap, @RequestBody PostVo postVo){
        String authStr =  headerMap.get("authorization");

        UserVo userVo = getUserVo(authStr);
        if(userVo == null)
            return Map.of("result", "fail");

        postVo.setUserName(userVo.getName());

        boardService.addPost(postVo);
        return Map.of("result", "success");
    }

    @PutMapping("post/{postId}")
    public Map<String, String> updatePost(@RequestHeader(authorizationHeader) String authStr,  @PathVariable("postId") int postId,
                                          @RequestBody PostVo postVo){
        UserVo userVo = getUserVo(authStr);
        if(userVo == null || !boardService.updatePost(postId, postVo, userVo))
            return Map.of("result", "fail");

        return Map.of("result", "success");
    }

    @DeleteMapping("post/{postId}")
    public Map<String, String> updatePost(@RequestHeader(authorizationHeader) String authStr, @PathVariable("postId") int postId) {
        UserVo userVo = getUserVo(authStr);
        if(userVo == null || !boardService.deletePost(postId, userVo))
            return Map.of("result", "fail");

        return Map.of("result", "success");
    }

    // 댓글 관련

    @PostMapping("comment")
    public Map<String, String> addComment(@RequestHeader(authorizationHeader) String authStr, @RequestBody CommentRequestDto commentRequestDto){
        UserVo userVo = getUserVo(authStr);
        if(userVo == null)
            return Map.of("result", "fail");

        boardService.addComment(commentRequestDto, userVo);
        return Map.of("result", "success");
    }

    @PutMapping("comment/{commentId}")
    public Map<String, String> updateComment(@RequestHeader(authorizationHeader) String authStr, @RequestBody CommentRequestDto commentRequestDto,
                                             @PathVariable("commentId") long commentId){
        UserVo userVo = getUserVo(authStr);
        if(userVo == null || !boardService.updateComment(commentId, commentRequestDto, userVo))
            return Map.of("result", "fail");

        return Map.of("result", "success");
    }

    @DeleteMapping("comment/{commentId}")
    public Map<String, String> deleteComment(@RequestHeader(authorizationHeader) String authStr, @PathVariable("commentId") long commentId){
        UserVo userVo = getUserVo(authStr);
        if(userVo == null || !boardService.deleteComment(commentId, userVo))
            return Map.of("result", "fail");

        return Map.of("result", "success");
    }

    // 답글 관련

    @PostMapping("reply")
    public Map<String, String> addReply(@RequestHeader(authorizationHeader) String authStr, @RequestBody ReplyRequestDto replyRequestDto){
        UserVo userVo = getUserVo(authStr);
        if(userVo == null)
            return Map.of("result", "fail");

        boardService.addReply(replyRequestDto, userVo);
        return Map.of("result", "success");
    }

    @PutMapping("reply/{replyId}")
    public Map<String, String> updateReply(@RequestHeader(authorizationHeader) String authStr, @RequestBody ReplyRequestDto replyRequestDto,
                                             @PathVariable("replyId") long replyId){
        UserVo userVo = getUserVo(authStr);
        if(userVo == null || !boardService.updateReply(replyId, replyRequestDto, userVo))
            return Map.of("result", "fail");

        return Map.of("result", "success");
    }

    @DeleteMapping("reply/{replyId}")
    public Map<String, String> deleteReply(@RequestHeader(authorizationHeader) String authStr, @PathVariable("replyId") long replyId){
        UserVo userVo = getUserVo(authStr);
        if(userVo == null || !boardService.deleteReply(replyId, userVo))
            return Map.of("result", "fail");

        return Map.of("result", "success");
    }
}
