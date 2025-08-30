package bankapp.account.web.controller;

import bankapp.account.exceptions.*;
import bankapp.account.request.transfer.TransferAmountRequest;
import bankapp.account.request.transfer.TransferAuthRequest;
import bankapp.account.request.transfer.TransferMessageRequest;
import bankapp.account.request.transfer.TransferRecipientRequest;
import bankapp.account.response.transfer.PendingTransferResponse;
import bankapp.account.response.transfer.TransferResultResponse;
import bankapp.account.service.transfer.TransferService;
import bankapp.core.common.SessionConst;
import bankapp.member.model.Member;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/transfer")
public class TransferController {

    private final TransferService transferService;


    @Autowired
    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @GetMapping("/new")
    public String showRecipientForm(Model model) {
        model.addAttribute("transferRecipientRequest", new TransferRecipientRequest());
        return "account/transfer/transfer-recipient-form";
    }


    /**
     * 송금 1단계: 수취인 계좌 정보 입력 및 유효성 검증을 처리합니다.
     *  그리고 세션에 송금 요청의 고유 id 를 저장합니다.
     *
     * @param transferRecipientRequest 수취인 정보(은행, 계좌번호)를 담은 DTO
     * @param bindingResult            DTO의 형식(@Validated) 검증 결과
     * @param request                  세션을 얻기 위한 객체
     * @return 검증 성공 시 금액 입력 폼으로 리다이렉트, 실패 시 다시 입력 폼을 보여줌
     */
    @PostMapping("/new")
    public String processRecipientInfo(@Validated @ModelAttribute TransferRecipientRequest transferRecipientRequest,
                                     BindingResult bindingResult,
                                     HttpServletRequest request) {


        if (bindingResult.hasErrors()) {
            return "account/transfer/transfer-recipient-form";
        }

        HttpSession session = request.getSession(false);
        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        String requestId ;
        try{
            requestId = transferService.processRecipient(transferRecipientRequest, loginMember);
        }catch (ExternalTransferNotSupportedException e){
            bindingResult.rejectValue("toBankCode", "Unavailable", "타행 이체는 현재 서비스 준비 중입니다.");
            return "account/transfer/transfer-recipient-form";
        }catch (RecipientAccountNotFoundException e){
            bindingResult.rejectValue("toAccountNumber", "notFound", "해당 계좌를 찾을 수 없습니다.");
            return "account/transfer/transfer-recipient-form";
        }catch(SameAccountTransferException e){
            bindingResult.rejectValue("toAccountNumber", "Same", "동일한 계좌로는 송금할 수 없습니다.");
            return "account/transfer/transfer-recipient-form";
        }
        // catch 하지 않은 에러 : 500 페이지 보여줌

        session.setAttribute("requestId", requestId);
        return "redirect:/transfer/amount";
    }

    @GetMapping("/amount")
    public String showAmountForm(Model model, HttpServletRequest request) {

        HttpSession session = request.getSession(false);
        // TransferRequestIdCheckInterceptor 로 유효성 검증함
        String requestId = (String) session.getAttribute("requestId");
        // TODO : 만약 홈으로 갔다가 다시 돌아온다면 ?

        prepareTransferDetailsViewModel(model , requestId);
        model.addAttribute("transferAmountRequest", new TransferAmountRequest());

        return "account/transfer/transfer-amount-form";
    }


    @PostMapping("/amount")
    public String processAmountInfo(@Validated @ModelAttribute TransferAmountRequest transferAmountRequest,
                                    BindingResult bindingResult,
                                    HttpServletRequest request,
                                    Model model) {

        HttpSession session = request.getSession(false);
        // TransferRequestIdCheckInterceptor 로 유효성 검증함
        String requestId = (String) session.getAttribute("requestId");

        if (bindingResult.hasErrors()) {
            prepareTransferDetailsViewModel(model , requestId);
            return "account/transfer/transfer-amount-form";
        }


        try{
            transferService.processAmount(requestId, transferAmountRequest);
        }catch (InsufficientBalanceException e){
            bindingResult.rejectValue("amount", "invalid", "잔액이 부족합니다.");
            prepareTransferDetailsViewModel(model , requestId);
            return "account/transfer/transfer-amount-form";
        }

        return "redirect:/transfer/message";
    }

    @GetMapping("/message")
    public String showMessageForm(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        // TransferRequestIdCheckInterceptor 로 유효성 검증함
        String requestId = (String) session.getAttribute("requestId");

        prepareTransferDetailsViewModel(model , requestId);
        return "account/transfer/transfer-message-form";
    }

    @PostMapping("/message")
    public String processMessageInfo(@Validated @ModelAttribute TransferMessageRequest transferMessageRequest,
                                     BindingResult bindingResult,
                                     HttpServletRequest request,
                                     Model model){

        HttpSession session = request.getSession(false);
        // TransferRequestIdCheckInterceptor 로 유효성 검증함
        String requestId = (String) session.getAttribute("requestId");


        if (bindingResult.hasErrors()) {
            prepareTransferDetailsViewModel(model , requestId);
            return "account/transfer/transfer-message-form";
        }

        transferService.processMessage(requestId, transferMessageRequest);

        return "redirect:/transfer/auth";
    }

    @GetMapping("auth")
    public String showAuthForm(Model model) {
        model.addAttribute("transferAuthRequest", new TransferAuthRequest());
        return "account/transfer/transfer-auth-form";
    }

    @PostMapping("execute")
    public String executeTransfer(@Validated @ModelAttribute TransferAuthRequest transferAuthRequest,
                                  BindingResult bindingResult ,
                                  HttpServletRequest request,
                                  Model model){

        if (bindingResult.hasErrors()) {
            return "account/transfer/transfer-auth-form";
        }

        HttpSession session = request.getSession(false);

        // TransferRequestIdCheckInterceptor 로 유효성 검증함
        String requestId = (String) session.getAttribute("requestId");

        // TODO: 예외처리
        // TODO : 최종 상태 완료로 바꾸고 기존 테이블 삭제 , 완료 테이블에 삽입 , 최종 결과 객체 생성
        transferService.executeTransfer(requestId, transferAuthRequest);


        // 결과 보여주기 (임시)
        return "account/transfer/transfer-complete-form";

    }

  


    private void prepareTransferDetailsViewModel(Model model , String requestId){
        PendingTransferResponse pendingTransferResponse = transferService.getPendingTransferResponse(requestId);
        model.addAttribute("pendingTransferResponse", pendingTransferResponse);
    }



}
