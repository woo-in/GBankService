package bankapp.account.web.controller;


import bankapp.account.manager.AccountManager;
import bankapp.account.model.Account;
import bankapp.account.request.transfer.TransferRecipientRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static bankapp.core.common.BankCode.WOOIN_BANK;

@Slf4j
@Controller
@RequestMapping("/transfer")
public class TransferController {

    private final AccountManager accountManager;

    @Autowired
    public TransferController(AccountManager accountManager) {
        this.accountManager = accountManager;
    }

    @GetMapping("/new")
    public String showTransferNewForm(Model model) {
        model.addAttribute("transferRecipientRequest", new TransferRecipientRequest());
        return "account/transfer/transfer-recipient-form";
    }

    @PostMapping("/new")
    public String processTransferNew(@Validated @ModelAttribute TransferRecipientRequest transferRecipientRequest,
                                     BindingResult bindingResult,
                                     HttpServletRequest request) {

        if(bindingResult.hasErrors()){
            // 값 유효하지 않음
            return "account/transfer/transfer-recipient-form";
        }

        // 1. 은행 - 계좌번호가 실제로 존재하는지 검증 (혹시 내 계좌도 아닌지 검증)
        // 2. 은행 정보와 계좌번호를 세션에 저장

        String tobankCode = transferRecipientRequest.getToBankCode();
        String toaccountNumber = transferRecipientRequest.getToAccountNumber();

        // 상수로 선언된 코드를 사용하여 당행/타행 여부 확인
        if (WOOIN_BANK.equals(tobankCode)) {
            // 본인 계좌로 이체하는 경우 체크

            // 1. 세션의 member_id 로 PRIMARY 계좌번호 알아내기
            // 세션 더 쉽게 알아내는 방법 있다. (노션 참조)





            if (!accountManager.isAccountNumberExist(toaccountNumber)) {
                // 계좌가 존재하지 않으면 오류 메시지와 함께 폼으로 돌려보냄
                bindingResult.rejectValue("toAccountNumber", "notFound", "해당 계좌를 찾을 수 없습니다.");
                return "account/transfer/transfer-recipient-form";
            }

            // TODO: 로그인한 사용자의 계좌(본인 계좌)로 이체하는 경우에 대한 예외 처리 필요

            Account recipientAccount = recipientAccountOpt.get();

            // 성공 시, 다음 단계로 예금주, 계좌번호, 은행코드 정보를 전달
            redirectAttributes.addFlashAttribute("recipientName", recipientAccount.getNickname());
            redirectAttributes.addFlashAttribute("recipientAccountNumber", recipientAccount.getAccountNumber());
            redirectAttributes.addFlashAttribute("toBankCode", bankCode);

            return "redirect:/transfer/amount"; // 금액 입력 폼으로 리다이렉트



        } else {
            // === 타행 이체 처리 (외부 API 연동 시뮬레이션) ===
            // 실제로는 Open Banking API 등을 호출하여 예금주 실명을 조회해야 합니다.
            // 현재 타행 이체는 지원하지 않으므로, 사용자에게 서비스 준비 중이라는 메시지를 보여줍니다.
            bindingResult.rejectValue("toBankCode", "Unavailable", "타행 이체는 현재 서비스 준비 중입니다.");
            return "account/transfer/transfer-recipient-form";
        }




    }


}
