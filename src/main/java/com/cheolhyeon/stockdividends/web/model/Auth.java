package com.cheolhyeon.stockdividends.web.model;

import com.cheolhyeon.stockdividends.web.domain.MemberEntity;
import lombok.Data;

import java.util.List;

public class Auth {
    // 로그인시 받을 요청 정보
    @Data
    public static class SignIn {
        private String username;
        private String password;

    }
    // 회원가입 시 받을 요청 정보
    @Data
    public static class SignUp {
        private String username;
        private String password;
        private List<String> roles;

        public MemberEntity toEntity() {
            return MemberEntity.builder()
                    .username(this.username)
                    .password(this.password)
                    .roles(this.roles)
                    .build();
        }

    }

}
