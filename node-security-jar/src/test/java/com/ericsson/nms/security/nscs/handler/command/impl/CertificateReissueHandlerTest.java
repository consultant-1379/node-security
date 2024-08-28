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
package com.ericsson.nms.security.nscs.handler.command.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.NscsMessageCommandResponse;
import com.ericsson.nms.security.nscs.api.command.manager.NscsCommandManager;
import com.ericsson.nms.security.nscs.api.command.types.CertificateReissueCommand;
import com.ericsson.nms.security.nscs.api.enums.RevocationReason;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.api.pki.NscsPkiEntitiesManagerException;
import com.ericsson.nms.security.nscs.api.pki.NscsPkiEntitiesManagerIF;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.utilities.NscsInputNodeRetrievalUtility;
import com.ericsson.nms.security.nscs.utilities.XmlValidatorUtility;
import com.ericsson.oss.itpf.security.pki.common.model.CertificateAuthority;
import com.ericsson.oss.itpf.security.pki.common.model.EntityInfo;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.Certificate;
import com.ericsson.oss.itpf.security.pki.manager.model.EntityCategory;
import com.ericsson.oss.itpf.security.pki.manager.model.EntityType;
import com.ericsson.oss.itpf.security.pki.manager.model.entities.Entity;
import com.ericsson.oss.services.dto.JobStatusRecord;
import com.ericsson.oss.services.enums.JobGlobalStatusEnum;
import com.ericsson.oss.services.jobs.interfaces.NscsJobCacheHandler;
import com.ericsson.oss.services.security.nscs.context.NscsContextService;

@RunWith(MockitoJUnitRunner.class)
public class CertificateReissueHandlerTest {

    @Spy
    private final Logger logger = LoggerFactory.getLogger(CertificateIssueHandler.class);

    @Mock
    private CommandContext mockCommandContext;

    @Mock
    private XmlValidatorUtility mockXmlUtility;

    @Mock
    private NscsCommandManager mockCommandManager;

    @Mock
    private NscsPkiEntitiesManagerIF mockNscsPkiManager;

    @Mock
    private List<String> myErrListString;

    @Mock
    private NscsJobCacheHandler cacheHandler;
    
    @Mock
    private NscsInputNodeRetrievalUtility nscsInputNodeRetrievalUtility;

    @InjectMocks
    private CertificateReissueHandler testObj;

    @Mock
    private NscsContextService nscsContextService;

    JobStatusRecord jobStatusRecord;

    @Before
    public void setup() {
        jobStatusRecord = new JobStatusRecord();
        UUID jobId = UUID.randomUUID();
        jobStatusRecord.setGlobalStatus(JobGlobalStatusEnum.PENDING);
        jobStatusRecord.setUserId("user");
        jobStatusRecord.setJobId(jobId);
    }

    @Test
    public void testProcessPositive_CA_SerialNumber() throws NscsPkiEntitiesManagerException {

        final String nodeName = "LTE01";
        final String entityName = nodeName + "-oam";
        final String categoryName = "NODE-OAM";
        final String issuerName = "EricssonCA";
        final String serialNumber = "12345678";
        final String hexSerialNumber = "bc614e";
        final String reason = RevocationReason.KEY_COMPROMISE.toString();
        final String certType = "OAM";

        final CertificateReissueCommand command = setupCertReissueCommandCASerialNumber(certType, issuerName, serialNumber, reason);

        Mockito.when(mockCommandManager.validateCertTypeValue(certType)).thenReturn(true);
        Mockito.when(mockCommandManager.validateReasonValue(Mockito.anyString())).thenReturn(true);
        Mockito.when(mockNscsPkiManager.isEntityNameAvailable(Mockito.anyString(), Mockito.eq(EntityType.CA_ENTITY))).thenReturn(false);

        final Entity entity = buildEntity(entityName, categoryName, issuerName, hexSerialNumber);

        final List<Entity> entityList = new ArrayList<>();
        entityList.add(entity);

        Mockito.when(mockNscsPkiManager.getEntityListByIssuerName(Mockito.anyString())).thenReturn(entityList);
        Mockito.when(mockCommandManager.isNodePresent(Mockito.anyString())).thenReturn(true);
        Mockito.when(mockCommandManager.validateNodesForCertificateReissue(Mockito.anyMap(), Mockito.anyList(), Mockito.anyMap(), Mockito.anyMap(), Mockito.anyMap())).thenReturn(true);
        Mockito.doNothing().when(mockCommandManager).executeCertificateReissueWfs(Mockito.anyMap(), Mockito.anyString(), Mockito.anyString(), Mockito.any(JobStatusRecord.class));
        Mockito.when(cacheHandler.insertJob((NscsCommandType) Mockito.any())).thenReturn(jobStatusRecord);
        final NscsCommandResponse response = testObj.process(command, mockCommandContext);
        Assert.assertNotNull("Response can't be null", response);
        Assert.assertEquals("Expecting workflow started successfully message.",
                CertificateReissueHandler.CERTIFICATE_REISSUE_EXECUTED + ". Perform 'secadm job get -j " + jobStatusRecord.getJobId().toString() + "' to get progress info.",
                ((NscsMessageCommandResponse) response).getMessage());
    }

