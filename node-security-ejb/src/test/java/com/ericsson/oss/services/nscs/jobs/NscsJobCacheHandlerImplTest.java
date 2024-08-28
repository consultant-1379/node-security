package com.ericsson.oss.services.nscs.jobs;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.UUID;
import java.util.concurrent.locks.Lock;

import javax.cache.Cache;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.handler.command.utility.PasswordHelper;
import com.ericsson.nms.security.nscs.logger.NscsSystemRecorder;
import com.ericsson.oss.itpf.sdk.cluster.lock.LockManager;
import com.ericsson.oss.services.dto.JobStatusRecord;
import com.ericsson.oss.services.security.nscs.context.NscsContextService;

@RunWith(MockitoJUnitRunner.class)
public class NscsJobCacheHandlerImplTest {

    @Spy
    private Logger logger = LoggerFactory.getLogger(NscsJobCacheHandlerImplTest.class);

    @InjectMocks
    NscsJobCacheHandlerImpl beanUnderTest;

    @Mock
    Cache<UUID, JobStatusRecord> jobManagementCache;

    @Mock
    private NscsContextService ctxService;

    @Mock
    private LockManager cacheLockManager;

    @Mock
    private NscsSystemRecorder nscsSystemRecorder;

    @Mock
    private PasswordHelper passwordHelper;

    public static final String CACHE_NAME = "JobManagementCache";

    @Test
    public void insertJob() {
        Lock lock = Mockito.mock(Lock.class);
        when(ctxService.getUserIdContextValue()).thenReturn("administrator");
        when(ctxService.getCommandTextContextValue()).thenReturn(null);
        when(ctxService.getSourceIpAddrContextValue()).thenReturn(null);
        when(ctxService.getSessionIdContextValue()).thenReturn(null);
        when(ctxService.getNumInvalidItemsContextValue()).thenReturn(null);
        when(cacheLockManager.getDistributedLock(CACHE_NAME)).thenReturn(lock);
        JobStatusRecord record = beanUnderTest.insertJob(NscsCommandType.GET_JOB);
        assertTrue(record != null);
        assertTrue(record.getUserId() == "administrator");
        assertTrue(record.getCommandName() == null);
        assertTrue(record.getSourceIP() == null);
        assertTrue(record.getSessionId() == null);
        assertTrue(record.getNumOfInvalid() == -1);
    }

    @Test
    public void insertJobWithCompactAuditLogParamsInContext() {
        Lock lock = Mockito.mock(Lock.class);
        when(ctxService.getUserIdContextValue()).thenReturn("administrator");
        when(ctxService.getCommandTextContextValue()).thenReturn("secadm command");
        when(ctxService.getSourceIpAddrContextValue()).thenReturn("1.2.3.4");
        when(ctxService.getSessionIdContextValue()).thenReturn("session-id");
        when(ctxService.getNumInvalidItemsContextValue()).thenReturn(0);
        when(cacheLockManager.getDistributedLock(CACHE_NAME)).thenReturn(lock);
        when(passwordHelper.encryptEncode("session-id")).thenReturn("session-id");
        JobStatusRecord record = beanUnderTest.insertJob(NscsCommandType.GET_JOB);
        assertTrue(record != null);
        assertTrue(record.getUserId() == "administrator");
        assertTrue(record.getCommandName() == "secadm command");
        assertTrue(record.getSourceIP() == "1.2.3.4");
        assertTrue(record.getSessionId() == "session-id");
        assertTrue(record.getNumOfInvalid() == 0);
    }
}
