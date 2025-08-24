### Association APIs (`/associations`)

- PUT `/associations/add` (ADMIN)
  - Body: AssociationModelAdd
  - Response: GuidResultModel

- GET `/associations/searchById/{id}`
  - Response: AssociationModelReturn

- POST `/associations/update` (ADMIN)
  - Body: AssociationModelReturn
  - Response: AssociationModelReturn

- DELETE `/associations/delete/{id}` (ADMIN)
  - Response: GeneralResultModel

- PUT `/associations/addList` (ADMIN)
  - Body: AssociationModelAdd[]
  - Response: AssociationModelReturn[]

- POST `/associations/criteria`
  - Body: AssociationCriteriaModel
  - Response: PageResult<AssociationModelReturn>

- GET `/associations/searchByIdEnriched/{id}`
  - Response: AssociationModelReturnEnriched

- POST `/associations/translate`
  - Body: TranslationRequest
  - Response: PageResult<WordModelReturn>

Examples:
```bash
curl -X PUT http://localhost:8080/associations/add \
  -H 'Authorization: Bearer $TOKEN' \
  -H 'Content-Type: application/json' \
  -d '{"word":"<wordUUID>","translation":"<translationUUID>"}'
```

