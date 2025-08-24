### Auth APIs (`/auth`)

- POST `/auth/login`
  - Body: JwtRequest
  - Response: JwtResponse
  - Example:
    ```bash
    curl -X POST http://localhost:8080/auth/login \
      -H 'Content-Type: application/json' \
      -d '{"login":"vshabin","password":"qwerty123"}'
    ```

- POST `/auth/refresh`
  - Auth: Bearer (access token) or cookie
  - Body: RefreshTokenModel
  - Response: JwtResponse

- POST `/auth/logout`
  - Auth: Bearer
  - Body: RefreshTokenModel
  - Response: GeneralResultModel

