/*
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 */

package com.ericsson.nms.security.nscs.iscf;

import com.ericsson.nms.security.nscs.ldap.utility.PlatformConfigurationReader;
import javax.inject.Inject;

/**
 * This class holds configuration values for ISCF. This may be used in future to model the
 * configuration and listen for value changes
 *
 * @author ealemca
 */
public class IscfConfigurationBean {

    @Inject
    PlatformConfigurationReader platformConfigurationReader;
    
    /**
     * The default value for URL of the SLS (Single Logon Service). 
     */
    private static final String iscfLogonServerAddress = "";
    
    /**
     * The property name to retrieve logonServerAddress value from global.properties
     */
    private static final String iscfLogonServerAddressProperty = "web_host_default";

    /**
     * The property name to retrieve logonServerAddress value from global.properties
     */
    private static final String iscfLogonServerProtocolProperty = "web_protocols_default";

    /**
     * The enrollment key length
     */
    private static final int enrollmentKeyLength = 0;

    /**
     * The iteration count used in the block-cipher encryption algorithm
     */
    private static final int cipherIterationCount = 1024;

    /**
     * The size of the cipher key used to generate cipher parameters
     */
    private static final int cipherKeySize = 128;

    /**
     * The size of the Initialisation Vector (IV) used to generate cipher parameters
     */
    private static final int cipherInitialisationVectorSize = 128;
    /**
     * The length of the generated String used for ISCF encryption
     */
    private static final int rbsIntegrityCodeLength = 20;

    /**
     * The length of the random byte array generated for salting ISCF encrypted values
     */
    private static final int saltLength = 20;

    /**
     * The number of days in advance an alarm is issued, when the node certificate
     * is about to expire
     */
    private static final int certExpiryWarnTime = 90;

    /**
     * The number of days for which this ISCF file will be valid
     */
    private static final int validityPeriodInDays = 14;

    /**
     * The default file transfer client mode
     */
    private static final String fileTransferClientMode = "Secure";

    /**
     * The default telnet and FTP servers mode
     */
    private static final String telnetAndFtpServersMode = "Unsecure";

    /**
     * The maximum length for Ipsec User Label
     */
    private static final int ipsecUserLabelMaxLength = 128;

    /**
     * Get the value of the Single Login Service URL
     *
     * @return  The Single Logon Server address
     */
    public String getIscfLogonServerAddress() {
        if (platformConfigurationReader == null) {
            return iscfLogonServerAddress;
        }
        String logonServProt = platformConfigurationReader.getProperty(iscfLogonServerProtocolProperty);
        String logonServAddr = platformConfigurationReader.getProperty(iscfLogonServerAddressProperty);
        return ((logonServAddr != null)  && (logonServProt != null)) ? 
                logonServProt + "://" + logonServAddr : iscfLogonServerAddress;
    }

  /**
     * Get the value of the enrollment key length
     *
     * @return enrollmentKeyLength The enrollment key length
     */
    public int getEnrollmentKeyLength() {
        return enrollmentKeyLength;
    }

    /**
     * Get the length of the generated String used for ISCF encryption
     *
     * @return rbsIntegrityCodeLength The length of the generated String used for ISCF encryption
     */
    public int getRbsIntegrityCodeLength() {
        return rbsIntegrityCodeLength;
    }

    /**
     * Get the length of the random byte array generated for salting ISCF encrypted values
     *
     * @return saltLength The length of the random byte array generated
     *         for salting ISCF encrypted values
     */
    public int getSaltLength() {
        return saltLength;
    }

    /**
     * Get the value of the cipher iteration count
     *
     * @return cipherIterationCount The cipher iteration count
     */
    public int getCipherIterationCount() {
        return cipherIterationCount;
    }

    /**
     * Get the cipher key size
     *
     * @return cipherKeySize The cipher key size
     */
    public int getCipherKeySize() {
        return cipherKeySize;
    }

    /**
     * Get the cipher Initialisation Vector size
     *
     * @return cipherInitialisationVectorSize The cipher Initialisation Vector size
     */
    public int getCipherInitialisationVectorSize() {
        return cipherInitialisationVectorSize;
    }

    /**
     * Get the value of the certificate expiry warn time
     *
     * @return certExpiryWarnTime The certificate expiry warn time
     */
    public int getCertExpiryWarnTime() {
        return certExpiryWarnTime;
    }

    /**
     * Get the value of the ISCF validity period
     *
     * @return validityPeriodInDays The number of days for which this ISCF file will be valid
     */
    public int getValidityPeriodInDays() {
        return validityPeriodInDays;
    }

    /**
     * Get the default file transfer client mode
     *
     * @return fileTransferClientMode
     */
    public String getFileTransferClientMode() {
        return fileTransferClientMode;
    }

    /**
     * Get the default Telnet and FTP servers mode
     *
     * @return telnetAndFtpServersMode
     */
    public String getTelnetAndFtpServersMode() {
        return telnetAndFtpServersMode;
    }

    /**
     * Get the value of the maximum length for Ipsec User Label
     *
     * @return ipsecUserLabelMaxLength The maximum length for Ipsec User Label
     */
    public int getIpsecUserLabelMaxLength() {
        return ipsecUserLabelMaxLength;
    }

}
