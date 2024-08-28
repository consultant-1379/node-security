/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2022
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
*******************************************************************************
*----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.exception.DataAccessException;
import com.ericsson.nms.security.nscs.api.exception.DataAccessSystemException;
import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.exception.PropertiesFileNotFoundException;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation.ModelIdentifierType;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.api.model.service.NscsTargetPO;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.util.PropertiesReader;
import com.ericsson.oss.itpf.datalayer.dps.DataPersistenceService;
import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject;
import com.ericsson.oss.itpf.datalayer.dps.persistence.PersistenceObject;
import com.ericsson.oss.itpf.modeling.common.info.ModelInfo;
import com.ericsson.oss.itpf.modeling.modelservice.exception.UnknownModelException;
import com.ericsson.oss.itpf.modeling.modelservice.exception.UnknownSchemaException;
import com.ericsson.oss.itpf.modeling.modelservice.meta.ModelMetaInformation;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.services.cm.cmreader.api.CmReaderService;
import com.ericsson.oss.services.cm.cmshared.dto.AttributeSpecification;
import com.ericsson.oss.services.cm.cmshared.dto.AttributeSpecificationContainer;
import com.ericsson.oss.services.cm.cmshared.dto.CmConstants;
import com.ericsson.oss.services.cm.cmshared.dto.CmObject;
import com.ericsson.oss.services.cm.cmshared.dto.CmObjectSpecification;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;
import com.ericsson.oss.services.cm.cmshared.dto.ValidatedAttributeSpecifications;
import com.ericsson.oss.services.cm.cmshared.dto.search.CmMatchCondition;
import com.ericsson.oss.services.cm.cmshared.dto.search.CmSearchCriteria;
import com.ericsson.oss.services.cm.cmshared.dto.search.CmSearchScope;
import com.ericsson.oss.services.cm.dto.mapping.DpsObjectMapper;

/**
 * Auxiliary class to provide access to information stored in DPS.
 *
 * Created by emaynes on 02/05/2014.
 */
public class NscsCMReaderService {

    private static final String ACCEPTANCE_TESTS_PROP = "acceptance.tests.env";

    private enum DeploymentEnvironment {
        PRODUCTION, ACCEPTANCE_TESTS, UNKNOWN
    }

    static DeploymentEnvironment deploymentEnv = DeploymentEnvironment.UNKNOWN;

    @Inject
    private Logger logger;

    @EServiceRef
    private CmReaderService reader;

    @Inject
    DpsObjectMapper mapper;

    @EServiceRef
    private DataPersistenceService dataPersistenceService;

    @Inject
    private ModelMetaInformation modelMetaInformation;

    private static final String DEFAULT_ERBS_NAMESPACE = "ERBS_NODE_MODEL";
    private static final String DPS_PRIMARY_TYPE_SCHEMA = "dps_primarytype";

    /**
     * Retrieve the value of a given attribute of a given MO type of a given namespace for ALL nodes.
     *
     * @param moType
     *            the type of searched MOs
     * @param namespace
     *            the namespace of searched MOs
     * @param attribute
     *            the searched attribute
     * @return a CmResponse containing the list of all matching MOs
     * @throws DataAccessException
     *             if CmResponse getStatusCode() < 0
     */
    public CmResponse getMOAttribute(final String moType, final String namespace, final String attribute) {
        return getMOAttribute(moType, namespace, attribute, Collections.<String> emptyList());
    }

    /**
     * Retrieve the value of a given attribute of a given MO type of DEFAULT_ERBS_NAMESPACE namespace for ALL nodes.
     *
     * @param moType
     *            the type of searched MOs
     * @param attribute
     *            the searched attribute
     * @return a CmResponse containing the list of all matching MOs
     * @throws DataAccessException
     *             if CmResponse getStatusCode() < 0
     */
    public CmResponse getMOAttribute(final String moType, final String attribute) {
        return getMOAttribute(moType, DEFAULT_ERBS_NAMESPACE, attribute, Collections.<String> emptyList());
    }

    /**
     * Retrieve the value of a given attribute of a given MO type of DEFAULT_ERBS_NAMESPACE namespace for a list of nodes specified by their FDN
     *
     * @param moType
     *            the type of searched MOs
     * @param attribute
     *            the searched attribute
     * @param nodes
     *            the list of the FDNs of the desired nodes
     * @return a CmResponse containing the list of all matching MOs
     * @throws DataAccessException
     *             if CmResponse getStatusCode() < 0
     */
    public CmResponse getMOAttribute(final String moType, final String attribute, final List<String> nodes) {
        return getMOAttribute(moType, DEFAULT_ERBS_NAMESPACE, attribute, nodes);
    }

    /**
     * Retrieve the value of a given attribute of a given MO type of DEFAULT_ERBS_NAMESPACE namespace for a list of nodes specified by their
     * NodeReference
     *
     * @param nodes
     *            the list of the NpodeReference of the desired nodes
     * @param moType
     *            the type of searched MOs
     * @param attribute
     *            the searched attribute
     * @return a CmResponse containing the list of all matching MOs
     * @throws DataAccessException
     *             if CmResponse getStatusCode() < 0
     */
    public CmResponse getMOAttribute(final List<? extends NodeReference> nodes, final String moType, final String attribute) {
        return getMOAttribute(nodes, moType, DEFAULT_ERBS_NAMESPACE, attribute);
    }

    /**
     * Retrieve the value of a given attribute of a given MO type of a given namespace for a list of nodes specified by their NodeReference
     *
     * @param nodes
     *            the list of the NodeReference of the desired nodes
     * @param moType
     *            the type of searched MOs
     * @param namespace
     *            the namespace of searched MOs
     * @param attribute
     *            the searched attribute
     * @return a CmResponse containing the list of all matching MOs
     * @throws DataAccessException
     *             if CmResponse getStatusCode() < 0
     */
    public CmResponse getMOAttribute(final List<? extends NodeReference> nodes, final String moType, final String namespace, final String attribute) {
        return getMOAttribute(nodes, moType, namespace, attribute, null);
    }

