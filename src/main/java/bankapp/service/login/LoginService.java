package bankapp.service.login;

import bankapp.model.member.Member;
import bankapp.request.login.LoginRequest;

public interface LoginService {
    Member login(LoginRequest loginRequest);
}
