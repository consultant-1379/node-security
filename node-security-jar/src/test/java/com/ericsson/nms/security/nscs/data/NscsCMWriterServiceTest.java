package com.ericsson.nms.security.nscs.data;

import com.ericsson.oss.itpf.datalayer.dps.DataBucket;
import com.ericsson.oss.itpf.datalayer.dps.DataPersistenceService;
import com.ericsson.oss.itpf.datalayer.dps.object.builder.ManagedObjectBuilder;
import com.ericsson.oss.itpf.datalayer.dps.object.builder.MibRootBuilder;
import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject;
import com.ericsson.oss.itpf.modeling.modelservice.meta.ModelMetaInformation;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests the NscsCMWriterService to provide access to information stored in DPS
 * 
 * @see NscsCMWriterService
 * @author eabdsin
 */
@RunWith(MockitoJUnitRunner.class)
public class NscsCMWriterServiceTest {

    /**
     * 
     */
    private static final String PARENT_FDN = "parentFdn";

    @Spy
    private final Logger logger = LoggerFactory.getLogger(NscsCMWriterService.class);

    @InjectMocks
    private NscsCMWriterService beanUnderTest;

    @Mock
    private DataPersistenceService dataPersistenceService;
    @Mock
    private DataBucket dataBucket;

    @Mock
    private ModelMetaInformation modelMetaInformation;

    @Before
    public void setup() {
        when(dataPersistenceService.getLiveBucket()).thenReturn(dataBucket);
    }

    /**
     * Tests the creation of the mib root
     * 
     * @throws Exception
     */
    @Test
    public void testCreateMibRoot() throws Exception {

        final Map<String, Object> attributes = new HashMap<>();

        MibRootBuilder mibRootBuilder = mock(MibRootBuilder.class);
        when(dataBucket.getMibRootBuilder()).thenReturn(mibRootBuilder);
        when(mibRootBuilder.namespace(anyString())).thenReturn(mibRootBuilder);
        when(mibRootBuilder.type(anyString())).thenReturn(mibRootBuilder);
        when(mibRootBuilder.version(anyString())).thenReturn(mibRootBuilder);
        when(mibRootBuilder.name(anyString())).thenReturn(mibRootBuilder);
        when(mibRootBuilder.addAttributes(attributes)).thenReturn(mibRootBuilder);
        when(dataBucket.findMoByFdn(PARENT_FDN)).thenReturn(mock(ManagedObject.class));

        beanUnderTest.createMibRoot(PARENT_FDN, buildObjectSpecification(attributes));

        verify(dataBucket).findMoByFdn(PARENT_FDN);
        verify(mibRootBuilder).create();

    }

    /**
     * Tests the creation of the managed Objects
     * 
     * @throws Exception
     */
    @Test
    public void testCreateManagedObject() throws Exception {

        final Map<String, Object> attributes = new HashMap<>();

        ManagedObjectBuilder moBuilder = mock(ManagedObjectBuilder.class);
        when(dataBucket.getManagedObjectBuilder()).thenReturn(moBuilder);
        when(moBuilder.type(anyString())).thenReturn(moBuilder);
        when(moBuilder.name(anyString())).thenReturn(moBuilder);
        when(moBuilder.addAttributes(attributes)).thenReturn(moBuilder);
        when(dataBucket.findMoByFdn(PARENT_FDN)).thenReturn(mock(ManagedObject.class));

        beanUnderTest.createMo(PARENT_FDN, buildObjectSpecification(attributes));

        verify(dataBucket).findMoByFdn(PARENT_FDN);
        verify(moBuilder).create();

    }

    private CmResponse buildCmResponse() {
        final CmResponse cmResponse = new CmResponse();
        final Collection<CmObject> cmObjects = new ArrayList<>(1);
        final CmObject cmObject = new CmObject();
        cmObjects.add(cmObject);
        cmResponse.setStatusCode(0);
        return cmResponse;
    }

    /**
     * build up Objects as required by CM components
     * 
     * @param attributes
     * @return
     */
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
