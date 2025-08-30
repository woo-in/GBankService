package bankapp.account.service.transfer;

import bankapp.account.exceptions.*;
import bankapp.account.model.transfer.PendingTransfer;
import bankapp.account.request.transfer.TransferAmountRequest;
import bankapp.account.request.transfer.TransferAuthRequest;
import bankapp.account.request.transfer.TransferMessageRequest;
import bankapp.account.request.transfer.TransferRecipientRequest;
import bankapp.account.response.transfer.PendingTransferResponse;
import bankapp.member.exceptions.IncorrectPasswordException;
import bankapp.member.model.Member;


public interface TransferService {

    // TODO : 트랜잭션 단위 인터페이스에 설명

    /**
     * 송금 절차의 첫 단계를 처리하고, 임시 송금 요청을 생성합니다.
     * <p>
     * 이 메소드는 입력된 수취인 계좌의 유효성을 검증하고, 보내는 사람의 주계좌를 확인하는 등
     * 송금에 필요한 모든 사전 조건을 검사합니다. 모든 검증이 통과되면,
     * 송금의 상태와 정보를 추적할 {@link PendingTransfer} 레코드를 데이터베이스에 생성합니다.
     *
     * @param transferRecipientRequest 수취인 정보(은행, 계좌번호)가 담긴 요청 객체
     * @param loginMember              현재 로그인한 사용자 정보 (보내는 사람)
     * @return                         성공적으로 생성된 임시 송금 요청의 고유 ID (requestId).
     * 이 ID는 다음 송금 단계를 위해 세션에 저장됩니다.
     * @throws ExternalTransferNotSupportedException 타행 이체 미지원 시(정책상 현재 막혀있을 경우)
     * @throws RecipientAccountNotFoundException   입력된 수취인 계좌번호를 찾을 수 없을 때
     * @throws SameAccountTransferException        보내는 사람과 받는 사람의 계좌가 동일할 때
     * @throws PrimaryAccountNotFoundException     송금을 보낼 로그인 사용자의 주계좌를 찾을 수 없을 때
     */
    String processRecipient(TransferRecipientRequest transferRecipientRequest, Member loginMember) throws ExternalTransferNotSupportedException, RecipientAccountNotFoundException, SameAccountTransferException, PrimaryAccountNotFoundException;


    /**
     * 진행 중인 송금의 상세 정보를 조회합니다.
     * <p>
     * 세션에 저장된 requestId를 사용하여 현재 진행 중인 송금 정보를 데이터베이스에서 조회합니다.
     * 이 메소드는 송금의 각 단계(예: 금액 입력, 최종 확인)에서 사용자에게 이전 단계에서
     * 입력한 정보를 확인시켜주기 위해 호출됩니다.
     *
     * @param requestId 조회할 송금 요청의 고유 ID. 보통 사용자의 세션에 저장되어 있습니다.
     * @return          화면에 표시될 송금 상세 정보가 담긴 {@link PendingTransferResponse} DTO 객체.
     * @throws          PendingTransferNotFoundException 전달된 requestId에 해당하는 송금 정보가 존재하지 않을 경우.
     * @throws          PrimaryAccountNotFoundException 송금 정보에 담긴 계좌 정보가 실제 존재하지 않을 때
     */
    PendingTransferResponse getPendingTransferResponse(String requestId) throws PendingTransferNotFoundException , PrimaryAccountNotFoundException;


    /**
     * 송금 절차의 '금액 입력 및 검증'을 처리합니다.
     * <p>
     * 입력된 금액의 유효성을 검사하고, 출금 계좌의 잔액과 비교합니다.
     * 모든 검증을 통과하면, 진행 중인 임시 송금 정보({@link PendingTransfer})에 저장합니다.
     *
     * @param requestId             진행 중인 송금의 고유 ID
     * @param transferAmountRequest 사용자가 입력한 금액 정보가 담긴 DTO
     * @throws InvalidAmountException         송금액이 유효하지 않을 때 (0원 이하)
     * @throws PendingTransferNotFoundException 해당 requestId의 송금 정보를 찾을 수 없을 때
     * @throws IllegalTransferStateException  현재 송금 상태가 금액을 입력할 수 있는 단계가 아닐 때
     * @throws InsufficientBalanceException   출금 계좌의 잔액이 송금액보다 부족할 때
     * @throws IllegalStateException          데이터 정합성 문제 등 예기치 않은 서버 내부 오류 발생 시
     */
    void processAmount(String requestId, TransferAmountRequest transferAmountRequest) throws InsufficientBalanceException, PendingTransferNotFoundException , InvalidAmountException , IllegalTransferStateException , IllegalStateException;


    /**
     * 송금 절차의 '메시지 입력' 단계를 처리합니다.
     * <p>
     * 이 메서드는 사용자가 최종 확인 화면에서 입력한 메시지를 진행 중인 임시 송금 정보({@link PendingTransfer})에 저장합니다.
     *
     * @param requestId             진행 중인 송금의 고유 ID
     * @param transferMessageRequest 사용자가 입력한 메시지 정보가 담긴 DTO
     * @throws PendingTransferNotFoundException 전달된 requestId에 해당하는 송금 정보가 존재하지 않을 경우
     * @throws IllegalTransferStateException  현재 송금 상태가 메시지를 입력할 수 있는 단계가 아닐 때
     */
    void processMessage(String requestId, TransferMessageRequest transferMessageRequest) throws PendingTransferNotFoundException ,IllegalTransferStateException;



    /**
     * 사용자의 최종 인증(계좌 비밀번호)을 거쳐 실제 송금 거래를 실행하고 완료합니다.
     * <p>
     * 이 메서드는 송금 절차의 마지막 단계로,
     * 송금 요청에 대해 사용자의 비밀번호를 검증합니다.
     * <p>
     * 검증이 성공하면, 보내는 사람 계좌에서 금액을 출금(debit)하고 받는 사람 계좌에 입금(credit)하는
     * 실제 금융 거래를 수행합니다. 이 모든 과정은 하나의 트랜잭션으로 묶여 원자성(Atomicity)을 보장합니다.
     *
     * @param requestId             실행할 송금 요청의 고유 ID
     * @param transferAuthRequest   사용자의 계좌 비밀번호가 담긴 인증 요청 DTO
     * @throws PendingTransferNotFoundException 주어진 requestId에 해당하는 송금 요청이 없을 경우
     * @throws IllegalTransferStateException  인증 단계가 맞지 않을 경우
     * @throws IncorrectPasswordException     입력된 계좌 비밀번호가 실제 비밀번호와 일치하지 않을 경우
     */
    void executeTransfer(String requestId , TransferAuthRequest transferAuthRequest) throws PendingTransferNotFoundException , IllegalTransferStateException , IncorrectPasswordException;


}
