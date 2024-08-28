#!/bin/bash

netSimListHeader="netSimList"
netSimInfoHeader="netSimInfo"
netSimListFile="netSimList.txt"
netSimInfoFile="netSimInfo.txt"
netSimPatchesDirectory="patches"
scriptENM="scriptENM"
target="target"

createNetSimFiles="createNetSimFiles.sh"
installEnmUtilNetSim="installEnmUtilNetSim.sh"
createScriptsForENM="createScriptsForENM.py"
msPassword="12shroot"
netSimServer=""
ms=""

currentDir=`pwd`
user=`whoami`

/bin/rm -f /home/$user/.ssh/known_hosts

getNetSimFiles() {
  netSimListTarget=$netSimListHeader$1".txt"
  netSimInfoTarget=$netSimInfoHeader$1".txt"
  
echo "NOME FILE = " $netSimListFile "- " $netSimInfoFile
  sshpass -p $msPassword scp -o "StrictHostKeyChecking=no" $currentDir/common/$installEnmUtilNetSim root@$ms:/tmp
  sshpass -p $msPassword scp -o "StrictHostKeyChecking=no" $currentDir/common/$createNetSimFiles root@$ms:/tmp
  sshpass -p $msPassword ssh -o "StrictHostKeyChecking=no" root@$ms /tmp/$createNetSimFiles $netSimServer

  sshpass -p $msPassword scp -o "StrictHostKeyChecking=no" root@$ms:/tmp/$netSimListFile $currentDir/$target/$netSimListTarget
  sshpass -p $msPassword scp -o "StrictHostKeyChecking=no" root@$ms:/tmp/$netSimInfoFile $currentDir/$target/$netSimInfoTarget

  sed -i -e 's/\x1b\[[0-9;]*m//g' -e 's/^[ \t]*//' $currentDir/$target/$netSimListTarget
  sed -i '/^\s*$/d' $currentDir/$target/$netSimListTarget
  sed -i '/Simulations:/d' $currentDir/$target/$netSimListTarget 
  sed -i '/Netsim operations/d' $currentDir/$target/$netSimListTarget 
  sed -i -e 's/\x1b\[[0-9;]*m//g' -e 's/^[ \t]*//' $currentDir/$target/$netSimInfoTarget
  
}
