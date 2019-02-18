FROM maven:3.3-jdk-8 as build
COPY . /tmp
WORKDIR /tmp
RUN mvn install

FROM store/oracle/serverjre:8
ARG VER
COPY --from=build /tmp/target/lora-tb-connector-$VER.jar /tmp/target/
WORKDIR /tmp/target/
CMD ["java", "-Xms32m", "-Xmx64m", "-jar", "lora-tb-connector-$VER.jar"]
