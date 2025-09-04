package bankapp.member.service.signup.component;

import bankapp.member.dao.MemberDao;
import bankapp.member.exceptions.DuplicateUsernameException;
import bankapp.member.exceptions.PasswordMismatchException;
import bankapp.member.request.signup.SignUpRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SignUpRequestValidator {

    private final MemberDao memberDao;
    @Autowired
    public SignUpRequestValidator(MemberDao memberDao) {
        this.memberDao = memberDao;
    }


    public void validate(SignUpRequest request) throws PasswordMismatchException, DuplicateUsernameException {
        // 1. 비밀번호 일치 여부 검증
        if (request.getPassword() == null || !request.getPassword().equals(request.getPasswordConfirm())) {
            throw new PasswordMismatchException("비밀번호가 일치하지 않습니다.");
        }

        // 2. 아이디(username) 중복 검증
        if (memberDao.findByUsername(request.getUsername()).isPresent()) {
            throw new DuplicateUsernameException("이미 존재하는 아이디입니다.");
        }
    }

}
