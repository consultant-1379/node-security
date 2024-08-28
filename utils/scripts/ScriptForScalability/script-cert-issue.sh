#!/bin/bash
my_dir="$(dirname "$0")"

source "$my_dir/pippo.sh"
# source pippo.sh

# File Name
outputDir="OUTPUT"
nodelist="nodesList"
certIssueFile="certIssue"
allNodesFile="allNodes.txt"
resultFile="result.txt"
fdnNodeFile="fdnNode.txt"

# Default values
batchSize=5
certType="OAM"
timeout=120
nodeType=('ERBS' 'RadioNode' 'MSRBS_V1')



usage() {
echo "Usage: `basename $0` [<OPTIONS>]
where:
  <OPTIONS>:
    -nt, --nodetype    node Type [RadioNode, ERBS, MSRBS_V1, ALL]
    -ct, --certtype    certificate Type [OAM,IPSEC]
    -b,  --batch        number of nodes for xml
    -t,  --timeout      seconds between two batch"
}

# check if the value provided is number
checkNumber() {
 if ! [[ "$2" =~ ^[0-9]+$ ]] ; then 
   echo "ERROR: $1 value must be a number"
   exit
 fi
}

containsElement () {
  local e
  for e in "${@:2}"; do [[ "$e" == "$1" ]] && return 0; done
  return 1
}

# check if nodeType provided is valid
checkNodeType() {
  if ! [ $2 == "ALL" ] ; then  
    if ! containsElement $2 "${nodeType[@]}" ; then 
     echo "ERROR: An incorrect value $2 has been encountered for nodeType possible value(s) are [${nodeSupported[@]}]"
     exit
    else 
      nodeType=($2)
    fi
  fi
}

# check if certType provided is valid
checkCertType() {
  certTypeSupported=('OAM' 'IPSEC')

  if ! containsElement $2 "${certTypeSupported[@]}" ; then 
     echo "ERROR: An incorrect value $2 has been encountered for certType possible value(s) are [${certTypeSupported[@]}]"
     exit
  fi
}


readParameter() {
for i in "$@"
do
case $1 in
    -nt|--nodetype)
    checkNodeType $1 $2 
    shift # past argument=value
    ;;
    -ct|--certtype)
    checkCertType $1 $2 
    certType=$2
    shift # past argument=value
    ;;
    -b*|--batch*)
    checkNumber $1 $2 
    batchSize=$2
    shift # past argument=value
    ;;
    -t*|--timeout*)
    checkNumber $1 $2 
    timeout=$2
    shift  # past argument=value
    ;;
    ?*)
    usage
    exit
    ;;
esac
shift 
done

}

# check that all new certificate serialNumber are different from initial list of certificates
# return count = number of duplicated serialNumber
countDuplicatedSerialNumber() {
  count=0
  cat $1 | (while read line
  do
    serialNumber=`echo $line| awk -F"," '{print $3}'`
    if ! [ $serialNumber == "N/A" ] ; then  
      node=`echo $line| awk -F"," '{print $1}' `
      grep -w $serialNumber $2
      if [ $? == 0 ] ; then 
        echo $serialNumber "duplicate for " $node " node" 
        count=$[$count + 1]
      fi
    fi
  done 
  return $count)
}

