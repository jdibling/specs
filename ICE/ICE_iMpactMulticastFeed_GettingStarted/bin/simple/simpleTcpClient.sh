#!/bin/bash

. ../common.sh

java $JVMARGS -classpath $CLASSPATH -Dconfig=conf/simpleTcpClient.properties com.theice.mdf.client.examples.SimpleClient

