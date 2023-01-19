FROM openjdk:11
WORKDIR /app
COPY target/pdfutil-0.0.1-SNAPSHOT.jar pdfutil.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "pdfutil.jar"]