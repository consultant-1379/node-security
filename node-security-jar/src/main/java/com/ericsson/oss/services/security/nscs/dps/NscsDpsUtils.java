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

import java.net.StandardProtocolFamily;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.exception.DataAccessException;
import com.ericsson.nms.security.nscs.api.exception.DataAccessSystemException;
import com.ericsson.nms.security.nscs.api.exception.TooManyChildMosException;
import com.ericsson.nms.security.nscs.api.model.service.NscsModelInfo;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.ModelDefinition;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.model.service.NscsModelServiceImpl;
import com.ericsson.nms.security.nscs.util.CertDetails;
import com.ericsson.nms.security.nscs.utilities.CbpOiMoNaming;
import com.ericsson.nms.security.nscs.utilities.NscsCommonValidator;
import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject;

/**
 * Utilities to manage NSCS interactions to DPS and to manage ManagedObject.
 */
public class NscsDpsUtils {

    private static final String SF_TYPE = Model.NETWORK_ELEMENT.securityFunction.type();
    private static final String NES_TYPE = Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.type();

    private Logger logger = LoggerFactory.getLogger(NscsDpsUtils.class);

    @Inject
    private NscsModelServiceImpl nscsModelServiceImpl;

    @Inject
    private NscsDataPersistenceServiceImpl nscsDataPersistenceServiceImpl;

    /**
     * Creates the node hierarchy top MO of given type, namespace and name for the given normalizable node reference.
     * 
     * The node hierarchy top MO (e.g. keystore, truststore, system for CBP-OI nodes, SystemFunctions, Transport for COM/ECIM) shall always be created
     * as child MO under the node root MO.
     * 
     * @param normalizableNodeReference
     *            the normalizable node reference.
     * @param refMimNamespace
     *            the reference MIM namespace. In case of CPP nodes it is null since there is only one single MIM. In case of ECIM nodes the
     *            implementation models are derived from reference models, and hence it can be different from the MIM namespace (different target
     *            types of ECIM family could have for a MIM different namespace but the reference namespace is common and unique for all). In case of
     *            EOI-YANG based nodes the namespace is conceptually the same as the reference, and hence the MIM reference namespace is null and the
     *            MIM namespace can be used instead (it is the same across all target types of EOI family).
     * @param moType
     *            the unscoped MO type.
     * @param moName
     *            the MO name.
     * @return the created hierarchy top MO.
     * @throws DataAccessException
     *             if the node root MO is null.
     */
    public ManagedObject createNodeHierarchyTopMo(final NormalizableNodeReference normalizableNodeReference, final String refMimNamespace,
            final String moType, final String moName) {

        ManagedObject hierarchyTopMO = null;
        final ManagedObject nodeRootMO = getNodeRootMo(normalizableNodeReference);
        if (nodeRootMO != null) {
            hierarchyTopMO = createChildMo(nodeRootMO, normalizableNodeReference, refMimNamespace, moType, moName, null);
            logger.info("create HierarchyTopMo : successfully created MO of type [{}] ns [{}] name [{}] for node [{}]", moType, refMimNamespace,
                    moName, normalizableNodeReference);
        } else {
            final String errorMsg = String.format("Null node root MO for node [%s]", normalizableNodeReference);
            logger.error("create HierarchyTopMo : {} creating MO of type [{}] ns [{}] name [{}]", errorMsg, moType, refMimNamespace, moName);
            throw new DataAccessException(errorMsg);
        }
        return hierarchyTopMO;
    }

    /**
     * Creates a MO of given type and name under the given parent MO for the given node reference and with the given attributes.
     * 
     * @param parentMo
     *            the parent MO.
     * @param normalizableNodeReference
     *            the node reference.
     * @param refMimNamespace
     *            the reference MIM namespace. In case of CPP nodes it is null since there is only one single MIM. In case of ECIM nodes the
     *            implementation models are derived from reference models, and hence it can be different from the MIM namespace (different target
     *            types of ECIM family could have for a MIM different namespace but the reference namespace is common and unique for all). In case of
     *            EOI-YANG based nodes the namespace is conceptually the same as the reference, and hence the MIM reference namespace is null and the
     *            MIM namespace can be used instead (it is the same across all target types of EOI family).
     * @param moType
     *            the unscoped MO type.
     * @param moName
     *            the MO name.
     * @param moAttributes
     *            the MO attributes.
     * @return the created MO.
     */
    public ManagedObject createChildMo(final ManagedObject parentMo, final NormalizableNodeReference normalizableNodeReference,
            final String refMimNamespace, final String moType, final String moName, final Map<String, Object> moAttributes) {
        final String targetCategory = normalizableNodeReference.getTargetCategory();
        final String targetType = normalizableNodeReference.getNeType();
        final String targetModelIdentity = normalizableNodeReference.getOssModelIdentity();
        final String scopedMoType = getScopedMoTypeFromUnscopedMoType(parentMo, normalizableNodeReference, moType);
        final NscsModelInfo nscsModelInfo = nscsModelServiceImpl.getModelInfoWithRefMimNs(targetCategory, targetType, targetModelIdentity,
                refMimNamespace, scopedMoType);
        if (!parentMo.getNamespace().equals(nscsModelInfo.getNamespace()) || !parentMo.getVersion().equals(nscsModelInfo.getVersion())) {
            return nscsDataPersistenceServiceImpl.createMibRoot(parentMo.getFdn(), nscsModelInfo.getName(), nscsModelInfo.getNamespace(),
                    nscsModelInfo.getVersion(), moName, moAttributes);
        } else {
            return nscsDataPersistenceServiceImpl.createMo(parentMo.getFdn(), nscsModelInfo.getName(), moName, moAttributes);
        }
    }

