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

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.manager.NscsCommandManager;
import com.ericsson.nms.security.nscs.api.exception.CertificateReissueWfException;
import com.ericsson.nms.security.nscs.api.exception.NodeNotCertifiableException;
import com.ericsson.nms.security.nscs.api.exception.NodeNotSynchronizedException;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.api.pki.NodeEntityCategory;
import com.ericsson.nms.security.nscs.api.pki.NscsPkiEntitiesManagerException;
import com.ericsson.nms.security.nscs.api.pki.NscsPkiEntitiesManagerIF;
import com.ericsson.nms.security.nscs.pib.configuration.ConfigurationListener;
import com.ericsson.nms.security.nscs.utilities.XmlValidatorUtility;
import com.ericsson.oss.itpf.sdk.recording.SystemRecorder;
import com.ericsson.oss.itpf.security.pki.common.model.CertificateAuthority;
import com.ericsson.oss.itpf.security.pki.common.model.EntityInfo;
import com.ericsson.oss.itpf.security.pki.common.model.certificate.Certificate;
import com.ericsson.oss.itpf.security.pki.manager.model.EntityCategory;
import com.ericsson.oss.itpf.security.pki.manager.model.EntityType;
import com.ericsson.oss.itpf.security.pki.manager.model.entities.Entity;
import com.ericsson.oss.services.dto.JobStatusRecord;
import com.ericsson.oss.services.enums.JobGlobalStatusEnum;
import com.ericsson.oss.services.jobs.interfaces.NscsJobCacheHandler;

@RunWith(MockitoJUnitRunner.class)
public class CertificateReissueAutoTest {

    private static final byte[] EMPTY_BYTE_ARRAY = "".getBytes(Charset.forName("UTF-8"));
    private final String EMPTY_STRING = "";
    private final String IPSEC = "IPSEC";
    private final String OAM = "OAM";
    private final String INVALID_CERT_TYPE = "OEM";
    //private final String FILE_XML = "file:abc.xml";
    private final String FILE_XML = "file:";

    @Spy
    private final Logger logger = LoggerFactory.getLogger(CertificateIssueHandler.class);

    @Mock
    private SystemRecorder systemRecorder;

    @Mock
    private XmlValidatorUtility mockXmlUtility;

    @Mock
    private NscsCommandManager mockCommandManager;

    @Mock
    private NscsPkiEntitiesManagerIF mockNscsPkiManager;

    @Mock
    private ConfigurationListener configurationListener;

    @Mock
    private List<String> myErrListString;

    @Mock
    private NscsJobCacheHandler cacheHandler;

    @InjectMocks
    private CertificateReissueAuto testObj;

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
    public void testProcess_NoError() throws NscsServiceException, NscsPkiEntitiesManagerException {

        logger.info("testProcessPositive start");

        final String nodeName = "LTE01";
        final String entityName = nodeName + "-oam";
        final String categoryName = "NODE-OAM";
        final String issuerName = "EricssonCA";
        final String serialNumber = "12345678";

        Mockito.when(mockNscsPkiManager.isEntityNameAvailable(Mockito.anyString(), Mockito.eq(EntityType.CA_ENTITY))).thenReturn(false);

        final Entity entity = buildEntity(entityName, categoryName, issuerName, serialNumber);

        final List<Entity> entityList = new ArrayList<>();
        entityList.add(entity);

        Mockito.when(mockNscsPkiManager.getEntitiesByCategoryWithInvalidCertificate(Mockito.any(Date.class), Mockito.anyInt(),
                Mockito.eq(NodeEntityCategory.IPSEC), Mockito.eq(NodeEntityCategory.OAM))).thenReturn(entityList);
        //Mockito.when(mockNscsPkiManager.findNodeEntityCategory(Mockito.any(EntityCategory.class))).thenReturn(NodeEntityCategory.OAM);
        Mockito.when(mockCommandManager.isNodePresent(Mockito.anyString())).thenReturn(true);
        Mockito.when(mockCommandManager.validateNodesForCertificateReissue(Mockito.anyMap(), Mockito.anyList(), Mockito.anyMap(), Mockito.anyMap(), Mockito.anyMap())).thenReturn(true);
        Mockito.doNothing().when(mockCommandManager).executeCertificateReissueWfs(Mockito.anyMap(), Mockito.anyString(), Mockito.anyString(), Mockito.any(JobStatusRecord.class));
        Mockito.when(cacheHandler.insertJob((NscsCommandType) Mockito.any())).thenReturn(jobStatusRecord);
        testObj.process();
        logger.info("testProcessPositive end");
    }

