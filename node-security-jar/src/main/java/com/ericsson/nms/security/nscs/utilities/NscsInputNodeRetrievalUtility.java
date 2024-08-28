/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.nms.security.nscs.utilities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.command.types.NscsNodeCommand;
import com.ericsson.nms.security.nscs.api.exception.DataAccessException;
import com.ericsson.nms.security.nscs.api.exception.InvalidCollectionNameException;
import com.ericsson.nms.security.nscs.api.exception.InvalidNodeNameExpressionException;
import com.ericsson.nms.security.nscs.api.exception.InvalidSavedSearchNameException;
import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes;
import com.ericsson.nms.security.nscs.api.exception.UnexpectedErrorException;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.itpf.sdk.security.accesscontrol.EAccessControl;
import com.ericsson.oss.services.cm.cmshared.dto.CmObject;
import com.ericsson.oss.services.security.nscs.context.NscsContextService;
import com.ericsson.oss.services.topologyCollectionsService.api.TopologyCollectionsEjbService;
import com.ericsson.oss.services.topologyCollectionsService.dto.CollectionDTO;
import com.ericsson.oss.services.topologyCollectionsService.dto.ManagedObjectDTO;
import com.ericsson.oss.services.topologyCollectionsService.dto.SavedSearchDTO;
import com.ericsson.oss.services.topologyCollectionsService.exception.TopologyCollectionsServiceException;
import com.ericsson.oss.services.topologySearchService.exception.TopologySearchServiceException;
import com.ericsson.oss.services.topologySearchService.service.api.SearchExecutor;
import com.ericsson.oss.services.topologySearchService.service.api.dto.NetworkExplorerResponse;

/**
 * Utility class to retrieve nodes from command or topology search/collection
 * service.
 *
 * @author xpradks
 */
public class NscsInputNodeRetrievalUtility {

	@Inject
	private Logger logger;

	@EServiceRef
	private SearchExecutor searchExecutor;

	@EServiceRef
	private TopologyCollectionsEjbService topologyCollectionsService;

	@Inject
	private EAccessControl eAccessControl;

	@Inject
	private NscsContextService contextService;

	@Inject
	private NscsNodeUtility nscsNodeUtility;

	/**
	 * This method will retrieve the node references list from the property
	 * values : saved search / collection / expressions provided in the given
	 * command object.
	 *
	 * @param command
	 *            Object of type (or sub type of) NscsNodeCommand.
	 * @return List of Node references.
	 * @throws InvalidSavedSearchNameException
	 *             is thrown when the given saved search name returns empty set.
	 * @throws InvalidNodeNameExpressionException
	 *             is thrown when the given expression returns empty set.
	 * @throws InvalidCollectionNameException
	 *             is thrown when the given collection name returns empty set.
	 * @throws UnexpectedErrorException
	 *             is thrown when internal error occurs while using topology
	 *             service.
	 * @throws DataAccessException
	 *             is thrown when node data is not accessible.
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public List<NodeReference> getNodeReferenceList(final NscsNodeCommand command)
			throws InvalidSavedSearchNameException, InvalidNodeNameExpressionException, InvalidCollectionNameException,
			UnexpectedErrorException, DataAccessException {
		logger.info("Processing command [{}] to retrieve node names from saved search/collection/name expression. ",
				command);
		final List<String> invalidNames = new ArrayList<>();
		final List<NodeReference> inputNodes = new ArrayList<>();
		final List<String> savedSearchNamesList = command.getSavedSearchNames();
		final List<String> collectionNamesList = command.getCollectionNames();
		if (savedSearchNamesList != null && !savedSearchNamesList.isEmpty()) {
			final Set<String> savedSearchNames = new LinkedHashSet<>(savedSearchNamesList);
			validateNames(savedSearchNames, invalidNames);
			if (!invalidNames.isEmpty()) {
				logger.error("Invalid saved search name(s) occured : {}", invalidNames);
				throw new InvalidSavedSearchNameException(NscsErrorCodes.INVALID_SYNTAX_FOR_SAVED_SEARCH_NAME,
						invalidNames);
			}
			inputNodes.addAll(getNodeReferenceListFromSavedSearches(savedSearchNames));
		} else if (collectionNamesList != null && !collectionNamesList.isEmpty()) {
			final Set<String> collectionNames = new LinkedHashSet<>(collectionNamesList);
			validateNames(collectionNames, invalidNames);
			if (!invalidNames.isEmpty()) {
				logger.error("Invalid collection name(s) occured : {}", invalidNames);
				throw new InvalidCollectionNameException(NscsErrorCodes.INVALID_SYNTAX_FOR_COLLECTION_NAME,
						invalidNames);
			}
			inputNodes.addAll(getNodesReferenceListFromCollection(collectionNames));
		} else {
			final Set<String> nodeNamesOrExpressions = new LinkedHashSet<>(command.getNodeNamesOrExpressions());
			inputNodes.addAll(getNodeReferenceListFromNodeNameOrExpression(nodeNamesOrExpressions));
		}
		logger.info("Proceeding with {} node(s) for the command {}..", inputNodes.size(), command);

		return inputNodes;
	}

	/**
	 * This method will validate the given set of strings for the against the
	 * syntax provided for saved search or collection names.
	 *
	 * @param names
	 *            Set of saved search/collection names.
	 */
	private void validateNames(final Set<String> names, List<String> invalidNames) {
		for (final String contextName : names) {
			if (!contextName.matches(Constants.SAVED_SEARCH_OR_COLLECTION_NAMES_SYNTAX)) {
				invalidNames.add(contextName);
			}
		}
	}

