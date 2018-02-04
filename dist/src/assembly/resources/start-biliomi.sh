#!/bin/bash

cd $(dirname $(readlink -f $0))

JAVA_HOME=`which java`
JAVA_VER=`java -version 2>&1 | grep "java version"`
JAVA_VERNO=${JAVA_VER//[^0-9]/}

if [ ${JAVA_HOME} != "" -a ${JAVA_VERNO} -eq 180000 ]; then
  java -jar biliomi.jar $1
else
  echo "Java 8 is needed in order to run Biliomi. Please visit https://www.java.com to download and install Java 8."
fi