    @Test(expected = CertificateReissueWfException.class)
    public void testProcess_NoErrorCertificateReissueWfException() throws NscsServiceException, NscsPkiEntitiesManagerException {

        logger.info("testProcessPositive start");

        final String nodeName = "LTE01";
        final String entityName = nodeName + "-oam";
        final String categoryName = "NODE-OAM";
        final String issuerName = "EricssonCA";
        final String serialNumber = "12345678";

        Mockito.when(mockNscsPkiManager.isEntityNameAvailable(Mockito.anyString(), Mockito.eq(EntityType.CA_ENTITY))).thenReturn(false);

        final Entity entity = buildEntity(entityName, categoryName, issuerName, serialNumber);

        final List<Entity> entityList = new ArrayList<>();
        entityList.add(entity);

        final NodeReference noderef = new NodeRef(nodeName);

        Mockito.when(mockNscsPkiManager.getEntitiesByCategoryWithInvalidCertificate(Mockito.any(Date.class), Mockito.anyInt(),
                Mockito.eq(NodeEntityCategory.IPSEC), Mockito.eq(NodeEntityCategory.OAM))).thenReturn(entityList);
        Mockito.when(mockCommandManager.isNodePresent(Mockito.anyString())).thenReturn(true);
        Mockito.when(mockCommandManager.validateNodesForCertificateReissue(Mockito.anyMap(), Mockito.anyList(), Mockito.anyMap(), Mockito.anyMap(),
                Mockito.anyMap())).thenAnswer(new Answer<Boolean>() {
                    @Override
                    public Boolean answer(final InvocationOnMock invocation) {
                        final Object[] args = invocation.getArguments();

                        ((Map<Entity, NodeReference>) args[2]).put(entity, noderef);

                        return true;
                    }
                });
        Mockito.doThrow(CertificateReissueWfException.class).when(mockCommandManager).executeCertificateReissueWfs(Mockito.anyMap(), Mockito.anyString(), Mockito.anyString(),
                Mockito.any(JobStatusRecord.class));
        Mockito.when(cacheHandler.insertJob((NscsCommandType) Mockito.any())).thenReturn(jobStatusRecord);
        testObj.process();
        logger.info("testProcessPositive end");
    }

    @Test
    public void testProcessNoEntityReturned() throws NscsServiceException, NscsPkiEntitiesManagerException {

        logger.info("testProcessPositiveNoEntityReturned start");
        Mockito.when(mockNscsPkiManager.isEntityNameAvailable(Mockito.anyString(), Mockito.eq(EntityType.CA_ENTITY))).thenReturn(false);

        final List<Entity> entityList = new ArrayList<>();

        Mockito.when(mockNscsPkiManager.getEntitiesByCategoryWithInvalidCertificate(Mockito.any(Date.class), Mockito.anyInt(),
                Mockito.eq(NodeEntityCategory.IPSEC), Mockito.eq(NodeEntityCategory.OAM))).thenReturn(entityList);
        Mockito.when(mockCommandManager.isNodePresent(Mockito.anyString())).thenReturn(true);
        Mockito.when(mockCommandManager.validateNodesForCertificateReissue(Mockito.anyMap(), Mockito.anyList(), Mockito.anyMap(), Mockito.anyMap(), Mockito.anyMap())).thenReturn(true);
        Mockito.doNothing().when(mockCommandManager).executeCertificateReissueWfs(Mockito.anyMap(), Mockito.anyString(), Mockito.anyString(), Mockito.any(JobStatusRecord.class));
        testObj.process();
        logger.info("testProcessPositiveNoEntityReturned end");
    }

