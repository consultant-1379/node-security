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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.exception.DataAccessException;
import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.itpf.sdk.security.accesscontrol.SecurityViolationException;
import com.ericsson.oss.services.cm.cmshared.dto.CmObject;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;
import com.ericsson.oss.services.topologyCollectionsService.exception.TopologyCollectionsServiceException;
import com.ericsson.oss.services.topologySearchService.exception.TopologySearchQueryException;
import com.ericsson.oss.services.topologySearchService.exception.TopologySearchServiceException;
import com.ericsson.oss.services.topologySearchService.service.api.SearchExecutor;
import com.ericsson.oss.services.topologySearchService.service.api.dto.NetworkExplorerResponse;

public class SavedSearchDAO {

    public static final String ATTRIBUTE_USERID = "userId";
    public static final String ATTRIBUTE_CATEGORY = "category";
    public static final String ATTRIBUTE_FDN = "fdn";
    public static final String ATTRIBUTE_NAME = "name";
    public static final String PRIVATE_CATEGORY = "Private";
    public static final String PUBLIC_CATEGORY = "Public";
    public static final String ATTRIBUTE_QUERY_SAVED_SEARCH = "searchQuery";

    @Inject
    NscsCMReaderService service;

    @Inject
    private Logger logger;

    @EServiceRef
    private SearchExecutor searchExecutor;

    public List<Map<String, Object>> getSavedSearchesByPoIds(final List<Long> persistenceObjectIds, String userId) throws SecurityViolationException {

        logger.debug("Getting info from getSavedSearchesByPoIds() method");

        final CmResponse cmResponse = service.getPosByPoIds(persistenceObjectIds);

        final List<Map<String, Object>> moWithAttributesList = new ArrayList<>();
        try {
            if (Math.signum(cmResponse.getStatusCode()) == -1) {
                logger.error("Cm-Reader data access fail with status : {}", cmResponse.getStatusMessage());
                throw new DataAccessException(NscsErrorCodes.UNEXPECTED_INTERNAL_ERROR);
            }

            final Collection<CmObject> savedSearchPO = cmResponse.getCmObjects();

            if (savedSearchPO != null) {

                final Iterator<CmObject> iterate = savedSearchPO.iterator();

                while (iterate.hasNext()) {

                    final CmObject cmObject = iterate.next();

                    final String savedSearchPoUserId = cmObject.getAttributes().get(ATTRIBUTE_USERID).toString();
                    final String category = String.valueOf(cmObject.getAttributes().get(ATTRIBUTE_CATEGORY));

                    if (savedSearchPoUserId.equals(userId) && category.equals(PRIVATE_CATEGORY) || category.equals(PUBLIC_CATEGORY)) {

                        String query = (String) cmObject.getAttributes().get(ATTRIBUTE_QUERY_SAVED_SEARCH);
                        logger.debug("Performing search query [{}]", query);

                        NetworkExplorerResponse resp = searchExecutor.search(query, userId, null);
                        final Iterator<CmObject> innerIterator = resp.getCmObjects().iterator();

                        while (innerIterator.hasNext()) {
                            final CmObject managedObjectMO = innerIterator.next();
                            if (managedObjectMO != null) {
                                final Map<String, Object> moAttributes = new HashMap<>();
                                NodeReference nodeRef = new NodeRef(managedObjectMO.getFdn());
                                NormalizableNodeReference normNode = service.getNormalizedNodeReference(nodeRef);
                                if (normNode != null) {
                                    //moAttributes.put(ATTRIBUTE_FDN, managedObjectMO.getFdn());
                                    moAttributes.put(ATTRIBUTE_NAME, normNode.getName());
                                    moWithAttributesList.add(moAttributes);
                                } else {
                                    logger.error("Cannot get NormalizedNodeReference for node fdn [{}]", managedObjectMO.getFdn());
                                }
                            }
                        }

                        logger.debug("Executed [{}] search queries", (resp.getCmObjects() != null) ? resp.getCmObjects().size() : "zero");

                    } else {
                        logger.error("User \"{}\" attempted to load a savedSearch owned by \"{}\"", userId, savedSearchPoUserId);
                        throw new DataAccessException(NscsErrorCodes.UNEXPECTED_INTERNAL_ERROR);
                    }
                }
            }

            else {
                logger.debug("There is no MOs available for this static collection");
            }
        } catch (final IllegalStateException e) {
            logger.error("Empty response for MoIdList");
            throw new DataAccessException(NscsErrorCodes.UNEXPECTED_INTERNAL_ERROR + ": " + e.getMessage());
        } catch (TopologySearchServiceException e) {
            logger.error("User \"{}\" attempted to load a savedSearch: " + e.getMessage(), userId);
        } catch (TopologySearchQueryException e) {
            logger.error("User \"{}\" attempted to load a savedSearch " + e.getMessage(), userId);
        } catch (TopologyCollectionsServiceException e) {
            logger.error("User \"{}\" attempted to load a savedSearch " + e.getMessage(), userId);
        }
        logger.debug("Returning moWithAttributesList size from getSavedSearchesByPoIds() "+ moWithAttributesList.size());

        return moWithAttributesList;
    }

}
