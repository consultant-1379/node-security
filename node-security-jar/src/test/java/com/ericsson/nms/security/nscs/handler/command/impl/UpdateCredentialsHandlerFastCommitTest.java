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
public class UpdateCredentialsHandlerFastCommitTest extends CommonHandlerFastCommit {

    @ObjectUnderTest
    private UpdateCredentialsHandler beanUnderTest;

    /**
     * Tests CreateCredentialsHandler Positive flow
     *
     * @throws Exception
     */

    @Test
    public void testProcessRadio_UpdateCredentialsHandler_Positive() throws Exception {
        final CredentialsCommand credentialsCommand = buildCredentialsCommand(NscsCommandType.UPDATE_CREDENTIALS, "radioNode", commandMapSecureParams);
        final NscsCommandResponse nscsResponse1 = beanUnderTest.process(credentialsCommand, commandContext);

        assertTrue(UpdateCredentialsHandler.ALL_CREDENTIALS_UPDATED_SUCCESSFULLY.equals(((NscsMessageCommandResponse) nscsResponse1).getMessage()));
        assertTrue(provideEcimSecurePassword);
    }

    @Test
    public void testProcessRadio_UpdateCredentialsWithLdapUserHandler_Positive() throws Exception {
        CredentialsCommand credentialsCommand = buildCredentialsCommand(NscsCommandType.UPDATE_CREDENTIALS, "radioNode", commandMapSecureParams, "disable");
        NscsCommandResponse nscsResponse = beanUnderTest.process(credentialsCommand, commandContext);

        assertTrue(UpdateCredentialsHandler.ALL_CREDENTIALS_UPDATED_SUCCESSFULLY.equals(((NscsMessageCommandResponse) nscsResponse).getMessage()));
        assertFalse(provideEcimSecurePassword);

        credentialsCommand = buildCredentialsCommand(NscsCommandType.UPDATE_CREDENTIALS, "radioNode", commandMapSecureParams, "enable");

        nscsResponse = beanUnderTest.process(credentialsCommand, commandContext);

        assertTrue(UpdateCredentialsHandler.ALL_CREDENTIALS_UPDATED_SUCCESSFULLY.equals(((NscsMessageCommandResponse) nscsResponse).getMessage()));
        assertTrue(provideEcimSecurePassword);
    }
    
    @Test
    public void testProcessPico_UpdateCredentialsHandler_Positive() throws Exception {
        final CredentialsCommand credentialsCommand = buildCredentialsCommand(NscsCommandType.UPDATE_CREDENTIALS, "picoNode", commandMapNoParams);
        final NscsCommandResponse nscsResponse1 = beanUnderTest.process(credentialsCommand, commandContext);

        assertTrue(UpdateCredentialsHandler.ALL_CREDENTIALS_UPDATED_SUCCESSFULLY.equals(((NscsMessageCommandResponse) nscsResponse1).getMessage()));
        assertTrue(provideEcimSecurePassword);
    }
    
    @Test
    public void testProcessPico_UpdateCredentialsWithLdapUserHandler_Positive() throws Exception {
        CredentialsCommand credentialsCommand = buildCredentialsCommand(NscsCommandType.UPDATE_CREDENTIALS, "picoNode", commandMapNoParams, "disable");
        NscsCommandResponse nscsResponse = beanUnderTest.process(credentialsCommand, commandContext);

        assertTrue(UpdateCredentialsHandler.ALL_CREDENTIALS_UPDATED_SUCCESSFULLY.equals(((NscsMessageCommandResponse) nscsResponse).getMessage()));
        assertFalse(provideEcimSecurePassword);

        
        credentialsCommand = buildCredentialsCommand(NscsCommandType.UPDATE_CREDENTIALS, "picoNode", commandMapNoParams, "enable");

        nscsResponse = beanUnderTest.process(credentialsCommand, commandContext);

        assertTrue(UpdateCredentialsHandler.ALL_CREDENTIALS_UPDATED_SUCCESSFULLY.equals(((NscsMessageCommandResponse) nscsResponse).getMessage()));
        assertTrue(provideEcimSecurePassword);
    }

    @Test
    public void testProcessCPP_UpdateCredentialsHandler_Positive() throws Exception {
        final CredentialsCommand credentialsCommand = buildCredentialsCommand(NscsCommandType.UPDATE_CREDENTIALS, "cppNode", commandMapAllParams);

        final NscsCommandResponse nscsResponse1 = beanUnderTest.process(credentialsCommand, commandContext);

        assertTrue(UpdateCredentialsHandler.ALL_CREDENTIALS_UPDATED_SUCCESSFULLY.equals(((NscsMessageCommandResponse) nscsResponse1).getMessage()));
        assertFalse(provideEcimSecurePassword);

    }

    @Test
    public void testProcessCPP_UpdateCredentialsWithLdapUserHandler_Positive() throws Exception {
        CredentialsCommand credentialsCommand = buildCredentialsCommand(NscsCommandType.UPDATE_CREDENTIALS, "cppNode", commandMapAllParams, "disable");
        NscsCommandResponse nscsResponse = beanUnderTest.process(credentialsCommand, commandContext);

        assertTrue(UpdateCredentialsHandler.ALL_CREDENTIALS_UPDATED_SUCCESSFULLY.equals(((NscsMessageCommandResponse) nscsResponse).getMessage()));
        assertFalse(provideEcimSecurePassword);
        
        credentialsCommand = buildCredentialsCommand(NscsCommandType.UPDATE_CREDENTIALS, "cppNode", commandMapAllParams, "enable");

        nscsResponse = beanUnderTest.process(credentialsCommand, commandContext);

        assertTrue(UpdateCredentialsHandler.ALL_CREDENTIALS_UPDATED_SUCCESSFULLY.equals(((NscsMessageCommandResponse) nscsResponse).getMessage()));
        assertFalse(provideEcimSecurePassword);

    }

}
