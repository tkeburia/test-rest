#!/usr/bin/env sh

echo "Executing entry point script for test-rest"
java -jar /app.jar --spring.config.location=classpath:/,file:$CONFIG_DIR