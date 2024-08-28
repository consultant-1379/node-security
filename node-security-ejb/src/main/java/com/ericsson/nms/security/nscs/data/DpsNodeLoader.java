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
package com.ericsson.nms.security.nscs.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import com.ericsson.oss.services.security.nscs.util.NscsStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.ModelDefinition.NetworkElementSecurity;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.gim.EcimUserManager;
import com.ericsson.nms.security.nscs.handler.command.utility.NscsNameMultipleValueResponseBuilder;
import com.ericsson.nms.security.nscs.utilities.NscsNodeUtility;
import com.ericsson.oss.itpf.datalayer.dps.DataBucket;
import com.ericsson.oss.itpf.datalayer.dps.DataPersistenceService;
import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject;
import com.ericsson.oss.itpf.datalayer.dps.query.Query;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryBuilder;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryExecutor;
import com.ericsson.oss.itpf.datalayer.dps.query.TypeRestrictionBuilder;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.services.cache.NodesConfigurationStatusRecord;

/**
 * @author egicass 201603 Loads all the nodes and decorates them with ipaddress, securitylevel and syncstatus attributes
 */
@LocalBean
@Stateless
public class DpsNodeLoader {

    public static final String ATTRIBUTE_FDN = "fdn";
    public static final String NETYPE = "neType";
    public static final String OSS_NE_DEF = "OSS_NE_DEF";
    public static final String NETWORK_ELEMENT = "NetworkElement";
    public static final String SYNC_STATUS = "syncStatus";
    private static final Logger logger = LoggerFactory.getLogger(DpsNodeLoader.class);

    @Inject
    private NscsCMReaderService readerService;

    @EServiceRef
    private DataPersistenceService dataPersistenceService;

    @Inject
    private NscsNodeUtility nscsNodeUtility;

    /**
     * Reads from DPS all existent NetworkElement MOs and for each existent MO retrieves from DPS the target type, the IP address, the synchronization
     * status, and the security level status (if any).
     *
     * @return a map (where K=node name and V=info of the node) of existent NetworkElement MOs.
     * @deprecated This method is for DEBUG PURPOSES ONLY (to manually load the cache with all existent nodes). If the number of existent
     *             NetworkElement MOs is big, the transaction timeout could be exceeded!
     *
     */
    @Deprecated
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Map<String, NodesConfigurationStatusRecord> getAllNodes() {

        final Map<String, NodesConfigurationStatusRecord> result = new HashMap<String, NodesConfigurationStatusRecord>();
        final DataBucket liveBucket = dataPersistenceService.getLiveBucket();
        final QueryExecutor queryExecutor = liveBucket.getQueryExecutor();
        final QueryBuilder queryBuilder = dataPersistenceService.getQueryBuilder();
        final Query<TypeRestrictionBuilder> typeQuery = queryBuilder.createTypeQuery(OSS_NE_DEF, NETWORK_ELEMENT);

        final Iterator<ManagedObject> poListIterator = queryExecutor.execute(typeQuery);
        while (poListIterator.hasNext()) {
            final ManagedObject mo = poListIterator.next();

            String nodeName = "N/A";
            try {

                nodeName = mo.getName();
                final NodesConfigurationStatusRecord outDto = getNode(nodeName);
                if (outDto != null) {
                    result.put(nodeName, outDto);
                }

            } catch (final Exception e) {
                logger.error("Cannot insert node [{}] into cache, error [{}]", nodeName, e.getMessage());
            }
        }

        return result;

    }

    /**
     * Returns all NetworkElementSecurity MOs present in the DPS with a configured ldapApplicationUserName attribute.
     *
     * @return a map (where K=PO id and V=MO FDN) of existent NetworkElementSecurity MOs with configured ldapApplicationUser.
     */
    public Map<Long, String> getNESWithLdapUser() {
        final DataBucket liveBucket = dataPersistenceService.getLiveBucket();
        final QueryExecutor queryExecutor = liveBucket.getQueryExecutor();
        final QueryBuilder queryBuilder = dataPersistenceService.getQueryBuilder();
        final Query<TypeRestrictionBuilder> typeQuery = queryBuilder.createTypeQuery(
                Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.namespace(),
                Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.type());
        final Map<Long, String> moList = new HashMap<Long, String>();
        final Iterator<ManagedObject> poListIterator = queryExecutor.execute(typeQuery);
        while (poListIterator.hasNext()) {
            final ManagedObject mo = poListIterator.next();

            final String nodeFdn = mo.getFdn();

            final String ldapUser = mo.getAttribute(NetworkElementSecurity.LDAP_APPLICATION_USER_NAME);
            logger.debug("getNESWithLdapUser fdn: {} ldapUser: {}", nodeFdn, ldapUser);

            if (ldapUser != null && ldapUser.endsWith(EcimUserManager.ECIM_COMMON_USER_NAME_BASE)) {
                moList.put(mo.getPoId(), nodeFdn);
            }
        }
        return moList;
    }

