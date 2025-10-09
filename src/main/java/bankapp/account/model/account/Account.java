package bankapp.account.model.account;

import bankapp.member.model.Member;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "account_type")
public abstract class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountId;

    @ManyToOne(fetch = FetchType.LAZY , optional = false)
    @JoinColumn(name = "member_id" , nullable = false)
    private Member member;


    @Column(unique = true , nullable = false)
    private String accountNumber;

    @Column(nullable = false)
    private BigDecimal balance;

    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "account_type", insertable = false, updatable = false)
    protected String accountType;

    protected Account() {}

    protected Account(Member member, String accountNumber, BigDecimal balance, String nickname, AccountStatus status) {
        this.member = member;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.nickname = nickname;
        this.status = status;
    }

    protected Account(Member member, String accountNumber, BigDecimal balance, AccountStatus status) {
        this.member = member;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.status = status;
    }
}


