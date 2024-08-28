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
package com.ericsson.oss.services.security.nscs.ldap.proxy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;

import com.ericsson.nms.security.nscs.api.command.NscsPropertyCommand;
import com.ericsson.nms.security.nscs.api.exception.InvalidFileContentException;
import com.ericsson.nms.security.nscs.api.exception.InvalidInputXMLFileException;
import com.ericsson.nms.security.nscs.api.exception.NscsLdapProxyException;
import com.ericsson.nms.security.nscs.enrollmentinfo.utility.FileConstants;
import com.ericsson.nms.security.nscs.util.CliUtil;
import com.ericsson.nms.security.nscs.util.FileUtil;
import com.ericsson.oss.itpf.security.identitymgmtservices.ProxyAgentAccountCounters;
import com.ericsson.oss.itpf.security.identitymgmtservices.ProxyAgentAccountDetails;
import com.ericsson.oss.itpf.security.identitymgmtservices.ProxyAgentAccountGetData;
import com.ericsson.oss.itpf.security.identitymgmtservices.enums.ProxyAgentAccountAdminStatus;
import com.ericsson.oss.itpf.security.pki.common.commonutils.CommonRuntimeException;
import com.ericsson.oss.itpf.security.pki.common.commonutils.JaxbUtil;
import com.ericsson.oss.services.security.nscs.ldap.proxy.dto.NscsProxyAccount;
import com.ericsson.oss.services.security.nscs.ldap.proxy.dto.NscsProxyAccounts;
import com.ericsson.oss.services.security.nscs.ldap.proxy.dto.NscsProxyAccountsCounters;
import com.ericsson.oss.services.security.nscs.ldap.proxy.dto.NscsProxyAccountsData;

/**
 * Auxiliary class containing utilities for the management of ldap proxy commands.
 */
public class NscsLdapProxyHelper {

    @Inject
    private FileUtil fileUtil;

    @Inject
    private CliUtil cliUtil;

    /**
     * Converts proxy accounts data DTO from Identity Management Services (IDMS) to NSCS format.
     * 
     * If IDMS proxy accounts data DTO is null, a valid empty NSCS proxy accounts data DTO is returned.
     * 
     * @param proxyAgentAccountGetData
     *            the IDMS proxy accounts data DTO.
     * @return the NSCS proxy accounts data DTO.
     * @throws {@link
     *             NscsLdapProxyException} if conversion fails.
     */
    public NscsProxyAccountsData fromIdmsDto(final ProxyAgentAccountGetData proxyAgentAccountGetData) {
        final NscsProxyAccountsData nscsProxyAccountsData = new NscsProxyAccountsData();
        if (proxyAgentAccountGetData != null) {
            final NscsProxyAccountsCounters nscsProxyAccountsCounters = fromIdmsDto(proxyAgentAccountGetData.getProxyAgentAccountCounters());
            nscsProxyAccountsData.setProxyAccountsCounters(nscsProxyAccountsCounters);
            final NscsProxyAccounts nscsProxyAccounts = fromIdmsDto(proxyAgentAccountGetData.getProxyAgentAccountDetailsList());
            nscsProxyAccountsData.setProxyAccounts(nscsProxyAccounts);
        }
        return nscsProxyAccountsData;
    }

