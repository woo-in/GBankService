package bankapp.service;


import bankapp.account.request.creation.AccountCreationRequest;
import bankapp.account.request.creation.BankAccount;
import bankapp.exceptions.DuplicateAccountException;
import bankapp.exceptions.InsufficientFundsException;
import bankapp.exceptions.InvalidAccountException;
import bankapp.service.deposit.DepositService;
import bankapp.service.find.FindAccountService;
import bankapp.service.open.OpenAccountService;
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


	@Autowired
	public BankAccountManager(List<OpenAccountService> openAccountServices,
							  DepositService depositService,
							  WithdrawService withdrawService,
							  FindAccountService findAccountService,
							  @Qualifier("freeTransferService") TransferService transferService) {

		this.openAccountServices = new HashMap<>();
		for(OpenAccountService openAccountService : openAccountServices) {
			this.openAccountServices.put(openAccountService.getAccountType(), openAccountService);
		}

		this.depositService = depositService;
		this.withdrawService = withdrawService;
		this.findAccountService = findAccountService;
		this.transferService = transferService;
	}

	public void openAccount(AccountCreationRequest accountCreationRequest) throws IllegalArgumentException, DuplicateAccountException {
		OpenAccountService openAccountService = openAccountServices.get(accountCreationRequest.getAccountType());
		if(openAccountService == null) {
			throw new IllegalArgumentException("Invalid account type");
		}
		openAccountService.openAccount(accountCreationRequest);
	}

    public void deposit(int accountNumber, double amount) throws IllegalArgumentException , InvalidAccountException {
		depositService.deposit(accountNumber, amount);
    }
	
    public void withdraw(int accountNumber, double amount) throws IllegalArgumentException, InsufficientFundsException, InvalidAccountException {
    	withdrawService.withdraw(accountNumber, amount);
    }
    
    public BankAccount findAccount(int accountNumber) throws InvalidAccountException {
    	return findAccountService.findAccount(accountNumber);
    }
        
    public void transfer(int fromAccountId , int toAccountId , double amount) throws IllegalArgumentException , InvalidAccountException ,InsufficientFundsException{
    	transferService.transfer(fromAccountId,toAccountId,amount);
    }
}

