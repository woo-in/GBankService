package bankapp.member.service.check;

import bankapp.member.dao.MemberDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultCheckService implements CheckService{

    private final MemberDao memberDao;

    @Autowired
    public DefaultCheckService(MemberDao memberDao){
        this.memberDao = memberDao;
    }

    public boolean isMemberIdExist(Long memberId){
        return memberDao.findByMemberId(memberId) != null;
    }

    public boolean isUsernameExist(String username){
        return memberDao.findByUsername(username) != null;
    }


}
