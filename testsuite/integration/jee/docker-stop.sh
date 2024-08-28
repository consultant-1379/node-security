#! /bin/bash

source docker-functions.sh
 
exec docker-compose kill
exec docker-compose down -v

volumes=$(docker volume ls -qf dangling=true)
if [ ! -z "$volumes" ]; then
    exec docker volume rm ${volumes}
fi
