package bankapp.controller;

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

}
