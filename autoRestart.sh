#!/bin/bash

#git get lastest commit
git stash
git pull --rebase origin master
git stash pop

#mvn complie package
mvn clean package

#shutdown tomcat
./usr/local/tools/apache-tomcat-8.0.36/bin/shutdown.sh

#copy to tomcat
cp target/index-1.0.war ./usr/local/tools/apache-tomcat-8.0.36/webapps

#start tomcat
./usr/local/tools/apache-tomcat-8.0.36/bin/startup.sh