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
package com.ericsson.oss.services.nscs.nodes.interfaces;

import com.ericsson.nms.security.nscs.ejb.startup.NscsNodesDataLoader;
import com.ericsson.oss.services.cache.NodesConfigurationStatusRecord;
import com.ericsson.oss.services.nscs.nodes.cache.service.interfaces.NscsNodesCacheHandler;

import javax.ejb.Local;

@Local
public interface CacheObserver {
	void update(NodesConfigurationStatusRecord node);
}
