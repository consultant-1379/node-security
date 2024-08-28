#!/bin/bash
my_dir="$(dirname "$0")"
. "$my_dir/common/common.sh"

usage() {
echo "Usage: `basename $0` [<OPTIONS>]
where:
  <OPTIONS>:
    -id  		clusterId or MS name 
    -netsim  		netsim name Server
    -max  		max number of netsim"
   "i.e. $0 -id 429 -netsim ieatnetsimv7002 -max 2"
}

readParameter() {
for i in "$@"
do
case $1 in
    # MS server name
    -id)
    if [[ $2 =~ ^[0-9] ]]; then
      ms=`wget -q -O - --no-check-certificate "https://cifwk-oss.lmera.ericsson.se/generateTAFHostPropertiesJSON/?clusterId=${2}&tunnel=true" | awk -F',' '{print $1}' | awk -F':' '{print $2}' | sed -e "s/\"//g" -e "s/ //g"`
    else
      ms=$2.athtem.eei.ericsson.se
    fi
    shift # past argument=value
    ;;
    # NetSim Server Name
    -netsim)
    netsimName=$2
    shift # past argument=value
    ;;
    # Max number of netsim simulation
    -max)
    if ! [[ "$2" =~ ^[0-9]+$ ]] ; then 
      echo "ERROR: $1 value must be a number"
      exit
    fi
    max=$2
    shift # past argument=value
    ;;
    ?*)
    usage
    exit
    ;;
esac
shift 
done

}

if [ -e $currentDir/$target ] ; then
  /bin/rm -rf $currentDir/$target
fi
/bin/mkdir $currentDir/$target

# Read Info Server Parameter 
if ! [ $# -eq 6 ] ; then
  usage
  exit
fi

readParameter $@

for i in `seq 01 $max` ;  do
  value=$(printf "%02d" $i)
  netSimServer=$netsimName-$value.athtem.eei.ericsson.se

  # Retrieve netsim simulations and nodes from ms [stored in netSimList_"01".txt -  netSimInfo_"01".txt] for all netsim servers 
  getNetSimFiles "_"$value
  python $currentDir/$scriptENM/$createScriptsForENM $currentDir/$target/$netSimListTarget $currentDir/$target/$netSimInfoTarget $netsimName-$value
done
