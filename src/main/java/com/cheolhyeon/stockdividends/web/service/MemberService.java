package com.cheolhyeon.stockdividends.web.service;

import com.cheolhyeon.stockdividends.web.domain.MemberEntity;
import com.cheolhyeon.stockdividends.web.exception.impl.AlreadyExistUserException;
import com.cheolhyeon.stockdividends.web.model.Auth;
import com.cheolhyeon.stockdividends.web.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    // 회원의 정보 가져오기
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "could not find user with username: " + username));
    }
    // 회원가입
    @Transactional
    public MemberEntity register(Auth.SignUp member) {
        if (memberRepository.existsByUsername(member.getUsername())) {
            throw new AlreadyExistUserException();
        }
        // 암호화 과정
        member.setPassword(passwordEncoder.encode(member.getPassword()));
        // DB에 엔티티 저장
        return memberRepository.save(member.toEntity());
    }

    // 로그인 검증
    @Transactional
    public MemberEntity authenticate(Auth.SignIn member) {
        MemberEntity user = memberRepository.findByUsername(member.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException(
                        "존재하지 않는 ID 입니다: " + member.getUsername()));
        // DB에서 찾은 user의 password는 인코딩된 암호임. 파라미터로 받은 member는 인코딩 되지 않은 것
        if (!passwordEncoder.matches(member.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다 ");
        }
        return user;
    }
}
