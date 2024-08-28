#!/bin/bash

# *-----------------------------------------------------------------------------
# *******************************************************************************
# * COPYRIGHT Ericsson 2021
# *
# * The copyright to the computer program(s) herein is the property of
# * Ericsson Inc. The programs may be used and/or copied only with written
# * permission from Ericsson Inc. or in accordance with the terms and
# * conditions stipulated in the agreement/contract under which the
# * program(s) have been supplied.
# *******************************************************************************
# *----------------------------------------------------------------------------*/

###############################################################################
###############################################################################
#
# Global constants
#
###############################################################################
###############################################################################

###############################################################################
#
# ENM size
#
###############################################################################
ENM_SIZE_XL="XL"
ENM_SIZE_S="S"
ENM_SIZE_XS="XS"

###############################################################################
#
# Commands
#
###############################################################################
CERTIFICATE_ISSUE="CERTIFICATE_ISSUE"
CERTIFICATE_REISSUE="CERTIFICATE_REISSUE"
CERTIFICATE_GET="CERTIFICATE_GET"
TRUST_DISTRIBUTE="TRUST_DISTRIBUTE"
TRUST_REMOVE="TRUST_REMOVE"
TRUST_GET="TRUST_GET"

###############################################################################
#
# Commands KPI
#
###############################################################################
declare -A max_batch_size_kpi
declare -A max_duration_kpi
declare -A max_error_percentage_kpi

declare -A max_batch_size_kpi_xl=([$CERTIFICATE_ISSUE]=1500 [$CERTIFICATE_REISSUE]=1500 [$CERTIFICATE_GET]=200
                                  [$TRUST_DISTRIBUTE]=1200 [$TRUST_REMOVE]=-1 [$TRUST_GET]=200)
declare -A max_duration_kpi_xl=([$CERTIFICATE_ISSUE]=40 [$CERTIFICATE_REISSUE]=40 [$CERTIFICATE_GET]=-1
                                [$TRUST_DISTRIBUTE]=25 [$TRUST_REMOVE]=-1 [$TRUST_GET]=-1)
declare -A max_error_percentage_kpi_xl=([$CERTIFICATE_ISSUE]=-1 [$CERTIFICATE_REISSUE]=-1 [$CERTIFICATE_GET]=-1
                                        [$TRUST_DISTRIBUTE]=-1 [$TRUST_REMOVE]=-1 [$TRUST_GET]=-1)

declare -A max_batch_size_kpi_s=([$CERTIFICATE_ISSUE]=100 [$CERTIFICATE_REISSUE]=100 [$CERTIFICATE_GET]=200
                                  [$TRUST_DISTRIBUTE]=100 [$TRUST_REMOVE]=-1 [$TRUST_GET]=200)
declare -A max_duration_kpi_s=([$CERTIFICATE_ISSUE]=40 [$CERTIFICATE_REISSUE]=40 [$CERTIFICATE_GET]=-1
                                [$TRUST_DISTRIBUTE]=30 [$TRUST_REMOVE]=-1 [$TRUST_GET]=-1)
declare -A max_error_percentage_kpi_s=([$CERTIFICATE_ISSUE]=-1 [$CERTIFICATE_REISSUE]=-1 [$CERTIFICATE_GET]=-1
                                        [$TRUST_DISTRIBUTE]=-1 [$TRUST_REMOVE]=-1 [$TRUST_GET]=-1)

declare -A max_batch_size_kpi_xs=([$CERTIFICATE_ISSUE]=50 [$CERTIFICATE_REISSUE]=50 [$CERTIFICATE_GET]=200
                                  [$TRUST_DISTRIBUTE]=50 [$TRUST_REMOVE]=-1 [$TRUST_GET]=200)
declare -A max_duration_kpi_xs=([$CERTIFICATE_ISSUE]=40 [$CERTIFICATE_REISSUE]=40 [$CERTIFICATE_GET]=-1
                                [$TRUST_DISTRIBUTE]=-1 [$TRUST_REMOVE]=-1 [$TRUST_GET]=-1)
declare -A max_error_percentage_kpi_xs=([$CERTIFICATE_ISSUE]=-1 [$CERTIFICATE_REISSUE]=-1 [$CERTIFICATE_GET]=-1
                                        [$TRUST_DISTRIBUTE]=-1 [$TRUST_REMOVE]=-1 [$TRUST_GET]=-1)

###############################################################################
#
# Workload and iteration states
#
###############################################################################
SUCCESS="SUCCESS"
ERROR="ERROR"
FAILED="FAILED"

###############################################################################
#
# Step states
#
###############################################################################
STEP_PASSED="PASSED"
STEP_PASSED_WITH_ERROR="ERROR"
STEP_NOT_PASSED="NOT_PASSED"
STEP_FAILED="FAILED"

###############################################################################
#
# Command error codes
#
###############################################################################
COMMAND_FINISHED_WITH_SUCCESS=0
COMMAND_FAILED=1
COMMAND_FINISHED_WITH_ERROR=2
COMMAND_NOT_YET_FINISHED=3
COMMAND_PASSED=4
COMMAND_PASSED_WITH_ERROR=5
COMMAND_NOT_PASSED=6

###############################################################################
###############################################################################
#
# Global variables
#
###############################################################################
###############################################################################
SECONDS=0

version=1.0.0
versions_history="Current version :
${version}

Versions history:
1.0.0      initial revision
"

script_basename=$(basename "$0")
script_basename_no_ext=${script_basename%.*}

enm_credentials_file=/tmp/enmutils/enm-credentials

num_iterations=1
period=30
nes=
nes_regexp="^(.+)=([0-9]+)(\+(.+)=([0-9]+))*$"
enm_size=$ENM_SIZE_XL
job_period=30
config_file="./${script_basename_no_ext}.cnf"
output_dir="."
verbose="false"

usage_slogan="
Usage: ${script_basename} [-h] [-v] [-V] -n NODES
                        [-s ENMSIZE] [-i ITERATIONS]
                        [-p PERIOD] [-j JOBPERIOD]
                        [-f CONFIGFILE] [-o OUTDIR]"

usage="${usage_slogan}

where:
    -h  show this help
    -v  show the script version
    -n  set the nodes in terms of <ne_type> and <num_nodes> (mandatory)
    -s  set the ENM size (default: ${enm_size})
    -i  set the number of iterations (default: ${num_iterations})
    -p  set the period (in m) for each iteration (default: ${period})
    -j  set the period (in s) for get job command (default: ${job_period})
    -f  set the workload configuration file (default: ${config_file})
    -o  set the output directory containing log and report (default: ${output_dir})
    -V  enable verbose mode

${script_basename} is a program to run Node Security workload.

The sequence of steps specified in the given configuration file is iterated for
the given number of iterations. Each step shall finish before executing the next
step. If a step fails (not for workflow failure) the script exits immediately.
For a single iteration a period can be set (0 means no period): when all steps
of an iteration have been completed, the script possibly waits until the given
period is reached before starting next iteration.

The nodes are specified in terms of <ne_type> and <num_nodes> with format:
<ne_type>=<num_nodes>[+<ne_type>=<num_nodes>]
The nodefile and xmlfile containing the involved nodes of required NE type are
(re)created at any script execution to use SYNCHRONIZED nodes for each runs.

Each line (step) in the configuration file has the following format:
<iteration>,<command>,<cert_trust_type>
where:
    <iteration>:       iteration in which the step shall be executed ('*' for
                       any iteration).
    <command>:         the command type of the step:
                       i   certificate issue
                       I   certificate reissue
                       t   trust distribute
                       e   trust distribute ENM_E-mail_CA
                       r   trust remove ENM_E-mail_CA
    <cert_trust_type>: the certificate/trust type of the command:
                       OAM     certificate/trust type
                       IPSEC   certificate/trust type
                       LAAD    trust type
Each line starting with '#' is treated as a comment and skipped.

Examples of configuration file:
1,t,OAM
*,i,OAM

The KPI are checked according to the specified ENM size.
Allowed sizes:
- XL
- S
- XS
"

short_usage="${usage_slogan}
Try '${script_basename} -h' for more information.
"

