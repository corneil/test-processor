#!/bin/bash
test_content() {
  FILE=$1
  JSON=$(cat $FILE)
  if [ "$JSON" != "" ]; then
    COUNT=$(grep -c -F "com.example.testprocessor.addition" $FILE)
    if ((COUNT==0)); then
      echo "Expected $FILE to contain com.example.testprocessor.addition"
    fi
  else
    echo "$FILE is empty"
  fi
}

FOLDERS="java kotlin"
for FOLDER in $FOLDERS; do
  docker inspect --type=image test-processor-$FOLDER:latest > inspect-$FOLDER.json
  jq -r '.[] | .Config.Labels | ."org.springframework.boot.spring-configuration-metadata.json"' inspect-$FOLDER.json > org.springframework.boot.spring-configuration-metadata-$FOLDER.json
  jq  -r '.[] | .Config.Labels | ."org.springframework.cloud.dataflow.spring-configuration-metadata.json"' inspect-$FOLDER.json > org.springframework.cloud.dataflow.spring-configuration-metadata-$FOLDER.json
  test_content org.springframework.boot.spring-configuration-metadata-$FOLDER.json
  test_content org.springframework.cloud.dataflow.spring-configuration-metadata-$FOLDER.json
done
