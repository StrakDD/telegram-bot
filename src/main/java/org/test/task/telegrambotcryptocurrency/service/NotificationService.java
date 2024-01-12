package org.test.task.telegrambotcryptocurrency.service;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.test.task.telegrambotcryptocurrency.client.MexcClient;
import org.test.task.telegrambotcryptocurrency.entity.User;
import org.test.task.telegrambotcryptocurrency.model.MexcTokenDetails;
import org.test.task.telegrambotcryptocurrency.model.UserTokenDetails;
import org.test.task.telegrambotcryptocurrency.repository.UserRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class NotificationService {

    private final MexcClient mexcClient;
    private final ExecutorService executorService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final TelegramBotService telegramBotService;

    @Async
    @Scheduled(fixedRateString = "${bot.prices.polling.seconds}", timeUnit = TimeUnit.SECONDS)
    public void notifyUsers() {
        var mexcTokensPrice = mexcClient.getRequest().getCryptocurrency().stream()
                .collect(Collectors.toMap(MexcTokenDetails::getSymbol, MexcTokenDetails::getPrice));

        var users = userRepository.findAll();
        users.forEach(user -> executorService.submit(() -> compareTokenPrice(mexcTokensPrice, user)));
    }

    @SneakyThrows
    private void compareTokenPrice(Map<String, BigDecimal> mexcTokensPrice, User user) {
        var userTokensDetails = objectMapper.readValue(user.getCryptocurrency(), new TypeReference<List<UserTokenDetails>>() {
        });

        userTokensDetails.forEach(userToken -> {
            if (isNotBetween(mexcTokensPrice.get(userToken.getSymbol()), userToken.getLowRangePrice(), userToken.getHighRangePrice())) {
                telegramBotService.sendTelegramMessage(user.getUserId(), "Token %s price changed more than threshold %s percent".formatted(userToken.getSymbol(), user.getThreshold()));
            }
        });
    }

    public static boolean isNotBetween(BigDecimal price, BigDecimal lowerBound, BigDecimal upperBound) {
        return (price.compareTo(lowerBound) + upperBound.compareTo(price)) < 2;
    }
}
