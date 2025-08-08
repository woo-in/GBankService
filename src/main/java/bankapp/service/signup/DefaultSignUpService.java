package bankapp.service.signup;

import bankapp.dao.BankAccountDao;
import bankapp.request.signup.SignUpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DefaultSignUpService implements SignUpService{

    // 데이터 베이스 연동
    private final BankAccountDao bankAccountDao;

    @Autowired
    public DefaultSignUpService(BankAccountDao bankAccountDao) {
        this.bankAccountDao = bankAccountDao;
    }

    @Override
    @Transactional
    public void signUp(SignUpRequest signUpRequest){

        // 아이디 중복 체크 , 비밀번호 일치 체크








    }


}
