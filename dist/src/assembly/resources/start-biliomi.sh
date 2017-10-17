#!/bin/bash

cd $(dirname $(readlink -f $0))

JAVA_HOME=`which java`
JAVA_VER=`java -version 2>&1 | grep "java version"`
JAVA_VERNO=${JAVA_VER//[^0-9]/}

if [ ${JAVA_HOME} != "" -a ${JAVA_VERNO} -ge 180000 ]; then
  FILES=(./biliomi-*.jar)
  java -Dinteractive -Dfile.encoding=UTF-8 -jar ${FILES[-1]}
else
  echo "Java 8 or higher is needed in order to run Biliomi."
  echo "Please visit https://www.java.com and download the latest version."
fi