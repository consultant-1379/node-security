#! /bin/bash

source docker-env-functions.sh

install_rpms_from_nexus
install_rpms_from_iso
install_rpms_in_folder $DOCKER_RPMS_DIR
cleanup_deployment
copy_jboss_config

mkdir -p /ericsson/tor/data
touch /ericsson/tor/data/global.properties

wait_postgres

docker_print "Waiting for Model Deployment Service"
wait_model_deployment

docker_print "Waiting for NEO4J"
wait_neo4j

JBOSS_DEPLOYMENTS_DIR="${JBOSS_HOME}/standalone/deployments"
# Find neo4j ear in installation directory and copy to deployments folder
NEO_EAR_STAGING_FILE=$(find '/opt/ericsson/ERICdpsneo4j_CXP9032728' -type f -regex ".*dps-neo4j-ear-.*\.ear")
docker_print "Neo4j ear staging file: ${NEO_EAR_STAGING_FILE}"
NEO_EAR_FILE=$(basename $(echo ${NEO_EAR_STAGING_FILE} | sed 's/staging/ear/g'))
docker_print "Deploying ${NEO_EAR_FILE}"
cp -f "${NEO_EAR_STAGING_FILE}" "${JBOSS_DEPLOYMENTS_DIR}/${NEO_EAR_FILE}"
# Find neo4j jca rar in installation directory and copy to deployments folder
NEO_RAR_STAGING_FILE=$(find '/opt/ericsson/ERICneo4jjca_CXP9032726' -type f -regex ".*neo4j-jca-rar-.*\.rar")
NEO_RAR_FILE=$(basename $(echo ${NEO_RAR_STAGING_FILE} | sed 's/staging/rar/g'))
docker_print "Deploying ${NEO_RAR_FILE}"
cp -f "${NEO_RAR_STAGING_FILE}" "${JBOSS_DEPLOYMENTS_DIR}/${NEO_RAR_FILE}"

#deploy_neo4j

LAAD_SIGNER_CERTIFICATE_FILE_PATH="/ericsson/cppaaservice/data/certs/"
LAAD_SIGNER_CERTIFICATE_TEMP_FILE="./tmp/onbuild-staging-area/config/resources/CppAAFileSignerKeyStore.p12"
if [ -e  $LAAD_SIGNER_CERTIFICATE_TEMP_FILE ]; then
	cp $LAAD_SIGNER_CERTIFICATE_TEMP_FILE $LAAD_SIGNER_CERTIFICATE_FILE_PATH
fi