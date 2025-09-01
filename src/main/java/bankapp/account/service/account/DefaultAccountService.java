package bankapp.account.service.account;

import bankapp.account.dao.account.AccountDao;
import bankapp.account.dao.ledger.AccountLedgerDao;
import bankapp.account.exceptions.AccountNotFoundException;
import bankapp.account.exceptions.InsufficientBalanceException;
import bankapp.account.exceptions.InvalidAmountException;
import bankapp.account.model.account.Account;
import bankapp.account.model.ledger.AccountLedger;
import bankapp.account.model.ledger.TransactionType;
import bankapp.account.request.account.AccountTransactionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

// TODO: TEST 코드 작성

@Service
public class DefaultAccountService implements AccountService {

    private final AccountDao accountDao;
    private final AccountLedgerDao accountLedgerDao;


    @Autowired
    public DefaultAccountService(AccountDao accountDao, AccountLedgerDao accountLedgerDao ){
        this.accountDao = accountDao;
        this.accountLedgerDao = accountLedgerDao;
    }

    @Override
    @Transactional
    public long debit(AccountTransactionRequest accountTransactionRequest) throws AccountNotFoundException, InsufficientBalanceException , InvalidAmountException {

        BigDecimal amount = accountTransactionRequest.getAmount();
        Long accountId = accountTransactionRequest.getAccountId();

        validateAmount(amount);

        // 비관적 락 (트랜잭션 끝날때 까지 로우를 잠금)
        Account account = lockAndGetAccount(accountId);

        validateSufficientBalance(account, amount);

        return applyDebitChanges(account,accountTransactionRequest).getLedgerId();
    }

    @Override
    @Transactional
    public long credit(AccountTransactionRequest accountTransactionRequest) throws AccountNotFoundException, InsufficientBalanceException , InvalidAmountException{

        BigDecimal amount = accountTransactionRequest.getAmount();
        Long accountId = accountTransactionRequest.getAccountId();

        validateAmount(amount);

        // 비관적 락 (트랜잭션 끝날때 까지 로우를 잠금)
        Account account = lockAndGetAccount(accountId);

        return applyCreditChanges(account,accountTransactionRequest).getLedgerId();
    }





    /**
     * 요청 DTO 자체의 유효성을 검증합니다. (예: 금액이 양수인지)
     */
    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("거래 금액은 0보다 커야 합니다.");
        }
    }

    /**
     * 계좌를 비관적 잠금으로 조회하고, 존재하지 않을 경우 예외를 발생시킵니다.
     */
    private Account lockAndGetAccount(Long accountId) {
        return accountDao.findByIdForUpdate(accountId)
                .orElseThrow(() -> new AccountNotFoundException("계좌가 존재하지 않습니다. ID: " + accountId));
    }

    /**
     * 계좌의 잔액이 출금 금액에 충분한지 검증합니다.
     */
    private void validateSufficientBalance(Account account, BigDecimal amountToDebit) {
        if (account.getBalance().compareTo(amountToDebit) < 0) {
            throw new InsufficientBalanceException("계좌 잔액이 부족합니다.");
        }
    }

    /**
     * 실제 계좌의 잔액을 변경하고, 거래 원장을 기록 , ledger_id 를 반환합니다.
     */
    private AccountLedger applyDebitChanges(Account account, AccountTransactionRequest accountTransactionRequest) {
        BigDecimal balanceAfter = account.getBalance().subtract(accountTransactionRequest.getAmount());

        accountDao.setBalance(account.getAccountId(), balanceAfter);
        return accountLedgerDao.save(AccountLedger.from(accountTransactionRequest, TransactionType.DEBIT, balanceAfter));
    }

    private AccountLedger applyCreditChanges(Account account, AccountTransactionRequest accountTransactionRequest) {
        BigDecimal balanceAfter = account.getBalance().add(accountTransactionRequest.getAmount());

        accountDao.setBalance(account.getAccountId(), balanceAfter);
        return accountLedgerDao.save(AccountLedger.from(accountTransactionRequest, TransactionType.CREDIT, balanceAfter));
    }


}
