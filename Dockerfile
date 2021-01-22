FROM openjdk:11
COPY ./songsWS/target/songsWS*.jar ~/
WORKDIR ~/
EXPOSE 8080
CMD ["java", "-jar", "songsWS*.jar"]