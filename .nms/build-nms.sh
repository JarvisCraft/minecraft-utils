#!/usr/bin/env bash
curl -o BuildTools.jar \
https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar \
|| wget -O BuildTools.jar \
https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar

java -jar BuildTools.jar