    @Test
    public void testProcess_BlockingError() throws NscsPkiEntitiesManagerException {
        logger.info("testProcess_BlockingError start");

        final String issuerName = "EricssonCA";

        final String nodeName1 = "LTE01";
        final String entityName1 = nodeName1 + "-oam";
        final String categoryName1 = "NODE-OAM";
        final String serialNumber1 = "12345678";

        final String nodeName2 = "LTE01";
        final String entityName2 = nodeName1 + "-oam";
        final String categoryName2 = "NODE-IPSEC";
        final String serialNumber2 = "12345678";

        Mockito.when(mockNscsPkiManager.isEntityNameAvailable(Mockito.anyString(), Mockito.eq(EntityType.CA_ENTITY))).thenReturn(false);

        final Entity entity1 = buildEntity(entityName1, categoryName1, issuerName, serialNumber1);
        final Entity entity2 = buildEntity(entityName2, categoryName2, issuerName, serialNumber2);

        final List<Entity> entityList = new ArrayList<>();
        entityList.add(entity1);
        entityList.add(entity2);

        Mockito.when(mockNscsPkiManager.getEntitiesByCategoryWithInvalidCertificate(Mockito.any(Date.class), Mockito.anyInt(),
                Mockito.eq(NodeEntityCategory.IPSEC), Mockito.eq(NodeEntityCategory.OAM))).thenReturn(entityList);

        final NodeReference noderef = new NodeRef(nodeName1);

        Mockito.when(mockCommandManager.validateNodesForCertificateReissue(Mockito.anyMap(), Mockito.anyList(), Mockito.anyMap(), Mockito.anyMap(),
                Mockito.anyMap())).thenAnswer(new Answer<Boolean>() {
                    @Override
                    public Boolean answer(final InvocationOnMock invocation) {
                        final Object[] args = invocation.getArguments();

                        ((Map<Entity, NodeReference>) args[2]).put(entity1, noderef);
                        ((Map<Entity, NscsServiceException>) args[3]).put(entity2, new NodeNotSynchronizedException());

                        return false;
                    }
                });

        Mockito.doNothing().when(mockCommandManager).executeCertificateReissueWfs(Mockito.anyMap(), Mockito.anyString(), Mockito.anyString(), Mockito.any(JobStatusRecord.class));

        //set the command type
        Mockito.when(cacheHandler.insertJob((NscsCommandType) Mockito.any())).thenReturn(jobStatusRecord);

        testObj.process();
        logger.info("testProcess_BlockingError end");
    }

    @Test
    public void testProcess_NoBlockingError() throws NscsPkiEntitiesManagerException {
        logger.info("testProcess_NoBlockingError start");

        final String issuerName = "EricssonCA";

        final String nodeName1 = "LTE01";
        final String entityName1 = nodeName1 + "-oam";
        final String categoryName1 = "NODE-OAM";
        final String serialNumber1 = "12345678";

        final String nodeName2 = "LTE01";
        final String entityName2 = nodeName2 + "-oam";
        final String categoryName2 = "NODE-IPSEC";
        final String serialNumber2 = "12345678";

        Mockito.when(mockNscsPkiManager.isEntityNameAvailable(Mockito.anyString(), Mockito.eq(EntityType.CA_ENTITY))).thenReturn(false);

        final Entity entity1 = buildEntity(entityName1, categoryName1, issuerName, serialNumber1);
        final Entity entity2 = buildEntity(entityName2, categoryName2, issuerName, serialNumber2);

        final List<Entity> entityList = new ArrayList<>();
        entityList.add(entity1);
        entityList.add(entity2);

        Mockito.when(mockNscsPkiManager.getEntitiesByCategoryWithInvalidCertificate(Mockito.any(Date.class), Mockito.anyInt(),
                Mockito.eq(NodeEntityCategory.IPSEC), Mockito.eq(NodeEntityCategory.OAM))).thenReturn(entityList);

        final NodeReference noderef1 = new NodeRef(nodeName1);
        final NodeReference noderef2 = new NodeRef(nodeName2);

        Mockito.when(mockCommandManager.validateNodesForCertificateReissue(Mockito.anyMap(), Mockito.anyList(), Mockito.anyMap(), Mockito.anyMap(),
                Mockito.anyMap())).thenAnswer(new Answer<Boolean>() {
                    @Override
                    public Boolean answer(final InvocationOnMock invocation) {
                        final Object[] args = invocation.getArguments();

                        ((Map<Entity, NodeReference>) args[2]).put(entity1, noderef1);
                        ((Map<Entity, NscsServiceException>) args[3]).put(entity2, new NodeNotCertifiableException());
                        final NodeNotSynchronizedException exc = new NodeNotSynchronizedException();
                        ((Map<String, String[]>) args[4]).put(noderef2.getName(), new String[] { "" + exc.getErrorCode(), exc.getMessage() });
                        logger.error("Node [{}] associated to entity [{}] has non blicking validation problem. Exception is [{}]", noderef2.getFdn(),
                                noderef2, exc.getMessage());

                        return false;
                    }
                });
        Mockito.doNothing().when(mockCommandManager).executeCertificateReissueWfs(Mockito.anyMap(), Mockito.anyString(), Mockito.anyString(), Mockito.any(JobStatusRecord.class));
        Mockito.when(cacheHandler.insertJob((NscsCommandType) Mockito.any())).thenReturn(jobStatusRecord);
        testObj.process();
        logger.info("testProcess_NoBlockingError end");
    }

