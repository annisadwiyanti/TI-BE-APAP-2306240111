#!/bin/bash

# Quick Test Execution Guide for Tour Package Backend

# 1. Run all tests
echo "Running all unit tests..."
./gradlew test

# 2. Run tests with JaCoCo coverage report
echo "Running tests with coverage report..."
./gradlew test jacocoTestReport

# 3. Run specific test file
echo "Running specific test class..."
./gradlew test --tests ActivityTest

# 4. Run tests with verbose output
echo "Running tests with verbose output..."
./gradlew test --info

# 5. View the HTML coverage report
# Open this file in browser after running tests:
# build/reports/jacoco/test/html/index.html

# 6. Check test results
echo "View test results at: build/reports/tests/test/index.html"
