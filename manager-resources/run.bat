@echo off

REM JAR FILES TO LOAD: PATHS WITH JARS TO ADD TO JAVA CLASSPATH
REM make sure there are no spaces and delimit with ';'
set modulespaths=lib/*;modules/*;modules/lib/*

REM MAIN JAR TO LOAD
set mainjar=FlipperMMDS.jar

REM MAIN CLASS TO LOAD
REM package name and class name as defined in java file
set mainclass=eu.ariaagent.flipper.Main

REM DO NOT EDIT BELOW THIS LINE

REM add all modules and libraries to classpath
echo java -cp "%mainjar%;%modulespaths%" %mainclass%
@echo on
java -cp "%mainjar%;%modulespaths%" %mainclass%
@echo off
PAUSE
