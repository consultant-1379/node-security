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
package com.ericsson.nms.security.nscs.handler.command.impl;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;

import com.ericsson.cds.cdi.support.rule.CdiInjectorRule;
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest;
import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse.NscsCommandResponseType;
import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.types.GetSnmpCommand;
import com.ericsson.nms.security.nscs.api.command.types.NscsNodeCommand;
import com.ericsson.nms.security.nscs.api.exception.NscsCapabilityModelException;

public class GetSnmpV3HandlerTest extends CommonHandlerFastCommit{
    @Rule
    public CdiInjectorRule cdiInjectorRule = new CdiInjectorRule(this);

    @ObjectUnderTest
    private GetSnmpv3Handler beanUnderTest;

    @Test
    public void testGetSNMP_Positive() throws Exception {
        final Map<String, Object> commandMap = new HashMap<String, Object>();
        setNodeContext("radioNode");
        final GetSnmpCommand getSNMPV3 = new GetSnmpCommand();
        getSNMPV3.setCommandType(NscsCommandType.GET_SNMP);
        commandMap.put(GetSnmpCommand.PLAIN_TEXT_PROPERTY, GetSnmpCommand.PLAIN_TEXT_SHOW);
        commandMap.put(NscsNodeCommand.NODE_LIST_PROPERTY, Arrays.asList("radioNode"));
        getSNMPV3.setProperties(commandMap);
        final NscsCommandResponse nscsResponse1 = beanUnderTest.process(getSNMPV3, commandContext);

        assertTrue(nscsResponse1.getResponseType() == NscsCommandResponseType.NAME_MULTIPLE_VALUE);

    }
 
    @Test
    public void testGetSNMP_Hide_Positive() throws Exception {
        final Map<String, Object> commandMap = new HashMap<String, Object>();
        setNodeContext("radioNode");
        final GetSnmpCommand getSNMPV3 = new GetSnmpCommand();
        getSNMPV3.setCommandType(NscsCommandType.GET_SNMP);
        commandMap.put(GetSnmpCommand.PLAIN_TEXT_PROPERTY, GetSnmpCommand.PLAIN_TEXT_HIDE);
        commandMap.put(NscsNodeCommand.NODE_LIST_PROPERTY, Arrays.asList("radioNode"));
        getSNMPV3.setProperties(commandMap);
        final NscsCommandResponse nscsResponse1 = beanUnderTest.process(getSNMPV3, commandContext);

        assertTrue(nscsResponse1.getResponseType() == NscsCommandResponseType.NAME_MULTIPLE_VALUE);

    }
    
    @Test
    public void testGetSNMP_Empty_Positive() throws Exception {
        final Map<String, Object> commandMap = new HashMap<String, Object>();
        setNodeContext("radioNode");
        final GetSnmpCommand getSNMPV3 = new GetSnmpCommand();
        getSNMPV3.setCommandType(NscsCommandType.GET_SNMP);
        commandMap.put(GetSnmpCommand.PLAIN_TEXT_PROPERTY, "");
        commandMap.put(NscsNodeCommand.NODE_LIST_PROPERTY, Arrays.asList("radioNode"));
        getSNMPV3.setProperties(commandMap);
        final NscsCommandResponse nscsResponse1 = beanUnderTest.process(getSNMPV3, commandContext);

        assertTrue(nscsResponse1.getResponseType() == NscsCommandResponseType.NAME_MULTIPLE_VALUE);

    }

    @Test(expected=NscsCapabilityModelException.class)
    public void testGetSNMP_Negative() throws Exception {
        final Map<String, Object> commandMap = new HashMap<String, Object>();
        setNodeContext("cppNode");
        final GetSnmpCommand getSNMPV3 = new GetSnmpCommand();
        getSNMPV3.setCommandType(NscsCommandType.GET_SNMP);
        commandMap.put(GetSnmpCommand.PLAIN_TEXT_PROPERTY, GetSnmpCommand.PLAIN_TEXT_SHOW);
        commandMap.put(NscsNodeCommand.NODE_LIST_PROPERTY, Arrays.asList("cppNode"));
        getSNMPV3.setProperties(commandMap);
        beanUnderTest.process(getSNMPV3, commandContext);
    }
}
