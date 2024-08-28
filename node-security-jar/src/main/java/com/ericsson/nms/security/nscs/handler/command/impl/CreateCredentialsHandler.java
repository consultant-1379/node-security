package com.ericsson.nms.security.nscs.handler.command.impl;

import static com.ericsson.nms.security.nscs.data.Model.NETWORK_ELEMENT;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.types.CredentialsCommand;
import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.exception.UnexpectedErrorException;
import com.ericsson.nms.security.nscs.api.ldap.LdapApplicationUserManager;
import com.ericsson.nms.security.nscs.api.model.service.NscsModelInfo;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.ModelDefinition.NetworkElementSecurity;
import com.ericsson.nms.security.nscs.data.NscsCMWriterService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.gim.EcimUserManager;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.command.CommandHandler;
import com.ericsson.nms.security.nscs.handler.command.CommandHandlerInterface;
import com.ericsson.nms.security.nscs.handler.command.CommandType;
import com.ericsson.nms.security.nscs.handler.command.utility.PasswordHelper;
import com.ericsson.nms.security.nscs.handler.validation.UseValidator;
import com.ericsson.nms.security.nscs.handler.validation.impl.CheckLdapUserOptionValidator;
import com.ericsson.nms.security.nscs.handler.validation.impl.CredMandatoryParamsValidator;
import com.ericsson.nms.security.nscs.handler.validation.impl.NoDuplNodeNamesAllowedValidator;
import com.ericsson.nms.security.nscs.handler.validation.impl.NodeHasNoNetworkElementSecurityMoValidator;
import com.ericsson.nms.security.nscs.handler.validation.impl.NodeMustBeNormalizableValidator;
import com.ericsson.nms.security.nscs.handler.validation.impl.NodeMustHaveSecurityFunctionMoValidator;
import com.ericsson.nms.security.nscs.handler.validation.impl.NormalizedNodeMustExistValidator;
import com.ericsson.nms.security.nscs.handler.validation.impl.StarIsNotAllowedValidator;
import com.ericsson.nms.security.nscs.model.service.NscsModelServiceImpl;
import com.ericsson.nms.security.nscs.utilities.CredentialsHelper;
import com.ericsson.oss.services.security.nscs.context.NscsContextService;

/**
 * <p>
 * Creates a NetworkElementSecurity Mo associated to each of the specified nodes.
 * </p>
 * Created by emaynes on 02/05/2014.
 */
@UseValidator({ NoDuplNodeNamesAllowedValidator.class, StarIsNotAllowedValidator.class, NormalizedNodeMustExistValidator.class,
        NodeMustBeNormalizableValidator.class, CredMandatoryParamsValidator.class, NodeMustHaveSecurityFunctionMoValidator.class,
        NodeHasNoNetworkElementSecurityMoValidator.class, CheckLdapUserOptionValidator.class })
@CommandType(NscsCommandType.CREATE_CREDENTIALS)
@Local(CommandHandlerInterface.class)
public class CreateCredentialsHandler implements CommandHandler<CredentialsCommand>, CommandHandlerInterface {

    public static final String ALL_CREDENTIALS_WERE_CREATED_SUCCESSFULLY = "All credentials were created successfully";

    @Inject
    private Logger logger;

    @Inject
    private NscsCMWriterService writer;

    @Inject
    private PasswordHelper passwordHelper;

    @Inject
    private NscsModelServiceImpl nscsModelServiceImpl;

    @Inject
    private NscsCapabilityModelService nscsCapabilityModelService;

    @Inject
    private EcimUserManager ecimUserManager;

    @EJB
    LdapApplicationUserManager ldapApplicationUserManager;

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
        logger.info("Starting the process of creating NetworkElementSecurity MO for nodes");

        // TODO get this from IdentityManagementService API when it's available
        final List<String> targetGroups = new LinkedList<>();
        targetGroups.add("defaultTargetGroup");

        final String networkElementSecurityType = NETWORK_ELEMENT.securityFunction.networkElementSecurity.type();
        final NscsModelInfo nscsModelInfo = nscsModelServiceImpl.getLatestVersionOfNormalizedModel(networkElementSecurityType);
        final String networkElementSecurityNamespace = nscsModelInfo.getNamespace();
        final String networkElementSecurityVersion = nscsModelInfo.getVersion();
        logger.info("Preparing WriterSpec for " + networkElementSecurityType + " MO with ns [" + networkElementSecurityNamespace + "] and version ["
                + networkElementSecurityVersion + "]");
        final NscsCMWriterService.WriterSpecificationBuilder specification = writer.withSpecification(networkElementSecurityType,
                networkElementSecurityNamespace, networkElementSecurityVersion);

        logger.debug("Create MO for nodes");

        boolean isDone = false;
        boolean propagate = false;
        String ldapApplicationUserPassword = null;
        final boolean ldapuserValue = "disable".equalsIgnoreCase(command.getLdapUserEnable()) ? false : true;

