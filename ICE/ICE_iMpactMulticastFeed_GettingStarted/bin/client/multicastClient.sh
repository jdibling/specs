#!/bin/bash

. ../common.sh

java $JVMARGS -classpath $CLASSPATH -Dconfig=conf/multicastClientConfig.xml -Djava.util.logging.config.file=conf/mdflogging.properties com.theice.mdf.client.gui.MDFGUIClient

