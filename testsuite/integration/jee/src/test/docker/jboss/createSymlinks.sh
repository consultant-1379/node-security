#!/bin/bash

if [[ "$#" -eq 3 ]]; then
  TARGET_FILE_DIR=$1
  NEW_SYMLINK_DIR=$2
  HOURS_OFFSET=$3
else
  echo Illegall number of args
  exit
fi

echo arg1 $TARGET_FILE_DIR
echo arg2 $NEW_SYMLINK_DIR

list=$(ls $TARGET_FILE_DIR)

echo $list

COUNT=0

for i in $list
do
  HOURS=$(($COUNT*$HOURS_OFFSET))
  TIMESTAMP=$(date -d "-$HOURS hour" +%Y%m%d%H%M)
  echo Creating symlink $NEW_SYMLINK_DIR/symlink$COUNT to file $TARGET_FILE_DIR/$i
  ln -s $TARGET_FILE_DIR/$i $NEW_SYMLINK_DIR/symlink$COUNT
  echo Setting timestamp of $NEW_SYMLINK_DIR/symlink$COUNT to $TIMESTAMP
  touch -mht ${TIMESTAMP} $NEW_SYMLINK_DIR/symlink$COUNT
  ((COUNT++))
done