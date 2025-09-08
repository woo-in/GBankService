package bankapp.feature.web.controller;

import bankapp.feature.dto.ExchangeRateDto;
import bankapp.feature.service.ExchangeRateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/feature/market-data")
public class MarketDataController {

    private final ExchangeRateService exchangeRateService;

    @Autowired
    public MarketDataController(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @GetMapping("/exchange-rates/today")
    public String getTodayExchangeRates(Model model) {
        List<ExchangeRateDto> rates = exchangeRateService.getTodayExchangeRates().block();
        model.addAttribute("exchangeRates", rates);
        model.addAttribute("now", LocalDateTime.now());
        return "feature/exchange-rate/exchange-rate";
    }

}
