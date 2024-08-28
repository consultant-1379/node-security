package com.ericsson.nms.security.nscs.handler.command.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.handler.command.CommandHandlerInterface;
import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.NscsNameMultipleValueCommandResponse;
import com.ericsson.nms.security.nscs.api.command.types.GetCredentialsCommand;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.data.MoObject;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.ModelDefinition.NetworkElementSecurity;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.command.CommandHandler;
import com.ericsson.nms.security.nscs.handler.command.CommandType;
import com.ericsson.nms.security.nscs.handler.command.utility.PasswordHelper;
import com.ericsson.nms.security.nscs.handler.validation.UseValidator;
import com.ericsson.nms.security.nscs.handler.validation.impl.CheckPlainTextValidator;
import com.ericsson.nms.security.nscs.handler.validation.impl.CheckUserTypeValidator;
import com.ericsson.nms.security.nscs.handler.validation.impl.GetCredentialsMandatoryParamsValidator;
import com.ericsson.nms.security.nscs.handler.validation.impl.NoDuplNodeNamesAllowedValidator;
import com.ericsson.nms.security.nscs.handler.validation.impl.NodeMustBeNormalizableValidator;
import com.ericsson.nms.security.nscs.handler.validation.impl.NodeMustHaveNetworkElementSecurityMoValidator;
import com.ericsson.nms.security.nscs.handler.validation.impl.NodeMustHaveSecurityFunctionMoValidator;
import com.ericsson.nms.security.nscs.handler.validation.impl.NormalizedNodeMustExistValidator;
import com.ericsson.nms.security.nscs.handler.validation.impl.StarIsNotAllowedValidator;

@UseValidator({ NoDuplNodeNamesAllowedValidator.class, StarIsNotAllowedValidator.class, NormalizedNodeMustExistValidator.class,
        NodeMustBeNormalizableValidator.class, GetCredentialsMandatoryParamsValidator.class, CheckUserTypeValidator.class,
        CheckPlainTextValidator.class, NodeMustHaveSecurityFunctionMoValidator.class, NodeMustHaveNetworkElementSecurityMoValidator.class })
@CommandType(NscsCommandType.GET_CREDENTIALS)
@Local(CommandHandlerInterface.class)
public class GetCredentialsHandler implements CommandHandler<GetCredentialsCommand>, CommandHandlerInterface {

    public static final String HEADER_NODE = "Node";
    public static final String HEADER_USERNAME = "User Name";
    public static final String HEADER_USERPASSWORD = "User Password";
    public static final String NOT_APPLICABLE = "N/A";
    public static final String NOT_CONFIGURED = "Not Configured";
    public static final String PASSWORD_HIDE = "***********";

    protected static final Map<String, String[]> cliToDpsMap = new HashMap<>();
    static {
        cliToDpsMap.put(GetCredentialsCommand.ROOT_USER_NAME_PROPERTY,
                new String[] { NetworkElementSecurity.ROOT_USER_NAME, NetworkElementSecurity.ROOT_USER_PASSWORD });
        cliToDpsMap.put(GetCredentialsCommand.NORMAL_USER_NAME_PROPERTY,
                new String[] { NetworkElementSecurity.NORMAL_USER_NAME, NetworkElementSecurity.NORMAL_USER_PASSWORD });
        cliToDpsMap.put(GetCredentialsCommand.SECURE_USER_NAME_PROPERTY,
                new String[] { NetworkElementSecurity.SECURE_USER_NAME, NetworkElementSecurity.SECURE_USER_PASSWORD });
        cliToDpsMap.put(GetCredentialsCommand.NWIEA_SECURE_USER_NAME_PROPERTY,
                new String[] { NetworkElementSecurity.NWIEA_SECURE_USER_NAME, NetworkElementSecurity.NWIEA_SECURE_USER_PASSWORD });
        cliToDpsMap.put(GetCredentialsCommand.NWIEB_SECURE_USER_NAME_PROPERTY,
                new String[] { NetworkElementSecurity.NWIEB_SECURE_USER_NAME, NetworkElementSecurity.NWIEB_SECURE_USER_PASSWORD });
        cliToDpsMap.put(GetCredentialsCommand.NODECLI_USER_NAME_PROPERTY,
                new String[] { NetworkElementSecurity.NODECLI_USER_NAME, NetworkElementSecurity.NODECLI_USER_PASSPHRASE });
    }

    @Inject
    private Logger logger;

    @Inject
    private NscsCMReaderService reader;

    @Inject
    private PasswordHelper passwordHelper;

    @Inject
    private NscsCapabilityModelService nscsCapabilityModelService;

