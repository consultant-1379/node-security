#!/bin/bash
my_dir="$(dirname "$0")"
. "$my_dir/common/common.sh"

ms="192.168.0.42"
netSimServer="192.168.0.2"
configure="false"

usage() {
echo "Usage: `basename $0` [<OPTIONS>]
where:
  <OPTIONS>:
    -p|--patches    install also the netsim Patch (you must have all netsim patch file in patches directory)  
    -c|--configure  automatically configure started node on ENM"
}

readParameter() {
for i in "$@"
do
case $1 in
    -p|--patches)
    echo "all patches in $currentDir/patches dir will be installed on netsim" 
    sh $currentDir/installPatch.sh $netSimServer
    ;;
    -c|--configure )
    configure="true"
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

readParameter $@

# Retrieve netsim simulations and nodes from ms [stored in netSimList.txt -  netSimInfo.txt] 
getNetSimFiles
python $currentDir/$scriptENM/$createScriptsForENM $currentDir/$target/$netSimListTarget $currentDir/$target/$netSimInfoTarget

if [ "$configure" == true ] ; then
    echo "Configure all Started Nodes"
    sh $currentDir/configureStartedNode.sh $target 
fi
