package bankapp.core.util;

public class AccountNumberFormatter {

    /**
     * 숫자로만 이루어진 계좌번호 문자열에 하이픈(-)을 추가하여 포맷팅합니다.
     * @param rawAccountNumber 하이픈이 없는 순수 숫자 계좌번호
     * @return 하이픈이 포함된 포맷팅된 계좌번호
     */
    public static String format(String rawAccountNumber) {
        if (rawAccountNumber == null) {
            return null;
        }

        // 숫자 이외의 문자가 있다면 제거 (안전장치)
        String digits = rawAccountNumber.replaceAll("[^0-9]", "");

        return switch (digits.length()) {
            case 10 -> digits.replaceFirst("(\\d{3})(\\d{2})(\\d{5})", "$1-$2-$3");
            case 11 -> digits.replaceFirst("(\\d{3})(\\d{3})(\\d{5})", "$1-$2-$3");
            case 12 -> digits.replaceFirst("(\\d{3})(\\d{3})(\\d{6})", "$1-$2-$3");
            case 14 -> digits.replaceFirst("(\\d{6})(\\d{2})(\\d{6})", "$1-$2-$3");
            case 15 -> digits.replaceFirst("(\\d{3})(\\d{6})(\\d{6})", "$1-$2-$3");
            default -> digits; // 정해진 규칙이 없으면 그대로 반환
        };
    }



}
