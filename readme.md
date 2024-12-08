# OrderProcessor

OrderProcessor is a Java-based application that reads order data from JSON files, validates customer IDs, and stores the orders in a PostgreSQL database. It also handles error logging and file management, ensuring that invalid files are moved to an error directory and valid files are processed and moved to an output directory.

## Features

- Reads order data from JSON files
- Validates customer IDs against a PostgreSQL database
- Stores valid orders in the database
- Moves invalid files to an error directory
- Moves processed files to an output directory
- Logs information and errors using SLF4J and Logback

## Configuration

Create a `config.properties` file in the `src/main/resources` directory with the following content:

```properties
db.url=jdbc:postgresql://localhost:5432/
db.user=username
db.password=password
input.directory=src/main/java/com/khabir/data/input
```