    /**
     * Converts proxy account DN from NSCS to Identity Management Services (IDMS) format.
     * 
     * @param proxyAccountDN
     *            the NSCS proxy account DN.
     * @return the IDMS proxy account DN.
     * @throws {@link
     *             NscsLdapProxyException} if proxy account DN has unexpected format.
     */
    public String toIdmsProxyAccountDN(final String proxyAccountDN) {
        if (proxyAccountDN != null) {
            try {
                new LdapName(proxyAccountDN);
            } catch (final InvalidNameException e) {
                final String message = String.format(NscsLdapProxyConstants.LDAP_PROXY_GET_INVALID_PARAMETER_VALUE_FORMAT,
                        NscsLdapProxyConstants.LDAP_PROXY_DN_PARAM, proxyAccountDN);
                final String suggestedSolution = String.format(NscsLdapProxyConstants.LDAP_PROXY_GET_ALLOWED_VALUES_FORMAT,
                        NscsLdapProxyConstants.LDAP_PROXY_GET_ALLOWED_DN_VALUES);
                throw new NscsLdapProxyException(message, e, suggestedSolution);
            }
            final Pattern proxyAccountDNPattern = Pattern.compile(NscsLdapProxyConstants.LDAP_PROXY_ACCOUNT_DN_REGEX);
            final Matcher proxyAccountDNMatcher = proxyAccountDNPattern.matcher(proxyAccountDN);
            if (proxyAccountDNMatcher.matches()) {
                return proxyAccountDN;
            }
        }
        final String message = String.format(NscsLdapProxyConstants.LDAP_PROXY_GET_INVALID_PARAMETER_VALUE_FORMAT,
                NscsLdapProxyConstants.LDAP_PROXY_DN_PARAM, proxyAccountDN);
        final String suggestedSolution = String.format(NscsLdapProxyConstants.LDAP_PROXY_GET_ALLOWED_VALUES_FORMAT,
                NscsLdapProxyConstants.LDAP_PROXY_GET_ALLOWED_DN_VALUES);
        throw new NscsLdapProxyException(message, suggestedSolution);
    }

    /**
     * Converts proxy account admin status from NSCS to Identity Management Services (IDMS) format.
     * 
     * @param adminStatus
     *            the NSCS proxy account admin status.
     * @return the IDMS proxy account admin status.
     * @throws {@link
     *             NscsLdapProxyException} if conversion fails.
     */
    public ProxyAgentAccountAdminStatus toIdmsAdminStatus(final String adminStatus) {
        if (NscsLdapProxyConstants.LDAP_PROXY_ADMIN_STATUS_DISABLED.equals(adminStatus)) {
            return ProxyAgentAccountAdminStatus.DISABLED;
        }
        if (NscsLdapProxyConstants.LDAP_PROXY_ADMIN_STATUS_ENABLED.equals(adminStatus)) {
            return ProxyAgentAccountAdminStatus.ENABLED;
        }
        final String message = String.format(NscsLdapProxyConstants.LDAP_PROXY_GET_INVALID_PARAMETER_VALUE_FORMAT,
                NscsLdapProxyConstants.LDAP_PROXY_ADMIN_STATUS_PARAM, adminStatus);
        final String suggestedSolution = String.format(NscsLdapProxyConstants.LDAP_PROXY_GET_ALLOWED_VALUES_FORMAT,
                NscsLdapProxyConstants.LDAP_PROXY_GET_ALLOWED_ADMIN_STATUS_VALUES);
        throw new NscsLdapProxyException(message, suggestedSolution);
    }

    /**
     * Converts proxy account inactivity period in days from NSCS to Identity Management Services (IDMS) format.
     * 
     * The inactivity period shall be an integer greater than 0.
     * 
     * @param now
     *            the current calendar.
     * @param inactivityDays
     *            the inactivity period in days expressed as string.
     * @return the starting inactivity time expressed in milliseconds.
     * @throws {@link
     *             NscsLdapProxyException} if conversion fails.
     */
    public Long toIdmsInactivityPeriodByDays(final Calendar now, final String inactivityDays) {
        try {
            final Integer numOfDays = Integer.valueOf(inactivityDays);
            if (numOfDays <= 0) {
                final String message = String.format(NscsLdapProxyConstants.LDAP_PROXY_GET_INVALID_PARAMETER_VALUE_FORMAT,
                        NscsLdapProxyConstants.LDAP_PROXY_GET_INACTIVITY_PERIOD_PARAM, inactivityDays);
                final String suggestedSolution = String.format(NscsLdapProxyConstants.LDAP_PROXY_GET_ALLOWED_VALUES_FORMAT,
                        NscsLdapProxyConstants.LDAP_PROXY_GET_ALLOWED_INACTIVITY_PERIOD_VALUES);
                throw new NscsLdapProxyException(message, suggestedSolution);
            }
            final Long intervalMillis = NscsLdapProxyConstants.LDAP_PROXY_GET_MILLIS_IN_DAY * numOfDays;
            return getBeforeTimeInMillisByInterval(now, intervalMillis);
        } catch (final NumberFormatException e) {
            final String message = String.format(NscsLdapProxyConstants.LDAP_PROXY_GET_INVALID_PARAMETER_VALUE_FORMAT,
                    NscsLdapProxyConstants.LDAP_PROXY_GET_INACTIVITY_PERIOD_PARAM, inactivityDays);
            final String suggestedSolution = String.format(NscsLdapProxyConstants.LDAP_PROXY_GET_ALLOWED_VALUES_FORMAT,
                    NscsLdapProxyConstants.LDAP_PROXY_GET_ALLOWED_INACTIVITY_PERIOD_VALUES);
            throw new NscsLdapProxyException(message, suggestedSolution);
        }
    }