    @Test
    public void testProcessPositive_CA_Nodelist() throws NscsPkiEntitiesManagerException {

        final String nodeName1 = "LTE01";
        final String entityName = nodeName1 + "-oam";
        final String categoryName = "NODE-OAM";
        final String issuerName = "EricssonCA";
        final String serialNumber = "12345678";
        final String reason = RevocationReason.KEY_COMPROMISE.toString();
        final String certType = "OAM";

        final CertificateReissueCommand command = setupCertReissueCommandCANodelist(certType, issuerName, nodeName1, reason);

        Mockito.when(mockCommandManager.validateCertTypeValue(certType)).thenReturn(true);
        Mockito.when(mockCommandManager.validateReasonValue(Mockito.anyString())).thenReturn(true);
        Mockito.when(mockNscsPkiManager.isEntityNameAvailable(Mockito.anyString(), Mockito.eq(EntityType.CA_ENTITY))).thenReturn(false);

        final Entity entity1 = buildEntity(entityName, categoryName, issuerName, serialNumber);

        final List<Entity> entityList = new ArrayList<>();
        entityList.add(entity1);

        Mockito.when(mockNscsPkiManager.getEntityListByIssuerName(Mockito.anyString())).thenReturn(entityList);

        final Set<String> duplicatedNodesSet = new HashSet<>();
        Mockito.when(mockCommandManager.validateDuplicatedNodes(Mockito.anyList())).thenReturn(duplicatedNodesSet);

        final List<NodeReference> inputNodes = new ArrayList<>();
        final NodeReference noderef = new NodeRef(nodeName1);
        inputNodes.add(noderef);
        final Map<Entity, NodeReference> validEntityNodesMap = new HashMap<>();
        final Map<NodeReference, NscsServiceException> blockingErrors = new HashMap<>();
        final Map<String, String[]> nonBlockingErrors = new HashMap<>();

        Mockito.when(mockCommandManager.validateNodesWithEntitiesForCertificateReissue(entityList, certType, inputNodes, validEntityNodesMap,
                blockingErrors, nonBlockingErrors)).thenAnswer(new Answer<Boolean>() {
                    @Override
                    public Boolean answer(final InvocationOnMock invocation) {
                        final Object[] args = invocation.getArguments();

                        ((Map<Entity, NodeReference>) args[3]).put(entity1, noderef);

                        return true;
                    }
                });
        Mockito.when(nscsInputNodeRetrievalUtility.getNodeReferenceList(command)).thenReturn(inputNodes);
        Mockito.doNothing().when(mockCommandManager).executeCertificateReissueWfs(Mockito.anyMap(), Mockito.anyString(), Mockito.anyString(), Mockito.any(JobStatusRecord.class));
        Mockito.when(cacheHandler.insertJob((NscsCommandType) Mockito.any())).thenReturn(jobStatusRecord);
        final NscsCommandResponse response = testObj.process(command, mockCommandContext);
        Assert.assertNotNull("Response can't be null", response);
        Assert.assertEquals("Expecting workflow started successfully message.",
                CertificateReissueHandler.CERTIFICATE_REISSUE_EXECUTED + ". Perform 'secadm job get -j " + jobStatusRecord.getJobId().toString() + "' to get progress info.",
                ((NscsMessageCommandResponse) response).getMessage());
    }