    /**
     * Retrieve the given attribute with given value of a given MO type of a given namespace for a list of nodes specified by their NodeReference
     *
     * @param nodes
     *            the list of the NodeReference of the desired nodes
     * @param moType
     *            the type of searched MOs
     * @param namespace
     *            the namespace of searched MOs
     * @param attribute
     *            the searched attribute
     * @param value
     *            the searched attribute value
     * @return a CmResponse containing the list of all matching MOs
     * @throws DataAccessException
     *             if CmResponse getStatusCode() < 0
     */
    public CmResponse getMOAttribute(final List<? extends NodeReference> nodes, final String moType, final String namespace, final String attribute,
            final String value) {
        return getMOAttribute(moType, namespace, attribute, value, NodeRef.toFdns(nodes));
    }

    /**
     * Retrieve the given attribute with given value of a given MO type of a given namespace for a list of nodes specified by their FDN.
     *
     * Note that this is the "core" method invoked by all getMOAttribute methods.
     *
     * @param moType
     *            the type of searched MOs
     * @param namespace
     *            the namespace of searched MOs
     * @param attribute
     *            the searched attribute
     * @param value
     *            the value of searched attribute
     * @param nodesFdn
     *            the specified FDNs
     * @return a CmResponse containing the list of all matching MOs
     * @throws DataAccessException
     *             if CmResponse getStatusCode() < 0
     */
    public CmResponse getMOAttribute(final String moType, final String namespace, final String attribute, final String value,
            final List<String> nodesFdn) {

        logger.debug("getMOAttribute for attribute [{}] moType [{}] and nameSpace [{}] ", attribute, moType, namespace);
        // Add object specification
        final CmObjectSpecification cmObjectSpecification = addSearchCMObject(moType, namespace);

        // Add search scope for the nodes
        final List<CmSearchScope> scopeList = getNodeCmSearchScope(nodesFdn);

        addSearchAttributes(attribute, value, cmObjectSpecification);

        final CmSearchCriteria cmSearchCriteria = createSearchCriteria(cmObjectSpecification, scopeList);

        final CmResponse cmResponse = reader.search(cmSearchCriteria, CmConstants.LIVE_CONFIGURATION);

        return exceptionIfFail(cmResponse);
    }

    /**
     * Retrieve the value of a given attribute of a given MO type of a given namespace for a list of nodes specified by their FDN
     *
     * @param moType
     *            the type of searched MOs
     * @param namespace
     *            the namespace of searched MOs
     * @param attribute
     *            the searched attribute
     * @param nodesFdn
     *            the specified FDNs
     * @return a CmResponse containing the list of all matching MOs
     * @throws DataAccessException
     *             if CmResponse getStatusCode() < 0
     */
    public CmResponse getMOAttribute(final String moType, final String namespace, final String attribute, final List<String> nodesFdn) {
        return getMOAttribute(moType, namespace, attribute, null, nodesFdn);
    }

    /**
     * Retrieve the value of a given attribute of a given MO type of a given namespace for a given node specified by its FDN
     *
     * @param nodeFdn
     *            the specified FDN
     * @param moType
     *            the type of searched MOs
     * @param namespace
     *            the namespace of searched MOs
     * @param attribute
     *            the searched attribute
     * @return a CmResponse containing the list of all matching MOs
     * @throws DataAccessException
     *             if CmResponse getStatusCode() < 0
     */
    public CmResponse getMOAttribute(final String nodeFdn, final String moType, final String namespace, final String attribute) {
        return nodeFdn == null ? new CmResponse() : getMOAttribute(moType, namespace, attribute, Arrays.asList(nodeFdn));
    }

    /**
     * Retrieve the value of a given attribute of a given MO type of a given namespace for a given node specified by its NodeReference
     *
     * @param node
     *            the specified NodeReference
     * @param moType
     *            the type of searched MOs
     * @param namespace
     *            the namespace of searched MOs
     * @param attribute
     *            the searched attribute
     * @return a CmResponse containing the list of all matching MOs
     * @throws DataAccessException
     *             if CmResponse getStatusCode() < 0
     */
    public CmResponse getMOAttribute(final NodeReference node, final String moType, final String namespace, final String attribute) {
        return node == null ? new CmResponse() : getMOAttribute(Arrays.asList(node), moType, namespace, attribute);
    }

    /**
     * Return the MO according to provided node FDN
     *
     * @param node
     *            a NodeReference instance
     * @return
     * @throws DataAccessException
     *             if CmResponse getStatusCode() < 0
     */
    public CmResponse getMoByFdn(final NodeReference node) {
        return getMoByFdn(node.getFdn());
    }

    /**
     * Return the MO according to provided FDN
     *
     * @param fdn
     * @return
     * @throws DataAccessException
     *             if CmResponse getStatusCode() < 0
     */
    public CmResponse getMoByFdn(final String fdn) throws DataAccessException, DataAccessSystemException {
        logger.debug("getMoByFdn for fdn [{}] ", fdn);
        final CmResponse response = reader.getMoByFdn(fdn, CmConstants.LIVE_CONFIGURATION);
        exceptionIfFail(response);
        return response;
    }

    /**
     * Fetches the ModelInfo information from ModelMetaInformation service
     *
     * @param modelType
     * @param namespace
     * @return ModelInfo
     */
    public ModelInfo getModelInfo(final String modelType, final String namespace) {

        ModelInfo modelInfo = null;
        try {
            modelInfo = modelMetaInformation.getLatestVersionOfModel(DPS_PRIMARY_TYPE_SCHEMA, namespace, modelType);
        } catch (UnknownModelException | UnknownSchemaException e) {
            logger.error("Error fetching model info", e);
            throw new DataAccessException(e);
        }

        return modelInfo;
    }

    /**
     * Fetches the model version information from ModelMetaInformation service
     *
     * @param modelType
     * @param namespace
     * @return String representing model version
     */
    public String getModelVersion(final String modelType, final String namespace) {
        final ModelInfo modelInfo = getModelInfo(modelType, namespace);
        return modelInfo.getVersion().toString();
    }

