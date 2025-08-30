package bankapp.account.dao.transfer;


import bankapp.account.model.transfer.PendingTransfer;

import java.util.Optional;

/**
 * 임시 송금 데이터(PENDING_TRANSFER)데이터에 접근하는 데이터 접근 객체(DAO) 의 명세를 정의
 */
public interface PendingTransferDao {

    // 기본 입력을 저장하는 메서드를 만들까해.

    /**
     * 필수 초기 데이터를 PENDING_TRANSFER 테이블에 저장합니다.
     *
     * @param pendingTransfer 저장할 임시 송금 객체.
     * 이 객체에는 최소한 request_id, sender_member_id, sender_account_id, status, expires_at 값이 반드시 포함되어야 합니다.
     * @return 데이터베이스에 성공적으로 저장된 PendingTransfer 객체.
     * (DB에서 자동 생성된 created_at 값 등이 포함되어 반환됩니다)
     */
    PendingTransfer init(PendingTransfer pendingTransfer);


    /**
     * 고유 ID(request_id)를 사용하여 특정 임시 송금 정보를 조회합니다.
     * 송금의 다음 단계를 진행할 때 세션에 저장된 ID로 현재 상태를 불러오는 데 사용됩니다.
     *
     * @param requestId 조회할 임시 송금의 고유 ID
     * @return 조회된 PendingTransfer 객체를 담은 Optional. 존재하지 않으면 Optional.empty() 반환.
     */
    Optional<PendingTransfer> findById(String requestId);

    /**
     * 기존 임시 송금 정보의 상태나 내용을 갱신(UPDATE)합니다.
     * (예: 금액 추가, 메시지 추가, 최종 상태 변경 등)
     *
     * @param pendingTransfer 갱신할 정보를 담은 임시 송금 객체
     * @return 갱신된 PendingTransfer 객체
     */
    PendingTransfer update(PendingTransfer pendingTransfer);



}