# get All Synchronized Nodes for each Node Type in "nodeType" array  
# Output: 
# a file for each nodeType with name nodelist."nodeType".txt (ex. nodelist.ERBS.txt)
# a file with all node allNodes.txt
getSynchronizedNodes() {
  for ((i=0; i < ${#nodeType[@]}; i++)); do
    /opt/ericsson/enmutils/bin/cli_app "cmedit get * networkelement.(netype==${nodeType[$i]}),CmFunction.syncStatus==SYNCHRONIZED -t"|/bin/grep "SYNCHRONIZED" |  /bin/awk '{print $1}' > $outputDir/$nodelist"."${nodeType[$i]}".txt"
    cat $outputDir/$nodelist"."${nodeType[$i]}".txt" >> $outputDir/$allNodesFile 
  done
}

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
  echo "Reading " $nodeFile "file"
    for nodeName in `cat $nodeFile`; do
      index=$((counter/$batchSize))
      if [ $((counter%$batchSize)) == 0 ]; then
         nameFile=$outputDir/$certIssueFile$index".xml"
         echo -e $headerNodes > $nameFile
      fi

      echo "Create " $nameFile "xml file"
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

# get certificates of all nodes (retrieved from allNodes.txt)
# Output:
# certificates.txt (certificates.txt.before if parameter is true) file with "secadm cert get" command informations
# certStateFiltered.txt (certStateFiltered.txt.before if parameter is true) file with NodeName, EnrollState and SerialNumber 
# of "secadm cert get" command informations
getCertificateState() {
  certificatesFile="certificates.txt"
  certStateFilteredFile="certStateFiltered.txt"
  if [ $1 == "true" ] ; then 
    certificatesFile=$certificatesFile".before"
    certStateFilteredFile=$certStateFilteredFile".before"
  fi

  for ((i=0; i < ${#nodeType[@]}; i++)); do
    command="/opt/ericsson/enmutils/bin/cli_app 'secadm cert get -ct ${certType} -nf file:${allNodesFile}' $outputDir/$allNodesFile"
    eval $command > $outputDir/$certificatesFile
    #cat $outputDir/$certificatesFile |grep NetworkElement | awk -F"\t" ' { print $1 " , " $2 " , " $5  }' | awk -F"=" ' { print $2 }'  > $outputDir/$certStateFilteredFile
    cat $outputDir/$certificatesFile |grep NetworkElement | awk -F"\t" ' { printf $1 " , " $2 " , "; if ($5=="") print "----------"; else print $5 }' | awk -F"=" ' { print $2 }'  > $outputDir/$certStateFilteredFile
  done
}

# add command web cli Start Time for all Nodes in result.txt file (created before)
addStartTimeInResult() {
  time=`date +%Y-%m-%dT%H:%M:%S`
  cat $1 | (while read line
  do
    node=`echo $line| awk -F"<NodeFdn>" '{print $2}'| awk -F"<" '{print $1}'`
    echo -e "NODE " $node "--" $time "\n"
    if ! [ "$node" == "" ] ; then
      sed -i "/$node/ s/$/\t $time/" $outputDir/$resultFile
    fi
  done)  
}

# Execute a cert issue command for all xml batch file (certIssue_"number".xml)
executeCertIssue() {
  for f in $outputDir/$certIssueFile*.xml; do
    addStartTimeInResult $f
    distr="/opt/ericsson/enmutils/bin/cli_app 'secadm cert issue -ct ${certType} -xf file:${f}' $f"
    eval $distr 
    echo issue started for $f
    echo Wating $timeout seconds for issue complete...
    sleep $timeout
  done
}

addNodeEnrollmentTimeInResultFile() {
 
  # Retrieve Fdn Node for All Node (also Unsynchronized or Unsupported)
  /opt/ericsson/enmutils/bin/cli_app 'cmedit get * ManagedElement'|grep FDN | awk -F":" '{ print $2 }'> $fdnNodeFile

  for nodeFile in `ls $outputDir/$nodelist*`; do
    nodeType=`echo $nodeFile |awk -F"." '{print $2}'`
    echo "NODE TYPE = " $nodeType

    for nodeName in `cat $nodeFile`; do
      fdnNodeName=`grep $nodeName $fdnNodeFile`
      if [ $certType == "OAM" ] ; then 
        if [ $nodeType == "RadioNode" ] || [ $nodeType == "MSRBS_V1" ] ; then
          nodeCredential=`/opt/ericsson/enmutils/bin/cli_app "cmedit get $fdnNodeName,SystemFunctions=1,SysM=1,NetconfTls=1" |grep NodeCredential |awk -F":" '{ print $2 }'`
          timeActionCompleted=`/opt/ericsson/enmutils/bin/cli_app "cmedit get $nodeCredential" |grep enrollmentProgress |
                               awk -F"timeActionCompleted=" '{ print $2 }' |awk -F"," '{ print $1 }'`
          sed -i "/$nodeName/ s/$/\t $timeActionCompleted/" $outputDir/$resultFile
        fi
      else # IPSEC
      if [ $nodeType == "RadioNode" ] ; then
        nodeCredential=`/opt/ericsson/enmutils/bin/cli_app "cmedit get $fdnNodeName,Transport=1,Ikev2PolicyProfile=1" |grep NodeCredential |awk -F":" '{ print $2 }'`
        timeActionCompleted=`/opt/ericsson/enmutils/bin/cli_app "cmedit get $nodeCredential" |grep enrollmentProgress |
                             awk -F"timeActionCompleted=" '{ print $2 }' |awk -F"," '{ print $1 }'`
        sed -i "/$nodeName/ s/$/\t $timeActionCompleted/" $outputDir/$resultFile
      fi
      if [ $nodeType == "MSRBS_V1" ] ; then
         nodeCredential="$fdnNodeName,SystemFunctions=1,SecM=1,CertM=1,NodeCredential=2"
         timeActionCompleted=`/opt/ericsson/enmutils/bin/cli_app "cmedit get $nodeCredential" |grep enrollmentProgress |
                              awk -F"timeActionCompleted=" '{ print $2 }' |awk -F"," '{ print $1 }'`
         sed -i "/$nodeName/ s/$/\t $timeActionCompleted/" $outputDir/$resultFile
      fi
    fi
       # ERB info not present 
   done
done

}

addCertificateValidFromInResultFile() {
 certTypeLowerCase=`echo "$certType" | awk '{print tolower($0)}'`

 for nodeName in `cat $outputDir/$allNodesFile`; do

      entityName=$nodeName"-"$certTypeLowerCase

       # Note: we may use the current year (i.2. 2016) and current year + 2 as alternative to "\t"
       certInfo=`/opt/ericsson/enmutils/bin/cli_app "pkiadm ctm EECert -l -en $entityName -s active " |grep $entityName | awk -F"\t" ' {print $6}'`
      
      sed -i "/$nodeName/ s/$/\t $certInfo/" $outputDir/$resultFile
  done
 
}


processingCertIssueResults() {
  certIssuedFile="certIssued.txt"
  cat $outputDir/$certStateFilteredFile | grep "IDLE" > $outputDir/$certIssuedFile
  countDuplicatedSerialNumber $outputDir/$certIssuedFile $outputDir/$certStateFilteredFile".before"
  duplicated=$?

  numberNodes=`cat $outputDir/$allNodesFile | wc -l`
  numberNodesSuccess=`cat $outputDir/$certIssuedFile | wc -l`
  numberNodesSuccess=$[$numberNodesSuccess-$duplicated]

  addCertificateValidFromInResultFile
  addNodeEnrollmentTimeInResultFile
  
}


# INIT SCRIPT 

# Create Output directory
currentDir=`pwd`
if [ -e $currentDir/$outputDir ] ; then 
   /bin/rm -rf $currentDir/$outputDir
fi
/bin/mkdir $currentDir/$outputDir

# Read Parameters
readParameter $@
echo Execute Certificate issue web cli command of ${nodeType[@]} in batch of $batchSize nodes with a sleep of $timeout seconds 
echo "nodeType = "${nodeType[@]}

# Getting Sync Nodes
echo "Getting Sync Nodes" 
getSynchronizedNodes

# Create Cert Issue xml file
echo "Create Cert Issue xml file"
createCertIssueXmlFile

# Get Certificates of all nodes before secadm cert issue command
echo "Get Certificates of all nodes before secadm cert issue command"
getCertificateState true

# Execute certificate issue web cli command
echo "Execute certificate issue web cli command"
executeCertIssue

# Get Certificates of all nodes after secadm cert issue command
echo "Get Certificates of all nodes after secadm cert issue command"
getCertificateState false

# Processing certificate results
echo " Processing certificate results"
processingCertIssueResults

echo "Create certificate for " $numberNodesSuccess " nodes of " $numberNodes 

