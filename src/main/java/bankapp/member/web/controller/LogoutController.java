package bankapp.member.web.controller;

import bankapp.core.common.SessionConst;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


/**
 * 사용자 로그아웃과 관련된 웹 요청을 처리하는 컨트롤러
 */
@Slf4j
@Controller("/logout")
public class LogoutController {

    /**
     * 로그아웃 (세션 초기화)
     * @param session HTTP 세션
     * @return 로그인 성공 시 , /login 으로 redirect
     */
    @PostMapping("")
    public String processLogout(HttpSession session) {
        session.setAttribute(SessionConst.LOGIN_MEMBER, null);
        return "redirect:/login";
    }
}

/*
 * TODO: (보안) 브라우저 뒤로가기 시 캐시된 개인정보 페이지가 노출되는 문제 해결
 *
 * [문제 현상]
 * - Safari 브라우저에서 '로그아웃 > 다른 계정으로 로그인' 후, 뒤로가기 버튼을 누르면
 * 이전 사용자의 홈 화면이 그대로 노출됨.
 *
 * [원인]
 * - 서버 세션은 정상적으로 변경되었으나, Safari의 강력한 BFCache(Back/Forward Cache) 정책이
 * 서버에 재요청 없이 메모리에 저장된 스냅샷을 보여주기 때문.
 *
 * [해결 방안]
 * - 모든 응답(Response)에 Cache-Control 헤더를 추가하여 브라우저의 캐싱을 막는 Filter 구현 필요.
 * - 예: response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
 */

