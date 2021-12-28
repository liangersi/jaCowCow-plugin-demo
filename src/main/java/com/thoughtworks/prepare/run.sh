#!/bin/sh
JAVA_HOME=/Library/Java/JavaVirtualMachines/adoptopenjdk-8.jdk
PATH=${JAVA_HOME}/bin:$PATH
#CLASSPATH=.:${JAVA_HOME}/lib/dt.jar:${JAVA_HOME}/lib/tools.jar
export JAVA_HOME
export PATH
export CLASSPATH

#javac ./dto/HelloHP.java
#java -cp ./dto:. dto.HelloHP

#javac ./dto/fortest.java
#java -cp ./dto:. dto.fortest

CLASSPATH=./src/main/java/

javac ./src/main/java/com/thoughtworks/prepare/JaCowCow.java
java -cp $CLASSPATH com/thoughtworks/prepare/JaCowCow
