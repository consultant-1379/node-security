#!/bin/bash
my_version=1.0.1
my_command=$(basename $0)
my_default_out_file="allLogs"
my_out_file=${my_default_out_file}
MY_SGS_ARRAY=()
MY_VMS_ARRAY=()

param_index=0

usage() {
cat << EOF   

Usage: `basename $0` <SGS>... [<OPTIONS>]
where:
  <SGS>                                       The service groups (e.g. secserv sps pkiraserv cmserv) or
                                              the specific VMs (e.g. svc-1-secserv svc-5-sps)
  <OPTIONS>:
    -h, --help                                This help
    -v, --version                             The script version
    -e, --examples                            Some examples
    -o, --out-file=<OUTFILE>                  The optional output file name (${my_default_out_file} as default)

Collect logs from /ericsson/3pp/jboss/standalone/log folders of specified service groups.
All VMs of given SG are involved (i.e. if secserv is specified, all svc-*-secserv VM are involved).
Copy this script on the ms-1 of your server/vApp and execute it.

EOF
}

examples() {
cat << EOF   

Examples:

./${my_command} secserv -o=TEST_001
./${my_command} secserv sps pkiraserv -o=TEST_002
./${my_command} svc-2-secserv sps -o=TEST_003
./${my_command} svc-1-secserv svc-1-sps -o=TEST_004

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
        -o=* | --out-file=* )
            my_out_file=$1
            my_out_file=${my_out_file#--out-file=}
            my_out_file=${my_out_file#-o=}
            ;;
        -* )
            echo
            echo $1: unknown option
            usage
            exit 1
            ;;
        * )
            case $param_index in
                * )
                    my_sg=$1 
                    MY_SGS_ARRAY+=($my_sg)
                    param_index=$(($param_index + 1))
                    ;;
            esac
            ;;
    esac
    shift
done

MY_NUM_SGS=${#MY_SGS_ARRAY[@]}
if [ $MY_NUM_SGS -le 0 ]; then
    echo
    echo Missing SGS mandatory parameter
    usage
    exit 1
fi

my_tmp_out_dir=./${my_out_file}
mkdir -p $my_tmp_out_dir
my_tmp_vms_list=./tmp_vms.txt

rm -f $my_tmp_vms_list

my_svc_regexp="svc-([1-9])-([a-z]*)"
my_counter=0
my_etc_host_file=/etc/hosts
while [ $my_counter -lt $MY_NUM_SGS ]; do
    my_sg=${MY_SGS_ARRAY[$my_counter]}
    my_pattern=""
    my_exclude_pattern=""
    if [[ $my_sg =~ $my_svc_regexp ]]; then
        my_pattern=$my_sg
        if [ "${BASH_REMATCH[2]}" == "mscm" ]; then
            my_exclude_pattern="mscmce\|mscmip"
        fi
    else
        my_pattern="-${my_sg}"
        if [ "${my_sg}" == "mscm" ]; then
            my_exclude_pattern="mscmce\|mscmip"
        fi
    fi
    if [ "${my_exclude_pattern}" != "" ]; then
        cat $my_etc_host_file | grep -e "${my_pattern}" | grep -v -e "${my_exclude_pattern}" >> $my_tmp_vms_list
    else
        cat $my_etc_host_file | grep -e "${my_pattern}" >> $my_tmp_vms_list
    fi
    my_counter=$(($my_counter + 1))
done

my_num_of_vms=`cat $my_tmp_vms_list | wc -l`
if [ $my_num_of_vms -le 0 ]; then
    echo
    echo No match found for requested SGS in file $my_etc_host_file
    rm -f $my_tmp_vms_list
    usage
    exit 1
fi

while read line
do
    my_curr_vm_ip=`echo $line | awk '{print $1}'`
    my_curr_vm_name=`echo $line | awk '{print $2}'`
    MY_VMS_ARRAY+=($my_curr_vm_name)
done < $my_tmp_vms_list

MY_NUM_VMS=${#MY_VMS_ARRAY[@]}
if [ $MY_NUM_VMS -le 0 ]; then
    echo
    echo No valid VM found
    rm -f $my_tmp_vms_list
    usage
    exit 1
fi

my_counter=0
while [ $my_counter -lt $MY_NUM_VMS ]; do
    my_vm=${MY_VMS_ARRAY[$my_counter]}
    echo Collecting logs for $my_vm
    my_vm_out_dir=$my_tmp_out_dir/$my_vm
    mkdir -p $my_vm_out_dir
    scp -i /root/.ssh/vm_private_key cloud-user@${my_vm}:/ericsson/3pp/jboss/standalone/log/*.* $my_vm_out_dir
    my_counter=$(($my_counter + 1))
done

tar -czvf ${my_out_file}.tar.gz ${my_tmp_out_dir}

# Clean tmp files and dirs
rm -f $my_tmp_vms_list
rm -rf $my_tmp_out_dir

echo
echo "Logs collected from VMs available in ${my_out_file}.tar.gz"
echo Done.
exit 0