    /**
     * Updates the given MO according to given attributes.
     *
     * @param mo
     *            the MO to update.
     * @param attributes
     *            the attributes to update.
     * @return the updated MO.
     */
    public ManagedObject updateMo(final ManagedObject mo, final Map<String, Object> attributes) {

        final String inputParams = String.format("mo [%s] attributes [%s]", mo, attributes);
        logger.debug("update Mo : starts for {}", inputParams);

        if (mo == null) {
            final String errorMsg = "Null MO";
            logger.error("update Mo : {}", errorMsg);
            throw new DataAccessSystemException(errorMsg);
        }
        try {
            if (attributes != null && !attributes.isEmpty()) {
                logger.debug("update Mo : updating Mo for {}", inputParams);
                mo.setAttributes(attributes);
            }
        } catch (final Exception e) {
            final String errorMsg = String.format("%s while updating Mo for %s", NscsLogger.stringifyException(e), inputParams);
            logger.error(errorMsg);
            throw e;
        }

        logger.debug("update Mo : returns {}", mo);
        return mo;
    }

    /**
     * Deletes the given MO.
     *
     * @param mo
     *            the MO to delete.
     * @return a total number of deleted PO/MOs including all children and grand...children.
     */
    public int deleteMo(final ManagedObject mo) {

        return nscsDataPersistenceServiceImpl.deletePo(mo);
    }

    /**
     * Gets from DPS the MO with the given FDN.
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
        return nscsDataPersistenceServiceImpl.getMoByFdn(fdn);
    }

    /**
     * Gets the FDN of the normalized root MO for the given node reference.
     * 
     * The node reference can be either a normalized or a normalizable node reference.
     * 
     * @param nodeReference
     *            the node reference.
     * @return the FDN of the normalized root MO or null if it does not exist or invalid node reference.
     */
    public String getNormalizedRootMoFdn(final NormalizableNodeReference nodeReference) {
        logger.debug("get NormalizedRootMoFdn : starts for [{}]", nodeReference);
        String normalizedRootFdn = null;
        if (NscsCMReaderService.isNormalizedNodeReference(nodeReference)) {
            normalizedRootFdn = nodeReference.getFdn();
        } else if (NscsCMReaderService.isMirrorNodeReference(nodeReference)) {
            normalizedRootFdn = nodeReference.getNormalizedRef().getFdn();
        } else {
            logger.error("get NormalizedRootMoFdn : invalid node reference [{}] of class [{}]", nodeReference,
                    nodeReference != null ? nodeReference.getClass() : null);
        }
        logger.debug("get NormalizedRootMoFdn : returns [{}]", normalizedRootFdn);
        return normalizedRootFdn;
    }

    /**
     * Gets the FDN of the mirror root MO for the given node reference.
     * 
     * The mirror root MO is the MO associated to the ENM normalized root MO (NetworkElement MO).
     * 
     * The node reference can be either a normalized or a normalizable node reference.
     * 
     * @param nodeReference
     *            the node reference.
     * @return the FDN of the mirror root MO or null if it does not exist or invalid node reference.
     */
    public String getMirrorRootMoFdn(final NormalizableNodeReference nodeReference) {
        logger.debug("get MirrorRootMoFdn : starts for {}", nodeReference);
        String mirrorRootFdn = null;
        if (NscsCMReaderService.isNormalizedNodeReference(nodeReference)) {
            mirrorRootFdn = nodeReference.getNormalizableRef().getFdn();
        } else if (NscsCMReaderService.isMirrorNodeReference(nodeReference)) {
            mirrorRootFdn = nodeReference.getFdn();
        } else {
            logger.error("get MirrorRootMoFdn : invalid node reference {} of class {}", nodeReference,
                    nodeReference != null ? nodeReference.getClass() : null);
        }
        logger.debug("get MirrorRootMoFdn : returns {}", mirrorRootFdn);
        return mirrorRootFdn;
    }

