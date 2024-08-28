/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2022
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.ejb.scriptengine;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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

import com.ericsson.nms.security.nscs.api.NscsService;
import com.ericsson.nms.security.nscs.api.command.NscsCliCommand;
import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsMessageCommandResponse;
import com.ericsson.nms.security.nscs.api.exception.CommandSyntaxException;
import com.ericsson.nms.security.nscs.api.exception.InvalidNodeNameException;
import com.ericsson.nms.security.nscs.api.exception.InvalidTargetGroupException;
import com.ericsson.nms.security.nscs.api.exception.UnexpectedCommandTypeException;
import com.ericsson.nms.security.nscs.api.exception.wrapper.MultiErrorNodeException;
import com.ericsson.nms.security.nscs.api.exception.wrapper.NodeException;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.util.DownloadFileHolder;
import com.ericsson.nms.security.nscs.util.ExportCacheItemsHolder;
import com.ericsson.oss.services.scriptengine.spi.dtos.Command;
import com.ericsson.oss.services.scriptengine.spi.dtos.CommandResponseDto;
import com.ericsson.oss.services.scriptengine.spi.dtos.ResponseDto;
import com.ericsson.oss.services.scriptengine.spi.dtos.file.FileDownloadResponseDto;
import com.ericsson.oss.services.scriptengine.spi.dtos.file.InMemoryFileDto;

@RunWith(MockitoJUnitRunner.class)
public class NscsScriptEngineCommandHandlerImplTest {
    @Spy
    private final Logger logger = LoggerFactory.getLogger(NscsScriptEngineCommandHandlerImpl.class);
    @Spy
    private NscsToSEResponseMapper nscsToSEResponseMapper;
    @InjectMocks
    NscsScriptEngineCommandHandlerImpl nscsScriptEngineCommandHandlerImpl;
    @Mock
    NscsService nscsService;

    @Mock
    private ExportCacheItemsHolder exportCacheItemsHolder;

    @Mock
    private Cache<String, Object> cache;

    @Test
    public void testAuthPriv() {
        final NscsMessageCommandResponse commandResponse = new NscsMessageCommandResponse();
        final String commandString = "snmp authpriv --auth_algo MD5 --auth_password authpass --priv_algo DES --priv_password privpass --nodelist LTE01dg2ERBS00003";
        Mockito.when(nscsService.processCommand(Mockito.any(NscsCliCommand.class))).thenReturn(commandResponse);
        Mockito.doNothing().when(nscsToSEResponseMapper).convertoToCommandResponseDto(Mockito.any(ResponseDto.class), 
                Mockito.any(NscsCommandResponse.class), Mockito.anyString());
        final CommandResponseDto response = nscsScriptEngineCommandHandlerImpl.execute(new Command("secadm", commandString));
        assertTrue(response.getCommand().contains("snmp authpriv"));
    }

    @Test
    public void testAuthNoPriv() {
        final NscsMessageCommandResponse commandResponse = new NscsMessageCommandResponse();
        final String commandString = "snmp authnopriv --auth_algo MD5 --auth_password authpass --nodelist LTE01dg2ERBS00003";
        Mockito.when(nscsService.processCommand(Mockito.any(NscsCliCommand.class))).thenReturn(commandResponse);
        Mockito.doNothing().when(nscsToSEResponseMapper).convertoToCommandResponseDto(Mockito.any(ResponseDto.class),
                Mockito.any(NscsCommandResponse.class), Mockito.anyString());
        final CommandResponseDto response = nscsScriptEngineCommandHandlerImpl.execute(new Command("secadm", commandString));
        final String responseString = response.getCommand();
        assertTrue(responseString.contains("snmp authnopriv"));
    }

    @Test
    public void testCredsCreate() {
        final NscsMessageCommandResponse commandResponse = new NscsMessageCommandResponse();
        final String commandString = "credentials create --secureusername netsim --secureuserpassword netsim -n LTE01dg2ERBS00003";
        Mockito.when(nscsService.processCommand(Mockito.any(NscsCliCommand.class))).thenReturn(commandResponse);
        Mockito.doNothing().when(nscsToSEResponseMapper).convertoToCommandResponseDto(Mockito.any(ResponseDto.class),
                Mockito.any(NscsCommandResponse.class), Mockito.anyString());
        final CommandResponseDto response = nscsScriptEngineCommandHandlerImpl.execute(new Command("secadm", commandString));
        final String responseString = response.getCommand();
        assertTrue(responseString.contains("credentials create"));
    }

    @Test
    public void testCredsUpdate() {
        final NscsMessageCommandResponse commandResponse = new NscsMessageCommandResponse();
        final String commandString = "credentials update --secureusername netsim --secureuserpassword netsim -n LTE01dg2ERBS00003";
        Mockito.when(nscsService.processCommand(Mockito.any(NscsCliCommand.class))).thenReturn(commandResponse);
        Mockito.doNothing().when(nscsToSEResponseMapper).convertoToCommandResponseDto(Mockito.any(ResponseDto.class),
                Mockito.any(NscsCommandResponse.class), Mockito.anyString());
        final CommandResponseDto response = nscsScriptEngineCommandHandlerImpl.execute(new Command("secadm", commandString));
        final String responseString = response.getCommand();
        assertTrue(responseString.contains("credentials update"));
    }

