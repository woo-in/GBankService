package bankapp.core.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 은행 코드를 상수로 정의한 클래스.
 * 유틸리티 클래스이므로 인스턴스화하지 못하도록 private 생성자를 추가합니다.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BankCode {

    public static final String WOOIN_BANK = "999";
    public static final String TOSS_BANK = "092";
    public static final String IBK_BANK = "003";
    public static final String KB_BANK = "004";
    public static final String NH_BANK = "011";
    public static final String SHINHAN_BANK = "088";
    public static final String WOORI_BANK = "020";
    public static final String HANA_BANK = "081";

}

