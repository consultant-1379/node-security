package com.ericsson.nms.security.nscs.workflow.task.cpp.ssh;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.cpp.seclevel.util.SecurityLevelCommonUtils;
import com.ericsson.nms.security.nscs.data.ModelDefinition;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.NscsCMWriterService;
import com.ericsson.nms.security.nscs.data.NscsCMWriterService.WriterSpecificationBuilder;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.handler.command.impl.MockUtils;
import com.ericsson.nms.security.nscs.iscf.IscfConfigurationBean;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.oss.itpf.sdk.eventbus.model.EventSender;
import com.ericsson.oss.mediation.sec.model.SSHCommandJob;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.ssh.EnableSecureFileTransferClientModeTask;

/**
 * Unit test for EnableSecureFileTransferClientModeTaskHandler
 * @author emaynes
 */
@RunWith(MockitoJUnitRunner.class)
public class EnableSecureFileTransferClientModeTaskHandlerTest {

    public static final String NODE_NAME = "ERBS_001";
    private static final String FDN = "MeContext=" + NODE_NAME;

    @Mock
    private NscsLogger nscsLogger;
    
    @Mock
    private EventSender<SSHCommandJob> commandJobSender;

    @Mock
    private NscsCMReaderService readerService;
    
    @Mock
    SecurityLevelCommonUtils securityLevelCommonUtils;
    
    @Mock
    IscfConfigurationBean config;
    
    @Mock
    NscsCMWriterService writer;
    
    @Mock
    WriterSpecificationBuilder writerSpecificationBuilder;

    @InjectMocks
    private EnableSecureFileTransferClientModeTaskHandler handlerUnderTest;

    @Test
    public void handlerInvocationTest() {

        doReturn(MockUtils.createNormalizableNodeRef(NODE_NAME)).when(readerService).getNormalizableNodeReference(any(NodeReference.class));

        Mockito.when(config.getIscfLogonServerAddress()).thenReturn("Test");
        
        Mockito.when(securityLevelCommonUtils.getManagedElementDataFdn(Mockito.any(NormalizableNodeReference.class))).thenReturn("Test");
        
        Mockito.when(writer.withSpecification("Test")).thenReturn(writerSpecificationBuilder);
        
        Mockito.when(writerSpecificationBuilder.setAttribute(ModelDefinition.ManagedElementData.LOGON_SERVER_ADDRESS, "Test")).thenReturn(writerSpecificationBuilder);
        
        Mockito.doNothing().when(writerSpecificationBuilder).updateMO();
        
        final EnableSecureFileTransferClientModeTask task = new EnableSecureFileTransferClientModeTask(FDN);
        handlerUnderTest.processTask(task);

        verify(commandJobSender).send(any(SSHCommandJob.class));
    }
}
