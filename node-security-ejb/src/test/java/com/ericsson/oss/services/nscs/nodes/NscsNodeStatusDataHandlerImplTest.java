package com.ericsson.oss.services.nscs.nodes;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.concurrent.locks.Lock;

import javax.cache.Cache;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.DpsNodeLoader;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.moget.MOGetServiceFactory;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.utilities.NscsNodeUtility;
import com.ericsson.oss.itpf.sdk.cluster.lock.LockManager;
import com.ericsson.oss.services.cache.NodesConfigurationStatusRecord;

@RunWith(MockitoJUnitRunner.class)
public class NscsNodeStatusDataHandlerImplTest {

    public static final String CACHE_NAME = "NodeSecurityReplicatedCache";

    @Spy
    private Logger logger = LoggerFactory.getLogger(NscsNodesListHandlerImplTest.class);

    @InjectMocks
    NscsNodesCacheHandlerImpl instance;

    @Mock
    Cache<String, NodesConfigurationStatusRecord> nodeStatusDataCache;

    @InjectMocks
    NodesConfigurationStatusRecord record;

    @Mock
    NscsCMReaderService readerService;

    @Mock
    LockManager cacheLockManager;

    @Mock
    private NscsNodeUtility nodeUtility;

    @Mock
    private DpsNodeLoader loader;

    @Mock
    MOGetServiceFactory moGetServiceFactory;

    @Before
    public void setup() {

        record.setId(new Long(1));
        record.setIpaddress("1.1.1.1");
        record.setIpsecconfig("(Configuration 1)");
        record.setName("LTE0000001");
        record.setOperationalsecuritylevel("LEVEL_1");
        record.setSyncstatus("UNSYNCHRONIZED");
        record.setType("ERBS");

    }

    @Test
    public void attributeChangedTest() {
        final String nodeName = "LTE0000001";
        final NormalizableNodeReference normNode = Mockito.mock(NormalizableNodeReference.class);
        final Lock lock = Mockito.mock(Lock.class);

        //attributeChangedTest
        when(loader.getNormalizedNodeReference((NodeReference) Matchers.anyObject())).thenReturn(normNode);
        when(normNode.getName()).thenReturn(nodeName);
        when(cacheLockManager.getDistributedLock(CACHE_NAME)).thenReturn(lock);
        when(nodeStatusDataCache.get(nodeName)).thenReturn(record);
        when(nodeUtility.isSecurityLevelSupported(normNode)).thenReturn(true);
        record.setSyncstatus("SYNCRONIZED");
        instance.insertOrUpdateNode(nodeName, record);
        assertTrue(instance.getNode(record.getName()).getSyncstatus().equals("SYNCRONIZED"));

        when(nodeUtility.getIpsecConfig(normNode, record.getSyncstatus())).thenReturn("Configuration 2");
        record.setOperationalsecuritylevel("LEVEL_2");
        instance.insertOrUpdateNode(nodeName, record);
        assertTrue(instance.getNode(record.getName()).getOperationalsecuritylevel().equals("LEVEL_2"));

        record.setIpaddress("2.2.2.2");
        instance.insertOrUpdateNode(nodeName, record);
        assertTrue(instance.getNode(record.getName()).getIpaddress().equals("2.2.2.2"));

    }

}
