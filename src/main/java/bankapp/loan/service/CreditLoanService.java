package bankapp.loan.service;

import bankapp.loan.exceptions.LoanProductNotFoundException;
import bankapp.loan.model.product.CreditLoanProduct;

import java.util.List;

public interface CreditLoanService {

    List<CreditLoanProduct> findAllCreditLoanProducts();
    void saveCreditLoanProduct(CreditLoanProduct creditLoanProduct);
    CreditLoanProduct findCreditLoanProductByLoanProductSlug(String loanProductSlug);

}
