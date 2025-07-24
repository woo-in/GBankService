package bankapp.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/menu")
public class MenuController {
    // 메뉴 출력
    @GetMapping("")
    public String showMenu(){
        return "menu/menu";
    }
}
