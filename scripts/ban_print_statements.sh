#!/bin/bash

cd ../src/main/java/seng202/team6 || exit 1

# check for System.x.printlin
# check 3 levels
for java_file in *.java */*.java */*/*.java
do
  grep -E "System.(err|out).print" $java_file
  if [[ $? -eq 0 ]]
  then
    echo File print statement: $java_file
    exit 1
  fi
done