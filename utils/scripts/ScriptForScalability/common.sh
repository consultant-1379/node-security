#!/bin/bash
my_dir="$(dirname "$0")"
source "$my_dir/utils.sh"

# Files definition 
outputDir="OUTPUT"
allNodesFile="allNodes.txt"
nodelist="nodesList"
resultFile="result.txt"
fdnNodeFile="fdnNode.txt"
serialNumberFile="serialNumber.txt"
summaryFile="summary.txt"
nodeModelIdentitySyncFile="nodeModelIdentitySync"
supportedNodeModelIdentityFile_ERBS="supportedNodeModelIdentity_ERBS.txt"
supportedNodeModelIdentityFile_Radionode="supportedNodeModelIdentity_RadioNode.txt"
supportedNodeModelIdentityFile_MSRBS_V1="supportedNodeModelIdentity_MSRBS_V1.txt"

# Default values
batchSize=0
certType="OAM"
timeout=0
nodeTypeWithMaxValues=('ERBS' 'RadioNode' 'MSRBS_V1')
nodeType=('ERBS' 'RadioNode' 'MSRBS_V1')
onlyChecks=false


usage() {
echo "Usage: `basename $0` [<OPTIONS>]
where:
  <OPTIONS>:
    -nt, --nodetype    node Type [i.e. RadioNode, ERBS, MSRBS_V1, ALL] = Max Number Nodes [ only valid if nodeType != ALL ]
    -ct, --certtype    certificate Type [OAM,IPSEC]
    -b,  --batch       number of nodes for xml
    -t,  --timeout     seconds between two batch
    -i,  --issuer      issuer name of trust [ used ONLY for trust remove ] 
    -v,  --verify      perform only gets"

   "i.e. $0 -nt ERBS=10250,RadioNode -ct OAM -b 2516 -t 36000 "
}

