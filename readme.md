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

## Usage

### 1. Set Up the Database

In order to create the databases, run the DatabaseUtils class. This will create the `orders` and `customer` tables in the PostgreSQL database.

### 2. Generate Inputs Automatically

To generate sample order inputs automatically, run the OrderGenerator class. This will create JSON files with sample orders in the input directory specified in the configuration. You can modify the input files to make them invalid and test the error handling of the OrderReader program.

### 3. Run the Program

Run the OrderReader program, which reads the order data from JSON files, validates customer IDs, and stores the orders in the PostgreSQL database.

The OrderReader program will continuously monitor the input directory for new JSON files, process them, and move them to the appropriate directories based on their validity.
