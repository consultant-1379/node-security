/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2024
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
*******************************************************************************
*----------------------------------------------------------------------------*/
package com.ericsson.oss.services.security.nscs.nbi;

import java.util.Arrays;
import java.util.concurrent.locks.Lock;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.nms.security.nscs.api.CredentialService;
import com.ericsson.nms.security.nscs.api.credentials.CredentialAttributes;
import com.ericsson.nms.security.nscs.api.credentials.SnmpV3Attributes;
import com.ericsson.nms.security.nscs.api.enums.SnmpSecurityLevel;
import com.ericsson.nms.security.nscs.api.exception.CommandSyntaxException;
import com.ericsson.nms.security.nscs.api.exception.CredentialServiceException;
import com.ericsson.nms.security.nscs.api.exception.InvalidArgumentValueException;
import com.ericsson.nms.security.nscs.api.exception.InvalidNodeNameException;
import com.ericsson.nms.security.nscs.api.exception.InvalidSubjAltNameXmlException;
import com.ericsson.nms.security.nscs.api.exception.NetworkElementSecurityNotfoundException;
import com.ericsson.nms.security.nscs.api.exception.NodeDoesNotExistException;
import com.ericsson.nms.security.nscs.api.exception.NscsBadRequestException;
import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.exception.SecurityFunctionMoNotfoundException;
import com.ericsson.nms.security.nscs.api.exception.SubjAltNameTypeNotSupportedXmlException;
import com.ericsson.nms.security.nscs.api.exception.UnexpectedErrorException;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.enrollmentinfo.request.model.NodeDetails;
import com.ericsson.nms.security.nscs.enrollmentinfo.response.model.EnrollmentInfo;
import com.ericsson.oss.itpf.sdk.cluster.lock.LockManager;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.services.security.nscs.nbi.resources.NscsLdapResponse;
import com.ericsson.oss.services.security.nscs.nbi.resources.NscsResourceInstance;

@Stateless
public class NbiServiceBean implements NbiService {
    private static final String SECURITY_SERVICE_LOCK_CLUSTER = "SecurityServiceLockCluster";
    private static final String NODE_RESOURCE_LOCK_NAME_FORMAT = "node-%s";
    private static final String TRYING_TO_ACQUIRE_LOCK = "[NSCS_NBI_SERVICE] trying to acquire lock [{}]";
    private static final String ACQUIRED_LOCK = "[NSCS_NBI_SERVICE] acquired lock [{}]";
    private static final String GOING_TO_RELEASE_LOCK = "[NSCS_NBI_SERVICE] going to release lock [{}]";
    private static final String RELEASED_LOCK = "[NSCS_NBI_SERVICE] released lock [{}]";
    private static final String NULL_MANDATORY_DTO_ATTR = "Null mandatory DTO attribute";
    private static final String PLEASE_PROVIDE_A_NOT_NULL_MANDATORY_DTO_ATTR = "Please provide a not null mandatory DTO attribute";

    private final Logger logger = LoggerFactory.getLogger(NbiServiceBean.class);

    @Inject
    private LockManager lockManager;

    @EServiceRef
    private CredentialService credentialService;

    @Inject
    private DomainHandlerImpl domainHandlerImpl;

    @Inject
    private LdapHandlerImpl ldapHandlerImpl;

    @Override
    public void createOrUpdateNodeCredentials(final String nodeName, final CredentialAttributes credentialAttributes,
            final String enablingPredefinedENMLDAPUser) {
        final String resourceLockName = getNodeResourceLockName(nodeName);
        final Lock resourceLock = lockManager.getDistributedLock(resourceLockName, SECURITY_SERVICE_LOCK_CLUSTER);
        try {
            logger.debug(TRYING_TO_ACQUIRE_LOCK, resourceLockName);
            resourceLock.lock();
            logger.debug(ACQUIRED_LOCK, resourceLockName);

            if (!credentialService.validateAttributes(nodeName, credentialAttributes)) {
                throw new NscsBadRequestException(NscsErrorCodes.INVALID_INPUT_VALUE);
            }
            credentialService.createNodeCredentials(credentialAttributes, nodeName, enablingPredefinedENMLDAPUser);
        } catch (final NscsServiceException | CredentialServiceException e) {
            remapException(e);
        } finally {
            logger.debug(GOING_TO_RELEASE_LOCK, resourceLockName);
            resourceLock.unlock();
            logger.debug(RELEASED_LOCK, resourceLockName);
        }
    }