    /**
     * Converts proxy account inactivity period in hours from NSCS to Identity Management Services (IDMS) format.
     * 
     * The inactivity period shall be an integer greater than 0.
     * 
     * @param now
     *            the current calendar.
     * @param inactivityHours
     *            the inactivity period in hours expressed as string.
     * @return the starting inactivity time expressed in milliseconds.
     * @throws {@link
     *             NscsLdapProxyException} if conversion fails.
     */
    public Long toIdmsInactivityPeriodByHours(final Calendar now, final String inactivityHours) {
        try {
            final Integer numOfHours = Integer.valueOf(inactivityHours);
            if (numOfHours <= 0) {
                final String message = String.format(NscsLdapProxyConstants.LDAP_PROXY_GET_INVALID_PARAMETER_VALUE_FORMAT,
                        NscsLdapProxyConstants.LDAP_PROXY_GET_INACTIVITY_PERIOD_PARAM, inactivityHours);
                final String suggestedSolution = String.format(NscsLdapProxyConstants.LDAP_PROXY_GET_ALLOWED_VALUES_FORMAT,
                        NscsLdapProxyConstants.LDAP_PROXY_GET_ALLOWED_INACTIVITY_PERIOD_VALUES);
                throw new NscsLdapProxyException(message, suggestedSolution);
            }
            final Long intervalMillis = NscsLdapProxyConstants.LDAP_PROXY_GET_MILLIS_IN_HOUR * numOfHours;
            return getBeforeTimeInMillisByInterval(now, intervalMillis);
        } catch (final NumberFormatException e) {
            final String message = String.format(NscsLdapProxyConstants.LDAP_PROXY_GET_INVALID_PARAMETER_VALUE_FORMAT,
                    NscsLdapProxyConstants.LDAP_PROXY_GET_INACTIVITY_PERIOD_PARAM, inactivityHours);
            final String suggestedSolution = String.format(NscsLdapProxyConstants.LDAP_PROXY_GET_ALLOWED_VALUES_FORMAT,
                    NscsLdapProxyConstants.LDAP_PROXY_GET_ALLOWED_INACTIVITY_PERIOD_VALUES);
            throw new NscsLdapProxyException(message, suggestedSolution);
        }
    }

    /**
     * Converts proxy account inactivity period in seconds from NSCS to Identity Management Services (IDMS) format.
     * 
     * The inactivity period shall be an integer greater than 0.
     * 
     * @param now
     *            the current calendar.
     * @param inactivitySeconds
     *            the inactivity period in seconds expressed as string.
     * @return the starting inactivity time expressed in milliseconds.
     * @throws {@link
     *             NscsLdapProxyException} if conversion fails.
     */
    public Long toIdmsInactivityPeriodBySeconds(final Calendar now, final String inactivitySeconds) {
        try {
            final Integer numOfSeconds = Integer.valueOf(inactivitySeconds);
            if (numOfSeconds <= 0) {
                final String message = String.format(NscsLdapProxyConstants.LDAP_PROXY_GET_INVALID_PARAMETER_VALUE_FORMAT,
                        NscsLdapProxyConstants.LDAP_PROXY_GET_INACTIVITY_PERIOD_PARAM, inactivitySeconds);
                final String suggestedSolution = String.format(NscsLdapProxyConstants.LDAP_PROXY_GET_ALLOWED_VALUES_FORMAT,
                        NscsLdapProxyConstants.LDAP_PROXY_GET_ALLOWED_INACTIVITY_PERIOD_VALUES);
                throw new NscsLdapProxyException(message, suggestedSolution);
            }
            final Long intervalMillis = NscsLdapProxyConstants.LDAP_PROXY_GET_MILLIS_IN_SECOND * numOfSeconds;
            return getBeforeTimeInMillisByInterval(now, intervalMillis);
        } catch (final NumberFormatException e) {
            final String message = String.format(NscsLdapProxyConstants.LDAP_PROXY_GET_INVALID_PARAMETER_VALUE_FORMAT,
                    NscsLdapProxyConstants.LDAP_PROXY_GET_INACTIVITY_PERIOD_PARAM, inactivitySeconds);
            final String suggestedSolution = String.format(NscsLdapProxyConstants.LDAP_PROXY_GET_ALLOWED_VALUES_FORMAT,
                    NscsLdapProxyConstants.LDAP_PROXY_GET_ALLOWED_INACTIVITY_PERIOD_VALUES);
            throw new NscsLdapProxyException(message, suggestedSolution);
        }
    }

