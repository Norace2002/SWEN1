# Monster Trading Card Game

## DB-Zugriff innerhalb eines Http-Servers

Ein Beispiel für den Zugriff auf die Datenbank mittels JDBC.

Aufteilung der Zuständigkeiten bei der Connection:
- DatabaseManager: kümmert sich um das Laden des Treibers und erstellt eine neue Connection
- UnitOfWork: Ist die Transaktionsklammer um alle Zugriffe auf der DB, die als eine Transaktion abzuwickeln sind
- Controller: Koordiniert alle Zugriffe auf die Datenbank und die Transaktionsklammer
- Repository: Führt die tatsächlichen Zugriffe auf die DB aus. Hierfür wird mittels UnitOfWork ein neues PreparedStatement erzeugt und verwendet.



