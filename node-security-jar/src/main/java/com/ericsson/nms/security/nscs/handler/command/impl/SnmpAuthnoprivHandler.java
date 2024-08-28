/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2015
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.handler.command.impl;

import java.nio.charset.StandardCharsets;

import javax.ejb.Local;
import javax.inject.Inject;
import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.types.SnmpAuthnopriv;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.exception.UnexpectedErrorException;
import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;
import com.ericsson.nms.security.nscs.data.Model;
import com.ericsson.nms.security.nscs.data.ModelDefinition;
import com.ericsson.nms.security.nscs.data.NscsCMWriterService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.command.CommandHandler;
import com.ericsson.nms.security.nscs.handler.command.CommandHandlerInterface;
import com.ericsson.nms.security.nscs.handler.command.CommandType;
import com.ericsson.nms.security.nscs.handler.validation.UseValidator;
import com.ericsson.nms.security.nscs.handler.validation.impl.NoDuplNodeNamesAllowedValidator;
import com.ericsson.nms.security.nscs.handler.validation.impl.NodeMustBeNormalizableValidator;
import com.ericsson.nms.security.nscs.handler.validation.impl.NodeMustHaveNetworkElementSecurityMoValidator;
import com.ericsson.nms.security.nscs.handler.validation.impl.NormalizedNodeMustExistValidator;
import com.ericsson.nms.security.nscs.handler.validation.impl.SnmpAuthnoprivParamsValidator;
import com.ericsson.nms.security.nscs.handler.validation.impl.StarIsNotAllowedValidator;
import com.ericsson.oss.itpf.security.cryptography.CryptographyService;
import com.ericsson.oss.services.security.nscs.context.NscsContextService;

/**
 * <p>
 * Modify Auth Protocol and Auth Key for the provided list of nodes.
 * </p>
 *
 * @author ebarmos, emelant
 */
@CommandType(NscsCommandType.SNMP_AUTHNOPRIV)
@Local(CommandHandlerInterface.class)
@UseValidator({ NoDuplNodeNamesAllowedValidator.class, StarIsNotAllowedValidator.class, NormalizedNodeMustExistValidator.class,
        NodeMustBeNormalizableValidator.class, NodeMustHaveNetworkElementSecurityMoValidator.class, SnmpAuthnoprivParamsValidator.class })
public class SnmpAuthnoprivHandler implements CommandHandler<SnmpAuthnopriv>, CommandHandlerInterface {

    public static final String SNMP_AUTHNOPRIV_COMMAND_OK = "Snmp Authnopriv Command OK.";

    @Inject
    private Logger logger;

    @Inject
    private NscsCMWriterService writer;

    @Inject
    private CryptographyService cryptographyService;

    @Inject
    private NscsCapabilityModelService nscsCapabilityModelService;

    @Inject
    private NscsContextService nscsContextService;

    @Override
    public NscsCommandResponse process(final SnmpAuthnopriv command, final CommandContext context) throws NscsServiceException {

        logger.info("Executing Snmp Authpriv change passwords command");

        final NscsCMWriterService.WriterSpecificationBuilder specification = writer.withSpecification();

        final int total = context.getValidNodes().size();
        int valid = 0;

        for (final NormalizableNodeReference node : context.getValidNodes()) {
            final boolean isValidCommand = nscsCapabilityModelService.isCliCommandSupported(node, NscsCapabilityModelService.SNMP_COMMAND);
            if (isValidCommand) {
                try {
                    specification.setNotNullAttribute(ModelDefinition.NetworkElementSecurity.AUTH_PROTOCOL, command.getAuthAlgo());
                    specification.setNotNullAttribute(ModelDefinition.NetworkElementSecurity.AUTH_KEY, this.encryptEncode(command.getAuthPwd()));
                } catch (final Exception e) {
                    logger.error("Could not replace MO attribute! Could be some problem in the cryptographyService, exception : {}", e);
                    throw new UnexpectedErrorException(e);
                }
                logger.debug("Updating NetworkElementSecurity for {}", node);
                specification.setFdn(Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.withNames(node.getName()).fdn());
                specification.updateMO();
                logger.debug("Updated NetworkElementSecurity for {}", node);
                valid++;
            }
        }

        logger.debug("Snmp Auth Protocol and Auth Key succesfully modified");

        nscsContextService.updateItemsStatsForSyncCommand(Integer.valueOf(valid), Integer.valueOf(total - valid), Integer.valueOf(valid), Integer.valueOf(0));

        return NscsCommandResponse.message(SNMP_AUTHNOPRIV_COMMAND_OK);
    }

    private static String encode(final byte[] bytes) {
        return DatatypeConverter.printBase64Binary(bytes);
    }

    private byte[] encrypt(final String text) {
        return cryptographyService.encrypt(text.getBytes(StandardCharsets.UTF_8));
    }

    public String encryptEncode(final String text) {
        if (text == null) {
            return null;
        }
        return encode(encrypt(text));
    }

}
