#! /bin/bash 

source docker-functions.sh

exec "curl --digest --user root:shroot --retry 120 --retry-delay 2 --silent $(docker-compose port jboss 9990)/management"

