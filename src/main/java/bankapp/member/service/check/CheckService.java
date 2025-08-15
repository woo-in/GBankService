package bankapp.member.service.check;

public interface CheckService {

    boolean isMemberIdExist(Long memberId);
    boolean isUsernameExist(String username);

}
