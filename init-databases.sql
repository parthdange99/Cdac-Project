-- ============================================================
-- FundBridge Microservices - Database Initialization Script
-- Run this script in MySQL before starting services
-- ============================================================

-- Create individual databases for each microservice
CREATE DATABASE IF NOT EXISTS fundbridge_auth
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS fundbridge_users
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS fundbridge_loans
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS fundbridge_campaigns
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS fundbridge_donations
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS fundbridge_payments
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS fundbridge_notifications
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Confirm
SHOW DATABASES LIKE 'fundbridge_%';
