#!/bin/bash

sudo wget -O /opt/bitnami/apache-tomcat/webapps/awarsattacker.war http://awars.javier-moreno.com.s3.amazonaws.com/awarsattacker-0.0.1-SNAPSHOT.war

sudo /opt/bitnami/apache-tomcat/scripts/ctl.sh stop
sudo /opt/bitnami/apache-tomcat/scripts/ctl.sh start
