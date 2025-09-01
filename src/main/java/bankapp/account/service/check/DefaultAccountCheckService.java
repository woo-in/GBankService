package bankapp.account.service.check;

import bankapp.account.dao.account.AccountDao;
import bankapp.account.exceptions.AccountNotFoundException;
import bankapp.account.exceptions.PrimaryAccountNotFoundException;
import bankapp.account.model.account.Account;
import bankapp.account.model.account.PrimaryAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static bankapp.core.common.BankCode.WOOIN_BANK;


/**
 * {@inheritDoc}
 * <p>
 * 이 구현체는 AccountDao를 사용하여 데이터베이스에 직접 조회함으로써
 * 계좌 정보 확인 및 조회 로직을 수행합니다.
 */

// TODO: 테스트 코드 작성

@Service
public class DefaultAccountCheckService implements AccountCheckService {

    private final AccountDao accountDao;

    @Autowired
    public DefaultAccountCheckService(AccountDao accountDao){
        this.accountDao = accountDao;
    }


    @Override
    public boolean isAccountNumberExist(String accountNumber){

        if (isInvalidAccountNumber(accountNumber)) {
            return false;
        }

        return accountDao.existsByAccountNumber(accountNumber);
    }

    @Override
    public PrimaryAccount findPrimaryAccountByMemberId(Long memberId) throws PrimaryAccountNotFoundException{

        return accountDao.findPrimaryAccountByMemberId(memberId)
                .orElseThrow(() -> new PrimaryAccountNotFoundException("해당 회원의 주계좌(PRIMARY)를 찾을 수 없습니다. memberId: " + memberId));

    }

    @Override
    public PrimaryAccount findPrimaryAccountByAccountId(Long accountId) throws PrimaryAccountNotFoundException{
        return accountDao.findPrimaryAccountByAccountId(accountId)
                .orElseThrow(() -> new PrimaryAccountNotFoundException("해당 회원의 주계좌(PRIMARY)를 찾을 수 없습니다. memberId: " + accountId));

    }


    @Override
    public Account findAccountByAccountNumber(String accountNumber) throws AccountNotFoundException{

        if (isInvalidAccountNumber(accountNumber)) {
            throw new AccountNotFoundException("해당 하는 계좌를 찾을 수 없습니다.");
        }

        return accountDao.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("해당 계좌를 찾을 수 없습니다. 계좌번호: " + accountNumber));
    }

    @Override
    public Account findAccountByAccountId(Long accountId) throws AccountNotFoundException{

        if(isInvalidAccountId(accountId)) {
            throw new AccountNotFoundException("해당 하는 계좌를 찾을 수 없습니다.");
        }

        return accountDao.findByAccountId(accountId)
                .orElseThrow(() -> new AccountNotFoundException("해당 계좌를 찾을 수 없습니다. 계좌번호: " + accountId));
    }

    @Override
    public boolean isExternalBank(String bankCode) {
        return !WOOIN_BANK.equals(bankCode);
    }

    private boolean isInvalidAccountNumber(String accountNumber) {
        return accountNumber == null || accountNumber.trim().isEmpty();
    }

    private boolean isInvalidAccountId(Long accountId) {
        return accountId == null ;
    }



}
