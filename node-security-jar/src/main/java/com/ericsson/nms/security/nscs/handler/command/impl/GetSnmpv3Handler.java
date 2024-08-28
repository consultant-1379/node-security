/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.handler.command.impl;

import java.util.List;
import java.util.Map;

import javax.ejb.Local;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.NscsNameMultipleValueCommandResponse;
import com.ericsson.nms.security.nscs.api.command.types.GetSnmpCommand;
import com.ericsson.nms.security.nscs.api.exception.NscsCapabilityModelException;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.data.MoObject;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.ModelDefinition;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.command.CommandHandler;
import com.ericsson.nms.security.nscs.handler.command.CommandHandlerInterface;
import com.ericsson.nms.security.nscs.handler.command.CommandType;
import com.ericsson.nms.security.nscs.handler.command.utility.PasswordHelper;
import com.ericsson.nms.security.nscs.handler.validation.UseValidator;
import com.ericsson.nms.security.nscs.handler.validation.impl.CheckPlainTextValidator;
import com.ericsson.nms.security.nscs.handler.validation.impl.GetSNMPMandatoryParamsValidator;
import com.ericsson.nms.security.nscs.handler.validation.impl.NoDuplNodeNamesAllowedValidator;
import com.ericsson.nms.security.nscs.handler.validation.impl.NodeMustBeNormalizableValidator;
import com.ericsson.nms.security.nscs.handler.validation.impl.NodeMustHaveNetworkElementSecurityMoValidator;
import com.ericsson.nms.security.nscs.handler.validation.impl.NormalizedNodeMustExistValidator;
import com.ericsson.nms.security.nscs.handler.validation.impl.StarIsNotAllowedValidator;
import com.ericsson.oss.services.security.nscs.command.util.SnmpCommandHelper;

/**
 * <p>
 * Get SNMP Auth Password, Priv Password for the provided list of nodes.
 * </p>
 *
 * @author DespicableUs
 */
@CommandType(NscsCommandType.GET_SNMP)
@Local(CommandHandlerInterface.class)
@UseValidator({ NoDuplNodeNamesAllowedValidator.class, StarIsNotAllowedValidator.class, NormalizedNodeMustExistValidator.class,
        NodeMustBeNormalizableValidator.class, NodeMustHaveNetworkElementSecurityMoValidator.class, GetSNMPMandatoryParamsValidator.class,
        CheckPlainTextValidator.class })
public class GetSnmpv3Handler implements CommandHandler<GetSnmpCommand>, CommandHandlerInterface {

    public static final String HEADER_NODE = "Node";
    public static final String HEADER_AUTHPASSWORD = "Auth Password";
    public static final String HEADER_PRIVPASSWORD = "Priv Password";
    public static final String HEADER_AUTH_ALGO = "Auth Algo";
    public static final String HEADER_PRIV_ALGO = "Priv Algo";
    public static final String EMPTY = "-";
    public static final String HIDE_VALUE = "***********";

    @Inject
    private Logger logger;

    @Inject
    PasswordHelper passwordHelper;

    @Inject
    private NscsCMReaderService reader;

    @Inject
    private NscsCapabilityModelService nscsCapabilityModelService;

    @Override
    public NscsCommandResponse process(final GetSnmpCommand command, final CommandContext context) throws NscsServiceException {
        logger.info("Executing Get Snmp Authpriv command");
        final NscsNameMultipleValueCommandResponse response = NscsNameMultipleValueCommandResponse.nameMultipleValue(4);

        response.add(HEADER_NODE, new String[] { HEADER_AUTH_ALGO, HEADER_AUTHPASSWORD, HEADER_PRIV_ALGO, HEADER_PRIVPASSWORD });
        for (final NormalizableNodeReference node : context.getValidNodes()) {

            final String nodoFdn = Model.getNomalizedRootMO(node.getNormalizedRef().getFdn()).securityFunction.networkElementSecurity
                    .withNames(node.getNormalizedRef().getName()).fdn();
            logger.info("Get Snmp Authpriv on node: " + nodoFdn);
            final MoObject moObject = reader.getMoObjectByFdn(nodoFdn);
            logger.info("MoObject: " + moObject);
            final Map<String, Object> properties = command.getProperties();

            if (nscsCapabilityModelService.isCliCommandSupported(node, NscsCapabilityModelService.SNMP_COMMAND)) {
                final List<String> expectedParams = SnmpCommandHelper.getExpectedSnmpGetAuthParams();

                String plaintextAction = GetSnmpCommand.PLAIN_TEXT_HIDE;
                if (properties.containsKey(GetSnmpCommand.PLAIN_TEXT_PROPERTY)) {
                    plaintextAction = (String) properties.get(GetSnmpCommand.PLAIN_TEXT_PROPERTY);
                }

                final String authPassword = (String) moObject.getAttribute(ModelDefinition.NetworkElementSecurity.AUTH_KEY);
                final String authPrivPassword = (String) moObject.getAttribute(ModelDefinition.NetworkElementSecurity.PRIV_KEY);
                final String authAlgo = (String) moObject.getAttribute(ModelDefinition.NetworkElementSecurity.AUTH_PROTOCOL);
                final String privAlgo = (String) moObject.getAttribute(ModelDefinition.NetworkElementSecurity.PRIV_PROTOCOL);

                if (isSupportedSNMPPasswd(ModelDefinition.NetworkElementSecurity.AUTH_KEY, ModelDefinition.NetworkElementSecurity.PRIV_KEY,
                        expectedParams)) {

                    final String[] responseBuild = new String[] { authAlgo, getDisplayedPassword(plaintextAction, authPassword, expectedParams),
                            privAlgo, getDisplayedPassword(plaintextAction, authPrivPassword, expectedParams) };
                    response.add(node.getName(), responseBuild);

                }

            } else {
                final String errorMsg = String.format("unsupported command for node %s", node);
                logger.error("getSnmpv3Handler process: {}", errorMsg);
                throw new NscsCapabilityModelException(errorMsg);
            }
        }
        return response;
    }

    private String getDisplayedPassword(final String snmpv3plainText, String authPassword, final List<String> expectedParams) {
        if (authPassword == null) {
            authPassword = EMPTY;
        } else if (snmpv3plainText.equals(GetSnmpCommand.PLAIN_TEXT_SHOW)) {
            authPassword = passwordHelper.decryptDecode(authPassword);
        } else {
            authPassword = HIDE_VALUE;
        }
        return authPassword;
    }

    private boolean isSupportedSNMPPasswd(final String authPasswd, final String privPasswd, final List<String> expectedParams) {

        if (containsCaseInsensitive(authPasswd, expectedParams) && containsCaseInsensitive(privPasswd, expectedParams)) {
            return true;
        }
        return false;
    }

    /**
     * @param s
     *            string to search
     * @param l
     *            list of strings
     * @return boolean value
     */
    private boolean containsCaseInsensitive(final String s, final List<String> l) {
        for (final String string : l) {
            if (string.equalsIgnoreCase(s)) {
                return true;
            }
        }
        return false;
    }
}
