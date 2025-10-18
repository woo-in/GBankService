package bankapp.infra.client.bok;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
public class BokInterestRateDto {

    @JsonProperty("StatisticSearch")
    private StatisticSearch statisticSearch;


    @Data
    @NoArgsConstructor
    public static class StatisticSearch {
        @JsonProperty("list_total_count")
        private int listTotalCount;

        @JsonProperty("row")
        private List<SearchData> row;

    }

    @Data
    @NoArgsConstructor
    public static class SearchData {
        @JsonProperty("STAT_NAME")
        private String statisticName; // 통계명 (e.g., "1.3.2.1. 시장금리(일별)")

        @JsonProperty("ITEM_NAME1")
        private String itemName; // 항목명 (e.g., "CD(91일)")

        @JsonProperty("UNIT_NAME")
        private String unitName; // 단위 (e.g., "연%")

        @JsonProperty("TIME")
        private String time; // 조회 시점 (e.g., "20251017")

        @JsonProperty("DATA_VALUE")
        private String dataValue; // 실제 금리 값 (e.g., "2.54")
    }



}
