### Export APIs (`/export`)

- POST `/export` (Authenticated)
  - Body: ExportCriteriaModel
  - Response: GuidResultModel (jobId)
  - Example:
    ```bash
    curl -X POST http://localhost:8080/export \
      -H 'Authorization: Bearer $TOKEN' \
      -H 'Content-Type: application/json' \
      -d '{"exportType":"USER_EXPORT","fileExtension":".xlsx","sendEmail":false,"sendTelegram":false}'
    ```

- GET `/export/get/{jobId}` (Authenticated)
  - Response: file content or GeneralResultModel when not ready

- GET `/export/sendEmail/{jobId}` (Authenticated)
  - Response: GeneralResultModel

- GET `/export/sendTelegram/{jobId}` (Authenticated)
  - Response: GeneralResultModel

