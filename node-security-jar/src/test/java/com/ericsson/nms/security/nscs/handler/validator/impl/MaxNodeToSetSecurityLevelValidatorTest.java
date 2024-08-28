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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ericsson.nms.security.nscs.handler.validation.impl.MaxNodeToSetSecurityLevelValidator;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ericsson.nms.security.nscs.api.command.NscsPropertyCommand;
import com.ericsson.nms.security.nscs.api.command.types.NscsNodeCommand;
import com.ericsson.nms.security.nscs.api.exception.*;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import static org.mockito.Mockito.*;

/**
 * User: ejuhpar
 * Date: 19/11/14
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class MaxNodeToSetSecurityLevelValidatorTest {

    @InjectMocks
    MaxNodeToSetSecurityLevelValidator validator = new MaxNodeToSetSecurityLevelValidator();

    @Spy
    private final Logger logger = LoggerFactory.getLogger(MaxNodeToSetSecurityLevelValidator.class);

    @Mock
    NscsNodeCommand nodeCommand;

    @Mock
    NscsPropertyCommand propertyCommand;

    @Mock
    CommandContext ctx;

    @Test
    public void setSecurityLevelAtMaxNumberTest(){

        NodeReference mockNodeRef = mock(NodeReference.class);
        int MAXNO = 160;
        List<NodeReference> mockList = new ArrayList<>();

        for (int nodeNumber = 0; nodeNumber < MAXNO; nodeNumber++){
            mockList.add(mockNodeRef);
        }
        when(nodeCommand.getNodes()).thenReturn(mockList);
        validator.validate(nodeCommand, null);
    }

    /**
     * Test the negative flow of CppSetSecurityLevelHandler
     * throws MaxNodesExceededException is thrown
     */
    @Test(expected = MaxNodesExceededException.class)
    public void testMaxNodesExceededException() {

        NodeReference mockNodeRef = mock(NodeReference.class);
        int MAXNO = 160;
        List<NodeReference> mockList = new ArrayList<>();

        for (int nodeNumber = 0; nodeNumber <= MAXNO; nodeNumber++){
            mockList.add(mockNodeRef);
        }


        when(nodeCommand.getNodes()).thenReturn(mockList);
        validator.validate(nodeCommand, null);
     }

    @Test
    public void testNodeCommandIsAllNodes(){
        when(nodeCommand.isAllNodes()).thenReturn(true);
        validator.validate(nodeCommand, null);
        verify(nodeCommand, never()).getNodes();
    }

    @Test(expected = UnexpectedCommandTypeException.class)
    public void testNotNscsNodeCommand(){
        CommandContext ctx = mock(CommandContext.class);
        validator.validate(propertyCommand, ctx);
    }

}
