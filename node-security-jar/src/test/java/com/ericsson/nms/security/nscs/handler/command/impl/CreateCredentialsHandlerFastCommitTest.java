package com.ericsson.nms.security.nscs.handler.command.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.ericsson.cds.cdi.support.rule.ObjectUnderTest;
import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.NscsMessageCommandResponse;
import com.ericsson.nms.security.nscs.api.command.types.CredentialsCommand;

/**
 * Tests the CreateCredentialsHandler that Creates a NetworkElementSecurity Mo associated to each of the specified nodes
 *
 * @see CreateCredentialsHandler
 *
 */

@RunWith(MockitoJUnitRunner.class)
public class CreateCredentialsHandlerFastCommitTest extends CommonHandlerFastCommit {

    @ObjectUnderTest
    private CreateCredentialsHandler beanUnderTest;

    /**
     * Tests CreateCredentialsHandler Positive flow
     *
     * @throws Exception
     */

    @Test
    public void testProcessRadio_CreateCredentialsHandler_Positive() throws Exception {
        final CredentialsCommand CredentialsCommand = buildCredentialsCommand(NscsCommandType.CREATE_CREDENTIALS, "radioNode", commandMapSecureParams);

        final NscsCommandResponse nscsResponse1 = beanUnderTest.process(CredentialsCommand, commandContext);

        assertTrue(CreateCredentialsHandler.ALL_CREDENTIALS_WERE_CREATED_SUCCESSFULLY.equals(((NscsMessageCommandResponse) nscsResponse1).getMessage()));
        assertTrue(provideEcimSecurePassword);

    }

    @Test
    public void testProcessRadio_CreateCredentialsWithLdapHandler_Positive() throws Exception {
        final CredentialsCommand CredentialsCommand = buildCredentialsCommand(NscsCommandType.CREATE_CREDENTIALS, "radioNode", commandMapSecureParams, "disable");

        final NscsCommandResponse nscsResponse1 = beanUnderTest.process(CredentialsCommand, commandContext);

        assertTrue(CreateCredentialsHandler.ALL_CREDENTIALS_WERE_CREATED_SUCCESSFULLY.equals(((NscsMessageCommandResponse) nscsResponse1).getMessage()));
        assertFalse(provideEcimSecurePassword);

        final CredentialsCommand CredentialsCommand2 = buildCredentialsCommand(NscsCommandType.CREATE_CREDENTIALS, "radioNode", commandMapSecureParams, "enable");

        final NscsCommandResponse nscsResponse2 = beanUnderTest.process(CredentialsCommand2, commandContext);

        assertTrue(CreateCredentialsHandler.ALL_CREDENTIALS_WERE_CREATED_SUCCESSFULLY.equals(((NscsMessageCommandResponse) nscsResponse2).getMessage()));
        assertTrue(provideEcimSecurePassword);

    }

    @Test
    public void testProcessPico_CreateCredentialsHandler_Positive() throws Exception {
        final CredentialsCommand CredentialsCommand = buildCredentialsCommand(NscsCommandType.CREATE_CREDENTIALS, "picoNode", commandMapNoParams);

        final NscsCommandResponse nscsResponse1 = beanUnderTest.process(CredentialsCommand, commandContext);

        assertTrue(CreateCredentialsHandler.ALL_CREDENTIALS_WERE_CREATED_SUCCESSFULLY.equals(((NscsMessageCommandResponse) nscsResponse1).getMessage()));
        assertTrue(provideEcimSecurePassword);

    }
    
    @Test
    public void testProcessPico_CreateCredentialsWithLdapHandler_Positive() throws Exception {
        final CredentialsCommand CredentialsCommand = buildCredentialsCommand(NscsCommandType.CREATE_CREDENTIALS, "picoNode", commandMapNoParams, "disable");

        final NscsCommandResponse nscsResponse1 = beanUnderTest.process(CredentialsCommand, commandContext);

        assertTrue(CreateCredentialsHandler.ALL_CREDENTIALS_WERE_CREATED_SUCCESSFULLY.equals(((NscsMessageCommandResponse) nscsResponse1).getMessage()));
        assertFalse(provideEcimSecurePassword);

        final CredentialsCommand CredentialsCommand2 = buildCredentialsCommand(NscsCommandType.CREATE_CREDENTIALS, "picoNode", commandMapNoParams, "enable");

        final NscsCommandResponse nscsResponse2 = beanUnderTest.process(CredentialsCommand2, commandContext);

        assertTrue(CreateCredentialsHandler.ALL_CREDENTIALS_WERE_CREATED_SUCCESSFULLY.equals(((NscsMessageCommandResponse) nscsResponse2).getMessage()));
        assertTrue(provideEcimSecurePassword);

    }
    
    @Test
    public void testProcessCPP_CreateCredentialsHandler_Positive() throws Exception {
        final CredentialsCommand CredentialsCommand = buildCredentialsCommand(NscsCommandType.CREATE_CREDENTIALS, "cppNode", commandMapAllParams);

        final NscsCommandResponse nscsResponse1 = beanUnderTest.process(CredentialsCommand, commandContext);

        assertTrue(CreateCredentialsHandler.ALL_CREDENTIALS_WERE_CREATED_SUCCESSFULLY.equals(((NscsMessageCommandResponse) nscsResponse1).getMessage()));
        assertFalse(provideEcimSecurePassword);

    }

    @Test
    public void testProcessCPP_CreateCredentialsWithLdapHandler_Positive() throws Exception {
        final CredentialsCommand CredentialsCommand = buildCredentialsCommand(NscsCommandType.CREATE_CREDENTIALS, "cppNode", commandMapAllParams,"enable");

        final NscsCommandResponse nscsResponse1 = beanUnderTest.process(CredentialsCommand, commandContext);

        assertTrue(CreateCredentialsHandler.ALL_CREDENTIALS_WERE_CREATED_SUCCESSFULLY.equals(((NscsMessageCommandResponse) nscsResponse1).getMessage()));
        assertFalse(provideEcimSecurePassword);
        final CredentialsCommand CredentialsCommand2 = buildCredentialsCommand(NscsCommandType.CREATE_CREDENTIALS, "cppNode", commandMapAllParams,"enable");

        final NscsCommandResponse nscsResponse2 = beanUnderTest.process(CredentialsCommand2, commandContext);

        assertTrue(CreateCredentialsHandler.ALL_CREDENTIALS_WERE_CREATED_SUCCESSFULLY.equals(((NscsMessageCommandResponse) nscsResponse2).getMessage()));
        assertFalse(provideEcimSecurePassword);
    }

}
