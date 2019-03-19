FROM openjdk:11-jre
ADD scripts/wait-for-it.sh /scripts/wait-for-it.sh
RUN chmod 777 /scripts/wait-for-it.sh
COPY build/libs/*.jar /app/demo.jar
WORKDIR /app
EXPOSE 8080
CMD ["java","-jar","demo.jar"]