package bankapp.service;


import bankapp.exceptions.*;
import bankapp.model.member.Member;
import bankapp.request.login.LoginRequest;
import bankapp.request.open.AccountCreationRequest;
import bankapp.request.deposit.AccountDepositRequest;
import bankapp.request.find.AccountFindRequest;
import bankapp.model.account.BankAccount;
import bankapp.request.signup.SignUpRequest;
import bankapp.request.transfer.AccountTransferRequest;
import bankapp.request.withdraw.AccountWithdrawRequest;
import bankapp.service.deposit.DepositService;
import bankapp.service.find.FindAccountService;
import bankapp.service.login.LoginService;
import bankapp.service.open.OpenAccountService;
import bankapp.service.signup.SignUpService;
import bankapp.service.transfer.TransferService;
import bankapp.service.withdraw.WithdrawService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BankAccountManager {

	private final DepositService depositService;
	private final WithdrawService withdrawService;
	private final FindAccountService findAccountService;
	private final TransferService transferService;
	private final Map<String , OpenAccountService> openAccountServices;
	private final SignUpService signUpService;
	private final LoginService loginService;


	@Autowired
	public BankAccountManager(List<OpenAccountService> openAccountServices,
							  DepositService depositService,
							  WithdrawService withdrawService,
							  FindAccountService findAccountService,
							  @Qualifier("freeTransferService") TransferService transferService,
							  SignUpService signUpService,
							  LoginService loginService) {

		this.openAccountServices = new HashMap<>();
		for(OpenAccountService openAccountService : openAccountServices) {
			this.openAccountServices.put(openAccountService.getAccountType(), openAccountService);
		}

		this.depositService = depositService;
		this.withdrawService = withdrawService;
		this.findAccountService = findAccountService;
		this.transferService = transferService;
		this.signUpService = signUpService;
		this.loginService = loginService;
	}

	public void openAccount(AccountCreationRequest accountCreationRequest) throws IllegalArgumentException, DuplicateAccountException {
		OpenAccountService openAccountService = openAccountServices.get(accountCreationRequest.getAccountType());
		if(openAccountService == null) {
			throw new IllegalArgumentException("Invalid account type");
		}
		openAccountService.openAccount(accountCreationRequest);
	}

    public void deposit(AccountDepositRequest accountDepositRequest) throws IllegalArgumentException , InvalidAccountException {
		depositService.deposit(accountDepositRequest);
    }
	
    public void withdraw(AccountWithdrawRequest accountWithdrawRequest) throws IllegalArgumentException, InsufficientFundsException, InvalidAccountException {
    	withdrawService.withdraw(accountWithdrawRequest);
    }
    
    public BankAccount findAccount(AccountFindRequest accountFindRequest) throws InvalidAccountException {
    	return findAccountService.findAccount(accountFindRequest);
    }
        
    public void transfer(AccountTransferRequest accountTransferRequest) throws IllegalArgumentException , InvalidAccountException ,InsufficientFundsException{
    	transferService.transfer(accountTransferRequest);
    }

	public void signUp(SignUpRequest signUpRequest) throws DuplicateUsernameException, PasswordMismatchException {
		signUpService.signUp(signUpRequest);
	}

	public Member login(LoginRequest loginRequest) throws UsernameNotFoundException , IncorrectPasswordException{
		return loginService.login(loginRequest);
	}


}