    /**
     * Gets from DPS the normalized root MO for the given node reference.
     * 
     * The normalized root MO is the MO associated to the ENM mirror root MO.
     * 
     * @param nodeReference
     *            the node reference.
     * @return the normalized root MO or null if it does not exist.
     * @throws {@link
     *             DataAccessSystemException} if normalized root MO FDN is null.
     */
    public ManagedObject getNormalizedRootMo(final NormalizableNodeReference nodeReference) {
        logger.debug("get NormalizedRootMo : starts for {}", nodeReference);
        final String normalizedRootFdn = getNormalizedRootMoFdn(nodeReference);
        if (normalizedRootFdn == null) {
            final String errorMsg = "Null normalized root FDN";
            logger.error("get NormalizedRootMo : {} for {}", errorMsg, nodeReference);
            throw new DataAccessSystemException(errorMsg);
        }
        return nscsDataPersistenceServiceImpl.getMoByFdn(normalizedRootFdn);
    }

    /**
     * Gets from DPS the node root MO for the given node reference.
     * 
     * The mirror root MO is the MO associated to the ENM normalized root MO (NetworkElement MO).
     * 
     * The node root MO type and namespace are returned by the Model Service for a given targetCategory, targetType, and targetModelIdentity. For node
     * not supporting the ManagedElement (see 3GPP node not supporting the ManagedElement) the Model Service could return either null or the type and
     * namespace of the ENM mirror root MO (MeContext).
     * 
     * The node root MO can be either a CHILD MO under the ENM mirror root MO or the ENM mirror root MO itself.
     * 
     * Following scenarios shall be considered:
     * 
     * node supporting the ManagedElement as node root MO (Model Service returns the type and namespace of such ManagedElement MO) and created in ENM
     * with MeContext (ossPrefix):
     * 
     * - ENM mirror root MO is the MeContext MO and node root MO is the ManagedElement MO present AS CHILD of MeContext
     * 
     * node supporting the ManagedElement as node root MO (Model Service returns the type and namespace of such ManagedElement MO) and created in ENM
     * without MeContext (ossPrefix):
     * 
     * - ENM mirror root MO is the ManagedElement MO and node root MO is the ManagedElement MO itself
     * 
     * node not supporting the ManagedElement as node root MO (Model Service returns null as node root MO info or the type and namespace of MeContext
     * MO) and created in ENM with MeContext (ossPrefix):
     * 
     * - ENM mirror root MO is the MeContext MO and node root MO is the MeContext MO itself.
     * 
     * Note that for node not supporting the ManagedElement as node root MO it is MANDATORY to create the node in ENM with MeContext (ossPrefix).
     * 
     * @param nodeReference
     *            the node reference.
     * @return the node root MO or null if it does not exist.
     */
    public ManagedObject getNodeRootMo(final NormalizableNodeReference nodeReference) {
        logger.debug("get NodeRootMo : starts for {}", nodeReference);
        final String mirrorRootFdn = getMirrorRootMoFdn(nodeReference);
        if (mirrorRootFdn == null) {
            final String errorMsg = "Null mirror root FDN";
            logger.error("get NodeRootMo : {} for {}", errorMsg, nodeReference);
            throw new DataAccessSystemException(errorMsg);
        }
        final String targetCategory = nodeReference.getTargetCategory();
        final String targetType = nodeReference.getNeType();
        final String nodeName = nodeReference.getName();
        final String inputParams = String.format("mirrorRootFdn [%s] targetCategory[%s] targetType [%s] nodeName [%s]", mirrorRootFdn, targetCategory,
                targetType, nodeName);
        logger.debug("get NodeRootMo : starts for {}", inputParams);

        final NscsModelInfo nscsModelInfo = nscsModelServiceImpl.getRootMoInfo(targetCategory, targetType);
        if (nscsModelInfo != null) {
            final Pattern mePattern = Pattern.compile(String.format("^(?:.+,)?%s=([^,]+)$", nscsModelInfo.getName()));
            final Matcher matcher = mePattern.matcher(mirrorRootFdn);
            if (matcher.find()) {
                return nscsDataPersistenceServiceImpl.getMoByFdn(mirrorRootFdn);
            } else {
                return nscsDataPersistenceServiceImpl.getMo(mirrorRootFdn, nscsModelInfo.getName(), nscsModelInfo.getNamespace(), nodeName);
            }
        } else {
            logger.info("get NodeRootMo : Null Root MO Info from Model Service for {}", inputParams);
            return nscsDataPersistenceServiceImpl.getMoByFdn(mirrorRootFdn);
        }
    }

