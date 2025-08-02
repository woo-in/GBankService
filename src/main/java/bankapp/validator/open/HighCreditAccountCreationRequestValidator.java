package bankapp.validator.open;


import bankapp.request.open.HighCreditAccountCreationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


import java.util.regex.Pattern;

@Slf4j
@Component
public class HighCreditAccountCreationRequestValidator implements Validator {

    // 이름 검증을 위한 정규식 (영어 2~20자)
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z]{2,20}$");

    @Override
    public boolean supports(Class<?> clazz) {
        return HighCreditAccountCreationRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        // 바인딩이 유효하지 않다면 값 유효성 확인할 필요 없음
        if(errors.hasErrors()){
            return;
        }

        HighCreditAccountCreationRequest request = (HighCreditAccountCreationRequest) target;

        // 계좌 ID 검증 (100001 ~ 999999)
        validateAccountNumber(request.getAccountNumber(), errors);

        // 이름 검증 (영어 2~20자)
        validateCustomerName(request.getCustomerName(), errors);

        // 입금액 검증 (자연수)
        validateBalance(request.getBalance(), errors);

        // 이자율 검증 (정수 0 ~ 100)
        validateRatio(request.getRatio(), errors);

        // 등급 검증 (정수 1 ~ 3)
        validateGrade(request.getGrade(), errors);
    }

    private void validateAccountNumber(Integer accountNumber, Errors errors) {

        if (accountNumber == null) {
            errors.rejectValue("accountNumber" , "empty" , "계좌 번호를 입력하세요.");
            return;
        }


        if (accountNumber < 100001 || accountNumber > 999999) {
            errors.rejectValue("accountNumber", "range",
                    "계좌 ID는 100001부터 999999 사이의 값이어야 합니다.");
        }
    }

    private void validateCustomerName(String customerName, Errors errors) {

        // null 은 바인딩 체크에서 하지 않을까 ?
        if (customerName == null || customerName.trim().isEmpty()) {
            errors.rejectValue("customerName", "empty",
                    "이름을 입력하세요.");
            return;
        }

        if (!NAME_PATTERN.matcher(customerName.trim()).matches()) {
            errors.rejectValue("customerName", "value",
                    "고객 이름은 영어 2자에서 20자 사이여야 합니다.");
        }
    }

    private void validateBalance(Double balance, Errors errors) {
        if (balance == null) {
            errors.rejectValue("balance" , "empty" , "입금액을 입력하세요.");
            return;
        }
        if (balance < 0) {
            errors.rejectValue("balance", "range",
                    "입금액은 0 이상이어야 합니다.");
        }
    }

    private void validateRatio(Integer ratio, Errors errors) {
        if (ratio == null) {
            errors.rejectValue("ratio" , "empty" , "이자율을 입력하세요.");
            return;
        }
        if (ratio < 0 || ratio > 100) {
            errors.rejectValue("ratio", "range",
                    "이자율은 0부터 100 사이의 정수여야 합니다.");
        }
    }

    private void validateGrade(Integer grade, Errors errors) {
        if (grade == null || grade == 0) {
            errors.rejectValue("grade", "empty", "신용등급을 선택하세요.");
            return;
        }

        if (grade < 1 || grade > 3) {
            errors.rejectValue("grade", "range",
                    "신용등급은 1(A), 2(B), 3(C) 중 하나를 선택해야 합니다.");
        }
    }




}
