/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2023
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
*******************************************************************************
*----------------------------------------------------------------------------*/
package com.ericsson.oss.services.security.nscs.ldap.service;

import java.util.Objects;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.exception.InvalidNodeTypeException;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.node.attribute.CommonLdapConfigurationTask;

/**
 * Factory class to provide the implementation of the MO LDAP Service for the specified node.
 */
public class MOLdapServiceFactory {

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private BeanManager beanManager;

    @Inject
    private NscsCapabilityModelService capabilityService;

    /**
     * Validate the LDAP configuration contained in the given task for the given node.
     * 
     * @param task
     *            the task.
     * @param normalizable
     *            the normalizable node reference.
     * @throws {@link
     *             IllegalArgumentException} if LDAP configuration is invalid.
     */
    public void validateLdapConfiguration(final CommonLdapConfigurationTask task, final NormalizableNodeReference normalizable) {
        final String qualifier = getMOLdapServiceTypeString(normalizable);
        final Bean<?> bean = getMOLdapBeanForOption(qualifier);
        final CreationalContext<?> creationalContext = beanManager.createCreationalContext(bean);
        try {
            final MOLdapService moLdapService = (MOLdapService) beanManager.getReference(bean, MOLdapService.class, creationalContext);
            moLdapService.validateLdapConfiguration(task, normalizable);
        } finally {
            creationalContext.release();
        }
    }

    /**
     * Configure the LDAP client on the given node according to the ldap workflow context of the given task.
     * 
     * The ldap workflow context of the given task is modified adding the bind DN configured on the node before updating it.
     * 
     * @param task
     *            the task.
     * @param normalizable
     *            the normalizable node reference.
     */
    public void ldapConfigure(final CommonLdapConfigurationTask task, final NormalizableNodeReference normalizable) {
        final String qualifier = getMOLdapServiceTypeString(normalizable);
        final Bean<?> bean = getMOLdapBeanForOption(qualifier);
        final CreationalContext<?> creationalContext = beanManager.createCreationalContext(bean);
        try {
            final MOLdapService moLdapService = (MOLdapService) beanManager.getReference(bean, MOLdapService.class, creationalContext);
            moLdapService.ldapConfigure(task, normalizable);
        } finally {
            creationalContext.release();
        }
    }

    /**
     * Return the String of qualifier for the given node reference.
     *
     * @param nodeReference
     *            the node reference.
     * @return the qualifier.
     * @throws InvalidNodeTypeException
     */
    private String getMOLdapServiceTypeString(final NodeReference nodeReference) throws InvalidNodeTypeException {

        String qualifier;
        try {
            qualifier = capabilityService.getMomType(nodeReference);
        } catch (final Exception e) {
            final String errorMessage = String.format("Exception %s : %s occurred while getting LDAP qualifier for node %s",
                    e.getClass().getCanonicalName(), e.getMessage(), nodeReference);
            nscsLogger.error(errorMessage, e);
            throw new InvalidNodeTypeException(errorMessage);
        }
        return qualifier;
    }

    /**
     * Get the instance of MO Ldap service for the given qualifier.
     *
     * @param qualifier
     *            the qualifier (the node platform).
     * @return the bean instance.
     * @throws InvalidNodeTypeException
     *             if no instance or more than one instance found.
     */
    @SuppressWarnings({ "unchecked" })
    private Bean<?> getMOLdapBeanForOption(final String qualifier) throws InvalidNodeTypeException {
        try {
            Set<Bean<?>> beans = beanManager.getBeans(MOLdapService.class, new NscsMOLdapServiceQualifier(qualifier));
            if (beans.size() == 1) {
                return beans.iterator().next();
            } else if (beans.isEmpty()) {
                final String errorMessage = String.format("No MOLdapService registered for qualifier %s", qualifier);
                nscsLogger.error(errorMessage);
                throw new InvalidNodeTypeException(errorMessage);
            } else {
                final String errorMessage = String.format("Multiple MOLdapService registered for qualifier %s", qualifier);
                nscsLogger.error(errorMessage);
                throw new InvalidNodeTypeException(errorMessage);
            }
        } catch (final Exception e) {
            final String errorMessage = String.format("Exception %s : %s occurred while retrieving MOLdapService registered for qualifier %s",
                    e.getClass().getCanonicalName(), e.getMessage(), qualifier);
            nscsLogger.error(errorMessage, e);
            throw new InvalidNodeTypeException(errorMessage);
        }
    }

    /**
     * Auxiliary class modeling the MO Ldap service qualifier.
     */
    public class NscsMOLdapServiceQualifier extends AnnotationLiteral<MOLdapServiceType> implements MOLdapServiceType {

        private static final long serialVersionUID = -4647696880578137978L;
        private final String moLdapServiceType;

        public NscsMOLdapServiceQualifier(final String qualifier) {
            this.moLdapServiceType = qualifier;
        }

        @Override
        public String moLdapServiceType() {
            return this.moLdapServiceType;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = super.hashCode();
            result = prime * result + getEnclosingInstance().hashCode();
            result = prime * result + Objects.hash(moLdapServiceType);
            return result;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (!super.equals(obj)) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            NscsMOLdapServiceQualifier other = (NscsMOLdapServiceQualifier) obj;
            if (!getEnclosingInstance().equals(other.getEnclosingInstance())) {
                return false;
            }
            return Objects.equals(moLdapServiceType, other.moLdapServiceType);
        }

        private MOLdapServiceFactory getEnclosingInstance() {
            return MOLdapServiceFactory.this;
        }
    }

}
