/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ericsson.nms.security.nscs.cucumber.setup;

/**
 *
 * @author epaocas
 */
public class Dependencies {
    public static final String NODE_SECURITY_EAR = "com.ericsson.nms.security.nscs:node-security-ear:ear:?";
    public static final String NODE_SECURITY_API = "com.ericsson.nms.security.nscs:node-security-api:jar:?";
    public static final String NODE_SECURITY_WORKFLOW_API = "com.ericsson.nms.security.nscs:node-security-wf-api:jar:?";
    public static final String NODE_SECURITY_JAR = "com.ericsson.nms.security.nscs:node-security-jar:jar:?";
    public static final String NODE_GDPR_API = "com.ericsson.nms.security.nscs:node-gdpr-api:jar:?";

    public static final String NODE_SECURITY_MODEL_JAR = "com.ericsson.oss.services.model.security:nodesecuritymodel-jar:jar:?";
    public static final String FM_MODEL_JAR = "com.ericsson.oss.services.fm.models:fmprocessedeventmodel-jar:jar:?";

//    public static final String PKI_CORE_EAR = "com.ericsson.nms.security:pki-core-ear:ear";
//    public static final String PKI_CORE_API = "com.ericsson.nms.security:pki-core-api:jar:?";
    
    public static final String MOCK_PKI_MANAGER_EAR = "com.ericsson.oss.itpf.security:mock-pki-manager-ear:ear";
    public static final String MOCK_PKI_MANAGER_API = "com.ericsson.oss.itpf.security:mock-pki-manager-api:jar:?";
    public static final String PKI_MANAGER_PROF_MAN_API = "com.ericsson.oss.itpf.security:pki-manager-profilemanagement-api:jar:?";
    public static final String PKI_MANAGER_CERT_MAN_API = "com.ericsson.oss.itpf.security:pki-manager-certificatemanagement-api:jar:?";
    public static final String PKI_MANAGER_COMMON_MODEL = "com.ericsson.oss.itpf.security:pki-manager-common-model:jar:?";
    public static final String PKI_COMMON_MODEL = "com.ericsson.oss.itpf.security:pki-common-model:jar:?";
    public static final String PKI_MANAGER_API = "com.ericsson.oss.itpf.security:pki-manager-api-jar:jar:?";
    public static final String PKI_MANAGER_CONF_MAN_API = "com.ericsson.oss.itpf.security:pki-manager-configurationmanagement-api:jar:?";
    public static final String PKI_MANAGER_CRL_MAN_API = "com.ericsson.oss.itpf.security:pki-manager-crlmanagement-api:jar:?";

//    public static final String SMRS_SERVICE_EAR = "com.ericsson.nms.security:smrs-service-ear:ear";
    public static final String SMRS_SERVICE_API = "com.ericsson.nms.security:smrs-service-api:jar:?";
    public static final String TOPOLOGY_SERVICE_API = "com.ericsson.oss.services:topologyCollectionsService-api:jar:?";

    public static final String WFS_EAR = "com.ericsson.oss.services.wfs:wfs-jee-ear:ear";
    public static final String WFS_REMOTE_API = "com.ericsson.oss.services.wfs:wfs-jee-remote-api:jar:?";
    public static final String WFS_API = "com.ericsson.oss.services.wfs:wfs-api:jar:?";
    public static final String SECURITY_WORKFLOWS_WAR = "com.ericsson.oss.services.security.workflow:security-workflows-war:war";

    public static final String CM_READER_EAR = "com.ericsson.oss.services.cm:cm-reader-ear:ear";
    public static final String CM_READER_API_JAR = "com.ericsson.oss.services.cm:cm-reader-api:jar:?";
    
    public static final String CM_WRITER_EAR = "com.ericsson.oss.services.cm:cm-writer-ear:ear";
    public static final String CM_WRITER_API_JAR = "com.ericsson.oss.services.cm:cm-writer-api:jar:?";
    
    public static final String SCRIPT_ENGINE_EAR = "com.ericsson.oss.services.cm:script-engine-ear:ear";
    public static final String SCRIPT_ENGINE_EDITOR_SPI_JAR = "com.ericsson.oss.services.cm:script-engine-editor-spi:jar:?";
   
    public static final String PIB_EAR = "com.ericsson.oss.itpf.common:PlatformIntegrationBridge-ear:ear";
    public static final String REST_EASY = "org.jboss.resteasy:resteasy-jaxrs:jar:?";
    public static final String PLEXUS_UTILS = "org.codehaus.plexus.plexus-utils:plexus-utils-jar:jar:?";
     
    public static final String SDK_DIST = "com.ericsson.oss.itpf.sdk:service-framework-dist:jar:?";

    public static final String IAIK_JCE_JAR = "iaik:iaik_jce:jar:?";
    public static final String BC_PROV_JAR = "org.bouncycastle:bcprov-jdk16:jar:?";

    public static final String RS_API_JAR = "javax.ws.rs:javax.ws.rs-api:jar:?";

    public static final String ID_MANAGER_SERVICE_API = "com.ericsson.nms.security:identitymgmtservices-api:jar:?";//:jar:?";
    public static final String CORE_MEDIATION_API = "com.ericsson.nms.mediation:core-mediation-api:jar:?";

    public static final String KEY_GENERATOR_JAR = "com.ericsson.oss.itpf.security:key-management-jar:jar:?";
    //public static final String BC_LIBRARY_JAR = "org.bouncycastle:bcpkix-jdk15on:jar:?";
    public static final String BCPROC_LIBRARY_JAR = "org.bouncycastle:bcprov-jdk15on:jar:?";

    public static final String CIPHERS_RESOURCE_PATH = "src/test/resources/ciphersconfig";
    public static final String CRL_RESOURCE_PATH = "src/test/resources/crlcheck";
    public static final String ISSUE_RESOURCE_PATH = "src/test/resources/issue";
    public static final String LDAP_RESOURCE_PATH = "src/test/resources/ldap";
    
}
