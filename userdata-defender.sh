#!/bin/bash

REM ************ SUSTITUYE LA URL Y EL WAR_ID **********************************************************
echo 'export METRICS_URL=http://awars.javier-moreno.com/awarsconsole/api' | sudo tee -a /etc/environment
echo 'export WAR_ID=SPAIN' | sudo tee -a /etc/environment
sudo wget -O /opt/bitnami/apache-tomcat/webapps/defender.war http://awars.javier-moreno.com.s3.amazonaws.com/AWarSDefender-0.0.1-SNAPSHOT.war

sudo /opt/bitnami/apache-tomcat/scripts/ctl.sh stop
sudo /opt/bitnami/apache-tomcat/scripts/ctl.sh start
