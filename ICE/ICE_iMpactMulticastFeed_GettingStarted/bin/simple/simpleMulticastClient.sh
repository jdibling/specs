#!/bin/bash

#
# Usage
#
#		The simple multicast client accepts the following system parameters
#
#		multicast.group.address
#		multicast.port
#		multicast.network.interface - the specific network interface on which to listen for multicast datagrams
#
#

. ../common.sh

java $JVMARGS -classpath $CLASSPATH -Dmulticast.group.address=233.156.208.8 -Dmulticast.port=20008 com.theice.mdf.client.multicast.SimpleMulticastClient

