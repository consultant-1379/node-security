#!/bin/bash
if [ ! ${CLOUD_DEPLOYMENT}  ]; then
    _CHMOD=/bin/chmod

    DIR_FILE="/ericsson/tor/data"
    FIND_FILE=$(find $DIR_FILE -name "ldapApplicationUser")

    if [ -e "${FIND_FILE}" ] ; then
    $_CHMOD 600 "${FIND_FILE}"
    fi
fi