    /**
     * Get all MOs of given type and namespace under the hierarchy of a root MO specified by its FDN.
     *
     * @param rootMoFdn
     *            the FDN of root MO
     * @param moType
     *            the type of searched MOs
     * @param moNamespace
     *            the namespace of searched MOs
     * @return all MOs matching the search criteria
     * @throws DataAccessException
     *             , DataAccessSystemException
     */
    public CmResponse getMos(final String rootMoFdn, final String moType, final String moNamespace)
            throws DataAccessException, DataAccessSystemException {

        logger.debug("getMos for rootFdn [{}] moType [{}] and nameSpace [{}] ", rootMoFdn, moType, moNamespace);
        // Create search scope for the parent MO
        final List<String> fdns = new LinkedList<String>();
        fdns.add(rootMoFdn);
        final List<CmSearchScope> scopeList = getNodeCmSearchScope(fdns);

        // Create object specification
        final CmObjectSpecification cmObjectSpecification = addSearchCMObject(moType, moNamespace);

        // Create search criteria from search scope and object specification
        final CmSearchCriteria cmSearchCriteria = createSearchCriteria(cmObjectSpecification, scopeList);

        // Invoke search of CM Reader
        final CmResponse cmResponse = reader.search(cmSearchCriteria, CmConstants.LIVE_CONFIGURATION);

        return exceptionIfFail(cmResponse);
    }

    /**
     * Get all MOs of given type and namespace under the hierarchy of a root MO specified by its FDN, with a list of attributes.
     *
     * @param rootMoFdn
     *            the FDN of root MO
     * @param moType
     *            the type of searched MOs
     * @param moNamespace
     *            the namespace of searched MOs
     * @param attrs
     *            The list of attributes to be returned IMPORTANT! This methods will return all listed attributes. If attrs == NULL, only attributes
     *            modelled as 'FROM_PERSISTENCE' will be returned! Be careful!!!
     * @return all MOs matching the search criteria
     * @throws DataAccessException
     *             , DataAccessSystemException
     */
    public CmResponse getMos(final String rootMoFdn, final String moType, final String moNamespace, final String... attrs)
            throws DataAccessException, DataAccessSystemException {

        logger.debug("getMos for rootFdn [{}] moType [{}] and nameSpace [{}] ", rootMoFdn, moType, moNamespace);
        // Create search scope for the parent MO
        final List<String> fdns = new LinkedList<String>();
        fdns.add(rootMoFdn);
        final List<CmSearchScope> scopeList = getNodeCmSearchScope(fdns);

        // Create object specification
        final CmObjectSpecification cmObjectSpecification = addSearchCMObject(moType, moNamespace);

        if (attrs != null) {
            for (final String att : attrs) {
                logger.info("getMos: Adding attribute [{}]", att);
                addSearchAttributes(att, null, cmObjectSpecification);
            }
        }

        // Create search criteria from search scope and object specification
        final CmSearchCriteria cmSearchCriteria = createSearchCriteria(cmObjectSpecification, scopeList);

        // Invoke search of CM Reader
        final CmResponse cmResponse = reader.search(cmSearchCriteria, CmConstants.LIVE_CONFIGURATION);

        return exceptionIfFail(cmResponse);
    }

    public CmResponse getAllMos(final String moType, final String moNamespace) {
        logger.info("getAllMos for moType : {} and nameSpace : {} ", moType, moNamespace);
        // Create object specification
        final CmObjectSpecification cmObjectSpecification = addSearchCMObject(moType, moNamespace);
        logger.debug("cmObjectSpecification is : {} ", cmObjectSpecification.toString());
        final CmSearchScope cmSearchScope = new CmSearchScope();
        cmSearchScope.setScopeType(CmSearchScope.ScopeType.UNSPECIFIED);
        final List<CmSearchScope> scopeList = new ArrayList<>();
        scopeList.add(cmSearchScope);
        // Create search criteria from search scope and object specification
        final CmSearchCriteria searchCriteria = createSearchCriteria(cmObjectSpecification, scopeList);
        logger.debug("searchCriteria is : {} ", searchCriteria.toString());
        final CmResponse cmResponse = reader.search(searchCriteria, CmConstants.LIVE_CONFIGURATION);
        logger.debug("CmResponse is : {} ", cmResponse.toString());
        return cmResponse;
    }

    private CmObjectSpecification addSearchCMObject(final String moType, final String namespace) {
        final CmObjectSpecification cmObjectSpecification = new CmObjectSpecification();
        cmObjectSpecification.setType(moType);
        // TORF-95973 : MGW support
        //        cmObjectSpecification.setNamespace(namespace == null ? DEFAULT_ERBS_NAMESPACE : namespace);
        return cmObjectSpecification;
    }

    private List<CmSearchScope> getNodeCmSearchScope(final List<String> nodesFdn) {
        final ArrayList<CmSearchScope> returnList = new ArrayList<CmSearchScope>();

        for (final String node : nodesFdn) {
            final CmSearchScope cmSearchScope = new CmSearchScope();
            cmSearchScope.setScopeType(CmSearchScope.ScopeType.FDN);
            cmSearchScope.setValue(node);
            cmSearchScope.setCmMatchCondition(CmMatchCondition.EQUALS);
            returnList.add(cmSearchScope);
        }

        return returnList;
    }

    private CmSearchCriteria createSearchCriteria(final CmObjectSpecification cmObjectToSearch, final List<CmSearchScope> scopeList) {
        final CmSearchCriteria cmSearchCriteria = new CmSearchCriteria();
        cmSearchCriteria.setSingleCmObjectSpecification(cmObjectToSearch);
        cmSearchCriteria.setCmSearchScopes(scopeList);
        return cmSearchCriteria;
    }

