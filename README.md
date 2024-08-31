# Getting Started

## Reference Documentation

For further reference, please consider the following sections:

* [Official Gradle documentation](https://docs.gradle.org)
* [Spring Boot Gradle Plugin Reference Guide](https://docs.spring.io/spring-boot/3.3.3/gradle-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/3.3.3/gradle-plugin/packaging-oci-image.html)
* [GraalVM Native Image Support](https://docs.spring.io/spring-boot/3.3.3/reference/packaging/native-image/introducing-graalvm-native-images.html)
* [Spring Boot Testcontainers support](https://docs.spring.io/spring-boot/3.3.3/reference/testing/testcontainers.html#testing.testcontainers)
* [Testcontainers Kafka Modules Reference Guide](https://java.testcontainers.org/modules/kafka/)
* [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/3.3.3/reference/htmlsingle/index.html#actuator)
* [Function](https://docs.spring.io/spring-cloud-function/docs/current/reference/html/spring-cloud-function.html)
* [Cloud Stream](https://docs.spring.io/spring-cloud-stream/docs/current/reference/html/spring-cloud-stream.html#spring-cloud-stream-overview-introducing)
* [Spring Configuration Processor](https://docs.spring.io/spring-boot/docs/3.3.3/reference/htmlsingle/index.html#appendix.configuration-metadata.annotation-processor)
* [Spring for Apache Kafka](https://docs.spring.io/spring-boot/docs/3.3.3/reference/htmlsingle/index.html#messaging.kafka)
* [Prometheus](https://docs.spring.io/spring-boot/docs/3.3.3/reference/htmlsingle/index.html#actuator.metrics.export.prometheus)
* [Testcontainers](https://java.testcontainers.org/)

## Guides

The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service with Spring Boot Actuator](https://spring.io/guides/gs/actuator-service/)

## Additional Links

These additional references should also help you:

* [Gradle Build Scans – insights for your project's build](https://scans.gradle.com#gradle)
* [Configure AOT settings in Build Plugin](https://docs.spring.io/spring-boot/3.3.3/how-to/aot.html)
* [Various sample apps using Spring Cloud Function](https://github.com/spring-cloud/spring-cloud-function/tree/main/spring-cloud-function-samples)

## GraalVM Native Support

This project has been configured to let you generate either a lightweight container or a native executable.
It is also possible to run your tests in a native image.

The useNative properties in `gradle.properties` can be changed permanently or invoked as follows:

```shell
./gradlew bootBuildImage -PuseNative=true
```

### Lightweight Container with Cloud Native Buildpacks

If you're already familiar with Spring Boot container images support, this is the easiest way to get started.
Docker should be installed and configured on your machine prior to creating the image.

To create the image, run the following goal:

```shell
./gradlew bootBuildImage
```

### Executable with Native Build Tools

Use this option if you want to explore more options such as running your tests in a native image.
The GraalVM `native-image` compiler should be installed and configured on your machine.

NOTE: GraalVM 22.3+ is required.

To create the executable, run the following goal:

```
$ ./gradlew nativeCompile
```

Then, you can run the app as follows:

```
$ build/native/nativeCompile/test-processor
```

You can also run your existing tests suite in a native image.
This is an efficient way to validate the compatibility of your application.

To run your existing tests in a native image, run the following goal:

```
$ ./gradlew nativeTest
```

### Gradle Toolchain support

There are some limitations regarding Native Build Tools and Gradle toolchains.
Native Build Tools disable toolchain support by default.
Effectively, native image compilation is done with the JDK used to execute Gradle.
You can read more about [toolchain support in the Native Build Tools here](https://graalvm.github.io/native-build-tools/latest/gradle-plugin.html#configuration-toolchains).

### Testcontainers support

This project uses [Testcontainers at development time](https://docs.spring.io/spring-boot/3.3.3/reference/features/dev-services.html#features.dev-services.testcontainers).

Testcontainers has been configured to use the following Docker images:

* [`confluentinc/cp-kafka:latest`](https://hub.docker.com/r/confluentinc/cp-kafka)

Please review the tags of the used images and set them to the same as you're running in production.

## Running Tests

```shell
cd java
./gradlew test -Ptag=test
./gradlew test -Ptag=integration
# will probably hang when mock and container test are executed after each other
./gradlew test 
```

## SCDF Configuration

Build the container

```shell
./gradlew bootBuildImage -PuseNative=true
```

From data flow installation script [package](https://github.com/spring-cloud/spring-cloud-dataflow/releases/download/v2.11.4/spring-cloud-dataflow-package-2.11.4.zip)

This will load the local docker container into the configured k8s registry.

_The image may not load in kind or minikube if there is a pod running that was created with the image._

```shell
source ./deploy/k8s/export-dataflow-ip.sh
./deploy/k8s/load-image.sh "example.com/library/test-processor:latest" true
```

Register application and deploy stream.

```shell
export SCDF_SHELL=<location of ./deploy/shell>
./register-app-create-dsl.sh
```
