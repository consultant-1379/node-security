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
package com.ericsson.oss.services.nscs.nodes.dao;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.oss.itpf.sdk.security.accesscontrol.SecurityViolationException;
import com.ericsson.oss.services.cm.cmshared.dto.CmObject;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;
import com.ericsson.oss.services.topologyCollectionsService.exception.TopologyCollectionsServiceException;
import com.ericsson.oss.services.topologySearchService.exception.TopologySearchQueryException;
import com.ericsson.oss.services.topologySearchService.exception.TopologySearchServiceException;
import com.ericsson.oss.services.topologySearchService.service.api.SearchExecutor;
import com.ericsson.oss.services.topologySearchService.service.api.dto.NetworkExplorerResponse;

@RunWith(MockitoJUnitRunner.class)
public class SavedSearchDAOTest {

	public static final String ATTRIBUTE_USERID = "userId";
	public static final String ATTRIBUTE_CATEGORY = "category";
	public static final String ATTRIBUTE_QUERY_SAVED_SEARCH = "searchQuery";
	public static final String PRIVATE_CATEGORY = "Private";

	@Spy
	private Logger logger = LoggerFactory.getLogger(SavedSearchDAOTest.class);

	@InjectMocks
	SavedSearchDAO instance;

	@Mock
	NscsCMReaderService service;

	@Mock
	SearchExecutor searchExecutor;

	List<Long> persistenceObjectIds;
	List<Long> moIDList;


	@Before
	public void setup(){
		persistenceObjectIds = new ArrayList<Long>();
		persistenceObjectIds.add(new Long(1));

		moIDList = new ArrayList<Long>();
		moIDList.add(new Long(1));
	}

	@Test
	public void getSavedSearchesByPoIds(){
		CmResponse cmResponse = Mockito.mock(CmResponse.class);
		when(service.getPosByPoIds(persistenceObjectIds)).thenReturn(cmResponse);
		CmObject cmObject = Mockito.mock(CmObject.class);
		List<CmObject> cmObjects = new ArrayList<CmObject>();
		cmObjects.add(cmObject);	
		cmObjects.add(cmObject);
		when(cmResponse.getCmObjects()).thenReturn(cmObjects);
		Iterator<CmObject> iterate = Mockito.mock(Iterator.class);
		when(iterate.hasNext()).thenReturn(true);
		when(iterate.next()).thenReturn(cmObject);
		Map<String,Object> m = Mockito.mock(HashMap.class);
		when(cmObject.getAttributes()).thenReturn(m);
		when(m.get(ATTRIBUTE_USERID)).thenReturn("Administrator");
		when(cmObject.getAttributes().get(ATTRIBUTE_CATEGORY)).thenReturn(PRIVATE_CATEGORY);
		NetworkExplorerResponse resp = Mockito.mock(NetworkExplorerResponse.class);
		try {
			when(searchExecutor.search(Mockito.anyString(), Mockito.anyString(),Mockito.anyString())).thenReturn(resp);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		List<CmObject> cmObjects2 = new ArrayList<CmObject>();
		cmObjects2.add(cmObject);	
		when(resp.getCmObjects()).thenReturn(cmObjects2);
		when(iterate.hasNext()).thenReturn(true);
		when(iterate.next()).thenReturn(cmObject);
		when(cmObject.getFdn()).thenReturn("NetworkElement=LMI_eRBS01");
		NormalizableNodeReference normNode = Mockito.mock(NormalizableNodeReference.class);
		when(service.getNormalizedNodeReference((NodeReference) Matchers.anyObject())).thenReturn(normNode);
		when(normNode.getName()).thenReturn("LMI_eRBS01");	
		final List<Map<String, Object>> moWithAtrributesList = instance.getSavedSearchesByPoIds(persistenceObjectIds, "Administrator");
		assertTrue(moWithAtrributesList.size()==2);
		//assertTrue(moWithAtrributesList.get(0).get("fdn").equals("NetworkElement=LMI_eRBS01"));
		assertTrue(moWithAtrributesList.get(0).get("name").equals("LMI_eRBS01"));
	}
}
