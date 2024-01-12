package org.test.task.telegrambotcryptocurrency.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.test.task.telegrambotcryptocurrency.model.MexcCryptocurrency;
import org.test.task.telegrambotcryptocurrency.model.MexcTokenDetails;

import java.util.List;

@Component
@AllArgsConstructor
public class MexcClient {

    private ObjectMapper objectMapper;

    @SneakyThrows
    public MexcCryptocurrency getRequest() {
        var response = RestClient.create().get()
                .uri("https://api.mexc.com/api/v3/ticker/price")
                .retrieve()
                .body(String.class);

        var tokensDetails = objectMapper.readValue(response, new TypeReference<List<MexcTokenDetails>>() {
        });

        return MexcCryptocurrency.builder().cryptocurrency(tokensDetails).build();
    }
}
