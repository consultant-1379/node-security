#!/bin/bash
my_version=1.0.1
my_command=$(basename $0)
my_pattern=
my_string=

param_index=0

usage() {
cat << EOF

Usage: `basename $0` <PATTERN> <STRING> [<OPTIONS>]
where:
  <PATTERN>                                   The mandatory pattern regular expression.
  <STRING>                                    The mandatory string to be checked against the given pattern.
  <OPTIONS>:
    -h, --help                                This help
    -v, --version                             The script version
    -e, --examples                            Some examples

Check the given STRING against the given PATTERN (regular expression).

EOF
}

version() {
cat << EOF

`basename $0` $my_version

EOF
}

examples() {
cat << EOF   

Examples:

./${my_command} 'aa(b{2,3}[xyz])cc' aabbxcc
./${my_command} 'aa(b{2,3}[xyz])cc' aabbcc
./${my_command} '(([0-9]*\-){2,2}[0-9]*) (([0-9]*\:){2,2}[0-9]*,[0-9]*)' '2016-04-07 19:02:47,164'
./${my_command} '\[([0-9,a-z,A-Z,\.]*)\]' '[com.ericsson.nms.security.nscs.WorkflowRestResource]'
./${my_command} '\[(([0-9a-zA-Z]*\.)*([0-9a-zA-Z]*))\]' '[com.ericsson.nms.security.nscs.WorkflowRestResource]'

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
        -e | --examples )
            examples
            exit 0
            ;;
        -* )
            echo
            echo $1: unknown option
            usage
            exit 1
            ;;
        * )
            case $param_index in
                0 )
                    my_pattern=$1 
                    param_index=$(($param_index + 1))
                    ;;
                1 )
                    my_string=$1 
                    param_index=$(($param_index + 1))
                    ;;
                * )
                   echo $1: unknown parameter
                   usage
                   exit 1
                   ;;
            esac
            ;;
    esac
    shift
done

if [ "$my_pattern" == "" ]; then
    echo
    echo Missing PATTERN mandatory parameter
    usage
    exit 1
fi

if [ "$my_string" == "" ]; then
    echo
    echo Missing STRING mandatory parameter
    usage
    exit 1
fi

echo
echo "regex : $my_pattern"
echo "string: $my_string"

if [[ $my_string =~ $my_pattern ]]; then
    echo
    echo "$my_string matches"
    i=1
    n=${#BASH_REMATCH[*]}
    while [[ $i -lt $n ]]
    do
        echo "  matchGroup[$i]: ${BASH_REMATCH[$i]}"
        let i++
    done
else
    echo
    echo "$my_string does not match"
fi

echo
echo Done.
exit 0

