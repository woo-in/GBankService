package bankapp.account.service.check;

import bankapp.account.dao.AccountDao;
import bankapp.account.exceptions.AccountNotFoundException;
import bankapp.account.exceptions.PrimaryAccountNotFoundException;
import bankapp.account.model.Account;
import bankapp.account.model.PrimaryAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static bankapp.core.common.BankCode.WOOIN_BANK;

@Component
public class DefaultAccountCheckService implements AccountCheckService {

    private final AccountDao accountDao;

    @Autowired
    public DefaultAccountCheckService(AccountDao accountDao){
        this.accountDao = accountDao;
    }

    @Override
    public boolean isAccountNumberExist(String accountNumber){

        if(accountNumber == null) {
            return false;
        }

        Optional<Account> accountOptional = accountDao.findByAccountNumber(accountNumber);
        return accountOptional.isPresent();

    }

    @Override
    public PrimaryAccount findPrimaryAccountByMemberId(Long memberId){

        // 계좌 목록 가져오기
        List<Account> accountList = accountDao.findByMemberId(memberId);

        // 계좌 목록에서 PRIMARY 계좌 찾아서 반환
        for(Account account : accountList){
            if(account.getAccountType().equals("PRIMARY")){
                return (PrimaryAccount) account;
            }
        }

        // 찾지 못했다면 예외반환
        // for문을 모두 통과했는데도 PRIMARY 계좌가 없으면 예외 발생
        throw new PrimaryAccountNotFoundException("해당 회원의 주계좌(PRIMARY)를 찾을 수 없습니다. memberId: " + memberId);
    }

    @Override
    public Account findAccountByAccountNumber(String accountNumber){
        if(accountNumber == null) {
            throw new AccountNotFoundException("해당 하는 계좌를 찾을 수 없습니다.");
        }

        Optional<Account> accountOptional = accountDao.findByAccountNumber(accountNumber);

        if(accountOptional.isEmpty()) {
            throw new AccountNotFoundException("해당 하는 계좌를 찾을 수 없습니다.");
        }

        return accountOptional.get();
    }

    @Override
    public boolean isExternalBank(String bankCode) {
        return !WOOIN_BANK.equals(bankCode);
    }



}
