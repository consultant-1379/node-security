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
package com.ericsson.oss.services.nodes.dto;

import java.io.Serializable;
import java.util.List;

/**
 * dto sent by Node Security Configuration UI
 * @author egicass
 *
 */
public class NodesDTO implements Serializable{

	private NodesFilterDTO filter;
	private List<Long> collectionIds;
	private List<Long> savedSearches;
	private int offset;
	private int limit;
	/**
	 * @return the filter
	 */
	public NodesFilterDTO getFilter() {
		return filter;
	}
	/**
	 * @param filter the filter to set
	 */
	public void setFilter(NodesFilterDTO filter) {
		this.filter = filter;
	}
	
	
	
	public List<Long> getCollectionIds() {
		return collectionIds;
	}
	public void setCollectionIds(List<Long> collectionIds) {
		this.collectionIds = collectionIds;
	}
	/**
	 * @return the offset
	 */
	public int getOffset() {
		return offset;
	}
	/**
	 * @param offset the offset to set
	 */
	public void setOffset(int offset) {
		this.offset = offset;
	}
	/**
	 * @return the limit
	 */
	public int getLimit() {
		return limit;
	}
	/**
	 * @param limit the limit to set
	 */
	public void setLimit(int limit) {
		this.limit = limit;
	}
	
	
	public List<Long> getSavedSearches() {
		return savedSearches;
	}
	public void setSavedSearches(List<Long> savedSearches) {
		this.savedSearches = savedSearches;
	}
	
	
	
	
	
}
