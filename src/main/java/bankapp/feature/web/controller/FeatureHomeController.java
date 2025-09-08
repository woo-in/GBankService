package bankapp.feature.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/feature")
public class FeatureHomeController {


    @GetMapping
    public String showHome() {
        return "feature/feature-home";
    }

}
