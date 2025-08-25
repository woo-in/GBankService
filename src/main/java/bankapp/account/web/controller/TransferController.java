//package bankapp.account.web.controller;
//
//
//import bankapp.account.exceptions.PrimaryAccountNotFoundException;
//import bankapp.account.manager.AccountManager;
//import bankapp.account.model.Account;
//import bankapp.account.model.PrimaryAccount;
//import bankapp.account.request.transfer.TransferAmountRequest;
//import bankapp.account.request.transfer.TransferRecipientRequest;
//import bankapp.core.common.SessionConst;
//import bankapp.core.util.AccountNumberFormatter;
//import bankapp.core.util.BankNameConverter;
//import bankapp.member.manager.MemberManager;
//import bankapp.member.model.Member;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpSession;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.validation.BindingResult;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//import static bankapp.core.common.BankCode.WOOIN_BANK;
//
//@Slf4j
//@Controller
//@RequestMapping("/transfer")
//public class TransferController {
//
//    private final AccountManager accountManager;
//    private final MemberManager memberManager;
//
//    @Autowired
//    public TransferController(AccountManager accountManager , MemberManager memberManager) {
//        this.accountManager = accountManager;
//        this.memberManager = memberManager;
//    }
//
//    @GetMapping("/new")
//    public String showTransferNewForm(Model model) {
//        model.addAttribute("transferRecipientRequest", new TransferRecipientRequest());
//        return "account/transfer/transfer-recipient-form";
//    }
//
//    @PostMapping("/new")
//    public String processTransferNew(@Validated @ModelAttribute TransferRecipientRequest transferRecipientRequest,
//                                     BindingResult bindingResult,
//                                     HttpServletRequest request) {
//
//
//        if (bindingResult.hasErrors()) {
//            // 값 유효하지 않음
//            return "account/transfer/transfer-recipient-form";
//        }
//
//        String toBankCode = transferRecipientRequest.getToBankCode();
//        String toAccountNumber = AccountNumberFormatter.format(transferRecipientRequest.getToAccountNumber());
//
//        // 상수로 선언된 코드를 사용하여 당행/타행 여부 확인
//        if (accountManager.isExternalBank(toBankCode)) {
//            // === 타행 이체 처리 (외부 API 연동 시뮬레이션) ===
//            // 실제로는 Open Banking API 등을 호출하여 예금주 실명을 조회해야 합니다.
//            // 현재 타행 이체는 지원하지 않으므로, 사용자에게 서비스 준비 중이라는 메시지를 보여줍니다.
//            bindingResult.rejectValue("toBankCode", "Unavailable", "타행 이체는 현재 서비스 준비 중입니다.");
//            return "account/transfer/transfer-recipient-form";
//        }
//
//        // 세션 얻기
//        HttpSession session = request.getSession(false);
//
//        if (session == null) {
//            // 세션이 없음
//            return "redirect:/login";
//        }
//
//        // 세션에서 값 얻기
//        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
//
//        // 1. 은행 - 계좌번호가 실제로 존재하는지 검증 (혹시 내 계좌도 아닌지 검증)
//
//        if (!accountManager.isAccountNumberExist(toAccountNumber)) {
//            // 계좌가 존재하지 않으면 오류 메시지와 함께 폼으로 돌려보냄
//            bindingResult.rejectValue("toAccountNumber", "notFound", "해당 계좌를 찾을 수 없습니다.");
//            return "account/transfer/transfer-recipient-form";
//        }
//
//        // 계좌 번호 동일 체크
//        PrimaryAccount fromAccount;
//        try {
//            fromAccount = accountManager.findPrimaryAccountByMemberId(loginMember.getMemberId());
//        } catch (PrimaryAccountNotFoundException e) {
//            // 계좌에 해당하는 PRIMARY 계좌가 존재하지 않음
//            return "redirect:/error";
//        }
//
//        if (toAccountNumber.equals(fromAccount.getAccountNumber())) {
//            bindingResult.rejectValue("toAccountNumber", "Same", "동일한 계좌로는 송금할 수 없습니다.");
//            return "account/transfer/transfer-recipient-form";
//        }
//
//        // 성공 (세션에 저장)
//        request.setAttribute("transferRecipientRequest", transferRecipientRequest);
//        request.setAttribute("fromAccount", fromAccount);
//
//
//        // 정상적인 상황에서의 세션 저장 값
//        // LOGIN_MEMBER : Member (보내는 멤버 정보)
//        // transferRecipientRequest : transferRecipientRequest (받는 계좌 은행 , 번호)
//        // fromAccount : fromAccount (보내는 계좌 정보)
//
//        return "redirect:/transfer/amount"; // 금액 입력 폼으로 리다이렉트
//    }
//
//    @GetMapping("/amount")
//    public String showTransferAmountForm(Model model, HttpServletRequest request) {
//
//
//        // 세션 얻기
//        HttpSession session = request.getSession(false);
//
//        if (session == null) {
//            // 세션이 없음
//            return "redirect:/login";
//        }
//
//        TransferRecipientRequest transferRecipientRequest = (TransferRecipientRequest) request.getAttribute("transferRecipientRequest");
//        PrimaryAccount fromAccount = (PrimaryAccount) request.getAttribute("fromAccount");
//
//
//        // 1. Flash Attribute가 Model에 잘 담겼는지 확인 (안전장치)
//        //    만약 이전 단계를 건너뛰고 이 URL로 직접 접근했다면,
//        if ((transferRecipientRequest) == null || (fromAccount) == null) {
//            // 데이터가 없음(비정상적 접근)
//            return "redirect:/transfer/new";
//        }
//
//
//        String toBankCode = transferRecipientRequest.getToBankCode();
//        String toBankName = BankNameConverter.getBankNameByCode(toBankCode);
//        String formattedToAccountNumber = AccountNumberFormatter.format(transferRecipientRequest.getToAccountNumber());
//
//        if(!WOOIN_BANK.equals(toBankCode)){
//            // 타행 계좌 (비정상적 접근)
//            return "redirect:/transfer/new";
//        }
//
//        // 2. 계좌번호 바탕으로 수취 계좌 및 수취 계정(Member)을 모델,세션에 추가
//        Account toAccount = accountManager.findAccountByAccountNumber(formattedToAccountNumber);
//        Member toMember = memberManager.findMemberByAccount(toAccount);
//
//        model.addAttribute("toMember", toMember);
//        request.setAttribute("toAccount", toAccount);
//        request.setAttribute("toMember", toMember);
//
//
//        // 3. TransferAmountRequest 객체를 모델,세션에 추가
//        model.addAttribute("transferAmountRequest", new TransferAmountRequest());
//        request.setAttribute("transferAmountRequest", new TransferAmountRequest());
//
//        // 4. 화면 표시를 위한 은행이름 , 포멧 계좌번호 모델에 추가
//        model.addAttribute("toBankName", toBankName);
//        model.addAttribute("formattedToAccountNumber", formattedToAccountNumber);
//
//        // 6. 뷰 반환
//        return "account/transfer/transfer-amount-form";
//    }
//
////
////    @PostMapping("/amount")
////    public String processTransferAmount(@Validated @ModelAttribute TransferAmountRequest transferAmountRequest,
////                                        BindingResult bindingResult,
////                                        HttpServletRequest request,
////                                        RedirectAttributes redirectAttributes) {
////
////
////        // 1. 금액 값 검증
////        // 2. 금액 유효성 검사
////        // 3. 다음 으로 리다이렉트
////
////    }
//
//
//
//
//
//}
