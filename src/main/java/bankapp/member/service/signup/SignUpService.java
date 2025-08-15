package bankapp.member.service.signup;

import bankapp.member.model.Member;
import bankapp.member.request.signup.SignUpRequest;

public interface SignUpService {
    Member signUp(SignUpRequest signUpRequest);
}
