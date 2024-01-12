package org.test.task.telegrambotcryptocurrency.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.test.task.telegrambotcryptocurrency.client.MexcClient;
import org.test.task.telegrambotcryptocurrency.entity.User;
import org.test.task.telegrambotcryptocurrency.exception.UserException;
import org.test.task.telegrambotcryptocurrency.model.MexcTokenDetails;
import org.test.task.telegrambotcryptocurrency.model.UserTokenDetails;
import org.test.task.telegrambotcryptocurrency.repository.UserRepository;

import java.math.BigDecimal;

@Service
@AllArgsConstructor
public class UserService {

    @Value("${bot.users.limit}")
    private final Integer userLimit;
    private final UserRepository userRepository;
    private final MexcClient mexcClient;
    private final ObjectMapper objectMapper;

    @Transactional
    public void registerUser(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {

            long chatId = update.getMessage().getChatId();

            if (userRepository.existsByUserId(chatId)) {
                throw new UserException("User already exists");
            } else {
                var usersNumber = userRepository.count();
                if (usersNumber >= userLimit) {
                    throw new UserException("Users limit exceeded");
                }
                var threshold = getThreshold(update.getMessage().getText());

                var user = User.builder()
                        .userId(chatId)
                        .threshold(threshold)
                        .cryptocurrency(getUserCryptocurrency(threshold))
                        .build();

                userRepository.save(user);
            }
        }
    }

    @Transactional
    public void refreshUserState(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();

            if (userRepository.existsByUserId(chatId)) {
                var threshold = getThreshold(update.getMessage().getText());

                var user = userRepository.findByUserId(chatId).get();
                user.setThreshold(threshold);
                user.setCryptocurrency(getUserCryptocurrency(threshold));
            } else {
                throw new UserException("User doesn't exist");
            }
        }
    }

    @Transactional
    public void deleteUser(long chatId) {
        userRepository.deleteByUserId(chatId);
    }

    @SneakyThrows
    private String getUserCryptocurrency(float threshold) {
        var mexCryptocurrency = mexcClient.getRequest();

        var userCryptocurrency = mexCryptocurrency.getCryptocurrency().stream()
                .map(token -> generateUserTokenDetails(token, threshold)).toList();

        return objectMapper.writeValueAsString(userCryptocurrency);
    }

    private Float getThreshold(String message) {
        return Float.parseFloat(message.split(" ")[1]);
    }

    private static UserTokenDetails generateUserTokenDetails(MexcTokenDetails mexcTokenDetails, float threshold) {
        var priceChange = mexcTokenDetails.getPrice().multiply(BigDecimal.valueOf(threshold)).divide(BigDecimal.valueOf(100));
        return UserTokenDetails.builder()
                .symbol(mexcTokenDetails.getSymbol())
                .lowRangePrice(mexcTokenDetails.getPrice().subtract(priceChange))
                .highRangePrice(mexcTokenDetails.getPrice().add(priceChange))
                .build();
    }
}
