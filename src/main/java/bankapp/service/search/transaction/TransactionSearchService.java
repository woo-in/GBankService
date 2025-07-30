package bankapp.service.search.transaction;

import bankapp.model.transaction.Transaction;
import bankapp.request.transaction.TransactionSearchRequest;

import java.util.List;

public interface TransactionSearchService {

    // 최근 거래 내역 10개 조회
    List<Transaction> getRecentTenTransactions(TransactionSearchRequest transactionSearchRequest);
}