	/**
	 * This method will retrieve the node references list from the given set of
	 * saved search names.If the saved search names contains an empty set and a
	 * proper set ,then the return value should contain union of both the sets.
	 *
	 * @param savedSearchNames
	 *            Set of saved search names.
	 * @return List of node references.
	 * @throws InvalidSavedSearchNameException
	 *             is thrown when the given saved search name returns empty set.
	 * @throws UnexpectedErrorException
	 *             is thrown when internal error occurs while using topology
	 *             service.
	 */
	@SuppressWarnings("unchecked")
	private List<NodeReference> getNodeReferenceListFromSavedSearches(final Set<String> savedSearchNames)
			throws InvalidSavedSearchNameException, UnexpectedErrorException {
		logger.info("Retrieving node names from the saved searches {}", savedSearchNames);
		final List<String> invalidNames = new ArrayList<>();
		final List<NodeReference> nodeRefs = new ArrayList<>();
		final Set<String> nodeFdns = new LinkedHashSet<>();
		final String userId = contextService.getUserIdContextValue();
		eAccessControl.setAuthUserSubject(userId);
		final Collection<SavedSearchDTO> ssDtos = getSavedSearchDTOs(userId, savedSearchNames);
		for (final SavedSearchDTO ssDto : ssDtos) {
			final String savedSearchUserId = ssDto.getOwner();
			final String category = String.valueOf(ssDto.getCategory());

			if ((savedSearchUserId.equals(userId) && category.equals(Constants.PRIVATE_CATEGORY))
					|| category.equals(Constants.PUBLIC_CATEGORY)) {
				final String searchQuery = ssDto.getQuery();
				final Set<String> nodeNames = new HashSet<>();
				final String orderBy = "";
				try {
					final NetworkExplorerResponse networkExplorerResponse = searchExecutor.search(searchQuery, userId,
							orderBy);
					nodeNames.addAll(getNodeNamesFromNetworkExplorerResponse(networkExplorerResponse));
				} catch (final TopologySearchServiceException | TopologyCollectionsServiceException e) {
					logger.error("Error while executing the search query {} for the user {} - {}", searchQuery, userId,
							e.getMessage());
					throw new UnexpectedErrorException("Error while executing the search query " + searchQuery
							+ " for the user " + userId + ". Please check the logs.");
				}
				if (!nodeNames.isEmpty()) {
					nodeFdns.addAll(nodeNames);
				}
			} else {
				logger.error("User {} is not allowed to access the savedSearch owned by {}", userId, savedSearchUserId);
				invalidNames.add(ssDto.getName());
			}
		}

		// To check if there are invalid names or nodeFdns is empty
		if (!invalidNames.isEmpty()) {
			throw new InvalidSavedSearchNameException(NscsErrorCodes.UNAUTHORISED_ACCESS_FOR_SAVED_SEARCH_NAME,
					invalidNames);
		} else if (nodeFdns.isEmpty()) {
			logger.error("Saved search name(s) with Empty set occured : {}", savedSearchNames);
			throw new InvalidSavedSearchNameException(NscsErrorCodes.EMPTY_SET_FOR_SAVED_SEARCH_OR_COLLECTION,
					savedSearchNames);
		}

		for (final String fdn : nodeFdns) {
			final NodeReference nodeRef = new NodeRef(fdn);
			nodeRefs.add(nodeRef);
		}

		return nodeRefs;
	}

