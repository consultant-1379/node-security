#!/bin/bash
netsimScript="/opt/ericsson/enmutils/bin/netsim"
netsim="netsim"
netsimListFile="netSimList.txt"
netsimInfoFile="netSimInfo.txt"
installEnmUtilNetSim="installEnmUtilNetSim.sh"

if [ $# -eq 1 ] ; then 
  netsim=$1
fi

 /tmp/$installEnmUtilNetSim

$netsimScript list $netsim > /tmp/$netsimListFile
$netsimScript info $netsim > /tmp/$netsimInfoFile
