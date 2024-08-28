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
package com.ericsson.oss.services.nodes.mgmt;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.handler.command.utility.NscsNameMultipleValueResponseBuilder;
import com.ericsson.oss.services.cache.NodesConfigurationStatusRecord;
import com.ericsson.oss.services.nodes.dto.NodesFilterDTO;

@RunWith(MockitoJUnitRunner.class)
public class NodesFilterImplTest {

    @Spy
    private Logger logger = LoggerFactory.getLogger(NodesFilterImplTest.class);

    @Mock
    NodesConfigurationStatusRecord node;

    @InjectMocks
    NodesFilterImpl beanUnderTest;

    @Mock
    NodesFilterDTO dto;

    private List<String> allSecLevels;
    private List<String> allIpsecConfigs;

    public static final String SL2_ACTIVATION_IN_PROGRESS = NscsNameMultipleValueResponseBuilder.SL2_ACTIVATION_IN_PROGRESS;
    public static final String SL2_DEACTIVATION_IN_PROGRESS = NscsNameMultipleValueResponseBuilder.SL2_DEACTIVATION_IN_PROGRESS;

    @Test
    public void SLFail() {

        allIpsecConfigs = Arrays.asList("(Configuration 1)", "(Configuration 2)", "DISABLED", "UNKNOWN", "IPSEC_NOT_SUPPORTED");
        allSecLevels = Arrays.asList("SL2", "UNKNOWN", "NOT_SUPPORTED", "SL2_ACTIVATION_IN_PROGRESS", "SL2_DEACTIVATION_IN_PROGRESS");

        Mockito.when(dto.getSecurityLevel()).thenReturn(allSecLevels);
        Mockito.when(node.getOperationalsecuritylevel()).thenReturn("SL1");
        Assert.assertFalse(beanUnderTest.apply(node, dto));

    }

    @Test
    public void SLActivationSuccess() {

        allIpsecConfigs = Arrays.asList("(Configuration 1)", "(Configuration 2)", "DISABLED", "UNKNOWN", "IPSEC_NOT_SUPPORTED");
        allSecLevels = Arrays.asList("SL2", "UNKNOWN", "NOT_SUPPORTED", "SL2_ACTIVATION_IN_PROGRESS");

        Mockito.when(dto.getIpsecconfig()).thenReturn(allIpsecConfigs);
        Mockito.when(dto.getSecurityLevel()).thenReturn(allSecLevels);
        Mockito.when(node.getIpsecconfig()).thenReturn("UNKNOWN");
        Mockito.when(node.getOperationalsecuritylevel()).thenReturn(SL2_ACTIVATION_IN_PROGRESS);
        Assert.assertTrue(beanUnderTest.apply(node, dto));

    }

    @Test
    public void SLActivationFail() {

        allIpsecConfigs = Arrays.asList("(Configuration 1)", "(Configuration 2)", "DISABLED", "UNKNOWN", "IPSEC_NOT_SUPPORTED");
        allSecLevels = Arrays.asList("SL2", "UNKNOWN", "NOT_SUPPORTED", "SL2_DEACTIVATION_IN_PROGRESS");

        Mockito.when(dto.getSecurityLevel()).thenReturn(allSecLevels);
        Mockito.when(node.getOperationalsecuritylevel()).thenReturn(SL2_ACTIVATION_IN_PROGRESS);
        Assert.assertFalse(beanUnderTest.apply(node, dto));

    }

    @Test
    public void SLDeActivationSuccess() {

        allIpsecConfigs = Arrays.asList("(Configuration 1)", "(Configuration 2)", "DISABLED", "UNKNOWN", "IPSEC_NOT_SUPPORTED");
        allSecLevels = Arrays.asList("SL2", "UNKNOWN", "NOT_SUPPORTED", "SL2_DEACTIVATION_IN_PROGRESS");

        Mockito.when(dto.getIpsecconfig()).thenReturn(allIpsecConfigs);
        Mockito.when(dto.getSecurityLevel()).thenReturn(allSecLevels);
        Mockito.when(node.getIpsecconfig()).thenReturn("UNKNOWN");
        Mockito.when(node.getOperationalsecuritylevel()).thenReturn(SL2_DEACTIVATION_IN_PROGRESS);
        Assert.assertTrue(beanUnderTest.apply(node, dto));

    }

