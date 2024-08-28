#!/bin/bash
my_dir="$(dirname "$0")"

source "$my_dir/common.sh"

# Files definition
nodeToRemoveTrusts="nodeToRemoveTrusts.txt"

# Execute a trust remove command for all Nodes with a SerialNumber and a Issuer 
executeTrustRemove() {
  for sn in `cat $outputDir/$serialNumberFile`; do
    grep $sn $outputDir/$trustsFilteredFile | awk '{print $1}' > $outputDir/$nodeToRemoveTrusts
    issuer=`grep -m 1 $sn $outputDir/$trustsFilteredFile | awk '{print $4}'`
    if [ -z $issuerToDelete ] || [ "$issuer" == "$issuerToDelete" ] ; then
      distr="/opt/ericsson/enmutils/bin/cli_app 'secadm trust remove -ct ${certType} --issuer-dn ${issuer} -sn ${sn} --nodefile file:$outputDir/$nodeToRemoveTrusts' $outputDir/$nodeToRemoveTrusts"
      eval $distr >> /tmp/stdout.out 
      echo -e "\tTrust Remove started for SN = "$sn "and ISSUER = " $issuer
      if ! [ $timeout -eq 0 ]; then 
        echo -e "\tWating $timeout seconds for trust remove complete..."
        sleep $timeout
      else
        numNodes=`cat $outputDir/$nodeToRemoveTrusts | wc -l`
        if [ $numNodes -gt 120 ]; then
          echo -e "\tWating $numNodes seconds for trust remove complete..."
          sleep $numNodes
        else
          echo -e "\tWating 120 seconds for trust remove complete..."
          sleep 120
        fi
      fi
    fi
 done
}

processingTrustRemoveResults() {
  # Write Summary File
  numberNodes=`cat $outputDir/$allNodesFile | wc -l`
  echo -e "Trust remove on "$numberNodes" nodes\n" >> $outputDir/$summaryFile
  echo -e "\n\nList of removed trusts:\n\n" >> $outputDir/$summaryFile
  grep -Fxvf $outputDir/$trustsFiltered $outputDir/$trustsFilteredFile".before" >> $outputDir/$summaryFile
}

# INIT SCRIPT 

# Create Output directory
currentDir=`pwd`
if [ -e $currentDir/$outputDir ] ; then 
   /bin/rm -rf $currentDir/$outputDir
fi
/bin/mkdir $currentDir/$outputDir

touch $outputDir/$serialNumberFile

# Read Parameters
readParameter $@
if [ $batchSize -eq 0 ] ; then 
  test="TEST: Execute Trust Remove web cli command of ${nodeType[@]} in batch of all synchronize nodes"
else
  test="TEST: Execute Trust Remove web cli command of ${nodeType[@]} in batch of $batchSize nodes"
fi

echo $test
echo -e $test"\n" > $outputDir/$summaryFile

# Getting Sync Nodes
echo "Step 1 : Getting Sync Nodes" 
getSynchronizedNodes

# Get Trust Certificates of all nodes 
echo "Step 2 : Get Trust Certificates of all nodes"
getTrusts true

# Execute Trust Remove web cli command
echo "Step 3 : Execute Trust Remove web cli command"
executeTrustRemove

# Get Trust Certificates of all nodes after secadm trust distribute command
echo "Step 4 : Get Trust Certificates of all nodes after secadm trust remove command"
getTrusts false

# Processing results
echo "Step 5 : Processing results"
processingTrustRemoveResults


