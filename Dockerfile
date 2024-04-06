FROM eclipse-temurin:17-jdk-alpine 

ADD /target/tourguide-0.0.1-SNAPSHOT.jar /tourguide-0.0.1-SNAPSHOT.jar

EXPOSE 8080
 
ENTRYPOINT ["java","-jar","/tourguide-0.0.1-SNAPSHOT.jar"]