        nscsContextService.initItemsStatsForSyncCommand(Integer.valueOf(context.getValidNodes().size()), Integer.valueOf(0));

        for (final NormalizableNodeReference node : context.getValidNodes()) {

            if (ldapApplicationUserPassword == null) {
                logger.debug("Create Credentials Handler Process: node[" + node + "]");
                if (nscsCapabilityModelService.isLdapCommonUserSupported(node) && ldapuserValue) {
                    logger.info("Retrieve or Generate new ldapApplicationUser password");
                    final Map.Entry<String, Boolean> ecimUserManagerResult = ecimUserManager.provideEcimSecurePassword().entrySet().iterator()
                            .next();
                    ldapApplicationUserPassword = ecimUserManagerResult.getKey();
                    propagate = ecimUserManagerResult.getValue();
                    logger.info("PASSWORD To Propagate: " + propagate);

                    command.getProperties().put(CredentialsCommand.LDAP_APPLICATION_USER_NAME_PROPERTY, ecimUserManager.getUsername());
                    command.getProperties().put(CredentialsCommand.LDAP_APPLICATION_USER_PASSWORD_PROPERTY, ldapApplicationUserPassword);
                }

                logger.debug("Reading default value for EnrollmentMode from Capability Model for node [{}]", node.getFdn());
                final String enrollmentModeStr = nscsCapabilityModelService.getDefaultEnrollmentMode(node);
                final EnrollmentMode enrollmentMode = EnrollmentMode.valueOf(enrollmentModeStr);
                logger.info("Default Enrollment Mode for node [{}] is [{}]", node.getFdn(), enrollmentMode.name());

                isDone = writeMOCredentialsAttributes(command, specification, targetGroups, enrollmentMode, node);
            }

            if (isDone) {
                logger.debug("Creating  NetworkElementSecurity for Node : {}", node);
                specification.setParent(Model.getNomalizedRootMO(node.getNormalizedRef().getFdn()).securityFunction
                        .withNames(node.getNormalizedRef().getName()).fdn());
                specification.createMIBRoot();
            }
        }

        if (propagate) {
            ldapApplicationUserManager.propagateLdapApplicationUserPassword(ldapApplicationUserPassword);
        }
        logger.debug("Credentials succesfully created");

        nscsContextService.updateItemsResultStatsForSyncCommand(Integer.valueOf(context.getValidNodes().size()), Integer.valueOf(0));

        return NscsCommandResponse.message(ALL_CREDENTIALS_WERE_CREATED_SUCCESSFULLY);
    }

    private boolean writeMOCredentialsAttributes(final CredentialsCommand command, final NscsCMWriterService.WriterSpecificationBuilder specification,
                                                 final List<String> targetGroups, final EnrollmentMode enrollmentMode, final NormalizableNodeReference normNodeRef)
                                                         throws NscsServiceException {

        boolean isDone = false;
        String attributeDbValue = null;
        try {
            /**
             * Starting from 18.02 some attributes (according to node type) are mandatory, so they are initialized to a dummy value if not specified
             * by operator
             */
            final List<String> expectedParams = nscsCapabilityModelService.getExpectedCredentialsParams(normNodeRef);
            for (final String param : expectedParams) {

                attributeDbValue = CredentialsHelper.isPasswordParam(param)
                        ? passwordHelper.encryptEncode(CredentialsHelper.UNDEFINED_CREDENTIALS) : CredentialsHelper.UNDEFINED_CREDENTIALS;
                        specification.setAttribute(CredentialsHelper.toAttribute(param), attributeDbValue);
            }

            final Iterator<Entry<String, String>> iterator = CredentialsHelper.entrySet().iterator();
            while (iterator.hasNext()) {

                final Entry<String, String> attribute = iterator.next();
                final String attributeCli = attribute.getKey();
                final String attributeDb = attribute.getValue();
                final String attributeCliValue = command.getValueString(attributeCli);
                if (!(attributeCliValue == null || attributeCliValue.isEmpty())) {
                    if (CredentialsHelper.isPasswordParam(attributeCli)) {
                        specification.setAttribute(attributeDb, passwordHelper.encryptEncode(attributeCliValue));
                    } else {
                        specification.setAttribute(attributeDb, attributeCliValue);
                    }
                }
            }

            specification.setAttribute(NetworkElementSecurity.NETWORK_ELEMENT_SECURITY_ID, "1");
            specification.setAttribute(NetworkElementSecurity.TARGET_GROUPS, targetGroups);
            specification.setAttribute(NetworkElementSecurity.ENROLLMENT_MODE, enrollmentMode.name());

            isDone = true;

        } catch (final Exception e) {
            logger.error("Could not define MO attribute! Could be some problem in the cryptographyService, exception : {}", e);
            throw new UnexpectedErrorException(e);
        }

        return isDone;
    }

}
