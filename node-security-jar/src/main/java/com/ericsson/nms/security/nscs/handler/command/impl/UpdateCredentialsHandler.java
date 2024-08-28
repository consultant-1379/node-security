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

package com.ericsson.nms.security.nscs.handler.command.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.types.CredentialsCommand;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.exception.UnexpectedErrorException;
import com.ericsson.nms.security.nscs.api.ldap.LdapApplicationUserManager;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.ModelDefinition.NetworkElementSecurity;
import com.ericsson.nms.security.nscs.data.ModelDefinition.NormalizedRootMO;
import com.ericsson.nms.security.nscs.data.NscsCMWriterService;
import com.ericsson.nms.security.nscs.data.NscsCMWriterService.WriterSpecificationBuilder;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.gim.EcimUserManager;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.command.CommandHandler;
import com.ericsson.nms.security.nscs.handler.command.CommandHandlerInterface;
import com.ericsson.nms.security.nscs.handler.command.CommandType;
import com.ericsson.nms.security.nscs.handler.command.utility.PasswordHelper;
import com.ericsson.nms.security.nscs.handler.validation.UseValidator;
import com.ericsson.nms.security.nscs.handler.validation.impl.CheckLdapUserOptionValidator;
import com.ericsson.nms.security.nscs.handler.validation.impl.CredUpdateParamsValidator;
import com.ericsson.nms.security.nscs.handler.validation.impl.NoDuplNodeNamesAllowedValidator;
import com.ericsson.nms.security.nscs.handler.validation.impl.NodeMustBeNormalizableValidator;
import com.ericsson.nms.security.nscs.handler.validation.impl.NodeMustHaveNetworkElementSecurityMoValidator;
import com.ericsson.nms.security.nscs.handler.validation.impl.NodeMustHaveSecurityFunctionMoValidator;
import com.ericsson.nms.security.nscs.handler.validation.impl.NormalizedNodeMustExistValidator;
import com.ericsson.nms.security.nscs.handler.validation.impl.StarIsNotAllowedValidator;
import com.ericsson.oss.services.security.nscs.context.NscsContextService;

/**
 * <p>
 * Updates a NetworkElementSecurity Mo associated to each of the specified nodes.
 * </p>
 */
@UseValidator({ NoDuplNodeNamesAllowedValidator.class, StarIsNotAllowedValidator.class, NormalizedNodeMustExistValidator.class,
    NodeMustBeNormalizableValidator.class, CredUpdateParamsValidator.class, NodeMustHaveSecurityFunctionMoValidator.class,
    NodeMustHaveNetworkElementSecurityMoValidator.class, CheckLdapUserOptionValidator.class })
@CommandType(NscsCommandType.UPDATE_CREDENTIALS)
@Local(CommandHandlerInterface.class)
public class UpdateCredentialsHandler implements CommandHandler<CredentialsCommand>, CommandHandlerInterface {

    public static final String ALL_CREDENTIALS_UPDATED_SUCCESSFULLY = "All credentials updated successfully";

    private final Map<String, String> mandatoryAttributes = new HashMap<>();

    @Inject
    private Logger logger;

    @Inject
    private NscsCMWriterService writer;

    @Inject
    private PasswordHelper passwordHelper;

    @Inject
    private NscsCapabilityModelService nscsCapabilityModelService;

    @Inject
    private EcimUserManager ecimUserManager;

    @EJB
    private LdapApplicationUserManager ldapApplicationUserManager;

    @Inject
    private NscsContextService nscsContextService;