    /**
     * Adds search attributes
     *
     * @param attribute
     * @param value
     *            If value is specified adds EQUALS match condition for the specified attribute
     * @param cmObjectSpecification
     */
    private void addSearchAttributes(final String attribute, final String value, final CmObjectSpecification cmObjectSpecification) {
        final AttributeSpecification attributeSpecification = new AttributeSpecification();
        attributeSpecification.setName(attribute);

        if (value != null) {
            logger.debug("Adding match condition attribute [{}] EQUALS value [{}]", attribute, value);
            attributeSpecification.setCmMatchCondition(CmMatchCondition.EQUALS);
            attributeSpecification.setValue(value);
        }

        if (cmObjectSpecification != null) {
            AttributeSpecificationContainer attributeSpecifications;
            if (cmObjectSpecification.getAttributeSpecificationContainer() != null) {
                attributeSpecifications = cmObjectSpecification.getAttributeSpecificationContainer();
            } else {
                attributeSpecifications = new ValidatedAttributeSpecifications();
            }
            attributeSpecifications.addAttributeSpecification(attributeSpecification);
            cmObjectSpecification.setAttributeSpecificationContainer(attributeSpecifications);
        }
    }

    private NscsServiceException wrapException(final Exception e) {
        logger.error("wrapException() - Exception [{}]", e.getMessage());
        return e instanceof NscsServiceException ? (NscsServiceException) e
                : new DataAccessSystemException(e, NscsErrorCodes.UNEXPECTED_INTERNAL_ERROR);
    }

    private CmResponse exceptionIfFail(final CmResponse response) throws DataAccessException, DataAccessSystemException {
        logger.debug("response statusCode {}, statusMessage: {}", response.getStatusCode(), response.getStatusMessage());
        if (response.getStatusCode() == -1) {
            logger.error("Cm-Reader data access fail with status : {}", response.getStatusMessage());
            throw new DataAccessException(NscsErrorCodes.UNEXPECTED_INTERNAL_ERROR);
        } else if (response.getStatusCode() < -1) {
            logger.error("Cm-Reader data access fail with status : {}", response.getStatusMessage());
            throw new DataAccessSystemException(NscsErrorCodes.UNEXPECTED_INTERNAL_ERROR);
        }
        return response;
    }

    /**
     * Create a normalizable node reference based on a given node reference (FDN)
     *
     * @param node
     *            a NodeReference instance
     * @return a Normalizable node reference or null if it is not possible to create one
     * @see com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference
     */
    public NormalizableNodeReference getNormalizableNodeReference(final NodeReference node) {
        logger.debug("Invoking getNormalizableNodeReference with node [{}]", node);
        final MoObject moObject = getMoObjectByNodeReference(node);

        return getNormalizableNodeReference(moObject);
    }

    /**
     * Create a normalized node reference based on a given node reference (FDN)
     *
     * @param node
     *            a NodeReference instance
     * @return an instance of Normalized node reference or null if it is not possible to create one
     * @see com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference
     */
    public NormalizableNodeReference getNormalizedNodeReference(final NodeReference node) {
        logger.debug("Invoking getNormalizedNodeReference with node [{}]", node);
        final MoObject moObject = getMoObjectByNodeReference(node);

        return getNormalizedNodeReference(moObject);
    }

