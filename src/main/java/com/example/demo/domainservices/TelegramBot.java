package com.example.demo.domainservices;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendContact;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Log4j2
public class TelegramBot extends TelegramLongPollingBot {

    @Value("${telegram.bot.username}")
    private String username;
    @Value("${telegram.bot.token}")
    private String token;
    @Value("${telegram.bot.chat}")
    private long chatId;

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    public void sendMessage(String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    public void sendMessage(String phoneNumber, String message, InputFile attachment) throws TelegramApiException {
        SendContact sendContact = new SendContact();
        sendContact.setPhoneNumber(phoneNumber);
        sendContact.setChatId(chatId);
        sendContact.setFirstName("a");
        sendContact.setLastName("a");
        sendContact.setDisableNotification(true);

        var messageObject = execute(sendContact);
        var userId = messageObject.getContact().getUserId();
        var deleteMessage = new DeleteMessage();
        deleteMessage.setMessageId(messageObject.getMessageId());
        deleteMessage.setChatId(chatId);
        execute(deleteMessage);

        SendDocument sendDocument = new SendDocument();
        sendDocument.setChatId(userId);
        sendDocument.setCaption(message);
        sendDocument.setDocument(attachment);

        execute(sendDocument);
    }

    @Override
    public void onUpdateReceived(Update update) {
    }
}
