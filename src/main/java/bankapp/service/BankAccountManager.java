package bankapp.service;


import bankapp.account.BankAccount;
import bankapp.exceptions.DuplicateAccountException;
import bankapp.exceptions.InsufficientFundsException;
import bankapp.exceptions.InvalidAccountException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BankAccountManager {

	// 인터페이스화 해서 서비스를 빠르게 갈아 끼우고 , 여러 개 서비스 한번에 지원하는 것 느낄 예정
	private final OpenHighCreditAccountService openHighCreditAccountService;

	private final OpenNormalAccountService openNormalAccountService;

	private final DepositService depositService;

	private final WithdrawService withdrawService;

	private final FindAccountService findAccountService;

	private final TransferService transferService;

	@Autowired
	public BankAccountManager(OpenHighCreditAccountService openHighCreditAccountService,
							  OpenNormalAccountService openNormalAccountService,
							  DepositService depositService,
							  WithdrawService withdrawService,
							  FindAccountService findAccountService,
							  TransferService transferService) {
		this.openHighCreditAccountService = openHighCreditAccountService;
		this.openNormalAccountService = openNormalAccountService;
		this.depositService = depositService;
		this.withdrawService = withdrawService;
		this.findAccountService = findAccountService;
		this.transferService = transferService;
	}

	public void openHighCreditAccount(int accountNumber, String customerName, double balance, int ratio, int grade) throws IllegalArgumentException, DuplicateAccountException {
		 openHighCreditAccountService.openHighCreditAccount(accountNumber, customerName, balance, ratio, grade);
	}
	
	public void openNormalAccount(int accountNumber, String customerName, double balance, int ratio) throws IllegalArgumentException, DuplicateAccountException {
		openNormalAccountService.openNormalAccount(accountNumber, customerName, balance, ratio);
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

