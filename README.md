# Weather At Zip

This is a Spring Boot application that gets weather and location information for a given Zip code.
Submitted for Cayuse.

## Building and Running

This project uses Gradle and the Gradle Wrapper (https://docs.gradle.org/current/userguide/gradle_wrapper.html).  
The only other requirement is that Java is installed, and in the path.  This was built against
Java 8 (on Windows), and was tested on Java 11 (on Linux).

Before running the server, working API keys will need to be entered into the application.yml file.
The server port is set to 8088 by default, but may need to be changed (although the integration 
tests use a random port, so if you're just going to run the test target, you won't need to change 
anything).

To build and start the server, enter these commands from a command prompt or terminal:

    ./gradlew assemble
    ./gradlew bootRun

Once the server is running, the API can be accessed from a tool like curl:

    curl http://localhost:8088/weather?zipcode=97204

To run all of the automated tests (all integration tests, at this point), enter these commands:

    ./gradlew test
