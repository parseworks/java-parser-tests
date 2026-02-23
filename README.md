# Java Parser Combinator Comparison

This project is a benchmark suite for comparing different Java parser combinator libraries.

## Project Structure

*   `core`: Contains the common interfaces for parsers (e.g., `CsvParser`).
*   `benchmarks`: The main module that runs performance tests on all implementations.
*   `dot-parse`: Implementation using [dot-parse](https://github.com/google/mug/wiki/dot-parse-Reference-Guide).
*   `parseworks-impl`: Implementation using [parseworks](https://github.com/parseworks/parseworks).

## How to add a new implementation

Anybody can contribute a new implementation by following these steps:

1.  **Create a new Maven submodule**: Create a new directory and add a `pom.xml` that inherits from the root project.
2.  **Add dependencies**: Add the `core` module as a dependency, along with your chosen parser library.
3.  **Implement the interface**: Create a class that implements `io.github.parsercompare.TestParser`.
4.  **Register as a Service**: Create a file `src/main/resources/META-INF/services/io.github.parsercompare.TestParser` in your module, and put the full name of your implementation class there.
5.  **Run benchmarks**: Execute `mvn clean install` followed by `java -jar benchmarks/target/benchmarks.jar`. The `benchmarks.jar` will automatically discover and test all implementations found on the classpath.

## Running the Benchmarks

To run the current benchmarks:

```bash
mvn clean install
java -jar benchmarks/target/benchmarks.jar
```