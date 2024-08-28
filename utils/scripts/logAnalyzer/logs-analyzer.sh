#!/bin/bash
my_version=1.0.1
my_command=$(basename $0)
MY_START_TIME="2000-01-01T00:00:00"
my_start_time=$MY_START_TIME
MY_END_TIME="3000-01-01T00:00:00"
my_end_time=$MY_END_TIME
my_out_dir="./output"
my_log_file=
MY_SERVICE_GROUPS_ARRAY=()
my_pattern=""

param_index=0

usage() {
cat << EOF

Usage: `basename $0` <LOGFILE> [<SERVICEGROUPS>...] [<OPTIONS>]
where:
  <LOGFILE>                                   The mandatory log file (.log .txt .csv .gz).
  <SERVICEGROUPS>                             The optional service groups.
  <OPTIONS>:
    -h, --help                                This help
    -v, --version                             The script version
    -p, --pattern=<PATTERN>                   The optional pattern (e.g. sshd, STARTED, ...)
    -s, --start-time=<STARTTIME>              The optional start time (included)
    -e, --end-time=<ENDTIME>                  The optional end time (excluded)
    -o, --out-dir=<OUTDIR>                    The optional output directory

Filter the log according to given filtering criteria (at least one shall be given), applied in following order:
- if SERVICEGROUPS given, only log lines related to such SGs are considered (elasticsearch only).
- if PATTERN given, only log lines containing such pattern are considered.
- if STARTTIME and/or ENDTIME given, only log lines in the given range [STARTTIME, ENDTIME) are considered.
  Note that an elasticsearch log only refers to a fixed date (format '^yyyy-mm-ddThh:mm:ss.<something>')
  so STARTTIME and ENDTIME can have time-only formats ('^hh:mm:ss' or '^hh:mm').
  Note that a generic log has lines starting with potentially different dates (format '^yyyy-mm-dd hh:mm:ss<something>')
  so STARTTIME and ENDTIME can have date-only formats ('^yyyy-mm-dd hh:mm:ss' or '^yyyy-mm-dd hh:mm' or '^yyyy-mm-dd').

EOF
}

error() {
cat << EOF

Usage: `basename $0` <LOGFILE> [<SERVICEGROUPS>...] [<OPTIONS>]
Try '`basename $0` --help' for more information.

EOF
}

version() {
cat << EOF

`basename $0` $my_version

EOF
}

get_extension() {
    extension="${1##*.}"
    echo $extension
}

get_basename() {
    extension=$(get_extension $1)
    bn=$(basename $1 .$extension)
    while [ "$extension" != "$bn" ] ; do
        extension=$(get_extension $bn)
        bn=$(basename $bn .$extension)
    done
    echo $bn
}

has_gz_extension() {
    extension=$(get_extension $1)
    if [ "$extension" != "gz" ]; then
        return 0
    else
        return 1
    fi
}

