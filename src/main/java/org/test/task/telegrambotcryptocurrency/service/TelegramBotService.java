package org.test.task.telegrambotcryptocurrency.service;

import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.test.task.telegrambotcryptocurrency.exception.UserException;

import java.util.Objects;

@Service
public class TelegramBotService extends TelegramLongPollingBot {

    private final String botUserName;
    private final UserService userService;

    public TelegramBotService(@Value("${bot.token}") String botToken, @Value("${bot.name}") String botUserName,
                              @Autowired UserService userService) {
        super(botToken);
        this.botUserName = botUserName;
        this.userService = userService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {

            var entities = update.getMessage().getEntities();
            long chatId = update.getMessage().getChatId();

            if (Objects.nonNull(entities) && !entities.isEmpty()) {
                String command = update.getMessage().getEntities().getFirst().getText();

                try {
                    switch (command) {
                        case "/startNotifier" -> userService.registerUser(update);
                        case "/restartNotifier" -> userService.refreshUserState(update);
                        case "/stop" -> userService.deleteUser(chatId);
                        case null, default -> sendTelegramMessage(chatId, "Unknown command");
                    }
                } catch (UserException e) {
                    sendTelegramMessage(chatId, e.getMessage());
                }
            } else {
                sendTelegramMessage(chatId, "Unknown command");
            }
        }
    }

    @Override
    public String getBotUsername() {
        return botUserName;
    }

    @SneakyThrows
    public void sendTelegramMessage(Long chatId, String message) {
        SendMessage sendMessage = new SendMessage(chatId.toString(), message);
        execute(sendMessage);
    }

    @PostConstruct
    @SneakyThrows
    public void init() {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(this);
    }
}
