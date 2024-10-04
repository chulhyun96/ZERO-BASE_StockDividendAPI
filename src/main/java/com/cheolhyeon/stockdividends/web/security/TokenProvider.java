package com.cheolhyeon.stockdividends.web.security;

import com.cheolhyeon.stockdividends.web.service.MemberService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TokenProvider {

    private static final long TOKEN_EXPIRATION_TIME = 60 * 60 * 1000 * 5; // 5hour

    private static final String KEY_ROLES = "roles";

    private final MemberService memberService;

    @Value("${spring.jwt.secret}")
    private String secretKey;

    /**
     * 토큰 생성(발급)
     * @param username
     * @param roles
     * @return
     */

    public String generateToken(String username, List<String> roles) {
        // 사용자의 권한정보를 저장하기 위한 Claims
        Claims claims = Jwts.claims().setSubject(username);
        claims.put(KEY_ROLES, roles);
        // 토큰이 생성된 시간
        Date now = new Date();
        // 토큰이 만료되는 시간
        Date expiredDate = new Date(now.getTime() + TOKEN_EXPIRATION_TIME);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now) // 토큰 생성시간
                .setExpiration(expiredDate) // 토큰 만료 시간
                .signWith(SignatureAlgorithm.HS512, this.secretKey) // 사용할 암호화 알고리즘
                .compact();
    }
    // jwt 로 부터 인증 정보를 가지고 오기
    public Authentication getAuthentication(String jwt) {
        UserDetails userDetails = memberService.loadUserByUsername(getUsername(jwt));
        // 사용자의 정보와 사용자의 권한정보를 넘김
        return new UsernamePasswordAuthenticationToken(userDetails,"", userDetails.getAuthorities());
    }
    public String getUsername(String token) {
        return parseClaims(token).getSubject();
    }
    public boolean validateToken(String token) {
        if (!StringUtils.hasText(token)) return false;
        Claims claims = parseClaims(token);
        return !claims.getExpiration().before(new Date()); // 현재 토큰의 만료시간이 이전인지 아닌지 check
    }
    // 토큰이 유효한지 검증
    private Claims parseClaims(String token) {
        // 토큰 시간이 만료된 상태에서 파싱할 경우 에러가 날 수 있음
        try {
            return Jwts.parser().setSigningKey(this.secretKey).parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e)  {
            return e.getClaims();
        }
    }
}