    /**
     * Converts proxy account count from NSCS to Identity Management Services (IDMS) format.
     * 
     * @param count
     *            the count expressed as string.
     * @return the count expressed as integer or null if count string is null.
     */
    public Integer toIdmsCount(final String count) {
        if (count == null) {
            return null;
        }
        try {
            final Integer countInteger = Integer.valueOf(count);
            if (countInteger < 0) {
                final String message = String.format(NscsLdapProxyConstants.LDAP_PROXY_GET_INVALID_PARAMETER_VALUE_FORMAT,
                        NscsLdapProxyConstants.LDAP_PROXY_GET_COUNT_PARAM, count);
                final String suggestedSolution = String.format(NscsLdapProxyConstants.LDAP_PROXY_GET_ALLOWED_VALUES_FORMAT,
                        NscsLdapProxyConstants.LDAP_PROXY_GET_ALLOWED_COUNT_VALUES);
                throw new NscsLdapProxyException(message, suggestedSolution);
            }
            return countInteger;
        } catch (final NumberFormatException e) {
            final String message = String.format(NscsLdapProxyConstants.LDAP_PROXY_GET_INVALID_PARAMETER_VALUE_FORMAT,
                    NscsLdapProxyConstants.LDAP_PROXY_GET_COUNT_PARAM, count);
            final String suggestedSolution = String.format(NscsLdapProxyConstants.LDAP_PROXY_GET_ALLOWED_VALUES_FORMAT,
                    NscsLdapProxyConstants.LDAP_PROXY_GET_ALLOWED_COUNT_VALUES);
            throw new NscsLdapProxyException(message, suggestedSolution);
        }
    }

    /**
     * Gets an XML string from an NscsProxyAccountsData JAXB object.
     * 
     * @param nscsProxyAccountsData
     *            the {@link NscsProxyAccountsData} JAXB object.
     * @return the XML string.
     * @throws {@link
     *             NscsLdapProxyException} if any exception is JAXB exception is thrown by JaxbUtil.
     */
    public String getXmlFromNscsProxyAccountsData(final NscsProxyAccountsData nscsProxyAccountsData) {
        try {
            return JaxbUtil.getXml(nscsProxyAccountsData, true);
        } catch (final CommonRuntimeException e) {
            final String message = String.format(NscsLdapProxyConstants.LDAP_PROXY_GET_JAXB_EXCEPTION_MARSHALING_CLASS_FORMAT,
                    NscsProxyAccountsData.class.getCanonicalName());
            throw new NscsLdapProxyException(message, e);
        }
    }

    /**
     * Gets from the XML file specified in the given command the list of NSCS proxy accounts.
     * 
     * At least one ProxyAccount shall be present.
     * 
     * @param command
     *            the ldap proxy set or delete command.
     * @return the list of NSCS proxy accounts.
     * @throws {@link
     *             InvalidFileContentException} if XML file content is invalid.
     * @throws {@link
     *             InvalidInputXMLFileException} if any unmarshaling exception is thrown.
     * @throws {@link
     *             NscsLdapProxyException} if no ProxyAccount element is present in XML file.
     */
    public List<NscsProxyAccount> getNscsProxyAccountsFromCommand(final NscsPropertyCommand command) {
        final String xmlContent = getXmlContentFromCommand(command);
        final NscsProxyAccountsData nscsProxyAccountsData = getNscsProxyAccountsDataFromXml(xmlContent);
        final List<NscsProxyAccount> nscsProxyAccounts = nscsProxyAccountsData.getProxyAccounts().getProxyAccounts();
        if (nscsProxyAccounts == null || nscsProxyAccounts.isEmpty()) {
            final String errorMsg = NscsLdapProxyConstants.LDAP_PROXY_NO_PROXY_ACCOUNT_ELEMENTS_IN_XML_FILE;
            throw new NscsLdapProxyException(errorMsg);
        }
        return nscsProxyAccounts;
    }

