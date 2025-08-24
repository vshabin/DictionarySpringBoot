### RabbitMQ APIs (`/rabbit`)

- POST `/rabbit/emit`
  - Body: DirectMessageModel
  - Response: string message
  - Example:
    ```bash
    curl -X POST http://localhost:8080/rabbit/emit \
      -H 'Content-Type: application/json' \
      -d '{"queueName":"telegramQueue","message":"Hello"}'
    ```