    /**
     * @param command
     *            CredentialsCommand instance
     * @param context
     *            a CommandContext instance
     * @return NscsMessageCommandResponse in case of success
     */
    @Override
    public NscsCommandResponse process(final CredentialsCommand command, final CommandContext context) throws NscsServiceException {

        initMapMandatoryAttributes(command);

        logger.debug("Updating credentials");

        boolean propagate = false;

        String ldapApplicationUserPassword = null;
        final boolean ldapuserValue = "disable".equalsIgnoreCase(command.getLdapUserEnable()) ? false : true;

        nscsContextService.initItemsStatsForSyncCommand(Integer.valueOf(context.getValidNodes().size()), Integer.valueOf(0));

        if (ldapuserValue) {
            for (final NormalizableNodeReference node : context.getValidNodes()) {

                if (nscsCapabilityModelService.isLdapCommonUserSupported(node)) {
                    logger.info("Retrieve or Generate new ldapApplicationUser password");
                    final Map.Entry<String, Boolean> ecimUserManagerResult = ecimUserManager.provideEcimSecurePassword().entrySet().iterator()
                            .next();
                    ldapApplicationUserPassword = ecimUserManagerResult.getKey();
                    propagate = ecimUserManagerResult.getValue();

                    logger.info("PASSWORD To Propagate: " + propagate);

                    command.getProperties().put(CredentialsCommand.LDAP_APPLICATION_USER_NAME_PROPERTY, ecimUserManager.getUsername());
                    command.getProperties().put(CredentialsCommand.LDAP_APPLICATION_USER_PASSWORD_PROPERTY, ldapApplicationUserPassword);
                    break;
                }
            }
        }
        updateMOCredentialsAttributes(command, context);
        if (!ldapuserValue) {
            removeLdapApplicationUserFromMO(command, context);
        }

        if (propagate) {
            ldapApplicationUserManager.propagateLdapApplicationUserPassword(ldapApplicationUserPassword);
        }

        logger.debug("Credentials succesfully updated");

        nscsContextService.updateItemsResultStatsForSyncCommand(Integer.valueOf(context.getValidNodes().size()), Integer.valueOf(0));

        return NscsCommandResponse.message(ALL_CREDENTIALS_UPDATED_SUCCESSFULLY);
    }

    private void updateMOCredentialsAttributes(final CredentialsCommand command, final CommandContext context) throws NscsServiceException {

        try {
            //we need to distinguish if node supports ldapApplicationUser or not building two different specifications
            final WriterSpecificationBuilder specificationWithLdap = writer.withSpecification();
            final WriterSpecificationBuilder specificationWithoutLdap = writer.withSpecification();
            buildSpecifications(command, specificationWithLdap, specificationWithoutLdap);

            for (final NormalizableNodeReference node : context.getValidNodes()) {
                logger.debug("Updating NetworkElementSecurity for {}", node);
                final NodeReference nodeRef = node.getNormalizedRef();
                final String nodeFdn = nodeRef.getFdn();
                final NormalizedRootMO normal = Model.getNomalizedRootMO(nodeFdn);
                //calling updateMO with relevant specifications depending on ldapApplicationUser supported by the node
                if (nscsCapabilityModelService.isLdapCommonUserSupported(node)) {
                    specificationWithLdap.setFdn(normal.securityFunction.networkElementSecurity.withNames(nodeRef.getName()).fdn());
                    specificationWithLdap.updateMO();
                } else {
                    specificationWithoutLdap.setFdn(normal.securityFunction.networkElementSecurity.withNames(nodeRef.getName()).fdn());
                    specificationWithoutLdap.updateMO();
                }
                logger.debug("Updated NetworkElementSecurity for {}", nodeRef);
            }

        } catch (final Exception e) {
            logger.error("Could not update MO attribute!", e);
            throw new UnexpectedErrorException(e);
        }
    }

    private void buildSpecifications(final CredentialsCommand command, final WriterSpecificationBuilder specificationWithLdap,
                                     final WriterSpecificationBuilder specificationWithoutLdap) {

        final Iterator<Entry<String, String>> iterator = mandatoryAttributes.entrySet().iterator();
        while (iterator.hasNext()) {

            final Entry<String, String> attribute = iterator.next();
            final String attributeCli = attribute.getKey();
            final String attributeDb = attribute.getValue();
            final String attributeCliValue = command.getValueString(attributeCli);

            if (!(attributeCliValue == null || attributeCliValue.isEmpty())) {
                setSpecificationsAttribute(specificationWithLdap, specificationWithoutLdap, attributeCli, attributeDb, attributeCliValue);
            }
        }
    }

    private void setSpecificationsAttribute(final WriterSpecificationBuilder specificationWithLdap,
                                            final WriterSpecificationBuilder specificationWithoutLdap, final String attributeCli,
                                            final String attributeDb, final String attributeCliValue) {
        final String passWord = "password";
        if (attributeCli.toLowerCase().contains(passWord)) {
            specificationWithLdap.setAttribute(attributeDb, passwordHelper.encryptEncode(attributeCliValue));
            if (!NetworkElementSecurity.LDAP_APPLICATION_USER_PASSWORD.equals(attributeDb)) {
                specificationWithoutLdap.setAttribute(attributeDb, passwordHelper.encryptEncode(attributeCliValue));
            }
        } else {
            specificationWithLdap.setAttribute(attributeDb, attributeCliValue);
            if (!NetworkElementSecurity.LDAP_APPLICATION_USER_NAME.equals(attributeDb)) {
                specificationWithoutLdap.setAttribute(attributeDb, attributeCliValue);
            }
        }
    }

