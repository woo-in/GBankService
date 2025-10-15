package bankapp.loan.model.product;

import bankapp.loan.common.enums.RepaymentMethod;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


@Data
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "loan_type")
@SuperBuilder
@NoArgsConstructor
public abstract class LoanProduct {

    // TODO : embedded 을 적극 사용할 것
    // TODO : 임시 , 완성 아님

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long loanProductId;

    @Column(nullable = false)
    private String loanProductName;

    @Column(nullable = false , unique = true)
    private String loanProductSlug;

    @Column(name = "loan_type" , insertable = false , updatable = false)
    private String loanType;

    @Column(precision = 5 , scale = 2, nullable = false)
    private BigDecimal minInterestRate;

    @Column(precision = 5 , scale = 2, nullable = false)
    private BigDecimal maxInterestRate;

    @Column(nullable = false)
    private BigDecimal maxLoanAmount;

    @Column(nullable = false)
    private Integer maxLoanTerm;

    @Embedded
    private LoanProductDetail loanProductDetail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "loan_product_repayment_method", // 생성될 테이블 이름
            joinColumns = @JoinColumn(name = "loan_product_id") // 외래 키
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "repayment_method" , nullable = false)
    @Singular
    private Set<RepaymentMethod> repaymentMethods = new HashSet<>();


}
