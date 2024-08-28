#! /bin/bash

source docker-env-functions.sh

docker_print "UI_TEST=$UI_TEST"

if [ "$UI_TEST" = "true" ];then
   echo "Preparing for UI test..."
#   cp $DOCKER_RPMS_DIR/iso/additional_rpms_for_ui_test.ui $DOCKER_RPMS_DIR/iso/additional_rpms_for_ui_test.txt
#   cp $DOCKER_JBOSS_SCRIPTS_DIR/config_modcluster.ui $DOCKER_JBOSS_SCRIPTS_DIR/config_modcluster.cli
   
   #Install additional RPMs and configure jboss
   startup.sh -NSJ

   #cleanup
#   rm -rf $DOCKER_RPMS_DIR/iso/additional_rpms_for_ui_test.txt
#   rm -rf $DOCKER_JBOSS_SCRIPTS_DIR/config_modcluster.cli
fi

wait_postgres
wait_dps_integration
