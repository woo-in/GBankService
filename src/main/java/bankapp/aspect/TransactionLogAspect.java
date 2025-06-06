package bankapp.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;

import bankapp.dao.BankAccountDao;
import bankapp.exceptions.InsufficientFundsException;
import bankapp.exceptions.InvalidAccountException;
import bankapp.exceptions.InvalidAccountException.Role;



@Aspect 
public class TransactionLogAspect {

	// 데이터 베이스 접근 
	@Autowired 
	private BankAccountDao bankAccountDao ; 
		
	@Pointcut("execution(public void bankapp.service.DepositService.deposit(int, double)) "
			+ "|| execution(public void bankapp.service.WithdrawService.withdraw(int, double))"
			+ " || execution(public void bankapp.service.TransferService.transfer(int,int,double))")
	private void depositOrWithdrawPointcut() {}
	
	@Around("depositOrWithdrawPointcut()")
	public Object measure(ProceedingJoinPoint joinPoint) throws Throwable{
		
		// 입금 -> (대상계좌 , 금액)
		// 출금 -> (대상계좌 , 금액)
		// 송금 -> (송금계좌 , 수취계좌 , 금액) 
		
		
		Object[] args = joinPoint.getArgs(); 
		int accountId = (int) args[0]; 
		
		// 작업명 
		String serviceName = joinPoint.getSignature().getName();
		
		try {	
			joinPoint.proceed(); // 입출금 메서드 실행  
			// 성공 로그기록 
			bankAccountDao.recordSuccessLog(serviceName,accountId); 
			// 만약 성공 로그가 송금이라면 거래로그 테이블에 기록 
			if(serviceName.equals("transfer")) {
				bankAccountDao.recordTransferLog((int)args[0],(int)args[1],(double)args[2]);
			}
		}
		catch(IllegalArgumentException e) {
			// 입금액 , 출금액 , 송금액이 음수 (UI 에서 처리하기 때문에 호출되지는 않음)
			bankAccountDao.recordFailLog(serviceName,accountId, "Negative number error");
			throw new IllegalArgumentException("Negative number error");
		}
		catch(InvalidAccountException e) {
			// 계좌 서칭 실패 
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
			bankAccountDao.recordFailLog(serviceName,accountId,"Insufficient Funds error");
			throw new InsufficientFundsException("Insufficient Funds error");
		}
		
		
		
		return null; 
	}
	
	
	
	
	
	
}
