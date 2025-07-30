package bankapp.aspect;

import bankapp.request.deposit.AccountDepositRequest;
import bankapp.request.transfer.AccountTransferRequest;
import bankapp.request.withdraw.AccountWithdrawRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import bankapp.dao.BankAccountDao;
import bankapp.exceptions.InsufficientFundsException;
import bankapp.exceptions.InvalidAccountException;
import bankapp.exceptions.InvalidAccountException.Role;




@Aspect 
public class TransactionLogAspect {

	private static final Logger log = LoggerFactory.getLogger(TransactionLogAspect.class);
	// 데이터 베이스 접근
	@Autowired 
	private BankAccountDao bankAccountDao ; 
		
	@Pointcut("execution(public void bankapp.service.deposit.DepositService.deposit(bankapp.request.deposit.AccountDepositRequest)) "
			+ "|| execution(public void bankapp.service.withdraw.WithdrawService.withdraw(bankapp.request.withdraw.AccountWithdrawRequest))"
			+ " || execution(public void bankapp.service.transfer.TransferService.transfer(bankapp.request.transfer.AccountTransferRequest))")
	private void depositOrWithdrawPointcut() {}
	
	@Around("depositOrWithdrawPointcut()")
	public Object measure(ProceedingJoinPoint joinPoint) throws Throwable{
		
		// 입금 -> (대상계좌 , 금액)
		// 출금 -> (대상계좌 , 금액)
		// 송금 -> (송금계좌 , 수취계좌 , 금액) 

		// accountNumber
		// senderNumber
		
		Object[] args = joinPoint.getArgs();

		// 입출금시 accountNumber 사용
		// 송금시 senderNumber,receiverNumber,amount 사용
		int accountNumber = -1;
		int senderNumber = -1;
		int receiverNumber = -1;
		//
		double amount = -1.0;
		if (args[0] instanceof AccountDepositRequest depositRequest) {
			accountNumber = depositRequest.getAccountNumber();
		} else if (args[0] instanceof AccountWithdrawRequest withdrawRequest) {
			accountNumber = withdrawRequest.getAccountNumber();
		} else if (args[0] instanceof AccountTransferRequest transferRequest) {
			senderNumber = transferRequest.getSenderNumber();
			receiverNumber = transferRequest.getReceiverNumber();
			amount = transferRequest.getAmount();
		}

		// 작업명 
		String serviceName = joinPoint.getSignature().getName();
		
		try {	
			joinPoint.proceed(); // 입출금 메서드 실행
			// 성공 로그기록 
			bankAccountDao.recordSuccessLog(serviceName,accountNumber);
			// 만약 성공 로그가 송금이라면 거래로그 테이블에 기록 
			if(serviceName.equals("transfer")) {
				bankAccountDao.recordTransferLog(senderNumber,receiverNumber,amount);
			}
		}
		catch(IllegalArgumentException e) {
			// 입금액 , 출금액 , 송금액이 음수 (UI 에서 처리하기 때문에 호출되지는 않음)
			bankAccountDao.recordFailLog(serviceName,accountNumber, "Negative number error");
			throw new IllegalArgumentException("Negative number error");
		}
		catch(InvalidAccountException e) {
			// 계좌 서칭 실패 , 왜 기록이 안되나 ?


			 if(e.getRole() == Role.SENDER) {
				 bankAccountDao.recordFailLog(serviceName,"Invalid sender id");
				 throw new InvalidAccountException(Role.SENDER); 
        	 }
        	 else if(e.getRole() == Role.RECEIVER) {
        		 bankAccountDao.recordFailLog(serviceName,"Invalid receiver id");
        		 throw new InvalidAccountException(Role.RECEIVER); 
        	 }
        	 else {
        		bankAccountDao.recordFailLog(serviceName,"Invalid id");
        		throw new InvalidAccountException(Role.GENERAL);
        	 }
		}
		catch(InsufficientFundsException e) {
			// 예치금이 적음
			bankAccountDao.recordFailLog(serviceName,accountNumber,"Insufficient Funds error");
			throw new InsufficientFundsException("Insufficient Funds error");
		}

		return null; 
	}
	
	
	
	
	
	
}
