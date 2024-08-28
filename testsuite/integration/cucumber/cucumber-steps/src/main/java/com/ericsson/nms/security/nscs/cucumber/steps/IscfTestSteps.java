/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.cucumber.steps;

import com.ericsson.nms.security.nscs.api.enums.CertificateType;
import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.enums.SecurityLevel;
import com.ericsson.nms.security.nscs.api.iscf.IpsecArea;
import com.ericsson.nms.security.nscs.api.iscf.IscfResponse;
import com.ericsson.nms.security.nscs.api.iscf.NodeIdentifier;
import com.ericsson.nms.security.nscs.api.iscf.SecurityDataResponse;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameFormat;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameParam;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameStringType;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation.ModelIdentifierType;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.cucumber.helper.EServiceProducer;
import com.ericsson.nms.security.nscs.cucumber.helper.NodeModelDefs;
import com.ericsson.nms.security.nscs.cucumber.helper.NodeSecurityDataSetup;
import com.ericsson.oss.cucumber.arquillian.api.CucumberGlues;
import com.ericsson.oss.itpf.security.pki.common.model.EntityInfo;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.extension.SubjectAltNameField;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.extension.SubjectAltNameString;
import com.ericsson.oss.itpf.security.pki.manager.model.EntityType;
import com.ericsson.oss.itpf.security.pki.manager.model.ProfileType;
import com.ericsson.oss.itpf.security.pki.manager.model.entities.Entity;
import com.ericsson.oss.itpf.security.pki.manager.model.profiles.EntityProfile;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import cucumber.api.java.After;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.junit.Assert.fail;

/**
 *
 * @author epaocas
 */
@CucumberGlues
public class IscfTestSteps {

    private static final Logger logger = LoggerFactory.getLogger(IscfTestSteps.class);
    private final static String NODE_NAME = "MYNODE";
    private final static String ERBS_NODE_NAME = "ERBS_ISCF_NODE";
    private final static String CPP_NODE_TYPE = "ERBS";
    private final static String CPP_MIM_VERSION = "E.1.63";
    private final static String ECIM_MIM_VERSION = "E.1.63";
    private final static String OAM_ENTITY_SUFFIX = "-oam";
    private final static String IPSEC_ENTITY_SUFFIX = "-ipsec";
    private final static NodeModelInformation CPP_MODEL_INFO =
            new NodeModelInformation(CPP_MIM_VERSION, ModelIdentifierType.MIM_VERSION, CPP_NODE_TYPE);
    private IscfResponse iscfResponse = null;
    private SecurityDataResponse securityDataResponse = null;
    private final String nodeFdn = "NetworkElement=" +
                NodeSecurityDataSetup.networkElementNameFromMeContextName(NODE_NAME);
    private final String nodeFdnShared = "NetworkElement=" + ERBS_NODE_NAME;
    private final Set<String> entities = new HashSet<>();

    @Inject
    private NodeSecurityDataSetup dataSetup;

    @Inject
    EServiceProducer eserviceHolder;


    @Given("^CPP node created on DPS$")
    public void givenIscfCreateNode() throws Exception {
        logger.info("**************Given CPP Node setup**********");
        dataSetup.deleteAllNodes();
        dataSetup.createNode(NODE_NAME, NodeModelDefs.SyncStatusValue.UNSYNCHRONIZED.name(),
                NodeSecurityDataSetup.IpAddressVersion.IPv4);
    }

    @When("^Generate ISCF OAM for CPP$")
    public void whenGenerateIscfOamFile() {
        logger.info("**************When Generate ISCF OAM File***********");
        iscfResponse = null;
        iscfResponse = eserviceHolder.getIscfService().generate("IllogicalName",
                NodeSecurityDataSetup.networkElementNameFromMeContextName(NODE_NAME), SecurityLevel.LEVEL_2, SecurityLevel.LEVEL_1,
                EnrollmentMode.SCEP, CPP_MODEL_INFO);
        entities.add(NODE_NAME + OAM_ENTITY_SUFFIX);
    }

    @When("^Generate ISCF OAM for CPP shared$")
    public void whenGenerateIscfOamFileShared() {
        logger.info("**************When Generate ISCF OAM File***********");
        final NodeReference nodeRef = new NodeRef(nodeFdnShared);
        iscfResponse = null;
        iscfResponse = eserviceHolder.getIscfService().generate("IllogicalName",
                nodeRef.getName(), SecurityLevel.LEVEL_2, SecurityLevel.LEVEL_1,
                EnrollmentMode.SCEP, CPP_MODEL_INFO);
        entities.add(nodeRef.getName() + OAM_ENTITY_SUFFIX);
    }

