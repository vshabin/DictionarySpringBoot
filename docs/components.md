### Overview

This document lists the main Spring components, services, repositories, and configuration classes.

### Security

- security.SecurityConfig: Spring Security configuration
- security.JwtAuthFilter: JWT filter
- security.DelegatedAuthenticationEntryPoint: Authentication entry point
- security.ApplicationExceptionHandler: Global exception handler
- security.UserProvider: Current user provider (Ebean)
- security.SecurityConst: constants and roles

### Controllers (REST)

- api.AuthController: /auth
- api.UserController: /user
- api.LanguageController: /languages
- api.WordController: /words
- api.AssociationController: /associations
- api.ExportController: /export
- api.ImportController: /import
- api.JobController: /jobs
- api.RabbitMQController: /rabbit

### Services

- domainservices.AuthService
- domainservices.UserService
- domainservices.LanguageService
- domainservices.WordService
- domainservices.AssociationService
- domainservices.JobService
- domainservices.ExportService
- domainservices.ImportService
- domainservices.TelegramBot
- domainservices.JwtProvider

### Job strategies

- domainservices.jobStrategies.UserExportJob
- domainservices.jobStrategies.AssociationsExportJob
- domainservices.jobStrategies.DictionaryImportJob
- domainservices.jobStrategies.EmailSendJob
- domainservices.jobStrategies.TelegramSendJob
- domainservices.jobStrategies.TestJobImpl

### Export/Import strategies

- domainservices.exportStrategies.UserExportImpl
- domainservices.exportStrategies.AssociationsExportImpl
- domainservices.importStrategies.DictionaryImportExcel

### Repositories

- infrastructure.repositories.user.UserRepository
- infrastructure.repositories.language.LanguageRepository
- infrastructure.repositories.word.WordRepository
- infrastructure.repositories.association.AssociationRepository
- infrastructure.repositories.job.JobRepository
- infrastructure.repositories.cronTask.CronTaskRepository
- infrastructure.repositories.auth.SessionRepository
- infrastructure.repositories.DbServer

### RabbitMQ

- rabbitMQ.RabbitMQConfig
- rabbitMQ.RabbitMQListener
- rabbitMQ.RabbitMQConst

### Config

- config.AppConfig (scheduling, executors)
- config.RedisConfig (Redis)
- config.ApplicationStartup (startup hooks)
- config.ProcessorInfo (hostname)

