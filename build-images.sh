#!/bin/bash
FOLDERS="java kotlin"
for FOLDER in $FOLDERS; do
  pushd $FOLDER
    ./gradlew bootBuildImage
  popd
done
