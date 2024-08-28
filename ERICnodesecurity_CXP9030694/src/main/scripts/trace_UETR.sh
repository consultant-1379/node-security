#!/bin/bash 

_this=$(basename $0)
_me="[$_this]"

GLOBAL_PROPERTIES_FILE=/ericsson/tor/data/global.properties
ENM_LOGIN_FILE=$HOME/.enm_login

HAPROXY_URL=`cat $GLOBAL_PROPERTIES_FILE | grep UI_PRE | cut -d= -f2`

_token_id=`cat $ENM_LOGIN_FILE`

function _usage() {
cat << EOF
   $_this: $1
   Usage: $_this <COMMAND> [<OPTIONS>]
   Try '${_this} --help' for more information.
EOF
}


function _log() {
   echo "$_me" "[INFO]: $1"
}


function _help() {
cat << EOF   

Usage: $_this [<OPTIONS>]
where:
  <OPTIONS>:
    -h, --help                                This help
    -f, --filename=<IMEI IMSI filename>       The name of IMSI or IMEI filename to anonymize
    -s, --salt=<salt>                         The name of the salt used to anonymize filename
EOF
}

_log "Start script ..."
while [ "$1" != "" ] ; do
   case $1 in
         -h | --help )
             _help
             exit 0
             ;;
         -f=* | --filename=* )
             _filename_attr=$1
             _filename=${_filename_attr#*=}
             ;;
         -s=* | --salt=* )
	     _salt_attr=$1	
             _salt=${_salt_attr#*=}
             ;;
         -* )
             _usage "invalid option '$1'"
             exit 1
             ;;
         * )
             case $_param_index in
                 0 )
                     _command=$1 
                     _param_index=$(($_param_index + 1))
                     ;;
                 * )
                     _usage "invalid command '$1'"
                     exit 1
                     ;;
             esac
             ;;
     esac
     shift
done

_log $_filename
_log $_salt

if [ -z $_salt ]; then
    _json="{\"filename\":\"${_filename}\"}"
    _url=https://${HAPROXY_URL}/node-security/gdpr/anonymizer/anonymize
else
    _json="{\"filename\":\"${_filename}\",\"salt\":\"${_salt}\"}"
    _url=https://${HAPROXY_URL}/node-security/gdpr/anonymizer/anonymizeWithSalt
fi

echo ${_json} | python -m json.tool


_anonymized_value=`curl -s --insecure -H "Content-Type: application/json"  --cookie "iPlanetDirectoryPro=${_token_id}" --request POST  -d ''${_json}'' ${_url}`

echo ""
echo "anonymized filename for $_filename is :$_anonymized_value"
echo ""

