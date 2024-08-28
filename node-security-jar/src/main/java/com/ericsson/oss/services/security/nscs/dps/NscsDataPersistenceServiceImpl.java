/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2020
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
*******************************************************************************
*----------------------------------------------------------------------------*/
package com.ericsson.oss.services.security.nscs.dps;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.exception.DataAccessException;
import com.ericsson.nms.security.nscs.api.exception.DataAccessSystemException;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.oss.itpf.datalayer.dps.DataBucket;
import com.ericsson.oss.itpf.datalayer.dps.DataPersistenceService;
import com.ericsson.oss.itpf.datalayer.dps.object.builder.ManagedObjectBuilder;
import com.ericsson.oss.itpf.datalayer.dps.object.builder.MibRootBuilder;
import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject;
import com.ericsson.oss.itpf.datalayer.dps.persistence.PersistenceObject;
import com.ericsson.oss.itpf.datalayer.dps.query.Query;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryBuilder;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryExecutor;
import com.ericsson.oss.itpf.datalayer.dps.query.Restriction;
import com.ericsson.oss.itpf.datalayer.dps.query.TypeContainmentRestrictionBuilder;
import com.ericsson.oss.itpf.datalayer.dps.query.TypeRestrictionBuilder;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;

/**
 * Auxiliary class to use DataPersistenceService.
 */
public class NscsDataPersistenceServiceImpl {

    @Inject
    private Logger logger;

    @EServiceRef
    private DataPersistenceService dataPersistenceService;

    /**
     * Creates the specified MIB root.
     * 
     * @param parentFdn
     *            the FDN of the parent MO or null if the MIB root has no parent.
     * @param type
     *            the type of the MO to create.
     * @param namespace
     *            the namespace of the MO to create.
     * @param version
     *            the version of the MO to create.
     * @param name
     *            the name of the MO to create.
     * @param attributes
     *            the attributes of the MO to create or null if no attribute is needed.
     * @return the created MO.
     */
    public ManagedObject createMibRoot(final String parentFdn, final String type, final String namespace, final String version, final String name,
            final Map<String, Object> attributes) {

        final String inputParams = String.format("parentFdn [%s] type [%s] namespace [%s] version [%s] name [%s] attributes [%s]", parentFdn, type,
                namespace, version, name, attributes);
        logger.debug("create MibRoot : starts for {}", inputParams);
        ManagedObject mo = null;

        final DataBucket liveBucket = dataPersistenceService.getLiveBucket();

        try {
            final MibRootBuilder mibRootBuilder = liveBucket.getMibRootBuilder();
            if (parentFdn != null) {
                ManagedObject parentMo = liveBucket.findMoByFdn(parentFdn);
                if (parentMo == null) {
                    final String errorMsg = String.format("MO not found for parent FDN [%s]", parentFdn);
                    logger.error("create MibRoot : {}", errorMsg);
                    throw new DataAccessException(errorMsg);
                }
                mibRootBuilder.parent(parentMo);
            }
            mibRootBuilder.type(type).namespace(namespace).version(version).name(name);
            if (attributes != null) {
                mibRootBuilder.addAttributes(attributes);
            }

            logger.debug("create MibRoot : creating MIB root for {}", inputParams);
            mo = mibRootBuilder.create();
        } catch (Exception e) {
            final String errorMsg = String.format("%s while creating MIB root for %s", NscsLogger.stringifyException(e), inputParams);
            logger.error(errorMsg);
            throw new DataAccessException(errorMsg);
        }

        logger.debug("create MibRoot : returns {}", mo);
        return mo;
    }

    /**
     * Creates the specified MO.
     * 
     * @param parentFdn
     *            the FDN of the parent MO.
     * @param type
     *            the type of the MO to create.
     * @param namespace
     *            the namespace of the MO to create.
     * @param version
     *            the version of the MO to create.
     * @param name
     *            the name of the MO to create.
     * @param attributes
     *            the attributes of the MO to create or null if no attribute is needed.
     * @return the created MO.
     */
    public ManagedObject createMo(final String parentFdn, final String type, final String name, final Map<String, Object> attributes) {

        final String inputParams = String.format("parentFdn [%s] type [%s] name [%s] attributes [%s]", parentFdn, type, name, attributes);
        logger.debug("create Mo : starts for {}", inputParams);

        if (parentFdn == null) {
            final String errorMsg = "Null parent FDN";
            logger.error("create Mo : {}", errorMsg);
            throw new DataAccessSystemException(errorMsg);
        }

        ManagedObject mo = null;

        final DataBucket liveBucket = dataPersistenceService.getLiveBucket();

        try {
            final ManagedObjectBuilder managedObjectBuilder = liveBucket.getManagedObjectBuilder();
            ManagedObject parentMo = liveBucket.findMoByFdn(parentFdn);
            if (parentMo == null) {
                final String errorMsg = String.format("MO not found for parent FDN [%s]", parentFdn);
                logger.error("create Mo : {}", errorMsg);
                throw new DataAccessException(errorMsg);
            }
            managedObjectBuilder.parent(parentMo);
            managedObjectBuilder.type(type).name(name);
            if (attributes != null) {
                managedObjectBuilder.addAttributes(attributes);
            }

            logger.debug("create Mo : creating MO for {}", inputParams);
            mo = managedObjectBuilder.create();
        } catch (Exception e) {
            final String errorMsg = String.format("%s while creating MO for %s", NscsLogger.stringifyException(e), inputParams);
            logger.error(errorMsg);
            throw new DataAccessException(errorMsg);
        }

        logger.debug("create Mo : returns {}", mo);
        return mo;
    }

