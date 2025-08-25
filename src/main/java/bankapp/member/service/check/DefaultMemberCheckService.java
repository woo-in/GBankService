package bankapp.member.service.check;

import bankapp.account.model.Account;
import bankapp.member.dao.MemberDao;
import bankapp.member.exceptions.MemberNotFoundException;
import bankapp.member.model.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DefaultMemberCheckService implements MemberCheckService {

    private final MemberDao memberDao;

    @Autowired
    public DefaultMemberCheckService(MemberDao memberDao){
        this.memberDao = memberDao;
    }

    @Override
    public boolean isMemberIdExist(Long memberId){
        if(memberId == null) {
            return false;
        }

        Optional<Member> memberOptional = memberDao.findByMemberId(memberId);
        return memberOptional.isPresent();
    }

    @Override
    public boolean isUsernameExist(String username){
        if(username == null) {
            return false;
        }

        Optional<Member> memberOptional = memberDao.findByUsername(username);
        return memberOptional.isPresent();
    }

    @Override
    public Member findMemberByAccount(Account account){
        if(account == null) {
            throw new MemberNotFoundException("멤버를 찾을 수 없습니다.");
        }

        Optional<Member> memberOptional = memberDao.findByMemberId(account.getMemberId());

        if(memberOptional.isEmpty()){
            throw new MemberNotFoundException("멤버를 찾을 수 없습니다.");
        }

        return memberOptional.get();
    }



}
