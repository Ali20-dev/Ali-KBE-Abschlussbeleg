FROM openjdk:11
COPY ./songsWS/target/songsWS-ALIS.jar ~/
WORKDIR ~/
EXPOSE 8080
CMD ["java", "-jar", "songsWS-ALIS.jar"]
