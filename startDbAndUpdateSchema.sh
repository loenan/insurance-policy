#!/bin/bash
docker-compose up -d
./mvnw liquibase:update
