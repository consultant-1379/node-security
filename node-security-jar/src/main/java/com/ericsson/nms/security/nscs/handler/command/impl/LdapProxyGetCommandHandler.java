/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2022
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse;
import com.ericsson.nms.security.nscs.api.command.NscsCommandType;
import com.ericsson.nms.security.nscs.api.command.types.LdapProxyGetCommand;
import com.ericsson.nms.security.nscs.api.exception.NscsLdapProxyException;
import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.enrollmentinfo.utility.FileConstants;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.command.CommandHandler;
import com.ericsson.nms.security.nscs.handler.command.CommandHandlerInterface;
import com.ericsson.nms.security.nscs.handler.command.CommandType;
import com.ericsson.nms.security.nscs.handler.command.utility.LdapProxyResponseBuilder;
import com.ericsson.nms.security.nscs.ldap.control.IdentityManagementProxy;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.oss.itpf.security.identitymgmtservices.ProxyAgentAccountGetData;
import com.ericsson.oss.itpf.security.identitymgmtservices.enums.ProxyAgentAccountAdminStatus;
import com.ericsson.oss.services.security.nscs.ldap.proxy.NscsLdapProxyConstants;
import com.ericsson.oss.services.security.nscs.ldap.proxy.NscsLdapProxyHelper;
import com.ericsson.oss.services.security.nscs.ldap.proxy.dto.NscsProxyAccountsData;

/**
 * Command handler of ldap proxy get command.
 */
@CommandType(NscsCommandType.LDAP_PROXY_GET)
@Local(CommandHandlerInterface.class)
public class LdapProxyGetCommandHandler implements CommandHandler<LdapProxyGetCommand>, CommandHandlerInterface {

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private IdentityManagementProxy identityManagementProxy;

    @Inject
    private NscsLdapProxyHelper nscsLdapProxyHelper;

    @Inject
    private LdapProxyResponseBuilder ldapProxyResponseBuilder;

    @Override
    public NscsCommandResponse process(final LdapProxyGetCommand command, final CommandContext context) throws NscsServiceException {

        nscsLogger.commandHandlerStarted(command);
        NscsCommandResponse response;
        try {
            response = buildLdapProxyGetResponse(command);
            nscsLogger.commandHandlerFinishedWithSuccess(command, NscsLdapProxyConstants.LDAP_PROXY_GET_SUCCESS);
        } catch (final NscsServiceException e) {
            final String errorMsg = String.format("%sCommand failed due to %s.", NscsLdapProxyConstants.LDAP_PROXY_GET_FAILURE,
                    NscsLogger.stringifyException(e));
            nscsLogger.error(errorMsg, e);
            nscsLogger.commandHandlerFinishedWithError(command, errorMsg);
            throw e;
        } catch (final Exception e) {
            final String errorMsg = String.format("%sCommand failed due to unexpected %s.", NscsLdapProxyConstants.LDAP_PROXY_GET_FAILURE,
                    NscsLogger.stringifyException(e));
            nscsLogger.error(errorMsg, e);
            nscsLogger.commandHandlerFinishedWithError(command, errorMsg);
            throw new NscsLdapProxyException(errorMsg, e);
        }
        return response;
    }

    /**
     * Builds the response to ldap proxy get command.
     * 
     * @param command
     *            the ldap proxy get command.
     * @return the response to ldap proxy get command.
     */
    private NscsCommandResponse buildLdapProxyGetResponse(final LdapProxyGetCommand command) {

        final StringBuilder filenameBuilder = new StringBuilder(NscsLdapProxyConstants.LDAP_PROXY_GET_BASE_FILENAME);
        final NscsProxyAccountsData nscsProxyAccountData = getProxyAccountsData(command, filenameBuilder);
        final String filename = filenameBuilder.toString();
        return buildLdapProxyGetFileResponse(nscsProxyAccountData, filename);
    }

    /**
     * Builds the file response containing the requested proxy accounts data and of given filename.
     * 
     * @param nscsProxyAccountsData
     *            the requested proxy accounts data.
     * @param filename
     *            the filename.
     * @return the file response.
     */
    private NscsCommandResponse buildLdapProxyGetFileResponse(final NscsProxyAccountsData nscsProxyAccountsData, final String filename) {

        final byte[] fileContents = nscsLdapProxyHelper.getXmlFromNscsProxyAccountsData(nscsProxyAccountsData).getBytes(StandardCharsets.UTF_8);
        final String fileMessage = String.format(NscsLdapProxyConstants.LDAP_PROXY_GET_FILENAME_SIZE_FORMAT, filename, fileContents.length);
        nscsLogger.info("Creating file response for {}", fileMessage);
        final String contentType = FileConstants.XML_CONTENT_TYPE;
        final String fileIdentifier = nscsLdapProxyHelper.createDeletableDownloadFileIdentifier(fileContents, filename, contentType);
        final String message = String.format(NscsLdapProxyConstants.LDAP_PROXY_GET_SUCCESSFULLY_GENERATED_FILE_FORMAT, fileMessage);
        return ldapProxyResponseBuilder.buildLdapProxyGetSuccessResponse(fileIdentifier, message);
    }

