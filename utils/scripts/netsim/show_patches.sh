#!/bin/sh

my_version=1.0.0
my_patch=

usage() {
cat << EOF

Usage: `basename $0` [<OPTIONS>]
where:
  <OPTIONS>:
    -h, --help                          This help
    -v, --version                       The script version
    -p, --patch=<PATCH>                 The patch number

  The script lists the installed patches. If a specific patch is specified, check if it is
  already installed.

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
     -p=* | --patch=* )
       my_patch=$1
       my_patch=${my_patch#--patch=}
       my_patch=${my_patch#-p=}
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

echo
if [ "$my_patch" == "" ]; then
  echo ".show installation" | /netsim/inst/netsim_shell | grep ^P0
else
  echo ".show installation" | /netsim/inst/netsim_shell | grep ^P0 | grep ${my_patch}
  if [ $? != 0 ]; then
    echo "Patch" ${my_patch} "not yet installed."
  fi
fi

echo

exit 0
