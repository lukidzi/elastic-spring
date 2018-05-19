#!/bin/bash
echo $1
if [[ -n $1 ]]; then
    echo "run with server"
    java -jar build/libs/elastic-demo-0.0.1-SNAPSHOT.jar --spring.profiles.active=local --elastic.server=$1
else
    java -jar build/libs/elastic-demo-0.0.1-SNAPSHOT.jar --spring.profiles.active=local
fi
