#!/bin/bash
my_version=1.0.1
my_command=$(basename $0)
my_default_out_file="watchLogs"
my_out_file=${my_default_out_file}
my_grep_pattern=
my_sleep_time=60
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
    -p, --pattern=<PATTERN>                   The optional pattern (e.g. sshd, STARTED, ...)
    -s, --sleep=<SLEEP>                       The optional sleep time in seconds (${my_sleep_time} as default)
    -o, --out-file=<OUTFILE>                  The optional output file name (${my_default_out_file} as default)

Collect logs from /ericsson/3pp/jboss/standalone/log folders of specified service groups.
All VMs of given SG are involved (i.e. if secserv is specified, all svc-*-secserv VM are involved).
Watch for presence of given pattern in logs: if pattern found save the logs in a directory.
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
        -p=* | --pattern=* )
            my_grep_pattern=$1
            my_grep_pattern=${my_grep_pattern#--pattern=}
            my_grep_pattern=${my_grep_pattern#-p=}
            ;;
        -s=* | --sleep=* )
            my_sleep_time=$1
            my_sleep_time=${my_sleep_time#--sleep=}
            my_sleep_time=${my_sleep_time#-s=}
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
    exit 1
fi

if [ "$my_grep_pattern" == "" ]; then
    echo
    echo Missing GREP PATTERN mandatory parameter
    exit 1
else
    my_out_file=${my_out_file}_${my_grep_pattern}
fi

my_found_file=${my_out_file}"_Found"
if [ -f ${my_found_file}.tar.gz ]; then
    echo
    echo ${my_found_file}.tar.gz already exists!
    exit 1
else
    my_tmp_found_dir=./${my_found_file}
fi

my_tmp_out_dir=./${my_out_file}
rm -rf $my_tmp_out_dir
mkdir -p $my_tmp_out_dir

my_tmp_vms_list=$my_tmp_out_dir/tmp_vms.txt
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
            my_exclude_pattern="mscmce"
        fi
    else
        my_pattern="-${my_sg}"
        if [ "${my_sg}" == "mscm" ]; then
            my_exclude_pattern="mscmce"
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
    exit 1
fi

my_has_searched_pattern=0
while [ $my_has_searched_pattern -eq 0 ]
do
    my_counter=0
    while [ $my_counter -lt $MY_NUM_VMS ]; do
        my_vm=${MY_VMS_ARRAY[$my_counter]}
        echo Collecting logs for $my_vm
        my_vm_out_dir=$my_tmp_out_dir/logs/$my_vm
        mkdir -p $my_vm_out_dir
        scp -i /root/.ssh/vm_private_key cloud-user@${my_vm}:/ericsson/3pp/jboss/standalone/log/*.* $my_vm_out_dir
        my_counter=$(($my_counter + 1))
    done

    my_tmp_out_grep=$my_tmp_out_dir/tmp_grep_${my_grep_pattern}.txt
    my_tmp_out_found_date=$my_tmp_out_dir/found/tmp_found_date.txt
    grep -r ${my_grep_pattern} ${my_tmp_out_dir}/logs > ${my_tmp_out_grep}
    my_num_of_grep_lines=`cat $my_tmp_out_grep | wc -l`
    if [ $my_num_of_grep_lines -eq 0 ]; then
        my_has_searched_pattern=0
    else
        my_has_searched_pattern=1
    fi
    if [[ $my_has_searched_pattern -eq 1 ]]; then
        echo `date +"%D %T"`: pattern ${my_grep_pattern} FOUND! > $my_tmp_out_found_date
        echo `date +"%D %T"`: pattern ${my_grep_pattern} FOUND!
        mv $my_tmp_out_dir $my_tmp_found_dir
#        my_tmp_dbs_list=$my_tmp_found_dir/tmp_dbs.txt
#        cat $my_etc_host_file | grep -e "db-" >> $my_tmp_dbs_list
#        my_num_of_dbs=`cat# $my_tmp_dbs_list | wc -l`
#        if [ $my_num_of_dbs -le 0 ]; then
#            echo
#            echo No match found for requested DB in file $my_etc_host_file
#        else
#            while read line
#            do
#                my_curr_db_ip=`echo $line | awk '{print $1}'`
#                my_db=`echo $line | awk '{print $2}'`
#                echo Collecting OpenDj logs from $my_db
#                my_db_out_dir=$my_tmp_found_dir/$my_db
#                mkdir -p $my_db_out_dir
#                scp -r -i /root/.ssh/vm_private_key litp-admin@${my_db}:/opt/opendj/logs $my_db_out_dir
#                scp -r -i /root/.ssh/vm_private_key litp-admin@${my_db}:/var/log/opendj $my_db_out_dir
#            done < $my_tmp_dbs_list
#        fi
        tar -czvf ${my_found_file}.tar.gz ${my_tmp_found_dir}
        rm -rf $my_tmp_found_dir
    else
        echo `date +"%D_%T"`: pattern ${my_grep_pattern} not found!
        # Clean tmp files and dirs
        rm -f $my_tmp_vms_list
        rm -f $my_tmp_out_grep
        rm -rf $my_tmp_out_dir/logs
        echo Sleeping ${my_sleep_time} seconds ...
        sleep ${my_sleep_time}
        echo ... watching again.
    fi
done

echo
echo "Logs collected from VMs available in ${my_found_file}.tar.gz"
echo Done.
exit 0

