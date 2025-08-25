//package bankapp.account.service.transfer;
//
//import bankapp.account.exceptions.ExternalTransferNotSupportedException;
//import bankapp.account.exceptions.PrimaryAccountNotFoundException;
//import bankapp.account.exceptions.RecipientAccountNotFoundException;
//import bankapp.account.exceptions.SameAccountTransferException;
//import bankapp.account.model.PrimaryAccount;
//import bankapp.account.request.transfer.TransferRecipientRequest;
//import bankapp.account.service.check.AccountCheckService;
//import bankapp.core.util.AccountNumberFormatter;
//import bankapp.member.model.Member;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//@Component
//public class DefaultTransferService implements TransferService{
//
//    private final AccountCheckService accountCheckService;
//
//    @Autowired
//    public DefaultTransferService(AccountCheckService accountCheckService) {
//        this.accountCheckService = accountCheckService;
//    }
//
//    @Override
//    public PrimaryAccount processRecipient(TransferRecipientRequest transferRecipientRequest, Member loginMember)
//            throws ExternalTransferNotSupportedException, RecipientAccountNotFoundException, SameAccountTransferException, PrimaryAccountNotFoundException{
//
//        String toBankCode = transferRecipientRequest.getToBankCode();
//        String toAccountNumber = AccountNumberFormatter.format(transferRecipientRequest.getToAccountNumber());
//
//        if (accountCheckService.isExternalBank(toBankCode)) {
//            throw new ExternalTransferNotSupportedException("타행 이체는 현재 서비스 준비 중입니다.");
//        }
//
//        // 2. 수취인 계좌 존재 여부 확인
//        if (!accountManager.isAccountNumberExist(toAccountNumber)) {
//            throw new RecipientAccountNotFoundException("해당 계좌를 찾을 수 없습니다.");
//        }
//
//        // 3. 보내는 사람 주계좌 조회
//        PrimaryAccount fromAccount = accountManager.findPrimaryAccountByMemberId(loginMember.getMemberId());
//
//        // 4. 동일 계좌 이체 여부 확인
//        if (toAccountNumber.equals(fromAccount.getAccountNumber())) {
//            throw new SameAccountTransferException("동일한 계좌로는 송금할 수 없습니다.");
//        }
//
//        return fromAccount;
//    }
//
//
//}
