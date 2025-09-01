package bankapp.account.model.account;

public class SavingsAccount extends Account {

    /**
     * SavingsAccount 객체가 생성될 때,
     * 계좌 타입을 "SAVINGS"로 설정합니다.
     * 테스트를 위해 임시로 설정하는 계좌 입니다.
     */
    public SavingsAccount() {
        super();
        this.setAccountType("SAVINGS");
    }

}