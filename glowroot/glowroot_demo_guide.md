# Glowroot Demo Application Setup: A Comprehensive Guide to Java APM

This guide will walk you through setting up a complete Application Performance Monitoring (APM) solution using [Glowroot](https://glowroot.org/) for a sample Spring Boot application, all orchestrated with Docker and Docker Compose. By following these steps, you will create a local environment where you can observe real-time performance metrics of a Java application.

## What You Are Creating

You are building a miniature APM ecosystem:
*   A **Glowroot Central Collector**: This acts as the central hub, receiving, storing, and visualizing performance data from monitored applications. You will interact with its web UI.
*   A **Demo Spring Boot Application**: This is the application whose performance you will monitor. It's instrumented with the Glowroot agent.
*   **Seamless Integration**: The Docker Compose setup ensures that the demo application automatically sends its performance data to the Glowroot Central Collector.

## What is Glowroot and Why Use It?

[Glowroot](https://glowroot.org/) is an open-source, ultra-fast, and lightweight Java APM tool designed to give you deep insights into your application's behavior. When you integrate Glowroot, you gain the ability to:

*   **Monitor Response Times and Throughput**: Understand how quickly your application responds to user requests and how many requests it handles. This helps you identify bottlenecks.
*   **Track Errors and Error Rates**: Immediately spot when and where errors are occurring in your application, allowing for quicker debugging.
*   **Detailed Transaction Traces**: For slow operations, Glowroot captures full transaction traces, showing you the exact sequence of method calls, database queries, and external service calls, along with their timings. This is invaluable for pinpointing the root cause of performance issues.
*   **Profile CPU Activity**: See which parts of your code are consuming the most CPU resources, helping you optimize critical sections.
*   **Monitor JVM Metrics**: Keep an eye on the health of your Java Virtual Machine, including heap memory usage, garbage collection activity, thread counts, and more.
*   **Set Up Alerts**: Configure thresholds for various metrics (e.g., response time, error rate) and receive alerts when these thresholds are breached, enabling proactive issue resolution.

## Understanding the Project Structure and Its Components

This project is structured to easily deploy a Glowroot-monitored application:

*   `docker-compose.yml`: This is the blueprint for your multi-container Docker application. It defines two key **services** that you will bring up:
    *   `glowroot-central`: This service will run the Glowroot Central Collector.
    *   `demo-app`: This service will run your sample Spring Boot application.
    It configures how these services communicate (e.g., the `demo-app` sends data to `glowroot-central`) and exposes ports for you to access the Glowroot UI.

*   `glowroot.jar`: This is the **Glowroot Agent**. It's a small Java `.jar` file that you attach to your application's JVM. Its role is to instrument your application's code at runtime, collect performance data, and send it to the Glowroot Central Collector without requiring any code changes in your application itself. In this setup, a copy is present in `demo-app/` for the `demo-app`'s Docker build.

*   `demo-app/`: This directory contains all the source code and build instructions for the sample Spring Boot application that will be monitored.
    *   `demo-app/Dockerfile`: This file defines how your `demo-app` Docker image is built. It packages your Spring Boot application (built from `pom.xml` and `src/`), copies the `glowroot.jar` agent, and most importantly, configures the Java application to run with the `javaagent` flag: `-javaagent:glowroot.jar`. This flag tells the JVM to load the Glowroot agent, enabling APM for your application.
    *   `demo-app/glowroot.properties`: This is the configuration file specifically for the Glowroot Agent embedded within your `demo-app`. It tells the agent where to send its collected data (i.e., the address of the `glowroot-central` service).

*   `glowroot-central-custom/`: This directory holds customizations for your Glowroot Central Docker image.
    *   `glowroot-central-custom/Dockerfile`: This Dockerfile extends the official `glowroot/glowroot-central` image. The `RUN apt-get install -y openjfx` command is included to install OpenJFX, which might be a dependency for certain advanced features or plugins within Glowroot's UI or reporting capabilities, ensuring maximum compatibility.

## Getting Started: Building and Running Your APM Environment

Follow these steps to deploy and interact with your Glowroot-monitored demo application.

### Prerequisites

Before you begin, ensure you have the following installed on your system:

*   **Docker**: For containerizing applications.
*   **Docker Compose**: For defining and running multi-container Docker applications.

### 1. Build Your Custom Docker Images

First, you need to build the Docker images for both your `demo-app` and the customized `glowroot-central`.

1.  **Open your terminal or command prompt.**
2.  **Navigate to the root directory of this project** (where you see `docker-compose.yml`).
3.  **Execute the build command:**
    ```bash
    docker-compose build
    ```
    *   **What this command does**: This command reads your `docker-compose.yml` and the associated `Dockerfile`s (`demo-app/Dockerfile` and `glowroot-central-custom/Dockerfile`). It then compiles the `demo-app`'s Java code, packages it into a `.jar` file, and creates two self-contained Docker images: one for your Spring Boot application with the Glowroot agent, and one for the Glowroot Central Collector. These images are essentially snapshots of your configured environments ready to be run as containers.

### 2. Start Your APM Containers

Once the images are successfully built, you can launch your Glowroot APM system.

1.  **From the same root directory**, run the following command:
    ```bash
    docker-compose up -d
    ```
    *   **What this command does**: This command reads your `docker-compose.yml` again. It then starts two independent but interconnected **containers** in the background (`-d` for detached mode):
        *   The `glowroot-central` container: This is your APM server, listening for performance data.
        *   The `demo-app` container: This is your Spring Boot application, now actively running and configured to send its performance metrics to the `glowroot-central` container.
    *   **What you have created**: You now have a fully operational APM environment where your application is being monitored in real-time.

### 3. Verify and Access Your APM Dashboard and Monitored Application

Now that everything is running, let's see it in action!

#### Accessing the Glowroot Central UI (Your APM Dashboard)

1.  Open your web browser.
2.  Navigate to: `http://localhost:4000`
    *   **What you will see**: You should be presented with the Glowroot Central dashboard. Initially, it might be empty or show minimal data. Give it a minute or two for the `demo-app` to start up fully and begin sending its performance data. Once data starts flowing, you'll see graphs, charts, and detailed information about your `demo-app`'s performance. You can then explore the various sections like "Transactions," "Errors," "JVM," and "Profiling" to gain insights.

#### Interacting with the Demo Application (Generating Data)

The `demo-app` is running and being monitored, but to see interesting data in Glowroot, you need to generate some activity by calling its endpoints.

*   The demo application runs on port `8080` *inside* its Docker container. By default in this setup, this port is not directly exposed to your host machine for external access. You'll primarily view its performance *through* the Glowroot UI.

*   **Understanding the Demo Application Endpoints:**
    The demo Spring Boot application provides three key endpoints to simulate different application behaviors:
    *   `/log`: This endpoint generates various log messages (INFO, WARN, ERROR) within the application. When accessed, Glowroot will capture these log events, allowing you to see how logs are integrated into APM data.
    *   `/error`: Calling this endpoint will intentionally cause an `IllegalStateException` within the application. This exception is caught and logged as an error. Glowroot will record this as an error transaction, demonstrating its error tracking capabilities.
    *   `/slow`: This endpoint simulates a slow operation by introducing a 5-second delay. Accessing this will create a slow transaction in Glowroot, which you can then inspect for detailed traces and performance bottlenecks.

*   **To generate traffic and see data appear in Glowroot**:
    1.  **Find the running `demo-app` container ID or name**:
        ```bash
        docker ps
        ```
        Look for the container associated with the `demo-app` service. Note its `CONTAINER ID`.
    2.  **Execute `curl` commands *inside* the `demo-app` container for each endpoint**: Replace `<demo-app-container-id>` with the actual ID you found.

        *   **Generate Log Messages:**
            ```bash
            docker exec -it <demo-app-container-id> curl http://localhost:8080/log
            ```
            Run this a few times.

        *   **Trigger an Error:**
            ```bash
            docker exec -it <demo-app-container-id> curl http://localhost:8080/error
            ```
            Run this a few times.

        *   **Simulate a Slow Transaction:**
            ```bash
            docker exec -it <demo-app-container-id> curl http://localhost:8080/slow
            ```
            This command will take 5 seconds to return.

    3.  **Observe Glowroot**: After running these commands, refresh your Glowroot Central UI (`http://localhost:4000`). You should now start seeing transaction data, response times, error counts, and log messages populate on the dashboard. Explore the "Transactions" and "Errors" sections to see the impact of your interactions.

### 4. Stopping and Cleaning Up Your Environment

When you are finished with your APM session, it's good practice to stop and remove the containers.

```bash
docker-compose down
```
*   **What this command does**: This command gracefully stops the running `glowroot-central` and `demo-app` containers, removes them, and also tears down the Docker network that Docker Compose created for them. This cleans up your system resources.

### 5. Viewing Container Logs (Troubleshooting)

If you encounter any issues or just want to see what's happening inside your containers:

*   **View logs for all services**:
    ```bash
    docker-compose logs -f
    ```
    This command streams the combined logs from both `glowroot-central` and `demo-app` containers to your terminal in real-time. Press `Ctrl+C` to stop streaming.
*   **View logs for a specific service** (e.g., `glowroot-central` or `demo-app`):
    ```bash
    docker-compose logs -f glowroot-central
    ```
    This is useful if you suspect an issue with one particular component.

By following this guide, you have successfully set up and experimented with a powerful Java APM solution.