    /**
     * Reads from DPS the target type, the IP address, the synchronization status, and the security level status (if any) of the given node.
     *
     * @param nodeName
     *            the name of the node.
     * @return the info of the node.
     *
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public NodesConfigurationStatusRecord getNode(final String nodeName) {

        NodesConfigurationStatusRecord outDto = null;
        final NodeReference nodeRef = new NodeRef(nodeName);
        final NormalizableNodeReference normNode = getNormalizedNodeReference(nodeRef);

        //GC20170428 Skip unsupported nodes
        if (normNode != null) {

            if (normNode.getNeType() != null) {

                final DataBucket bucket = dataPersistenceService.getLiveBucket();
                final ManagedObject mo = bucket.findMoByFdn(normNode.getFdn());

                if (mo != null) {
                    outDto = new NodesConfigurationStatusRecord();

                    String operationalSecurityLevel = NscsNameMultipleValueResponseBuilder.LEVEL_NOT_SUPPORTED;
                    String syncstatus = getSyncStatus(normNode.getName());
                    //force cache to handle only SYNC / UNSYNC value. All values not equals to SYNC will be shown as UNSYNC
                    //Also events coming from DPS will be handled in the same way.
                    if (!NscsNameMultipleValueResponseBuilder.SYNCHRONIZED.equals(syncstatus)
                            && !NscsNameMultipleValueResponseBuilder.UNSYNCHRONIZED.equals(syncstatus)) {
                        syncstatus = NscsNameMultipleValueResponseBuilder.UNSYNCHRONIZED;
                    }
                    if (nscsNodeUtility.isSecurityLevelSupported(normNode)) {
                        operationalSecurityLevel = getOperationalSecurityLevelForRecord(normNode, syncstatus);
                    }

                    outDto.setId(mo.getPoId());
                    outDto.setName(mo.getName());
                    outDto.setType(normNode.getNeType());
                    outDto.setIpaddress(getIpAddressForRecord(normNode));
                    outDto.setOperationalsecuritylevel(operationalSecurityLevel);
                    outDto.setSyncstatus(syncstatus);
                }

            } else {
                logger.error("Node fdn [{}] has null neType", nodeName);
            }

        } else {
            logger.error("Cannot get NormalizedNodeReference for node fdn [{}]", nodeName);
        }

        return outDto;
    }

    /**
     * Reads from DPS the Normalized Node Reference of the given Node Reference.
     *
     * @param nodeRef
     *            the Node Reference.
     * @return the Normalized Node Reference.
     *
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public NormalizableNodeReference getNormalizedNodeReference(final NodeReference nodeRef) {
        return readerService.getNormalizedNodeReference(nodeRef);
    }

    /**
     * Retrieves syncStatus for MOI. Returns blank if MOI does not have a linked CmFunction or if CMFunction does not have a value for syncStatus
     *
     * @param nodeName
     *            of MOI
     * @return syncStatus {String}
     */
    public String getSyncStatus(final String nodeName) {
        String syncStatus = NscsNameMultipleValueResponseBuilder.UNSYNCHRONIZED;

        if (NscsStringUtils.isNotEmpty(nodeName)) {

            //Build FDN of CmFunction using nodeName of MOI
            final String cmFunctionFDN = NETWORK_ELEMENT + "=" + nodeName + ",CmFunction=1";

            try {

                final DataBucket bucket = dataPersistenceService.getLiveBucket();
                final ManagedObject managedObject = bucket.findMoByFdn(cmFunctionFDN);

                //Check if CmFunction has syncStatus attribute in current managedObject retrieved from DPS
                if (managedObject != null) {
                    logger.debug("ManagedObject fetched is {}", managedObject.getFdn());

                    //get syncStatus from CmFunction
                    final Object syncStatusValue = managedObject.getAttribute(SYNC_STATUS);

                    logger.debug("syncStatus is {}", syncStatusValue);
                    syncStatus = String.valueOf(syncStatusValue);
                }
            } catch (final Exception exception) {
                logger.debug("Got an exception while fetching synStatus: {}", exception.getMessage());
            }

        }
        return syncStatus;
    }

