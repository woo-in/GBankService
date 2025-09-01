package bankapp.account.web.controller;

import bankapp.account.response.account.AccountResponse;
import bankapp.account.service.check.AccountCheckService;
import bankapp.core.common.SessionConst;
import bankapp.member.model.Member;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/")
public class HomeController {

    private final AccountCheckService accountCheckService;

    public HomeController(AccountCheckService accountCheckService) {
        this.accountCheckService = accountCheckService;
    }

    @GetMapping
    public String showHome(HttpServletRequest  request, Model model) {

        HttpSession session = request.getSession(false);
        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);

        AccountResponse accountResponse = AccountResponse.from(accountCheckService.findPrimaryAccountByMemberId(loginMember.getMemberId()));
        model.addAttribute("accountResponse", accountResponse);

        return "account/home/home";
    }

    private void prepareHomeDetailsViewModel(Model model){
        //AccountResponse accountResponse = accountCheckService.
        //model.addAttribute("pendingTransferResponse", pendingTransferResponse);
    }

}
