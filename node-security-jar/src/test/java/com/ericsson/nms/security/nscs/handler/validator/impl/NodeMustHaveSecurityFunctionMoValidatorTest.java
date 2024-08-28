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
package com.ericsson.nms.security.nscs.handler.validator.impl;

import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.command.NscsPropertyCommand;
import com.ericsson.nms.security.nscs.api.command.types.NscsNodeCommand;
import com.ericsson.nms.security.nscs.api.exception.SecurityFunctionMoNotfoundException;
import com.ericsson.nms.security.nscs.api.exception.UnexpectedCommandTypeException;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.validation.impl.NodeMustHaveSecurityFunctionMoValidator;

@RunWith(MockitoJUnitRunner.class)
public class NodeMustHaveSecurityFunctionMoValidatorTest {

    @InjectMocks
    NodeMustHaveSecurityFunctionMoValidator validator = new NodeMustHaveSecurityFunctionMoValidator();

    @Spy
    Logger logger = LoggerFactory.getLogger(NodeMustHaveSecurityFunctionMoValidatorTest.class);

    @Mock
    NscsNodeCommand nodeCommand;

    @Mock
    NscsPropertyCommand propertyCommand;

    @Mock
    CommandContext ctx;

    static final String NAME_ONE = "ERBS01";
    static final String FDN_ONE = String.format("%s=%s", Model.NETWORK_ELEMENT.type(), NAME_ONE);

    static final String NAME_TWO = "ERBS02";
    static final String FDN_TWO = String.format("%s=%s", Model.NETWORK_ELEMENT.type(), NAME_TWO);

    @Mock
    NormalizableNodeReference nodeRefOne;

    @Mock
    NormalizableNodeReference nodeRefTwo;

    @Mock
    NodeReference normalizedRefOne;

    @Mock
    NodeReference normalizedRefTwo;

    @Mock
    NscsCMReaderService reader;

    List<NormalizableNodeReference> oneNodeList;

    List<NormalizableNodeReference> twoNodesList;

    @Before
    public void setUp() {
        oneNodeList = Arrays.asList(nodeRefOne);

        twoNodesList = Arrays.asList(nodeRefOne, nodeRefTwo);

    }

    @Test
    public void testNodeWithoutSecurityFunctionMo() {
        when(nodeRefOne.getNormalizedRef()).thenReturn(normalizedRefOne);
        when(normalizedRefOne.getName()).thenReturn(NAME_ONE);
        when(normalizedRefOne.getFdn()).thenReturn(FDN_ONE);
        when(ctx.getValidNodes()).thenReturn(oneNodeList);
        when(reader.exists(Model.getNomalizedRootMO(FDN_ONE).securityFunction.withNames(NAME_ONE).fdn())).thenReturn(false);
        validator.validate(nodeCommand, ctx);
        Mockito.verify(ctx, Mockito.times(1))
                .setAsInvalidOrFailed(Mockito.any(NodeRef.class), Mockito.any(SecurityFunctionMoNotfoundException.class));
    }

    @Test
    public void testNodeWithSecurityFunctionMo() {
        when(nodeRefOne.getNormalizedRef()).thenReturn(normalizedRefOne);
        when(normalizedRefOne.getName()).thenReturn(NAME_ONE);
        when(normalizedRefOne.getFdn()).thenReturn(FDN_ONE);
        when(ctx.getValidNodes()).thenReturn(oneNodeList);
        when(reader.exists(Model.getNomalizedRootMO(FDN_ONE).securityFunction.withNames(NAME_ONE).fdn())).thenReturn(true);
        validator.validate(nodeCommand, ctx);
        Mockito.verify(ctx, Mockito.never()).setAsInvalidOrFailed(Mockito.any(NodeRef.class), Mockito.any(SecurityFunctionMoNotfoundException.class));
    }

    @Test
    public void testNodeListWithAndWithoutSecurityFunctionMo() {
        when(nodeRefOne.getNormalizedRef()).thenReturn(normalizedRefOne);
        when(normalizedRefOne.getName()).thenReturn(NAME_ONE);
        when(normalizedRefOne.getFdn()).thenReturn(FDN_ONE);
        when(nodeRefTwo.getNormalizedRef()).thenReturn(normalizedRefTwo);
        when(normalizedRefTwo.getName()).thenReturn(NAME_TWO);
        when(normalizedRefTwo.getFdn()).thenReturn(FDN_TWO);
        when(ctx.getValidNodes()).thenReturn(twoNodesList);
        when(reader.exists(Model.getNomalizedRootMO(FDN_ONE).securityFunction.withNames(NAME_ONE).fdn())).thenReturn(true);
        when(reader.exists(Model.getNomalizedRootMO(FDN_TWO).securityFunction.withNames(NAME_TWO).fdn())).thenReturn(false);
        validator.validate(nodeCommand, ctx);
        Mockito.verify(ctx, Mockito.times(1))
                .setAsInvalidOrFailed(Mockito.any(NodeRef.class), Mockito.any(SecurityFunctionMoNotfoundException.class));
    }

    @Test(expected = UnexpectedCommandTypeException.class)
    public void testNotNscsNodeCommand() {
        validator.validate(propertyCommand, ctx);
    }

}
