package bankapp.loan.repository;

import bankapp.loan.model.product.CreditLoanProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CreditLoanProductRepository extends JpaRepository<CreditLoanProduct, Long> {

    Optional<CreditLoanProduct> findByLoanProductSlug(String loanProductSlug);


}
