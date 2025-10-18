package bankapp.infra.client.bok;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Component
public class BokApiClient {

    private final WebClient.Builder webClientBuilder;

    private WebClient webClient;

    @Value("${external.api.koreaBank.auth-key}")
    private String authKey;

    @Value("${external.api.koreaBank.requestUrl}")
    private String requestUrl;


    @Autowired
    public BokApiClient(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @PostConstruct
    public void init() {
        // 이 시점에는 requestUrl 필드에 값이 정상적으로 주입되어 있습니다.
        this.webClient = webClientBuilder.baseUrl(requestUrl).build();
    }




    /**
     * 한국은행 경제통계시스템의 통계표 목록을 조회합니다.
     * @param lang 언어 (kr/en)
     * @param searchDate 통계 서칭 날짜
     * @param detailCode 세부 코드 (예 : 010151000)
     * @return API 응답을 Mono<BokInterestRateDto.SearchData> 형태로 반환
     */
    public Mono<BokInterestRateDto.SearchData> fetchDayInterestRate(String lang,
                                        LocalDate searchDate,
                                        String detailCode) {

        final String serviceName = "StatisticSearch";
        final String format = "json";
        final int reqStartCount = 1;
        final int reqEndCount = 1;
        final String code = "817Y002";
        final String period = "D";
        String formattedStartDate = searchDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String formattedEndDate = searchDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));


        return this.webClient.get()
                .uri("/{serviceName}/{authKey}/{format}/{lang}/{reqStartCount}/{reqEndCount}/{code}/{period}/{formattedStartDate}/{formattedEndDate}/{detailCode}",
                        // 2. 위 placeholder에 순서대로 값을 채워 넣습니다.
                        serviceName, authKey, format, lang, reqStartCount, reqEndCount, code , period , formattedStartDate , formattedEndDate,detailCode)
                .retrieve()
                .bodyToMono(BokInterestRateDto.class)
                .flatMap(response -> { // 2. flatMap으로 내부 데이터 안전하게 추출
                    return Optional.ofNullable(response.getStatisticSearch())
                            .map(BokInterestRateDto.StatisticSearch::getRow)
                            .filter(row -> !row.isEmpty())
                            .map(row -> Mono.just(row.get(0))) // 3. row 리스트의 첫 번째 데이터를 Mono로 감싸서 반환
                            .orElse(Mono.empty()); // 4. 데이터가 없으면 빈 Mono 반환
                });

    }



}
