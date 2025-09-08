package bankapp.feature.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class ExchangeRateDto {

    // 조회 결과
    @JsonProperty("result")
    private int result ;
    // 1 : 성공, 2 : DATA코드 오류, 3 : 인증코드 오류, 4 : 일일제한횟수

    // 통화 코드
    @JsonProperty("cur_unit")
    private String curUnit;

    // 국가/통화명
    @JsonProperty("cur_nm")
    private String curName;

    // 전신환(송금) 받으실 때
    @JsonProperty("ttb")
    private String ttb;

    // 전신환(송금) 보내실 때
    @JsonProperty("tts")
    private String tts;

    // 매매 기준율
    @JsonProperty("deal_bas_r")
    private String dealBasR;




}
