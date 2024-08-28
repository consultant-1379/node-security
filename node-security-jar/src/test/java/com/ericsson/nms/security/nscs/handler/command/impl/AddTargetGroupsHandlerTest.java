package com.ericsson.nms.security.nscs.handler.command.impl;

/**
 * 
 */

import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.NscsMessageCommandResponse;
import com.ericsson.nms.security.nscs.api.command.types.NscsNodeCommand;
import com.ericsson.nms.security.nscs.api.command.types.TargetGroupsCommand;
import com.ericsson.nms.security.nscs.api.exception.TargetGroupsUpdateException;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.NscsCMWriterService;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.oss.services.cm.cmshared.dto.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static com.ericsson.nms.security.nscs.handler.command.impl.MockUtils.setupCommandContext;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

/**
 * 
 * Tests the target groups addition into NetworkElementSecurity Mo respecting provided list of nodes.
 * 
 * 
 * @see AddTargetGroupsHandler
 * @author eabdsin
 */
@RunWith(MockitoJUnitRunner.class)
public class AddTargetGroupsHandlerTest {

    /**
     * 
     */
    private static final String NODE12 = "node1";
    /**
     * 
     */
    private static final String TARGET_GROUP3 = "TargetGroup3";
    /**
     * 
     */
    private static final String TARGET_GROUP2 = "TargetGroup2";
    /**
     * 
     */
    private static final String TARGET_GROUP1 = "TargetGroup1";
    public static final String NODE_NAME_HEADER = "Node Name        ";
    public static final String NODE_SECURITY_LEVEL_HEADER = "Node Security Level";

    @Spy
    private final Logger logger = LoggerFactory.getLogger(NscsCMWriterService.class);

    @InjectMocks
    private AddTargetGroupsHandler beanUnderTest;

    @Mock
    private NscsCMWriterService cMWriterService;

    @Mock
    private NscsCMReaderService cMReaderService;

    @Mock
    private CommandContext commandContext;

    @Before
    public void setupTest() {
        setupCommandContext(commandContext, NODE12);
    }

    @Test
    public void testCreateManagedObject() throws Exception {

        buildNscsResponse();
        final TargetGroupsCommand targetGroupsCommand = buildTargetGroupCommand();
        final List<String> targetGroups = new ArrayList<String>();
        final CmResponse cmResponse = buildCmResponse(targetGroups);
        final Collection<CmObject> cmObjects = cmResponse.getCmObjects();
        final CmObject cmObject = cmObjects.iterator().next();

        final NscsCMWriterService.WriterSpecificationBuilder specificationBuilder = org.mockito.Mockito.mock(NscsCMWriterService.WriterSpecificationBuilder.class); //NscsCMWriterService.WriterSpecificationBuilder(); 

        when(
                cMReaderService.getMOAttribute(any(List.class), eq(Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.type()),
                        eq(Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.namespace()),
                        eq(Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.TARGET_GROUPS))).thenReturn(cmResponse);

        when(cMWriterService.withSpecification(cmObject.getFdn())).thenReturn(specificationBuilder);
        when(specificationBuilder.setAttribute(Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.TARGET_GROUPS, targetGroups)).thenReturn(specificationBuilder);

        final NscsCommandResponse nscsResponse1 = beanUnderTest.process(targetGroupsCommand, commandContext);

        assertTrue(AddTargetGroupsHandler.TARGET_GROUPS_SUCCESSFULLY_ADDED.equals(((NscsMessageCommandResponse) nscsResponse1).getMessage()));

    }

