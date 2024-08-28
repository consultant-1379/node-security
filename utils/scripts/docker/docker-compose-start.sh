#! /bin/bash

#source docker-functions.sh

function dump() {
           eval echo "\$$1"    
}

function exec() {
          echo -n "Executing: $@ "
                  if [ -z "$DOCKER_DEBUG" ];then
                                    eval $@ || exit 1
                                                    echo -e "\n" 
                                                                      fi
}

temp() {
          docker exec -it $CONTAINER_ID bash
                  docker cp $SRC_FILE $CONTAINER_ID:$DEST_FILE
                            curl -L https://github.com/docker/machine/releases/download/v0.7.0/docker-machine-`uname -s`-`uname -m` > /usr/local/bin/docker-machine && chmod +x /usr/local/bin/docker-machine
} 


exec dump COMPOSE_PROJECT_NAME
exec dump JBOSS_IMAGE_START_CMD
exec dump DOCKER_MACHINE_HOST_IP
exec export COMPOSE_HTTP_TIMEOUT=200
exec dump COMPOSE_HTTP_TIMEOUT

exec docker-compose kill
#[ "$1" != "--no-pull" ] && exec docker-compose pull --ignore-pull-failures
[ "$1" != "--no-pull" ] && exec docker-compose pull
exec docker-compose build --pull
exec docker-compose up --force-recreate -d
exec docker-compose logs 