cli_app=/opt/ericsson/enmutils/bin/cli_app
xmlfile=
nodefile=
total_num_nodes=0
# Reduced nodefile for secadm trust get command
getnodefile=
max_num_nodes_in_get=200
total_num_nodes_in_get=0

stringified_json_report=
reportfile=
last_command_with_job_error=0
last_job_duration=0
last_job_wfs=0
last_job_error_wfs=0

###############################################################################
###############################################################################
#
# Functions
#
###############################################################################
###############################################################################

###############################################################################
#
# Login Functions
#
###############################################################################

###############################################################################
#
# Action :
#  login
#  Login is needed to use cli_app
# Globals :
#   enm_credentials_file: the ENM credentials file.
# Arguments:
#   None.
# Returns:
#   None.
#
###############################################################################
login() {
    local is_login_successful="false"
    local max_num_failed_login_attempts=3
    local num_failed_login_attempts=0
    while [ "$is_login_successful" != "true" ]
    do
        if [ ! -f $enm_credentials_file ]; then
            echo -n "Username: "
            read user
            echo -n "Password: "
            read -s passwd
            echo
            printf "%s\n%s\n" ${user} ${passwd} > $enm_credentials_file
            local secadm_test_response=`$cli_app "secadm test"`
            if [ "$secadm_test_response" == "Test Command OK." ]; then
                is_login_successful="true"
            else
                rm -f $enm_credentials_file
                num_failed_login_attempts=$[num_failed_login_attempts + 1]
                if [ $num_failed_login_attempts -lt $max_num_failed_login_attempts ]; then
                    echo "Permission denied, please try again."
                else
                    echo "Permission denied, max num of login attempts exceeded."
                    exit 1
                fi
            fi
        else
            is_login_successful="true"
        fi
    done
}

###############################################################################
#
# Log Functions
#
###############################################################################

###############################################################################
#
# Action :
#  debug
#  Log a message at DEBUG level if verbose mode is enabled.
# Globals :
#   verbose: the verbose mode flag.
#   logfile: the log file.
# Arguments:
#   msg: the message to log.
# Returns:
#   None.
#
###############################################################################
debug() {
    if [ "$verbose" == "true" ]; then
        local date=$(date '+%Y-%m-%dT%H:%M:%S')
        printf "%s DEBUG %s\n" ${date} "$@" >> $logfile
    fi
}

###############################################################################
#
# Action :
#  info
#  Log a message at INFO level.
# Globals :
#   logfile: the log file.
# Arguments:
#   msg: the message to log.
# Returns:
#   None.
#
###############################################################################
info() {
    local date=$(date '+%Y-%m-%dT%H:%M:%S')
    printf "%s INFO  %s\n" ${date} "$@" >> $logfile
}

###############################################################################
#
# Action :
#  info_with_date
#  Log a message at INFO level and return the date.
# Globals :
#   logfile: the log file.
# Arguments:
#   msg: the message to log.
# Returns:
#   the date.
#
###############################################################################
info_with_date() {
    local date=$(date '+%Y-%m-%dT%H:%M:%S')
    printf "%s INFO  %s\n" ${date} "$@" >> $logfile
    printf "%s" ${date}
}

###############################################################################
#
# Action :
#  error
#  Log a message at ERROR level.
# Globals :
#   logfile: the log file.
# Arguments:
#   msg: the message to log.
# Returns:
#   None.
#
###############################################################################
error() {
    local date=$(date '+%Y-%m-%dT%H:%M:%S')
    printf "%s ERROR %s\n" ${date} "$@" >> $logfile
}

###############################################################################
#
# Action :
#  error_on_exit
#  Log a message at ERROR level causing the exit.
# Globals :
#   logfile: the log file.
# Arguments:
#   msg: the message to log.
# Returns:
#   None.
#
###############################################################################
error_on_exit() {
    error "Exiting workload on error [$@]"
    printf "%s\n" "$@" >&2
}

###############################################################################
#
# Action :
#  error_with_date
#  Log a message at ERROR level and return the date.
# Globals :
#   logfile: the log file.
# Arguments:
#   msg: the message to log.
# Returns:
#   the date.
#
###############################################################################
error_with_date() {
    local date=$(date '+%Y-%m-%dT%H:%M:%S')
    printf "%s ERROR %s\n" ${date} "$@" >> $logfile
    printf "%s" ${date}
}

###############################################################################
#
# Action :
#  warn
#  Log a message at WARNING level.
# Globals :
#   logfile: the log file.
# Arguments:
#   msg: the message to log.
# Returns:
#   None.
#
###############################################################################
warn() {
    local date=$(date '+%Y-%m-%dT%H:%M:%S')
    printf "%s WARN  %s\n" ${date} "$@" >> $logfile
}

###############################################################################
#
# Date and Time Functions
#
###############################################################################

###############################################################################
#
# Action :
#  seconds_to_date
#  Convert the number of seconds in UTC date and time.
# Globals :
#   None.
# Arguments:
#   num_of_seconds: the number of seconds.
# Returns:
#   the date and time with format "%Y-%m-%dT%T".
#
###############################################################################
seconds_to_date() {
    local num_of_seconds=$1
    printf '%s' `date -u -d @"$num_of_seconds" +"%Y-%m-%dT%T"`
}

###############################################################################
#
# Action :
#  seconds_to_time
#  Convert the number of seconds in time.
# Globals :
#   None.
# Arguments:
#   num_of_seconds: the number of seconds.
# Returns:
#   the time.
#
###############################################################################
seconds_to_time() {
    local num_of_seconds=$1
    local days=$((num_of_seconds/60/60/24))
    local hours=$((num_of_seconds/60/60%24))
    local minutes=$((num_of_seconds/60%60))
    local seconds=$((num_of_seconds%60))

    if [[ ${days} != 0 ]]; then
        printf '%d days %02d:%02d:%02d' $days $hours $minutes $seconds
    else
        printf '%02d:%02d:%02d' $hours $minutes $seconds
    fi
}

###############################################################################
#
# Action :
#  date_to_seconds
#  Convert the date in number of seconds.
# Globals :
#   None.
# Arguments:
#   d: the date.
# Returns:
#   the number of seconds.
#
###############################################################################
date_to_seconds() {
    local d=$1
    local num_of_seconds=$(date -d "$d" +%s)
    printf '%d' $num_of_seconds
}

###############################################################################
#
# Action :
#  date_to_tag
#  Convert the date in a tag used in script filenames.
# Globals :
#   None.
# Arguments:
#   d: the date.
# Returns:
#   the tag.
#
###############################################################################
date_to_tag() {
    local d=$1
    local tag=$(date -d "$d" +"%m-%d-%Y_%H-%M-%S")
    printf '%s' $tag
}

###############################################################################
#
# Action :
#  get_duration
#  Get the duration in seconds between two dates.
# Globals :
#   None.
# Arguments:
#   start_date: the start date.
#   end_date: the end date.
# Returns:
#   the duration in seconds.
#
###############################################################################
get_duration() {
    local start_date=$1
    debug "get_duration : start date [$start_date]"
    local end_date=$2
    debug "get_duration : end date [$end_date]"
    local start_date_in_seconds=$(date_to_seconds "${start_date}")
    local end_date_in_seconds=$(date_to_seconds "${end_date}")
    local duration=$(($end_date_in_seconds - $start_date_in_seconds))
    printf '%d' $duration
}

###############################################################################
#
# Report Functions
#
###############################################################################

###############################################################################
#
# Action :
#  workload_started
#  Log and add to stringified report a workload started.
#  Define the JSON report file name and if the file exists, remove it.
# Globals :
#   num_iterations: the number of iterations.
#   period: the period in minutes of the iterations.
#   job_period: the period (in s) for the get job command.
#   nes: the list of nodes in terms of <ne_type> and <num_nodes>.
#   config_file: the configuration file.
#   stringified_json_report: the stringified JSON report.
#   output_dir: the output directory.
#   reportfile: the report file.
# Arguments:
#   tag: the workload tag.
# Returns:
#   None.
#
###############################################################################
workload_started() {
    tag=$1
    local date=$(info_with_date "Workload started : num_iterations [${num_iterations}] nes [$nes] period (in m) [${period}] job period (in s) [${job_period}] config_file [$config_file]")
    stringified_json_report+="{\"workloadStart\": \"$date\", \"numIterations\": \"$num_iterations\", \"nes\": \"$nes\", \"periodInMin\": \"$period\", \"jobPeriodInSec\": \"$job_period\", \"configFile\": \"$config_file\""
    reportfile=${output_dir}/report_nscs_workload.json
    if [ -f $reportfile ]; then
        debug "Removing already existent reportfile [${reportfile}]"
        rm -f $reportfile
    else
        debug "Not yet existent reportfile [${reportfile}]"
    fi
}

