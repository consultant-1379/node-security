#!/bin/sh

my_version=1.0.0
my_sim=
my_node=

usage() {
cat << EOF

Usage: `basename $0` <SIM> <NODE> [<OPTIONS>]
where:
  <SIM>                          The simulation name (e.g. LTEE163-V3x160-5K-FDD-LTE06)
  <NODE>                         The node (e.g. LTE06ERBS00001)
  <OPTIONS>:
    -h, --help                   This help
    -v, --version                The script version

  The script creates and configures few mandatory MOs for IPSEC certificate issue/reissue and
  IPSEC trust distribute:

    ManagedElement/IpSystem/IpSec = 1
    ManagedElement/EnodeBFunction = 1:SctpRef : the ref number of Sctp
    ManagedElement/IpSystem/IpAccessHostEt = 1 :IpInterfaceMoRef : the ref number of VpnInterface :ipAddress : 1.2.3.4 
    ManagedElement/IpSystem/IpAccessHostEt = 2 :ipAddress : 1.2.3.6 
    ManagedElement/IpSystem/IpAccessSctp = 1 :IpAccessHostEtRef1 : the ref number of IpAccessHotEt=1 
    ManagedElement/IpSystem/VpnInterface = 1 :ipAccessHostEtRef : the ref number of IpAccessHostEt=2
    ManagedElement/TransportNetwork/Sctp = 1 :ipAccessSctpRef : the ref number of IpAccessSctp

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
         my_node=$1 
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

if [ "$my_node" == "" ]; then
  echo
  echo Missing node
  usage
  exit 1
fi

echo
echo "Executing netsim_shell commands ..."
echo

is_started=$(isStarted $my_sim $my_node)

if [ "$is_started" == "1" ]; then

    /netsim/inst/netsim_shell -sim $my_sim -ne $my_node << EOF

createmo:parentid="ManagedElement=1,IpSystem=1", type="IpAccessHostEt", name="1";
createmo:parentid="ManagedElement=1,IpSystem=1", type="IpAccessHostEt", name="2";
createmo:parentid="ManagedElement=1,IpSystem=1", type="IpAccessSctp", name="1";
createmo:parentid="ManagedElement=1,IpSystem=1", type="VpnInterface", name="1";
createmo:parentid="ManagedElement=1,IpSystem=1", type="IpSec", name="1";
createmo:parentid="ManagedElement=1", type="ENodeBFunction", name="1";
createmo:parentid="ManagedElement=1,TransportNetwork=1", type="Sctp", name="1";

setmoattribute:mo="ManagedElement=1,ENodeBFunction=1",attributes="sctpRef (moref)=ManagedElement=1,TransportNetwork=1,Sctp=1";
setmoattribute:mo="ManagedElement=1,IpSystem=1,IpAccessHostEt=1",attributes="ipInterfaceMoRef (moref)=ManagedElement=1,IpSystem=1,VpnInterface=1 || ipAddress=1.2.3.4";
setmoattribute:mo="ManagedElement=1,IpSystem=1,IpAccessHostEt=2",attributes="ipAddress=1.2.3.6";
setmoattribute:mo="ManagedElement=1,IpSystem=1,IpAccessSctp=1",attributes="ipAccessHostEtRef1 (moref)=ManagedElement=1,IpSystem=1,IpAccessHostEt=1";
setmoattribute:mo="ManagedElement=1,IpSystem=1,VpnInterface=1",attributes="ipAccessHostEtRef (moref)=ManagedElement=1,IpSystem=1,IpAccessHostEt=2";
setmoattribute:mo="ManagedElement=1,TransportNetwork=1,Sctp=1",attributes="ipAccessSctpRef (moref)=ManagedElement=1,IpSystem=1,IpAccessSctp=1";

EOF

else

    /netsim/inst/netsim_shell -sim $my_sim -ne $my_node << EOF

.start
createmo:parentid="ManagedElement=1,IpSystem=1", type="IpAccessHostEt", name="1";
createmo:parentid="ManagedElement=1,IpSystem=1", type="IpAccessHostEt", name="2";
createmo:parentid="ManagedElement=1,IpSystem=1", type="IpAccessSctp", name="1";
createmo:parentid="ManagedElement=1,IpSystem=1", type="VpnInterface", name="1";
createmo:parentid="ManagedElement=1,IpSystem=1", type="IpSec", name="1";
createmo:parentid="ManagedElement=1", type="ENodeBFunction", name="1";
createmo:parentid="ManagedElement=1,TransportNetwork=1", type="Sctp", name="1";

setmoattribute:mo="ManagedElement=1,ENodeBFunction=1",attributes="sctpRef (moref)=ManagedElement=1,TransportNetwork=1,Sctp=1";
setmoattribute:mo="ManagedElement=1,IpSystem=1,IpAccessHostEt=1",attributes="ipInterfaceMoRef (moref)=ManagedElement=1,IpSystem=1,VpnInterface=1 || ipAddress=1.2.3.4";
setmoattribute:mo="ManagedElement=1,IpSystem=1,IpAccessHostEt=2",attributes="ipAddress=1.2.3.6";
setmoattribute:mo="ManagedElement=1,IpSystem=1,IpAccessSctp=1",attributes="ipAccessHostEtRef1 (moref)=ManagedElement=1,IpSystem=1,IpAccessHostEt=1";
setmoattribute:mo="ManagedElement=1,IpSystem=1,VpnInterface=1",attributes="ipAccessHostEtRef (moref)=ManagedElement=1,IpSystem=1,IpAccessHostEt=2";
setmoattribute:mo="ManagedElement=1,TransportNetwork=1,Sctp=1",attributes="ipAccessSctpRef (moref)=ManagedElement=1,IpSystem=1,IpAccessSctp=1";
.stop

EOF

fi

echo
echo "Done."
echo
exit 0