	/**
	 * This method will retrieve the node references list from the given set of
	 * node name list or expressions.
	 *
	 * @param nodeNamesOrExpressions
	 *            Set of node names or expressions.
	 * @return List of node references.
	 * @throws InvalidNodeNameExpressionException
	 *             is thrown when the given expression returns empty set.
	 * @throws UnexpectedErrorException
	 *             is thrown when internal error occurs while using topology
	 *             service.
	 */
	private List<NodeReference> getNodeReferenceListFromNodeNameOrExpression(final Set<String> nodeNamesOrExpressions)
			throws InvalidNodeNameExpressionException, UnexpectedErrorException {
		logger.info("Retrieving node names from the node list/expression {}", nodeNamesOrExpressions);

		final Set<String> uniqueNodeNames = new LinkedHashSet<>();
		final Set<String> expression = new HashSet<>();
		for (final String nodeNamesOrExpression : nodeNamesOrExpressions) {
			if (nodeNamesOrExpression.contains("*")) {
				expression.add(nodeNamesOrExpression);
			} else {
				uniqueNodeNames.add(nodeNamesOrExpression);
			}
			if (!expression.isEmpty()) {
				uniqueNodeNames.addAll(getNodeNamesFromExpression(expression));
			}
		}
		return NodeRef.from(uniqueNodeNames);
	}

    /**
     * This method will retrieve the node references list from the given set of collection names.If the Collection names contains an empty set and a
     * proper set ,then the return value should contain union of both the sets.
     *
     * @param collectionNames
     *            Set of collection names.
     * @return List of node references.
     * @throws InvalidCollectionNameException
     *             is thrown when the given collection name returns empty set.
     *
     */
    private List<NodeReference> getNodesReferenceListFromCollection(final Set<String> collectionNames) {
        logger.info("Retrieving node names from the collections [{}]", collectionNames);
        final List<NodeReference> nodes = new ArrayList<>();
        final String userId = contextService.getUserIdContextValue();
        eAccessControl.setAuthUserSubject(userId);
        final List<String> invalidCollectionNamesList = new ArrayList<>();
        final Set<String> fdnsList = new LinkedHashSet<>();
        final Collection<CollectionDTO> collectionDTOs = getCollectionDtos(userId, collectionNames);
        for (final CollectionDTO collectionDTO : collectionDTOs) {
            prepareFdnsListAndInvalidCollectionNamesList(collectionDTO, fdnsList, userId, invalidCollectionNamesList, nodes);
        }
        // TODO: Improvement task <> to handle partial success scenario
        if (!invalidCollectionNamesList.isEmpty()) {
            logger.error("Unauthorised access for the collection names {}", invalidCollectionNamesList);
            throw new InvalidCollectionNameException(NscsErrorCodes.UNAUTHORISED_ACCESS_FOR_COLLECTION_NAME, invalidCollectionNamesList);
        }
        if (nodes.isEmpty()) {
            logger.error("Collection name(s) with Empty set occured : {}", collectionNames);
            throw new InvalidCollectionNameException(NscsErrorCodes.EMPTY_SET_FOR_SAVED_SEARCH_OR_COLLECTION, collectionNames);
        }
        return nodes;
    }

    private void prepareFdnsListAndInvalidCollectionNamesList(final CollectionDTO collectionDTO, final Set<String> fdnsList, final String userId,
            final List<String> invalidNames, List<NodeReference> nodes) {
        final CollectionDTO collectionDTOByID = topologyCollectionsService.getCollectionByID(collectionDTO.getId(), userId);
        final String ownerName = collectionDTOByID.getOwner();
        final String category = collectionDTOByID.getCategory().toString();
        if (ownerName.equalsIgnoreCase(userId) || ownerName.equalsIgnoreCase(Constants.AUTOGENERATED_COLLECTIONS_USER_ID)
                || category.equals(Constants.PUBLIC_CATEGORY)) {
            for (final ManagedObjectDTO managedObjectDTO : collectionDTOByID.getElements()) {
                final String fdn = managedObjectDTO.getFdn();
                if (fdn != null && fdnsList.add(fdn)) {
                    final NodeReference nodeRef = new NodeRef(nscsNodeUtility.getNodeNameFromFdn(fdn));
                    nodes.add(nodeRef);
                }
            }
        } else {
            logger.error("User {} is not allowed to access the collection name owned by {}", userId, ownerName);
            invalidNames.add(collectionDTOByID.getName());
        }

    }

