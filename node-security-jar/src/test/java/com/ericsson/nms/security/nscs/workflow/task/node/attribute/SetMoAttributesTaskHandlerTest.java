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
package com.ericsson.nms.security.nscs.workflow.task.node.attribute;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.nms.security.nscs.api.command.types.CiphersConfigCommand;
import com.ericsson.nms.security.nscs.api.exception.UnexpectedErrorException;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.NscsCMWriterService;
import com.ericsson.nms.security.nscs.data.NscsCMWriterService.WriterSpecificationBuilder;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.handler.ciphersconfig.utility.CiphersConstants;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.utilities.NscsNodeUtility;
import com.ericsson.oss.itpf.modeling.modelservice.typed.core.target.TargetTypeInformation;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.MissingMoException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.node.attribute.SetMoAttributesTask;

/**
 * Test Class for SetMoAttributesTaskHandlerTest
 *
 * @author xkumkam
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class SetMoAttributesTaskHandlerTest {

    @InjectMocks
    SetMoAttributesTaskHandler setMoAttributesTaskHandler;

    @Mock
    SetMoAttributesTask setMoAttributesTask;

    @Mock
    private NscsLogger nscsLogger;

    @Mock
    private NscsCMReaderService readerService;

    @Mock
    private NscsCMWriterService writerService;

    @Mock
    private NscsNodeUtility nscsNodeUtility;

    @Mock
    private NormalizableNodeReference radioNormNodeRef;

    @Mock
    WriterSpecificationBuilder writerSpecificationBuilder;

    private static final String RADIO_NODE_NAME = "RADIO-NODE-123";
    private static final String RADIO_FDN_WITHOUT_ME_CONTEXT = "ManagedElement=" + RADIO_NODE_NAME;
    private static final String RADIO_NODE_ROOT_FDN = String.format("ManagedElement=%s", RADIO_NODE_NAME);

    private final NormalizableNodeReference mockNormNode = new MockNormalizableNodeRef();
    private static final Map<String, Map<String, Object>> mosMap = new HashMap<String, Map<String, Object>>();
    private static final String MO_ATTRIBUTES_KEY_VALUES = "mOAttributesKey";

    /**
     * Test to set the TLS ciphers successfully on the node
     */
    @Test
    public void testProcessTask_TLSCiphersConfigured() {
        Map<String, Map<String, Object>> mosMap = new HashMap<String, Map<String, Object>>();
        mosMap = buildMosMap(CiphersConstants.PROTOCOL_TYPE_TLS);
        when(readerService.getNormalizableNodeReference(any(NodeReference.class))).thenReturn(mockNormNode);
        when(setMoAttributesTask.getNode()).thenReturn(radioNormNodeRef);
        when(radioNormNodeRef.getName()).thenReturn(RADIO_NODE_NAME);
        when(nscsNodeUtility.getSingleInstanceMoFdn(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(RADIO_FDN_WITHOUT_ME_CONTEXT);
        when(writerService.withSpecification()).thenReturn(writerSpecificationBuilder);
        when(setMoAttributesTask.getMoAttributes()).thenReturn(mosMap);
        final String result = setMoAttributesTaskHandler.processTask(setMoAttributesTask);

        assertEquals(result, "Mos_Configured");
    }

    /**
     * Test to set the TLS ciphers on the node throws UnexpectedErrorException in case of RuntimeException caught
     */
    @Test(expected = UnexpectedErrorException.class)
    public void testProcessTask_TLSCiphersNotConfigured() {
        Map<String, Map<String, Object>> mosMap = new HashMap<String, Map<String, Object>>();
        mosMap = buildMosMap(CiphersConstants.PROTOCOL_TYPE_TLS);
        when(readerService.getNormalizableNodeReference(any(NodeReference.class))).thenReturn(mockNormNode);
        when(setMoAttributesTask.getNode()).thenReturn(radioNormNodeRef);
        when(radioNormNodeRef.getName()).thenReturn(RADIO_NODE_NAME);
        when(nscsNodeUtility.getSingleInstanceMoFdn(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(RADIO_FDN_WITHOUT_ME_CONTEXT);
        when(writerService.withSpecification()).thenReturn(writerSpecificationBuilder);
        when(setMoAttributesTask.getMoAttributes()).thenReturn(mosMap);
        Mockito.doThrow(RuntimeException.class).when(writerSpecificationBuilder).updateMO();
        setMoAttributesTaskHandler.processTask(setMoAttributesTask);
    }

    /**
     * Test to set the SSH ciphers successfully on the node
     */
    @Test
    public void testProcessTask_SSHCiphersConfigured() {
        Map<String, Map<String, Object>> mosMap = new HashMap<String, Map<String, Object>>();
        mosMap = buildMosMap(CiphersConstants.PROTOCOL_TYPE_SSH);
        when(readerService.getNormalizableNodeReference(any(NodeReference.class))).thenReturn(mockNormNode);
        when(setMoAttributesTask.getNode()).thenReturn(radioNormNodeRef);
        when(radioNormNodeRef.getName()).thenReturn(RADIO_NODE_NAME);
        when(nscsNodeUtility.getSingleInstanceMoFdn(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(RADIO_FDN_WITHOUT_ME_CONTEXT);
        when(writerService.withSpecification()).thenReturn(writerSpecificationBuilder);
        when(setMoAttributesTask.getMoAttributes()).thenReturn(mosMap);
        final String result = setMoAttributesTaskHandler.processTask(setMoAttributesTask);

        assertEquals(result, "Mos_Configured");
    }

    /**
     * Negative test case to expect MissingMoException while setting the ciphers on the node
     */
    @Test(expected = MissingMoException.class)
    public void testProcessTask_MissingMoException() {
        Map<String, Map<String, Object>> mosMap = new HashMap<String, Map<String, Object>>();
        mosMap = buildMosMap(CiphersConstants.PROTOCOL_TYPE_SSH);
        when(readerService.getNormalizableNodeReference(any(NodeReference.class))).thenReturn(mockNormNode);
        when(setMoAttributesTask.getNode()).thenReturn(radioNormNodeRef);
        when(radioNormNodeRef.getName()).thenReturn(RADIO_NODE_NAME);
        when(writerService.withSpecification()).thenReturn(writerSpecificationBuilder);
        when(setMoAttributesTask.getMoAttributes()).thenReturn(mosMap);
        setMoAttributesTaskHandler.processTask(setMoAttributesTask);
    }

    private Map<String, Map<String, Object>> buildMosMap(final String protocolType) {
        final Map<String, Object> moAttrs = new HashMap<String, Object>();
        if (CiphersConstants.PROTOCOL_TYPE_TLS.equals(protocolType)) {
            moAttrs.put(CiphersConfigCommand.PROTOCOL_PROPERTY, CiphersConstants.PROTOCOL_TYPE_TLS);
            moAttrs.put(CiphersConfigCommand.CIPHER_FILTER_PROPERTY, "ALL:aRSA:SHA256");
            moAttrs.put(CiphersConfigCommand.NODE_LIST_PROPERTY, RADIO_NODE_NAME);
            mosMap.put(MO_ATTRIBUTES_KEY_VALUES, moAttrs);
        } else if (CiphersConstants.PROTOCOL_TYPE_SSH.equals(protocolType)) {
            moAttrs.put(CiphersConfigCommand.PROTOCOL_PROPERTY, CiphersConstants.PROTOCOL_TYPE_SSH);
            moAttrs.put(CiphersConfigCommand.ENCRYPT_ALGOS_PROPERTY, "3des-cbc,aes128-ctr");
            moAttrs.put(CiphersConfigCommand.KEX_PROPERTY, "diffie-hellman-group1-sha1");
            moAttrs.put(CiphersConfigCommand.MACS_PROPERTY, "hmac-sha1,hmac-sha2-256");
            moAttrs.put(CiphersConfigCommand.NODE_LIST_PROPERTY, RADIO_NODE_NAME);
            mosMap.put(MO_ATTRIBUTES_KEY_VALUES, moAttrs);
        }

        return mosMap;
    }

    private class MockNormalizableNodeRef implements NormalizableNodeReference {

        private static final long serialVersionUID = -3799671708615088019L;

        @Override
        public String getName() {
            return RADIO_NODE_NAME;
        }

        @Override
        public String getFdn() {
            return RADIO_NODE_ROOT_FDN;
        }

        @Override
        public boolean hasNormalizedRef() {
            return false;
        }

        @Override
        public NodeReference getNormalizedRef() {
            return null;
        }

        @Override
        public boolean hasNormalizableRef() {
            return false;
        }

        @Override
        public NodeReference getNormalizableRef() {
            return null;
        }

        @Override
        public String getTargetCategory() {
            return TargetTypeInformation.CATEGORY_NODE;
        }

        @Override
        public String getNeType() {
            return "RadioTNode";
        }

        @Override
        public String getOssModelIdentity() {
            return "";
        }
    }

}
