#!/bin/bash
my_dir="$(dirname "$0")"

source "$my_dir/common.sh"

# Files definition
certIssueFile="certIssue"

# build Xml <Node> section according to Node Type
xmlNodeSectionBuilder() {
 if [ $2 == "ERBS" ] ; then 
  enrollmentMode="CMPv2_INITIAL"
 fi
 if [ "$2" == "RadioNode" ] || [ "$2" == "MSRBS_V1" ] ; then 
  enrollmentMode="CMPv2_VC"
 fi

 xmlNodeSection="\t<Node>
      \n\t\t<NodeFdn>$1</NodeFdn>
      \n\t\t<EnrollmentMode>"$enrollmentMode"</EnrollmentMode>
      \n\t\t<KeySize>RSA_2048</KeySize>"
 if [ $2 == "MSRBS_V1" ] ; then 
  xmlNodeSection=$xmlNodeSection"\n\t\t<CommonName>$1.ericsson.se</CommonName>"
 fi

 xmlNodeSection=$xmlNodeSection"\n\t</Node>"
}

# Output:
# certIssue_"number".xml file (where number is related to the batch size) 
# result.txt file with node, nodeType, startCliCmd columns
createCertIssueXmlFile() {
  headerNodes="<?xml version='1.0' encoding='utf-8'?>\n<Nodes>"
  footerNodes="</Nodes>"

  echo -e "NodeName \t NodeType \t StartCliCmd \t Valid From \t EndTimeCert" > $outputDir/$resultFile

  counter=0
  for nodeFile in `ls $outputDir/$nodelist*`; do
    echo -e "\tReading " $nodeFile "file"
    for nodeName in `cat $nodeFile`; do
      index=$((counter/$batchSize))
      if [ $((counter%$batchSize)) == 0 ]; then
         nameFile=$outputDir/$certIssueFile$index".xml"
         echo -e $headerNodes > $nameFile
         echo -e "\tCreate " $nameFile "xml file"
      fi

      nodeType=`echo $nodeFile |awk -F"." '{print $2}'`
      
      xmlNodeSectionBuilder $nodeName $nodeType
      echo -e $xmlNodeSection >> $nameFile
      if [ $((($counter+1)%$batchSize)) == 0 ]; then
        echo $footerNodes >> $nameFile
      fi
      counter=$[$counter +1]

      echo -e $nodeName "\t" $nodeType "\t" >> $outputDir/$resultFile
    done
  done

# Add footer Nodes in file xml if not present
  cat $nameFile| grep $footerNodes > /tmp/stdout.out
  if [ $? == 1 ] ; then
    echo $footerNodes >> $nameFile
  fi
}

# Execute a cert issue command for all xml batch file (certIssue_"number".xml)
executeCertIssue() {
  for f in $outputDir/$certIssueFile*.xml; do
    addStartTimeInResult $f
    distr="/opt/ericsson/enmutils/bin/cli_app 'secadm cert issue -ct ${certType} -xf file:${f}' $f"
    eval $distr >> /tmp/stdout.out 
    echo -e "\tIssue started for "$f

    if ! [ $timeout -eq 0 ]; then 
      echo -e "\tWating $timeout seconds for issue complete..."
      sleep $timeout
    else
      numNodes=`grep "NodeFdn" $f | wc -l`
      if [ $numNodes -gt 120 ]; then
        echo -e "\tWating $numNodes seconds for issue complete..."
        sleep $numNodes
      else
        echo -e "\tWating 120 seconds for issue complete..."
        sleep 120
      fi
    fi
  done
}

processingCertIssueResults() {
  # Add Valid From and Enrollment Time (only for COM ECIM Nodes)i in result file
  addCertificateValidFromInResultFile
  addNodeEnrollmentTimeInResultFile

  # Write Summary File
  numberNodes=`cat $outputDir/$allNodesFile | wc -l`
  echo -e "Certificate created on  PLACEHOLDER_NODESUCCESS of "$numberNodes"\n" >> $outputDir/$summaryFile
  checkCertificateIssueResult $outputDir/$certStateFilteredFile $outputDir/$certStateFilteredFile".before"
  numberNodesFailed=$?

  numberNodesSuccess=`cat $outputDir/$certStateFilteredFile | wc -l`
  numberNodesSuccess=$[$numberNodesSuccess-$numberNodesFailed]

  sed -i "s/PLACEHOLDER_NODESUCCESS/$numberNodesSuccess/" $outputDir/$summaryFile

  
}


# INIT SCRIPT 

# Read Parameters
readParameter $@

# Create Output directory
currentDir=`pwd`
if [ $onlyChecks == "true" ] ; then
  if ! [ -e $currentDir/$outputDir ] ; then 
      echo " $outputDir directory not present. No Data to verify"
      exit
  fi

  test="TEST: Verify $outputDir Certificate issue data"
  echo $test

else
  if [ -e $currentDir/$outputDir ] ; then 
      /bin/rm -rf $currentDir/$outputDir
  fi
  /bin/mkdir $currentDir/$outputDir
 
  if [ $batchSize -eq 0 ] ; then 
    test="TEST: Execute Certificate issue web cli command of ${nodeType[@]} in batch of all synchronized nodes"
  else
    test="TEST: Execute Certificate issue web cli command of ${nodeType[@]} in batch of $batchSize nodes"
  fi
  echo $test

  # Initialize summary File
  echo -e $test"\n" > $outputDir/$summaryFile

  # Getting Sync Nodes
  echo "Step 1 : Getting Sync Nodes" 
  getSynchronizedNodes

  # Create Cert Issue xml file
  echo "Step 2 : Create Cert Issue xml file"
  createCertIssueXmlFile

  # Get Certificates of all nodes before secadm cert issue command
  echo "Step 3 : Get Certificates of all nodes before secadm cert issue command"
  getCertificateState true

  # Execute certificate issue web cli command
  echo "Step 4 : Execute certificate issue web cli command"
  executeCertIssue
fi

# Get Certificates of all nodes after secadm cert issue command
echo "Step 5 : Get Certificates of all nodes after secadm cert issue command"
getCertificateState false

# Processing certificate results
echo "Step 6 : Processing certificate results"
processingCertIssueResults

echo "Create certificate for " $numberNodesSuccess " nodes of " $numberNodes 

