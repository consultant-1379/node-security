#!/bin/bash
_usage="$(basename "$0") [-h] -f filename [-t tag] [-e exception] -- program to check arquillian transactions

where:
    -h  show this help text
    -f  the mandatory arquillian log
    -t  the optional log tag
    -e  the optional exception"

_filename=
declare -a _tags=()
declare -a _exceptions=()

while getopts ':hf:t:e:' option; do
  case "$option" in
    h) echo "$_usage"
       exit
       ;;
    f) _filename=$OPTARG
       ;;
    t) _tags+=($OPTARG)
       ;;
    e) _exceptions+=($OPTARG)
       ;;
    :) printf "missing argument for -%s\n" "$OPTARG" >&2
       echo "$_usage" >&2
       exit 1
       ;;
   \?) printf "illegal option: -%s\n" "$OPTARG" >&2
       echo "$_usage" >&2
       exit 1
       ;;
  esac
done
shift $((OPTIND - 1))

if [ "$_filename" == "" ]; then
  printf "missing mandatory filename\n" >&2
  echo "$_usage" >&2
  exit 1
fi
if [ ! -f $_filename ]; then
  printf "%s: no such file or directory\n" $_filename >&2
  echo "$_usage" >&2
  exit 1
fi

if [ ${#_tags[@]} -le 0 ]; then
  _tags=("NSCS_ARQ_DATA_SETUP" "NSCS_ARQ_NODES_DATA_SETUP" "NSCS_ARQ_CPP_NODES_DATA_SETUP" "NSCS_ARQ_DG2_NODES_DATA_SETUP" "NSCS_ARQ_CPP_SECURITY_SERVICE")
fi

if [ ${#_exceptions[@]} -le 0 ]; then
  _exceptions=("IllegalStateException")
fi

echo

for _tag in "${_tags[@]}"
do
   echo "TAG: $_tag"
   echo "    Transaction begin"
   echo "        STARTED: "`grep -e $_tag $_filename | grep -e"\] transaction :" | grep begin | grep STARTED | wc -l`
   echo "        SUCCESS: "`grep -e $_tag $_filename | grep -e"\] transaction :" | grep begin | grep SUCCESS | wc -l`
   echo "        FAILED : "`grep -e $_tag $_filename | grep -e"\] transaction :" | grep begin | grep FAILED | wc -l`
   echo "        ERROR  : "`grep -e $_tag $_filename | grep -e"\] transaction :" | grep begin | grep ERROR | wc -l`
   echo "    Transaction commit"
   echo "        STARTED: "`grep -e $_tag $_filename | grep -e"\] transaction :" | grep commit | grep STARTED | wc -l`
   echo "        SUCCESS: "`grep -e $_tag $_filename | grep -e"\] transaction :" | grep commit | grep SUCCESS | wc -l`
   echo "        FAILED : "`grep -e $_tag $_filename | grep -e"\] transaction :" | grep commit | grep FAILED | wc -l`
   echo "        ERROR  : "`grep -e $_tag $_filename | grep -e"\] transaction :" | grep commit | grep ERROR | wc -l`
   echo "    Transaction rollback"
   echo "        STARTED: "`grep -e $_tag $_filename | grep -e"\] transaction :" | grep rollback | grep STARTED | wc -l`
   echo "        SUCCESS: "`grep -e $_tag $_filename | grep -e"\] transaction :" | grep rollback | grep SUCCESS | wc -l`
   echo "        FAILED : "`grep -e $_tag $_filename | grep -e"\] transaction :" | grep rollback | grep FAILED | wc -l`
   echo "        ERROR  : "`grep -e $_tag $_filename | grep -e"\] transaction :" | grep rollback | grep ERROR | wc -l`
done

echo

for _exception in "${_exceptions[@]}"
do
   echo "EXCEPTION: $_exception"
   echo "    OCCURS in tests: "`grep -e $_exception $_filename | grep -v -e"Caused by:" | grep -v -e "EJB default" | wc -l`
   echo "    OCCURS in EJBs : "`grep -e $_exception $_filename | grep -v -e"Caused by:" | grep -e "EJB default" | wc -l`
done

echo

exit 0
