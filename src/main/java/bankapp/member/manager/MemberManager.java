package bankapp.member.manager;

import bankapp.member.exceptions.DuplicateUsernameException;
import bankapp.member.exceptions.IncorrectPasswordException;
import bankapp.member.exceptions.PasswordMismatchException;
import bankapp.member.exceptions.UsernameNotFoundException;
import bankapp.member.model.Member;
import bankapp.member.request.login.LoginRequest;
import bankapp.member.request.signup.SignUpRequest;
import bankapp.member.service.login.LoginService;
import bankapp.member.service.signup.SignUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MemberManager {

    private final SignUpService signUpService;
    private final LoginService loginService;


    @Autowired
    public MemberManager(SignUpService signUpService, LoginService loginService) {
        this.signUpService = signUpService;
        this.loginService = loginService;
    }

    public void signUp(SignUpRequest signUpRequest) throws DuplicateUsernameException, PasswordMismatchException {
        signUpService.signUp(signUpRequest);
    }

    public Member login(LoginRequest loginRequest) throws UsernameNotFoundException, IncorrectPasswordException {
        return loginService.login(loginRequest);
    }

}
