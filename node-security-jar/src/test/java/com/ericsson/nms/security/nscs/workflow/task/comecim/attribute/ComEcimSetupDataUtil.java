/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2020
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.workflow.task.comecim.attribute;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ericsson.nms.security.nscs.api.enums.AlgorithmKeys;
import com.ericsson.nms.security.nscs.data.ModelDefinition.EnrollmentServer;
import com.ericsson.oss.itpf.security.pki.common.model.Algorithm;
import com.ericsson.oss.itpf.security.pki.common.model.CertificateAuthority;
import com.ericsson.oss.itpf.security.pki.common.model.EntityInfo;
import com.ericsson.oss.itpf.security.pki.common.model.EntityStatus;
import com.ericsson.oss.itpf.security.pki.common.model.Subject;
import com.ericsson.oss.itpf.security.pki.common.model.SubjectField;
import com.ericsson.oss.itpf.security.pki.common.model.SubjectFieldType;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.extension.SubjectAltName;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.extension.SubjectAltNameField;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.extension.SubjectAltNameFieldType;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.extension.SubjectAltNameString;
import com.ericsson.oss.itpf.security.pki.manager.model.EntityType;
import com.ericsson.oss.itpf.security.pki.manager.model.entities.Entity;
import com.ericsson.oss.itpf.security.pki.manager.model.profiles.EntityProfile;
import com.ericsson.oss.services.cm.cmshared.dto.CmObject;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;

public class ComEcimSetupDataUtil {

    private static final String IPSEC_ENTITY_PROFILE_NAME = "MicroRBSIPSec_SAN_CHAIN_EP";
    private static final AlgorithmKeys ALGOKEYS = AlgorithmKeys.RSA_2048;

    public Entity createEntity(final String fdn) {
        final EntityInfo entityInfo = new EntityInfo();
        final CertificateAuthority certificateAuthority = new CertificateAuthority();
        certificateAuthority.setName("NE_OAM_CA");
        certificateAuthority.setIssuer(certificateAuthority);
        entityInfo.setName(fdn);
        entityInfo.setId(1);

        final SubjectAltNameField subjectAltNameField = new SubjectAltNameField();
        subjectAltNameField.setType(SubjectAltNameFieldType.IP_ADDRESS);

        final SubjectAltNameString subjectAltNameValueString = new SubjectAltNameString();
        subjectAltNameValueString.setValue("12.13.14.15");
        subjectAltNameField.setValue(subjectAltNameValueString);
        final List<SubjectAltNameField> subjectAltNameValueList = new ArrayList<>();
        subjectAltNameValueList.add(subjectAltNameField);

        final SubjectAltName subjectAltNameValues = new SubjectAltName();
        subjectAltNameValues.setSubjectAltNameFields(subjectAltNameValueList);

        final Subject subject = new Subject();

        final SubjectField subjectFieldCN = new SubjectField();
        subjectFieldCN.setType(SubjectFieldType.COMMON_NAME);
        subjectFieldCN.setValue(fdn);

        final List<SubjectField> entSubjectFieldList = new ArrayList<>();
        entSubjectFieldList.add(subjectFieldCN);
        subject.setSubjectFields(entSubjectFieldList);

        entityInfo.setSubject(subject);
        entityInfo.setSubjectAltName(subjectAltNameValues);
        entityInfo.setOTP("OTP");

        final Entity ee = new Entity();
        ee.setType(EntityType.ENTITY);
        final EntityProfile ep = new EntityProfile();
        ep.setActive(true);
        ep.setName(IPSEC_ENTITY_PROFILE_NAME);
        ee.setEntityProfile(ep);
        entityInfo.setStatus(EntityStatus.NEW);
        ee.setEntityInfo(entityInfo);
        entityInfo.setIssuer(certificateAuthority.getIssuer());

        final Algorithm keyGenerationAlgorithm = new Algorithm();
        keyGenerationAlgorithm.setKeySize((ALGOKEYS.getKeySize()));
        ee.setKeyGenerationAlgorithm(keyGenerationAlgorithm);

        return ee;

    }

    public CmResponse buildCmResponse(final String nodeName, final String attribute, final Object expectedValue) {
        return buildCmResponse(nodeName, attribute, expectedValue, true) ;
    }

    public CmResponse buildCmResponse(final String nodeName, final String attribute, final Object expectedValue, boolean isthere) {
        final CmResponse cmResponse = new CmResponse();
        final Map<String, Object> attributesMap = new HashMap<>();

        attributesMap.put(attribute, expectedValue);
        attributesMap.put("protocol", "CMP");
        attributesMap.put("uri", "uri");

        Map<String, Object> certificateContent = new HashMap<>();
        certificateContent.put("issuer", "CN=ENM_TEST_CA,DC=mx,DC=ATT,DC=com");
        certificateContent.put("serialNumber", "6645858043101664338");
        attributesMap.put("certificateContent", certificateContent);

        if(isthere){
        List<String> reservedByCategory = new ArrayList<>();
        reservedByCategory.add("CN=ENM_TEST_CA,DC=mx,DC=ATT,DC=com");
        attributesMap.put("reservedByCategory", reservedByCategory);
        }

        final Collection<CmObject> cmObjects = new ArrayList<>(1);
        final CmObject cmObject = new CmObject();
        cmObject.setAttributes(attributesMap);
        cmObject.setFdn("MeContext=" + nodeName);
        cmObjects.add(cmObject);

        cmResponse.setTargetedCmObjects(cmObjects);
        cmResponse.setStatusCode(0);
        cmResponse.setTargetedCmObjects(cmObjects);
        return cmResponse;

    }

}
