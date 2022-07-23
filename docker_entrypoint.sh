#!/bin/sh

# Restore database from S3.
litestream restore -if-replica-exists -v "${DB_PATH}"

litestream replicate -exec "java ${JAVA_OPTS} -jar /zinemite/zinemite-standalone.jar"