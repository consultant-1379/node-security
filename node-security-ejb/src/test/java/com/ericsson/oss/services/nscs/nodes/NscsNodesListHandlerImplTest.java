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
package com.ericsson.oss.services.nscs.nodes;

import java.util.*;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.services.cache.NodesConfigurationStatusRecord;
import com.ericsson.oss.services.nodes.dto.NodesDTO;
import com.ericsson.oss.services.nscs.nodes.cache.service.interfaces.NscsNodesCacheHandler;
import com.ericsson.oss.services.nscs.nodes.dao.CollectionsDAO;

@RunWith(MockitoJUnitRunner.class)
public class NscsNodesListHandlerImplTest {

	private final static String FAKE_USER = "Administrator";

	@Spy
	private Logger logger = LoggerFactory.getLogger(NscsNodesListHandlerImplTest.class);

	@InjectMocks
	NodesDTO dto;

	@InjectMocks
	NscsNodesListHandlerImpl instance;

	@Mock
	NscsNodesCacheHandler cacheHandler;

	@Mock    
	CollectionsDAO dao;

	List<Long> collectionIds;
	List<Map<String,Object>> nodes;

	@Before
	public void setup(){
		collectionIds = Collections.singletonList(1L);
		dto.setCollectionIds(collectionIds);
		dto.setOffset(5);
		dto.setLimit(15);

		nodes = new ArrayList<Map<String,Object>>(){
	        {add(new HashMap<String, Object>(){{put("name","LMI_eRBS01");}{put("neType","ERBS");}});}
			{add(new HashMap<String, Object>(){{put("name","LMI_eRBS02");}{put("neType","ERBS");}});}
			{add(new HashMap<String, Object>(){{put("name","LMI_eRBS03");}{put("neType","ERBS");}});}
			{add(new HashMap<String, Object>(){{put("name","LMI_eRBS04");}{put("neType","ERBS");}});}
			{add(new HashMap<String, Object>(){{put("name","LTE02ERBS08");}{put("neType","ERBS");}});}
			{add(new HashMap<String, Object>(){{put("name","LTE03ERBS00001");}{put("neType","ERBS");}});}
			{add(new HashMap<String, Object>(){{put("name","LTE03ERBS002");}{put("neType","ERBS");}});}
			{add(new HashMap<String, Object>(){{put("name","LTE02ERBS04");}{put("neType","ERBS");}});}
			{add(new HashMap<String, Object>(){{put("name","LTE02ERBS09");}{put("neType","ERBS");}});}
			{add(new HashMap<String, Object>(){{put("name","LTE03ERBS00002");}{put("neType","ERBS");}});}
			{add(new HashMap<String, Object>(){{put("name","LTE03ERBS003");}{put("neType","ERBS");}});}
			{add(new HashMap<String, Object>(){{put("name","LTE02ERBS05");}{put("neType","ERBS");}});}
			{add(new HashMap<String, Object>(){{put("name","LMI_eRBS05");}{put("neType","ERBS");}});}
			{add(new HashMap<String, Object>(){{put("name","LMI_eRBS06");}{put("neType","ERBS");}});}
			{add(new HashMap<String, Object>(){{put("name","LTE02ERBS10");}{put("neType","ERBS");}});}
			{add(new HashMap<String, Object>(){{put("name","LTE03ERBS00003");}{put("neType","ERBS");}});}
			{add(new HashMap<String, Object>(){{put("name","LTE03ERBS0021");}{put("neType","ERBS");}});}
			{add(new HashMap<String, Object>(){{put("name","LTE02ERBS06");}{put("neType","ERBS");}});}
			{add(new HashMap<String, Object>(){{put("name","LMI_eRBS07");}{put("neType","ERBS");}});}
			{add(new HashMap<String, Object>(){{put("name","LMI_eRBS08");}{put("neType","ERBS");}});}
			{add(new HashMap<String, Object>(){{put("name","LTE02ERBS11");}{put("neType","ERBS");}});}
			{add(new HashMap<String, Object>(){{put("name","LTE03ERBS00004");}{put("neType","ERBS");}});}
			{add(new HashMap<String, Object>(){{put("name","LTE03ERBS004");}{put("neType","ERBS");}});}
			{add(new HashMap<String, Object>(){{put("name","LTE02ERBS07");}{put("neType","ERBS");}});}
			{add(new HashMap<String, Object>(){{put("name","LMI_eRBS011");}{put("neType","ERBS");}});}
			{add(new HashMap<String, Object>(){{put("name","LMI_eRBS021");}{put("neType","ERBS");}});}
			{add(new HashMap<String, Object>(){{put("name","LTE02ERBS081");}{put("neType","ERBS");}});}
			{add(new HashMap<String, Object>(){{put("name","LTE02ERBS041");}{put("neType","ERBS");}});}
			{add(new HashMap<String, Object>(){{put("name","LMI_eRBS032");}{put("neType","ERBS");}});}
			{add(new HashMap<String, Object>(){{put("name","LMI_eRBS042");}{put("neType","ERBS");}});}
			{add(new HashMap<String, Object>(){{put("name","LTE02ERBS092");}{put("neType","ERBS");}});}
			{add(new HashMap<String, Object>(){{put("name","LTE02ERBS052");}{put("neType","ERBS");}});}
			{add(new HashMap<String, Object>(){{put("name","LMI_eRBS053");}{put("neType","ERBS");}});}
			{add(new HashMap<String, Object>(){{put("name","LMI_eRBS063");}{put("neType","ERBS");}});}
			{add(new HashMap<String, Object>(){{put("name","LTE02ERBS103");}{put("neType","ERBS");}});}
			{add(new HashMap<String, Object>(){{put("name","LTE02ERBS063");}{put("neType","ERBS");}});}
			{add(new HashMap<String, Object>(){{put("name","LMI_eRBS074");}{put("neType","ERBS");}});}
			{add(new HashMap<String, Object>(){{put("name","LMI_eRBS084");}{put("neType","ERBS");}});}
			{add(new HashMap<String, Object>(){{put("name","LTE02ERBS114");}{put("neType","ERBS");}});}
			{add(new HashMap<String, Object>(){{put("name","LTE02ERBS074");}{put("neType","ERBS");}});}
		};

		Mockito.when(dao.getCollectionsByPoIds(collectionIds, "Administrator")).thenReturn(nodes);

		int count=0;

		for(Map<String,Object> m : nodes ){
			String name = m.get("name").toString();
			
			NodesConfigurationStatusRecord record = new NodesConfigurationStatusRecord();
			record.setIpaddress("127.0.0.1");
			record.setName(name);
			if(count<10){
				record.setOperationalsecuritylevel("SL2");
				count++;
			}else
				record.setOperationalsecuritylevel("SL1");
			record.setSyncstatus("SYNCHRONIZED");
			record.setType("ERBS");
			
			Mockito.when((cacheHandler).getNode(name)).thenReturn(record);
		}
	}

	@Test
	public void testCount(){
		Assert.assertEquals(40, instance.getCount(dto, FAKE_USER));
	}

	@Test
	public void testPage(){
		logger.info("the number of returned object in a page should be 15");
		Assert.assertEquals(15, instance.getPage(dto, FAKE_USER).size());
	}
}
