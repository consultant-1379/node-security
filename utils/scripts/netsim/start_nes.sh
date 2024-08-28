#!/bin/sh

my_version=1.0.0
my_sim=
my_nes=

usage() {
cat << EOF

Usage: `basename $0` <SIM> <NES> [<OPTIONS>]
where:
  <SIM>                          The simulation name (e.g. LTE16A-V17x160-5K-DG2-FDD-LTE07)
  <NES>                          The node simulations (e.g. LTE07dg2ERBS00160 for a single node
                                                            LTE07dg2ERBS00 or LTE07dg2ERBS for multiple nodes)
  <OPTIONS>:
    -h, --help                   This help
    -v, --version                The script version

  The script starts given nodes of given simulation.

EOF
}

version() {
cat << EOF

`basename $0` VERSION: $my_version

EOF
}

isStarted () {
    echo ".isstarted" | /netsim/inst/netsim_shell -sim $1 -ne $2 | grep NotStarted > /dev/null;
    echo $?;
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
         my_sim=$1
         param=$(($param + 1))
         ;;
        1 )
         my_nes=$1 
         param=$(($param + 1))
         ;;
        2 )
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

if [ "$my_sim" == "" ]; then
    echo
    echo Missing simulation
    usage
    exit 1
fi

if [ "$my_nes" == "" ]; then
    echo
    echo Missing nodes
    usage
    exit 1
fi

echo
echo "Executing netsim_shell commands ..."
echo

my_simnes_file=./allsimnes.txt
my_nes_file=./simnes.txt

echo ".show simnes" | /netsim/inst/netsim_shell -sim $my_sim > $my_simnes_file
grep $my_nes $my_simnes_file > $my_nes_file
my_num_of_files=`cat $my_nes_file | wc -l`
if [ $my_num_of_files -le 0 ]; then
    echo
    echo No match found for $my_nes in simulation $my_sim
    usage
    exit 1
fi
echo Number of nodes to be managed is $my_num_of_files

#Loop over the nodes
count=1
while [ ${count} -le ${my_num_of_files} ]
do
    my_node=`sed $count'q;d' $my_nes_file | awk '{print $1}'`
    is_started=$(isStarted $my_sim $my_node)
    if [ "$is_started" == "1" ]; then
        echo
        echo "Already started ${my_node} : skip it!"
    else
        echo "Executing netsim_shell command to start not yet started ${my_node} ..."
        echo ".start" | /netsim/inst/netsim_shell -sim $my_sim -ne $my_node
    fi

    count=$(( $count + 1 ))
done

rm -f $my_nes_file
rm -f $my_simnes_file

echo
echo Done.
echo

exit 0
