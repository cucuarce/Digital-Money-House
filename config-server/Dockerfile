FROM openjdk:17-slim
WORKDIR /app
COPY target/*.jar app.jar

RUN apt-get update && apt-get install -y openssh-client
RUN mkdir -p ~/.ssh
RUN ssh-keyscan gitlab.com >> ~/.ssh/known_hosts

ENTRYPOINT ["java", "-jar", "app.jar"]
EXPOSE 8888
