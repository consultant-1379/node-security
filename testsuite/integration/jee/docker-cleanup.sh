#!/bin/bash



DOCKER_PATH="/var/lib/docker/volumes/* \
/var/lib/docker/image/aufs/repositories.json \
/var/lib/docker/image/aufs/distribution/* \
/var/lib/docker/image/aufs/imagedb/metadata/sha256/* \
/var/lib/docker/image/aufs/imagedb/content/sha256/* \
/var/lib/docker/containers/* \
/var/lib/docker/aufs/diff/* \
/var/lib/docker/aufs/layers/* \
/var/lib/docker/aufs/mnt/*"

function exit_on_error() {
   echo $1
   exit 1
}

[ "$(whoami)" != "root" ] && exit_on_error "you must be root!!!"

docker ps -a -q | xargs docker rm -fv 2> /dev/null || echo -n
docker images -q | xargs docker rmi -f 2> /dev/null || echo -n
service docker stop || exit_on_error "Failed to stop docker!!!"

for path in $DOCKER_PATH; do
    echo -n "Cleaning $path ... "
    rm -rf $path
    echo done
done
service docker start || exit_on_error "Failed to start docker!!!"
