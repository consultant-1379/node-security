#! /bin/bash

source docker-functions.sh

CONTAINER_ID=`docker ps -a | grep -m 1 $1 | awk -F ' ' '{print $1}'`

echo $CONTAINER_ID

exec docker exec -it $CONTAINER_ID bash

