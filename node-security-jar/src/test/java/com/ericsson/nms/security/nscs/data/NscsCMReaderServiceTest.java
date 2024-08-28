package com.ericsson.nms.security.nscs.data;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.oss.itpf.datalayer.dps.DataBucket;
import com.ericsson.oss.itpf.datalayer.dps.DataPersistenceService;
import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject;
import com.ericsson.oss.itpf.modeling.modelservice.meta.ModelMetaInformation;
import com.ericsson.oss.itpf.sdk.core.retry.RetriableCommand;
import com.ericsson.oss.itpf.sdk.core.retry.RetryManager;
import com.ericsson.oss.itpf.sdk.core.retry.RetryPolicy;
import com.ericsson.oss.services.cm.cmreader.api.CmReaderService;
import com.ericsson.oss.services.cm.cmshared.dto.AttributeSpecification;
import com.ericsson.oss.services.cm.cmshared.dto.AttributeSpecificationContainer;
import com.ericsson.oss.services.cm.cmshared.dto.CmConstants;
import com.ericsson.oss.services.cm.cmshared.dto.CmObject;
import com.ericsson.oss.services.cm.cmshared.dto.CmObjectSpecification;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;
import com.ericsson.oss.services.cm.cmshared.dto.ValidatedAttributeSpecifications;
import com.ericsson.oss.services.cm.cmshared.dto.search.CmMatchCondition;
import com.ericsson.oss.services.cm.cmshared.dto.search.CmSearchCriteria;
import com.ericsson.oss.services.cm.cmshared.dto.search.CmSearchScope;

/**
 * Tests the NscsCMReaderService to provide access to information stored in DPS
 * 
 * Tests related to getNormalizableNodeReference and getNormalizedNodeReference methods are available in NscsCMReaderServiceSpecTest.groovy file.
 *
 * @see NscsCMReaderService
 * @author eabdsin
 */
@RunWith(MockitoJUnitRunner.class)
public class NscsCMReaderServiceTest {

    public static final String NE_FDN = "NetworkElement=myNode";
    private static final String ROOT_MO = "VcuTop";

    @Spy
    private final Logger logger = LoggerFactory.getLogger(NscsCMReaderService.class);

    @Mock
    private DataPersistenceService dataPersistenceService;

    @InjectMocks
    @Spy
    private NscsCMReaderService beanUnderTest;

    @Mock
    private CmReaderService reader;

    @Mock
    RetryManager retryManager;

    @Mock
    private ModelMetaInformation modelMetaInformation;

    @Mock
    private NodeReference nodeReference;

    @Mock
    private DataBucket dataBucket;

    @Mock
    private ManagedObject managedObject;

    @Mock
    private MoObject moObject;

    @Mock
    private PoObject poObject;

    private static final String DEFAULT_MO_TYPE = "MoType";
    private static final String DEFAULT_ERBS_NAMESPACE = "ERBS_NODE_MODEL";
    private static final String DEFAULT_SGSN_MME_NAMESPACE = "SgsnMmeTop";
    private static final String DEFAULT_ATTRIBUTE = "targetGroup";

    @Captor
    ArgumentCaptor<RetriableCommand<?>> cmdCaptor;

