# Introducing Spring for Apache Pulsar 

## Live Demo - User Registration 

This is a simple Spring for Apache Pulsar application that takes a simple produce/consume use case through the following:
- Imperative produce/consume (user to/from user-topic)
- Improve the above usage w/ topic detection and config props
- Reactive produce/consume (single)
- Reactive produce/consume (stream)
- Add Spring Cloud Stream binder to process messages

There is a commit for each of the above steps. 

### Appears In
This demo was used in the following:
- DevNexus 2023 for [Introducing Spring for Apache Pulsar](https://devnexus.com/archive/devnexus2023/presentations/introducing-spring-for-apache-pulsar/) ([video](https://youtu.be/J4wqiooLi6s?si=7vNmCqUZAzudvi9t))
- Pulsar Summit 2023 (EU Virtual) [Introducing Spring for Apache Pulsar](https://pulsar-summit.org/event/europe-2023/sessions/europe-2023-introducing-spring-for-apache-pulsar) ([video](https://youtu.be/ltDpip8s6Uk?si=OMawoPyEDcib54k3))
- Pulsar Summit 2023 (North America) [Introducing Spring for Apache Pulsar](https://pulsar-summit.org/event/north-america-2023/sessions/north-america-2023-introducing-spring-for-apache-pulsar)

### Building
To build the application execute the following command:
```shell
./gradlew build
```
### Running
> **_NOTE:_**  Be sure you have an Apache Pulsar standalone cluster available at `pulsar://localhost:6650`

#### IDE
To run the app in your IDE just choose `Run/Debug` on the `PulsarApplication` class.

#### Command Line
To run the app on the command line open a terminal and execute the following command:
```shell
./gradlew bootRun
```
> **_TIP:_**  When running on the command line the `add-opens` JVM args are automatically configured which prevents annoying exceptions at runtime.

### GraalVM Native Image
#### Native Build Tools
To build a native image using the GraalVM native build tools, follow [these pre-requisites](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#native-image.developing-your-first-application.native-build-tools) 
to install GraalVM on your machine, but choose `22.3.3.r17-grl` as the version.

> **_WARNING:_**  If you don't choose `22.3.3.r17-grl` as the version you will likely run into issues when building.

Compile the demo into a GraalVM native image with:
```shell
./gradlew nativeCompile
```
Then you can run the native image with:
```shell
./build/native/nativeCompile/intro-spring-pulsar-demo
```
You will see a huge improvement in startup time.

#### Paketo Buildpacks
If you prefer, you can instead use Paketo buildpacks to build the image by following [these steps](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#native-image.developing-your-first-application.buildpacks).
