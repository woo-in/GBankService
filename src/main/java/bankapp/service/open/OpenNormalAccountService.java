package bankapp.service.open;

import bankapp.account.request.creation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import bankapp.dao.BankAccountDao;
import bankapp.exceptions.DuplicateAccountException;

@Component
public class OpenNormalAccountService implements OpenAccountService{

	// 데이터베이스 연동
	private final BankAccountDao bankAccountDao;

	@Autowired
	public OpenNormalAccountService(BankAccountDao bankAccountDao) {
		this.bankAccountDao = bankAccountDao;
	}

	public String getAccountType() {
		return "NormalAccount";
	}

	//보통 계좌 개설 서비스
	@Transactional
    public void openAccount(AccountCreationRequest accountCreationRequest) throws IllegalArgumentException, DuplicateAccountException {

		NormalAccountCreationRequest normalAccountCreationRequest;

		if(accountCreationRequest instanceof NormalAccountCreationRequest){
			normalAccountCreationRequest = (NormalAccountCreationRequest)accountCreationRequest;
		}
		else{
			throw new IllegalArgumentException("Invalid account type");
		}

		if (normalAccountCreationRequest.getBalance() < 0.0 || normalAccountCreationRequest.getRatio() < 0) {
			//시스템 상 , 절대로 허용 불가 , 중복 체크
			throw new IllegalArgumentException("negative number error");
		}

		// accountNumber 로 서칭 , 있다면 예외발생
		if(bankAccountDao.isAccountExist(normalAccountCreationRequest.getAccountNumber())) {
			throw new DuplicateAccountException("Duplicate account number");
		}

		// 계좌 생성
		bankAccountDao.insertNormalAccount(normalAccountCreationRequest);
	}
}
