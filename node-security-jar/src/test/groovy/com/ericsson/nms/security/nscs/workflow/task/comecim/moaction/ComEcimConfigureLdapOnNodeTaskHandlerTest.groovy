package com.ericsson.nms.security.nscs.workflow.task.comecim.moaction

import org.slf4j.Logger

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.nms.security.nscs.data.ModelDefinition
import com.ericsson.nms.security.nscs.data.NscsCMReaderService
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference
import com.ericsson.nms.security.nscs.utilities.NSCSComEcimNodeUtility
import com.ericsson.oss.itpf.datalayer.dps.DataPersistenceService
import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject
import com.ericsson.oss.itpf.datalayer.dps.stub.RuntimeConfigurableDps
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.comecim.moaction.ComEcimConfigureLdapOnNodeTask
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowParameterKeys
import com.ericsson.oss.services.security.nscs.utils.NodeDataSetup

class ComEcimConfigureLdapOnNodeTaskHandlerTest extends NodeDataSetup {

    private static final String LDAP_MO_FDN = "ManagedElement=LTE44dg2ERBS00001,SystemFunctions=1,SecM=1,UserManagement=1,LdapAuthenticationMethod=1,Ldap=1"
    private static final String TLS_MODE_KEY_UNDER_TEST = "LDAPS"
    private static final Boolean USE_TLS_KEY_UNDER_TEST = true
    private static final String USER_LABEL_KEY_UNDER_TEST = "test groovy"

    private static final String BIND_PASSWORD_UNDER_TEST = "testpassword"
    private static final String BIND_DN_UNDER_TEST = "cn=ProxyAccount_4,ou=Profiles,dc=apache,dc=com"
    private static final String BASE_DN_UNDER_TEST = "dc=apache,dc=com"
    private static final String LDAP_SERVER_PORT_UNDER_TEST = "1636"
    private static final String LDAP_IP_ADDRESS_UNDER_TEST = "10.10.10.10"
    private static final String FALLBACK_LDAP_IP_ADDRESS_UNDER_TEST = "10.10.10.20"

    @ObjectUnderTest
    ComEcimConfigureLdapOnNodeTaskHandler comEcimConfigureLdapOnNodeTaskHandler

    @MockedImplementation
    private Logger logger

    @MockedImplementation
    NSCSComEcimNodeUtility nscsComEcimNodeUtility;

    private RuntimeConfigurableDps runtimeConfigurableDps;
    private DataPersistenceService dataPersistenceService;
    private ComEcimConfigureLdapOnNodeTask comEcimConfigureLdapOnNodeTask = new ComEcimConfigureLdapOnNodeTask()
    private Map<String, Serializable> ldapWorkFlowContext = new HashMap<>()

    def setup(){
        runtimeConfigurableDps = cdiInjectorRule.getService(RuntimeConfigurableDps.class)
        dataPersistenceService = runtimeConfigurableDps.build()
        NscsCMReaderService.deploymentEnv = NscsCMReaderService.DeploymentEnvironment.PRODUCTION

        comEcimConfigureLdapOnNodeTask.parameters.put(ComEcimConfigureLdapOnNodeTask.NODE_FDN_PARAMETER, LDAP_MO_FDN)
        comEcimConfigureLdapOnNodeTask.parameters.put(ComEcimConfigureLdapOnNodeTask.TLS_MODE_KEY ,TLS_MODE_KEY_UNDER_TEST)
        comEcimConfigureLdapOnNodeTask.parameters.put(ComEcimConfigureLdapOnNodeTask.USE_TLS_KEY,USE_TLS_KEY_UNDER_TEST)
        comEcimConfigureLdapOnNodeTask.parameters.put(ComEcimConfigureLdapOnNodeTask.USER_LABEL_KEY, USER_LABEL_KEY_UNDER_TEST)

        ldapWorkFlowContext.put(WorkflowParameterKeys.BIND_PASSWORD.toString(), BIND_PASSWORD_UNDER_TEST)
        ldapWorkFlowContext.put(WorkflowParameterKeys.BIND_DN.toString(), BIND_DN_UNDER_TEST)
        ldapWorkFlowContext.put(WorkflowParameterKeys.BASE_DN.toString(), BASE_DN_UNDER_TEST)
        ldapWorkFlowContext.put(WorkflowParameterKeys.LDAP_SERVER_PORT.toString(), LDAP_SERVER_PORT_UNDER_TEST)
        ldapWorkFlowContext.put(WorkflowParameterKeys.LDAP_IP_ADDRESS.toString(), LDAP_IP_ADDRESS_UNDER_TEST)
        ldapWorkFlowContext.put(WorkflowParameterKeys.FALLBACK_LDAP_IP_ADDRESS.toString(), FALLBACK_LDAP_IP_ADDRESS_UNDER_TEST)

        comEcimConfigureLdapOnNodeTask.parameters.put("ldapWorkFlowContext",ldapWorkFlowContext)
    }

    def " test Process Task" () {
        given:
        createNodeWithManagedElement("RadioNode", null, "LTE44dg2ERBS00001", null, "ComTop")
        runtimeConfigurableDps.addManagedObject().withFdn(LDAP_MO_FDN).generateTree().target(targetPo).build()

        nscsComEcimNodeUtility.getLdapMoFdn(_ as NormalizableNodeReference) >> LDAP_MO_FDN
        when:
        comEcimConfigureLdapOnNodeTaskHandler.processTask(comEcimConfigureLdapOnNodeTask)
        then:
        ManagedObject mo = dataPersistenceService.getLiveBucket().findMoByFdn(LDAP_MO_FDN);
        Boolean useTls = mo.getAttribute(ModelDefinition.Ldap.USE_TLS)
        String port = mo.getAttribute(ModelDefinition.Ldap.LDAP_IP_ADDRESS)

        useTls == USE_TLS_KEY_UNDER_TEST && port.equals(LDAP_IP_ADDRESS_UNDER_TEST)
    }
}
