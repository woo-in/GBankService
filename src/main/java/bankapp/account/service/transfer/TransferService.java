package bankapp.account.service.transfer;

import bankapp.account.exceptions.ExternalTransferNotSupportedException;
import bankapp.account.exceptions.PrimaryAccountNotFoundException;
import bankapp.account.exceptions.RecipientAccountNotFoundException;
import bankapp.account.exceptions.SameAccountTransferException;
import bankapp.account.model.PrimaryAccount;
import bankapp.account.request.transfer.TransferRecipientRequest;
import bankapp.member.model.Member;

public interface TransferService {

    /**
     * 수취인 계좌 정보를 처리하고 보내는 사람의 주계좌를 반환합니다.
     *
     * @param transferRecipientRequest 수취인 정보 요청 객체
     * @param loginMember              로그인한 사용자 정보 (보내는 사람)
     * @return 보내는 사람의 주계좌
     * @throws ExternalTransferNotSupportedException 타행 이체 미지원 시
     * @throws RecipientAccountNotFoundException   수취인 계좌 미발견 시
     * @throws SameAccountTransferException        동일 계좌 이체 시
     * @throws PrimaryAccountNotFoundException     보내는 사람의 주계좌 미발견 시
     */
    PrimaryAccount processRecipient(TransferRecipientRequest transferRecipientRequest, Member loginMember) throws ExternalTransferNotSupportedException, RecipientAccountNotFoundException, SameAccountTransferException, PrimaryAccountNotFoundException;



}
