package bankapp.core.config;

import bankapp.account.exceptions.IllegalTransferStateException;
import bankapp.account.exceptions.InvalidAmountException;
import bankapp.account.exceptions.PendingTransferNotFoundException;
import bankapp.account.exceptions.PrimaryAccountNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    // 처리하고자 하는 예외들을 배열로 묶어서 지정합니다.
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // 응답 상태 코드를 500으로 설정
    @ExceptionHandler({
            InvalidAmountException.class,
            PendingTransferNotFoundException.class,
            IllegalTransferStateException.class,
            IllegalStateException.class,
            PrimaryAccountNotFoundException.class,
            // 추가적으로 500 에러로 처리하고 싶은 다른 예외들도 여기에 추가할 수 있습니다.
            Exception.class // 가장 상위 타입인 Exception을 마지막에 두어 혹시 모를 다른 모든 서버 오류도 처리
    })
    public String handleServerException(Exception e) {
        // 서버에 어떤 오류가 발생했는지 로그를 남기는 것이 매우 중요합니다.
        log.error("서버 오류 발생: {}", e.getMessage(), e);
        // templates/error/500.html 파일을 뷰(View)로 반환합니다.
        return "error/500";
    }
}