    @Before
    public void setUp() {
        when(retryManager.executeCommand(any(RetryPolicy.class), cmdCaptor.capture())).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(final InvocationOnMock invocation) throws Throwable {
                // call actual command
                return cmdCaptor.getValue().execute(null);
            }
        });
    }

    /**
     * Tests the Retrieval of the value of a given attribute for ALL ERBS nodes
     *
     * @throws Exception
     */
    @Test
    public void testGetMOAttribute() throws Exception {

        buildSearchAttributes(DEFAULT_ERBS_NAMESPACE);
        final CmResponse cmResponse = buildCmResponse();

        when(reader.search(any(CmSearchCriteria.class), eq(CmConstants.LIVE_CONFIGURATION))).thenReturn(cmResponse);

        final CmResponse cmResponse2 = beanUnderTest.getMOAttribute(DEFAULT_MO_TYPE, DEFAULT_ATTRIBUTE);

        assertTrue(cmResponse.equals(cmResponse2));
        verify(reader).search(any(CmSearchCriteria.class), eq(CmConstants.LIVE_CONFIGURATION));

    }

    /**
     * Tests the Retrieval of all Mos of a given fdn
     *
     * @throws Exception
     */
    @Test
    public void testGetMoByFdn() throws Exception {

        buildSearchAttributes(DEFAULT_ERBS_NAMESPACE);
        final CmResponse cmResponse = buildCmResponse();

        when(nodeReference.getFdn()).thenReturn(NE_FDN);
        when(reader.getMoByFdn(anyString(),anyString())).thenReturn(cmResponse);
        final CmResponse cmResponse2 = beanUnderTest.getMoByFdn(nodeReference);

        assertTrue(cmResponse.equals(cmResponse2));
    }

    /**
     * Tests the Retrieval of all Mos of a given moType
     *
     * @throws Exception
     */
    @Test
    public void testGetMos() throws Exception {

        buildSearchAttributes(DEFAULT_ERBS_NAMESPACE);
        final CmResponse cmResponse = buildCmResponse();

        when(reader.search(any(CmSearchCriteria.class), eq(CmConstants.LIVE_CONFIGURATION))).thenReturn(cmResponse);

        final CmResponse cmResponse2 = beanUnderTest.getMos(ROOT_MO, DEFAULT_MO_TYPE, DEFAULT_ERBS_NAMESPACE, "any_attr");

        assertTrue(cmResponse.equals(cmResponse2));
        verify(reader).search(any(CmSearchCriteria.class), eq(CmConstants.LIVE_CONFIGURATION));

    }

    /**
     * Tests the Retrieval of all Mos of a given moType
     *
     * @throws Exception
     */
    @Test
    public void testGetAllMos() throws Exception {

        buildSearchAttributes(DEFAULT_ERBS_NAMESPACE);
        final CmResponse cmResponse = buildCmResponse();

        when(reader.search(any(CmSearchCriteria.class), eq(CmConstants.LIVE_CONFIGURATION))).thenReturn(cmResponse);

        final CmResponse cmResponse2 = beanUnderTest.getAllMos(DEFAULT_MO_TYPE, DEFAULT_ERBS_NAMESPACE);

        assertTrue(cmResponse.equals(cmResponse2));
        verify(reader).search(any(CmSearchCriteria.class), eq(CmConstants.LIVE_CONFIGURATION));

    }

    /**
     * Tests the Retrieval of the value of a given attribute for ALL SGSN-MME nodes
     *
     * @throws Exception
     */
    @Test
    public void testGetSgsnMmeMOAttribute() throws Exception {

        buildSearchAttributes(DEFAULT_SGSN_MME_NAMESPACE);
        final CmResponse cmResponse = buildCmResponse();

        when(reader.search(any(CmSearchCriteria.class), eq(CmConstants.LIVE_CONFIGURATION))).thenReturn(cmResponse);
        final CmResponse cmResponse2 = beanUnderTest.getMOAttribute(DEFAULT_MO_TYPE, DEFAULT_SGSN_MME_NAMESPACE, DEFAULT_ATTRIBUTE);

        assertTrue(cmResponse.equals(cmResponse2));
        verify(reader).search(any(CmSearchCriteria.class), eq(CmConstants.LIVE_CONFIGURATION));
    }

    /*
     * P R I V A T E - M E T H O D S
     */

    private CmResponse buildCmResponse() {
        final CmResponse cmResponse = new CmResponse();
        final Collection<CmObject> cmObjects = new ArrayList<>(1);
        final CmObject cmObject = new CmObject();
        cmObjects.add(cmObject);
        cmResponse.setStatusCode(0);
        return cmResponse;
    }

    private CmSearchCriteria buildSearchAttributes(final String namespace) {

        final List<String> nodes = new ArrayList<>();
        final String moType = DEFAULT_MO_TYPE;

        final String attribute = DEFAULT_ATTRIBUTE;
        // Add search scope for the nodes
        final List<CmSearchScope> scopeList = getNodeCmSearchScope(nodes);
        final CmObjectSpecification cmObjectSpecification = addSearchCMObject(moType, namespace);

        addSearchAttributes(attribute, cmObjectSpecification);

        final CmSearchCriteria cmSearchCriteria = createSearchCriteria(cmObjectSpecification, scopeList);
        return cmSearchCriteria;
    }

    private List<CmSearchScope> getNodeCmSearchScope(final List<String> nodes) {
        final ArrayList<CmSearchScope> returnList = new ArrayList<CmSearchScope>();

        for (final String node : nodes) {
            final CmSearchScope cmSearchScope = new CmSearchScope();
            cmSearchScope.setScopeType(CmSearchScope.ScopeType.NODE_NAME);
            cmSearchScope.setValue(node);
            cmSearchScope.setCmMatchCondition(CmMatchCondition.EQUALS);
            returnList.add(cmSearchScope);
        }

        return returnList;
    }

    private CmSearchCriteria createSearchCriteria(final CmObjectSpecification cmObjectToSearch, final List<CmSearchScope> scopeList) {
        final CmSearchCriteria cmSearchCriteria = new CmSearchCriteria();
        cmSearchCriteria.setSingleCmObjectSpecification(cmObjectToSearch);
        cmSearchCriteria.setCmSearchScopes(scopeList);
        return cmSearchCriteria;
    }

    private void addSearchAttributes(final String attribute, final CmObjectSpecification cmObjectSpecification) {
        final AttributeSpecification attributeSpecification = new AttributeSpecification();
        attributeSpecification.setName(attribute);
        final AttributeSpecificationContainer attributeSpecifications = new ValidatedAttributeSpecifications();
        attributeSpecifications.addAttributeSpecification(attributeSpecification);
        cmObjectSpecification.setAttributeSpecificationContainer(attributeSpecifications);
    }

    private CmObjectSpecification addSearchCMObject(final String moType, final String namespace) {
        final CmObjectSpecification cmObjectSpecification = new CmObjectSpecification();
        cmObjectSpecification.setType(moType);
        cmObjectSpecification.setNamespace(namespace == null ? DEFAULT_ERBS_NAMESPACE : namespace);
        return cmObjectSpecification;
    }
}
