#!/bin/bash
my_version=1.0.0

MY_ITERATION=1000
MY_FILE="/tmp/BATCH_force_sync.txt"
my_counter=0
param_index=0

usage() {
cat << EOF   

Usage: `basename $0` <FILE> [<OPTIONS>]
where:
  <FILE>                                       The file containing the web cli command to be executed

  <OPTIONS>:
    -h, --help                                This help
    -v, --version                             The script version
    -i, --iteration=<INTEGER>                 The optional number of iteration to be executed. ${MY_ITERATION} as default.

This script may come in hand to execute several web cli commands from a file.
The script uses enmutils and cli_app scripts available on ms-1 server.
The script allows to execute such web cli commands for many iterations.

EOF
}
version() {
cat << EOF

`basename $0` $my_version

EOF
}

error() {
cat << EOF   

Try './${my_command} --help' for more information.

EOF
}
# Parse parameters

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
        -i=* | --iteration=* )
            MY_ITERATION=$1
            MY_ITERATION=${MY_ITERATION#--iteration=}
            MY_ITERATION=${MY_ITERATION#-i=}
            ;;
        -* )
            echo
            echo $1: unknown option
            error
            exit 1
            ;;
        * )
            case $param_index in
                * )
                    MY_FILE=$1
                    param_index=$(($param_index + 1))
                    ;;
            esac
            ;;
    esac
    shift
done

if [ ! -f "$MY_FILE" ]; then
        echo
        echo $MY_FILE : No such file or directory
        error
        exit 1
    fi

if [ $MY_ITERATION -le 0 ]; then
    echo
    echo Invalid iteration parameter $MY_ITERATION
    error
    exit 1
fi

while [ $my_counter -lt $MY_ITERATION ]; do
    while read -r line; do echo [$my_counter / $MY_ITERATION]; /opt/ericsson/enmutils/bin/cli_app "$line"; done <$MY_FILE
    my_counter=$(($my_counter + 1))
done
echo Completed.