    @When("^Generate ISCF Combo for CPP shared$")
    public void whenGenerateIscfComboFileShared()  throws Exception {
        logger.info("**************When Generate ISCF Combo File***********");
        final NodeReference nodeRef = new NodeRef(nodeFdnShared);
        final Set<IpsecArea> ipsecAreas = new HashSet<>();
        ipsecAreas.add(IpsecArea.OM);
        ipsecAreas.add(IpsecArea.TRANSPORT);
        final SubjectAltNameParam subjectAltNameParam = new SubjectAltNameParam(SubjectAltNameFormat.IPV4,
                new SubjectAltNameStringType("127.0.0.1"));
        iscfResponse = null;
        iscfResponse = eserviceHolder.getIscfService().generate("IllogicalName",
                nodeRef.getName(), SecurityLevel.LEVEL_2, SecurityLevel.LEVEL_1,
                "Label", subjectAltNameParam, ipsecAreas, EnrollmentMode.SCEP, CPP_MODEL_INFO);
        entities.add(nodeRef.getName() + OAM_ENTITY_SUFFIX);
        entities.add(nodeRef.getName() + IPSEC_ENTITY_SUFFIX);
    }

    @When("^Generate ISCF Combo for CPP$")
    public void whenGenerateIscfComboFile()  throws Exception {
        logger.info("**************When Generate ISCF Combo File***********");
        final Set<IpsecArea> ipsecAreas = new HashSet<>();
        ipsecAreas.add(IpsecArea.OM);
        ipsecAreas.add(IpsecArea.TRANSPORT);
        final SubjectAltNameParam subjectAltNameParam = new SubjectAltNameParam(SubjectAltNameFormat.IPV4,
                new SubjectAltNameStringType("127.0.0.1"));
        final NodeReference nodeRef = new NodeRef(nodeFdn);
        //1.    generate Iscf files, Corba and Ipsec, for nodeFdn
        iscfResponse = null;
        iscfResponse = eserviceHolder.getIscfService().generate("AnotherIllogicalName", nodeRef.getName(), SecurityLevel.LEVEL_2,
                SecurityLevel.LEVEL_1, "Label", subjectAltNameParam, ipsecAreas, EnrollmentMode.SCEP, CPP_MODEL_INFO);
        entities.add(nodeRef.getName() + OAM_ENTITY_SUFFIX);
        entities.add(nodeRef.getName() + IPSEC_ENTITY_SUFFIX);
    }

    @Then("^ISCF file is not empty$")
    public void thenCheckIscfData()  throws Exception {
        logger.info("**************Then Check ISCF data***********");
        Assert.assertNotNull("Generated IscfResponse is null", iscfResponse);
        final byte[] xmlOutput = iscfResponse.getIscfContent();
        Assert.assertNotNull("byte[] returned from ISCFGenerator is null", xmlOutput);
        Assert.assertTrue("byte[] returned from ISCFGenerator is of length 0", xmlOutput.length > 0);
    }

    @Then("^End Entity \"(.*)\" exists in PKI with SAN type \"(.*)\" and value \"(.*)\"$")
    public void thenEndEntityExistsWithSan(String entityName, String sanType, String sanVal) throws Exception {
        if (eserviceHolder.getEntityManagementService().isEntityNameAvailable(entityName, EntityType.ENTITY))
            fail("End Entity not found");
        Entity foundEnt = getEndEntity(entityName);
        Assert.assertNotNull("Null End Entity", foundEnt);
        Assert.assertNotNull("Null End EntityInfo", foundEnt.getEntityInfo());
        if ((sanType != null) && (sanVal != null) && (!sanType.isEmpty()) && (!sanVal.isEmpty())) {
            Assert.assertNotNull("Null subjectAltName", foundEnt.getEntityInfo().getSubjectAltName());
            Assert.assertNotNull("Null subjectAltNameField", foundEnt.getEntityInfo().getSubjectAltName().getSubjectAltNameFields());
            Assert.assertFalse("Empty subjectAltName", foundEnt.getEntityInfo().getSubjectAltName().getSubjectAltNameFields().isEmpty());
            SubjectAltNameField sanField = foundEnt.getEntityInfo().getSubjectAltName().getSubjectAltNameFields().get(0);
            Assert.assertTrue("SAN type does not match", sanType.equals(sanField.getType().toString()));
            SubjectAltNameString sanString = (SubjectAltNameString)sanField.getValue();
            Assert.assertTrue("SAN value does not match", sanVal.equals(sanString.getValue()));
        }
        else {
            Assert.assertNull("subjectAltName is not null", foundEnt.getEntityInfo().getSubjectAltName());
        }
    }