###############################################################################
#
# Action :
#  workload_created_nodes_files
#  Log and add to stringified report the successful creation of nodes files.
# Globals :
#   xmlfile: the XML file used in command with --xmlfile option.
#   nodefile: the text file used in command with --nodefile option.
#   getnodefile: nodefile in trust get command with --nodefile option.
#   max_num_nodes_in_get: max nodes for trust get command.
#   total_num_nodes_in_get: num of nodes for trust get command.
#   total_num_nodes: the total number of involved SYNCHRONIZED nodes.
#   stringified_json_report: the stringified JSON report.
# Arguments:
#   None.
# Returns:
#   None.
#
###############################################################################
workload_created_nodes_files() {
    info "Added [${total_num_nodes}] nodes in xmlfile [${xmlfile}] and nodefile [${nodefile}]"
    info "Added [${total_num_nodes_in_get}] nodes in getnodefile [${getnodefile}]"
    #stringified_json_report+=", \"xmlfile\": \"$xmlfile\", \"nodefile\": \"$nodefile\", \"getnodefile\": \"$getnodefile\", \"totalNumNodes\": \"$total_num_nodes\", \"totalNumNodesInGet\": \"$total_num_nodes_in_get\""
    stringified_json_report+=", \"totalNumNodes\": \"$total_num_nodes\", \"totalNumNodesInGet\": \"$total_num_nodes_in_get\""
}

###############################################################################
#
# Action :
#  workload_finished_with_success
#  Log and add to stringified report a workload finished with success.
#  Write the stringified report in JSON report file.
# Globals :
#   stringified_json_report: the stringified JSON report.
#   reportfile: the report file.
# Arguments:
#   None.
# Returns:
#   None.
#
###############################################################################
workload_finished_with_success() {
    local date=$(info_with_date "Workload finished with success")
    stringified_json_report+=", \"workloadStatus\": \"$SUCCESS\", \"workloadEnd\": \"$date\"}"
    python -m json.tool <<< $stringified_json_report > $reportfile
}

###############################################################################
#
# Action :
#  workload_finished_with_error
#  Log and add to stringified report a workload finished with error.
#  Write the stringified report in JSON report file.
# Globals :
#   stringified_json_report: the stringified JSON report.
#   reportfile: the report file.
# Arguments:
#   error_msg: the error message.
# Returns:
#   None.
#
###############################################################################
workload_finished_with_error() {
    local error_msg=$@
    local date=$(error_with_date "Workload finished with error : ${error_msg}")
    stringified_json_report+=", \"workloadStatus\": \"$ERROR\", \"workloadError\": \"$error_msg\", \"workloadEnd\": \"$date\"}"
    python -m json.tool <<< $stringified_json_report > $reportfile
}

###############################################################################
#
# Action :
#  workload_failed
#  Log and add to stringified report a workload failed.
#  Write the stringified report in JSON report file.
# Globals :
#   stringified_json_report: the stringified JSON report.
#   reportfile: the report file.
# Arguments:
#   error_msg: the error message.
# Returns:
#   None.
#
###############################################################################
workload_failed() {
    local error_msg=$@
    local date=$(error_with_date "Workload failed : ${error_msg}")
    stringified_json_report+=", \"workloadStatus\": \"$FAILED\", \"workloadError\": \"$error_msg\", \"workloadEnd\": \"$date\"}"
    python -m json.tool <<< $stringified_json_report > $reportfile
}

###############################################################################
#
# Action :
#  iteration_started
#  Log and add to stringified report an iteration started.
# Globals :
#   num_iterations: the number of iterations.
#   stringified_json_report: the stringified JSON report.
# Arguments:
#   iteration: the current iteration.
# Returns:
#   None.
#
###############################################################################
iteration_started() {
    local iteration=$1
    local date=$(info_with_date "Iteration [$iteration/$num_iterations] started")
    if [ $iteration -eq 1 ]; then
        stringified_json_report+=", \"workload_iterations\": ["
    else
        stringified_json_report+=","
    fi
    stringified_json_report+="{\"iteration\": \"$iteration/$num_iterations\", \"iterationStart\": \"$date\", \"iteration_steps\": ["
}

###############################################################################
#
# Action :
#  iteration_finished_with_success
#  Log and add to stringified report an iteration finished with success.
# Globals :
#   num_iterations: the number of iterations.
#   stringified_json_report: the stringified JSON report.
# Arguments:
#   iteration: the current iteration.
#   duration: the duration (in seconds) of the iteration.
# Returns:
#   None.
#
###############################################################################
iteration_finished_with_success() {
    local iteration=$1
    local duration=$2
    local duration_time=$(seconds_to_time $duration)
    local date=$(info_with_date "Iteration [$iteration/$num_iterations] finished with success in [$duration_time]")
    stringified_json_report+="], \"iterationStatus\": \"$SUCCESS\", \"iterationEnd\": \"$date\", \"iterationDuration\": \"$duration_time\"}"
    if [ $iteration -eq $num_iterations ]; then
        stringified_json_report+="]"
    fi
}

###############################################################################
#
# Action :
#  iteration_finished_with_error
#  Log and add to stringified report an iteration finished with error.
# Globals :
#   num_iterations: the number of iterations.
#   stringified_json_report: the stringified JSON report.
# Arguments:
#   iteration: the current iteration.
#   error_msg: the error message.
# Returns:
#   None.
#
###############################################################################
iteration_finished_with_error() {
    local iteration=$1
    local error_msg=$2
    local date=$(error_with_date "Iteration [$iteration/$num_iterations] finished with error : ${error_msg}")
    stringified_json_report+="], \"iterationStatus\": \"$ERROR\", \"iterationError\": \"$error_msg\", \"iterationEnd\": \"$date\"}"
    if [ $iteration -eq $num_iterations ]; then
        stringified_json_report+="]"
    fi
}

###############################################################################
#
# Action :
#  iteration_failed
#  Log and add to stringified report an iteration failed.
# Globals :
#   num_iterations: the number of iterations.
#   stringified_json_report: the stringified JSON report.
# Arguments:
#   iteration: the current iteration.
#   error_msg: the error message.
# Returns:
#   None.
#
###############################################################################
iteration_failed() {
    local iteration=$1
    local error_msg=$2
    local date=$(error_with_date "Iteration [$iteration/$num_iterations] failed : ${error_msg}")
    stringified_json_report+="], \"iterationStatus\": \"$FAILED\", \"iterationError\": \"$error_msg\", \"iterationEnd\": \"$date\"}]"
}

###############################################################################
#
# Action :
#  step_started
#  Log and add to stringified report a step started.
# Globals :
#   stringified_json_report: the stringified JSON report.
# Arguments:
#   step_num: the step number.
#   step: the step
#   t: the certificate/trust type.
# Returns:
#   None.
#
###############################################################################
step_started() {
    local step_num=$1
    local step=$2
    local t=$3
    local date=$(info_with_date "Step [$step_num] started : step [$step] type [$t]")
    if [ $step_num -gt 1 ]; then
        stringified_json_report+=","
    fi
    stringified_json_report+="{\"step\": \"$step\", \"stepType\": \"$t\", \"stepStart\": \"$date\", \"step_secadm_commands\": ["
}