    /**
     * Updates the MO of given FDN according to given attributes.
     *
     * @param fdn
     *            the FDN of the MO to update.
     * @param attributes
     *            the attributes to update.
     * @return the updated MO.
     */
    public ManagedObject updateMo(final String fdn, final Map<String, Object> attributes) {

        final String inputParams = String.format("fdn [%s] attributes [%s]", fdn, attributes);
        logger.debug("update Mo : starts for {}", inputParams);

        if (fdn == null) {
            final String errorMsg = "Null FDN";
            logger.error("update Mo : {}", errorMsg);
            throw new DataAccessSystemException(errorMsg);
        }

        ManagedObject mo = null;

        final DataBucket liveBucket = dataPersistenceService.getLiveBucket();

        try {
            mo = liveBucket.findMoByFdn(fdn);
            if (mo == null) {
                final String errorMsg = String.format("MO not found for FDN [%s]", fdn);
                logger.error("update Mo : {}", errorMsg);
                throw new DataAccessException(errorMsg);
            }
            if (attributes != null && !attributes.isEmpty()) {
                logger.debug("update Mo : updating MO for {}", inputParams);
                mo.setAttributes(attributes);
            }
        } catch (final Exception e) {
            final String errorMsg = String.format("%s while updating MO for %s", NscsLogger.stringifyException(e), inputParams);
            logger.error(errorMsg);
            throw new DataAccessException(errorMsg);
        }

        logger.debug("update Mo : returns {}", mo);
        return mo;
    }

    /**
     * Deletes the given MO.
     * 
     * Deletes the persistence object and all associations involving that persistence object (true uni-directional associations pointing at this
     * object will not be deleted).
     * 
     * If the supplied object is a managed object, then it will recursively delete all of its descendants (including their associations). If any error
     * occurs while deleting a managed object, and/or its descendants, the transaction will be marked to be rolled back only.
     *
     * @param po
     *            the persistence object to be deleted.
     * @return a total number of deleted PO/MOs including all children and grand...children.
     * @throws DataAccessSystemException
     *             if the given persistence object is null.
     * @throws DataAccessException
     *             if the delete persistence object fails.
     */
    public int deletePo(final PersistenceObject po) {

        final String inputParams = String.format("po [%s]", po);
        logger.debug("delete Po : starts for {}", inputParams);

        if (po == null) {
            final String errorMsg = "Null PO";
            logger.error("delete Po : {}", errorMsg);
            throw new DataAccessSystemException(errorMsg);
        }

        int deletedPos = 0;

        final DataBucket liveBucket = dataPersistenceService.getLiveBucket();

        try {
            deletedPos = liveBucket.deletePo(po);
        } catch (final Exception e) {
            final String errorMsg = String.format("%s while deleting PO for %s", NscsLogger.stringifyException(e), inputParams);
            logger.error(errorMsg, e);
            throw new DataAccessException(errorMsg);
        }

        logger.debug("delete Po : returns {}", deletedPos);
        return deletedPos;
    }

    /**
     * Gets the MO with the given FDN.
     * 
     * @param fdn
     *            the FDN of the searched MO.
     * @return the MO with the given FDN or null if no such MO exists.
     */
    public ManagedObject getMoByFdn(final String fdn) {

        final String inputParams = String.format("fdn [%s]", fdn);
        logger.debug("get MoByFdn : starts for {}", inputParams);

        if (fdn == null) {
            final String errorMsg = "Null FDN";
            logger.error("get MoByFdn : {}", errorMsg);
            throw new DataAccessSystemException(errorMsg);
        }
        ManagedObject mo = null;
        final DataBucket liveBucket = dataPersistenceService.getLiveBucket();
        try {
            mo = liveBucket.findMoByFdn(fdn);
        } catch (final Exception e) {
            final String errorMsg = String.format("%s while getting MO for %s", NscsLogger.stringifyException(e), inputParams);
            logger.error(errorMsg);
            throw new DataAccessException(errorMsg);
        }
        return mo;
    }

