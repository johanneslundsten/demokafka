FROM openjdk:11-jre
COPY build/libs/*.jar /app/demo.jar
WORKDIR /app
EXPOSE 8080
CMD ["java","-jar","demo.jar"]