    /**
     * Gets the IP address of the given node.
     *
     * @param normNode
     *            the normalizable node reference
     * @return the node IP address or NscsNameMultipleValueResponseBuilder.NOT_AVAILABLE on error.
     */
    private String getIpAddressForRecord(final NormalizableNodeReference normNode) {
        String ipAddress = nscsNodeUtility.getNodeIpAddress(normNode);
        if (ipAddress == null) {
            logger.error("Can not read ipAddress for [" + normNode.getFdn() + "]");
            ipAddress = NscsNameMultipleValueResponseBuilder.NOT_AVAILABLE;
        }
        return ipAddress;
    }

    /**
     * Normalizes the given Security Level status value.
     *
     * @param securityLevel
     *            the Security Level status
     * @return the normalized value
     */
    public static String normalizeSecurityLevel(final String securityLevel) {
        String result;
        switch (securityLevel) {
        case NscsNameMultipleValueResponseBuilder.UNKNOWN:
            result = NscsNameMultipleValueResponseBuilder.UNKNOWN;
            break;
        case NscsNameMultipleValueResponseBuilder.LEVEL_UNDEFINED:
        case NscsNameMultipleValueResponseBuilder.LEVEL_1:
            result = NscsNameMultipleValueResponseBuilder.LEVEL_1;
            break;

        case NscsNameMultipleValueResponseBuilder.LEVEL_2:
            result = NscsNameMultipleValueResponseBuilder.LEVEL_2;
            break;

        case NscsNameMultipleValueResponseBuilder.OPERATION_IN_PROGRESS:
            result = NscsNameMultipleValueResponseBuilder.OPERATION_IN_PROGRESS;
            break;

        case NscsNameMultipleValueResponseBuilder.SL2_ACTIVATION_IN_PROGRESS:
            result = NscsNameMultipleValueResponseBuilder.SL2_ACTIVATION_IN_PROGRESS;
            break;

        case NscsNameMultipleValueResponseBuilder.SL2_DEACTIVATION_IN_PROGRESS:
            result = NscsNameMultipleValueResponseBuilder.SL2_DEACTIVATION_IN_PROGRESS;
            break;

        case NscsNameMultipleValueResponseBuilder.IPSEC_ACTIVATION_IN_PROGRESS:
            result = NscsNameMultipleValueResponseBuilder.IPSEC_ACTIVATION_IN_PROGRESS;
            break;

        case NscsNameMultipleValueResponseBuilder.IPSEC_DEACTIVATION_IN_PROGRESS:
            result = NscsNameMultipleValueResponseBuilder.IPSEC_DEACTIVATION_IN_PROGRESS;
            break;

        default:
            result = NscsNameMultipleValueResponseBuilder.LEVEL_NOT_SUPPORTED;
            break;

        }
        return result;
    }

    /**
     * Updates a NetworkElementSecurity MO specified by the given PO id setting its ldapApplicationUserPassword attribute to the given value.
     *
     * @param poId
     *            the PO id of the given NetworkElementSecurity MO
     * @param password
     *            the value to set in the ldapApplicationUserPassword attribute
     */
    public void updateNESWithLdapUser(final Long poId, final String password) {
        final DataBucket bucket = dataPersistenceService.getLiveBucket();
        final ManagedObject mo = (ManagedObject) bucket.findPoById(poId);
        if (mo != null) {
            mo.setAttribute(NetworkElementSecurity.LDAP_APPLICATION_USER_PASSWORD, password);
        } else {
            logger.error("PoId not found [{}]", poId);
        }
    }

    /**
     * Gets the operational Security Level status of the given node of given synchronization status.
     *
     * @param normNode
     *            the Normalizable Node Reference
     * @param syncstatus
     *            the synchronization status of the node
     * @return the operational Security Level status or NscsNameMultipleValueResponseBuilder.UNKNOWN on error.
     */
    private String getOperationalSecurityLevelForRecord(final NormalizableNodeReference normNode, final String syncstatus) {

        String securityLevel = "";

        try {
            securityLevel = nscsNodeUtility.getSecurityLevel(normNode, syncstatus);

        } catch (final Exception e) {
            logger.error("Can not read securityLevel for [{}]", normNode.getFdn());
        }
        return normalizeSecurityLevel(securityLevel);
    }

}
