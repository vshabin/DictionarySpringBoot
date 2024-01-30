package com.example.demo.domainservices;

import it.tdlight.client.SimpleAuthenticationSupplier;
import it.tdlight.client.SimpleTelegramClient;
import it.tdlight.client.SimpleTelegramClientBuilder;
import it.tdlight.jni.TdApi;

public class TelegramClient implements AutoCloseable {

    private final SimpleTelegramClient client;


    public TelegramClient(SimpleTelegramClientBuilder clientBuilder,
                          SimpleAuthenticationSupplier<?> authenticationData) {

        // Add an example update handler that prints when the bot is started
        clientBuilder.addUpdateHandler(TdApi.UpdateAuthorizationState.class, this::onUpdateAuthorizationState);

        // Add an example update handler that prints every received message
        clientBuilder.addUpdateHandler(TdApi.UpdateNewMessage.class, this::onUpdateNewMessage);

        // Build the client
        this.client = clientBuilder.build(authenticationData);
    }

    @Override
    public void close() throws Exception {
        client.close();
    }

    public SimpleTelegramClient getClient() {
        return client;
    }

    /**
     * Print the bot status
     */
    private void onUpdateAuthorizationState(TdApi.UpdateAuthorizationState update) {
        TdApi.AuthorizationState authorizationState = update.authorizationState;
        if (authorizationState instanceof TdApi.AuthorizationStateReady) {
            System.out.println("Logged in");
        } else if (authorizationState instanceof TdApi.AuthorizationStateClosing) {
            System.out.println("Closing...");
        } else if (authorizationState instanceof TdApi.AuthorizationStateClosed) {
            System.out.println("Closed");
        } else if (authorizationState instanceof TdApi.AuthorizationStateLoggingOut) {
            System.out.println("Logging out...");
        }
    }

    /**
     * Print new messages received via updateNewMessage
     */
    private void onUpdateNewMessage(TdApi.UpdateNewMessage update) {
        // Get the message content
        TdApi.MessageContent messageContent = update.message.content;

        // Get the message text
        String text;
        if (messageContent instanceof TdApi.MessageText messageText) {
            // Get the text of the text message
            text = messageText.text.text;
        } else {
            // We handle only text messages, the other messages will be printed as their type
            text = String.format("(%s)", messageContent.getClass().getSimpleName());
        }

        long chatId = update.message.chatId;

        // Get the chat title
        client.send(new TdApi.GetChat(chatId))
                // Use the async completion handler, to avoid blocking the TDLib response thread accidentally
                .whenCompleteAsync((chatIdResult, error) -> {
                    if (error != null) {
                        // Print error
                        System.err.printf("Can't get chat title of chat %s%n", chatId);
                        error.printStackTrace(System.err);
                    } else {
                        // Get the chat name
                        String title = chatIdResult.title;
                        // Print the message
                        System.out.printf("Received new message from chat %s (%s): %s%n", title, chatId, text);
                    }
                });
    }
}
