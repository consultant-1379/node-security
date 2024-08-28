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
package com.ericsson.nms.security.nscs.data.moget;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.exception.InvalidNodeTypeException;
import com.ericsson.nms.security.nscs.api.exception.SecurityMODoesNotExistException;
import com.ericsson.nms.security.nscs.api.exception.TrustCategoryMODoesNotExistException;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.data.moaction.MoActionState;
import com.ericsson.nms.security.nscs.data.moaction.MoActionWithParameter;
import com.ericsson.nms.security.nscs.data.moaction.MoActionWithoutParameter;
import com.ericsson.nms.security.nscs.data.moget.param.CertStateInfo;
import com.ericsson.nms.security.nscs.data.moget.param.NtpServer;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;

/**
 * Factory class to provide the implementation of the MO Get Service for the specified node.
 *
 */
public class MOGetServiceFactory {

    @Inject
    private Logger logger;

    @Inject
    private BeanManager beanManager;

    @Inject
    private NscsCapabilityModelService capabilityService;

    /**
     * Perform get of certificate enrollment state for the given node and the given certificate type.
     *
     * Some details of certificate itself are returned as well.
     *
     * @param nodeRef
     * @param certType
     * @return
     */
    public CertStateInfo getCertificateIssueStateInfo(final NodeReference nodeRef, final String certType) {
        CertStateInfo certStateInfo = null;
        final String qualifier = getMOGetServiceTypeString(nodeRef);
        Bean<?> bean = getMOGetBeanForOption(qualifier);
        CreationalContext<?> creationalContext = beanManager.createCreationalContext(bean);
        try {
            final MOGetService moGetService = (MOGetService) beanManager.getReference(bean, MOGetService.class, creationalContext);
            if (moGetService != null) {
                certStateInfo = moGetService.getCertificateIssueStateInfo(nodeRef, certType);
            }
        } finally {
            creationalContext.release();
        }
        return certStateInfo;
    }

    /**
     * Perform get of trusted certificate install state for the given node and the given trust category type.
     * 
     * Some details of trusted certificates are returned as well.
     * 
     * @param nodeRef
     * @param trustCategory
     * @return
     */
    public CertStateInfo getTrustCertificateStateInfo(final NodeReference nodeRef, final String trustCategory) {
        CertStateInfo certStateInfo = null;
        final String qualifier = getMOGetServiceTypeString(nodeRef);
        Bean<?> bean = getMOGetBeanForOption(qualifier);
        CreationalContext<?> creationalContext = beanManager.createCreationalContext(bean);
        try {
            final MOGetService moGetService = (MOGetService) beanManager.getReference(bean, MOGetService.class, creationalContext);
            if (moGetService != null) {
                certStateInfo = moGetService.getTrustCertificateStateInfo(nodeRef, trustCategory);
            }
        } finally {
            creationalContext.release();
        }
        return certStateInfo;
    }

    /**
     * Gets the security level of a node given its current nodeRef and sync status
     *
     * @param nodeRef
     * @param syncstatus
     * @return the actual securityLevel
     */
    public String getSecurityLevel(final NormalizableNodeReference nodeRef, final String syncstatus) {
        String securityLevel = null;
        final String qualifier = getMOGetServiceTypeString(nodeRef);
        Bean<?> bean = getMOGetBeanForOption(qualifier);
        CreationalContext<?> creationalContext = beanManager.createCreationalContext(bean);
        try {
            final MOGetService moGetService = (MOGetService) beanManager.getReference(bean, MOGetService.class, creationalContext);
            if (moGetService != null) {
                securityLevel = moGetService.getSecurityLevel(nodeRef, syncstatus);
            }
        } finally {
            creationalContext.release();
        }
        return securityLevel;
    }

    /**
     * Returns the current IPSec configuration of the node
     *
     * @param normNode
     * @param syncstatus
     * @return the actual ipsec configuration
     */
    public String getIpsecConfig(final NormalizableNodeReference nodeRef, final String syncstatus) {
        String ipsecConfig = null;
        final String qualifier = getMOGetServiceTypeString(nodeRef);
        Bean<?> bean = getMOGetBeanForOption(qualifier);
        CreationalContext<?> creationalContext = beanManager.createCreationalContext(bean);
        try {
            final MOGetService moGetService = (MOGetService) beanManager.getReference(bean, MOGetService.class, creationalContext);
            if (moGetService != null) {
                ipsecConfig = moGetService.getIpsecConfig(nodeRef, syncstatus);
            }
        } finally {
            creationalContext.release();
        }
        return ipsecConfig;
    }

