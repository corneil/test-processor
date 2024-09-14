#!/bin/bash
FOLDERS="java kotlin"
for FOLDER in $FOLDERS; do
  pushd $FOLDER
    ./gradlew clean assemble
    ./gradlew test -Ptag=test
    ./gradlew test -Ptag=integration
  popd
done
RS=$PWD/report-summary.sh
find . -name "TEST*xml" -type f -exec $RS '{}' \;