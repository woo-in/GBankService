package bankapp.member.service.signup;

import bankapp.account.dao.AccountDao;
import bankapp.member.dao.MemberDao;
import bankapp.member.exceptions.DuplicateUsernameException;
import bankapp.member.exceptions.PasswordMismatchException;
import bankapp.account.model.Account;
import bankapp.member.model.Member;
import bankapp.member.request.signup.SignUpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class DefaultSignUpService implements SignUpService{

    // 데이터 베이스 연동
    private final MemberDao memberDao;
    private final AccountDao accountDao;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DefaultSignUpService(MemberDao memberDao , AccountDao accountDao , PasswordEncoder passwordEncoder) {
        this.memberDao = memberDao;
        this.accountDao = accountDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void signUp(SignUpRequest signUpRequest){

        String username = signUpRequest.getUsername();
        String password = signUpRequest.getPassword();
        String passwordConfirm = signUpRequest.getPasswordConfirm();
        String name = signUpRequest.getName();
        String accountNickname = signUpRequest.getAccountNickname();

        // 1. 유효성 검사
        // 아이디 중복 체크
        if(memberDao.findByUsername(signUpRequest.getUsername()) != null) {
            throw new DuplicateUsernameException("이미 존재하는 아이디입니다.");
        }

        // 비밀 번호 일치 체크
        if(passwordConfirm == null || !passwordConfirm.equals(password)) {
            throw new PasswordMismatchException("비밀번호가 일치하지 않습니다.");
        }

        // 2. MEMBER 에 저장
        Member newMember = new Member();
        newMember.setUsername(username);
        newMember.setPassword(passwordEncoder.encode(password));
        newMember.setName(name);

        Member savedMember = memberDao.save(newMember);

        // 3. ACCOUNT 에 저장
        Account newAccount = new Account();
        newAccount.setMemberId(savedMember.getMemberId());
        newAccount.setNickname(accountNickname);
        newAccount.setAccountNumber(createAccountNumber()); // 새로운 계좌 번호 생성
        newAccount.setBalance(BigDecimal.ZERO);
        newAccount.setAccountType("PRIMARY");

        accountDao.save(newAccount);
    }

    // 임시로 고유한 계좌 번호 생성하는 메서드
    private String createAccountNumber() {
        // 실제로는 더 정교한 규칙이 필요 , 여기서는 UUID 로 간단히 구현
        // 예 : 110 - xxxx - xxxx
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        return "110-" + uuid.substring(0, 4) + "-" + uuid.substring(4, 8);

    }


}
