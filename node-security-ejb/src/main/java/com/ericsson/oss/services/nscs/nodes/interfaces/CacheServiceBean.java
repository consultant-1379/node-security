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

import com.ericsson.oss.services.cache.NodesConfigurationStatusRecord;

public interface CacheServiceBean {

    /**
     * Loads from DPS all existent NetworkElement MOs to Node Cache.
     *
     * @deprecated This method is for DEBUG PURPOSES ONLY (to manually load the cache with all existent nodes). If the number of existent
     *             NetworkElement MOs is big, the transaction timeout could be exceeded!
     *
     */
    @Deprecated
    void update();

    /**
     * Clears the Node Cache.
     */
    void clear();

    /**
     * Gets the number of nodes in Node Cache.
     *
     * @return the number of nodes in cache
     */
    int count();

    /**
     * Gets the record from the Node Cache for the given node name.
     *
     * @param nodeName
     *            the node name
     * @return the node record
     */
    NodesConfigurationStatusRecord getNode(String nodeName);

    /**
     * Returns from the Node Cache records between the specified offset (from index), inclusive, and limit (to index as string), exclusive. If limit
     * is "*" all records starting from offset are returned.
     *
     * @param offset
     *            the from index
     * @param limit
     *            the to index (as string) or "*"
     * @return the list of requested records
     * @throws Exception
     *             if limit cannot be converted to an integer
     */
    List<NodesConfigurationStatusRecord> content(Integer offset, String limit) throws Exception;

}