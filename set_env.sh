#!/bin/bash

export POSTGRES_HOST="localhost"
export POSTGRES_PORT=5432
export POSTGRES_USER="test"
export POSTGRES_PASSWORD="test"
export POSTGRES_DATABASE="news"
export POSTGRES_SCHEMA="news"
export POSTGRES_DRIVER="org.postgresql.Driver"
export HTTP_HEADER_LOG=false
export HTTP_BODY_LOG=false
export HTTP_HOST="0.0.0.0"
export HTTP_PORT=8080
export SCRAPE_URL="https://nytimes.com"
export INTERVAL=1.minute