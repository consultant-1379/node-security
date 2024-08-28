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
package com.ericsson.nms.security.nscs.parser.service;

import java.util.Arrays;

import org.junit.Test;

import com.ericsson.nms.security.nscs.api.command.NscsCommandType;

public class ParserCredPostitiveTest extends AbstractParserTest {

    @Test
    public void testCREATE_CREDENTIALS() {
        assertValidCredCommand(
                "credentials create --rootusername ru --rootuserpassword rp --secureusername su --secureuserpassword sp --normalusername nu --normaluserpassword np --nodelist node1,node2",
                NscsCommandType.CREATE_CREDENTIALS, Arrays.asList("node1", "node2"), "ru", "rp", "su", "sp", "nu", "np", null, null, null, null);

        assertValidCredCommand("credentials create --rootusername ru --rootuserpassword rp  --nodelist node1,node2",
                NscsCommandType.CREATE_CREDENTIALS, Arrays.asList("node1", "node2"), "ru", "rp", null, null, null, null, null, null, null, null);

        assertValidCredCommand(
                "credentials create --rootusername ru --rootuserpassword rp --secureusername su --secureuserpassword sp --normalusername nu --normaluserpassword np --nodelist node1,node2",
                NscsCommandType.CREATE_CREDENTIALS, Arrays.asList("node1", "node2"), "ru", "rp", "su", "sp", "nu", "np", null, null, null, null);

        assertValidCredCommand("credentials create -rn value -rp val2 -sn val3 -sp val4 -nn  val5   -np  val6  -n node1,node2",
                NscsCommandType.CREATE_CREDENTIALS, Arrays.asList("node1", "node2"), "value", "val2", "val3", "val4", "val5", "val6", null, null,
                null, null);

        assertValidCredCommand("creds create -rn value -rp val2 -sn val3 -sp val4 -nn  val5   -np  val6  -n n-ode1-,node2-",
                NscsCommandType.CREATE_CREDENTIALS, Arrays.asList("n-ode1-", "node2-"), "value", "val2", "val3", "val4", "val5", "val6", null, null,
                null, null);

        assertValidCredCommand("creds create -rn value -rp val2 -sn val3 -sp val4 -nn  val5   -np  val6  -n node1-n-p,node2-xyz",
                NscsCommandType.CREATE_CREDENTIALS, Arrays.asList("node1-n-p", "node2-xyz"), "value", "val2", "val3", "val4", "val5", "val6", null,
                null, null, null);

        assertValidCredCommand("creds create -rn value -rp val2 -sn val3 -sp val4 -nn  val5   -np  val6  -n n--np-de1,node-2-xyz",
                NscsCommandType.CREATE_CREDENTIALS, Arrays.asList("n--np-de1", "node-2-xyz"), "value", "val2", "val3", "val4", "val5", "val6", null,
                null, null, null);

        assertValidCredCommand(
                "credentials create --rootusername value --rootuserpassword \"v$r1S%r~#\\\"Paw0R%!\"  --nodelist \"MeContext=node1\",node2 ",
                NscsCommandType.CREATE_CREDENTIALS, Arrays.asList("MeContext=node1", "node2"), "value", "v$r1S%r~#\"Paw0R%!", null, null, null, null,
                null, null, null, null);

        assertValidCredCommand("credentials create --rootusername value --rootuserpassword val2  --nodelist \"MeContext=node1\",\"MeContext=node2\" ",
                NscsCommandType.CREATE_CREDENTIALS, Arrays.asList("MeContext=node1", "MeContext=node2"), "value", "val2", null, null, null, null,
                null, null, null, null);

        assertValidCredCommand(
                "credentials create --rootusername value --rootuserpassword \"v$r1S%r~#\\\"Paw0R%!\"  --nodelist \"NetworkElement=node1\",node2 ",
                NscsCommandType.CREATE_CREDENTIALS, Arrays.asList("NetworkElement=node1", "node2"), "value", "v$r1S%r~#\"Paw0R%!", null, null, null,
                null, null, null, null, null);

    }

    @Test
    public void testUPDATE_CREDENTIALS() {
        assertValidCredCommand(
                "credentials update --rootusername value --rootuserpassword val2 --secureusername val3 --secureuserpassword val4 --normalusername  val5   --normaluserpassword val6  --nodelist node1,node2",
                NscsCommandType.UPDATE_CREDENTIALS, Arrays.asList("node1", "node2"), "value", "val2", "val3", "val4", "val5", "val6", null, null,
                null, null);
        assertValidCredCommand(
                "credentials update --rootusername value --rootuserpassword val2 --secureusername val3 --secureuserpassword val4 --normalusername  val5   --normaluserpassword val6  --nodelist NetworkElement=node1,MeContext=node2",
                NscsCommandType.UPDATE_CREDENTIALS, Arrays.asList("NetworkElement=node1", "MeContext=node2"), "value", "val2", "val3", "val4", "val5",
                "val6", null, null, null, null);
    }
}
