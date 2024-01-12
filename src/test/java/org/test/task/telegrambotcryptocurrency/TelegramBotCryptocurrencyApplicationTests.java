package org.test.task.telegrambotcryptocurrency;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.test.task.telegrambotcryptocurrency.client.MexcClient;
import org.test.task.telegrambotcryptocurrency.repository.UserRepository;
import org.test.task.telegrambotcryptocurrency.service.TelegramBotService;

@SpringBootTest
class TelegramBotCryptocurrencyApplicationTests {

    @Autowired
    private MexcClient mexcClient;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TelegramBotService telegramBotService;

    @Test
    @SneakyThrows
    void contextLoads() {


        telegramBotService.execute(new SendMessage("333413125", "hel"));
    }

}
