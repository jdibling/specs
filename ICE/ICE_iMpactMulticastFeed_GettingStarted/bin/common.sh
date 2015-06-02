#!/bin/bash
#
# Common shell script
# 
# The flag java.net.preferIPv4Stack is set to true in order to overcome network problems observed on certain Linux environments
#
#
export PATH=./lib:$PATH
export INSTALL_DIR=..
export JVMARGS="-Xms512m -Xmx512m -Djava.net.preferIPv4Stack=true "
export CLASSPATH=.:./conf:$INSTALL_DIR/lib/*
