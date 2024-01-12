package org.test.task.telegrambotcryptocurrency.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class UserTokenDetails {

    @JsonProperty("symbol")
    private String symbol;

    @JsonProperty("low_price")
    private BigDecimal lowRangePrice;

    @JsonProperty("high_price")
    private BigDecimal highRangePrice;
}