###############################################################################
#
# Action :
#  step_finished
#  Log and add to stringified report a step finished with the given status.
# Globals :
#   stringified_json_report: the stringified JSON report.
# Arguments:
#   step_num: the step number.
#   step_status: the step status.
#   duration: the duration (in seconds) of the step.
# Returns:
#   None.
#
###############################################################################
step_finished() {
    local step_num=$1
    local step_status=$2
    local duration=$3
    local duration_time=$(seconds_to_time $duration)
    local date=$(info_with_date "Step [$step_num] finished with status [$step_status] in [$duration_time]")
    stringified_json_report+="], \"stepStatus\": \"$step_status\", \"stepEnd\": \"$date\", \"stepDuration\": \"$duration_time\"}"
}

###############################################################################
#
# Action :
#  step_failed
#  Log and add to stringified report a step failed.
# Globals :
#   stringified_json_report: the stringified JSON report.
# Arguments:
#   step_num: the step number.
#   error_msg: the error message.
# Returns:
#   None.
#
###############################################################################
step_failed() {
    local step_num=$1
    local error_msg=$2
    local date=$(error_with_date "Step [$step_num] failed : ${error_msg}")
    stringified_json_report+="], \"stepStatus\": \"$STEP_FAILED\", \"stepError\": \"$error_msg\", \"stepEnd\": \"$date\"}"
}

###############################################################################
#
# Action :
#  command_started
#  Log and add to stringified report a command started.
# Globals :
#   stringified_json_report: the stringified JSON report.
# Arguments:
#   cmd_num: the command number.
#   cmd_type: the command type.
# Returns:
#   None.
#
###############################################################################
command_started() {
    local cmd_num=$1
    local cmd_type=$2
    debug "command_started: cmd_num [$cmd_num] cmd_type [$cmd_type]"
    local cmd_start_date=$(info_with_date "Command [$cmd_type] started")
    if [ $cmd_num -gt 1 ]; then
        stringified_json_report+=","
    fi
    stringified_json_report+="{\"cmdType\": \"$cmd_type\", \"cmdStart\": \"$cmd_start_date\""
}

###############################################################################
#
# Action :
#  command_response_received
#  Log and add to stringified report a command response received.
# Globals :
#   stringified_json_report: the stringified JSON report.
# Arguments:
#   cmd_response_duration: the command response time in seconds.
# Returns:
#   None.
#
###############################################################################
command_response_received() {
    local cmd_response_duration=$1
    debug "command_response_received: cmd_response_duration [$cmd_response_duration]"
    local cmd_response_time=$(seconds_to_time $cmd_response_duration)
    info "Command response received in [$cmd_response_time]"
    stringified_json_report+=", \"cmdResponseTime\": \"$cmd_response_time\""
}

###############################################################################
#
# Action :
#  job_started
#  Log and add to stringified report a job started.
# Globals :
#   stringified_json_report: the stringified JSON report.
# Arguments:
#   job_get: the job get command.
# Returns:
#   None.
#
###############################################################################
job_started() {
    local job_get=$1
    debug "job_started: job_get[$job_get]"
    stringified_json_report+=", \"jobGet\": \"$job_get\""
}

###############################################################################
#
# Action :
#  job_completed_with_success
#  Log and add to stringified report a command with job completed with success.
# Globals :
#   stringified_json_report: the stringified JSON report.
# Arguments:
#   job_duration: the command job in seconds.
#   num_wfs: the number of workflows.
# Returns:
#   None.
#
###############################################################################
job_completed_with_success() {
    local job_duration=$1
    local num_wfs=$2
    local job_duration_time=$(seconds_to_time $job_duration)
    info "Job completed with success on [$num_wfs] workflows in [$job_duration_time]"
    stringified_json_report+=", \"jobStatus\": \"$SUCCESS\", \"jobDuration\": \"$job_duration_time\", \"numWfs\": \"$num_wfs\""
}

###############################################################################
#
# Action :
#  job_completed_with_error
#  Log and add to stringified report a command with job completed with error.
# Globals :
#   stringified_json_report: the stringified JSON report.
# Arguments:
#   job_duration: the command job in seconds.
#   num_wfs: the number of workflows.
#   num_error_wfs: the number of error workflows.
# Returns:
#   None.
#
###############################################################################
job_completed_with_error() {
    local job_duration=$1
    local num_wfs=$2
    local num_error_wfs=$3
    local job_duration_time=$(seconds_to_time $job_duration)
    error "Job completed with error for [$num_error_wfs/$num_wfs] workflows in [$job_duration_time]"
    stringified_json_report+=", \"jobStatus\": \"$ERROR\", \"jobDuration\": \"$job_duration_time\", \"numErrorWfs\": \"$num_error_wfs\", \"numWfs\": \"$num_wfs\""
}

###############################################################################
#
# Action :
#  command_finished
#  Log and add to stringified report a command finished.
# Globals :
#   stringified_json_report: the stringified JSON report.
# Arguments:
#   cmd_num: the command number.
#   cmd_status: the command status.
#   duration: the duration (in seconds) of the command.
#   kpi_msg: the KPI message for the command.
# Returns:
#   None.
#
###############################################################################
command_finished() {
    local cmd_num=$1
    local cmd_status=$2
    local duration=$3
    local kpi_msg=$4
    local duration_time=$(seconds_to_time $duration)
    local date=$(info_with_date "Command [$cmd_num] finished with status [$cmd_status] in [$duration_time]")
    if [ "$kpi_msg" == "" ]; then
        stringified_json_report+=", \"cmdStatus\": \"$cmd_status\", \"cmdEnd\": \"$date\", \"cmdDuration\": \"$duration_time\", \"cmdKPI\": \"all KPI satisfied\"}"
    else
        stringified_json_report+=", \"cmdStatus\": \"$cmd_status\", \"cmdEnd\": \"$date\", \"cmdDuration\": \"$duration_time\", \"cmdKPI\": \"$kpi_msg\"}"
    fi
}

###############################################################################
#
# Action :
#  command_failed
#  Log and add to stringified report a command finished with error.
# Globals :
#   stringified_json_report: the stringified JSON report.
# Arguments:
#   error_msg: the error message.
# Returns:
#   None.
#
###############################################################################
command_failed() {
    local error_msg=$1
    error "Command finished with error [$error_msg]"
    stringified_json_report+=", \"cmdStatus\": \"FAILED\", \"cmdError\": \"$error_msg\"}"
}

###############################################################################
#
# Action :
#  clear_command_report_global_variables
#  Clear global variables used in JSON report for current command.
# Globals :
#   last_command_with_job_error: the error of last command with job.
#   last_job_duration: the duration of last completed job.
#   last_job_wfs: the num of workflows of last completed job.
#   last_job_error_wfs: the num of error workflows of last completed job.
# Arguments:
#   None.
# Returns:
#   None.
#
###############################################################################
clear_command_report_global_variables() {
    last_command_with_job_error=
    last_job_duration=0
    last_job_wfs=0
    last_job_error_wfs=0
}

###############################################################################
#
# Command Functions
#
###############################################################################

