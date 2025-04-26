# big-id

## Overview
This project is a simple Java program designed to find specific strings within a large text file.  
The program is modular, using multi-threading to improve performance by processing parts of the text concurrently.

## Modules

### Main Module
**Functionality:**
- Reads a large text file in parts (e.g., 1000 lines at a time).
- Sends each part to a matcher.
- Waits for all matcher threads to complete.
- Calls the aggregator to combine and print the final results.

**Concurrency:**
- Multiple matcher tasks run in parallel, improving processing speed for large files.

### Matcher Module
**Functionality:**
- Accepts a text chunk and searches for specific target strings.
- Outputs a map from each matched word to its location(s) (line number and character offset) within the text.

### Aggregator Module
**Functionality:**
- Aggregates the results produced by all matchers.
- Prints the results (word and its locations) to the console.

## Key Features
- Concurrent processing: Multiple matchers work simultaneously on different parts of the file.
- Batch reading: The file is read in configurable batches (e.g., 1000 lines) to control memory usage.
- Flexible target words: The program accepts a dynamic list of strings to search for.
- Clear modular design: Separate classes for reading, matching, and aggregating.

## Sample Run
The `MainApplication` class demonstrates a full sample run:
- Loads a URL or local text file.
- Specifies a list of target words (e.g., common English names).
- Processes the text in parallel.
- Prints out where each target word was found (line and character offset).

## How to Run

1. Clone the repository
2. Build the project using Maven:
    ```bash
    mvn clean install
    ```
3. Run the application:
    ```bash
    mvn exec:java -Dexec.mainClass="com.example.bigid.MainApplication"
    ```

## Dependencies
- Java 8+
- Maven
- SLF4J (for logging)
- Mockito and JUnit (for testing)
- Commons Validator (for URL validation)

## Notes
- Results are printed in no particular order (depending on thread completion timing).
- The number of lines processed per batch and number of threads are configurable for tuning performance.
- No external frameworks for concurrency; standard Java `ExecutorService` is used.
- If a matcher encounters an error, the program continues running and returns partial results instead of failing the entire execution.
- Lines are processed as complete units; words are never split across two lines.
- Word separation is based solely on spaces; other delimiters are not considered for splitting words.
- Matching is case-insensitive but ignores matches where words contain special characters.
(For example, searching for Hows will not match How's.)
- Large files are processed in configurable batches to optimize memory usage and concurrency.