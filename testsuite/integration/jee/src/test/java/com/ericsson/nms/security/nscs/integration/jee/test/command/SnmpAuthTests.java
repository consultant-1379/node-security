/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2015
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.integration.jee.test.command;

public interface SnmpAuthTests {

    void snmpAuthpriv() throws Exception;

    void snmpAuthnopriv() throws Exception;

    void miniLinkIndoorSnmpAuthPriv() throws Exception;

    void miniLinkIndoorSnmpAuthNoPriv() throws Exception;

    void miniLinkCn210SnmpAuthPriv() throws Exception;

    void miniLinkCn210SnmpAuthNoPriv() throws Exception;

    void miniLinkCn510R1SnmpAuthPriv() throws Exception;

    void miniLinkCn510R1SnmpAuthNoPriv() throws Exception;

    void miniLinkCn510R2SnmpAuthPriv() throws Exception;

    void miniLinkCn510R2SnmpAuthNoPriv() throws Exception;

    void miniLinkCn810R1SnmpAuthPriv() throws Exception;

    void miniLinkCn810R1SnmpAuthNoPriv() throws Exception;

    void miniLinkCn810R2SnmpAuthPriv() throws Exception;

    void miniLinkCn810R2SnmpAuthNoPriv() throws Exception;

    void miniLink665xSnmpAuthNoPriv() throws Exception;

    void miniLink669xSnmpAuthNoPriv() throws Exception;

    void miniLinkMW2SnmpAuthNoPriv() throws Exception;

    void miniLink6352SnmpAuthPriv() throws Exception;

    void miniLink6352SnmpAuthNoPriv() throws Exception;

    void miniLink6351SnmpAuthPriv() throws Exception;

    void miniLink6351SnmpAuthNoPriv() throws Exception;

    void miniLink6366SnmpAuthPriv() throws Exception;

    void miniLink6366SnmpAuthNoPriv() throws Exception;

    void miniLinkPT2020SnmpAuthPriv() throws Exception;

    void miniLinkPT2020SnmpAuthNoPriv() throws Exception;

    void switch6391SnmpAuthPriv() throws Exception;

    void switch6391SnmpAuthNoPriv() throws Exception;

    void fronthaul6392SnmpAuthPriv() throws Exception;

    void fronthaul6392SnmpAuthNoPriv() throws Exception;

    void ciscoAsr9000SnmpAuthPriv() throws Exception;

    void ciscoAsr9000SnmpAuthNoPriv() throws Exception;

    void ciscoAsr900SnmpAuthPriv() throws Exception;

    void ciscoAsr900SnmpAuthNoPriv() throws Exception;

    void juniperMxSnmpAuthPriv() throws Exception;

    void juniperMxSnmpAuthNoPriv() throws Exception;

}