###############################################################################
#
# Action :
#  create_nodes_files
#  Create node files; if files already exist, remove them before creating again.
# Globals :
#   xmlfile: the XML file used in command with --xmlfile option.
#   nodefile: the text file used in command with --nodefile option.
#   getnodefile: nodefile in trust get command with --nodefile option.
#   nes: the list of nodes in terms of <ne_type> and <num_nodes>
#   max_num_nodes_in_get: max nodes for trust get command.
#   total_num_nodes_in_get: num of nodes for trust get command.
#   total_num_nodes: the total number of involved SYNCHRONIZED nodes.
# Arguments:
#   tag: the workload tag.
# Returns:
#   0 if success, 1 if failure.
#
###############################################################################
create_nodes_files() {
    tag=$1
    #xmlfile=${output_dir}/xmlfile_${tag}.xml
    #nodefile=${output_dir}/nodefile_${tag}.txt
    #getnodefile=${output_dir}/get_nodefile_${tag}.txt
    xmlfile=${output_dir}/xmlfile.xml
    nodefile=${output_dir}/nodefile.txt
    getnodefile=${output_dir}/get_nodefile.txt

    if [ -f $xmlfile ]; then
        debug "Removing already existent xmlfile [${xmlfile}]"
        rm -f $xmlfile
    else
        debug "Not yet existent xmlfile [${xmlfile}]"
    fi

    if [ -f $nodefile ]; then
        debug "Removing already existent nodefile [${nodefile}]"
        rm -f $nodefile
    else
        debug "Not yet existent nodefile [${nodefile}]"
    fi

    if [ -f $getnodefile ]; then
        debug "Removing already existent getnodefile [${getnodefile}]"
        rm -f $getnodefile
    else
        debug "Not yet existent getnodefile [${getnodefile}]"
    fi

    info "Creating xmlfile [${xmlfile}] and nodefile [${nodefile}] and getnodefile [${getnodefile}]"
    echo "<?xml version=\"1.0\" encoding=\"UTF-8\"?>">${xmlfile}
    echo "<Nodes>">>${xmlfile}
    touch $nodefile
    touch $getnodefile

    IFS='+' read -r -a array_nes <<< "$nes"
    for type_number in "${array_nes[@]}"
    do
        debug "Getting nodes : type_number [${type_number}]"
        IFS='=' read -r ne_type num_nodes <<< "$type_number"
        debug "Getting nodes : ne_type [${ne_type}] num_nodes [${num_nodes}]"
        local current_num_nodes=0
        for node in $($cli_app "cmedit get * --scopefilter (CmFunction.syncStatus==SYNCHRONIZED) NetworkElement --netype=${ne_type}" | grep FDN | cut -d= -f2 | uniq)
        do
            if [ $current_num_nodes -lt $num_nodes ]; then
                echo "  <Node>">>${xmlfile}
                echo "    <NodeFdn>"$node"</NodeFdn>">>${xmlfile}
                #echo "    <EntityProfileName>DUSGen2OAM_CHAIN_EP</EntityProfileName>">>${xmlfile}
                echo "  </Node>">>${xmlfile}
                echo -n "$node;">>${nodefile}
                if [ $total_num_nodes_in_get -lt $max_num_nodes_in_get ]; then
                    echo -n "$node;">>${getnodefile}
                    total_num_nodes_in_get=$[total_num_nodes_in_get + 1]
                fi
            else
                break
            fi
            current_num_nodes=$[current_num_nodes + 1]
        done
        local num_added_nodes=0
        if [ $current_num_nodes -ge $num_nodes ]; then
            num_added_nodes=$num_nodes
        else
            num_added_nodes=$current_num_nodes
        fi
        if [ $num_added_nodes -gt 0 ]; then
            total_num_nodes=$[total_num_nodes + num_added_nodes]
            info "Added [${num_added_nodes}] of required [${num_nodes}] nodes of type [${ne_type}]"
        else
            warn "No node found of required [${num_nodes}] nodes of type [${ne_type}]"
        fi
    done

    echo "</Nodes>">>${xmlfile}
    truncate -s-1 ${nodefile}
    truncate -s-1 ${getnodefile}

    if [ $total_num_nodes -gt 0 ]; then
        return 0
    else
        return 1
    fi
}

###############################################################################
#
# Action :
#  check_job_get_response
#  Check secadm job get command response.
# Globals :
#   total_num_nodes: the total number of involved SYNCHRONIZED nodes.
# Arguments:
#   job_get_response: the secadm job get command response.
# Returns: $COMMAND_FINISHED_WITH_SUCCESS if job completed with success
#          $COMMAND_FAILED if failed (an unrecoverable error occurred)
#          $COMMAND_FINISHED_WITH_ERROR if job completed with error
#          $COMMAND_NOT_YET_FINISHED if not yet completed.
#
###############################################################################
check_job_get_response() {

    local job_get_response=$@
    debug "Checking job get response [$job_get_response]"

    local job_get_response_regexp="^(.+) Command Id : (.+) Job User : (.+) Job Status : (.+) Job Start Date : (.+) Job End Date : (.+) Num Of Workflows : (.+) Num Of Pending Workflows : (.+) Num Of Running Workflows : (.+) Num Of Success Workflows : (.+) Num Of Error Workflows : (.+) Min Duration Of Success Workflows : (.+) Max Duration Of Success Workflows : (.+) Avg Duration Of Success Workflows : (.+)$"
    if [[ $job_get_response =~ $job_get_response_regexp ]]; then
        local job_status=${BASH_REMATCH[4]}
        debug "Job Status [${job_status}]"
        if [ "$job_status" == "COMPLETED" ]; then
            local job_start_date=${BASH_REMATCH[5]}
            local job_end_date=${BASH_REMATCH[6]}
            local job_duration=$(get_duration "${job_start_date}" "${job_end_date}")
            debug "Job Start Date [${job_start_date}] Job End Date [${job_end_date}] Job Duration [${job_duration}]"
            local num_wfs=${BASH_REMATCH[7]}
            debug "Num Of Workflows [$num_wfs]"
            if [ $num_wfs -lt $total_num_nodes ]; then
                warn "Job completed but executed on [$num_wfs] instead of [$total_num_nodes] nodes. Maybe some nodes went to UNSYNCHRONIZED state"
            fi
            local num_error_wfs=${BASH_REMATCH[11]}
            debug "Num Of Error Workflows [$num_error_wfs]"
            last_job_duration=$job_duration
            last_job_wfs=$num_wfs
            last_job_error_wfs=$num_error_wfs
            if [ $num_error_wfs -gt 0 ]; then
                job_completed_with_error $job_duration $num_wfs $num_error_wfs
                return $COMMAND_FINISHED_WITH_ERROR
            else
                job_completed_with_success $job_duration $num_wfs
                return $COMMAND_FINISHED_WITH_SUCCESS
            fi
        else
            debug "Job not yet completed : job get response [$job_get_response]"
            return $COMMAND_NOT_YET_FINISHED
        fi
    else
        last_command_with_job_error="Unexpected format of job get response [$job_get_response]"
        return $COMMAND_FAILED
    fi
}

###############################################################################
#
# Action :
#  check_command_response
#  Check secadm command response
# Globals :
#   job_period: the period (in s) for the get job command.
# Arguments:
#   cmd_response: the secadm command response.
# Returns: $COMMAND_FINISHED_WITH_SUCCESS if job completed with success
#          $COMMAND_FAILED if failed (an unrecoverable error occurred)
#          $COMMAND_FINISHED_WITH_ERROR if job completed with error
#          $COMMAND_NOT_YET_FINISHED if not yet completed.
#
###############################################################################
check_command_response() {

    local cmd_response=$@
    debug "Checking command response [$cmd_response]"

    job_get_regexp="Successfully started a job (.+). Perform '(.+)' to get progress info."
    if [[ $cmd_response =~ $job_get_regexp ]]; then
        local job_get_summary="${BASH_REMATCH[2]} --summary"
        job_started "${job_get_summary}"
        local completed="false"
        local job_get_num=1
        while [ "$completed" != "true" ]
        do
            local job_get_start=$SECONDS
            job_get_response=`$cli_app "${job_get_summary}"`
            local job_get_end=$SECONDS
            local job_get_duration=$(($job_get_end - $job_get_start))
            debug "Job get response [$job_get_response] in [$(seconds_to_time $job_get_duration)] at attempt [$job_get_num]"
            check_job_get_response $job_get_response
            local res=$?
            if [ $res -eq $COMMAND_NOT_YET_FINISHED ]; then
                debug "Job not yet completed after [$job_get_num] get attempts"
                sleep $job_period
            elif [ $res -eq $COMMAND_FINISHED_WITH_SUCCESS ]; then
                debug "Job completed with success after [$job_get_num] get attempts with result [$res]"
                completed="true"
                return $res
            elif [ $res -eq $COMMAND_FINISHED_WITH_ERROR ]; then
                debug "Job completed with error after [$job_get_num] get attempts with result [$res]"
                completed="true"
                return $res
            else
                debug "Job failed after [$job_get_num] get attempts with result [$res]"
                return $res
            fi
            job_get_num=$[job_get_num + 1]
        done
    else
        last_command_with_job_error="No successfully started job in command response [$cmd_response]"
        return $COMMAND_FAILED
    fi
}

