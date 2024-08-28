#! /bin/bash

source docker-functions.sh

exec export COMPOSE_HTTP_TIMEOUT=500 
exec echo $COMPOSE_HTTP_TIMEOUT
exec docker-compose kill
#[ "$1" != "--no-pull" ] && exec docker-compose pull --ignore-pull-failures
[ "$1" != "--no-pull" ] && exec docker-compose pull
exec docker-compose build --pull
exec docker-compose up --force-recreate -d
exec docker-compose logs -f

