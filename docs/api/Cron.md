### Cron Task APIs (`/cron`)

- PUT `/cron/add` (ADMIN)
  - Body: CronTaskPostModel
  - Response: GuidResultModel

- GET `/cron/findById/{id}` (Authenticated)
  - Response: CronTaskReturnModel

Example:
```bash
curl -X PUT http://localhost:8080/cron/add \
  -H 'Authorization: Bearer $TOKEN' \
  -H 'Content-Type: application/json' \
  -d '{"taskName":"USER_EXPORT","cronExpression":"0 */5 * * * *"}'
```

