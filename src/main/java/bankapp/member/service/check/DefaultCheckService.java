package bankapp.member.service.check;

import bankapp.member.dao.MemberDao;
import bankapp.member.model.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DefaultCheckService implements CheckService{

    private final MemberDao memberDao;

    @Autowired
    public DefaultCheckService(MemberDao memberDao){
        this.memberDao = memberDao;
    }

    public boolean isMemberIdExist(Long memberId){
        if(memberId == null) {
            return false;
        }

        Optional<Member> memberOptional = memberDao.findByMemberId(memberId);
        return memberOptional.isPresent();
    }

    public boolean isUsernameExist(String username){
        if(username == null) {
            return false;
        }

        Optional<Member> memberOptional = memberDao.findByUsername(username);
        return memberOptional.isPresent();
    }

}