###############################################################################
#
# Action :
#  do_secadm_command_with_job
#  Perform secadm command starting job.
# Globals :
#   None.
# Arguments:
#   cmd_num: the command number.
#   cmd_type: the command type.
#   cmd: the secadm command.
#   nodes_file: the nodes file.
# Returns: $COMMAND_PASSED if finished with success and KPI satisfied
#          $COMMAND_FAILED if failed (an unrecoverable error occurred)
#          $COMMAND_PASSED_WITH_ERROR if finished with error and KPI satisfied
#          $COMMAND_NOT_PASSED if finished and KPI unsatisfied.
#
###############################################################################
do_secadm_command_with_job() {

    local cmd_num=$1
    local cmd_type=$2
    local cmd=$3
    local nodes_file=$4
    debug "Do secadm command with job : cmd num [$cmd_num] cmd type [$cmd_type] cmd [$cmd] nodes file [$nodes_file]"

    clear_command_report_global_variables

    command_started $cmd_num $cmd_type
    local cmd_start=$SECONDS
    local cmd_response=`$cli_app "${cmd}" $nodes_file`
    local cmd_end=$SECONDS
    debug "Do secadm command with job : command response [${cmd_response}]"
    local cmd_response_duration=$(($cmd_end - $cmd_start))
    command_response_received $cmd_response_duration
    check_command_response $cmd_response
    local check_command_response_result=$?
    if [ $check_command_response_result -eq $COMMAND_FAILED ]; then
        error "Do secadm command with job : failed check command response with error result [$check_command_response_result]"
        command_failed $last_command_with_job_error
        return $COMMAND_FAILED
    fi
    info "Do secadm command with job : check command response returns [$check_command_response_result]"
    total_command_duration=$[ cmd_response_duration + last_job_duration ]
    job_error_wfs_percentage=$[ last_job_error_wfs / last_job_wfs * 100 ]
    command_max_batch_size_kpi=${max_batch_size_kpi[$cmd_type]}
    command_max_duration_kpi_in_minutes=${max_duration_kpi[$cmd_type]}
    if [ $command_max_duration_kpi_in_minutes -eq -1 ]; then
        command_max_duration_kpi=-1
    else
        command_max_duration_kpi=$[ command_max_duration_kpi_in_minutes * 60 ]
    fi
    command_max_error_percentage_kpi=${max_error_percentage_kpi[$cmd_type]}
    info "Do secadm command with job : command response duration [$cmd_response_duration]"
    info "Do secadm command with job : job duration [$last_job_duration]"
    info "Do secadm command with job : total command duration [$total_command_duration]"
    info "Do secadm command with job : job error wfs percentage [$job_error_wfs_percentage] error_wfs/wfs [$last_job_error_wfs/$last_job_wfs]"
    info "Do secadm command with job : max batch size KPI [$command_max_batch_size_kpi] for command type [$cmd_type]"
    info "Do secadm command with job : max duration KPI [$command_max_duration_kpi] for command type [$cmd_type]"
    info "Do secadm command with job : max error percentage KPI [$command_max_error_percentage_kpi] for command type [$cmd_type]"
    command_kpi_message=""
    passed="true"
    if [ ! $command_max_duration_kpi -eq -1 ]; then
        if [ $total_command_duration -le $command_max_duration_kpi ]; then
            if [ ! $command_max_batch_size_kpi -eq -1 ] && [ $last_job_wfs -lt $command_max_batch_size_kpi ]; then
                command_kpi_message+=" max duration KPI satisfied on fewer nodes than max batch size KPI [$last_job_wfs/$command_max_batch_size_kpi];"
            fi
        else
            passed="false"
            if [ $command_max_batch_size_kpi -eq -1 ] || [ $last_job_wfs -le $command_max_batch_size_kpi ]; then
                command_kpi_message+=" max duration KPI unsatisfied;"
            else
                command_kpi_message+=" max duration KPI unsatisfied on more nodes than max batch size KPI [$last_job_wfs/$command_max_batch_size_kpi];"
            fi
        fi
    fi
    if [ ! $command_max_error_percentage_kpi -eq -1 ]; then
        if [ $job_error_wfs_percentage -le $command_max_error_percentage_kpi ]; then
            if [ ! $command_max_batch_size_kpi -eq -1 ] && [ $last_job_wfs -lt $command_max_batch_size_kpi ]; then
                command_kpi_message+=" max error percentage KPI satisfied on fewer nodes than max batch size KPI [$last_job_wfs/$command_max_batch_size_kpi];"
            fi
        else
            passed="false"
            if [ $command_max_batch_size_kpi -eq -1 ] || [ $last_job_wfs -le $command_max_batch_size_kpi ]; then
                command_kpi_message+=" max error percentage KPI unsatisfied;"
            else
                command_kpi_message+=" max error percentage KPI unsatisfied on more nodes than max batch size KPI [$last_job_wfs/$command_max_batch_size_kpi];"
            fi
        fi
    fi
    if [ $check_command_response_result -eq $COMMAND_FINISHED_WITH_SUCCESS ]; then
        command_finished $cmd_num $SUCCESS $total_command_duration "${command_kpi_message}"
    elif [ $res -eq $COMMAND_FINISHED_WITH_ERROR ]; then
        command_finished $cmd_num $ERROR $total_command_duration "${command_kpi_message}"
    fi
    if [ "$passed" == "true" ]; then
        if [ $check_command_response_result -eq $COMMAND_FINISHED_WITH_SUCCESS ]; then
            return $COMMAND_PASSED
        elif [ $check_command_response_result -eq $COMMAND_FINISHED_WITH_ERROR ]; then
            return $COMMAND_PASSED_WITH_ERROR
        fi
    else
        return $COMMAND_NOT_PASSED
    fi
}

###############################################################################
#
# Action :
#  do_secadm_command
#  Perform secadm command.
# Globals :
#   None.
# Arguments:
#   cmd_num: the command number.
#   cmd_type: the command type.
#   cmd: the secadm command.
#   nodes_file: the nodes file.
# Returns: $COMMAND_PASSED if finished with success and KPI satisfied
#          $COMMAND_FAILED if failed (an unrecoverable error occurred)
#          $COMMAND_PASSED_WITH_ERROR if finished with error and KPI satisfied
#          $COMMAND_NOT_PASSED if finished and KPI unsatisfied.
#
###############################################################################
do_secadm_command() {

    local cmd_num=$1
    local cmd_type=$2
    local cmd=$3
    local nodes_file=$4
    debug "Do secadm command : cmd num [$cmd_num] cmd type [$cmd_type] cmd [$cmd] nodes file [$nodes_file]"

    clear_command_report_global_variables

    command_started $cmd_num $cmd_type
    local cmd_start=$SECONDS
    local cmd_response=`$cli_app "${cmd}" $nodes_file`
    local cmd_end=$SECONDS
    debug "Do secadm command : command response [${cmd_response}]"
    local cmd_response_duration=$(($cmd_end - $cmd_start))
    command_response_received $cmd_response_duration
    return $COMMAND_FAILED
}

###############################################################################
#
# Action :
#  do_certificate_issue
#  Perform secadm certificate issue OAM.
# Globals :
#   xmlfile: the XML file used in command with --xmlfile option
# Arguments:
#   certificate_type: the certificate type (OAM or IPSEC)
# Returns: $COMMAND_PASSED if finished with success and KPI satisfied
#          $COMMAND_FAILED if failed (an unrecoverable error occurred)
#          $COMMAND_PASSED_WITH_ERROR if finished with error and KPI satisfied
#          $COMMAND_NOT_PASSED if finished and KPI unsatisfied.
#
###############################################################################
do_certificate_issue() {

    local certificate_type=$1
    debug "Do certificate issue : certificate type [${certificate_type}] xmlfile [${xmlfile}]"

    local xmlfile_basename=$(basename ${xmlfile})
    debug "Do certificate issue : xmlfile_basename [${xmlfile_basename}]"

    do_secadm_command_with_job 1 $CERTIFICATE_ISSUE "secadm certificate issue --certtype "${certificate_type}" --xmlfile file:${xmlfile_basename}" $xmlfile
    local res=$?
    if [ $res -eq $COMMAND_FAILED ]; then
        error "Do certificate issue : failed command"
    fi
    return $res
}

