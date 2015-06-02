#!/bin/bash

. ../common.sh

java $JVMARGS -classpath $CLASSPATH -Dconfig=conf/tunnelProxy.properties -Djava.util.logging.config.file=conf/mdflogging.properties com.theice.mdf.client.multicast.tunnel.TunnelProxy

