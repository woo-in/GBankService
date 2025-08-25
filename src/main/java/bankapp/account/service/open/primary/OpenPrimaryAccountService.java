package bankapp.account.service.open.primary;

import bankapp.account.exceptions.InvalidDepositAmountException;
import bankapp.account.model.PrimaryAccount;
import bankapp.account.request.open.OpenPrimaryAccountRequest;
import bankapp.member.exceptions.MemberNotFoundException;

/**
 * 신규 주계좌(Primary Account) 개설 비즈니스 로직의 명세를 정의합니다.
 */
public interface OpenPrimaryAccountService {

    /**
     * 사용자의 요청에 따라 신규 주계좌를 개설합니다.
     * 이 과정에서 요청 데이터의 유효성(예: 초기 입금액, 회원 존재 여부)을 검증합니다.
     *
     * @param openPrimaryAccountRequest 계좌 개설에 필요한 데이터를 담은 DTO (회원 ID, 초기 입금액 , 닉네임)
     * @return 성공적으로 개설된 주계좌 엔티티
     * @throws InvalidDepositAmountException 초기 입금액이 0원 미만일 경우
     * @throws MemberNotFoundException 요청된 회원 ID가 존재하지 않을 경우
     */
    PrimaryAccount openPrimaryAccount(OpenPrimaryAccountRequest openPrimaryAccountRequest) throws InvalidDepositAmountException, MemberNotFoundException;
}