    @Test
    public void testProcessPositive_CA() throws NscsPkiEntitiesManagerException {

        final String nodeName = "LTE01";
        final String entityName = nodeName + "-oam";
        final String categoryName = "NODE-OAM";
        final String issuerName = "EricssonCA";
        final String serialNumber = "12345678";
        final String reason = RevocationReason.KEY_COMPROMISE.toString();
        final String certType = "OAM";

        final CertificateReissueCommand command = setupCertReissueCommandCA(certType, issuerName, reason);

        Mockito.when(mockCommandManager.validateCertTypeValue(certType)).thenReturn(true);
        Mockito.when(mockCommandManager.validateReasonValue(Mockito.anyString())).thenReturn(true);
        Mockito.when(mockNscsPkiManager.isEntityNameAvailable(Mockito.anyString(), Mockito.eq(EntityType.CA_ENTITY))).thenReturn(false);

        final Entity entity = buildEntity(entityName, categoryName, issuerName, serialNumber);

        final List<Entity> entityList = new ArrayList<>();
        entityList.add(entity);

        Mockito.when(mockNscsPkiManager.getEntityListByIssuerName(Mockito.anyString())).thenReturn(entityList);

        //		Mockito.when(mockCommandManager.validateEntitiesForCertificateReissue(Mockito.anyString(),
        //				Mockito.anyList(), Mockito.anyList(), Mockito.anyMap()))
        //				.thenReturn(true);
        Mockito.when(mockCommandManager.isNodePresent(Mockito.anyString())).thenReturn(true);
        Mockito.when(mockCommandManager.validateNodesForCertificateReissue(Mockito.anyMap(), Mockito.anyList(), Mockito.anyMap(), Mockito.anyMap(), Mockito.anyMap())).thenReturn(true);
        Mockito.doNothing().when(mockCommandManager).executeCertificateReissueWfs(Mockito.anyMap(), Mockito.anyString(), Mockito.anyString(), Mockito.any(JobStatusRecord.class));
        Mockito.when(cacheHandler.insertJob((NscsCommandType) Mockito.any())).thenReturn(jobStatusRecord);
        final NscsCommandResponse response = testObj.process(command, mockCommandContext);
        Assert.assertNotNull("Response can't be null", response);
        Assert.assertEquals("Expecting workflow started successfully message.",
                CertificateReissueHandler.CERTIFICATE_REISSUE_EXECUTED + ". Perform 'secadm job get -j " + jobStatusRecord.getJobId().toString() + "' to get progress info.",
                ((NscsMessageCommandResponse) response).getMessage());
    }

    @Test
    public void testProcessPositive_CertType_Nodelist() {

        final String nodeName1 = "LTE01";
        final String certType = "OAM";
        final String entityName = nodeName1 + "-oam";
        final String categoryName = "NODE-OAM";
        final String issuerName = "EricssonCA";
        final String serialNumber = "12345678";
        final String reason = RevocationReason.KEY_COMPROMISE.toString();

        final CertificateReissueCommand command = setupCertReissueCommandCertTypeNodelist(certType, nodeName1, reason);

        Mockito.when(mockCommandManager.validateCertTypeValue(certType)).thenReturn(true);
        Mockito.when(mockCommandManager.validateReasonValue(Mockito.anyString())).thenReturn(true);
        final Set<String> duplicatedNodesSet = new HashSet<>();
        Mockito.when(mockCommandManager.validateDuplicatedNodes(Mockito.anyList())).thenReturn(duplicatedNodesSet);
        Mockito.when(mockCommandManager.validateNodesWithEntitiesForCertificateReissue(Mockito.anyString(), Mockito.anyList(), Mockito.anyList(), Mockito.anyMap(), Mockito.anyMap()))
                .thenReturn(true);
        Mockito.doNothing().when(mockCommandManager).executeCertificateReissueWfs(Mockito.anyMap(), Mockito.anyString(), Mockito.anyString(), Mockito.any(JobStatusRecord.class));
        Mockito.when(cacheHandler.insertJob((NscsCommandType) Mockito.any())).thenReturn(jobStatusRecord);
        final NscsCommandResponse response = testObj.process(command, mockCommandContext);
        Assert.assertNotNull("Response can't be null", response);
        Assert.assertEquals("Expecting workflow started successfully message.",
                CertificateReissueHandler.CERTIFICATE_REISSUE_EXECUTED + ". Perform 'secadm job get -j " + jobStatusRecord.getJobId().toString() + "' to get progress info.",
                ((NscsMessageCommandResponse) response).getMessage());
    }

