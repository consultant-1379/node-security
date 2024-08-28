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

import java.util.List;
import javax.ejb.Local;

import com.ericsson.nms.security.nscs.exception.RbacException;
import com.ericsson.oss.services.cache.NodesConfigurationStatusRecord;
import com.ericsson.oss.services.nodes.dto.NodeNamesDTO;
import com.ericsson.oss.services.nodes.dto.NodesDTO;

@Local
public interface NscsNodesListHandler {
    /**
     * It returns a list records that contains the status information about a subset of nodes.
     * The request is paged with offset,limit parameters defined by user.
     * @param dto - the DTO that contains the subset of nodes from which getting status information
     * @param userId - the userId that performs the request
     * @return - list of NodesConfigurationStatusRecord that contains the info about each node of the list
     * @throws RbacException - raised if authorization fails
     */
    List<NodesConfigurationStatusRecord> getPage(NodesDTO dto, String userId) throws RbacException;

    /**
     * It returns the number of nodes that satisfy the conditions defined by user
     * @param dto - the DTO that contains the subset of nodes from which getting status information
     * @param userId - the userId that performs the request
     * @return - number of nodes that satisfy the conditions defined by user
     * @throws RbacException  - raised if authorization fails
     */
    int getCount(NodesDTO dto, String userId) throws RbacException;

    /**
     * It returns a list records that contains the status information of a list of nodes defined by user.
     * No Paging mechanism is implemented
     * @param dto - the DTO that contains the subset of nodes from which getting status information
     * @return - list of NodesConfigurationStatusRecord that contains the info about each node of the list
     * @throws RbacException  - raised if authorization fails
     */
    List<NodesConfigurationStatusRecord> getNodes(NodeNamesDTO dto) throws RbacException;

}