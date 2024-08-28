#!/bin/bash
docker-compose kill
docker-compose rm -f
docker rmi $(docker images -q)
docker volume rm $(docker volume ls -f dangling=true -q)