    @Test
    public void testCreateManagedObject_WhenTargetGroupsAlreadyAdded() throws Exception {

        buildNscsResponse();
        final TargetGroupsCommand targetGroupsCommand = buildTargetGroupCommand();
        final List<String> targetGroups = buildTargetGroupList();
        final CmResponse cmResponse = buildCmResponse(targetGroups);
        final Collection<CmObject> cmObjects = cmResponse.getCmObjects();
        final CmObject cmObject = cmObjects.iterator().next();

        final NscsCMWriterService.WriterSpecificationBuilder specificationBuilder = org.mockito.Mockito.mock(NscsCMWriterService.WriterSpecificationBuilder.class); //NscsCMWriterService.WriterSpecificationBuilder(); 

        when(
                cMReaderService.getMOAttribute(any(List.class), eq(Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.type()),
                        eq(Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.namespace()),
                        eq(Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.TARGET_GROUPS))).thenReturn(cmResponse);

        when(cMWriterService.withSpecification(cmObject.getFdn())).thenReturn(specificationBuilder);
        when(specificationBuilder.setAttribute(Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.TARGET_GROUPS, targetGroups)).thenReturn(specificationBuilder);

        boolean thrown = false;
        try {

            beanUnderTest.process(targetGroupsCommand, commandContext);
            fail("Shouldn't come here");
        } catch (final TargetGroupsUpdateException e) {
            assertTrue(e != null);
            thrown = true;

        }
        assertTrue(thrown);

    }

    private CmResponse buildCmResponse(final List<String> targetGroups) {
        final CmResponse cmResponse = new CmResponse();
        final Map<String, Object> attributesMap = new HashMap<>();

        attributesMap.put(Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.TARGET_GROUPS, targetGroups);

        final Collection<CmObject> cmObjects = new ArrayList<>(1);
        final CmObject cmObject = new CmObject();
        cmObject.setAttributes(attributesMap);
        cmObject.setFdn(NODE12);
        cmObjects.add(cmObject);
        cmResponse.setTargetedCmObjects(cmObjects);
        cmResponse.setStatusCode(0);
        return cmResponse;

    }

    private NscsMessageCommandResponse buildNscsResponse() {

        final NscsMessageCommandResponse response = NscsCommandResponse.message(AddTargetGroupsHandler.TARGET_GROUPS_SUCCESSFULLY_ADDED);

        return response;
    }

    /**
     * @return
     */
    private List<String> buildTargetGroupList() {
        final List<String> targetGroups = new ArrayList<String>();
        targetGroups.add(TARGET_GROUP1);
        targetGroups.add(TARGET_GROUP2);
        targetGroups.add(TARGET_GROUP3);
        return targetGroups;
    }

    private TargetGroupsCommand buildTargetGroupCommand() {
        final TargetGroupsCommand targetGroupsCommand = new TargetGroupsCommand();
        targetGroupsCommand.setCommandType(NscsCommandType.ADD_TARGET_GROUPS);
        final Map<String, Object> commandMap = new HashMap<String, Object>() {

            private static final long serialVersionUID = 1L;

            {
                {
                    put(NscsNodeCommand.NODE_LIST_PROPERTY, Arrays.asList(NODE12));
                    put(TargetGroupsCommand.TARGET_GROUP_PROPERTY, buildTargetGroupList());
                }
            }
        };
        targetGroupsCommand.setProperties(commandMap);
        return targetGroupsCommand;
    }

    public CmObjectSpecification buildObjectSpecification(final Map<String, Object> attributes) {

        final String version = null;
        final String namespace = null;
        final String type = null;
        final String name = "1";

        final CmObjectSpecification os = new CmObjectSpecification();
        os.setType(type);
        os.setNamespace(namespace);
        os.setName(name);
        os.setNamespaceVersion(version);

        final AttributeSpecificationContainer osAttributes = getAttributeSpecifications(attributes);

        os.setAttributeSpecificationContainer(osAttributes);
        return os;
    }

    private AttributeSpecificationContainer getAttributeSpecifications(final Map<String, Object> attributes) {
        final StringifiedAttributeSpecifications osAttributes = new StringifiedAttributeSpecifications();
        for (final Map.Entry<String, Object> attrib : attributes.entrySet()) {
            addAttributeSpecification(osAttributes, attrib.getKey(), attrib.getValue());
        }
        return osAttributes;
    }

    private void addAttributeSpecification(final StringifiedAttributeSpecifications attributes, final String name, final Object value) {
        final AttributeSpecification attributeSpecification = new AttributeSpecification();
        attributeSpecification.setName(name);
        attributeSpecification.setValue(value);
        attributes.addAttributeSpecification(attributeSpecification);
    }
}
