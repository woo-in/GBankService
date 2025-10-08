package bankapp.member.service.signup;

import bankapp.member.exceptions.DuplicateUsernameException;
import bankapp.member.exceptions.PasswordMismatchException;
import bankapp.member.model.Member;
import bankapp.member.repository.MemberRepository;
import bankapp.member.request.signup.SignUpRequest;
import bankapp.member.service.signup.component.SignUpRequestValidator;
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

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final SignUpRequestValidator signUpRequestValidator;
    @Autowired
    public DefaultSignUpService(MemberRepository memberRepository ,  PasswordEncoder passwordEncoder , SignUpRequestValidator signUpRequestValidator) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.signUpRequestValidator = signUpRequestValidator;
    }

    @Override
    @Transactional
    public Member signUp(SignUpRequest signUpRequest) throws DuplicateUsernameException, PasswordMismatchException{

        signUpRequestValidator.validate(signUpRequest);


        Member newMember = new Member(signUpRequest.getUsername(),
                passwordEncoder.encode(signUpRequest.getPassword()),
                signUpRequest.getName()
                );

        return memberRepository.save(newMember);
    }


}
