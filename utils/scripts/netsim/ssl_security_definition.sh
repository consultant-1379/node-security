#!/bin/sh

my_version=1.0.0
my_sim=
my_name=
my_dir=

usage() {
cat << EOF

Usage: `basename $0` <SIM> [<OPTIONS>]
where:
  <SIM>                          The simulation name (e.g. MSRBS-V2_15B-V13_R28C or LTE16A-V17x160-5K-DG2-FDD-LTE07)
  <OPTIONS>:
    -h, --help                   This help
    -v, --version                The script version
    -n, --name=<NAME>            The mandatory SSL Security Definition name
    -d, --dir=<DIR>              The mandatory directory containing the certificates and keys for SSL Security Definition

  The script defines an SSL Security Definition for the specified simulation.

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
     -n=* | --name=* )
       my_name=$1
       my_name=${my_name#--name=}
       my_name=${my_name#-n=}
       ;;
     -d=* | --dir=* )
       my_dir=$1
       my_dir=${my_dir#--dir=}
       my_dir=${my_dir#-d=}
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
  echo Missing simulation mandatory parameter
  usage
  exit 1
fi

if [ "$my_name" == "" ]; then
    echo
    echo Missing SSL Security Definition name mandatory parameter
    usage
    exit 1
fi

if [ "$my_dir" == "" ]; then
    echo
    echo Missing SSL Security Definition directory mandatory parameter
    usage
    exit 1
fi

echo
echo "Executing netsim_shell commands ..."
echo
/netsim/inst/netsim_shell -sim $my_sim << EOF

.select configuration
.setssliop createormodify $my_name
.setssliop description no_value
.setssliop clientverify 0
.setssliop clientdepth 1
.setssliop serververify 0
.setssliop serverdepth 1
.setssliop protocol_version sslv2|sslv3|tlsv1
.setssliop clientcertfile $my_dir/certificate1.crt
.setssliop clientcacertfile $my_dir/certificate1.crt
.setssliop clientkeyfile $my_dir/privateKey1.key
.setssliop clientpassword ""
.setssliop servercertfile $my_dir/certificate1.crt
.setssliop servercacertfile $my_dir/certificate1.crt
.setssliop serverkeyfile $my_dir/privateKey1.key
.setssliop serverpassword ""
.setssliop save force

EOF

echo Done.

exit 0
