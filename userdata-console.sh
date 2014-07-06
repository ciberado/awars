#!/bin/bash


sudo wget -O /opt/bitnami/apache-tomcat/webapps/awarsconsole.war http://awars.javier-moreno.com.s3.amazonaws.com/AWarSConsole-0.0.2-SNAPSHOT.war

sudo /opt/bitnami/apache-tomcat/scripts/ctl.sh stop
sudo /opt/bitnami/apache-tomcat/scripts/ctl.sh start
