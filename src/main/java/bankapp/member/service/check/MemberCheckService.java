package bankapp.member.service.check;

import bankapp.account.model.Account;
import bankapp.member.model.Member;

public interface MemberCheckService {

    boolean isMemberIdExist(Long memberId);
    boolean isUsernameExist(String username);
    Member findMemberByAccount(Account account);
}
