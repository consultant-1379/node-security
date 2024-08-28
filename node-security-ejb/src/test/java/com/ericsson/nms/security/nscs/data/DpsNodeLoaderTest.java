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
package com.ericsson.nms.security.nscs.data;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.utilities.NscsNodeUtility;
import com.ericsson.oss.itpf.datalayer.dps.DataBucket;
import com.ericsson.oss.itpf.datalayer.dps.DataPersistenceService;
import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject;
import com.ericsson.oss.itpf.datalayer.dps.query.Query;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryBuilder;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryExecutor;
import com.ericsson.oss.itpf.datalayer.dps.query.TypeRestrictionBuilder;
import com.ericsson.oss.itpf.datalayer.dps.query.projection.Projection;
import com.ericsson.oss.services.cache.NodesConfigurationStatusRecord;
import com.ericsson.oss.services.cm.cmshared.dto.CmObject;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;
import com.ericsson.oss.services.cm.dto.mapping.DpsObjectMapper;

@RunWith(MockitoJUnitRunner.class)
public class DpsNodeLoaderTest {

    public static final String ATTRIBUTE_FDN = "fdn";
    public static final String ATTRIBUTE_NETYPE = "neType";

    @Spy
    private Logger logger = LoggerFactory.getLogger(DpsNodeLoaderTest.class);

    @InjectMocks
    DpsNodeLoader instance;

    @Mock
    DataPersistenceService dataPersistenceService;

    @Mock
    QueryExecutor queryExecutor;

    @Mock
    DataBucket liveBucket;

    @Mock
    QueryBuilder queryBuilder;

    @Mock
    Query<TypeRestrictionBuilder> typeQuery;

    @Mock
    DpsObjectMapper mapper;

    @Mock
    ManagedObject managedObject;

    @Mock
    private NscsCMReaderService readerService;

    @Mock
    private NscsNodeUtility nodeUtility;

    List<Object[]> mos;

    @Before
    public void setup() {

        when(dataPersistenceService.getLiveBucket()).thenReturn(liveBucket);
        when(dataPersistenceService.getQueryBuilder()).thenReturn(queryBuilder);
        when(liveBucket.getQueryExecutor()).thenReturn(queryExecutor);

        final Object[][] nodes = new Object[][] { { "LMI_eRBS01", "ERBS" }, { "LMI_eRBS02", "ERBS" } };

        mos = Arrays.asList(nodes);

    }

    @Test
    public void getAllNodesTest() {

        when(queryBuilder.createTypeQuery(DpsNodeLoader.OSS_NE_DEF, DpsNodeLoader.NETWORK_ELEMENT)).thenReturn(typeQuery);
        when(queryExecutor.executeProjection((Query<TypeRestrictionBuilder>) Matchers.anyObject(), (Projection) Matchers.anyObject(),
                (Projection) Matchers.anyObject())).thenReturn(mos);
        final Iterator<Object> poListIterator = Mockito.mock(Iterator.class);

        final CmObject cmObject = Mockito.mock(CmObject.class);
        when(queryExecutor.execute(typeQuery)).thenReturn(poListIterator);
        when(poListIterator.hasNext()).thenReturn(true, false);
        when(poListIterator.next()).thenReturn(managedObject);
        final DataBucket bucket = Mockito.mock(DataBucket.class);
        final ManagedObject mo = Mockito.mock(ManagedObject.class);

        //when(mapper.mapToCmObject(persistenceObject, DpsObjectMapper.INCLUDE_ALL_ATTRIBUTES)).thenReturn(cmObject);

        when(cmObject.getName()).thenReturn("LMI_eRBS01");
        when(cmObject.getFdn()).thenReturn("NetworkElement=LMI_eRBS01");

        when(managedObject.getName()).thenReturn("LMI_eRBS01");
        when(managedObject.getFdn()).thenReturn("NetworkElement=LMI_eRBS01");

        final NormalizableNodeReference normNode = Mockito.mock(NormalizableNodeReference.class);
        when(normNode.getFdn()).thenReturn("NetworkElement=LMI_eRBS01");

        when(readerService.getNormalizedNodeReference((NodeReference) Matchers.anyObject())).thenReturn(normNode);
        when(normNode.getNeType()).thenReturn("ERBS");
        final CmResponse response = Mockito.mock(CmResponse.class);
        final List<CmObject> cmObjects = new ArrayList<CmObject>();
        cmObjects.add(cmObject);
        cmObjects.add(cmObject);
        when(response.getCmObjects()).thenReturn(cmObjects);

        when(bucket.findMoByFdn(Mockito.anyString())).thenReturn(mo);
        when(mo.getAttribute(Mockito.anyString())).thenReturn(response);

        when(nodeUtility.isSecurityLevelSupported(normNode)).thenReturn(true);
        when(nodeUtility.getNodeIpAddress(normNode)).thenReturn("192.168.1.101");
        when(nodeUtility.getSecurityLevel(normNode, "UNSYNCHRONIZED")).thenReturn("LEVEL_1");

        // final List<ManagedObject> map = instance.getAllNodes();
        // assertTrue(map.size() > 0);
        //assertFalse(map.containsKey("ERBS1008"));

    }

    @Test
    public void getNodeTest() {

        final String nodeFdn = "NetworkElement=LMI_eRBS01";
        final String nodeName = "LMI_eRBS01";
        final ManagedObject mo = Mockito.mock(ManagedObject.class);
        final CmResponse response = Mockito.mock(CmResponse.class);
        final DataBucket bucket = Mockito.mock(DataBucket.class);
        when(dataPersistenceService.getLiveBucket()).thenReturn(bucket);
        when(bucket.findMoByFdn(nodeFdn)).thenReturn(mo);

        final NormalizableNodeReference normNode = Mockito.mock(NormalizableNodeReference.class);
        when(readerService.getNormalizedNodeReference((NodeReference) Matchers.anyObject())).thenReturn(normNode);
        when(normNode.getNeType()).thenReturn("ERBS");
        when(normNode.getFdn()).thenReturn(nodeFdn);

        when(bucket.findMoByFdn(Mockito.anyString())).thenReturn(mo);
        when(mo.getAttribute(Mockito.anyString())).thenReturn(response);

        when(nodeUtility.isSecurityLevelSupported(normNode)).thenReturn(true);
        when(nodeUtility.getNodeIpAddress(normNode)).thenReturn("192.168.1.101");
        when(nodeUtility.getSecurityLevel(normNode, "UNSYNCHRONIZED")).thenReturn("LEVEL_1");
        final NodesConfigurationStatusRecord record = instance.getNode(nodeName);
        assertTrue(record.getIpaddress().equals("192.168.1.101"));
        assertTrue(record.getOperationalsecuritylevel().equals("LEVEL_1"));
        assertTrue(record.getSyncstatus() == "UNSYNCHRONIZED");

    }

}
