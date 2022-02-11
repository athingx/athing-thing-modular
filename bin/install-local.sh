#!/bin/bash

projects[i++]="io.github.athingx.athing.thing.modular:thing-modular"
projects[i++]="io.github.athingx.athing.thing.modular:thing-modular-aliyun"

mvn clean install \
  -f ../pom.xml \
  -pl "$(printf "%s," "${projects[@]}")" -am \
  '-Dmaven.test.skip=true'