    /**
     * Gets LDAP proxy accounts data according to the ldap proxy get command.
     * 
     * The filename is returned, changed according to the type of get request.
     * 
     * @param command
     *            the ldap proxy get command.
     * @param the
     *            filename string builder.
     * @return the ldap proxy accounts data.
     */
    private NscsProxyAccountsData getProxyAccountsData(final LdapProxyGetCommand command, final StringBuilder filenameBuilder) {
        ProxyAgentAccountGetData proxyAgentAccountGetData = null;
        final Boolean isLegacy = command.isLegacy();
        final Boolean isSummary = command.isSummary();
        final Calendar now = Calendar.getInstance();
        if (command.isAllProxies()) {
            final Integer count = nscsLdapProxyHelper.toIdmsCount(command.getCount());
            proxyAgentAccountGetData = identityManagementProxy.getAllProxyAccounts(isLegacy, isSummary, count);
            filenameBuilder.append(NscsLdapProxyConstants.LDAP_PROXY_GET_ALL);
        } else if (command.getAdminStatus() != null) {
            final ProxyAgentAccountAdminStatus proxyAgentAccountAdminStatus = nscsLdapProxyHelper.toIdmsAdminStatus(command.getAdminStatus());
            proxyAgentAccountGetData = identityManagementProxy.getAllProxyAccountsByAdminStatus(proxyAgentAccountAdminStatus, isLegacy, isSummary);
            filenameBuilder.append(command.getAdminStatus().toLowerCase(Locale.ROOT));
        } else if (command.getInactivityDays() != null) {
            final Long inactivityPeriod = nscsLdapProxyHelper.toIdmsInactivityPeriodByDays(now, command.getInactivityDays());
            proxyAgentAccountGetData = identityManagementProxy.getAllProxyAccountsByInactivityPeriod(inactivityPeriod, isLegacy, isSummary);
            filenameBuilder.append(String.format(NscsLdapProxyConstants.LDAP_PROXY_GET_INACTIVITY_FORMAT, command.getInactivityDays(),
                    NscsLdapProxyConstants.LDAP_PROXY_GET_INACTIVITY_DAYS));
        } else if (command.getInactivityHours() != null) {
            final Long inactivityPeriod = nscsLdapProxyHelper.toIdmsInactivityPeriodByHours(now, command.getInactivityHours());
            proxyAgentAccountGetData = identityManagementProxy.getAllProxyAccountsByInactivityPeriod(inactivityPeriod, isLegacy, isSummary);
            filenameBuilder.append(String.format(NscsLdapProxyConstants.LDAP_PROXY_GET_INACTIVITY_FORMAT, command.getInactivityHours(),
                    NscsLdapProxyConstants.LDAP_PROXY_GET_INACTIVITY_HOURS));
        } else if (command.getInactivitySeconds() != null) {
            final Long inactivityPeriod = nscsLdapProxyHelper.toIdmsInactivityPeriodBySeconds(now, command.getInactivitySeconds());
            proxyAgentAccountGetData = identityManagementProxy.getAllProxyAccountsByInactivityPeriod(inactivityPeriod, isLegacy, isSummary);
            filenameBuilder.append(String.format(NscsLdapProxyConstants.LDAP_PROXY_GET_INACTIVITY_FORMAT, command.getInactivitySeconds(),
                    NscsLdapProxyConstants.LDAP_PROXY_GET_INACTIVITY_SECONDS));
        } else {
            final String message = NscsLdapProxyConstants.LDAP_PROXY_GET_NO_VALID_FILTER_OPTIONS;
            throw new NscsLdapProxyException(message);
        }
        if (isLegacy) {
            filenameBuilder.append(NscsLdapProxyConstants.LDAP_PROXY_GET_LEGACY);
        }
        if (isSummary) {
            filenameBuilder.append(NscsLdapProxyConstants.LDAP_PROXY_GET_SUMMARY);
        }
        filenameBuilder.append(stringifyCalendar(now)).append(FileConstants.XML_EXTENSION);
        return nscsLdapProxyHelper.fromIdmsDto(proxyAgentAccountGetData);
    }

    final String stringifyCalendar(final Calendar calendar) {
        final Date date = calendar.getTime();
        final DateFormat df = new SimpleDateFormat("_yyyyMMdd_HHmmss");
        return df.format(date);

    }

}
