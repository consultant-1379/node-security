netsimPatchesDirectory="patches"

if [ -e /tmp/$netsimPatchesDirectory ] ; then
  if [ -n "$(ls -A /tmp/$netsimPatchesDirectory)" ] ; then
    for filename in /tmp/$netsimPatchesDirectory/* ; do
      /netsim/inst/netsim_shell << EOF
.install patch $filename 
EOF
    done
  else
    echo "No patches to install"
  fi
fi

