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

import java.util.*;

import com.ericsson.nms.security.nscs.api.model.NodeRef;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.command.NscsPropertyCommand;
import com.ericsson.nms.security.nscs.api.command.types.NscsNodeCommand;
import com.ericsson.nms.security.nscs.api.exception.*;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.command.impl.AddTargetGroupsHandler;
import com.ericsson.nms.security.nscs.handler.validation.impl.NoDuplNodeNamesAllowedValidator;

/**
 *
 * Tests the validator that checks if there are duplicated node names on a node list passed
 * as a parameter to a NscsNodeCommand
 *
 * @see NoDuplNodeNamesAllowedValidator
 * @author xpawpio
 */
@RunWith(MockitoJUnitRunner.class)
public class NoDuplNodeNamesAllowedValidatorTest {

	@Spy
	Logger logger = LoggerFactory.getLogger(NoDuplNodeNamesAllowedValidatorTest.class);

	@InjectMocks
	NoDuplNodeNamesAllowedValidator validator = new NoDuplNodeNamesAllowedValidator();

	@Mock
	CommandContext ctx;

	@Mock
	NscsNodeCommand nodeCommand;

	@Mock
	NscsPropertyCommand propertyCommand;

    @Mock
    NodeReference nodeRef;
    List<NodeReference> nodesList = Arrays.asList(nodeRef, nodeRef);


	@Test
	public void testDuplNodeNamesInNodeList(){
        when(ctx.getAllNodes()).thenReturn(nodesList);
        validator.validate(nodeCommand, ctx);
        verify(ctx, times(2)).setAsInvalidOrFailed(any(NodeRef.class), any(DuplicateNodeNamesException.class));

    }

	@Test(expected = UnexpectedCommandTypeException.class)
	public void testUnexpectedCommandType(){
		validator.validate(propertyCommand, ctx);
	}
}