###############################################################################
#
# Action :
#  do_certificate_reissue
#  Perform secadm certificate reissue OAM.
# Globals :
#   nodefile: the file used in command with --nodefile option
# Arguments:
#   certificate_type: the certificate type (OAM or IPSEC)
# Returns: $COMMAND_PASSED if finished with success and KPI satisfied
#          $COMMAND_FAILED if failed (an unrecoverable error occurred)
#          $COMMAND_PASSED_WITH_ERROR if finished with error and KPI satisfied
#          $COMMAND_NOT_PASSED if finished and KPI unsatisfied.
#
###############################################################################
do_certificate_reissue() {

    local certificate_type=$1
    debug "Do certificate reissue : certificate type [${certificate_type}] nodefile [${nodefile}]"

    local nodefile_basename=$(basename ${nodefile})
    debug "Do certificate reissue : nodefile_basename [${nodefile_basename}]"

    do_secadm_command_with_job 1 $CERTIFICATE_REISSUE "secadm certificate reissue --certtype "${certificate_type}" --nodefile file:${nodefile_basename}" $nodefile
    local res=$?
    if [ $res -eq $COMMAND_FAILED ]; then
        error "Do certificate reissue : failed command"
    fi
    return $res
}

###############################################################################
#
# Action :
#  do_trust_distribute
#  Perform secadm trust distribute OAM.
# Globals :
#   nodefile: the file used in command with --nodefile option
# Arguments:
#   trust_category: the trust category (OAM or IPSEC or LAAD)
# Returns: $COMMAND_PASSED if finished with success and KPI satisfied
#          $COMMAND_FAILED if failed (an unrecoverable error occurred)
#          $COMMAND_PASSED_WITH_ERROR if finished with error and KPI satisfied
#          $COMMAND_NOT_PASSED if finished and KPI unsatisfied.
#
###############################################################################
do_trust_distribute() {

    local trust_category=$1

    debug "Do trust distribute : trust category [${trust_category}] nodefile [${nodefile}]"

    local nodefile_basename=$(basename ${nodefile})
    debug "Do trust distribute : nodefile_basename [${nodefile_basename}]"

    do_secadm_command_with_job 1 $TRUST_DISTRIBUTE "secadm trust distribute --trustcategory "${trust_category}" --nodefile file:${nodefile_basename}"  $nodefile
    local res=$?
    if [ $res -eq $COMMAND_FAILED ]; then
        error "Do trust distribute : failed command"
    fi
    return $res
}

###############################################################################
#
# Action :
#  do_trust_distribute_email
#  Perform secadm trust distribute OAM for CA ENM_E-mail_CA.
# Globals :
#   nodefile: the file used in command with --nodefile option
# Arguments:
#   trust_category: the trust category (OAM or IPSEC or LAAD)
# Returns: $COMMAND_PASSED if finished with success and KPI satisfied
#          $COMMAND_FAILED if failed (an unrecoverable error occurred)
#          $COMMAND_PASSED_WITH_ERROR if finished with error and KPI satisfied
#          $COMMAND_NOT_PASSED if finished and KPI unsatisfied.
#
###############################################################################
do_trust_distribute_email() {

    local trust_category=$1
    debug "Do trust distribute email : trust category [${trust_category}] nodefile [${nodefile}]"

    local nodefile_basename=$(basename ${nodefile})
    debug "Do trust distribute email : nodefile_basename [${nodefile_basename}]"

    do_secadm_command_with_job 1 $TRUST_DISTRIBUTE "secadm trust distribute --trustcategory "${trust_category}" -ca ENM_E-mail_CA --nodefile file:${nodefile_basename}" $nodefile
    local res=$?
    if [ $res -eq $COMMAND_FAILED ]; then
        error "Do trust distribute email : failed command"
    fi
    return $res
}

###############################################################################
#
# Action :
#  do_trust_remove_email
#  Perform secadm trust distribute OAM for CA ENM_E-mail_CA.
# Globals :
#   nodefile: the file used in command with --nodefile option
#   getnodefile: nodefile in trust get command with --nodefile option
# Arguments:
#   trust_category: the trust category (OAM or IPSEC or LAAD)
# Returns: $COMMAND_PASSED if finished with success and KPI satisfied
#          $COMMAND_FAILED if failed (an unrecoverable error occurred)
#          $COMMAND_PASSED_WITH_ERROR if finished with error and KPI satisfied
#          $COMMAND_NOT_PASSED if finished and KPI unsatisfied.
#
###############################################################################
do_trust_remove_email() {

    local trust_category=$1
    debug "Do trust remove email : trust category [${trust_category}] nodefile [${nodefile}] getnodefile [${getnodefile}]"

    local nodefile_basename=$(basename ${nodefile})
    debug "Do trust remove email : nodefile_basename [${nodefile_basename}]"

    local getnodefile_basename=$(basename ${getnodefile})
    debug "Do trust remove email : getnodefile_basename [${getnodefile_basename}]"

    local trust_get_start_date=$SECONDS
    local trust_get_email=`$cli_app "secadm trust get --trustcategory "${trust_category}" --nodefile file:${getnodefile_basename}" $getnodefile | sed 's/CN/\nCN/' | grep ENM_E-mail_CA`
    local trust_get_end_date=$SECONDS
    local trust_get_duration=$(($trust_get_end_date - $trust_get_start_date))
    info "Do trust remove email : get trust on [$total_num_nodes_in_get] nodes result in [$(seconds_to_time $trust_get_duration)]"
    debug "Do trust remove email : get trust result [${trust_get_email}]"

    if [ "$trust_get_email" == "" ]; then
        error "Do trust remove email : unexpected empty trust get result"
        return $COMMAND_FAILED
    fi
    local serial_number=`echo ${trust_get_email} | awk '{ print $2 }'`
    local issuer_dn=`echo ${trust_get_email} | awk '{ print $3 }'`
    if [ "$serial_number" == "" ] || [ "$issuer_dn" == "" ]; then
        error "Do trust remove email : wrong SN [$serial_number] or issuer DN [$issuer_dn] from trust get result [$trust_get_email]"
        return $COMMAND_FAILED
    fi
    debug "Do trust remove email : SN [${serial_number}] and issuer DN [${issuer_dn}]"

    do_secadm_command_with_job 1 $TRUST_REMOVE "secadm trust remove --trustcategory "${trust_category}" --issuer-dn ${issuer_dn} --serialnumber ${serial_number} --nodefile file:${nodefile_basename}" $nodefile
    local res=$?
    if [ $res -eq $COMMAND_FAILED ]; then
        error "Do trust remove email : failed command"
    fi
    return $res
}

###############################################################################
###############################################################################
#
# Main program
#
###############################################################################
###############################################################################
login

error_on_getopts=

while getopts ':hvVs:i:p:n:j:f:o:' option; do
  case "$option" in
    h) echo "$usage"
       exit
       ;;
    v) echo "$versions_history"
       exit
       ;;
    V) verbose="true"
       ;;
    s) enm_size=$OPTARG
       ;;
    i) num_iterations=$OPTARG
       ;;
    p) period=$OPTARG
       ;;
    n) nes=$OPTARG
       ;;
    j) job_period=$OPTARG
       ;;
    f) config_file=$OPTARG
       ;;
    o) output_dir=$OPTARG
       ;;
    :) error_on_getopts="missing argument for -$OPTARG"
       ;;
   \?) error_on_getopts="illegal option: -$OPTARG"
       ;;
  esac
done
shift $((OPTIND - 1))

if [ ! -d $output_dir ]; then
    mkdir -p $output_dir
fi
logfile="${output_dir}/${script_basename_no_ext}.log"
if [ ! -f $logfile ]; then
    touch $logfile
fi

info "Starting workload"

if [ "$error_on_getopts" != "" ]; then
    error_on_exit "$error_on_getopts"
    echo "$short_usage" >&2
    exit 1
fi

if [ "$enm_size" == $ENM_SIZE_XL ]; then
    for key in ${!max_batch_size_kpi_xl[@]}; do
        max_batch_size_kpi[$key]=${max_batch_size_kpi_xl[$key]}
    done
    for key in ${!max_duration_kpi_xl[@]}; do
        max_duration_kpi[$key]=${max_duration_kpi_xl[$key]}
    done
    for key in ${!max_error_percentage_kpi_xl[@]}; do
        max_error_percentage_kpi[$key]=${max_error_percentage_kpi_xl[$key]}
    done
