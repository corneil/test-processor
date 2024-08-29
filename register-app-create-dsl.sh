#!/usr/bin/env bash
set -euo pipefail

readonly SCDF_SHELL="${SCDF_SHELL:?must be set}"
readonly K8S_DIR=$(realpath "$SCDF_SHELL/../k8s")

"$K8S_DIR/load-image.sh" "example.com/library/test-processor:latest" true

cat > ./register-app.shell <<EOF
app register --uri docker:example.com/library/test-processor:latest --name test-processor --type processor --force
stream create --name hpl --definition "http | test-processor | log"
stream deploy --name hpl --properties app.*.logging.level.root=error,app.*.logging.level.org.springframework.boot=info,app.*.logging.level.org.springframework.cloud.stream.app=info,app.test-processor.logging.level.com.example=debug,deployer.http.kubernetes.createLoadBalancer=true,deployer.test-processor.kubernetes.startup-http-probe-delay=1
EOF

"$SCDF_SHELL/shell.sh" --spring.shell.commandFile="./register-app.shell"
