package bankapp.feature.service;


import bankapp.feature.dto.ExchangeRateDto;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Service
public class ExchangeRateService {

    private final WebClient.Builder webClientBuilder;

    private  WebClient webClient;

    @Value("${external.api.koreaexim.auth-key}")
    private String authKey;

    @Value("${external.api.koreaexim.requestUrl}")
    private String requestUrl;

    @Autowired
    public ExchangeRateService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }


    /**
     * 모든 의존성 주입이 완료된 후 WebClient를 초기화합니다.
     */
    @PostConstruct
    public void init() {
        // 이 시점에는 requestUrl 필드에 값이 정상적으로 주입되어 있습니다.
        this.webClient = webClientBuilder.baseUrl(requestUrl).build();
    }


    /**
     * 특정 날짜의 환율 정보를 조회합니다.
     * @param searchDate 조회할 날짜
     * @return 환율 정보 DTO 리스트를 담은 Mono
     */
    public Mono<List<ExchangeRateDto>> getDailyExchangeRates(LocalDate searchDate) {
        // API가 요구하는 날짜 형식(yyyyMMdd)으로 포맷합니다.
        String formattedDate = searchDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        return webClient.get()
                // URI를 파라미터와 함께 안전하게 구성합니다.
                .uri(uriBuilder -> uriBuilder
                        .queryParam("authkey", authKey)
                        .queryParam("searchdate", formattedDate)
                        .queryParam("data", "AP01") // 데이터 타입: 환율
                        .build())
                .retrieve() // 요청을 실행하고 응답을 받습니다.
                // 응답 본문을 Flux<ExchangeRateDto> 형태로 변환합니다.
                // 응답이 JSON 배열이므로 Flux(0...N개의 데이터 스트림)로 받습니다.
                .bodyToFlux(ExchangeRateDto.class)
                // Flux의 모든 아이템을 List로 수집합니다.
                .collectList();

    }

    // 오늘 날짜의 환율 정보를 가져오는 편의 메서드
    public Mono<List<ExchangeRateDto>> getTodayExchangeRates() {
        return getDailyExchangeRates(LocalDate.now());
    }

}
