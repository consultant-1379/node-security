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

import java.util.*;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.oss.services.cm.cmshared.dto.*;

/**
 * Internal class that builds AttributeSpecificationContainer to call CMWriter with from the MoParam / MoParams objects.
 * 
 * @author egbobcs
 *
 */
public class AttributeSpecBuilder {

	@Inject 
	Logger log;

	/**
	 * Builds the AttributeSpecificationContainer from the MoParams object
	 *
	 * @param param
	 * @return AttributeSpecificationContainer
	 */
	public AttributeSpecificationContainer getAttributeSpecCont(final MoParams param) {
		return (AttributeSpecificationContainer)getObject(param);
	}
	
	/**
	 * Recursive method to build the AttributeSpecificationContainer.	 
	 */
	private Object getObject(final MoParam param) {
		switch (param.getParamType()) {
		case SIMPLE:
			log.debug("getObject simple param: {}",param);		
			return param.getParam();
		case LIST:
			log.debug("getObject LIST param: {}",param);
			final List<Object> l = new ArrayList<>();
			for (final MoParam li : MoParam.getList(param)) {
				log.debug("getObject LIST item: {}",li);
				//Recursive call for each items in the LIST
				l.add(getObject(li));
			}	
			return l;
		case MAP:
			log.debug("getObject MAP param: {}",param);
			final Map<String, MoParam> map = MoParams.getParamMap(param);
			//TODO: Use ValidatedAttributeSpecifications as soon as the functionality is verified E2E because
			//it is more efficient
			final AttributeSpecificationContainer c = new StringifiedAttributeSpecifications();
			for (final String key : map.keySet()) {
				//Recursive call for each items in the MAP
				final Object value = getObject(map.get(key));
				log.debug("getObject MAP key/value: {}/{}",key,param);
				addAttributeSpecification(c, key, value);
			}
			return c;
		default: throw new IllegalArgumentException("Invalid ParamType : " + param.getParamType().toString());
		}
	}
	
	private void addAttributeSpecification(final AttributeSpecificationContainer c, final String name, final Object value) {
		log.debug("addAttributeSpecification name/value@Class: {}/{}@{}",name,value,value.getClass().getSimpleName());
		final AttributeSpecification a = new AttributeSpecification();
		a.setName(name);
		a.setValue(value);
		c.addAttributeSpecification(a);
	}
}
