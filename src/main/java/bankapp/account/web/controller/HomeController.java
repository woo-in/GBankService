package bankapp.account.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


// 임시 컨트롤러
@Controller
@RequestMapping("/")
public class HomeController {

    @GetMapping
    public String showHome(){
        return "account/home/home";
    }

}