    @Override
    public void createOrUpdateNodeSnmp(final String nodeName, final SnmpV3Attributes snmpV3Attributes, final SnmpSecurityLevel snmpSecurityLevel) {
        final String resourceLockName = getNodeResourceLockName(nodeName);
        final Lock resourceLock = lockManager.getDistributedLock(resourceLockName, SECURITY_SERVICE_LOCK_CLUSTER);
        try {
            logger.debug(TRYING_TO_ACQUIRE_LOCK, resourceLockName);
            resourceLock.lock();
            logger.debug(ACQUIRED_LOCK, resourceLockName);

            credentialService.configureSnmpV3(snmpSecurityLevel, snmpV3Attributes, Arrays.asList(nodeName));
        } catch (final NscsServiceException | CredentialServiceException e) {
            remapException(e);
        } finally {
            logger.debug(GOING_TO_RELEASE_LOCK, resourceLockName);
            resourceLock.unlock();
            logger.debug(RELEASED_LOCK, resourceLockName);
        }
    }

    @Override
    public EnrollmentInfo generateEnrollmentInfo(final String nodeNameOrFdn, final String domainName, final String ipFamily,
            final NodeDetails nodeDetails) {
        EnrollmentInfo enrollmentInfo = null;
        final String resourceLockName = getNodeResourceLockName(nodeNameOrFdn);
        final Lock resourceLock = lockManager.getDistributedLock(resourceLockName, SECURITY_SERVICE_LOCK_CLUSTER);
        try {
            logger.debug(TRYING_TO_ACQUIRE_LOCK, resourceLockName);
            resourceLock.lock();
            logger.debug(ACQUIRED_LOCK, resourceLockName);

            enrollmentInfo = domainHandlerImpl.generateEnrollmentInfo(nodeNameOrFdn, domainName, ipFamily, nodeDetails);
        } catch (final NscsBadRequestException e) {
            throw e;
        } catch (final NodeDoesNotExistException | NetworkElementSecurityNotfoundException | InvalidArgumentValueException
                | SubjAltNameTypeNotSupportedXmlException | InvalidSubjAltNameXmlException e) {
            throw new NscsBadRequestException("Generate enrollment failed", e);
        } catch (final Exception e) {
            throw new UnexpectedErrorException(e);
        } finally {
            logger.debug(GOING_TO_RELEASE_LOCK, resourceLockName);
            resourceLock.unlock();
            logger.debug(RELEASED_LOCK, resourceLockName);
        }
        return enrollmentInfo;
    }

    @Override
    public NscsResourceInstance deleteEnrollmentInfo(final String nodeNameOrFdn, final String domainName) {
        NscsResourceInstance resourceInstance;
        final String resourceLockName = getNodeResourceLockName(nodeNameOrFdn);
        final Lock resourceLock = lockManager.getDistributedLock(resourceLockName, SECURITY_SERVICE_LOCK_CLUSTER);
        try {
            logger.debug(TRYING_TO_ACQUIRE_LOCK, resourceLockName);
            resourceLock.lock();
            logger.debug(ACQUIRED_LOCK, resourceLockName);

            resourceInstance = domainHandlerImpl.deleteEnrollmentInfo(nodeNameOrFdn, domainName);
        } catch (final NodeDoesNotExistException | InvalidArgumentValueException e) {
            throw new NscsBadRequestException("Delete enrollment failed", e);
        } catch (final Exception e) {
            throw new UnexpectedErrorException(e);
        } finally {
            logger.debug(GOING_TO_RELEASE_LOCK, resourceLockName);
            resourceLock.unlock();
            logger.debug(RELEASED_LOCK, resourceLockName);
        }
        return resourceInstance;
    }