    /**
     * Gets all MOs of given type and namespace that are under (as child or grand child of) a parent MO of given FDN.
     * 
     * @param parentFdn
     *            the FDN of the parent MO.
     * @param type
     *            the MO type.
     * @param namespace
     *            the MO namespace.
     * @return the list of found MOs.
     */
    public List<ManagedObject> getMoListByType(final String parentFdn, final String type, final String namespace) {

        final String inputParams = String.format("parentFdn [%s] type [%s] namespace [%s]", parentFdn, type, namespace);
        logger.debug("get MoListByType : starts for {}", inputParams);

        if (parentFdn == null) {
            final String errorMsg = "Null parent FDN";
            logger.error("get MoListByType : {}", errorMsg);
            throw new DataAccessSystemException(errorMsg);
        }

        final DataBucket liveBucket = dataPersistenceService.getLiveBucket();

        try {
            ManagedObject parentMO = liveBucket.findMoByFdn(parentFdn);
            if (parentMO == null) {
                final String errorMsg = String.format("MO not found for parent FDN [%s]", parentFdn);
                logger.error("get MoListByType : {}", errorMsg);
                throw new DataAccessException(errorMsg);
            }
            final QueryBuilder queryBuilder = dataPersistenceService.getQueryBuilder();
            final Query<TypeContainmentRestrictionBuilder> typeQuery = queryBuilder.createTypeQuery(namespace, type, parentFdn);
            final QueryExecutor queryExecutor = liveBucket.getQueryExecutor();
            return queryExecutor.getResultList(typeQuery);
        } catch (Exception e) {
            final String errorMsg = String.format("%s while getting result list for %s", NscsLogger.stringifyException(e), inputParams);
            logger.error(errorMsg);
            throw new DataAccessException(errorMsg);
        }
    }

    /**
     * Get the MO of given type, namespace and name that is under (as child or grand child of) a parent MO of given FDN.
     * 
     * @param parentFdn
     *            the FDN of the parent MO.
     * @param type
     *            the MO type.
     * @param namespace
     *            the MO namespace.
     * @param name
     *            the MO name.
     * @return the found MO or null.
     */
    public ManagedObject getMo(final String parentFdn, final String type, final String namespace, final String name) {

        final String inputParams = String.format("parentFdn [%s] type [%s] namespace [%s]", parentFdn, type, namespace);
        logger.debug("get Mo : starts for {}", inputParams);

        if (parentFdn == null) {
            final String errorMsg = "Null parent FDN";
            logger.error("get Mo : {}", errorMsg);
            throw new DataAccessSystemException(errorMsg);
        }

        if (name == null) {
            final String errorMsg = "Null MO name";
            logger.error("get Mo : {}", errorMsg);
            throw new DataAccessSystemException(errorMsg);
        }

        try {
            for (final ManagedObject mo : getMoListByType(parentFdn, type, namespace)) {
                if (name.equals(mo.getName())) {
                    return mo;
                }
            }
        } catch (Exception e) {
            final String errorMsg = String.format("%s while getting MO for %s", NscsLogger.stringifyException(e), inputParams);
            logger.error(errorMsg);
            throw new DataAccessException(errorMsg);
        }
        return null;
    }

    /**
     * Gets all MOs of given type and namespace with given attribute.
     * 
     * @param type
     *            the MO type.
     * @param namespace
     *            the MO namespace.
     * @param attributeName
     *            the attribute name.
     * @return the list of found MOs.
     */
    public List<ManagedObject> getMoListByTypeWithAttribute(final String type, final String namespace, final String attributeName) {

        final String inputParams = String.format("type [%s] namespace [%s] attribute [%s]", type, namespace, attributeName);
        logger.debug("get MoListByTypeWithAttribute : starts for {}", inputParams);

        final DataBucket liveBucket = dataPersistenceService.getLiveBucket();

        try {
            final QueryBuilder queryBuilder = dataPersistenceService.getQueryBuilder();
            final Query<TypeRestrictionBuilder> typeQuery = queryBuilder.createTypeQuery(namespace, type);
            final Restriction restriction = typeQuery.getRestrictionBuilder().hasAttributeNamed(attributeName);
            typeQuery.setRestriction(restriction);
            final QueryExecutor queryExecutor = liveBucket.getQueryExecutor();
            return queryExecutor.getResultList(typeQuery);
        } catch (final Exception e) {
            final String errorMsg = String.format("%s while getting result list for %s", NscsLogger.stringifyException(e), inputParams);
            logger.error(errorMsg);
            throw new DataAccessException(errorMsg);
        }
    }
}
