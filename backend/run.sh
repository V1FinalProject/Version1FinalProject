#!/usr/bin/env bash
# Loads backend/.env (gitignored, holds ANTHROPIC_API_KEY) into the environment,
# then starts the Spring Boot app.
set -a
source "$(dirname "$0")/.env"
set +a
exec "$(dirname "$0")/mvnw" -f "$(dirname "$0")/pom.xml" spring-boot:run