my_date_regexp="(([0-9]{2,4}-){2,2}[0-9]{2,2})"
my_time_regexp="((([0-9]{2,2}:){2,2}[0-9]{2,2})|([0-9]{2,2}:[0-9]{2,2})|([0-9]{2,2}))"
my_date_only_regexp="^"${my_date_regexp}"$"
my_time_only_regexp="^"${my_time_regexp}"$"
my_date_and_time_regexp="^"${my_date_regexp}"[T| ]"${my_time_regexp}"$"

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
            my_pattern=$1
            my_pattern=${my_pattern#--pattern=}
            my_pattern=${my_pattern#-p=}
            ;;
        -s=* | --start-time=* )
            my_start_time=$1
            my_start_time=${my_start_time#--start-time=}
            my_start_time=${my_start_time#-s=}
            ;;
        -e=* | --end-time=* )
            my_end_time=$1
            my_end_time=${my_end_time#--end-time=}
            my_end_time=${my_end_time#-e=}
            ;;
        -o=* | --out-dir=* )
            my_out_dir=$1
            my_out_dir=${my_out_dir#--out=}
            my_out_dir=${my_out_dir#-o=}
            ;;
        -* )
            echo
            echo $1: unknown option
            error
            exit 1
            ;;
        * )
            case $param_index in
                0 )
                    my_log_file=$1 
                    param_index=$(($param_index + 1))
                    ;;
                * )
                    my_param=$1 
                    MY_SERVICE_GROUPS_ARRAY+=($my_param)
                    param_index=$(($param_index + 1))
                    ;;
            esac
            ;;
    esac
    shift
done

if [ "$my_log_file" == "" ]; then
    echo
    echo Missing LOGFILE mandatory parameter
    error
    exit 1
fi

if [ ! -f $my_log_file ]; then
    echo
    echo $my_log_file : No such file or directory
    error
    exit 1
fi

my_log_file_base=$(get_basename $my_log_file)

my_in_log_file=${my_log_file}

has_gz_extension $my_in_log_file
my_has_gz_extension=$?
my_is_gz_file=$my_has_gz_extension

my_time_only_start_time=0
if [ "$my_start_time" != "$MY_START_TIME" ]; then
    if [[ $my_start_time =~ $my_date_and_time_regexp ]] ; then
        my_start_time=${my_start_time// /T}
    elif [[ $my_start_time =~ $my_date_only_regexp ]] ; then
        if [[ $my_has_gz_extension -eq 1 ]]; then
            echo
            echo $my_start_time : date-only format for STARTTIME not allowed for elasticsearch logs.
            error
            exit 1
        fi
        my_start_time=${my_start_time// /T}
    elif [[ $my_start_time =~ $my_time_only_regexp ]] ; then
        if [[ $my_has_gz_extension -eq 0 ]]; then
            echo
            echo $my_start_time : time-only format for STARTTIME not allowed for not elasticsearch logs.
            error
            exit 1
        fi
        my_start_time=${my_start_time// /T}
        my_time_only_start_time=1
    else
        echo
        echo $my_start_time : wrong format for STARTTIME
        error
        exit 1
    fi
fi

my_time_only_end_time=0
if [ "$my_end_time" != "$MY_END_TIME" ]; then
    if [[ $my_end_time =~ $my_date_and_time_regexp ]] ; then
        my_end_time=${my_end_time// /T}
    elif [[ $my_end_time =~ $my_date_only_regexp ]] ; then
        if [[ $my_has_gz_extension -eq 1 ]]; then
            echo
            echo $my_end_time : date-only format for ENDTIME not allowed for elasticsearch logs.
            error
            exit 1
        fi
        my_end_time=${my_end_time// /T}
    elif [[ $my_end_time =~ $my_time_only_regexp ]] ; then
        if [[ $my_has_gz_extension -eq 0 ]]; then
            echo
            echo $my_end_time : time-only format for ENDTIME not allowed for not elasticsearch logs.
            error
            exit 1
        fi
        my_end_time=${my_end_time// /T}
        my_time_only_end_time=1
    else
        echo
        echo $my_end_time : wrong format for ENDTIME
        error
        exit 1
    fi
fi

if [[ "$my_end_time" != "$MY_START_TIME" ]] && [[ "$my_end_time" != "$MY_END_TIME" ]] && [[ ${my_start_time} > ${my_end_time} ]]; then
    echo
    echo $my_start_time : start time is after end time $my_end_time
    error
    exit 1
fi

MY_NUM_SERVICE_GROUPS=${#MY_SERVICE_GROUPS_ARRAY[@]}
if [ $MY_NUM_SERVICE_GROUPS -le 0 ] && [ "$my_pattern" == "" ] && [ "$my_start_time" == "$MY_START_TIME" ] && [ "$my_end_time" == "$MY_END_TIME" ]; then
    echo
    echo No filtering criteria given: the output file would be the input one.
    error
    exit 1
fi

my_work_out_dir=${my_out_dir}/${my_log_file_base}
mkdir -p $my_work_out_dir
my_file_name=$my_log_file_base

# Service groups filter, if filtered file already present re-use it
if [ $MY_NUM_SERVICE_GROUPS -gt 0 ]; then
    my_file_name=${my_file_name}-SG
    my_counter=0
    my_egrep_param="'@@JBOSS@"
    while [ $my_counter -lt $MY_NUM_SERVICE_GROUPS ]; do
        my_service_group=${MY_SERVICE_GROUPS_ARRAY[$my_counter]}
        my_file_name=${my_file_name}_${my_service_group}
        my_egrep_param=${my_egrep_param}"|@svc-.*-${my_service_group}@JBOSS@"
        my_egrep_param=${my_egrep_param}"|@${my_service_group}@JBOSS@"
        my_counter=$(($my_counter + 1))
    done
    my_egrep_param=${my_egrep_param}"'"
    my_service_group_file=${my_work_out_dir}/${my_file_name}.txt
    if [ ! -f $my_service_group_file ]; then
        echo Filtering file ${my_in_log_file} on service groups ...
        if [[ $my_has_gz_extension -eq 0 ]]; then
            egrep ${my_egrep_param} ${my_in_log_file} > ${my_service_group_file}
        else
            zegrep ${my_egrep_param} ${my_in_log_file} > ${my_service_group_file}
        fi
        echo ... done.
    else
        echo Already filtered on service groups ...
    fi
    my_in_log_file=${my_service_group_file}
fi

# Pattern filter, if filtered file already present re-use it

has_gz_extension $my_in_log_file
my_has_gz_extension=$?

if [ "$my_pattern" != "" ]; then
    my_file_name=${my_file_name}-P_${my_pattern}
    my_pattern_file=${my_work_out_dir}/${my_file_name}.txt
    if [ ! -f $my_pattern_file ]; then
        echo Filtering file ${my_in_log_file} on pattern ${my_pattern} ...
        if [[ $my_has_gz_extension -eq 0 ]]; then
            grep ${my_pattern} ${my_in_log_file} > ${my_pattern_file}
        else
            zgrep ${my_pattern} ${my_in_log_file} > ${my_pattern_file}
        fi
        echo ... done.
    else
        echo Already filtered on pattern ${my_pattern}
    fi
    my_in_log_file=${my_pattern_file}
fi

# Time filter, if filtered file already present re-use it

has_gz_extension $my_in_log_file
my_has_gz_extension=$?

if [[ ${my_start_time} != "$MY_START_TIME" ]] || [[ ${my_end_time} != "$MY_END_TIME" ]]; then
    my_file_name=${my_file_name}-FROM_${my_start_time}
    my_file_name=${my_file_name}-TO_${my_end_time}
    my_file_name=${my_file_name//:/_}
    my_time_file=$my_work_out_dir/${my_file_name}.txt
    if [ ! -f $my_time_file ]; then
        echo Filtering file ${my_in_log_file} between ${my_start_time} and ${my_end_time} ...
        if [[ $my_has_gz_extension -eq 0 ]]; then
            if [[ $my_is_gz_file -eq 1 ]]; then
                cat $my_in_log_file | awk -v start=${my_start_time} \
                                          -v end=${my_end_time} \
                                          -v time_only_start=${my_time_only_start_time} \
                                          -v time_only_end=${my_time_only_end_time} \
                                          -v time_file=${my_time_file} \
                    'p=0;
                     $0 ~ /^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-2][0-9]:[0-5][0-9]:[0-5][0-9]/
                     {
                         FS="T";
                         curr_start=start;
                         curr_end=end;
                         if (time_only_start) { curr_start=$1"T"start }
                         if (time_only_end) { curr_end=$1"T"end }
                         if ($1"T"$2 >= curr_start) p=1;
                         if ($1"T"$2 >= curr_end) p=0;
                     }
                     p { print $0 > time_file }' > /dev/null
            else
                cat $my_in_log_file | awk -v start=${my_start_time} \
                                          -v end=${my_end_time} \
                                          -v time_file=${my_time_file} \
                    'p=0;
                     $0 ~ /^[0-9]{4}-[0-9]{2}-[0-9]{2} [0-2][0-9]:[0-5][0-9]:[0-5][0-9]/
                     {
                         FS=" ";
                         if ($1"T"$2 >= start) p=1;
                         if ($1"T"$2 >= end) p=0;
                     }
                     p { print $0 > time_file }' > /dev/null
            fi
        else
            zcat $my_in_log_file | awk -v start=${my_start_time} \
                                       -v end=${my_end_time} \
                                       -v time_only_start=${my_time_only_start_time} \
                                       -v time_only_end=${my_time_only_end_time} \
                                       -v time_file=${my_time_file} \
                                       -F'T' \
                'p=0;
                 $0 ~ /^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-2][0-9]:[0-5][0-9]:[0-5][0-9]/
                 {
                     curr_start=start;
                     curr_end=end;
                     if (time_only_start) { curr_start=$1"T"start }
                     if (time_only_end) { curr_end=$1"T"end }
                     if ($1"T"$2 >= curr_start) p=1;
                     if ($1"T"$2 >= curr_end) p=0;
                 }
                 p { print $0 > time_file }' > /dev/null
        fi
        echo ... done.
    else
        echo Already filtered between ${my_start_time} and ${my_end_time}
    fi
    my_in_log_file=${my_time_file}
fi

echo
echo "Filter results available in $my_work_out_dir"
echo
exit 0

