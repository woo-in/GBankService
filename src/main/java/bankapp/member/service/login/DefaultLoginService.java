package bankapp.member.service.login;

import bankapp.member.dao.MemberDao;
import bankapp.member.exceptions.IncorrectPasswordException;
import bankapp.member.exceptions.UsernameNotFoundException;
import bankapp.member.model.Member;
import bankapp.member.request.login.LoginRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class DefaultLoginService implements LoginService{

    // 데이터 베이스 연동
    private final MemberDao memberDao;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DefaultLoginService(MemberDao memberDao , PasswordEncoder passwordEncoder) {
        this.memberDao = memberDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public Member login(LoginRequest loginRequest){
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        // 1. 아이디로 회원 정보 조회
        Member member = memberDao.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 아이디입니다."));


        // 2. 비밀번호 비교 (null 아님)
        if(!passwordEncoder.matches(password, member.getPassword())) {
            // 예외 던지기
            throw new IncorrectPasswordException("비밀번호가 일치하지 않습니다.");
        }

        /// 3. 인증 완료
        return member;
    }

}