    @SuppressWarnings("serial")
    private CertificateReissueCommand setupCertReissueCommandCASerialNumber(final String certType, final String issuer, final String serialNumber,
                                                                            final String reason) {

        final CertificateReissueCommand command = new CertificateReissueCommand();
        command.setCommandType(NscsCommandType.CERTIFICATE_REISSUE);
        final Map<String, Object> commandMap = new HashMap<String, Object>() {
            {
                {
                    put(CertificateReissueCommand.CERT_TYPE_PROPERTY, certType);
                    put(CertificateReissueCommand.CA_PROPERTY, issuer);
                    put(CertificateReissueCommand.SERIAL_NUMBER_PROPERTY, serialNumber);
                    put(CertificateReissueCommand.REASON_PROPERTY, reason);
                }
            }
        };
        command.setProperties(commandMap);
        return command;
    }

    @SuppressWarnings("serial")
    private CertificateReissueCommand setupCertReissueCommandCANodelist(final String certType, final String issuer, final String inputNodes,
                                                                        final String reason) {

        final CertificateReissueCommand command = new CertificateReissueCommand();
        command.setCommandType(NscsCommandType.CERTIFICATE_REISSUE);
        final List<String> nodeList = new ArrayList<>();
        nodeList.add(inputNodes);
        final Map<String, Object> commandMap = new HashMap<String, Object>() {
            {
                {
                    put(CertificateReissueCommand.CERT_TYPE_PROPERTY, certType);
                    put(CertificateReissueCommand.CA_PROPERTY, issuer);
                    put(CertificateReissueCommand.NODE_LIST_PROPERTY, nodeList);
                    put(CertificateReissueCommand.REASON_PROPERTY, reason);
                }
            }
        };
        command.setProperties(commandMap);
        return command;
    }

    @SuppressWarnings("serial")
    private CertificateReissueCommand setupCertReissueCommandCA(final String certType, final String issuer, final String reason) {

        final CertificateReissueCommand command = new CertificateReissueCommand();
        command.setCommandType(NscsCommandType.CERTIFICATE_REISSUE);
        final Map<String, Object> commandMap = new HashMap<String, Object>() {
            {
                {
                    put(CertificateReissueCommand.CERT_TYPE_PROPERTY, certType);
                    put(CertificateReissueCommand.CA_PROPERTY, issuer);
                    put(CertificateReissueCommand.REASON_PROPERTY, reason);
                }
            }
        };
        command.setProperties(commandMap);
        return command;
    }

    @SuppressWarnings("serial")
    private CertificateReissueCommand setupCertReissueCommandCertTypeNodelist(final String certType, final String inputNodes, final String reason) {

        final CertificateReissueCommand command = new CertificateReissueCommand();
        command.setCommandType(NscsCommandType.CERTIFICATE_REISSUE);
        final List<String> nodeList = new ArrayList<>();
        nodeList.add(inputNodes);
        final Map<String, Object> commandMap = new HashMap<String, Object>() {
            {
                {
                    put(CertificateReissueCommand.CERT_TYPE_PROPERTY, certType);
                    put(CertificateReissueCommand.NODE_LIST_PROPERTY, nodeList);
                    put(CertificateReissueCommand.REASON_PROPERTY, reason);
                }
            }
        };
        command.setProperties(commandMap);
        return command;
    }

    private Entity buildEntity(final String entityName, final String categoryName, final String issuerName, final String serialNumber) {
        final Entity entity = new Entity();
        final EntityCategory entityCategory = new EntityCategory();
        entityCategory.setName(categoryName);
        entity.setCategory(entityCategory);
        final EntityInfo entityInfo = new EntityInfo();
        entityInfo.setName(entityName);
        final CertificateAuthority issuer = new CertificateAuthority();
        issuer.setName(issuerName);
        entityInfo.setIssuer(issuer);
        final Certificate activeCertificate = new Certificate();
        activeCertificate.setSerialNumber(serialNumber);
        entityInfo.setActiveCertificate(activeCertificate);
        entity.setEntityInfo(entityInfo);
        return entity;
    }

}
