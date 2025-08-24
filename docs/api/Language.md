### Language APIs (`/languages`)

- PUT `/languages/add` (ADMIN)
  - Body: LanguageModelAdd
  - Response: GuidResultModel

- GET `/languages/searchById/{id}`
  - Response: LanguageModelReturn

- GET `/languages/searchByName/{name}`
  - Response: LanguageModelReturn

- POST `/languages/update` (ADMIN)
  - Body: LanguageModelReturn
  - Response: LanguageModelReturn

- DELETE `/languages/delete/{id}` (ADMIN)
  - Response: GeneralResultModel

- PUT `/languages/addList` (ADMIN)
  - Body: LanguageModelAdd[]
  - Response: LanguageModelReturn[]

- POST `/languages/criteria`
  - Body: LanguageCriteriaModel
  - Response: PageResult<LanguageModelReturn>

Examples:
```bash
curl -X PUT http://localhost:8080/languages/add \
  -H 'Authorization: Bearer $TOKEN' \
  -H 'Content-Type: application/json' \
  -d '{"name":"japan","regEx":"[а-яА-Я]+"}'
```