    @Test(expected = CertificateReissueWfException.class)
    public void testProcess_NoBlockingErrorException() throws NscsPkiEntitiesManagerException {
        logger.info("testProcess_NoBlockingError start");

        final String issuerName = "EricssonCA";

        final String nodeName1 = "LTE01";
        final String entityName1 = nodeName1 + "-oam";
        final String categoryName1 = "NODE-OAM";
        final String serialNumber1 = "12345678";

        final String nodeName2 = "LTE01";
        final String entityName2 = nodeName1 + "-oam";
        final String categoryName2 = "NODE-IPSEC";
        final String serialNumber2 = "12345678";

        Mockito.when(mockNscsPkiManager.isEntityNameAvailable(Mockito.anyString(), Mockito.eq(EntityType.CA_ENTITY))).thenReturn(false);

        final Entity entity1 = buildEntity(entityName1, categoryName1, issuerName, serialNumber1);
        final Entity entity2 = buildEntity(entityName2, categoryName2, issuerName, serialNumber2);

        final List<Entity> entityList = new ArrayList<>();
        entityList.add(entity1);
        entityList.add(entity2);

        Mockito.when(mockNscsPkiManager.getEntitiesByCategoryWithInvalidCertificate(Mockito.any(Date.class), Mockito.anyInt(),
                Mockito.eq(NodeEntityCategory.IPSEC), Mockito.eq(NodeEntityCategory.OAM))).thenReturn(entityList);

        final NodeReference noderef = new NodeRef(nodeName1);

        Mockito.when(mockCommandManager.validateNodesForCertificateReissue(Mockito.anyMap(), Mockito.anyList(), Mockito.anyMap(), Mockito.anyMap(),
                Mockito.anyMap())).thenAnswer(new Answer<Boolean>() {
                    @Override
                    public Boolean answer(final InvocationOnMock invocation) {
                        final Object[] args = invocation.getArguments();

                        ((Map<Entity, NodeReference>) args[2]).put(entity1, noderef);
                        ((Map<Entity, NscsServiceException>) args[3]).put(entity2, new NodeNotCertifiableException());
                        ((Map<Entity, NscsServiceException>) args[4]).put(entity2, new NodeNotCertifiableException());

                        return false;
                    }
                });
        Mockito.doThrow(CertificateReissueWfException.class).when(mockCommandManager).executeCertificateReissueWfs(Mockito.anyMap(), Mockito.anyString(), Mockito.anyString(),
                Mockito.any(JobStatusRecord.class));
        Mockito.when(cacheHandler.insertJob((NscsCommandType) Mockito.any())).thenReturn(jobStatusRecord);
        testObj.process();
        logger.info("testProcess_NoBlockingError with exception end");
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