    /**
     * Gets from DPS the node hierarchy top MO of given type and name for the given node reference.
     * 
     * The node hierarchy top MO (e.g. keystore, truststore, system for CBP-OI nodes, SystemFunctions, Transport for COM/ECIM) shall always be created
     * as child MO under the node root MO.
     * 
     * 
     * @param nodeReference
     *            the node reference.
     * @param refMimNamespace
     *            the reference MIM namespace. In case of CPP nodes it is null since there is only one single MIM. In case of ECIM nodes the
     *            implementation models are derived from reference models, and hence it can be different from the MIM namespace (different target
     *            types of ECIM family could have for a MIM different namespace but the reference namespace is common and unique for all). In case of
     *            EOI-YANG based nodes the namespace is conceptually the same as the reference, and hence the MIM reference namespace is null and the
     *            MIM namespace can be used instead (it is the same across all target types of EOI family).
     * @param moType
     *            the unscoped MO type.
     * @param moName
     *            the MO name.
     * @return the MO or null if it does not exist.
     */
    public ManagedObject getNodeHierarchyTopMo(final NormalizableNodeReference nodeReference, final String refMimNamespace, final String moType,
            final String moName) {
        final String mirrorRootFdn = getMirrorRootMoFdn(nodeReference);
        if (mirrorRootFdn == null) {
            final String errorMsg = "Null mirror root FDN";
            logger.error("get NodeHierarchyTopMo : {}", errorMsg);
            throw new DataAccessSystemException(errorMsg);
        }

        final String targetCategory = nodeReference.getTargetCategory();
        final String targetType = nodeReference.getNeType();
        final String targetModelIdentity = nodeReference.getOssModelIdentity();
        final String inputParams = String.format(
                "mirrorRootFdn [%s] targetCategory [%s] targetType [%s] targetModelIdentity [%s] refMimNs [%s] moType [%s] moName [%s]",
                mirrorRootFdn, targetCategory, targetType, targetModelIdentity, refMimNamespace, moType, moName);
        logger.debug("get NodeHierarchyTopMo : starts for {}", inputParams);

        final NscsModelInfo nscsModelInfo = nscsModelServiceImpl.getModelInfoWithRefMimNs(targetCategory, targetType, targetModelIdentity,
                refMimNamespace, moType);
        return nscsDataPersistenceServiceImpl.getMo(mirrorRootFdn, nscsModelInfo.getName(), nscsModelInfo.getNamespace(), moName);
    }

    /**
     * Gets from DPS the only MO of given type and namespace for the given node reference.
     * 
     * The node hierarchy top MO (e.g. keystore, truststore, system for CBP-OI nodes, SystemFunctions, Transport for COM/ECIM) shall always be created
     * as child MO under the node root MO.
     * 
     * 
     * @param normalizableNodeReference
     *            the node reference.
     * @param refMimNamespace
     *            the reference MIM namespace. In case of CPP nodes it is null since there is only one single MIM. In case of ECIM nodes the
     *            implementation models are derived from reference models, and hence it can be different from the MIM namespace (different target
     *            types of ECIM family could have for a MIM different namespace but the reference namespace is common and unique for all). In case of
     *            EOI-YANG based nodes the namespace is conceptually the same as the reference, and hence the MIM reference namespace is null and the
     *            MIM namespace can be used instead (it is the same across all target types of EOI family).
     * @param moType
     *            the unscoped MO type.
     * @return the MO or null if it does not exist or more than one MO is found.
     */
    public ManagedObject getOnlyMO(final NormalizableNodeReference normalizableNodeReference, final String refMimNamespace, final String moType) {
        final String mirrorRootFdn = normalizableNodeReference.getFdn();
        final String targetCategory = normalizableNodeReference.getTargetCategory();
        final String targetType = normalizableNodeReference.getNeType();
        final String targetModelIdentity = normalizableNodeReference.getOssModelIdentity();
        final String inputParams = String.format(
                "mirrorRootFdn [%s] targetCategory [%s] targetType [%s] targetModelIdentity [%s] refMimNs [%s] moType [%s]", mirrorRootFdn,
                targetCategory, targetType, targetModelIdentity, refMimNamespace, moType);
        logger.debug("get OnlyMO : starts for {}", inputParams);

        if (mirrorRootFdn == null) {
            final String errorMsg = "Null mirror root FDN";
            logger.error("get OnlyMO : {}", errorMsg);
            throw new DataAccessSystemException(errorMsg);
        }

        final NscsModelInfo nscsModelInfo = nscsModelServiceImpl.getModelInfoWithRefMimNs(targetCategory, targetType, targetModelIdentity,
                refMimNamespace, moType);
        final List<ManagedObject> mos = nscsDataPersistenceServiceImpl.getMoListByType(mirrorRootFdn, nscsModelInfo.getName(),
                nscsModelInfo.getNamespace());

        if (mos.isEmpty()) {
            logger.debug("get OnlyMO : no MO of type [{}] and ns [{}] under mirror root FDN [{}] for node [{}]", nscsModelInfo.getName(),
                    nscsModelInfo.getNamespace(), mirrorRootFdn, normalizableNodeReference);
            return null;
        }
        if (mos.size() > 1) {
            final String errorMessage = String.format("Too many [%s] MOs of type [%s] and ns [%s] under mirror root FDN [%s] for node [%s]",
                    mos.size(), nscsModelInfo.getName(), nscsModelInfo.getNamespace(), mirrorRootFdn, normalizableNodeReference);
            logger.error("get OnlyMO : {}", errorMessage);
            return null;
        }
        final ManagedObject mo = mos.get(0);
        logger.debug("get OnlyMO : found MO [{}] of type [{}] and ns [{}] under mirror root FDN [{}] for node [{}]", mo.getFdn(),
                nscsModelInfo.getName(), nscsModelInfo.getNamespace(), mirrorRootFdn, normalizableNodeReference);
        return mo;
    }

