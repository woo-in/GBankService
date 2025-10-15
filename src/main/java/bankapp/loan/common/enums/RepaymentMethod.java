package bankapp.loan.common.enums;

public enum RepaymentMethod {

    /**
     * 원리금 균등분할상환: 매달 동일한 금액(원금+이자)을 상환합니다.
     */
    LEVEL_PAYMENT("원리금균등분할상환"),

    /**
     * 원금 균등분할상환: 매달 동일한 원금을 상환하며, 이자는 남은 원금에 따라 줄어듭니다.
     */
    EQUAL_PRINCIPAL_PAYMENT("원금균등분할상환"),

    /**
     * 만기일시상환: 대출 기간 동안 이자만 내다가 만기에 원금 전체를 상환합니다.
     */
    BULLET_REPAYMENT("만기일시상환"),

    /**
     * 마이너스 통장 방식: 한도 내에서 자유롭게 빌리고 쓴 만큼만 이자를 계산하여 상환합니다.
     */
    OVERDRAFT("마이너스통장"),

    /**
     * 거치식 상환: 일정 기간 이자만 납부 후, 원리금 또는 원금을 분할 상환합니다.
     */
    GRACE_PERIOD("거치식 상환");


    private final String displayName;

    RepaymentMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