    @When("^Generate Security Data OAM for \"(.*)\" node \"(.*)\" with: enrollment mode \"(.*)\", SAN type \"(.*)\" and value \"(.*)\"$")
    public void whenGenerateSecurityDataOamWithSANForEcimNode(String nodeType, String nodeName, String mode, String sanType,
                                        String sanVal) throws UnsupportedEncodingException {
        logger.info("**************When Generate Security Data OAM for ECIM node with SAN***********");
        final String nodeFdnEcim = "NetworkElement=" + nodeName;
        SubjectAltNameFormat sanFormat = null;
        SubjectAltNameParam subjectAltName = null;
        if ((sanType != null) && (!sanType.isEmpty())) {
            try {
                sanFormat = SubjectAltNameFormat.valueOf(sanType);
            } catch (IllegalArgumentException exc) {
                fail(exc.getMessage());
            }
            subjectAltName = new SubjectAltNameParam(sanFormat, new SubjectAltNameStringType(sanVal));
        }
        EnrollmentMode enrollMode = null;
        try {
            enrollMode = EnrollmentMode.valueOf(mode);
        } catch (IllegalArgumentException exc) {
            fail(exc.getMessage());
        }
        final NodeModelInformation EcimModelInfo
                = new NodeModelInformation(ECIM_MIM_VERSION, ModelIdentifierType.MIM_VERSION, nodeType);
        securityDataResponse = eserviceHolder.getIscfService()
                .generateSecurityDataOam(new NodeIdentifier(nodeFdnEcim, null), subjectAltName,
                                         enrollMode, EcimModelInfo);
        entities.add(nodeName + OAM_ENTITY_SUFFIX);
    }

    @When("^Generate Security Data IPSec for \"(.*)\" node \"(.*)\" with: enrollment mode \"(.*)\", SAN type \"(.*)\" and value \"(.*)\"$")
    public void whenGenerateSecurityDataIpsecForEcimNode(String nodeType, String nodeName, String mode, String sanType,
                                        String sanVal) throws UnsupportedEncodingException {
        logger.info("**************When Generate Security Data IPSec for ECIM node***********");
        final String nodeFdnEcim = "NetworkElement=" + nodeName;
        SubjectAltNameFormat sanFormat = null;
        SubjectAltNameParam subjectAltName = null;
        if ((sanType != null) && (!sanType.isEmpty())) {
            try {
                sanFormat = SubjectAltNameFormat.valueOf(sanType);
            } catch (IllegalArgumentException exc) {
                fail(exc.getMessage());
            }
            subjectAltName = new SubjectAltNameParam(sanFormat, new SubjectAltNameStringType(sanVal));
        }
        EnrollmentMode enrollMode = null;
        try {
            enrollMode = EnrollmentMode.valueOf(mode);
        } catch (IllegalArgumentException exc) {
            fail(exc.getMessage());
        }
        final NodeModelInformation EcimModelInfo
                = new NodeModelInformation(ECIM_MIM_VERSION, ModelIdentifierType.MIM_VERSION, nodeType);
        securityDataResponse = eserviceHolder.getIscfService()
                .generateSecurityDataIpsec(new NodeIdentifier(nodeFdnEcim, null), subjectAltName,
                                         enrollMode, EcimModelInfo);
        entities.add(nodeName + IPSEC_ENTITY_SUFFIX);
    }

    @When("^Generate Security Data Combo for \"(.*)\" node \"(.*)\" with: enrollment mode \"(.*)\", SAN type \"(.*)\" and value \"(.*)\"$")
    public void whenGenerateSecurityDataComboForEcimNode(String nodeType, String nodeName, String mode, String sanType,
                                        String sanVal) throws UnsupportedEncodingException {
        logger.info("**************When Generate Security Data Combo for ECIM node***********");
        final String nodeFdnEcim = "NetworkElement=" + nodeName;
        SubjectAltNameFormat sanFormat = null;
        SubjectAltNameParam subjectAltName = null;
        if ((sanType != null) && (!sanType.isEmpty())) {
            try {
                sanFormat = SubjectAltNameFormat.valueOf(sanType);
            } catch (IllegalArgumentException exc) {
                fail(exc.getMessage());
            }
            subjectAltName = new SubjectAltNameParam(sanFormat, new SubjectAltNameStringType(sanVal));
        }
        EnrollmentMode enrollMode = null;
        try {
            enrollMode = EnrollmentMode.valueOf(mode);
        } catch (IllegalArgumentException exc) {
            fail(exc.getMessage());
        }
        final NodeModelInformation EcimModelInfo
                = new NodeModelInformation(ECIM_MIM_VERSION, ModelIdentifierType.MIM_VERSION, nodeType);
        securityDataResponse = eserviceHolder.getIscfService()
                .generateSecurityDataCombo(new NodeIdentifier(nodeFdnEcim, null), subjectAltName,
                                         enrollMode, EcimModelInfo);
        entities.add(nodeName + OAM_ENTITY_SUFFIX);
        entities.add(nodeName + IPSEC_ENTITY_SUFFIX);
    }

