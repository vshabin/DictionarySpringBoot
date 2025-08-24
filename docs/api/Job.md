### Job APIs (`/jobs`)

- GET `/jobs/findById/{id}` (Authenticated)
  - Response: JobModelReturn

- PUT `/jobs/add` (ADMIN)
  - Body: JobModelPost
  - Response: GuidResultModel

- POST `/jobs/cancel/{id}` (ADMIN)
  - Response: GuidResultModel

- GET `/jobs/criteria` (Authenticated)
  - Body: JobCriteriaModel
  - Response: PageResult<JobEnrichedModel>

Examples:
```bash
curl -H 'Authorization: Bearer $TOKEN' \
  http://localhost:8080/jobs/findById/1e723432-ed5e-420e-9cf8-3a51ff669735
```

