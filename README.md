[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/vlo9idtn)
# lab1-wa2025

## Extra Features

### Velocity Calculation
We have added a function to compute the average velocity between two waypoints in km/h. The calculation is based on the distance between the points and the elapsed time.

### Intersection Detection
We have implemented a system to identify if two path segments intersect and calculate the approximate intersection point. This is useful for navigation analysis and detecting possible overlaps in trajectories.

## Using Docker
To run the project in a Docker environment, follow these steps:

1. Make sure you have Docker installed on your system.
2. Build the Docker image:
   ```sh
   docker build -t image-name .
   ```
3. Run the container:
   ```sh
   docker run -p 8080:8080 image-name
   ```

The service will be available on port 8080 of your localhost. If necessary, you can modify the port in the execution command.

## Testing with JUnit
We have implemented unit tests using JUnit to ensure the correctness of our features.

Tests can be run directly from IntelliJ IDEA:

1. Open the test file in IntelliJ.
2. Click on the green play button next to the test function or the class name.

Ensure JUnit is properly configured in your project dependencies.