    private void removeLdapApplicationUserFromMO(final CredentialsCommand command, final CommandContext context) throws NscsServiceException {

        try {
            final WriterSpecificationBuilder specification = writer.withSpecification();

            logger.info("ATTRIBUTES = " + mandatoryAttributes);
            final Iterator<Entry<String, String>> iterator = mandatoryAttributes.entrySet().iterator();

            while (iterator.hasNext()) {

                final Entry<String, String> attribute = iterator.next();
                final String attributeCli = attribute.getKey();
                final String attributeDb = attribute.getValue();
                final String attributeCliValue = "";

                if (attributeCli.equals(CredentialsCommand.LDAP_APPLICATION_USER_NAME_PROPERTY)
                        || attributeCli.equals(CredentialsCommand.LDAP_APPLICATION_USER_PASSWORD_PROPERTY)) {
                    specification.setAttribute(attributeDb, attributeCliValue);
                }
            }

            for (final NodeReference node : context.toNormalizedRef(context.getValidNodes())) {
                logger.debug("Updating NetworkElementSecurity for {}", node);
                final String nodeFdn = node.getFdn();
                final NormalizedRootMO normal = Model.getNomalizedRootMO(nodeFdn);
                specification.setFdn(normal.securityFunction.networkElementSecurity.withNames(node.getName()).fdn());
                specification.updateMO();
                logger.debug("Updated NetworkElementSecurity for {}", node);
            }

        } catch (final Exception e) {
            logger.error("Could not update MO attribute!", e);
            throw new UnexpectedErrorException(e);
        }
    }

    private void initMapMandatoryAttributes(final CredentialsCommand command) {

        logger.debug("Init mandatory map update");

        mandatoryAttributes.put(CredentialsCommand.ROOT_USER_NAME_PROPERTY, NetworkElementSecurity.ROOT_USER_NAME);
        mandatoryAttributes.put(CredentialsCommand.ROOT_USER_PASSWORD_PROPERTY, NetworkElementSecurity.ROOT_USER_PASSWORD);
        mandatoryAttributes.put(CredentialsCommand.NORMAL_USER_NAME_PROPERTY, NetworkElementSecurity.NORMAL_USER_NAME);
        mandatoryAttributes.put(CredentialsCommand.NORMAL_USER_PASSWORD_PROPERTY, NetworkElementSecurity.NORMAL_USER_PASSWORD);
        mandatoryAttributes.put(CredentialsCommand.NWIEA_SECURE_USER_NAME_PROPERTY, NetworkElementSecurity.NWIEA_SECURE_USER_NAME);
        mandatoryAttributes.put(CredentialsCommand.NWIEA_SECURE_PASSWORD_PROPERTY, NetworkElementSecurity.NWIEA_SECURE_USER_PASSWORD);
        mandatoryAttributes.put(CredentialsCommand.NWIEB_SECURE_USER_NAME_PROPERTY, NetworkElementSecurity.NWIEB_SECURE_USER_NAME);
        mandatoryAttributes.put(CredentialsCommand.NWIEB_SECURE_PASSWORD_PROPERTY, NetworkElementSecurity.NWIEB_SECURE_USER_PASSWORD);
        mandatoryAttributes.put(CredentialsCommand.SECURE_USER_NAME_PROPERTY, NetworkElementSecurity.SECURE_USER_NAME);
        mandatoryAttributes.put(CredentialsCommand.SECURE_USER_PASSWORD_PROPERTY, NetworkElementSecurity.SECURE_USER_PASSWORD);
        mandatoryAttributes.put(CredentialsCommand.LDAP_APPLICATION_USER_NAME_PROPERTY, NetworkElementSecurity.LDAP_APPLICATION_USER_NAME);
        mandatoryAttributes.put(CredentialsCommand.LDAP_APPLICATION_USER_PASSWORD_PROPERTY, NetworkElementSecurity.LDAP_APPLICATION_USER_PASSWORD);
        mandatoryAttributes.put(CredentialsCommand.NODECLI_USER_NAME_PROPERTY, NetworkElementSecurity.NODECLI_USER_NAME);
        mandatoryAttributes.put(CredentialsCommand.NODECLI_USER_PASSPHRASE_PROPERTY, NetworkElementSecurity.NODECLI_USER_PASSPHRASE);
    }

}
