#!/bin/bash
my_dir="$(dirname "$0")"
. "$my_dir/common/common.sh"

if [ $# -eq 1 ] ; then
  target=$1
fi

netsimStartedToDeleteDirectory=`pwd`"/$target/startedToDelete"

ms="192.168.0.42"

sshpass -p $msPassword scp -o "StrictHostKeyChecking=no" $currentDir/common/$installEnmUtilNetSim root@$ms:/tmp
sshpass -p $msPassword ssh -o "StrictHostKeyChecking=no" root@$ms /tmp/$installEnmUtilNetSim

if [ -e $netsimStartedToDeleteDirectory ] ; then
  for filename in $netsimStartedToDeleteDirectory/* ; do
    sshpass -p $msPassword scp -o "StrictHostKeyChecking=no" $filename root@$ms:/tmp
    sshpass -p $msPassword scp -o "StrictHostKeyChecking=no" `pwd`/common/comando.sh root@$ms:/tmp
    fileNameWithoutPath=$(basename $filename)
    sshpass -p $msPassword ssh -o "StrictHostKeyChecking=no" root@$ms /tmp/comando.sh $fileNameWithoutPath 
  done 
else 
  echo "$netsimStartedToDeleteDirectory dir must be present"
fi