    /**
     * Gets from the given parent MO (without access to DPS) the child MO of given unscoped type (not containing any $$ notation) and name for a given
     * node reference.
     * 
     * @param parentMo
     *            the parent MO.
     * @param normalizableNodeReference
     *            the node reference.
     * @param moType
     *            the unscoped MO type.
     * @param moName
     *            the MO name.
     * @return the FDN of the MO or null if it does not exist.
     */
    public ManagedObject getChildMo(final ManagedObject parentMo, final NormalizableNodeReference normalizableNodeReference, final String moType,
            final String moName) {
        
        if (parentMo.getChildrenSize() > 0) {
            final String scopedMoType = getScopedMoTypeFromUnscopedMoType(parentMo, normalizableNodeReference, moType);
            for (final ManagedObject mo : parentMo.getChildren()) {
                if (scopedMoType.equals(mo.getType()) && moName.equals(mo.getName())) {
                    return mo;
                }
            }
        }
        logger.debug(
                "get ChildMo : no children of type [{}] and name [{}] under [{}] for targetCategory [{}] targetType [{}] targetModelIdentity [{}]",
                moType, moName, parentMo.getFdn(), normalizableNodeReference.getTargetCategory(), normalizableNodeReference.getNeType(),
                normalizableNodeReference.getOssModelIdentity());
        return null;
    }

    /**
     * Gets from the given parent MO (without access to DPS) the child MO of given unscoped type (not containing any $$ notation) and with a given attribute of given value (containing a DN)
     * for a given node reference.
     * 
     * @param parentMo
     *            the parent MO.
     * @param normalizableNodeReference
     *            the node reference.
     * @param moType
     *            the unscoped MO type.
     * @param moAttr
     *            the MO attribute.
     * @param moAttrValue
     *            the MO attribute value (containing a DN).
     * @return the FDN of the MO or null if it does not exist.
     */
    public ManagedObject getChildMoByAttrAsDn(final ManagedObject parentMo, final NormalizableNodeReference normalizableNodeReference,
            final String moType, final String moAttr, final String moAttrValue) {
        if (parentMo.getChildrenSize() > 0) {
            final String scopedMoType = getScopedMoTypeFromUnscopedMoType(parentMo, normalizableNodeReference, moType);
            for (final ManagedObject mo : parentMo.getChildren()) {
                if (scopedMoType.equals(mo.getType()) && mo.getAttribute(moAttr) != null && CertDetails.matchesDN(moAttrValue, mo.getAttribute(moAttr))) {
                    return mo;
                }
            }
        }
        logger.debug(
                "get ChildMoByAttrAsDn : no children of type [{}] attr [{}] value [{}] under [{}] for targetCategory [{}] targetType [{}] targetModelIdentity [{}]",
                moType, moAttr, moAttrValue, parentMo.getFdn(), normalizableNodeReference.getTargetCategory(), normalizableNodeReference.getNeType(),
                normalizableNodeReference.getOssModelIdentity());
        return null;
    }

