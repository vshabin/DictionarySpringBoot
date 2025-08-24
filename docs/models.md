### Common

- GeneralResultModel
  - errorCode: string | null
  - errorMessage: string | null

- GuidResultModel extends GeneralResultModel
  - id: UUID | null

- PageResult<T> extends GeneralResultModel
  - pageContent: T[]
  - totalCount: number

### Auth

- JwtRequest
  - login: string
  - password: string

- JwtResponse extends GeneralResultModel
  - type: "Bearer"
  - accessToken: string
  - refreshToken: string

- RefreshTokenModel
  - refreshToken: string

### Users

- UserModelPost extends GeneralResultModel
  - login: string
  - password: string
  - fullName: string
  - role: ADMIN | USER
  - email?: string
  - telegramLogin?: string
  - telegramChatId?: string

- UserModelReturn extends GeneralResultModel
  - id: UUID
  - login: string
  - fullName: string
  - role: ADMIN | USER
  - email?: string
  - telegramLogin?: string
  - telegramChatId?: string
  - createdAt?: string (yyyy.MM.dd HH:mm:ss.SSS)
  - archiveDate?: string (yyyy.MM.dd HH:mm:ss.SSS)

### Languages

- LanguageModelAdd extends GeneralResultModel
  - name: string
  - regEx: string

- LanguageModelReturn extends GeneralResultModel
  - id: UUID
  - name: string
  - regEx: string
  - createdAt?: string

- LanguageCriteriaModel extends PageModel
  - nameFilter?: string
  - fromFilter?: string (ISO date-time)
  - toFilter?: string (ISO date-time)
  - sortFilter?: string

### Words

- WordModelPost extends GeneralResultModel
  - word: string
  - languageId: UUID

- WordModelReturn extends GeneralResultModel
  - id: UUID
  - word: string
  - languageId: UUID
  - createdAt?: string

- WordCriteriaModel extends PageModel
  - wordFilter?: string
  - langIdFilter?: UUID
  - langNameFilter?: string
  - fromFilter?: string
  - toFilter?: string
  - sortFilter?: string

### Associations

- AssociationModelAdd extends GeneralResultModel
  - word: UUID
  - translation: UUID

- AssociationModelReturn extends GeneralResultModel
  - id: UUID
  - word: UUID
  - translation: UUID
  - createdAt?: string
  - createdByUserId?: UUID

- AssociationModelReturnEnriched extends GeneralResultModel
  - id: UUID
  - firstWordId: UUID
  - secondWordId: UUID
  - firstWordText: string
  - secondWordText: string
  - firstWordLangName: string
  - secondWordLangName: string

- AssociationCriteriaModel extends PageModel
  - wordIdFilter?: UUID
  - translationIdFilter?: UUID
  - wordFilter?: string
  - translationFilter?: string
  - wordLanguageIdFilter?: UUID
  - translationLanguageIdFilter?: UUID
  - wordLanguageNameFilter?: string
  - translationLanguageNameFilter?: string
  - anyWordIdFilter?: UUID
  - anyWordFilter?: string
  - anyLanguageIdFilter?: UUID
  - anyLanguageFilter?: string
  - fromFilter?: string
  - toFilter?: string
  - sortFilter?: string
  - createdByUUID?: UUID[]

- TranslationRequest extends PageModel
  - word: string
  - wordLanguage: string
  - translationLanguage: string

### Jobs & Export/Import

- TaskType: DICTIONARY_IMPORT_EXCEL | ASSOCIATIONS_EXPORT | USER_EXPORT | TEST_TASK | SEND_EMAIL | SEND_TELEGRAM
- TaskStatus: SUCCESS | FAILED | IS_RUNNING | NEW | ATTEMPTS_ARE_OVER | CRITICAL_ERROR | CANCELED
- ExportType: ASSOCIATIONS_EXPORT | USER_EXPORT
- ImportType: DICTIONARY_IMPORT

- JobModelPost extends GeneralResultModel
  - taskType: TaskType
  - params?: string (JSON)
  - minStartTime?: string (ISO date-time)

- JobModelReturn extends GeneralResultModel
  - jobId: UUID
  - taskType: TaskType
  - status: TaskStatus
  - params?: string
  - createdAt?: string

- JobEnrichedModel extends GeneralResultModel
  - job: JobModelReturn
  - creatorLogin: string
  - creatorRole: string

- ExportCriteriaModel extends PageModel
  - exportType: ExportType
  - fileExtension: string (e.g. .xlsx)
  - sendEmail: boolean
  - sendTelegram: boolean
  - fromFilter?: string
  - toFilter?: string
  - byLoginFilter?: string
  - byRoleFilter?: string
  - byFullNameFilter?: string

- ExportReturnModel extends GeneralResultModel
  - fileName: string
  - fileExtension: string
  - fileBody: base64

- ImportReturnModel extends GeneralResultModel
  - fileName: string
  - fileExtension: string
  - fileBody: base64

### RabbitMQ

- DirectMessageModel
  - queueName: string
  - message: string

