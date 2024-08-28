#!/bin/bash

file="/tmp/$1"
cat $file | (while read line 
do
if ! [ "$line" == "" ] ; then 
  distr="/opt/ericsson/enmutils/bin/cli_app '$line'"
  eval $distr >> /tmp/stdout.out 
fi
done)
