package bankapp.service.search.transaction;

import bankapp.dao.BankAccountDao;
import bankapp.model.transaction.Transaction;
import bankapp.request.transaction.TransactionSearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class DefaultTransactionSearchService {

    // 데이터 베이스 연동
    private final BankAccountDao bankAccountDao;

    @Autowired
    public DefaultTransactionSearchService(BankAccountDao bankAccountDao) {
        this.bankAccountDao = bankAccountDao;
    }

//    // 최근 거래 내역 10개 조회
//    @Transactional
//    @Override
//    List<Transaction> getRecentTenTransactions(TransactionSearchRequest transactionSearchRequest){
//
//        int accountNumber = transactionSearchRequest.getAccountNumber();
//
//
//
//
//
//
//
//    }


}