    @Test
    public void testCredsGet() {
        final NscsMessageCommandResponse commandResponse = new NscsMessageCommandResponse();
        final String commandString = "credentials get --secureusername netsim --secureuserpassword netsim -n LTE01dg2ERBS00003";
        Mockito.when(nscsService.processCommand(Mockito.any(NscsCliCommand.class))).thenReturn(commandResponse);
        Mockito.doNothing().when(nscsToSEResponseMapper).convertoToCommandResponseDto(Mockito.any(ResponseDto.class),
                Mockito.any(NscsCommandResponse.class), Mockito.anyString());
        final CommandResponseDto response = nscsScriptEngineCommandHandlerImpl.execute(new Command("secadm", commandString));
        final String responseString = response.getCommand();
        assertTrue(responseString.contains("credentials get"));
    }

    @Test
    public void testGenericCommand() {
        final NscsMessageCommandResponse commandResponse = new NscsMessageCommandResponse();
        final String commandString = "certificate get --certtype IPSEC --nodelist LTE01dg2ERBS00003";
        Mockito.when(nscsService.processCommand(Mockito.any(NscsCliCommand.class))).thenReturn(commandResponse);
        Mockito.doNothing().when(nscsToSEResponseMapper).convertoToCommandResponseDto(Mockito.any(ResponseDto.class),
                Mockito.any(NscsCommandResponse.class), Mockito.anyString());
        final CommandResponseDto response = nscsScriptEngineCommandHandlerImpl.execute(new Command("secadm", commandString));
        final String responseString = response.getCommand();
        assertTrue(responseString.contains("certificate get"));
    }

    @Test
    public void testGenericCommand_ThrownNscsSystemException() {
        final String commandString = "certificate get";
        Mockito.when(nscsService.processCommand(Mockito.any(NscsCliCommand.class))).thenThrow(new UnexpectedCommandTypeException());
        Mockito.doNothing().when(nscsToSEResponseMapper).createErrorCommandResponse(Mockito.any(ResponseDto.class));
        nscsScriptEngineCommandHandlerImpl.execute(new Command("secadm", commandString));
    }

    @Test
    public void testGenericCommand_ThrownNodeException() {
        final String commandString = "certificate get";
        final InvalidNodeNameException invalidNodeNameException = new InvalidNodeNameException();
        final NodeReference node1 = new NodeRef("node1");
        final NodeException nodeException = new NodeException(Arrays.asList(node1, new NodeRef("node2")), invalidNodeNameException);
        Mockito.when(nscsService.processCommand(Mockito.any(NscsCliCommand.class))).thenThrow(nodeException);
        Mockito.doNothing().when(nscsToSEResponseMapper).createErrorCommandResponse(Mockito.any(ResponseDto.class));
        nscsScriptEngineCommandHandlerImpl.execute(new Command("secadm", commandString));
    }

    @Test
    public void testGenericCommand_ThrownMultiErrorNodeException() {
        final String commandString = "certificate get";
        Mockito.when(nscsService.processCommand(Mockito.any(NscsCliCommand.class))).thenThrow(new MultiErrorNodeException());
        Mockito.doNothing().when(nscsToSEResponseMapper).createErrorCommandResponse(Mockito.any(ResponseDto.class));
        nscsScriptEngineCommandHandlerImpl.execute(new Command("secadm", commandString));
    }

    @Test
    public void testGenericCommand_ThrownNscsInvalidItemsException() {
        final String commandString = "certificate get";
        Mockito.when(nscsService.processCommand(Mockito.any(NscsCliCommand.class))).thenThrow(new InvalidTargetGroupException(new ArrayList<>()));
        Mockito.doNothing().when(nscsToSEResponseMapper).createErrorCommandResponse(Mockito.any(ResponseDto.class));
        nscsScriptEngineCommandHandlerImpl.execute(new Command("secadm", commandString));
    }

    @Test
    public void testGenericCommand_ThrownNscsServiceException() {
        final String commandString = "certificate get";
        Mockito.when(nscsService.processCommand(Mockito.any(NscsCliCommand.class))).thenThrow(new CommandSyntaxException());
        Mockito.doNothing().when(nscsToSEResponseMapper).createErrorCommandResponse(Mockito.any(ResponseDto.class));
        nscsScriptEngineCommandHandlerImpl.execute(new Command("secadm", commandString));
    }

    @Test
    public void testGenericCommand_ThrownNullPointerException() {
        final String commandString = "certificate get";
        Mockito.when(nscsService.processCommand(Mockito.any(NscsCliCommand.class))).thenThrow(new NullPointerException());
        Mockito.doNothing().when(nscsToSEResponseMapper).createErrorCommandResponse(Mockito.any(ResponseDto.class));
        nscsScriptEngineCommandHandlerImpl.execute(new Command("secadm", commandString));
    }

