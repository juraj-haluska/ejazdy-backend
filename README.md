# EjazdyBackend

Backend v Spring Boote ku semestrálnej práci eJazdy. Frontend
a inštrukcie na spustenie frontendu sa nachádzajú v repozitári ejazdy-frontend.

## Popis semestrálnej práce

eJazdy má slúžiť ako registračný systém jázd pre autoškoly. V systéme sa nachádzajú
tri druhy používateľov: Administrátor, Inštruktor a Študent. Základný princíp
sýstému spočíva v tom, že inštruktor môže cez systém vypísať termíny jázd, na ktoré
sa následne môžu študenti prihlasovať. Odhlásenie študenta z jazdy je umožnené administrátorovi
a inštruktorom bez obmedzení, študenti sa môžu odhlásiť z jazy len do 24h
pre jej začatím. Hlavnou úlohou administrátora je správa účtov - pridať/odobrať
inštruktora alebo študenta. Pridanie nového používateľa je uskutočnené zaslaním emailovej 
pozvánky s vygenerovaným heslom. Pozvaný užívateľ je následne pri prvom prihlásení vyzvaný 
na zadanie osobných údajov a nového hesla. Administrátor taktiež môže prihlásiť študentov na
jazdy, ktoré boli vypísané inštruktorom.

### Technológie použité v semestrálnej práci:
- Spring Boot
- Amazon Cognito
- Amazon DynamoDB
- Angular

### Demo

Aplikáciu je možné odkúšať tu: http://ejazdy.sk.
Na prihlásenie do aplikácie použite účty vypísané v README.md v repozitári ejazdy-frontend.

### Dokumentácia

Dokumentácia sa nachádza v priečinku docs.

## Build a spustenie

Požiadavky: maven

1. Naklonovanie repozitára
```
git clone [repo_url]
cd ejazdy-backend
```

2. Nastavenie AWS credentials

eJazdy využíva služby od Amazonu - Cognito a DynamoDB. Na prístup
ku týmto službám potrebuje backend AWS credentials. Nastaviť AWS credentials je 
možné dvoma spôsobmi:
- cez enviroment variables, v konzole:
```
export AWS_ACCESS_KEY=[key]
export AWS_SECRET_KEY=[secret]
```
- alebo priamo v konfiguračnom súbore application.properties
```
cognito.access-key=[key]
cognito.secret-key=[secret]
dynamo.access-key=[key]
dynamo.secret-key=[secret]
```

3. Build
```
mvn package
```

4. Spusenie
```
java -jar target/ejazdy-backend-0.0.1-SNAPSHOT.jar
```
Backend by mal defaultne bežať na **localhost:8090**

## Autor
Juraj Haluška (https://github.com/spacive)