	private Collection<CollectionDTO> getCollectionDtos(final String userId, final Set<String> collectionNames)
			throws InvalidCollectionNameException {
		logger.info("Retrieving collection dtos for the collections {}", collectionNames);
		final Collection<CollectionDTO> collectionDtos = new ArrayList<>();
		final List<String> invalidNames = new ArrayList<>();
		for (final String collectionName : collectionNames) {
			final Collection<CollectionDTO> collectionDto = topologyCollectionsService.getCollectionsByName(userId,
					collectionName);
			if (collectionDto == null || collectionDto.isEmpty()) {
				invalidNames.add(collectionName);
				continue;
			}
			collectionDtos.addAll(collectionDto);
		}
		if (!invalidNames.isEmpty()) {
			logger.error("Collection name {} returns empty set.", invalidNames);
			throw new InvalidCollectionNameException(invalidNames);
		}
		return collectionDtos;
	}

	private Collection<SavedSearchDTO> getSavedSearchDTOs(final String userId, final Set<String> savedSearchNames)
			throws InvalidSavedSearchNameException {
		logger.info("Retrieving saved search dtos for the names {}", savedSearchNames);

		final List<String> invalidNames = new ArrayList<>();
		final Collection<SavedSearchDTO> ssDtoCollection = new ArrayList<>();

		for (final String savedSearchName : savedSearchNames) {
			final Collection<SavedSearchDTO> savedSearchDtos = topologyCollectionsService.getSavedSearchesByName(userId,
					savedSearchName);
			if (savedSearchDtos == null || savedSearchDtos.isEmpty()) {
				invalidNames.add(savedSearchName);
			} else {
				ssDtoCollection.addAll(savedSearchDtos);
			}
		}
		if (!invalidNames.isEmpty()) {
			logger.error("Saved search {} returns empty set.", invalidNames);
			throw new InvalidSavedSearchNameException(invalidNames);
		}
		return ssDtoCollection;
	}

	private Set<String> getNodeNamesFromExpression(final Set<String> expressions)
			throws InvalidNodeNameExpressionException, UnexpectedErrorException {
		logger.info("Retrieving node names from the search query {}", expressions);
		final List<String> invalidNames = new ArrayList<>();
		final String userId = contextService.getUserIdContextValue();
		eAccessControl.setAuthUserSubject(userId);
		final Set<String> nodeNames = new HashSet<>();
		for (String expr : expressions) {
			try {
				final String orderBy = "";
				final NetworkExplorerResponse networkExplorerResponse = searchExecutor.search(expr, userId, orderBy);
				nodeNames.addAll(getNodeNamesFromNetworkExplorerResponse(networkExplorerResponse));
			} catch (final TopologySearchServiceException | TopologyCollectionsServiceException e) {
				logger.error("Error while executing the search query {} for the user {} - {}", expressions, userId,
						e.getMessage());
				throw new UnexpectedErrorException("Error while executing the search query " + expressions
						+ " for the user " + userId + " Please check the logs.");
			}
			if (nodeNames.isEmpty()) {
				invalidNames.add(expr);
			}
		}
		if (!invalidNames.isEmpty()) {
			logger.error("Node name expression {} returns empty set.", invalidNames);
			throw new InvalidNodeNameExpressionException(invalidNames);
		}
		return nodeNames;
	}

	private Set<String> getNodeNamesFromNetworkExplorerResponse(final NetworkExplorerResponse networkExplorerResponse) {
		final Collection<CmObject> cmObjects = new LinkedList<>();
		cmObjects.addAll(networkExplorerResponse.getCmObjects());
		final Set<String> nodeNames = new HashSet<>(cmObjects.size());
		for (final CmObject cmObject : cmObjects) {
			final String fdn = cmObject.getFdn();
			if (fdn != null && !fdn.isEmpty()) {
				nodeNames.add(nscsNodeUtility.getNodeNameFromFdn(fdn));
			}
		}
		return nodeNames;
	}
}
