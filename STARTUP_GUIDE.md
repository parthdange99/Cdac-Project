# FundBridge Microservices Startup Procedure

Since microservices depend on each other (e.g. for configuration and service discovery), they must be started in a specific order. 

Please start the applications in STS in the following order:

### 1. Infrastructure Services (Start these first)
1. **`discovery-server`** (Starts on port `8761`)
   *Wait until you see `Tomcat started on port 8761` in the console.*
2. **`config-server`** (Starts on port `8888`)
   *Wait until you see `Tomcat started on port 8888` in the console.*
3. **`api-gateway`** (Starts on port `8080`)
   *Wait until it starts successfully.*

### 2. Core Services
4. **`auth-service`** (Starts on port `8081`)
5. **`user-service`** (Starts on port `8082`)

### 3. Business Services
6. **`campaign-service`** (Starts on port `8084`)
7. **`loan-service`** (Starts on port `8083`)
8. **`donation-service`** (Starts on port `8085`)
9. **`payment-service`** (Starts on port `8086`)
10. **`notification-service`** (Starts on port `8087`)
11. **`ai-service`** (Starts on port `8088`)

---

### Important Note on the Build Error:
The `java.lang.ExceptionInInitializerError: com.sun.tools.javac.code.TypeTag :: UNKNOWN` error you were seeing earlier is caused by **JDK 24**. Lombok does not yet support Java 24, and your Maven/STS was using JDK 24 to run the build. 

I successfully built the entire project using **JDK 21** (`C:\Program Files\Java\jdk-21.0.11`). Make sure your STS is configured to use JDK 21 as its installed JRE and for Maven builds.

---

## How to Execute the Startup Sequence

You can start the entire platform using either of these two methods:

### Option A: Using Spring Tool Suite (STS)
1. Open STS and look for the **Boot Dashboard** window.
2. Select all of your microservice projects (discovery-server, config-server, etc.).
3. Click the **Start (Play)** button at the top of the dashboard.
4. Wait for them to turn green (which means they have successfully registered with Eureka).
5. Open a terminal in undbridge-frontend and run 
pm start to launch the React app.

### Option B: Using the Automation Script (Recommended for speed)
I have created a start-all.ps1 PowerShell script in the root directory that completely automates this process for you!

1. Open a PowerShell window in your C:\Anti\fundbridge-microservices folder.
2. Run the script by typing:
   `.\start-all.ps1`
3. The script will automatically pop open a new terminal window for each microservice in the correct dependency order (waiting for Eureka to start first), and finally launch the frontend!
