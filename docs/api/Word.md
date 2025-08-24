### Word APIs (`/words`)

- PUT `/words/add` (ADMIN)
  - Body: WordModelPost
  - Response: GuidResultModel

- GET `/words/searchById/{id}`
  - Response: WordModelReturn

- GET `/words/searchByName/{name}`
  - Response: WordModelReturn

- POST `/words/update` (ADMIN)
  - Body: WordModelReturn
  - Response: WordModelReturn

- DELETE `/words/delete/{id}` (ADMIN)
  - Response: GeneralResultModel

- PUT `/words/addList` (ADMIN)
  - Body: WordModelPost[]
  - Response: WordModelReturn[]

- POST `/words/criteria`
  - Body: WordCriteriaModel
  - Response: PageResult<WordModelReturn>

- GET `/words/searchByIdEnriched/{id}`
  - Response: WordModelReturnEnriched

- GET `/words/searchByNameEnriched/{name}`
  - Response: WordModelReturnEnriched

Examples:
```bash
curl -X PUT http://localhost:8080/words/add \
  -H 'Authorization: Bearer $TOKEN' \
  -H 'Content-Type: application/json' \
  -d '{"word":"Собака","languageId":"1e723432-ed5e-420e-9cf8-3a51ff669735"}'
```