    /**
     * @param command
     *            CredentialsCommand instance
     * @param context
     *            a CommandContext instance
     * @return NscsMessageCommandResponse in case of success
     * Note: nodeCLI user credentials are expected for all the nodes so there is no check.
     */
    @Override
    public NscsCommandResponse process(final GetCredentialsCommand command, final CommandContext context) throws NscsServiceException {
        logger.info("Starting the process of creating NetworkElementSecurity MO for nodes");
        final NscsNameMultipleValueCommandResponse response = NscsNameMultipleValueCommandResponse.nameMultipleValue(2);

        response.add(HEADER_NODE, new String[] { HEADER_USERNAME, HEADER_USERPASSWORD });
        for (final NormalizableNodeReference node : context.getValidNodes()) {

            final String nodoFdn = Model.getNomalizedRootMO(node.getNormalizedRef().getFdn()).securityFunction.networkElementSecurity.withNames(node.getNormalizedRef().getName()).fdn();
            logger.info("Get credentials on node: " + nodoFdn);
            final MoObject moObject = reader.getMoObjectByFdn(nodoFdn);
            logger.info("MoObject: " + moObject);
            final Map<String, Object> properties = command.getProperties();
            final ArrayList<String> usertypeAction = new ArrayList<>();

            final List<String> expectedParams = nscsCapabilityModelService.getExpectedCredentialsParams(node);

            if (properties.containsKey(GetCredentialsCommand.USER_TYPE_PROPERTY)) {
                usertypeAction.add((String) properties.get(GetCredentialsCommand.USER_TYPE_PROPERTY));
            } else {
                if (isSupportedUser(NetworkElementSecurity.ROOT_USER_NAME, NetworkElementSecurity.ROOT_USER_PASSWORD, expectedParams)) {
                    usertypeAction.add(GetCredentialsCommand.ROOT_USER_NAME_PROPERTY);
                }
                if (isSupportedUser(NetworkElementSecurity.SECURE_USER_NAME, NetworkElementSecurity.SECURE_USER_PASSWORD, expectedParams)) {
                    usertypeAction.add(GetCredentialsCommand.SECURE_USER_NAME_PROPERTY);
                }
                if (isSupportedUser(NetworkElementSecurity.NORMAL_USER_NAME, NetworkElementSecurity.NORMAL_USER_PASSWORD, expectedParams)) {
                    usertypeAction.add(GetCredentialsCommand.NORMAL_USER_NAME_PROPERTY);
                }
                if (isSupportedUser(NetworkElementSecurity.NWIEA_SECURE_USER_NAME, NetworkElementSecurity.NWIEA_SECURE_USER_PASSWORD, expectedParams)) {
                    usertypeAction.add(GetCredentialsCommand.NWIEA_SECURE_USER_NAME_PROPERTY);
                }
                if (isSupportedUser(NetworkElementSecurity.NWIEB_SECURE_USER_NAME, NetworkElementSecurity.NWIEB_SECURE_USER_PASSWORD, expectedParams)) {
                    usertypeAction.add(GetCredentialsCommand.NWIEB_SECURE_USER_NAME_PROPERTY);
                }
                usertypeAction.add(GetCredentialsCommand.NODECLI_USER_NAME_PROPERTY);
            }

            String plaintextAction = GetCredentialsCommand.PLAIN_TEXT_HIDE;
            if (properties.containsKey(GetCredentialsCommand.PLAIN_TEXT_PROPERTY)) {
                plaintextAction = (String) properties.get(GetCredentialsCommand.PLAIN_TEXT_PROPERTY);
            }

            for (final String usertype : usertypeAction) {
                final String[] responseBuild = new String[] { getDisplayedUserName(moObject, usertype, expectedParams), getDisplayedPassword(plaintextAction, moObject, usertype, expectedParams) };
                response.add(node.getName(), responseBuild);
            }
        }

        logger.debug("GET Credentials succesfully executed");
        return response;

    }

    private boolean isSupportedUser(final String userName, final String userPassword, final List<String> expectedParams) {

        if (containsCaseInsensitive(userName, expectedParams) && containsCaseInsensitive(userPassword, expectedParams)) {
            return true;
        }
        return false;
    }

    public boolean containsCaseInsensitive(final String s, final List<String> l) {
        for (final String string : l) {
            if (string.equalsIgnoreCase(s)) {
                return true;
            }
        }
        return false;
    }

    private String getDisplayedUserName(final MoObject moObject, final String usertype, final List<String> expectedParams) {
        final String userNameLabel = cliToDpsMap.get(usertype)[0];
        String userNameValue = (String) moObject.getAttribute(userNameLabel);
        if (userNameValue == null) {
            if (containsCaseInsensitive(userNameLabel, expectedParams) || (NetworkElementSecurity.NODECLI_USER_NAME.equalsIgnoreCase(userNameLabel))) {
                userNameValue = NOT_CONFIGURED;
            } else {
                userNameValue = NOT_APPLICABLE;
            }
        }
        final StringBuilder userNameDisplay = new StringBuilder();
        userNameDisplay.append(userNameLabel).append(":").append(userNameValue);
        return userNameDisplay.toString();
    }

    private String getDisplayedPassword(final String plaintext, final MoObject moObject, final String usertype, final List<String> expectedParams) {
        final String userPasswordLabel = cliToDpsMap.get(usertype)[1];
        String userPasswordValue = (String) moObject.getAttribute(userPasswordLabel);

        if (userPasswordValue == null) {
            if (containsCaseInsensitive(userPasswordLabel, expectedParams) || (NetworkElementSecurity.NODECLI_USER_PASSPHRASE.equalsIgnoreCase(userPasswordLabel))) {
                userPasswordValue = NOT_CONFIGURED;
            } else {
                userPasswordValue = NOT_APPLICABLE;
            }
        } else if (plaintext.equals(GetCredentialsCommand.PLAIN_TEXT_SHOW)) {
            userPasswordValue = passwordHelper.decryptDecode(userPasswordValue);
        } else {
            userPasswordValue = PASSWORD_HIDE;
        }

        final StringBuilder userPasswordDisplay = new StringBuilder();
        userPasswordDisplay.append(userPasswordLabel).append(":").append(userPasswordValue);
        return userPasswordDisplay.toString();
    }
}