elif [ "$enm_size" == $ENM_SIZE_S ]; then
    for key in ${!max_batch_size_kpi_s[@]}; do
        max_batch_size_kpi[$key]=${max_batch_size_kpi_s[$key]}
    done
    for key in ${!max_duration_kpi_s[@]}; do
        max_duration_kpi[$key]=${max_duration_kpi_s[$key]}
    done
    for key in ${!max_error_percentage_kpi_s[@]}; do
        max_error_percentage_kpi[$key]=${max_error_percentage_kpi_s[$key]}
    done
elif [ "$enm_size" == $ENM_SIZE_XS ]; then
    for key in ${!max_batch_size_kpi_xs[@]}; do
        max_batch_size_kpi[$key]=${max_batch_size_kpi_xs[$key]}
    done
    for key in ${!max_duration_kpi_xs[@]}; do
        max_duration_kpi[$key]=${max_duration_kpi_xs[$key]}
    done
    for key in ${!max_error_percentage_kpi_xs[@]}; do
        max_error_percentage_kpi[$key]=${max_error_percentage_kpi_xs[$key]}
    done
else
    error_on_exit "wrong ENM size: $enm_size"
    echo "$short_usage" >&2
    exit 1
fi

if [ $num_iterations -le 0 ]; then
    error_on_exit "wrong num iterations: $num_iterations"
    echo "$short_usage" >&2
    exit 1
fi

if [ $period -lt 0 ]; then
    error_on_exit "wrong period: $period"
    echo "$short_usage" >&2
    exit 1
fi
period_in_sec=$(($period * 60))

if [ "$nes" = "" ]; then
    error_on_exit "missing mandatory nodes"
    echo "$short_usage" >&2
    exit 1
fi

if [[ ! $nes =~ $nes_regexp ]]; then
    error_on_exit "wrong nodes format: $nes"
    echo "$short_usage" >&2
    exit 1
fi

if [ $job_period -le 0 ]; then
    error_on_exit "wrong job period: $job_period"
    echo "$short_usage" >&2
    exit 1
fi

if [ ! -f $config_file ]; then
    error_on_exit "cannot access configuration file: $config_file"
    echo "$short_usage" >&2
    exit 1
fi

tag=$(date +"%m-%d-%Y_%H-%M-%S")

workload_started $tag

debug "Starting validation of config_file [$config_file]"

iteration_regexp="^([0-9]+)$"
while IFS=, read -r iteration step type
do
    debug "Validating line : iteration [$iteration] step [${step}] type [${type}]"
    if [[ $iteration =~ $iteration_regexp ]] && [ $iteration -le $num_iterations ] || [ "$iteration" == "*" ]; then
        case "$step" in
            "i" | "I")
                if [ "$type" != "OAM" ] && [ "$type" != "IPSEC" ]; then
                    error_msg="Failed validation of config file : wrong certificate type [${type}] for step [${step}]"
                    workload_failed "${error_msg}"
                    error_on_exit "${error_msg}"
                    exit 1
                fi
                ;;
            "t" | "e" | "r")
                if [ "$type" != "OAM" ] && [ "$type" != "IPSEC" ] && [ "$type" != "LAAD" ]; then
                    error_msg="Failed validation of config file : wrong trust category [${type}] for step [${step}]"
                    workload_failed "${error_msg}"
                    error_on_exit "${error_msg}"
                    exit 1
                fi
                ;;
            *)
                error_msg="Failed validation of config file : illegal step [$step]"
                workload_failed "${error_msg}"
                error_on_exit "${error_msg}"
                exit 1
             ;;
        esac
    elif [[ $iteration == \#* ]]; then
        debug "Skipping commented out line : iteration [$iteration] step [${step}] type [${type}]"
    elif [ "$iteration" == "" ]; then
        debug "Skipping empty line"
    else
        error_msg="Failed validation of config file : wrong iteration [${iteration}] for num_iterations [${num_iterations}] step [${step}]"
        workload_failed "${error_msg}"
        error_on_exit "${error_msg}"
        exit 1
    fi

done < "$config_file"

debug "Successful validation of config_file [$config_file]"

create_nodes_files $tag
res=$?
if [ $res -gt 0 ]; then
    error_msg="Failed : no synchronized nodes found for nes [${nes}]"
    workload_failed "${error_msg}"
    error_on_exit "${error_msg}"
    exit 1
else
    workload_created_nodes_files
fi

workload_status=$SUCCESS
workload_error=
current_iteration=1
while [ $current_iteration -le $num_iterations ]
do
    iteration_started $current_iteration
    iteration_start=$SECONDS

    iteration_status=$SUCCESS
    iteration_error=
    current_step=1
    while IFS=, read -r iteration step type
    do
        if [ "$iteration" == "$current_iteration" ] || [ "$iteration" == "*" ]; then
            step_started $current_step $step $type
            step_start_date=$SECONDS

            case "$step" in
                "i")
                    do_certificate_issue ${type}
                    command_result=$?
                    ;;
                "I")
                    do_certificate_reissue ${type}
                    command_result=$?
                    ;;
                "t")
                    do_trust_distribute ${type}
                    command_result=$?
                    ;;
                "e")
                    do_trust_distribute_email ${type}
                    command_result=$?
                    ;;
                "r")
                    do_trust_remove_email ${type}
                    command_result=$?
                    ;;
            esac

            if [ $command_result -eq $COMMAND_FAILED ]; then
                step_error="Failed step [${step}] type [${type}]"
                iteration_status=$FAILED
                iteration_error+=" Step [${step}] type [${type}] failed"
                step_failed $current_step "${step_error}"
                break
            else
                step_end_date=$SECONDS
                step_duration=$(($step_end_date - $step_start_date))
                if [ $command_result -eq $COMMAND_PASSED ]; then
                    step_status=$STEP_PASSED
                elif [ $command_result -eq $COMMAND_PASSED_WITH_ERROR ]; then
                    step_status=$STEP_PASSED_WITH_ERROR
                    iteration_status=$ERROR
                    iteration_error+=" Step [${step}] type [${type}] passed with error;"
                elif [ $command_result -eq $COMMAND_NOT_PASSED ]; then
                    step_status=$STEP_NOT_PASSED
                    iteration_status=$ERROR
                    iteration_error+=" Step [${step}] type [${type}] not passed;"
                fi
                step_finished $current_step $step_status $step_duration
            fi

            current_step=$[current_step + 1]
        fi

    done < "$config_file"

    if [ "$iteration_status" == $FAILED ]; then
        workload_status=$FAILED
        workload_error+=" Iteration [$current_iteration] failed"
        iteration_failed $current_step $iteration_error
        break
    else
        iteration_end=$SECONDS
        iteration_duration=$(($iteration_end - $iteration_start))
        if [ "$iteration_status" == $SUCCESS ]; then
            iteration_finished_with_success $current_iteration $iteration_duration
        else
            workload_status=$ERROR
            workload_error+=" Iteration [$current_iteration] finished with error;"
            iteration_finished_with_error $current_iteration $iteration_error
        fi
        if [ $current_iteration -lt $num_iterations ] && [ $period_in_sec -gt 0 ]; then
            if [ $iteration_duration -lt $period_in_sec ]; then
                sleep_in_sec=$(($period_in_sec - $iteration_duration))
                info "Sleeping for remaining [$sleep_in_sec] seconds"
                sleep $sleep_in_sec
            else
                warn "Iteration [$current_iteration/$num_iterations] duration [$iteration_duration] exceeded period of [$period_in_sec] seconds"
            fi
        fi
    fi

    current_iteration=$[current_iteration + 1]
done

if [ "$workload_status" == $SUCCESS ]; then
    workload_finished_with_success
elif [ "$workload_status" == $ERROR ]; then
    workload_finished_with_error "${workload_error}"
else
    workload_failed "${workload_error}"
    error_on_exit "${workload_error}"
    exit 1
fi

exit 0
