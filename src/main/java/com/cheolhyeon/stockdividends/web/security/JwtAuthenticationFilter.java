package com.cheolhyeon.stockdividends.web.security;

import io.netty.util.internal.ObjectUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor                    // 한 요청당 한번 필터가 실행됨.
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    // 토큰은 Http 헤더에 포함됨, 어떤 키로 토큰을 주고받을지에 대한 키값
    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer "; // Bearer xxxx-yyyy.zzz 로 들어옴

    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 요청에 토큰을 포함시켜서 해당 토큰이 유효한지 아닌지 확인
        String token = resolveTokenFromRequest(request);

        //null 이 아니고 값이 아니며 유효한지 검증
        if (StringUtils.hasText(token) && tokenProvider.validateToken(token)) {
            Authentication auth = tokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        filterChain.doFilter(request, response);
    }
    private String resolveTokenFromRequest(HttpServletRequest request) {
        // HttpServlet으로 부터 토큰을 가지고옴
        String token = request.getHeader(TOKEN_HEADER);
        // 토큰이 존재하면서 Totekn Prefix로 시작을 한다면
        if (!ObjectUtils.isEmpty(token) && token.startsWith(TOKEN_PREFIX)) {
            log.info("TOKEN = {}", token.substring(TOKEN_PREFIX.length()));
            return token.substring(TOKEN_PREFIX.length()); // 실제 토큰 값을 반환
        }
        return null;
    }
}
