package bankapp.member.service.signup;
import bankapp.member.dao.MemberDao;
import bankapp.member.exceptions.DuplicateUsernameException;
import bankapp.member.exceptions.PasswordMismatchException;
import bankapp.member.model.Member;
import bankapp.member.request.signup.SignUpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


// TODO: 테스트 코드 작성

/**
 * {@inheritDoc}
 * <p>
 * 이 구현체는 MemberDao와 PasswordEncoder를 사용하여
 * 아이디 중복을 확인하고 비밀번호를 암호화하여 데이터베이스에 새로운 회원을 저장하는 방식으로
 * 회원 가입 로직을 수행합니다.
 */
@Service
public class DefaultSignUpService implements SignUpService{

    private final MemberDao memberDao;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    public DefaultSignUpService(MemberDao memberDao ,  PasswordEncoder passwordEncoder) {
        this.memberDao = memberDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public Member signUp(SignUpRequest signUpRequest) throws DuplicateUsernameException, PasswordMismatchException{

        String username = signUpRequest.getUsername();
        String password = signUpRequest.getPassword();
        String passwordConfirm = signUpRequest.getPasswordConfirm();
        String name = signUpRequest.getName();

        if(passwordConfirm == null || !passwordConfirm.equals(password)) {
            throw new PasswordMismatchException("비밀번호가 일치하지 않습니다.");
        }

        if(memberDao.findByUsername(signUpRequest.getUsername()).isPresent()) {
            throw new DuplicateUsernameException("이미 존재하는 아이디입니다.");
        }

        Member newMember = new Member();
        newMember.setUsername(username);
        newMember.setPassword(passwordEncoder.encode(password));
        newMember.setName(name);

        return memberDao.insertMember(newMember);
    }


}
