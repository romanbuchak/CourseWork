# CourseWork

1. Draw a diagram of the classes that will be used to complete the course work and indicate the relationships between them.
2. Write appropriate classes, use the lombok library to reduce the amount of code.
3. Implements REST services for all entities using Spring Boot. It is necessary to implement operations: GET/POST/PUT/DELETE operation GET/2 - returns the entity with id equal to 2. Operation /GET - returns all entities that are present in the system.
4. Divide the code into controllers, services and data access level.
5. Linking controllers, services, and data access levels should be done using dependency inversion.

6. Implement data storage and subtraction from the csv file. Important: Each entity is stored in a separate file.
7. If the file does not exist for the entity, it must be created, the file name must contain the date of creation, for example: fish-2022-05-27.csv
8. Each file should contain headers (matching the names of the attributes of the designed classes) only in the first line.
9. When you start the application, all entities are read from the file and saved in the hash map. When subtracting data, you should search for all files for the entity that were created in the current month (for example, all files created in June if the program runs in June).

10. The project must contain README.md with a description of the task and step-by-step instructions for starting the program.
11. The project must use maven to assemble the project.
12. The code should be checked using Spotbugs and checkstyle.
13. The code should contain unit tests to check the logic of writing and searching for files on the file system.
14. Create a set (collection) of REST queries that test the performance of developed services.

To run the program you need:
- download jar file
- run the project
- copy the path to the target folder
- run the command cd + path to target
- run it using java -jar + path to jar file
