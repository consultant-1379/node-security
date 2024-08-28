/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.integration.jee.test.setup;

public class Dependencies extends IntegrationTestDependencies {

    // To deploy ear from Arquillian at runtime
	public static final String NODE_SECURITY_EAR = "com.ericsson.nms.security.nscs:node-security-ear:ear:?";
    public static final String PIB_EAR = "com.ericsson.oss.itpf.common:PlatformIntegrationBridge-ear:ear:?";
    public static final String SCRIPT_ENGINE_EAR = "com.ericsson.oss.services.cm:script-engine-ear:ear:?";
    public static final String CM_READER_EAR = "com.ericsson.oss.services.cm:cm-reader-ear:ear:?";
    public static final String CM_WRITER_EAR = "com.ericsson.oss.services.cm:cm-writer-ear:ear:?";
    public static final String WFS_EAR = "com.ericsson.oss.services.wfs:wfs-jee-ear:ear:?";
    public static final String SECURITY_WORKFLOWS_WAR = "com.ericsson.oss.services.security.workflow:security-workflows-war:war:?";
    public static final String MOCK_PKI_MANAGER_EAR = "com.ericsson.oss.itpf.security:mock-pki-manager-ear:ear:?";

    // For identitymgmt-mock & smrs-mock
    public static final String ID_MANAGER_SERVICE_API = "com.ericsson.nms.security:identitymgmtservices-api:jar:?";
    public static final String SMRS_SERVICE_API = "com.ericsson.nms.security:smrs-service-api:jar:?";

    // For nscs-test
    public static final String NODE_SECURITY_API = "com.ericsson.nms.security.nscs:node-security-api:jar:?";
    public static final String PKI_MANAGER_PROF_MAN_API = "com.ericsson.oss.itpf.security:pki-manager-profilemanagement-api:jar:?";
    public static final String PKI_MANAGER_CERT_MAN_API = "com.ericsson.oss.itpf.security:pki-manager-certificatemanagement-api:jar:?";
    public static final String PKI_MANAGER_COMMON_MODEL = "com.ericsson.oss.itpf.security:pki-manager-common-model:jar:?";
    public static final String PKI_COMMON_MODEL = "com.ericsson.oss.itpf.security:pki-common-model:jar:?";
    public static final String PKI_MANAGER_API = "com.ericsson.oss.itpf.security:pki-manager-api-jar:jar:?";
    public static final String MOCK_PKI_MANAGER_API = "com.ericsson.oss.itpf.security:mock-pki-manager-api:jar:?";
    public static final String NODE_SECURITY_MODEL_JAR = "com.ericsson.oss.services.model.security:nodesecuritymodel-jar:jar:?";
    public static final String NODE_SECURITY_JAR = "com.ericsson.nms.security.nscs:node-security-jar:jar:?";
    public static final String REST_EASY = "org.jboss.resteasy:resteasy-jaxrs:jar:?";
    public static final String WFS_REMOTE_API = "com.ericsson.oss.services.wfs:wfs-jee-remote-api:jar:?";
    public static final String KEY_GENERATOR_JAR = "com.ericsson.oss.itpf.security:key-management-jar:jar:?";
    public static final String BCPROC_LIBRARY_JAR = "org.bouncycastle:bcprov-jdk15on:jar:?";

    public static final String CIPHERS_RESOURCE_PATH = "src/test/resources/ciphersconfig";
    public static final String CRL_RESOURCE_PATH = "src/test/resources/crlcheck";
    public static final String ISSUE_RESOURCE_PATH = "src/test/resources/issue";
    public static final String LDAP_RESOURCE_PATH = "src/test/resources/ldap";

    public static final String VERSION_DPS = "VERSION_DPS";
    public static final String VERSION_NEO4J_JCA_RAR = "VERSION_NEO4J_JCA_RAR";
    public static final String TAG_VERSION = "$VERSION";
    public static final String NEO4J_JCA_RAR = "com.ericsson.oss.itpf.datalayer.dps.3pp:neo4j-jca-rar:rar:$VERSION";
    public static final String DPS_EAR_EAP7 = "com.ericsson.oss.itpf.datalayer.dps:dps-ear:ear:eap7:$VERSION";
    public static final String DPS_NEO4J_EAR_EAP7 = "com.ericsson.oss.itpf.datalayer.dps:dps-neo4j-ear:ear:eap7:$VERSION";
    public static final String DPS_TESTSUITE_MOCK_EJB = "com.ericsson.oss.itpf.datalayer.dps:dps-testsuite-mock-data-access-delegate-ejb:$VERSION";
}