    @Test
    public void SLDeActivationFail() {

        allIpsecConfigs = Arrays.asList("(Configuration 1)", "(Configuration 2)", "DISABLED", "UNKNOWN", "IPSEC_NOT_SUPPORTED");
        allSecLevels = Arrays.asList("SL2", "UNKNOWN", "NOT_SUPPORTED", "SL2_ACTIVATION_IN_PROGRESS");

        Mockito.when(dto.getSecurityLevel()).thenReturn(allSecLevels);
        Mockito.when(node.getOperationalsecuritylevel()).thenReturn(SL2_DEACTIVATION_IN_PROGRESS);
        Assert.assertFalse(beanUnderTest.apply(node, dto));

    }

    //    @Test
    //    public void IPSecFail() {
    //
    //        allIpsecConfigs = Arrays.asList("(Configuration 2)", "DISABLED", "UNKNOWN", "IPSEC_NOT_SUPPORTED");
    //        allSecLevels = Arrays.asList("SL1", "SL2", "UNKNOWN", "NOT_SUPPORTED", "SL2_ACTIVATION_IN_PROGRESS", "SL2_DEACTIVATION_IN_PROGRESS");
    //
    //        Mockito.when(dto.getSecurityLevel()).thenReturn(allSecLevels);
    //        Mockito.when(dto.getIpsecconfig()).thenReturn(allIpsecConfigs);
    //        Mockito.when(node.getOperationalsecuritylevel()).thenReturn("SL1");
    //        Mockito.when(node.getOperationalsecuritylevel()).thenReturn("(Configuration 1)");
    //        Assert.assertFalse(beanUnderTest.apply(node, dto));
    //
    //    }

    //    @Test
    //    public void Test2() {
    //
    //        //allIpsecConfigs = Arrays.asList("(Configuration 1)", "(Configuration 2)", "DISABLED", "UNKNOWN", "IPSEC_NOT_SUPPORTED");
    //        allSecLevels = Arrays.asList("SL2", "UNKNOWN", "NOT_SUPPORTED", "SL2_ACTIVATION_IN_PROGRESS", "SL2_DEACTIVATION_IN_PROGRESS");
    //        Mockito.when(dto.getSecurityLevel()).thenReturn(allSecLevels);
    //        Mockito.when(node.getOperationalsecuritylevel()).thenReturn("SL1");
    //        Assert.assertFalse(beanUnderTest.apply(node, dto));
    //
    //    }

    @Test
    public void TestName1() {

        logger.info("Filter should return true for LTE03ERBS0%");
        allIpsecConfigs = Arrays.asList("(Configuration 1)", "(Configuration 2)", "DISABLED", "UNKNOWN", "IPSEC_NOT_SUPPORTED");
        allSecLevels = Arrays.asList("SL1", "SL2", "UNKNOWN", "NOT_SUPPORTED", "SL2_ACTIVATION_IN_PROGRESS", "SL2_DEACTIVATION_IN_PROGRESS");

        Mockito.when(dto.getSecurityLevel()).thenReturn(allSecLevels);
        Mockito.when(dto.getIpsecconfig()).thenReturn(allIpsecConfigs);
        Mockito.when(node.getOperationalsecuritylevel()).thenReturn("SL2");
        Mockito.when(node.getIpsecconfig()).thenReturn("(Configuration 1)");
        Mockito.when(node.getName()).thenReturn("LTE03ERBS0%");
        Assert.assertTrue(beanUnderTest.apply(node, dto));

    }

    @Test
    public void TestName2() {

        logger.info("Filter should return false for %TEST%");

        allIpsecConfigs = Arrays.asList("(Configuration 1)", "(Configuration 2)", "DISABLED", "UNKNOWN", "IPSEC_NOT_SUPPORTED");
        allSecLevels = Arrays.asList("SL1", "SL2", "UNKNOWN", "NOT_SUPPORTED", "SL2_ACTIVATION_IN_PROGRESS", "SL2_DEACTIVATION_IN_PROGRESS");
        Mockito.when(dto.getSecurityLevel()).thenReturn(allSecLevels);
        Mockito.when(dto.getIpsecconfig()).thenReturn(allIpsecConfigs);
        Mockito.when(dto.getName()).thenReturn("%TEST%");

        Mockito.when(node.getOperationalsecuritylevel()).thenReturn("SL2");
        Mockito.when(node.getIpsecconfig()).thenReturn("(Configuration 1)");
        Mockito.when(node.getName()).thenReturn("%TE03ERBS0%");
        Assert.assertFalse(beanUnderTest.apply(node, dto));
    }

}
