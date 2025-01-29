FROM amazoncorretto:21.0.6
LABEL maintainer=vishwaprotim
WORKDIR /app
COPY target/CSV-Parser-0.0.1-SNAPSHOT.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","./app.jar"]