    /**
     * Get the target PO for the given MO (specified by its FDN).
     * 
     * This method is currently invoked only by troubleshooting REST API.
     *
     * @param fdn
     *            the MO FDN.
     * @return the target PO.
     */
    public NscsTargetPO getTargetPO(final String fdn) {
        logger.debug("get TargetPO : starts for MO FDN [{}]", fdn);
        final MoObject moObject = getMoObjectByFdn(fdn);
        if (moObject != null) {
            final PersistenceObject target = moObject.getPo().getTarget();
            if (target != null) {
                return new NscsTargetPO((String) (target.getAttribute("category")), (String) (target.getAttribute("type")),
                        (String) (target.getAttribute("name")), (String) (target.getAttribute("modelIdentity")));
            } else {
                logger.error("get TargetPO : null target PO");
            }
        } else {
            final String errorMsg = String.format("Null MO for FDN %s", fdn);
            logger.error("get TargetPO : {}", errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }
        return null;
    }

    /**
     * Gets a MoObject based on the provided node reference
     *
     * @param node
     *            a NodeReference instance
     * @return the managed object which has the supplied FDN, or null if no such managed object exists in this data store
     */
    public MoObject getMoObjectByNodeReference(final NodeReference node) {
        return getMoObjectByFdn(node.getFdn());
    }

    /**
     * Gets a MoObject based on the provided FDN string
     *
     * @param fdn
     *            String representing a node FDN
     * @return the managed object which has the supplied FDN, or null if no such managed object exists in this data store
     */
    public MoObject getMoObjectByFdn(final String fdn) {
        logger.debug("getMoObjectByFdn for fdn [{}]", fdn);
        try {
            MoObject moObject = null;
            logger.debug("Before invoking DataPersistenceService getLiveBucket() for fdn [{}]", fdn);

            setReadOnlyDpsTransaction();
            final ManagedObject moByFdn = dataPersistenceService.getLiveBucket().findMoByFdn(fdn);
            if (moByFdn != null) {
                moObject = new MoObject(moByFdn);
                logger.debug("getMoObjectByFdn not null object for fdn [{}]", fdn);
            }

            return moObject;
        } catch (final Exception e) {
            throw wrapException(e);
        }
    }

    /**
     * Checks if the given node reference exists in the data store
     *
     * @param node
     *            a NodeReference instance
     * @return true if there is a managed object which has the supplied FDN
     */
    public boolean exists(final NodeReference node) {
        return exists(node.getFdn());
    }

    /**
     * Checks if the given node FDN exists in the data store
     *
     * @param fdn
     *            String representing a node FDN
     * @return true if there is a managed object which has the supplied FDN
     */
    public boolean exists(final String fdn) {
        try {
            setReadOnlyDpsTransaction();
            return dataPersistenceService.getLiveBucket().findMoByFdn(fdn) != null;
        } catch (final Exception e) {
            throw wrapException(e);
        }
    }

    /**
     * Returns the target type of the given Network Element (NE) specified by its FDN.
     *
     * @param fdn
     *            The FDN of the given NE
     * @return the target type of the NE or null if NE doesn't exist
     */
    public String getTargetType(final String fdn) {
        String targetType = null;
        final NodeReference nodeRef = new NodeRef(fdn);
        final NormalizableNodeReference normNodeRef = getNormalizedNodeReference(nodeRef);
        if (normNodeRef != null) {
            targetType = normNodeRef.getNeType();
        }
        return targetType;
    }

    /**
     * Return the OSS Model Identity of the given Network Element (NE)
     *
     * @param fdn
     *            The FDN of the given NE
     * @return the OSS Model Identity of the NE or null if NE doesn't exist
     */
    public String getOssModelIdentity(final String fdn) {

        final NodeReference nodeRef = new NodeRef(fdn);
        final NormalizableNodeReference normNodeRef = getNormalizedNodeReference(nodeRef);
        if (normNodeRef != null) {
            return normNodeRef.getOssModelIdentity();
        }
        return null;
    }

    /**
     * Return the Model Information in terms of type and OSS Model Version of the given Network Element (NE)
     *
     * @param fdn
     *            The FDN of the given NE
     * @return the OSS Model Identity of the NE or null if NE doesn't exist
     */
    public NodeModelInformation getNodeModelInformation(final String fdn) {

        String modelIdentifier = null;
        ModelIdentifierType modelIdentifierType = ModelIdentifierType.UNKNOWN;
        String targetType = null;
        logger.info("get NodeModelInformation for fdn [{}]", fdn);

        final NodeReference nodeRef = new NodeRef(fdn);
        final NormalizableNodeReference normNodeRef = getNormalizedNodeReference(nodeRef);
        if (normNodeRef != null) {
            modelIdentifier = normNodeRef.getOssModelIdentity();
            modelIdentifierType = ModelIdentifierType.OSS_IDENTIFIER;
            targetType = normNodeRef.getNeType();
        }
        return new NodeModelInformation(modelIdentifier, modelIdentifierType, targetType);
    }

    /**
     * Return if given NormalizableNodeReference is a normalized node reference.
     *
     * @param nodeRef
     * @return
     */
    public static boolean isNormalizedNodeReference(final NormalizableNodeReference nodeRef) {
        boolean isNormalized = false;
        if (nodeRef != null && nodeRef instanceof NormalizedNodeReferenceImpl) {
            isNormalized = true;
        }
        return isNormalized;
    }

    /**
     * Return if given NormalizableNodeReference is a mirror node reference.
     *
     * @param nodeRef
     * @return
     */
    public static boolean isMirrorNodeReference(final NormalizableNodeReference nodeRef) {
        boolean isMirror = false;
        if (nodeRef != null && nodeRef instanceof NormalizableNodeReferenceImpl) {
            isMirror = true;
        }
        return isMirror;
    }

    private String safeToString(final Object o) {
        if (o == null) {
            return null;
        }
        return o.toString();
    }

    /**
     * Return an instance of NormalizableNodeReferenceImpl for the given MO or null if given MO doesn't exist or a normalizable reference to given MO
     * can't be found.
     *
     * @param mo
     *            the MO.
     * @return an instance of NormalizableNodeReferenceImpl or null.
     */
    private NormalizableNodeReference getNormalizableNodeReference(final MoObject mo) {

        if (mo == null) {
            logger.info("get NormalizableNodeReference : mo is null");
            return null;
        }

        logger.debug("get NormalizableNodeReference : starts for mo [{}] of type [{}]", mo.getFdn(), mo.getType());

        NormalizableNodeReference normalizableNodeReference = null;

        final PersistenceObject targetPo = mo.getPo().getTarget();
        if (targetPo == null) {
            logger.error("get NormalizableNodeReference : null target PO for mo [{}] of type [{}]", mo.getFdn(), mo.getType());
            return null;
        }
        final String targetCategory = safeToString(targetPo.getAttribute("category"));
        final String targetType = safeToString(targetPo.getAttribute("type"));
        final String targetName = safeToString(targetPo.getAttribute("name"));
        final String targetModelIdentity = safeToString(targetPo.getAttribute("modelIdentity"));
        logger.debug("get NormalizableNodeReference : from target PO: targetCategory [{}] targetType [{}] targetName [{}] targetModelIdentity [{}]",
                targetCategory, targetType, targetName, targetModelIdentity);

        switch (mo.getType()) {
        case ModelDefinition.NETWORK_ELEMENT_TYPE:
            final PoObject nodeRootPo = mo.getAssociationOfEndpoint(ModelDefinition.NODE_ROOT_REF_ENDPOINT);
            if (nodeRootPo != null) {
                final MoObject nodeRootMo = nodeRootPo.toMoObject();
                logger.debug("get NormalizableNodeReference : found node root mo [{}] of type [{}] for mo [{}] of type [{}]", nodeRootMo.getFdn(),
                        nodeRootMo.getType(), mo.getFdn(), mo.getType());
                normalizableNodeReference = new NormalizableNodeReferenceImpl(nodeRootMo.getFdn(), mo.getFdn(), targetCategory, targetType,
                        targetModelIdentity);
            }
            break;
        case ModelDefinition.ME_CONTEXT_TYPE:
            // break intentionally omitted
        case ModelDefinition.MANAGED_ELEMENT_TYPE:
            String networkElementFdn = null;
            final PoObject networkElementPo = mo.getAssociationOfEndpoint(ModelDefinition.NETWORK_ELEMENT_REF_ENDPOINT);
            if (networkElementPo != null) {
                final MoObject networkElementMo = networkElementPo.toMoObject();
                networkElementFdn = networkElementMo.getFdn();
                logger.debug("get NormalizableNodeReference : found mo [{}] of type [{}] for node root mo [{}] of type [{}]",
                        networkElementFdn, networkElementMo.getType(), mo.getFdn(), mo.getType());
            }
            normalizableNodeReference = new NormalizableNodeReferenceImpl(mo.getFdn(), networkElementFdn, targetCategory, targetType,
                    targetModelIdentity);
            break;
        case ModelDefinition.VIRTUAL_NETWORK_FUNCTION_MANAGER_TYPE:
            // break intentionally omitted
        case ModelDefinition.NETWORK_FUNCTION_VIRTUALIZATION_ORCHESTRATOR_TYPE:
            // break intentionally omitted
        case ModelDefinition.VIRTUAL_INFRASTRUCTURE_MANAGER_TYPE:
            // break intentionally omitted
        case ModelDefinition.CLOUD_INFRASTRUCTURE_MANAGER_TYPE:
            // break intentionally omitted
        case ModelDefinition.MANAGEMENT_SYSTEM_TYPE:
            logger.debug("get NormalizableNodeReference : virtual node mo [{}] of type [{}]", mo.getFdn(), mo.getType());
            normalizableNodeReference = new NormalizableNodeReferenceImpl(mo.getFdn(), mo.getFdn(), targetCategory, targetType, targetModelIdentity);
            break;
        default:
            break;
        }

        logger.debug("get NormalizableNodeReference : returns [{}]", normalizableNodeReference);

        return normalizableNodeReference;
    }

    /**
     * Return an instance of NormalizedNodeReferenceImpl for the given MO or null if given MO doesn't exist or a normalized reference to given MO
     * can't be found.
     *
     * @param mo
     *            the MO.
     * @return an instance of NormalizedNodeReferenceImpl or null.
     */
    private NormalizableNodeReference getNormalizedNodeReference(final MoObject mo) {

        if (mo == null) {
            logger.info("get NormalizedNodeReference : mo is null");
            return null;
        }

        logger.debug("get NormalizedNodeReference : starts for mo [{}] of type [{}]", mo.getFdn(), mo.getType());

        NormalizableNodeReference normalizedNodeReference = null;

        final PersistenceObject targetPo = mo.getPo().getTarget();
        if (targetPo == null) {
            logger.error("get NormalizedNodeReference : null target PO for mo [{}] of type [{}]", mo.getFdn(), mo.getType());
            return null;
        }
        final String targetCategory = safeToString(targetPo.getAttribute("category"));
        final String targetType = safeToString(targetPo.getAttribute("type"));
        final String targetName = safeToString(targetPo.getAttribute("name"));
        final String targetModelIdentity = safeToString(targetPo.getAttribute("modelIdentity"));
        logger.debug("get NormalizedNodeReference : from target PO: targetCategory [{}] targetType [{}] targetName [{}] targetModelIdentity [{}]",
                targetCategory, targetType, targetName, targetModelIdentity);

        switch (mo.getType()) {
        case ModelDefinition.NETWORK_ELEMENT_TYPE:
            String nodeRootFdn = null;
            final PoObject nodeRootPo = mo.getAssociationOfEndpoint(ModelDefinition.NODE_ROOT_REF_ENDPOINT);
            if (nodeRootPo != null) {
                final MoObject nodeRootMo = nodeRootPo.toMoObject();
                nodeRootFdn = nodeRootMo.getFdn();
                logger.debug("get NormalizedNodeReference : found node root mo [{}] of type [{}] for mo [{}] of type [{}]", nodeRootFdn,
                        nodeRootMo.getType(), mo.getFdn(), mo.getType());
            }
            normalizedNodeReference = new NormalizedNodeReferenceImpl(mo.getFdn(), nodeRootFdn, targetCategory, targetType, targetModelIdentity);

            break;
        case ModelDefinition.ME_CONTEXT_TYPE:
            // break intentionally omitted
        case ModelDefinition.MANAGED_ELEMENT_TYPE:
            final PoObject networkElementPo = mo.getAssociationOfEndpoint(ModelDefinition.NETWORK_ELEMENT_REF_ENDPOINT);
            if (networkElementPo != null) {
                final MoObject networkElementMo = networkElementPo.toMoObject();
                logger.debug("get NormalizedNodeReference : found mo [{}] of type [{}] for node root mo [{}] of type [{}]",
                        networkElementMo.getFdn(), networkElementMo.getType(), mo.getFdn(), mo.getType());
                normalizedNodeReference = new NormalizedNodeReferenceImpl(networkElementMo.getFdn(), mo.getFdn(), targetCategory, targetType,
                        targetModelIdentity);
            }

            break;
        case ModelDefinition.VIRTUAL_NETWORK_FUNCTION_MANAGER_TYPE:
            // break intentionally omitted
        case ModelDefinition.NETWORK_FUNCTION_VIRTUALIZATION_ORCHESTRATOR_TYPE:
            // break intentionally omitted
        case ModelDefinition.VIRTUAL_INFRASTRUCTURE_MANAGER_TYPE:
            // break intentionally omitted
        case ModelDefinition.CLOUD_INFRASTRUCTURE_MANAGER_TYPE:
            // break intentionally omitted
        case ModelDefinition.MANAGEMENT_SYSTEM_TYPE:
            logger.debug("get NormalizedNodeReference : virtual node mo [{}] of type [{}]", mo.getFdn(), mo.getType());
            normalizedNodeReference = new NormalizedNodeReferenceImpl(mo.getFdn(), mo.getFdn(), targetCategory, targetType, targetModelIdentity);
            break;
        default:
            break;
        }

        logger.debug("get NormalizedNodeReference : returns [{}]", normalizedNodeReference);

        return normalizedNodeReference;
    }

    /**
     * This inner class implements a reference to an MeContext or ManagedElement (COM/ECIM only) MO with its associated NetworkElement MO (if any).
     */
    private static class NormalizableNodeReferenceImpl extends NodeRef implements NormalizableNodeReference {

        private static final long serialVersionUID = -2633468747635691158L;

        private String targetCategory;
        private String targetType;
        private String ossModelIdentity;
        private NodeReference normalizedRef;

        public NormalizableNodeReferenceImpl(final String nameOrFdn, final String normalizedNameOrFdn, final String targetCategory,
                final String targetType, final String ossModelIdentity) {
            super(nameOrFdn);
            this.setTargetCategory(targetCategory);
            this.setNeType(targetType);
            this.setOssModelIdentity(ossModelIdentity);
            if (normalizedNameOrFdn != null) {
                this.normalizedRef = new NodeRef(normalizedNameOrFdn);
            }
        }

        @Override
        public boolean hasNormalizedRef() {
            return (this.normalizedRef != null);
        }

        @Override
        public NodeReference getNormalizedRef() {
            if (this.normalizedRef == null) {
                throw new IllegalStateException(String.format("Node [%s] can't be normalized.", getFdn()));
            }
            return this.normalizedRef;
        }

        @Override
        public boolean hasNormalizableRef() {
            return true;
        }

        @Override
        public NodeReference getNormalizableRef() {
            return this;
        }

        @Override
        public String getTargetCategory() {
            return targetCategory;
        }

        public final void setTargetCategory(final String targetCategory) {
            this.targetCategory = targetCategory;
        }

        @Override
        public String getNeType() {
            return targetType;
        }

        public final void setNeType(final String targetType) {
            this.targetType = targetType;
        }

        @Override
        public String getOssModelIdentity() {
            return this.ossModelIdentity;
        }

        public final void setOssModelIdentity(final String ossModelIdentity) {
            this.ossModelIdentity = ossModelIdentity;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("NormalizableNodeReferenceImpl{");
            sb.append("normalizedRef=").append(normalizedRef);
            sb.append('[').append(super.toString()).append(']');
            sb.append('}');
            return sb.toString();
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            if (!super.equals(o)) {
                return false;
            }

            final NormalizableNodeReferenceImpl that = (NormalizableNodeReferenceImpl) o;

            if (!targetType.equals(that.targetType)) {
                return false;
            }
            if (!normalizedRef.equals(that.normalizedRef)) {
                return false;
            }
            if (!(ossModelIdentity.equals(that.ossModelIdentity))) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + (targetType != null ? targetType.hashCode() : 0);
            result = 31 * result + (ossModelIdentity != null ? ossModelIdentity.hashCode() : 0);
            result = 31 * result + normalizedRef.hashCode();
            return result;
        }
    }

    /**
     * This inner class implements a reference to a NetworkElement MO with its associated MeContext or ManagedElement (COM/ECIM only) MO (if any).
     */
    private static class NormalizedNodeReferenceImpl extends NodeRef implements NormalizableNodeReference {

        private static final long serialVersionUID = 3530185635372487136L;

        private String targetCategory;
        private String targetType;
        private String ossModelIdentity;
        private NodeReference normalizableRef;

        public NormalizedNodeReferenceImpl(final String nameOrFdn, final String normalizableNameOrFdn, final String targetCategory,
                final String targetType, final String ossModelIdentity) {
            super(nameOrFdn);
            this.setTargetCategory(targetCategory);
            this.setNeType(targetType);
            this.setOssModelIdentity(ossModelIdentity);
            if (normalizableNameOrFdn != null) {
                this.normalizableRef = new NodeRef(normalizableNameOrFdn);
            } else {
                this.normalizableRef = null;
            }
        }

        @Override
        public boolean hasNormalizedRef() {
            return true;
        }

        @Override
        public NodeReference getNormalizedRef() {
            return this;
        }

        @Override
        public boolean hasNormalizableRef() {
            return (this.normalizableRef != null);
        }

        @Override
        public NodeReference getNormalizableRef() {
            return this.normalizableRef;
        }

        @Override
        public String getTargetCategory() {
            return targetCategory;
        }

        public final void setTargetCategory(final String targetCategory) {
            this.targetCategory = targetCategory;
        }

        @Override
        public String getNeType() {
            return targetType;
        }

        public final void setNeType(final String targetType) {
            this.targetType = targetType;
        }

        @Override
        public String getOssModelIdentity() {
            return this.ossModelIdentity;
        }

        public final void setOssModelIdentity(final String ossModelIdentity) {
            this.ossModelIdentity = ossModelIdentity;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("NormalizedNodeReferenceImpl{");
            sb.append("normalizableRef=").append(normalizableRef);
            sb.append('[').append(super.toString()).append(']');
            sb.append('}');
            return sb.toString();
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            if (!super.equals(o)) {
                return false;
            }

            final NormalizableNodeReferenceImpl that = (NormalizableNodeReferenceImpl) o;

            if (!targetType.equals(that.targetType)) {
                return false;
            }
            if (!normalizableRef.equals(that.normalizedRef)) {
                return false;
            }
            if (!ossModelIdentity.equals(that.ossModelIdentity)) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + (targetType != null ? targetType.hashCode() : 0);
            result = 31 * result + (ossModelIdentity != null ? ossModelIdentity.hashCode() : 0);
            result = 31 * result + normalizableRef.hashCode();
            return result;
        }

    }

    public String getMeContextFdn(final String networkElementFdn) {
        PersistenceObject meContextPo = null;

        setReadOnlyDpsTransaction();
        final ManagedObject networkElementMo = dataPersistenceService.getLiveBucket().findMoByFdn(networkElementFdn);
        if (networkElementMo != null) {
            final Collection<PersistenceObject> meContextAssociated = networkElementMo.getAssociations(ModelDefinition.NODE_ROOT_REF_ENDPOINT);
            if (meContextAssociated.size() == 1) {
                meContextPo = meContextAssociated.iterator().next();
            } else {
                logger.debug("No MeContext is associated with Network Element with Fdn {} ", networkElementMo);
                return "";
            }
            final String meContextFdn = ((ManagedObject) meContextPo).getFdn();
            logger.debug("ME Context FDN associated with NE FDN {} is {}", networkElementFdn, meContextFdn);
            return meContextFdn;
        } else {
            logger.error("Null ManagedObject with NE FDN [{}], return empty data", networkElementFdn);
            return "";
        }
    }

    public CmResponse getPosByPoIds(final List<Long> persistenceObjectIds) {

        final CmResponse cmResponse = new CmResponse();
        setReadOnlyDpsTransaction();
        final List<PersistenceObject> persistenceObjects = dataPersistenceService.getLiveBucket().findPosByIds(persistenceObjectIds);
        Collection<CmObject> cmObjects = mapper.mapToCmObjects(persistenceObjects.iterator(), DpsObjectMapper.INCLUDE_ALL_ATTRIBUTES);
        populateCmResponse(cmResponse, cmObjects);

        return cmResponse;
    }

    public CmResponse getNodesByPoIds(final List<Long> persistenceObjectIds) {

        logger.debug("Started getting node list from collection");
        final CmResponse cmResponse = new CmResponse();

        try {
            populateCmResponse(cmResponse, getRootAssociations(persistenceObjectIds));
        } catch (final Exception e) {
            logger.error("[{}]:{}", "getNodesByPoIds", e.getMessage());
        }

        return cmResponse;
    }

    private List<CmObject> getRootAssociations(final List<Long> poids) throws Exception {
        setReadOnlyDpsTransaction();
        final List<PersistenceObject> persistenceObjects = dataPersistenceService.getLiveBucket().findPosByIds(poids);
        final List<CmObject> networkElements = new ArrayList<>();
        ManagedObject mo = null;
        for (final PersistenceObject persistenceObject : persistenceObjects) {

            CmObject cmObject = null;
            if (persistenceObject.getType().equals(ModelDefinition.NETWORK_ELEMENT_TYPE)) {
                mo = (ManagedObject) persistenceObject;
                cmObject = mapper.mapToCmObject(mo, DpsObjectMapper.INCLUDE_ALL_ATTRIBUTES);
                networkElements.add(cmObject);

            } else if (persistenceObject.getType().equals(ModelDefinition.MANAGED_ELEMENT_TYPE)) {
                PersistenceObject objectToExtractRoot = persistenceObject;
                final ManagedObject parent = ((ManagedObject) persistenceObject).getParent();
                if (parent != null && !parent.getType().equals(ModelDefinition.SUB_NETWORK_TYPE)) {
                    objectToExtractRoot = parent;
                }
                networkElements.addAll(extractNetworkElement(objectToExtractRoot));
            } else if (persistenceObject.getType().equals(ModelDefinition.ME_CONTEXT_TYPE)) {
                networkElements.addAll(extractNetworkElement(persistenceObject));
            }
        }

        if (networkElements.isEmpty()) {
            throw new Exception("Cannot find any node root");
        }
        return networkElements;
    }

    private List<CmObject> extractNetworkElement(final PersistenceObject persistenceObject) {
        final List<CmObject> result = new ArrayList<CmObject>();

        try {

            final Collection<PersistenceObject> networkElementMos = persistenceObject.getAssociations(ModelDefinition.NETWORK_ELEMENT_REF_ENDPOINT);
            for (final PersistenceObject networkElementMo : networkElementMos) {
                if (networkElementMo instanceof ManagedObject) {
                    final CmObject cmObject = mapper.mapToCmObject(networkElementMo, DpsObjectMapper.INCLUDE_ALL_ATTRIBUTES);
                    result.add(cmObject);
                }
            }
        } catch (final Exception e) {
            logger.error("[{}]:{}, cannot get Networkelement for node: [{}]", "extractNetworkElement", e.getMessage(),
                    ((ManagedObject) persistenceObject).getFdn());
        }

        //if no cmObject is returned
        if (result.size() == 0) {
            logger.error("[{}], cannot get Networkelement for node: [{}]", "extractNetworkElement", ((ManagedObject) persistenceObject).getFdn());
        }

        return result;
    }

    private void populateCmResponse(final CmResponse cmResponse, final Collection<CmObject> cmObjects) {
        cmResponse.setTargetedCmObjects(cmObjects);
        final int responseSize = cmObjects.size();
        cmResponse.setStatusCode(responseSize);
        cmResponse.setStatusMessage(responseSize + " instance(s)");
    }

    public Map<String, Object> readAttributesFromDelegate(final String parentFdn, final String... paramVarArgs) {
        logger.debug("FDN given [{}]", parentFdn);
        logger.debug("Got dataBucket");
        setReadOnlyDpsTransaction();
        final ManagedObject parentMo = dataPersistenceService.getLiveBucket().findMoByFdn(parentFdn);
        if (parentMo == null) {
            throw new DataAccessException("Invalid parent MO : " + parentFdn);
        }
        final Map<String, Object> attributes = parentMo.readAttributesFromDelegate(paramVarArgs);
        return attributes;
    }

   /**
     * Checks if current environment is Acceptance tests (Arquillian/Docker)
     *
     * @return true if environment is Acceptance tests, false otherwise (production)
     */
    private static boolean acceptanceTestsEnvironment() {
        if (!DeploymentEnvironment.UNKNOWN.equals(deploymentEnv)) {
            return DeploymentEnvironment.ACCEPTANCE_TESTS.equals(deploymentEnv);
        }
        Properties props;
        try {
            props = PropertiesReader.getConfigProperties();
        } catch (PropertiesFileNotFoundException exc) {
            return false;    // Production env
        }
        deploymentEnv = DeploymentEnvironment.PRODUCTION;  // By default NOT Acceptance test env
        if ((props != null) &&
                ("true".equalsIgnoreCase(props.getProperty(ACCEPTANCE_TESTS_PROP, "false")))) {
            deploymentEnv = DeploymentEnvironment.ACCEPTANCE_TESTS;
            return true;     // Acceptance tests env
        }
        return false;    // Production env
    }

   /**
     * Sets current DPS transaction in read-only mode
     *
     */
    private void setReadOnlyDpsTransaction() {
        if (acceptanceTestsEnvironment()) {
            logger.debug("NscsCMReaderService: setting read-only Transaction");
            dataPersistenceService.setWriteAccess(false);
        }
    }

}
