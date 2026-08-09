# FundBridge Microservices Startup Script
# This script launches all Spring Boot microservices and the React frontend in the correct order.

Write-Host "Starting FundBridge Microservices Platform..." -ForegroundColor Yellow

# 1. Infrastructure
Write-Host "Starting Infrastructure Services (Discovery, Config, Gateway)..." -ForegroundColor Cyan
Start-Process powershell -ArgumentList "-NoExit -Command `"cd discovery-server; mvn spring-boot:run`"" 
Start-Sleep -Seconds 15 # Wait for Eureka to spin up

Start-Process powershell -ArgumentList "-NoExit -Command `"cd config-server; mvn spring-boot:run`""
Start-Sleep -Seconds 15 # Wait for Config Server

Start-Process powershell -ArgumentList "-NoExit -Command `"cd api-gateway; mvn spring-boot:run`""
Start-Sleep -Seconds 10

# 2. Core & Business Services
Write-Host "Starting Core & Business Services..." -ForegroundColor Cyan
$services = @(
    "auth-service", 
    "user-service", 
    "campaign-service", 
    "loan-service", 
    "donation-service", 
    "payment-service", 
    "notification-service", 
    "ai-service"
)

foreach ($svc in $services) {
    Write-Host "Starting $svc..." -ForegroundColor Green
    Start-Process powershell -ArgumentList "-NoExit -Command `"cd $svc; mvn spring-boot:run`""
    Start-Sleep -Seconds 5 # Stagger the startups so memory doesn't spike all at once
}

# 3. Frontend
Write-Host "Starting React Frontend..." -ForegroundColor Magenta
Start-Process powershell -ArgumentList "-NoExit -Command `"cd fundbridge-frontend; npm start`""

Write-Host "Done! All 11 microservices and the frontend have been launched in separate windows." -ForegroundColor Yellow
Write-Host "Please give them a minute or two to fully initialize and register with Eureka." -ForegroundColor Yellow
