package bankapp.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class MenuController {
    // 메뉴 출력
    @GetMapping({"/", "/menu"})
    public String showMenu(){
        return "menu/menu";
    }

    // 임시(로그인 성공 출력)
    @GetMapping("/home")
    public String showHome(){
        return "menu/home";
    }


}
