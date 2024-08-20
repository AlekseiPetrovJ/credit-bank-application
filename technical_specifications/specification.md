# Правила скоринга

## Прескоринг
Правила прескоринга (можно придумать новые правила или изменить существующие):  
Имя, Фамилия - от 2 до 30 латинских букв. Отчество, при наличии - от 2 до 30 латинских букв.  
Сумма кредита - действительно число, большее или равное 30000.  
Срок кредита - целое число, большее или равное 6.  
Дата рождения - число в формате гггг-мм-дд, не позднее 18 лет с текущего дня.  
Email адрес - строка, подходящая под паттерн ^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$  
Серия паспорта - 4 цифры, номер паспорта - 6 цифр.  

## Скоринг
Правила скоринга (можно придумать новые правила или изменить существующие):  
Рабочий статус: Безработный → отказ; Самозанятый → ставка увеличивается на 1; Владелец бизнеса → ставка увеличивается на 2  
Позиция на работе: Менеджер среднего звена → ставка уменьшается на 2; Топ-менеджер → ставка уменьшается на 3  
Сумма займа больше, чем 25 зарплат → отказ  
Семейное положение: Замужем/женат → ставка уменьшается на 3; Разведен → ставка увеличивается на 1  
Возраст менее 20 или более 65 лет → отказ  
Пол: Женщина, возраст от 32 до 60 лет → ставка уменьшается на 3; Мужчина, возраст от 30 до 55 лет → ставка уменьшается на 3; Не бинарный → ставка увеличивается на 7  
Стаж работы: Общий стаж менее 18 месяцев → отказ; Текущий стаж менее 3 месяцев → отказ  


# API

## calculator

POST: /calculator/offers - расчёт возможных условий кредита.  
POST: /calculator/calc - валидация присланных данных + полный расчет параметров кредита  

## deal

POST: /deal/statement - расчёт возможных условий кредита  
POST: /deal/offer/select - выбор одного из предложений  
POST: /deal/calculate/{statementId} -  полный расчет параметров кредита  
POST: /deal/document/{statementId}/send - запрос на отправку документов  
POST: /deal/document/{statementId}/sign - запрос на подписание документов  
POST: /deal/document/{statementId}/code - подписание документов  
GET: /deal/admin/statement/{statementId} - получить заявку по id  
PUT: /deal/admin/statement/{statementId}/status - обновить статус заявки  

## statement

POST: /statement - первичная валидация заявки, создание заявки  
POST: /statement/offer - выбор одного из предложений  

# DTO сущности

## LoanStatementRequestDto
```javascript
{
"amount": "BigDecimal",
"term": "Integer",
"firstName": "String",
"lastName": "String",
"middleName": "String",
"email": "String",
"birthdate": "LocalDate",
"passportSeries": "String",
"passportNumber": "String"
}
```

## LoanOfferDto

```javascript
{
"statementId": "UUID",
"requestedAmount": "BigDecimal",
"totalAmount": "BigDecimal",
"term": "Integer",
"monthlyPayment": "BigDecimal",
"rate": "BigDecimal",
"isInsuranceEnabled": "Boolean",
"isSalaryClient": "Boolean"
}
```

## ScoringDataDto
```javascript
{
"amount": "BigDecimal",
"term": "Integer",
"firstName": "String",
"lastName": "String",
"middleName": "String",
"gender": "Enum",
"birthdate": "LocalDate",
"passportSeries": "String",
"passportNumber": "String",
"passportIssueDate": "LocalDate",
"passportIssueBranch": "String",
"maritalStatus": "Enum",
"dependentAmount": "Integer",
"employment": "EmploymentDto",
"accountNumber": "String",
"isInsuranceEnabled": "Boolean",
"isSalaryClient": "Boolean"
}
```
## CreditDto
```javascript
{
"amount": "BigDecimal",
"term": "Integer",
"monthlyPayment": "BigDecimal",
"rate": "BigDecimal",
"psk": "BigDecimal",
"isInsuranceEnabled": "Boolean",
"isSalaryClient": "Boolean",
"paymentSchedule": "List<PaymentScheduleElementDto>"
}
```
## FinishRegistrationRequestDto
```javascript
{
"gender": "Enum",
"maritalStatus": "Enum",
"dependentAmount": "Integer",
"passportIssueDate": "LocalDate",
"passportIssueBrach": "String",
"employment": "EmploymentDto",
"accountNumber": "String"
}
```
## EmploymentDto
```javascript
{
"employmentStatus": "Enum",
"employerINN": "String",
"salary": "BigDecimal",
"position": "Enum",
"workExperienceTotal": "Integer",
"workExperienceCurrent": "Integer"
}
```
## PaymentScheduleElementDto
```javascript
{
"number": "Integer",
"date": "LocalDate",
"totalPayment": "BigDecimal",
"interestPayment": "BigDecimal",
"debtPayment": "BigDecimal",
"remainingDebt": "BigDecimal"
}
```
## StatementStatusHistoryDto
```javascript
{
"status": "Enum",
"time": "LocalDateTime",
"changeType": "Enum"
}
```
## EmailMessage
```javascript
{
"address": "String",
"theme": "Enum",
"statementId": "Long"
}
```

