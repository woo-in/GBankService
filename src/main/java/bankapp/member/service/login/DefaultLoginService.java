package bankapp.member.service.login;

import bankapp.member.dao.MemberDao;
import bankapp.member.exceptions.IncorrectPasswordException;
import bankapp.member.exceptions.UsernameNotFoundException;
import bankapp.member.model.Member;
import bankapp.member.request.login.LoginRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// TODO: 테스트 코드 작성
/**
 * {@inheritDoc}
 * <p>
 * 이 구현체는 MemberDao와 PasswordEncoder를 사용하여
 * 데이터베이스의 회원 정보와 암호화된 비밀번호를 비교하는 방식으로
 * 로그인 로직을 수행합니다.
 */

@Service
public class DefaultLoginService implements LoginService{

    private final MemberDao memberDao;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    public DefaultLoginService(MemberDao memberDao , PasswordEncoder passwordEncoder) {
        this.memberDao = memberDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public Member login(LoginRequest loginRequest) throws UsernameNotFoundException, IncorrectPasswordException{
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        Member member = memberDao.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 아이디입니다."));

        if(!passwordEncoder.matches(password, member.getPassword())) {
            throw new IncorrectPasswordException("비밀번호가 일치하지 않습니다.");
        }

        return member;
    }

}

