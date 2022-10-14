# spring-resilience-sample

## run the sample
1. start the target service
```
cd target-service
mvn spring-boot:run
```
2. check if target service is running
    - Open http://localhost:8081 in browser
    - your should see *{"echo":"target server"}*
    - also check the sysout in the cmd. you should see something like: *called with duration: 100*

3. start the source service
```
cd source-service
mvn spring-boot:run
```
4. run the resilience tests by calling
    - http://localhost:8080
    - http://localhost:8080/echoError
    - check the sysout and the code to understand what happens or is expected
5. play around with the settings in application.yml or modify the settings directly in the code
6. check the various information about health and metrics
    - http://localhost:8080/actuator/metrics
    - http://localhost:8080/actuator/health