# check if nodeType provided is valid
checkNodeType() {
  if ! [ $2 == "ALL" ] ; then 
    IFS=',' read -ra nodeTypeInput <<< "$2"

    nodeType=()
    for i in "${nodeTypeInput[@]}"; do
       nodeTypeName=`echo $i | awk -F"=" '{print $1}'`
       nodeType+=($nodeTypeName)
       if ! containsElement $nodeTypeName "${nodeType[@]}" ; then 
         echo "ERROR : An incorrect value $nodeTypeName has been encountered for nodeType possible value(s) are [${nodeSupported[@]}]"
         exit
       fi
    done

    nodeTypeWithMaxValues=("${nodeTypeInput[@]}")
    
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
    echo $2
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
    -i*|--issuer*)
    issuerToDelete=$2
    shift  # past argument=value
    ;;
    -v*|--verify*)
    onlyChecks=true
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
checkCertificateIssueResult() {
  count=0
  cat $1 | (while read line
  do
    
   node=`echo $line| awk -F"," '{print $1}' `
   state=`echo $line| awk -F"," '{print $2}'`
   if [ $state == "IDLE" ] ; then
      serialNumber=`echo $line| awk -F"," '{print $4}'`
      if ! [ $serialNumber == "N/A" ] ; then  
        grep -w $serialNumber $2
        if [ $? == 0 ] ; then 
          echo -e  $node "\t Unchanged Certificate" >> $outputDir/$summaryFile
          count=$[$count + 1]
        fi
      fi
    else
      errorMessage=`echo $line| awk -F"," '{print $3}'`
      echo -e $node "\t Error $state $errorMessage" >> $outputDir/$summaryFile
      count=$[$count + 1]
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
   nodeModelIdentitySyncFileCurrent=$outputDir/$nodeModelIdentitySyncFile"."${nodeType[$i]}".txt"
  /opt/ericsson/enmutils/bin/cli_app "cmedit get * networkelement.(netype==${nodeType[$i]},nodeModelIdentity),CmFunction.syncStatus==SYNCHRONIZED -t"|/bin/grep -v "SYNCHRONIZED"  |/bin/grep ${nodeType[$i]} > $nodeModelIdentitySyncFileCurrent

  supportedNodeModelIdentityFile=""
  if [ $nodeType == "RadioNode" ] ; then
    supportedNodeModelIdentityFile="supportedNodeModelIdentityFile_Radionode"
  fi

  if [ $nodeType == "MSRBS_V1" ] ; then
    supportedNodeModelIdentityFile="supportedNodeModelIdentityFile_MSRBS_V1"
  fi
  
  if [ $nodeType == "ERBS" ] ; then
    supportedNodeModelIdentityFile="supportedNodeModelIdentityFile_ERBS"
  fi
 
  echo "supported Node Identity File : "$supportedNodeModelIdentityFile

  if [ -e $supportedNodeModelIdentityFile ] ; then
      grep -f $supportedNodeModelIdentityFile $nodeModelIdentitySyncFileCurrent |/bin/awk '{print $1}' >  $outputDir/$nodelist"."${nodeType[$i]}".txt"
  else 
      cat $nodeModelIdentitySyncFileCurrent |/bin/awk '{print $1}' >  $outputDir/$nodelist"."${nodeType[$i]}".txt"
  fi

  max=`echo ${nodeTypeWithMaxValues[$i]} | awk -F"=" '{ print $2 }'`
  if ! [ "$max" == "" ] ; then
    max=$(($max+1))
    sed -i "$max,$ d" $outputDir/$nodelist"."${nodeType[$i]}".txt"
  fi
  cat $outputDir/$nodelist"."${nodeType[$i]}".txt" >> $outputDir/$allNodesFile 
  done
  if [ $batchSize -eq 0 ] ; then
     batchSize=`cat  $outputDir/$allNodesFile | wc -l`
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

#  for ((i=0; i < ${#nodeType[@]}; i++)); do
    command="/opt/ericsson/enmutils/bin/cli_app 'secadm cert get -ct ${certType} -nf file:${allNodesFile}' $outputDir/$allNodesFile"
    eval $command > $outputDir/$certificatesFile
    #cat $outputDir/$certificatesFile |grep NetworkElement | awk -F"\t" ' { print $1 " , " $2 " , " $5  }' | awk -F"=" ' { print $2 }'  > $outputDir/$certStateFilteredFile
    cat $outputDir/$certificatesFile |grep NetworkElement | awk -F"\t" ' { printf $1 ", "$2", "$3", "; if ($5=="") print "----------"; else print $5 }' | awk -F"=" ' { print $2 }'  > $outputDir/$certStateFilteredFile
 # done
}

# add command web cli Start Time for all Nodes in result.txt file (created before)
addStartTimeInResult() {
  time=`date +%Y-%m-%dT%H:%M:%S`
  cat $1 | (while read line
  do
    node=`echo $line| awk -F"<NodeFdn>" '{print $2}'| awk -F"<" '{print $1}'`
    if ! [ "$node" == "" ] ; then
      sed -i "/$node/ s/$/\t $time/" $outputDir/$resultFile
    fi
  done)  
}

addNodeEnrollmentTimeInResultFile() {
 
  # Retrieve Fdn Node for All Node (also Unsynchronized or Unsupported)
  /opt/ericsson/enmutils/bin/cli_app 'cmedit get * ManagedElement'|grep FDN | awk -F":" '{ print $2 }'> $fdnNodeFile

  for nodeFile in `ls $outputDir/$nodelist*`; do
    nodeType=`echo $nodeFile |awk -F"." '{print $2}'`

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

getTrusts() {
  trustsFile="trusts.txt"
  trustsFilteredFile="trustsFiltered.txt"

  if [ $1 == "true" ] ; then 
    trustsFile=$trustsFile".before"
    trustsFilteredFile=$trustsFilteredFile".before"
  fi

  command="/opt/ericsson/enmutils/bin/cli_app 'secadm trust get -ct ${certType} -nf file:${allNodesFile}' $outputDir/$allNodesFile"
  eval $command > $outputDir/$trustsFile
  createNodeTrustsFilteredFile $outputDir/$trustsFile

}

createNodeTrustsFilteredFile(){
  cat $1 | (while read line
  do
    node=`echo -e "$line"| awk -F"\t" '{print $1}'`
    if [[ $node == Net* ]] ; then 
       # NetworkElement
      ne=`echo $node | awk -F"NetworkElement=" ' { print $2}'`
      state=`echo -e "$line"| awk -F"\t" '{print $2}'`
      message=`echo -e "$line"| awk -F"\t" '{print $3}'`
      subject=`echo -e "$line"| awk -F"\t" '{print $4}'`
      sn=`echo -e "$line"| awk -F"\t" '{print $5}'`
      issuer=`echo -e "$line"| awk -F"\t" '{print $6}'`
    elif  [[ $node == CN=* ]] ; then 
      subject=`echo -e "$line"| awk -F"\t" '{print $1}'`
      sn=`echo -e "$line"| awk -F"\t" '{print $2}'`
      issuer=`echo -e "$line"| awk -F"\t" '{print $3}'`
    else 
      continue
    fi
   
    if  ! [[ $sn == "N/A"  || $sn == ""  ||  $sn == "0" ]] ; then 
      echo -e $ne "\t" $subject "\t" $sn "\t" $issuer "\t" >> $outputDir/$trustsFilteredFile
      grep -w $sn $outputDir/$serialNumberFile >/tmp/stdout.out  
      if [ $? == 1 ] ; then 
        echo -e $sn >> $outputDir/$serialNumberFile 
      fi
    fi
  done)
}
