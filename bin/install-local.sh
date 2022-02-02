#!/bin/bash

projects[i++]="io.github.athingx.athing.aliyun.modular:modular-thing"
projects[i++]="io.github.athingx.athing.aliyun.modular:modular-thing-impl"

mvn clean install \
  -f ../pom.xml \
  -pl "$(printf "%s," "${projects[@]}")" -am \
  '-Dmaven.test.skip=true'