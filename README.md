# HttpServerBase

## DB-Zugriff innerhalb eines Http-Servers

Ein Beispiel für den Zugriff auf die Datenbank mittels JDBC.

Aufteilung der Zuständigkeiten bei der Connection:
- DatabaseManager: kümmert sich um das Laden des Treibers und erstellt eine neue Connection
- UnitOfWork: Ist die Transaktionsklammer um alle Zugriffe auf der DB, die als eine Transaktion abzuwickeln sind
- Controller (hier am Beispiel des WeatherController): Koordiniert alle Zugriffe auf die Datenbank und die Transaktionsklammer
- Repository (hier am Beispiel des WeatherRepository): Führt die tatsächlichen Zugriffe auf die DB aus. Hierfür wird mittels UnitOfWork ein neues PreparedStatement erzeugt und verwendet.



