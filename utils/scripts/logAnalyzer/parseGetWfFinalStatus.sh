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
  <INFILE>                                    The mandatory file containing the getwffinalstatus result.
  <OPTIONS>:
    -h, --help                                This help
    -v, --version                             The script version
    -o, --out-dir=<OUTDIR>                    The optional output directory. ${my_out_dir} as default

Parse the result of node-security/workflow/getwffinalstatus.

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

#mkdir -p $my_out_dir
my_in_file_base=$(basename $my_in_file .txt)
my_tmp_file=./${my_in_file_base}_tmp.txt
my_tmp_tmp_file=./${my_in_file_base}_tmp_tmp.txt

my_getwffinalstatus_invoke_count=`grep -e "RestResource getwffinalstatus invoked" $my_in_file | wc -l`
if [ $my_getwffinalstatus_invoke_count -le 0 ]; then
    echo
    echo $my_in_file : No getwffinalstatus result in file
    usage
    exit 1
fi

echo
echo getwffinalstatus invoke count = $my_getwffinalstatus_invoke_count
grep -v -e "Discarding not serialized context data" $my_in_file | grep -v -e "PuTTY log" > $my_tmp_file

grep -e " running workflows" $my_tmp_file | awk '{print $9}' | sed "s/\[//g;s/\]//g" > $my_tmp_tmp_file
my_getwffinalstatus_running_count=`cat $my_tmp_tmp_file`
echo getwffinalstatus running count= $my_getwffinalstatus_running_count
my_getwffinalstatus_success_count=`grep -e "state SUCCESS" $my_tmp_file | wc -l`
my_getwffinalstatus_failed_count=`grep -e "state FAILED" $my_tmp_file | wc -l`
echo getwffinalstatus success count= $my_getwffinalstatus_success_count
echo getwffinalstatus failed count = $my_getwffinalstatus_failed_count

# Clean all temp files
rm -f $my_tmp_file
rm -f $my_tmp_tmp_file

echo
#echo "Parse results available in $my_out_dir directory"
#echo Done.
exit 0

