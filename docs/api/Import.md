### Import APIs (`/import`)

- POST `/import` (Authenticated)
  - Multipart/form-data: file, type=ImportType
  - Response: GuidResultModel (jobId)
  - Example:
    ```bash
    curl -X POST http://localhost:8080/import \
      -H 'Authorization: Bearer $TOKEN' \
      -F file=@/path/to/file.xlsx \
      -F type=DICTIONARY_IMPORT
    ```

- GET `/import/get/{jobId}` (Authenticated)
  - Response: file content or GeneralResultModel

