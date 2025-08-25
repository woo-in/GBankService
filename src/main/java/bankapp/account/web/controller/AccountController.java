package bankapp.account.web.controller;

import bankapp.core.common.SessionConst;
import bankapp.member.model.Member;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller
@RequestMapping("/account")
public class AccountController {


    // 통장 메인 화면을 보여주는 메서드
    @GetMapping
    public String showMainAccount(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER , required = false) Member loginMember
            ){

        // 1. 세션 확인 (방어적 코드)
        if(loginMember == null){
            return "redirect:/login";
        }

        // 2. 서비스 호출
        // (데이터 베이스에 접근해 , loginMember 에 해당하는 계좌정보를 account 에 담아야 함)


        // 3. 모델에 데이터 담기


        // 4. 뷰 반환
        return "main/account/account";
    }


}
