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
package com.ericsson.nms.security.nscs.data.moaction.param;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.services.cm.cmshared.dto.AttributeSpecification;
import com.ericsson.oss.services.cm.cmshared.dto.AttributeSpecificationContainer;

@RunWith(MockitoJUnitRunner.class)
public class TestAttributeBuilder {
	
	@InjectMocks
	AttributeSpecBuilder builder;

	@Spy 
	private final Logger log = LoggerFactory.getLogger(TestAttributeBuilder.class);

	@Test
	public void testBuilderSimple() {
		final MoParams p = new MoParams();
		p.addParam("key", "value");
		final AttributeSpecificationContainer c = builder.getAttributeSpecCont(p);
		
		final Set<String> attributes = c.getAttributeNames();
		assertEquals(1, attributes.size());
			
		final String key = attributes.iterator().next();
		assertEquals("key", key);
		final AttributeSpecification value = c.getAttributeSpecification(key);
		assertEquals("value", value.getValue());				
	}
	
	@Test 		
	public void testBuilderComplex() {	

		//See how TestMoParams.getInstallTrustedCertsParams() builds the params: certSpecList, startTime, duration, accountInfoList.
		//The same object is used here to verify the built CM object.
		final MoParams params = TestMoParams.getInstallTrustedCertsParams();
			
		//The code below shows HOW HARD is to use CM objects to verify the contents of them, 
		//likes of AttributeSpecificationContainer and AttributeSpecification:
	
		//Verify Parent
		final AttributeSpecificationContainer c = builder.getAttributeSpecCont(params);
		 
		final Set<String> attributes = c.getAttributeNames();
		assertEquals(4, attributes.size()); 
		
		//Verify Parent/certSpecList
		final Object cl = c.getAttributeSpecification("certSpecList").getValue();
		assertTrue(cl instanceof List<?>);		
		final List<Object> list = (List<Object>)cl;
		
		//Verify Parent/certSpecList/0
		final Object li = list.iterator().next();
		assertTrue(li instanceof AttributeSpecificationContainer);
		final AttributeSpecificationContainer lic = (AttributeSpecificationContainer)li;
		
		//Verify Parent/certSpecList/fingerprint
		assertEquals("fingerprint", lic.getAttributeNames().iterator().next());		
		final AttributeSpecification lics =  lic.getAttributeSpecification("fingerprint");
		
		//Verify Parent/certSpecList/fingerprint/value
		assertEquals("dummyFingerprint0", lics.getValue());
		
		
		//Verify the rest of the params
		final Object accountInfoList = c.getAttributeSpecification("accountInfoList").getValue();
		assertTrue(accountInfoList instanceof List<?>);
		
		final Object duration = c.getAttributeSpecification("duration").getValue();
		assertTrue(duration instanceof String);	
	}
}
