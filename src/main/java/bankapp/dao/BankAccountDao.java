package bankapp.dao;


import bankapp.account.create.HighCreditAccountCreationRequest;
import bankapp.account.create.NormalAccountCreationRequest;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import bankapp.account.model.BankAccount;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Component
public class BankAccountDao {

	final private JdbcTemplate jdbcTemplate;
	
	public BankAccountDao(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource); 
	}
	
	
	// sql unchecked 예외 처리 할 것 
	
	// 입금 (반드시 탐색 후 호출해야 함) 
	public void updateBalancePlus(int accountNumber , double amount){
		String updateSql = "UPDATE BankAccount SET balance = balance + ? WHERE accountNumber = ?"; 
		jdbcTemplate.update(updateSql , amount , accountNumber); 		
	}
	
	// 출금 (반드시 탐색 후 호출해야 함) 
	public void updateBalanceMinus(int accountNumber , double amount){
		String updateSql = "UPDATE BankAccount SET balance = balance - ? WHERE accountNumber = ?"; 
		jdbcTemplate.update(updateSql , amount , accountNumber);
	}

	
	// 신용 신뢰 계좌 개설 
	public void insertHighCreditAccount(HighCreditAccountCreationRequest highCreditAccountCreationRequest) {
		String insertSql = "INSERT INTO BankAccount VALUES(?,?,?,?,?,?)";
		jdbcTemplate.update(insertSql,
				highCreditAccountCreationRequest.getAccountNumber(),
				highCreditAccountCreationRequest.getCustomerName(),
				highCreditAccountCreationRequest.getBalance(),
				highCreditAccountCreationRequest.getRatio(),
				"HighCreditAccount",
				highCreditAccountCreationRequest.getGrade());
	}
	
	// 보통 예금 계좌 개설 
	public void insertNormalAccount(NormalAccountCreationRequest normalAccountCreationRequest) {
		String insertSql = "INSERT INTO BankAccount VALUES(?,?,?,?,?,?)";
		jdbcTemplate.update(insertSql ,
				normalAccountCreationRequest.getAccountNumber() ,
				normalAccountCreationRequest.getCustomerName() ,
				normalAccountCreationRequest.getBalance() ,
				normalAccountCreationRequest.getRatio() ,
				"NormalAccount",
				-1);
	}
	
	
	
	// accountNumber 로 계좌가 존재하는지 탐색 
	@SuppressWarnings("DataFlowIssue")
    public boolean isAccountExist(int accountNumber) {
		String checkSql = "SELECT COUNT(*) FROM BankAccount WHERE accountNumber = ?";

		int count = jdbcTemplate.queryForObject(checkSql , Integer.class , accountNumber);

        return count != 0;
	}
	
	// 현재 잔액 탐색 (반드시 탐색후 호출) 
	@SuppressWarnings("DataFlowIssue")
    public double selectBalance(int accountNumber) {
		String checkSql = "SELECT balance FROM BankAccount WHERE accountNumber = ?"; 
		return jdbcTemplate.queryForObject(checkSql , Double.class , accountNumber);
	}
	
	
	
	// accountNumber 를 바탕으로 계좌정보 탐색후 pass (반드시 탐색후 호출) 
	public BankAccount selectBankAccount(int accountNumber) {
		
		String checkSql = "SELECT * FROM BankAccount WHERE accountNumber = ?"; 
		return jdbcTemplate.queryForObject(checkSql ,new BankAccountRowMapper()  , accountNumber);
		
	}
	
	// 성공 로그 기록 
	public void recordSuccessLog(String serviceName,int accountId) {
		String insertSql = "INSERT INTO TransactionLog(accountId , transactionType , transactionStatus) VALUES(?,?,?)"; 
		jdbcTemplate.update(insertSql , accountId , serviceName , "success");
	}

	/**
	 * 이 메서드는 항상 **새로운 트랜잭션을 시작**하도록 지정됩니다.
	 *
	 * 호출한 쪽에서 이미 트랜잭션이 진행 중이더라도,
	 * 기존 트랜잭션은 **일시 중단(suspend)** 되고,
	 * 이 메서드는 **독립적인 새로운 트랜잭션**으로 실행됩니다.
	 *
	 * 주로 사용하는 상황:
	 * - 호출한 쪽에서 예외가 발생해도, 이 메서드의 작업(DB 저장 등)은 rollback되지 않게 하기 위해
	 * - 실패 로그, 감사 로그 등 **예외가 발생해도 반드시 DB에 기록되어야 하는 작업**에 사용
	 *
	 * 트랜잭션 전파 수준: Propagation.REQUIRES_NEW
	 * → "항상 새로운 트랜잭션 시작"
	 */

	// 실패 로그 기록
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void recordFailLog(String serviceName ,int accountId , String reason) {
		String insertSql = "INSERT INTO TransactionLog(accountId , transactionType , transactionStatus , failureReason) VALUES(?,?,?,?)";
		jdbcTemplate.update(insertSql , accountId ,serviceName , "fail" , reason);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void recordFailLog(String serviceName , String reason) {
		String insertSql = "INSERT INTO TransactionLog(transactionType , transactionStatus , failureReason) VALUES(?,?,?)";
		jdbcTemplate.update(insertSql , serviceName, "fail" , reason);
	}
	
	// 거래 로그 기록 
	public void recordTransferLog(int senderId , int receiverId , double amount) {
		String insertSql = "INSERT INTO TransferLog(senderId , receiverId , amount) VALUES(?,?,?)";
		jdbcTemplate.update(insertSql , senderId, receiverId , amount);
	}
		
	
	
	
}
