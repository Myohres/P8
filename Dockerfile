FROM eclipse-temurin:17-jdk-alpine 

VOLUME /tmp

RUN  mvn install:install-file -Dfile=libs/gpsUtil.jar -DgroupId=gpsUtil -DartifactId=gpsUtil-Dversion=1.0.0 -Dpackaging=jar 
RUN  mvn install:install-file -Dfile=libs/RewardCentral.jar -DgroupId=rewardCentral-DartifactId=rewardCentral -Dversion=1.0.0 -Dpackaging=jar 
RUN  mvn install:install-file -Dfile=libs/TripPricer.jar -DgroupId=tripPricer -DartifactId=tripPricer-Dversion=1.0.0 -Dpackaging=jar
RUN  mvn clean install -Dmaven.test.skip=true

ADD target/* .jar  tourguide-0.0.1-SNAPSHOT.jar

EXPOSE 8080
  
ENTRYPOINT ["java","-jar","tourguide-0.0.1-SNAPSHOT.jar"]