    @Override
    public NscsLdapResponse createLdapConfiguration(final String nodeNameOrFdn, final String ipFamily) {
        NscsLdapResponse ldapResponse;
        final String resourceLockName = getNodeResourceLockName(nodeNameOrFdn);
        final Lock resourceLock = lockManager.getDistributedLock(resourceLockName, SECURITY_SERVICE_LOCK_CLUSTER);
        try {
            logger.debug(TRYING_TO_ACQUIRE_LOCK, resourceLockName);
            resourceLock.lock();
            logger.debug(ACQUIRED_LOCK, resourceLockName);

            ldapResponse = ldapHandlerImpl.createLdapConfiguration(nodeNameOrFdn, ipFamily);
        } catch (final NscsBadRequestException e) {
            throw e;
        } catch (final NodeDoesNotExistException | NetworkElementSecurityNotfoundException e) {
            throw new NscsBadRequestException("Create LDAP configuration failed", e);
        } catch (final Exception e) {
            throw new UnexpectedErrorException(e);
        } finally {
            logger.debug(GOING_TO_RELEASE_LOCK, resourceLockName);
            resourceLock.unlock();
            logger.debug(RELEASED_LOCK, resourceLockName);
        }
        return ldapResponse;
    }

    @Override
    public NscsResourceInstance deleteLdapConfiguration(final String nodeNameOrFdn) {
        NscsResourceInstance resourceInstance;
        final String resourceLockName = getNodeResourceLockName(nodeNameOrFdn);
        final Lock resourceLock = lockManager.getDistributedLock(resourceLockName, SECURITY_SERVICE_LOCK_CLUSTER);
        try {
            logger.debug(TRYING_TO_ACQUIRE_LOCK, resourceLockName);
            resourceLock.lock();
            logger.debug(ACQUIRED_LOCK, resourceLockName);

            resourceInstance = ldapHandlerImpl.deleteLdapConfiguration(nodeNameOrFdn);
        } catch (final NodeDoesNotExistException | NetworkElementSecurityNotfoundException e) {
            throw new NscsBadRequestException("Delete LDAP configuration failed", e);
        } catch (final Exception e) {
            throw new UnexpectedErrorException(e);
        } finally {
            logger.debug(GOING_TO_RELEASE_LOCK, resourceLockName);
            resourceLock.unlock();
            logger.debug(RELEASED_LOCK, resourceLockName);
        }
        return resourceInstance;
    }

    /**
     * Get the name of resource lock for a given node resource.
     * 
     * @param nodeNameOrFdn
     *            the node name or FDN.
     * @return the resource lock name.
     */
    private String getNodeResourceLockName(final String nodeNameOrFdn) {
        final NodeReference nodeReference = new NodeRef(nodeNameOrFdn);
        return String.format(NODE_RESOURCE_LOCK_NAME_FORMAT, nodeReference.getFdn());
    }

    private void remapException(final RuntimeException e) {
        if (e.getCause() != null) {
            if (e.getCause().getCause() != null) {
                if (e.getCause().getCause() instanceof InvalidNodeNameException) {
                    throw new NodeDoesNotExistException();
                } else if (e.getCause().getCause() instanceof NetworkElementSecurityNotfoundException) {
                    throw new NetworkElementSecurityNotfoundException();
                } else if (e.getCause().getCause() instanceof CommandSyntaxException) {
                    throw new NscsBadRequestException(NULL_MANDATORY_DTO_ATTR, PLEASE_PROVIDE_A_NOT_NULL_MANDATORY_DTO_ATTR);
                } else if (e.getCause().getCause() instanceof SecurityFunctionMoNotfoundException) {
                    throw new NscsBadRequestException(NscsErrorCodes.SECURITY_FUNCTION_NOT_FOUND_FOR_THIS_NODE,
                            NscsErrorCodes.CREATE_A_SECURITY_FUNCTION_MO_ASSOCIATED_TO_THIS_NODE);
                }
            }
            if (e.getCause() instanceof InvalidNodeNameException) {
                throw new NodeDoesNotExistException();
            } else if (e.getCause() instanceof NetworkElementSecurityNotfoundException) {
                throw new NetworkElementSecurityNotfoundException();
            } else if (e.getCause() instanceof CommandSyntaxException) {
                throw new NscsBadRequestException(NULL_MANDATORY_DTO_ATTR, PLEASE_PROVIDE_A_NOT_NULL_MANDATORY_DTO_ATTR);
            } else if (e.getCause() instanceof SecurityFunctionMoNotfoundException) {
                throw new NscsBadRequestException(NscsErrorCodes.SECURITY_FUNCTION_NOT_FOUND_FOR_THIS_NODE,
                        NscsErrorCodes.CREATE_A_SECURITY_FUNCTION_MO_ASSOCIATED_TO_THIS_NODE);
            }
        }
        throw e;
    }
}