    /**
     * Gets from the given parent MO (without access to DPS) the only child MO of given unscoped type for a given node reference.
     * 
     * @param parentMo
     *            the parent MO.
     * @param normalizableNodeReference
     *            the node reference.
     * @param moType
     *            the unscoped MO type.
     * @return the FDN of the child MO or null if it does not exist.
     * @throws TooManyChildMosException
     *             if more than one MO is found.
     */
    public ManagedObject getOnlyChildMo(final ManagedObject parentMo, final NormalizableNodeReference normalizableNodeReference,
            final String moType) {

        final List<ManagedObject> childMOs = getChildMos(parentMo, normalizableNodeReference, moType);
        if (childMOs.isEmpty()) {
            logger.debug("get OnlyChildMo : no child MO of type [{}] under parent MO [{}] for node [{}]", moType, parentMo.getFdn(),
                    normalizableNodeReference);
            return null;
        }
        if (childMOs.size() > 1) {
            final String errorMessage = String.format("Too many [%s] child MOs of type [%s] under parent MO [%s] for node [%s]", childMOs.size(),
                    moType, parentMo.getFdn(), normalizableNodeReference);
            logger.error("get OnlyChildMo : {}", errorMessage);
            throw new TooManyChildMosException(errorMessage);
        }
        final ManagedObject childMO = childMOs.get(0);
        logger.debug("get OnlyChildMo : found child MO [{}] of type [{}] under parent MO [{}] for node [{}]", childMO.getFdn(), moType,
                parentMo.getFdn(), normalizableNodeReference);
        return childMO;
    }

    /**
     * Gets from the given parent MO (without access to DPS) the child MOs of given unscoped type for a given node reference.
     * 
     * @param parentMo
     *            the parent MO.
     * @param normalizableNodeReference
     *            the node reference.
     * @param moType
     *            the unscoped MO type.
     * @return the list of the MOs. The list is empty if no child MOs found.
     */
    public List<ManagedObject> getChildMos(final ManagedObject parentMo, final NormalizableNodeReference normalizableNodeReference,
            final String moType) {
        List<ManagedObject> childMos = new ArrayList<>();
        if (parentMo.getChildrenSize() > 0) {
            final String scopedMoType = getScopedMoTypeFromUnscopedMoType(parentMo, normalizableNodeReference, moType);

            for (final ManagedObject mo : parentMo.getChildren()) {
                if (scopedMoType.equals(mo.getType())) {
                    childMos.add(mo);
                }
            }
        }
        logger.debug("get ChildMos : found [{}] children of type [{}] under [{}] for node [{}]", childMos.size(), moType,
                parentMo.getFdn(), normalizableNodeReference);
        return childMos;
    }

    /**
     * Gets from the given normalized parent MO (without access to DPS) the only child MO of given unscoped type.
     * 
     * @param parentMo
     *            the parent MO.
     * @param moType
     *            the unscoped MO type.
     * @return the FDN of the child MO or null if it does not exist.
     * @throws TooManyChildMosException
     *             if more than one MO is found.
     */
    public ManagedObject getOnlyChildMo(final ManagedObject parentMo, final String moType) {

        final List<ManagedObject> childMOs = getChildMos(parentMo, moType);
        if (childMOs.isEmpty()) {
            logger.debug("get OnlyChildMo : no child MO of type [{}] under parent MO [{}]", moType, parentMo.getFdn());
            return null;
        }
        if (childMOs.size() > 1) {
            final String errorMessage = String.format("Too many [%s] child MOs of type [%s] under parent MO [%s]", childMOs.size(), moType,
                    parentMo.getFdn());
            logger.error("get OnlyChildMo : {}", errorMessage);
            throw new TooManyChildMosException(errorMessage);
        }
        final ManagedObject childMO = childMOs.get(0);
        logger.debug("get OnlyChildMo : found child MO [{}] of type [{}] under parent MO [{}]", childMO.getFdn(), moType, parentMo.getFdn());
        return childMO;
    }

    /**
     * Gets from the given normalized parent MO (without access to DPS) the child MOs of given unscoped type.
     * 
     * @param parentMo
     *            the parent MO.
     * @param moType
     *            the unscoped MO type.
     * @return the list of the MOs. The list is empty if no child MOs found.
     */
    public List<ManagedObject> getChildMos(final ManagedObject parentMo, final String moType) {
        List<ManagedObject> childMos = new ArrayList<>();
        if (parentMo.getChildrenSize() > 0) {
            for (final ManagedObject mo : parentMo.getChildren()) {
                if (moType.equals(mo.getType())) {
                    childMos.add(mo);
                }
            }
        }
        logger.debug("get ChildMos : found [{}] children of type [{}] under [{}]", childMos.size(), moType, parentMo.getFdn());
        return childMos;
    }