    @Test
    public void testRemoveExtraPropertiesWithTORF597790PropertyWithNotNullValue() {
        final NscsCliCommand nscsCliCommand = new NscsCliCommand("command");
        final Map<String, Object> properties = new HashMap<>();
        properties.put("key", "value");
        properties.put("TORF-597790", "userId");
        nscsCliCommand.setProperties(properties);
        nscsScriptEngineCommandHandlerImpl.removeExtraProperties(nscsCliCommand);
        assertTrue(nscsCliCommand.getProperties().containsKey("key"));
        assertFalse(nscsCliCommand.getProperties().containsKey("TORF-597790"));
        assertTrue(properties.containsKey("key"));
        assertFalse(properties.containsKey("TORF-597790"));
    }

    @Test
    public void testRemoveExtraPropertiesWithTORF597790PropertyWithNullValue() {
        final NscsCliCommand nscsCliCommand = new NscsCliCommand("command");
        final Map<String, Object> properties = new HashMap<>();
        properties.put("key", "value");
        properties.put("TORF-597790", null);
        nscsCliCommand.setProperties(properties);
        nscsScriptEngineCommandHandlerImpl.removeExtraProperties(nscsCliCommand);
        assertTrue(nscsCliCommand.getProperties().containsKey("key"));
        assertFalse(nscsCliCommand.getProperties().containsKey("TORF-597790"));
        assertTrue(properties.containsKey("key"));
        assertFalse(properties.containsKey("TORF-597790"));
    }

    @Test
    public void testRemoveExtraPropertiesWithoutTORF597790Property() {
        final NscsCliCommand nscsCliCommand = new NscsCliCommand("command");
        final Map<String, Object> properties = new HashMap<>();
        properties.put("key", "value");
        nscsCliCommand.setProperties(properties);
        nscsScriptEngineCommandHandlerImpl.removeExtraProperties(nscsCliCommand);
        assertTrue(nscsCliCommand.getProperties().containsKey("key"));
        assertFalse(nscsCliCommand.getProperties().containsKey("TORF-597790"));
        assertTrue(properties.containsKey("key"));
        assertFalse(properties.containsKey("TORF-597790"));
    }

    @Test
    public void testDownloadRequest_ExistingDeletableDownloadFileHolder() {
        final String fileId = "this is the file identifier";
        final DownloadFileHolder downloadFileHolder = buildDownloadFileHolder(true);
        Mockito.when(exportCacheItemsHolder.fetch(fileId)).thenReturn(downloadFileHolder);
        Mockito.when(exportCacheItemsHolder.getCache()).thenReturn(cache);
        final FileDownloadResponseDto response = nscsScriptEngineCommandHandlerImpl.execute(fileId);
        assertNotNull(response);
        assertTrue(response instanceof InMemoryFileDto);
    }

    @Test
    public void testDownloadRequest_ThrownExceptionRemovingExistingDeletableDownloadFileHolder() {
        final String fileId = "this is the file identifier";
        final DownloadFileHolder downloadFileHolder = buildDownloadFileHolder(true);
        Mockito.when(exportCacheItemsHolder.fetch(fileId)).thenReturn(downloadFileHolder);
        Mockito.when(exportCacheItemsHolder.getCache()).thenReturn(cache);
        Mockito.when(cache.remove(fileId)).thenThrow(new NullPointerException());
        final FileDownloadResponseDto response = nscsScriptEngineCommandHandlerImpl.execute(fileId);
        assertNotNull(response);
        assertTrue(response instanceof InMemoryFileDto);
    }

    @Test
    public void testDownloadRequest_ExistingNotDeletableDownloadFileHolder() {
        final String fileId = "this is the file identifier";
        final DownloadFileHolder downloadFileHolder = buildDownloadFileHolder(false);
        Mockito.when(exportCacheItemsHolder.fetch(fileId)).thenReturn(downloadFileHolder);
        final FileDownloadResponseDto response = nscsScriptEngineCommandHandlerImpl.execute(fileId);
        assertNotNull(response);
        assertTrue(response instanceof InMemoryFileDto);
    }

    @Test
    public void testDownloadRequest_NotExistingDownloadFileHolder() {
        final String fileId = "this is the file identifier";
        Mockito.when(exportCacheItemsHolder.fetch(fileId)).thenReturn(null);
        final FileDownloadResponseDto response = nscsScriptEngineCommandHandlerImpl.execute(fileId);
        assertNull(response);
    }

    private DownloadFileHolder buildDownloadFileHolder(final boolean isDeletable) {
        final String content = "this is the file content";
        final byte[] contentArray = content.getBytes();
        final DownloadFileHolder downloadFileHolder = new DownloadFileHolder();
        downloadFileHolder.setFileName("this is the file identifier");
        downloadFileHolder.setContentToBeDownloaded(contentArray);
        downloadFileHolder.setContentType("this is the content type");
        downloadFileHolder.setDeletable(isDeletable);
        return downloadFileHolder;
    }
}
