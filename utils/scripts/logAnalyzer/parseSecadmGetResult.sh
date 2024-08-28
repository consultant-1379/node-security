#!/bin/bash
my_version=1.0.1
my_command=$(basename $0)
my_out_dir="./output"
my_in_file=

param_index=0

usage() {
cat << EOF

Usage: `basename $0` <INFILE> [<OPTIONS>]
where:
  <INFILE>                                    The mandatory file containing the GET result.
  <OPTIONS>:
    -h, --help                                This help
    -v, --version                             The script version
    -o, --out-dir=<OUTDIR>                    The optional output directory. ${my_out_dir} as default

Parse the result of secadm certificate/trust get.

EOF
}

version() {
cat << EOF

`basename $0` $my_version

EOF
}

# Parse parameters

while [ "$1" != "" ] ; do
    case $1 in
        -h | --help )
            usage
            exit 0
            ;;
        -e | --examples )
            examples
            exit 0
            ;;
        -v | --version )
            version
            exit 0
            ;;
        -o=* | --out-dir=* )
            my_out_dir=$1
            my_out_dir=${my_out_dir#--out=}
            my_out_dir=${my_out_dir#-o=}
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
                    my_in_file=$1 
                    param_index=$(($param_index + 1))
                    ;;
                1 )
                   echo $1: unknown parameter
                   usage
                   exit 1
                   ;;
            esac
            ;;
    esac
    shift
done

if [ "$my_in_file" == "" ]; then
    echo
    echo Missing INFILE mandatory parameter
    usage
    exit 1
fi

if [ ! -f $my_in_file ]; then
    echo
    echo $my_in_file : No such file or directory
    usage
    exit 1
fi

my_in_file_base=$(basename $my_in_file .txt)
my_success_file=$my_out_dir/${my_in_file_base}_success.txt
my_failed_file=$my_out_dir/${my_in_file_base}_failed.txt
my_unsynch_file=$my_out_dir/${my_in_file_base}_unsynch.txt
mkdir -p $my_out_dir

grep IDLE $my_in_file | awk '{print $1}' > $my_success_file
grep -e "N/A                 N/A" $my_in_file | awk '{print $1}' > $my_failed_file
grep -e "The node specified is not synchronized" $my_in_file | awk '{print $1}' > $my_unsynch_file

sed -i "s/NetworkElement=//g" $my_success_file
sed -i "s/NetworkElement=//g" $my_failed_file
sed -i "s/NetworkElement=//g" $my_unsynch_file

my_num_success=`cat $my_success_file | wc -l`
my_num_failed=`cat $my_failed_file | wc -l`
my_num_unsynch=`cat $my_unsynch_file | wc -l`

echo
echo SUCCESS=$my_num_success
echo FAILED =$my_num_failed
echo UNSYNCH=$my_num_unsynch

# Clean all temp files

echo
echo "Parse results available in $my_out_dir directory"
echo Done.
exit 0

