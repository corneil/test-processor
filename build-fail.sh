#!/bin/bash
set +e # allow failures
LOGFILE="$PWD/build.log"
rm -rf $LOGFILE
FOLDERS="java kotlin"
for FOLDER in $FOLDERS; do
  pushd $FOLDER
    ./gradlew clean build -Pfork=0 | tee -a $LOGFILE
  popd
done
RS=$PWD/report-summary.sh
find . -name "TEST*xml" -type f -exec $RS '{}' \;