    @When("^Update End Entity \"(.*)\" with profile \"(.*)\"$")
    public void updateEntityWithNewProfile(String entityName, String profileName)  throws Exception {
        logger.info("**************When Update End entity with profile***********");
        if ((entityName != null) && (profileName != null)) {
            EntityProfile ep = new EntityProfile();
            ep.setName(profileName);
            ep.setType(ProfileType.ENTITY_PROFILE);
            EntityProfile foundEp = eserviceHolder.getProfileManagementService().getProfile(ep);
            if (foundEp != null) {
                Entity endEntity = this.getEndEntity(entityName);
                if (endEntity != null) {
                    endEntity.setEntityProfile(foundEp);
                    eserviceHolder.getEntityManagementService().updateEntity_v1(endEntity);
                }
            }
        }
    }

    @Then("^Security Data contains Integration Info for Certificate Types$")
    public void thenCheckSecurityDataForCertTypes(List<String> certTypesList) throws Exception {
        logger.info("**************Then Check Security data***********");
        Assert.assertNotNull(securityDataResponse);
        List<SecurityDataResponse.SecurityDataContainer> secDataContList = securityDataResponse.getSecurityDataContainers();
        Assert.assertNotNull("Null SecurityDataContainer list", secDataContList);
        Assert.assertFalse("Empty SecurityDataContainer list",secDataContList.isEmpty());
        Assert.assertNotNull("Certificate types list is null", certTypesList);
        Assert.assertFalse("Certificate types list is empty", certTypesList.isEmpty());
        Assert.assertEquals(certTypesList.size(), secDataContList.size());
        SecurityDataResponse.SecurityDataContainer secDataCont;
        for (int index = 0; index < certTypesList.size(); index++) {
            secDataCont = secDataContList.get(index);
            Assert.assertNotNull("Null SecurityDataContainer", secDataCont);
            Assert.assertEquals("Wrong TrustCategoryType", CertificateType.valueOf(certTypesList.get(index)),
                                                           secDataCont.getTrustCategoryType());
            SecurityDataResponse.NodeCredentialData nodeCredData = secDataCont.getNodeCredentials();
            Assert.assertNotNull("Null NodeCredentialData", nodeCredData);
            Assert.assertNotNull("Null TrustCategoryData", secDataCont.getTrustCategories());
        }
    }

    @Then("^End Entity \"(.*)\" contains Entity Profile \"(.*)\"$")
    public void checkEntityProfile(String entityName, String profileName) throws Exception {
        logger.info("**************Then check End entity profile***********");
        if ((entityName != null) && (profileName != null)) {
            Entity endEntity = this.getEndEntity(entityName);
            Assert.assertNotNull("Null End Entity", endEntity);
            Assert.assertNotNull("Null Entity Profile", endEntity.getEntityProfile());
            Assert.assertEquals(profileName, endEntity.getEntityProfile().getName());
        }
    }

    @After(value = {"@ISCF"}, order = 1)
    public void removePkiEntities() {
        for (String entityName : entities) {
            if (entityName.endsWith(OAM_ENTITY_SUFFIX) || entityName.endsWith(IPSEC_ENTITY_SUFFIX)) {
                eserviceHolder.getIscfService().cancel(entityName);
            }
        }
        entities.clear();
        securityDataResponse = null;
    }

    private Entity getEndEntity(final String entityName)  throws Exception {
        if (eserviceHolder.getEntityManagementService().isEntityNameAvailable(entityName, EntityType.ENTITY))
            return null;
        Entity ent = new Entity();
        EntityInfo entInfo = new EntityInfo();
        entInfo.setName(entityName);
        ent.setType(EntityType.ENTITY);
        ent.setEntityInfo(entInfo);
        return eserviceHolder.getEntityManagementService().getEntity(ent);
    }

}
