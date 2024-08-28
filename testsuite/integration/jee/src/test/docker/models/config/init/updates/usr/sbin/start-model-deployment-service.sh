#!/bin/bash -e

source docker-env-functions.sh

LOG=/var/log/mdt.log

if [ -f ${LOG} ]; then
	docker_print "Removing: ${LOG}"
	rm -f ${LOG}
fi

/etc/init.d/modeldeployservice start
while [ ! -f ${LOG} ]; do
	sleep 1
done
while ! grep "Service registered successfully" ${LOG}; do
	sleep 1
done

