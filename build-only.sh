#!/bin/bash
FOLDERS="java kotlin"
for FOLDER in $FOLDERS; do
  pushd $FOLDER
    ./gradlew clean assemble
  popd
done
