package bankapp.member.service.signup;
import bankapp.member.dao.MemberDao;
import bankapp.member.exceptions.DuplicateUsernameException;
import bankapp.member.exceptions.PasswordMismatchException;
import bankapp.member.model.Member;
import bankapp.member.request.signup.SignUpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class DefaultSignUpService implements SignUpService{

    // 데이터 베이스 연동
    private final MemberDao memberDao;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DefaultSignUpService(MemberDao memberDao ,  PasswordEncoder passwordEncoder) {
        this.memberDao = memberDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public Member signUp(SignUpRequest signUpRequest){

        String username = signUpRequest.getUsername();
        String password = signUpRequest.getPassword();
        String passwordConfirm = signUpRequest.getPasswordConfirm();
        String name = signUpRequest.getName();

        // 1. 유효성 검사

        // 비밀 번호 일치 체크
        if(passwordConfirm == null || !passwordConfirm.equals(password)) {
            throw new PasswordMismatchException("비밀번호가 일치하지 않습니다.");
        }

        // 아이디 중복 체크
        if(memberDao.findByUsername(signUpRequest.getUsername()).isPresent()) {
            throw new DuplicateUsernameException("이미 존재하는 아이디입니다.");
        }

        // 2. MEMBER 에 저장
        Member newMember = new Member();
        newMember.setUsername(username);
        newMember.setPassword(passwordEncoder.encode(password));
        newMember.setName(name);

        return memberDao.insertMember(newMember);
    }


}
