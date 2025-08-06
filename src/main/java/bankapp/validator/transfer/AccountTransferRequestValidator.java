package bankapp.validator.transfer;

import bankapp.request.transfer.AccountTransferRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Slf4j
@Component
public class AccountTransferRequestValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return AccountTransferRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        
        // 바인딩이 유효하지 않다면 값 유효성 확인할 필요 없음
        if(errors.hasErrors()){
            return;
        }

        AccountTransferRequest request = (AccountTransferRequest) target;

        // 송금 계좌 ID 검증 (100001 ~ 999999)
        validateSenderNumber(request.getSenderNumber(), errors);

        // 수취 계좌 ID 검증 (100001 ~ 999999)
        validateReceiverNumber(request.getReceiverNumber(), errors);

        // 송금액 검증 (양수)
        validateAmount(request.getAmount(), errors);
        
        // 송금 계좌와 수취 계좌가 동일한지 검증
        validateDifferentAccounts(request.getSenderNumber(), request.getReceiverNumber(), errors);
    }

    private void validateSenderNumber(Integer senderNumber, Errors errors) {
        if (senderNumber == null || senderNumber == 0) {
            errors.rejectValue("senderNumber", "empty", "송금 계좌 ID를 입력하세요.");
            return;
        }

        if (senderNumber < 100001 || senderNumber > 999999) {
            errors.rejectValue("senderNumber", "range",
                    "송금 계좌 ID는 100001부터 999999 사이의 값이어야 합니다.");
        }
    }

    private void validateReceiverNumber(Integer receiverNumber, Errors errors) {
        if (receiverNumber == null || receiverNumber == 0) {
            errors.rejectValue("receiverNumber", "empty", "수취 계좌 ID를 입력하세요.");
            return;
        }

        if (receiverNumber < 100001 || receiverNumber > 999999) {
            errors.rejectValue("receiverNumber", "range",
                    "수취 계좌 ID는 100001부터 999999 사이의 값이어야 합니다.");
        }
    }

    private void validateAmount(Double amount, Errors errors) {
        if (amount == null) {
            errors.rejectValue("amount", "empty", "송금액을 입력하세요.");
            return;
        }

        if (amount <= 0) {
            errors.rejectValue("amount", "range",
                    "송금액은 0보다 큰 값이어야 합니다.");
        }
    }


    private void validateDifferentAccounts(Integer senderNumber, Integer receiverNumber, Errors errors) {
        if (senderNumber != null && receiverNumber != null && senderNumber.equals(receiverNumber)) {
            errors.rejectValue("receiverNumber", "same",
                    "송금 계좌와 수취 계좌는 달라야 합니다.");
        }
    }
}