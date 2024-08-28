#! /bin/bash

function exec() {
  echo "Executing: $@"
  eval $@ || exit 1  
}

temp() {  
  docker exec -it $CONTAINER_ID bash
  docker cp $SRC_FILE $CONTAINER_ID:$DEST_FILE
  curl -L https://github.com/docker/machine/releases/download/v0.7.0/docker-machine-`uname -s`-`uname -m` > /usr/local/bin/docker-machine && chmod +x /usr/local/bin/docker-machine
}  

