FROM gradle:7.0.0-jdk11 as builder
USER root

ADD . .

RUN ["gradle", "build", "-x", "test", "--stacktrace"]

FROM alpine:3.15

RUN wget -O /OpenJDK17U.tar.gz https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.3%2B7/OpenJDK17U-jre_x64_alpine-linux_hotspot_17.0.3_7.tar.gz && \
	echo "27e4589db9b8e6df60a75737f12ab7df63f796ecf8e46c0a196f77b2c99af1ac  /OpenJDK17U.tar.gz" | sha256sum -c - && \
	tar x -ovzf OpenJDK17U.tar.gz && \
	rm -rf OpenJDK17U.tar.gz && \
    mkdir -p /opt/java/openjdk && \
    mv jdk-17.0.3\+7-jre/* /opt/java/openjdk && \
    rm -rf jdk-17.0.3\+7-jre

ENV LANG C.UTF-8

ENV JAVA_HOME=/opt/java/openjdk
ENV PATH=$PATH:/opt/java/openjdk/bin

RUN mkdir /opt/app-root
WORKDIR /opt/app-root
RUN chmod g+w /opt/app-root
COPY --from=builder /home/gradle/build/container .
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "spring-coroutine-multiThread-webflux.jar"]