    /**
     * Creates a deletable download file holder identifier.
     * 
     * @param fileContents
     *            bytes to be written to the downloading file.
     * @param fileName
     *            name of the download file.
     * @param contentType
     *            type of content of downloading file.
     * @return the download file holder identifier
     * @throws NscsLdapProxyException
     *             thrown when failure occurs while preparing file download.
     */
    public String createDeletableDownloadFileIdentifier(final byte[] fileContents, final String fileName, final String contentType) {
        try {
            return fileUtil.createDeletableDownloadFileIdentifier(fileContents, fileName, contentType);
        } catch (final IOException e) {
            final String message = String.format(NscsLdapProxyConstants.LDAP_PROXY_GET_IO_EXCEPTION_FILE_FORMAT, fileName);
            throw new NscsLdapProxyException(message, e);
        }
    }

    /**
     * Gets the before time in milliseconds by a given interval in milliseconds.
     * 
     * @param now
     *            the current calendar.
     * @param intervalMillis
     *            the interval in milliseconds.
     * @return the before time in milliseconds.
     */
    private Long getBeforeTimeInMillisByInterval(final Calendar now, final Long intervalMillis) {
        final long nowMillis = now.getTimeInMillis();
        final long beforeMillis = nowMillis - intervalMillis;
        return Long.valueOf(beforeMillis);
    }

    /**
     * Converts proxy accounts counters DTO from Identity Management Services (IDMS) to NSCS format.
     * 
     * @param proxyAgentAccountCounters
     *            the IDMS proxy accounts counters DTO.
     * @return the NSCS proxy accounts counters DTO.
     */
    private NscsProxyAccountsCounters fromIdmsDto(final ProxyAgentAccountCounters proxyAgentAccountCounters) {
        if (proxyAgentAccountCounters == null) {
            return null;
        }
        final NscsProxyAccountsCounters nscsProxyAccountsCounters = new NscsProxyAccountsCounters();
        nscsProxyAccountsCounters.setNumOfProxyAccounts(proxyAgentAccountCounters.getNumOfProxyAccount());
        nscsProxyAccountsCounters.setNumOfRequestedProxyAccounts(proxyAgentAccountCounters.getNumOfRequestedProxyAccount());
        nscsProxyAccountsCounters.setNumOfLegacyProxyAccounts(proxyAgentAccountCounters.getNumOfProxyAccountLegacy());
        nscsProxyAccountsCounters.setNumOfRequestedLegacyProxyAccounts(proxyAgentAccountCounters.getNumOfRequestedProxyAccountLegacy());
        return nscsProxyAccountsCounters;
    }

    /**
     * Converts list of proxy accounts DTO from Identity Management Services (IDMS) to NSCS format.
     * 
     * @param proxyAgentAccountDetailsList
     *            the IDMS list of proxy accounts DTO.
     * @return the NSCS proxy accounts DTO.
     */
    private NscsProxyAccounts fromIdmsDto(final List<ProxyAgentAccountDetails> proxyAgentAccountDetailsList) {
        if (proxyAgentAccountDetailsList == null) {
            return null;
        }
        final List<NscsProxyAccount> proxyAccountsList = new ArrayList<>();
        for (final ProxyAgentAccountDetails proxyAgentAccountDetails : proxyAgentAccountDetailsList) {
            final NscsProxyAccount nscsProxyAccount = fromIdmsDto(proxyAgentAccountDetails);
            proxyAccountsList.add(nscsProxyAccount);
        }
        final NscsProxyAccounts proxyAccounts = new NscsProxyAccounts();
        proxyAccounts.setProxyAccounts(proxyAccountsList);
        return proxyAccounts;
    }

