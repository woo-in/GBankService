package bankapp.member.service.login;

import bankapp.member.model.Member;
import bankapp.member.request.login.LoginRequest;

import java.util.Optional;

public interface LoginService {
    Member login(LoginRequest loginRequest);
}
