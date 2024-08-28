#!/bin/bash
my_dir="$(dirname "$0")"

source "$my_dir/common.sh"

# Files definition
trustDistribution="trustDistribution"

# Output:
# trustDistribution_"number".txt file (where number is related to the batch size) 
# result.txt file with node, nodeType, startCliCmd columns
createBatchFiles() {
  echo -e "NodeName \t NodeType \t StartCliCmd \t  " > $outputDir/$resultFile

  counter=0
  for nodeFile in `ls $outputDir/$nodelist*`; do
    echo -e "\tReading " $nodeFile "file"
    for nodeName in `cat $nodeFile`; do
      index=$((counter/$batchSize))
      if [ $((counter%$batchSize)) == 0 ]; then
         nameFile=$outputDir/$trustDistribution$index".txt"
         echo -e "\tCreate " $nameFile " file"
      fi

      echo $nodeName >> $nameFile
      counter=$[$counter +1]

      nodeType=`echo $nodeFile |awk -F"." '{print $2}'`
      echo -e $nodeName "\t" $nodeType "\t" >> $outputDir/$resultFile
    done
  done
}


# Execute a cert issue command for all xml batch file (certIssue_"number".xml)
executeTrustDistribution() {
  for f in $outputDir/$trustDistribution*.txt; do
    
    # TODO modificare addStartTimeInResult per renderla generica 
    time=`date +%Y-%m-%dT%H:%M:%S` 
    cat $f | (while read line
    do
      sed -i "/$line/ s/$/\t $time/" $outputDir/$resultFile
    done)
    
    distr="/opt/ericsson/enmutils/bin/cli_app 'secadm trust distribute -ct ${certType} -nf file:${f}' $f"
    eval $distr >> /tmp/stdout.out 
    echo -e "\tTrust Distribute started for "$f
    if ! [ $timeout -eq 0 ]; then 
      echo -e "\tWating $timeout seconds for trust distribute complete..."
      sleep $timeout
    else
      numNodes=`cat $f | wc -l`
      if [ $numNodes -gt 120 ]; then
        echo -e "\tWating $numNodes seconds for trust distribute complete..."
        sleep $numNodes
      else
        echo -e "\tWating 120 seconds for trust distribute complete..."
        sleep 120
      fi
    fi
 done
}

processingTrustDistributionResults() {
  # Write Summary File
  numberNodes=`cat $outputDir/$allNodesFile | wc -l`
  echo -e "Trust distributed on "$numberNodes" nodes\n" >> $outputDir/$summaryFile
  echo -e "\n\nList of new trusts:\n\n" >> $outputDir/$summaryFile
  grep -Fxvf $outputDir/$trustsFilteredFile".before" $outputDir/$trustsFilteredFile >> $outputDir/$summaryFile
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
  test="TEST: Execute Trust Distribution web cli command of ${nodeType[@]} in batch of all synchronized nodes"
else
  test="TEST: Execute Trust Distribution web cli command of ${nodeType[@]} in batch of $batchSize nodes"
fi

echo $test
echo -e $test"\n" > $outputDir/$summaryFile


# Getting Sync Nodes
echo "Step 1 : Getting Sync Nodes" 
getSynchronizedNodes

# Create batch Files
echo "Step 2 : Create batch Files" 
createBatchFiles

# Get Trust Certificates of all nodes 
echo "Step 3 : Get Trust Certificates of all nodes"
getTrusts true

# Execute Trust Distribution web cli command
echo "Step 4 : Execute Trust Distribution web cli command"
executeTrustDistribution

# Get Trust Certificates of all nodes after secadm trust distribute command
echo "Step 5 : Get Trust of all nodes after secadm trust distribute command"
getTrusts false

# Processing results
echo "Step 6 : Processing results"
processingTrustDistributionResults

