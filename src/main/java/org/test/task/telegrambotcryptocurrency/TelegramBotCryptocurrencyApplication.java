package org.test.task.telegrambotcryptocurrency;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableJpaAuditing
@EnableScheduling
@EnableAsync
@SpringBootApplication
public class TelegramBotCryptocurrencyApplication {

    public static void main(String[] args) {
        SpringApplication.run(TelegramBotCryptocurrencyApplication.class, args);
    }

}
