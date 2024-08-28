#!/bin/sh

my_version=1.0.0
my_nes=

usage() {
cat << EOF

Usage: `basename $0` <NES> [<OPTIONS>]
where:
  <NES>                          The node simulations (e.g. LTE07dg2ERBS00160 for a single node
                                                            LTE07dg2ERBS00 or LTE07dg2ERBS for multiple nodes)
  <OPTIONS>:
    -h, --help                   This help
    -v, --version                The script version

  The script gets IP address of given nodes.

EOF
}

version() {
cat << EOF

`basename $0` VERSION: $my_version

EOF
}

param=0

while [ "$1" != "" ] ; do
   case $1 in
     -h | --help )
       usage
       exit 0
       ;;
     -v | --version )
       version
       exit 0
       ;;
     -*)
       echo
       echo $1: unknown option
       usage
       exit 1
       ;;
     * )
      case $param in
        0 )
         my_nes=$1 
         param=$(($param + 1))
         ;;
        1 )
          echo
          echo $1: unknown parameter
          usage
          exit 1
         ;;
      esac
      ;;
     esac
     shift
done

if [ "$my_nes" == "" ]; then
    echo
    echo Missing nodes
    usage
    exit 1
fi

echo
echo "Executing netsim_shell commands ..."
echo

echo ".show allsimnes" | /netsim/inst/netsim_shell | grep $my_nes

echo
echo Done.
echo

exit 0
