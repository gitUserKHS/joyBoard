package com.me.board.controller;

import com.me.board.VO.UserVo;
import com.me.board.dto.LoginRequestDto;
import com.me.board.dto.RefreshTokenDto;
import com.me.board.service.AuthService;
import com.me.board.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Log4j2
public class AuthController {

    private final AuthService authService;

    @PostMapping("join")
    public Map<String, String> join(@RequestBody UserVo userVo) {
        try {
            authService.join(userVo);
        } catch (Exception e) {
            return Map.of("join", "fail");
        }
        return Map.of("join", "success");
    }

    @PostMapping("login")
    public String login(@RequestBody LoginRequestDto loginRequestDto){
        return "login success";
    }

    @PostMapping("reissue")
    public ResponseEntity<Map<String, String>> reIssue(@RequestHeader("Authorization") String authStr, @RequestBody RefreshTokenDto refreshTokenDto){
        String accessTok = authService.reIssue(authStr, refreshTokenDto);

        if(accessTok == null)
            return ResponseEntity.badRequest().body(Map.of("reissue", "fail"));

        accessTok = "Bearer " + accessTok;

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Authorization", accessTok);

        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(Map.of("reissue", "success"));
    }

    @PostMapping("log-out")
    public Map<String, String> logout(@RequestHeader("Authorization") String authStr){
        if(!authService.logout(authStr))
            return Map.of("logout", "fail");

        return Map.of("logout", "success");
    }

    @GetMapping("check")
    public String test(){
        return "test";
    }
}
