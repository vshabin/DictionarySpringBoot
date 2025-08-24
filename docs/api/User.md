### User APIs (`/user`)

- GET `/user/searchById/{id}` (ADMIN)
  - Response: UserModelReturn
  - Example:
    ```bash
    curl -H 'Authorization: Bearer $TOKEN' \
      http://localhost:8080/user/searchById/1e723432-ed5e-420e-9cf8-3a51ff669735
    ```

- GET `/user/searchByLogin/{login}` (ADMIN)
  - Response: UserModelReturn

- PUT `/user/add` (PermitAll)
  - Body: UserModelPost
  - Response: GuidResultModel
  - Example:
    ```bash
    curl -X PUT http://localhost:8080/user/add \
      -H 'Content-Type: application/json' \
      -d '{"login":"vshabin","password":"qwerty123","fullName":"Иванов Иван","role":"ADMIN"}'
    ```

