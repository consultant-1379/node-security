#!/bin/bash
netsimPatchesDirectory="patches"
netsimServer=$1

if [ -e `pwd`/$netsimPatchesDirectory ] ; then
    sshpass -p netsim scp -o "StrictHostKeyChecking=no" -r `pwd`/$netsimPatchesDirectory netsim@$netsimServer:/tmp
    sshpass -p netsim scp -o "StrictHostKeyChecking=no" -r `pwd`/common/netsimPatchInstall.sh netsim@$netsimServer:/tmp
    sshpass -p netsim ssh -o "StrictHostKeyChecking=no"  netsim@$netsimServer /tmp/netsimPatchInstall.sh
else 
    echo "The `pwd`/$netsimPatchesDirectory doesn't exists"
fi

