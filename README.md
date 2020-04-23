# Simple bank interface

### Тесты
Для тестов используется h2 in-memory база данных поэтому просто запускаем

`./gradlew clean test`

### Запуск приложения 
Для запуска необходимо скачать и развернуть докер контейнер с PostgreSQL и настроенными таблицами 

`git clone https://github.com/creew/docker-postgres-example.git`<br>
`cd docker-postgres-example`<br>
`make`<br>

и далее собрать и запустить приложение 

`./gradlew clean bootJar`<br>
`java -jar build/libs/bank-0.0.1-SNAPSHOT.jar`<br>