    /**
     * Return progress state of specified MO action without parameters performed on given MO (specified by FDN).
     *
     * @param nodeRef
     * @param moFdn
     * @param action
     * @return
     */
    public MoActionState getMoActionState(final NodeReference nodeRef, final String moFdn, final MoActionWithoutParameter action) {
        MoActionState moActionState = null;
        final String qualifier = getMOGetServiceTypeString(nodeRef);
        Bean<?> bean = getMOGetBeanForOption(qualifier);
        CreationalContext<?> creationalContext = beanManager.createCreationalContext(bean);
        try {
            final MOGetService moGetService = (MOGetService) beanManager.getReference(bean, MOGetService.class, creationalContext);
            if (moGetService != null) {
                moActionState = moGetService.getMoActionState(moFdn, action);
            }
        } finally {
            creationalContext.release();
        }
        return moActionState;
    }

    /**
     * Return progress state of specified MO action with parameters performed on given MO (specified by FDN).
     *
     * @param nodeRef
     * @param moFdn
     * @param action
     * @return
     */
    public MoActionState getMoActionState(final NodeReference nodeRef, final String moFdn, final MoActionWithParameter action) {
        MoActionState moActionState = null;
        final String qualifier = getMOGetServiceTypeString(nodeRef);
        Bean<?> bean = getMOGetBeanForOption(qualifier);
        CreationalContext<?> creationalContext = beanManager.createCreationalContext(bean);
        try {
            final MOGetService moGetService = (MOGetService) beanManager.getReference(bean, MOGetService.class, creationalContext);
            if (moGetService != null) {
                moActionState = moGetService.getMoActionState(moFdn, action);
            }
        } finally {
            creationalContext.release();
        }
        return moActionState;
    }

    /**
     * Return crlCheck or certRevStatusCheck MO Attribute value
     *
     * @param nodeRef
     * @param certType
     *
     * @return the MO Attribute Value
     */
    public String getCrlCheckStatus(final NormalizableNodeReference nodeRef, final String certType) {
        String crlCheckStatus = null;
        final String qualifier = getMOGetServiceTypeString(nodeRef);
        Bean<?> bean = getMOGetBeanForOption(qualifier);
        CreationalContext<?> creationalContext = beanManager.createCreationalContext(bean);
        try {
            final MOGetService moGetService = (MOGetService) beanManager.getReference(bean, MOGetService.class, creationalContext);
            if (moGetService != null) {
                crlCheckStatus = moGetService.getCrlCheckStatus(nodeRef, certType);
            }
        } finally {
            creationalContext.release();
        }
        return crlCheckStatus;
    }

    /**
     * To validate node for crl check MO existance
     * 
     * @param nodeRef
     * @param certType
     */
    public boolean validateNodeForCrlCheckMO(final NormalizableNodeReference nodeRef, final String certType)
            throws SecurityMODoesNotExistException, TrustCategoryMODoesNotExistException {
        Boolean isCrlCheckMoExistent = null;
        final String qualifier = getMOGetServiceTypeString(nodeRef);
        Bean<?> bean = getMOGetBeanForOption(qualifier);
        CreationalContext<?> creationalContext = beanManager.createCreationalContext(bean);
        try {
            final MOGetService moGetService = (MOGetService) beanManager.getReference(bean, MOGetService.class, creationalContext);
            if (moGetService != null) {
                isCrlCheckMoExistent = moGetService.validateNodeForCrlCheckMO(nodeRef, certType);
            }
        } finally {
            creationalContext.release();
        }
        return isCrlCheckMoExistent;
    }

    /**
     * Return the String of qualifier for the given nodeRef
     *
     * @param nodeRef
     * @return
     * @throws InvalidNodeTypeException
     */
    String getMOGetServiceTypeString(final NodeReference nodeRef) throws InvalidNodeTypeException {

        String qualifier;
        try {
            qualifier = capabilityService.getMomType(nodeRef);
        } catch (final Exception e) {
            final String errorMessage = "Caught exception[" + e.getClass().getName() + "] msg[" + e.getMessage()
                    + "] getting CertificateStateInfoTypes for NodeReference[" + nodeRef + "].";
            logger.error(errorMessage);
            throw new InvalidNodeTypeException(errorMessage);
        }
        return qualifier;
    }

