netsimScript="/opt/ericsson/enmutils/bin/netsim"
echo "CHECK INSTALL"
if ! [ -e $netsimScript ] ; then 
  nexus='https://arm1s11-eiffel004.eiffel.gic.ericsson.se:8443/nexus';
  gr='com.ericsson.dms.torutility';
  art='ERICtorutilitiesinternal_CXP9030579';
  ver=`/usr/bin/repoquery -a --repoid=ms_repo --qf "%{version}" ERICtorutilities_CXP9030570`;
  wget -O $art-$ver.rpm "$nexus/service/local/artifact/maven/redirect?r=releases&g=${gr}&a=${art}&v=${ver}&e=rpm"
  yum -y install $art-$ver.rpm
fi

