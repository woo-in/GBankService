package bankapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    // 실제 배포시 인증 기능 사용
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // "/**" 는 모든 URL 경로를 의미합니다.
                        // 모든 경로에 대한 요청을 허용합니다.
                        .requestMatchers("/**").permitAll()
                );

        return http.build();
    }


}


//public Member signIn(LoginRequest request) {
//    // 1. 아이디로 회원 정보 조회
//    Member member = memberDao.findByUsername(request.getUsername());
//    if (member == null) {
//        throw new IllegalStateException("존재하지 않는 아이디입니다.");
//    }
//
//    // 2. 비밀번호 비교 (핵심!)
//    if (passwordEncoder.matches(request.getPassword(), member.getPassword())) {
//        // 비밀번호 일치 시, 회원 정보 반환
//        return member;
//    } else {
//        // 비밀번호 불일치 시, 예외 발생
//        throw new IllegalStateException("비밀번호가 일치하지 않습니다.");
//    }
//}