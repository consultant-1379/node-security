#!/bin/bash

# check if the value provided is number
checkNumber() {
 if ! [[ "$2" =~ ^[0-9]+$ ]] ; then 
   echo "ERROR: $1 value must be a number"
   exit
 fi
}

containsElement () {
  local e
  for e in "${@:2}"; do [[ "$e" == "$1" ]] && return 0; done
  return 1
}