    /**
     * Converts proxy account DTO from Identity Management Services (IDMS) to NSCS format.
     * 
     * @param proxyAgentAccountDetails
     *            the IDMS proxy account DTO.
     * @return the NSCS proxy account DTO.
     */
    private NscsProxyAccount fromIdmsDto(final ProxyAgentAccountDetails proxyAgentAccountDetails) {
        if (proxyAgentAccountDetails == null) {
            return null;
        }
        final NscsProxyAccount nscsProxyAccount = new NscsProxyAccount();
        nscsProxyAccount.setDn(proxyAgentAccountDetails.getUserDn());
        nscsProxyAccount.setAdminStatus(fromIdmsDto(proxyAgentAccountDetails.getAdminStatus()));
        nscsProxyAccount.setCreateDate(fromIdmsDto(proxyAgentAccountDetails.getCreateTimestamp()));
        nscsProxyAccount.setLastLoginDate(fromIdmsDto(proxyAgentAccountDetails.getLastLoginTime()));
        return nscsProxyAccount;
    }

    /**
     * Converts proxy account admin status from Identity Management Services (IDMS) to NSCS format.
     * 
     * Reading null from IDMS shall be considered as ENABLED since this is the status of legacy proxy accounts where admin status was not set,
     * 
     * @param adminStatus
     *            the IDMS proxy account admin status.
     * @return the NSCS proxy account admin status.
     */
    private String fromIdmsDto(final ProxyAgentAccountAdminStatus adminStatus) {
        if (adminStatus == null) {
            return NscsLdapProxyConstants.LDAP_PROXY_ADMIN_STATUS_ENABLED;
        }
        String nscsAdminStatus = null;
        switch (adminStatus) {
        case DISABLED:
            nscsAdminStatus = NscsLdapProxyConstants.LDAP_PROXY_ADMIN_STATUS_DISABLED;
            break;
        case ENABLED:
            nscsAdminStatus = NscsLdapProxyConstants.LDAP_PROXY_ADMIN_STATUS_ENABLED;
            break;
        default:
            break;
        }
        return nscsAdminStatus;
    }

    /**
     * Converts a date from Identity Management Services (IDMS) to NSCS format.
     * 
     * @param millis
     *            the IDMS date expressed in millis.
     * @return the NSCS date or NEVER if millis is null.
     */
    private String fromIdmsDto(final Long millis) {
        if (millis == null) {
            return NscsLdapProxyConstants.LDAP_PROXY_GET_NEVER;
        }
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        final Date date = calendar.getTime();
        final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(date);
    }

    /**
     * Gets XML file content as string from the file specified in the given command.
     * 
     * @param command
     *            the command.
     * @return the XML content as string.
     * @throws InvalidFileContentException
     *             if XML file content is invalid.
     */
    private String getXmlContentFromCommand(final NscsPropertyCommand command) {
        final String xmlContent = cliUtil.getCommandInputDataWithNewExceptionHandling(command, FileConstants.FILE_URI);
        if (null == xmlContent || xmlContent.isEmpty()) {
            final String errorMsg = NscsLdapProxyConstants.LDAP_PROXY_NULL_OR_EMPTY_XML_FILE_CONTENT;
            throw new InvalidFileContentException(errorMsg);
        }
        return xmlContent;
    }

    /**
     * Gets an NscsProxyAccountsData JAXB object from an XML file content as string.
     * 
     * @param xmlContent
     *            the XML file content as string.
     * @return the {@link NscsProxyAccountsData} JAXB object.
     * @throws {@link
     *             InvalidInputXMLFileException} if any unmarshaling exception is thrown by JaxbUtil.
     */
    private NscsProxyAccountsData getNscsProxyAccountsDataFromXml(final String xmlContent) {
        try {
            return JaxbUtil.getObject(new ByteArrayInputStream(xmlContent.getBytes(StandardCharsets.UTF_8)), NscsProxyAccountsData.class,
                    getFileResourceAsUrl(NscsLdapProxyConstants.LDAP_PROXY_SCHEMA_XSD));
        } catch (final CommonRuntimeException e) {
            throw new InvalidInputXMLFileException(e);
        }
    }

    /**
     * Gets URL for the given file.
     * 
     * @param filename
     *            the file name.
     * @return {@link URL} the URL.
     */
    private URL getFileResourceAsUrl(final String filename) {
        return Thread.currentThread().getContextClassLoader().getResource(filename);
    }

}