    /**
     * Get the instance of MO getter for the given qualifier.
     *
     * @param qualifier
     *            the qualifier (the node family).
     * @return the bean instance.
     * @throws InvalidNodeTypeException
     *             if no instance or more than one instance found.
     */
    @SuppressWarnings({ "unchecked" })
    private Bean<?> getMOGetBeanForOption(final String qualifier) throws InvalidNodeTypeException {
        try {
            Set<Bean<?>> beans = beanManager.getBeans(MOGetService.class, new NscsMOGetServiceQualifier(qualifier));
            if (beans.size() == 1) {
                Bean<MOGetService> bean = (Bean<MOGetService>) beans.iterator().next();
                return bean;
            } else if (beans.size() < 1) {
                String msg = "No MOGetService registered for option " + qualifier;
                logger.error(msg);
                throw new InvalidNodeTypeException(msg);
            } else {
                String msg = "Multiple MOGetService registered for option " + qualifier;
                logger.error(msg);
                throw new InvalidNodeTypeException(msg);
            }
        } catch (final Exception e) {
            String msg = "Internal Error retrieving MOGetService registered for option " + qualifier;
            logger.error(msg);
            throw new InvalidNodeTypeException(msg);
        }
    }

    /**
     * Auxiliary class modeling the getCertStateInfo implementation qualifier.
     */
    public class NscsMOGetServiceQualifier extends AnnotationLiteral<MOGetServiceType> implements MOGetServiceType {

        private static final long serialVersionUID = -1374078063069741434L;
        private final String moGetServiceType;

        public NscsMOGetServiceQualifier(final String qualifier) {
            this.moGetServiceType = qualifier;
        }

        @Override
        public String moGetServiceType() {
            return this.moGetServiceType;
        }
    }

    /**
     * validates the node for NTP support by checking the NTP server related MO attributes
     *
     * @param nodeRef
     *        normalizable node reference
     */
    public boolean validateNodeForNtp(final NormalizableNodeReference nodeRef) {
        Boolean isNtpSupported = false;
        CreationalContext<?> creationalContext = null;
        try {
            final String qualifier = getMOGetServiceTypeString(nodeRef);
            Bean<?> bean = getMOGetBeanForOption(qualifier);
            creationalContext = beanManager.createCreationalContext(bean);
            final MOGetService moGetService = (MOGetService) beanManager.getReference(bean, MOGetService.class, creationalContext);
            if (moGetService != null) {
                isNtpSupported = moGetService.validateNodeForNtp(nodeRef);
            }
        } catch (InvalidNodeTypeException exc) {
            final String errorMessage = "Caught exception[" + exc.getClass().getName() + "] msg[" + exc.getMessage()
                    + "] invalid node type for NodeReference[" + nodeRef + "].";
            logger.error(errorMessage);
        } finally {
            if (creationalContext != null) {
                creationalContext.release();
            }
        }
        return isNtpSupported;
    }

    /**
     * Perform ntp list for the given node.
     *
     * @param nodeRef
     *            normalizable node reference
     * @return listNtpServerKeyIds
     *            list of NTP server Key Id's
     */
    public List<NtpServer> listNtpServerDetails(final NormalizableNodeReference nodeRef) {
        List<NtpServer> listNtpServerKeyIds = new ArrayList<>();
        final String qualifier = getMOGetServiceTypeString(nodeRef);
        final Bean<?> bean = getMOGetBeanForOption(qualifier);
        CreationalContext<?> creationalContext = beanManager.createCreationalContext(bean);
        try {
            final MOGetService moGetService = (MOGetService) beanManager.getReference(bean, MOGetService.class, creationalContext);
            if (moGetService != null) {
                listNtpServerKeyIds = moGetService.listNtpServerDetails(nodeRef);
            }
        } finally {
            creationalContext.release();
        }
        return listNtpServerKeyIds;
    }

    /**
     * Convert the given keySize from the NSCS normalized value to the node supported value for the given node.
     *
     * @param nodeRef
     *            normalizable node reference
     * @param keySize
     *            the NSCS normalized key size value
     *
     * @return the node supported key size value
     */
    public String getNodeCredentialKeyInfo(final NormalizableNodeReference nodeRef, final String keySize) {
        String keyAlgorithm = null;
        final String qualifier = getMOGetServiceTypeString(nodeRef);
        final Bean<?> bean = getMOGetBeanForOption(qualifier);
        CreationalContext<?> creationalContext = beanManager.createCreationalContext(bean);
        try {
            final MOGetService moGetService = (MOGetService) beanManager.getReference(bean, MOGetService.class, creationalContext);
            if (moGetService != null) {
                keyAlgorithm = moGetService.getNodeSupportedFormatOfKeyAlgorithm(nodeRef, keySize);
            }
        } finally {
            creationalContext.release();
        }
        return keyAlgorithm;
    }
}