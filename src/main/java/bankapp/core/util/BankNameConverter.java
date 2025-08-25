package bankapp.core.util;


import bankapp.core.common.BankCode;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// 은행 코드 : 은행 이름
public final class BankNameConverter {

    // 각 은행 코드에 해당하는 은행 이름을 저장하는 불변 Map
    // ConcurrentHashMap을 사용하여 스레드 안전성을 보장합니다.
    private static final Map<String, String> BANK_MAP;

    // 클래스가 로딩될 때 static 블록이 한 번만 실행되어 Map을 초기화합니다.
    static {
        Map<String, String> bankMap = new ConcurrentHashMap<>();
        bankMap.put(BankCode.WOOIN_BANK, "우인은행");
        bankMap.put(BankCode.TOSS_BANK, "토스뱅크");
        bankMap.put(BankCode.IBK_BANK, "IBK기업은행");
        bankMap.put(BankCode.KB_BANK, "KB국민은행");
        bankMap.put(BankCode.NH_BANK, "NH농협은행");
        bankMap.put(BankCode.SHINHAN_BANK, "신한은행");
        bankMap.put(BankCode.WOORI_BANK, "우리은행");
        bankMap.put(BankCode.HANA_BANK, "하나은행");
        BANK_MAP = Map.copyOf(bankMap); // 불변 Map으로 만듭니다.
    }

    /**
     * 외부에서 인스턴스를 생성하는 것을 막기 위해 private 생성자를 선언합니다.
     */
    private BankNameConverter() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 은행 코드를 받아 해당하는 은행 이름을 반환합니다.
     * 만약 코드가 존재하지 않으면 "알 수 없는 은행"을 반환합니다.
     *
     * @param bankCode 조회할 은행 코드 (e.g., "999", "092")
     * @return 은행 이름 문자열
     */
    public static String getBankNameByCode(String bankCode) {
        if (bankCode == null || bankCode.isBlank()) {
            return "알 수 없는 은행";
        }
        // Map에서 코드를 조회하고, 없으면 기본값을 반환합니다.
        return BANK_MAP.getOrDefault(bankCode, "알 수 없는 은행");
    }
}
