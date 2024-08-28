#!/bin/bash
set -x
AWK=/bin/awk
BASENAME=/bin/basename
CUT=/bin/cut
ECHO="echo -e"
ENV=/bin/env
GREP=/bin/grep
LOGGER=/usr/bin/logger
LOGGER_TAG="TOR_NS"
LITP_JEE_DE_PATTERN="node-security"
SCRIPT_NAME=$( ${BASENAME} ${0} )
SED=/bin/sed
FM_TOPIC=jms.topic.FMAlarmOutBusTopic
DIVERT_QUEUE=jms.queue.FMAlarmOutDivertQueue
##
## ENV
##
STANDALONE_XML=$( ${ECHO} ${LITP_JEE_CONTAINER_command_line_options} | ${GREP} -o \\-\\-server\\-config=\.*\.xml | ${CUT} -d= -f2 | ${AWK} {'print $1'} )
JBOSS_CONFIG=${LITP_JEE_CONTAINER_home_dir}/standalone/configuration/${STANDALONE_XML}
container_check=$( ${ENV} | ${GREP} _JEE_DE_name | ${GREP} ${LITP_JEE_DE_PATTERN} > /dev/null 2>&1 )
ret_val=${?}

##
## INFORMATION print
##
info()
{
	if [ ${#} -eq 0 ]; then
		while read data; do
			logger -s -t ${LOGGER_TAG} -p user.notice "INFORMATION ( ${SCRIPT_NAME} ): ${data}"
		done
	else
		logger -s -t ${LOGGER_TAG} -p user.notice "INFORMATION ( ${SCRIPT_NAME} ): $@"
	fi
}

##
## ERROR print
##
error()
{
	if [ ${#} -eq 0 ]; then
		while read data; do
			logger -s -t ${LOGGER_TAG} -p user.err "ERROR ( ${SCRIPT_NAME} ): ${data}"
		done
	else
		logger -s -t ${LOGGER_TAG} -p user.err "ERROR ( ${SCRIPT_NAME} ): $@"
	fi
}

##
## WARN print
##
warn()
{
	if [ ${#} -eq 0 ]; then
		while read data; do
			logger -s -t ${LOGGER_TAG} -p user.warning "WARN ( ${SCRIPT_NAME} ): ${data}"
		done
	else
		logger -s -t ${LOGGER_TAG} -p user.warning "WARN ( ${SCRIPT_NAME} ): $@"
	fi
}


##
## Clean up function, nothing to do so far
##
cleanup ()
{
	info "No cleanup to be performed"
}

##
## Exit gracfully so as not to break flow
##
graceful_exit ()
{
	[ "${#}" -gt 1 -a "${1}" -eq 0 ] && info "${2}"
	[ "${#}" -gt 1 -a "${1}" -gt 0 ] && error "${2}"
	#cleanup
	exit ${1}
}

############
## EXECUTION
############
if [ ${ret_val} -eq 0 ]; then
        info "Node Security Container found, Checking for Divert in ${JBOSS_CONFIG}"
        if ${GREP} diverts ${JBOSS_CONFIG}; then
                info "<diverts> already exists in ${JBOSS_CONFIG}"
        else
                info "Node Security Container found, Checking for Divert in ${JBOSS_CONFIG}"
                ${SED} -i "/<\/acceptors>/a \\\t\t<diverts>\\n\t\t\t <divert name=\"oasis-divert\">\\n\t\t\t\t <address>${FM_TOPIC}<\/address>\\n\t\t\t\t  <forwarding-address>${DIVERT_QUEUE}<\/forwarding-address>\\n\t\t\t\t  <exclusive>false<\/exclusive>\\n\t\t\t <\/divert>\\n\t\t<\/diverts> " ${JBOSS_CONFIG}
        fi
        [ ${?} -eq 0 ] && graceful_exit 0 "Updated ${JBOSS_CONFIG}" || warn "Failed to update ${JBOSS_CONFIG}"
fi

exit 0