    /**
     * Gets from DPS the asymmetric-key MO of given name in the keystore hierarchy for the given CBP-OI node reference.
     * 
     * @param normalizableNodeReference
     *            the node reference.
     * @param asymmetricKeyName
     *            the asymmetric-key MO name.
     * @return the MO or null if it does not exist.
     */
    public ManagedObject getAsymmetricKeyMO(final NormalizableNodeReference normalizableNodeReference, final String asymmetricKeyName) {

        ManagedObject asymmetricKeyMO = null;

        final String moType = ModelDefinition.KEYSTORE_TYPE;
        final String refMimNs = ModelDefinition.CBP_OI_KEYSTORE_NS;
        final ManagedObject keystoreMO = getNodeHierarchyTopMo(normalizableNodeReference, refMimNs, moType, CbpOiMoNaming.getName(moType));
        if (keystoreMO != null) {
            final ManagedObject asymmetricKeysMO = getChildMo(keystoreMO, normalizableNodeReference, ModelDefinition.ASYMMETRIC_KEYS_TYPE,
                    CbpOiMoNaming.getName(ModelDefinition.ASYMMETRIC_KEYS_TYPE));
            if (asymmetricKeysMO != null) {
                asymmetricKeyMO = getChildMo(asymmetricKeysMO, normalizableNodeReference, ModelDefinition.ASYMMETRIC_KEY_TYPE, asymmetricKeyName);
                if (asymmetricKeyMO == null) {
                    final String errorMessage = String.format("asymmetric-key MO with name [%s] not found for node [%s]", asymmetricKeyName,
                            normalizableNodeReference);
                    logger.error("get AsymmetricKeyMO failed: {}", errorMessage);
                }
            } else {
                final String errorMessage = String.format("asymmetric-keys MO not found for node [%s]", normalizableNodeReference);
                logger.error("get AsymmetricKeyMO failed: {}", errorMessage);
            }
        } else {
            final String errorMessage = String.format("keystore MO not found for node [%s]", normalizableNodeReference);
            logger.error("get AsymmetricKeyMO failed: {}", errorMessage);
        }
        return asymmetricKeyMO;
    }

    /**
     * Gets from DPS the certificates MO of given name in the truststore hierarchy for the given CBP-OI node reference.
     * 
     * @param normalizableNodeReference
     *            the node reference.
     * @param certificatesName
     *            the certificates MO name.
     * @return the MO or null if it does not exist.
     */
    public ManagedObject getCertificatesMO(final NormalizableNodeReference normalizableNodeReference, final String certificatesName) {

        ManagedObject certificatesMO = null;

        final String moType = ModelDefinition.TRUSTSTORE_TYPE;
        final String refMimNs = ModelDefinition.CBP_OI_TRUSTSTORE_NS;
        final ManagedObject truststoreMO = getNodeHierarchyTopMo(normalizableNodeReference, refMimNs, moType, CbpOiMoNaming.getName(moType));
        if (truststoreMO != null) {
            certificatesMO = getChildMo(truststoreMO, normalizableNodeReference, ModelDefinition.TRUSTSTORE_CERTIFICATES_TYPE, certificatesName);
            if (certificatesMO == null) {
                final String errorMessage = String.format("certificates MO with name [%s] not found for node [%s]", certificatesName,
                        normalizableNodeReference);
                logger.error("get CertificatesMO failed: {}", errorMessage);
            }
        } else {
            final String errorMessage = String.format("truststore MO not found for node [%s]", normalizableNodeReference);
            logger.error("get CertificatesMO failed: {}", errorMessage);
        }
        return certificatesMO;
    }

    /**
     * Gets from DPS the user MO of given name in the system hierarchy for the given CBP-OI node reference.
     * 
     * @param normalizableNodeReference
     *            the node reference.
     * @param userName
     *            the user MO name.
     * @return the MO or null if it does not exist.
     */
    public ManagedObject getUserMO(final NormalizableNodeReference normalizableNodeReference, final String userName) {

        ManagedObject userMO = null;

        final String moType = ModelDefinition.SYSTEM_TYPE;
        final String refMimNs = ModelDefinition.CBP_OI_SYSTEM_NS;
        final ManagedObject systemMO = getNodeHierarchyTopMo(normalizableNodeReference, refMimNs, moType, CbpOiMoNaming.getName(moType));
        if (systemMO != null) {
            final ManagedObject authenticationMO = getOnlyChildMo(systemMO, normalizableNodeReference, ModelDefinition.AUTHENTICATION_TYPE);
            if (authenticationMO != null) {
                userMO = getChildMo(authenticationMO, normalizableNodeReference, ModelDefinition.USER_TYPE, userName);
            } else {
                final String errorMessage = String.format("authorization MO not found for node [%s]", normalizableNodeReference);
                logger.error("get UserMO failed: {}", errorMessage);
            }
        } else {
            final String errorMessage = String.format("system MO not found for node [%s]", normalizableNodeReference);
            logger.error("get UserMO failed: {}", errorMessage);
        }
        return userMO;
    }

