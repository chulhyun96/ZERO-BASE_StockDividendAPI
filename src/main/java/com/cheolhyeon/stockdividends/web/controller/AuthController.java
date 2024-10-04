package com.cheolhyeon.stockdividends.web.controller;

import com.cheolhyeon.stockdividends.web.domain.MemberEntity;
import com.cheolhyeon.stockdividends.web.model.Auth;
import com.cheolhyeon.stockdividends.web.security.TokenProvider;
import com.cheolhyeon.stockdividends.web.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {
    private final MemberService memberService;
    private final TokenProvider tokenProvider;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Auth.SignUp request) {
        //회원가입을 위한 API
        MemberEntity register = memberService.register(request);
        return ResponseEntity.ok(register);
    }
    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody Auth.SignIn request) {
        //로그인용 API
        MemberEntity user = memberService.authenticate(request);
        String token = tokenProvider.generateToken(user.getUsername(), user.getRoles());
        return ResponseEntity.ok(token);
    }
}