    /**
     * Gets from Model Service the scoped MO type (possibly containing the $$ notation) for an unscoped MO type (not containing the $$ notation) under
     * a given parent MO for a given target (node reference).
     * 
     * @param parentMo
     *            the parent MO.
     * @param normalizableNodeReference
     *            the target expressed as node reference.
     * @param unscopedMoType
     *            the unscoped MO type (not containing $$ notation).
     * @return the scoped MO type (possibly containing the $$ notation).
     */
    private String getScopedMoTypeFromUnscopedMoType(final ManagedObject parentMo, final NormalizableNodeReference normalizableNodeReference,
            final String unscopedMoType) {

        final String targetCategory = normalizableNodeReference.getTargetCategory();
        final String targetType = normalizableNodeReference.getNeType();
        final String targetModelIdentity = normalizableNodeReference.getOssModelIdentity();
        final String parentMoNamespace = parentMo.getNamespace();
        final String parentMoType = parentMo.getType();
        final String parentMoVersion = parentMo.getVersion();
        final String inputParams = String.format(
                "unscopedMoType [%s] targetCategory [%s] targetType [%s] targetModelIdentity [%s] parentMoNamespace [%s] parentMoType [%s] parentMoVersion [%s]",
                unscopedMoType, targetCategory, targetType, targetModelIdentity, parentMoNamespace, parentMoType, parentMoVersion);
        logger.debug("get MoTypeFromUnscopedType : starts for {}", inputParams);

        final String moType = nscsModelServiceImpl.getScopedMoTypeFromUnscopedMoType(targetCategory, targetType, targetModelIdentity,
                parentMoNamespace, parentMoType, parentMoVersion, unscopedMoType);

        logger.debug("get MoTypeFromUnscopedType : returns {} for {}", moType, inputParams);
        return moType;
    }

    /**
     * Get the NetworkElementSecurity MO under the given normalized root MO (without access to DPS).
     * 
     * @param normalizedRootMO
     *            the normalized root MO.
     * @return the NetworkElementSecurity MO or null if it does not exist.
     */
    public ManagedObject getNetworkElementSecurityMO(final ManagedObject normalizedRootMO) {

        ManagedObject networkElementSecurityMO = null;

        final ManagedObject securityFunctionMO = getOnlyChildMo(normalizedRootMO, SF_TYPE);
        if (securityFunctionMO != null) {
            networkElementSecurityMO = getOnlyChildMo(securityFunctionMO, NES_TYPE);
        } else {
            final String errorMessage = String.format("%s MO not found under parent MO [%s]", SF_TYPE, normalizedRootMO.getFdn());
            logger.error("get NetworkElementSecurityMO failed: {}", errorMessage);
        }
        return networkElementSecurityMO;
    }

    /**
     * Get the ConnectivityInformation MO under the given normalized root MO (without access to DPS) for the given target (node reference).
     * 
     * @param normalizedRootMO
     *            the normalized root MO.
     * @param normalizedNodeRef
     *            the target expressed as node reference.
     * @return the ConnectivityInformation MO or null if it does not exist.
     */
    public ManagedObject getConnectivityInformationMO(final ManagedObject normalizedRootMO, final NormalizableNodeReference normalizedNodeRef) {
        final NscsModelInfo nscsModelInfo = nscsModelServiceImpl.getConnectivityInfo(normalizedNodeRef.getTargetCategory(),
                normalizedNodeRef.getNeType());
        return getOnlyChildMo(normalizedRootMO, nscsModelInfo.getName());
    }

    /**
     * Get the protocol family from the ConnectivityInformation MO under the given normalized root MO (without access to DPS) for the given target
     * (node reference).
     * 
     * @param normalizedRootMO
     *            the normalized root MO.
     * @param normalizedNodeRef
     *            the target expressed as node reference.
     * @return the protocol family or null if ConnectivityInformation MO does not exist.
     */
    public StandardProtocolFamily getProtocolFamilyFromConnectivityInformationMO(final ManagedObject normalizedRootMO,
            final NormalizableNodeReference normalizedNodeRef) {
        StandardProtocolFamily protocolFamily = null;
        final ManagedObject connectivityInfoMO = getConnectivityInformationMO(normalizedRootMO, normalizedNodeRef);
        if (connectivityInfoMO != null) {
            final String ipAddress = connectivityInfoMO.getAttribute(ModelDefinition.IP_ADDRESS);
            if (NscsCommonValidator.getInstance().isValidIPv4Address(ipAddress)) {
                protocolFamily = StandardProtocolFamily.INET;
            } else if (NscsCommonValidator.getInstance().isValidIPv6Address(ipAddress)) {
                protocolFamily = StandardProtocolFamily.INET6;
            }
        }
        return protocolFamily;
    }

}
