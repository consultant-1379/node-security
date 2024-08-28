/*-----------------------------------------------------------------------------
*******************************************************************************
* COPYRIGHT Ericsson 2021
*
* The copyright to the computer program(s) herein is the property of
* Ericsson Inc. The programs may be used and/or copied only with written
* permission from Ericsson Inc. or in accordance with the terms and
* conditions stipulated in the agreement/contract under which the
* program(s) have been supplied.
*******************************************************************************
*----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.utilities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Singleton class providing methods for common validations used in NSCS.
 */
public class NscsCommonValidator implements Serializable {

    private static final long serialVersionUID = 6049611762871458706L;

    private static final NscsCommonValidator theValidator = new NscsCommonValidator();

    /**
     * IPv4 regular expression.
     * 
     * An IPv4 address has the following format:
     * 
     * x.x.x.x
     * 
     * In the IPv4 address, x is called an octet and must be a decimal value between 0 and 255.
     * 
     * The octets are separated by periods.
     * 
     * The IPv4 address must contain three periods and four octets.
     */
    private static final String IPV4_REGEX = "^(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(\\.(?!$)|$)){4}$";

    private static final Pattern ipv4Pattern = Pattern.compile(IPV4_REGEX);

    /**
     * Domain name regular expression.
     *
     * The supported domain name is also a hostname (that is, it has been assigned to an Internet host and associated with the host's IP address).
     *
     * Hostname is composed of a sequence of labels concatenated with dots.
     *
     * The rightmost label is the Top Level Domain name (TLD).
     *
     * Each label except TLD must be from 1 to 63 characters long, the allowed characters are a-z | A-Z | 0-9 and hyphen(-). The RFC 952 disallows
     * hostname labels from starting with a digit or with a hyphen character and from ending with a hyphen. However, the subsequent RFC 1123 permits
     * hostname labels to start with digits.
     *
     * The TLD that must be from 2 to 6 characters long, the allowed characters are a-z | A-Z.
     *
     * Internationalized domain names are not supported.
     *
     * The entire hostname, including the delimiting dots, has a maximum of 253 ASCII characters.
     *
     * The hostname shall be case-insensitive.
     */
    private static final String DOMAIN_HOSTNAME_REGEX = "^((?!-)[A-Za-z0-9-]{1,63}(?<!-)\\.)+[A-Za-z]{2,6}$";

    private static final Pattern domainHostnamePattern = Pattern.compile(DOMAIN_HOSTNAME_REGEX, Pattern.CASE_INSENSITIVE);
    /**

     * ^ asserts the start of the string.
     *
     * (?=.{1,255}$) is a positive lookahead assertion that ensures the string length is between 1 and 255 characters.
     *
     * (?!.*\\.{2}) is a negative lookahead assertion that ensures there are no consecutive periods.
     *
     * (((?:(?!-)[\\x21-\\x7E]{1,63}(?<!-|_)(?:\\.|$)){1,})*) is the main pattern for matching the domain name.
     *
     *       (?:(?!-)[\\x21-\\x7E]{1,63}(?<!-|_)(?:\\.|$)){1,} is a non-capturing group that matches each segment of the domain name:
     *           (?!-) ensures that the segment does not start with a hyphen.
     *           [\x21-\x7E]{1,63} matches 1 to 63 characters within the ASCII range of printable characters (excluding space).
     *           (?<!-|_) ensures that the segment does not end with a hyphen or underscore.
     *           (?:\\.|$) matches either a period or the end of the string.
     *           {1,} quantifier allows this group to repeat one or more times.
     *
     *   * quantifier allows the entire group to repeat zero or more times, allowing for multiple domain segments separated by periods.
     *
     * (?<!\\.)$ is a negative lookbehind assertion that ensures the string does not end with a period.
     *
     * $ asserts the end of the string.
     */
    private static final String DNS_NAME_REGEX = "^(?=.{1,255}$)(?!.*\\.{2})(((?:(?!-)[\\x21-\\x7E]{1,63}(?<!-|_)(?:\\.|$)){1,})*)$";

    private static final Pattern dnsNamePattern = Pattern.compile(DNS_NAME_REGEX, Pattern.CASE_INSENSITIVE);

    /**
     * ^ --> Asserts the start of the string
     *[_A-Za-z0-9-\\+]+ --> Matches one or more occurrences of the following characters _ A-Z a-z 0-9 - +
     * (\\.[_A-Za-z0-9-]+)*  --> this allows for zero or more occurrences of the dot(.) _ A-Z a-z 0-9 - + (This part represents the domain name and allows for subdomains separated by dots)
     * @ --> Matches the at symbol "@"
     * [A-Za-z0-9-]+ --> Matches one or more occurrences of the characters A-Z a-z 0-9 -
     * (\\.[A-Za-z0-9]+)*  --> this allows for zero or more occurrences of the following dot(.) A-Z a-z 0-9 (this for subdomain)
     *(\\.[A-Za-z]{2,6})  -->  this enforces at least two occurrences of the following dot(.) A-Z a-z (final capture of 2 to 6 characters after dot)
     * $ --> Asserts the end of the string
     */
    private static final String RFC822_NAME_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-\\+]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,6})$";


    private static final Pattern rFC822NamePattern = Pattern.compile(RFC822_NAME_REGEX, Pattern.CASE_INSENSITIVE);
    /**
     * Returns the singleton instance.
     * 
     * @return the singleton instance.
     */
    public static NscsCommonValidator getInstance() {
        return theValidator;
    }

    /**
     * Returns if the given string is a valid IP address.
     * 
     * @param ipAddress
     *            the string to be validated as a valid IP address.
     * @return true if the string contains a valid IP address, false otherwise.
     */
    public boolean isValidIPAddress(final String ipAddress) {
        if (ipAddress == null || ipAddress.isEmpty()) {
            return false;
        }
        return isValidIPv4Address(ipAddress) || isValidIPv6Address(ipAddress);
    }

    /**
     * Returns if the given string is a valid IPv4 address.
     * 
     * @param ipAddress
     *            the string to be validated as a valid IPv4 address.
     * @return true if the string contains a valid IPv4 address, false otherwise.
     */
    public boolean isValidIPv4Address(final String ipAddress) {
        if (ipAddress == null || ipAddress.isEmpty()) {
            return false;
        }

        final Matcher matcher = ipv4Pattern.matcher(ipAddress);
        return matcher.matches();
    }

    /**
     * Returns if the given string is a valid IPv6 address.
     * 
     * Since IPv6 addresses contain colons, they cannot be directly used in URLs because the colons would conflict with both the protocol declaration
     * (http:// or https://) and port numbers. Therefore, when a literal IPv6 address is used, it is enclosed in square brackets.
     * 
     * An IPv6 address can have either of the following two formats: Normal - Pure IPv6 format Dual - IPv6 plus IPv4 formats
     * 
     * An IPv6 (Normal) address has the following format:
     * 
     * y:y:y:y:y:y:y:y
     * 
     * where y is called a segment and can be any hexadecimal value between 0 and FFFF (or ffff, validation shall be case-insensitive).
     * 
     * In each segment the leading zeros can be omitted (2a instead of 002a).
     * 
     * The segments are separated by colons.
     * 
     * An IPv6 normal address must have eight segments, however a short notation (double colon) can be used: the longest sequence of consecutive
     * all-zero segments can be replaced with double colon ("::") and in case the address contains multiple occurrences of such sequences, the
     * leftmost one should be compressed.
     * 
     * Only one double colon is allowed.
     * 
     * An IPv6 (Dual) address combines an IPv6 and an IPv4 address and has the following format:
     * 
     * y:y:y:y:y:y:x.x.x.x
     * 
     * The IPv6 portion of the address (indicated with y's) is always at the beginning, followed by the IPv4 portion (indicated with x's).
     * 
     * In the IPv6 portion of the address, y is called a segment and can be any hexadecimal value between 0 and FFFF (or ffff).
     * 
     * The segments are separated by colons.
     * 
     * The IPv6 portion of the address must have six segments but there is a short notation (double colon) for segments that are zero.
     * 
     * In the IPv4 portion of the address x is called an octet and must be a decimal value between 0 and 255.
     * 
     * The octets are separated by periods.
     * 
     * The IPv4 portion of the address must contain three periods and four octets.
     * 
     * The IPv4 portion occupies two segments.
     * 
     * @param ipAddress
     *            the string to be validated as a valid IPv6 address.
     * @return true if the string contains a valid IPv6 address, false otherwise.
     */
    public boolean isValidIPv6Address(final String ipAddress) {
        if (ipAddress == null || ipAddress.isEmpty()) {
            return false;
        }
        if (ipAddress.startsWith("[")) {
            if (ipAddress.endsWith("]") && ipAddress.length() > 2) {
                return isValidLiteralIPv6Address(ipAddress.substring(1, ipAddress.length() - 1));
            } else {
                return false;
            }
        }
        return isValidLiteralIPv6Address(ipAddress);
    }

    /**
     * Returns if the given string is a valid domain hostname.
     * 
     * @param domainHostname
     *            the string to be validated as a valid domain hostname.
     * @return true if the string contains a valid domain hostname, false otherwise.
     */
    public boolean isValidDomainHostname(final String domainHostname) {
        if (domainHostname == null || domainHostname.isEmpty()) {
            return false;
        }

        // the entire hostname, including the delimiting dots, has a maximum of 253 ASCII characters
        if (domainHostname.length() > 253) {
            return false;
        }

        final Matcher matcher = domainHostnamePattern.matcher(domainHostname);
        return matcher.matches();
    }

    public boolean isValidRFC822Name(final String rFC822Name) {
        if (rFC822Name == null || rFC822Name.isEmpty()) {
            return false;
        }


        if (rFC822Name.length() > 253) {
            return false;
        }
        // rfs822Pattern will check the value for Email format
        final Matcher matcher = rFC822NamePattern.matcher(rFC822Name);
        return matcher.matches();
    }
    public boolean isValidDNSName(final String dnsName) {
        if (dnsName == null || dnsName.isEmpty()) {
            return false;
        }


        if (dnsName.length() > 253) {
            return false;
        }
        // dnsNamePattern will check the value for dns name format
        final Matcher matcher = dnsNamePattern.matcher(dnsName);
        return matcher.matches();
    }
    /**
     * Returns if the given string has valid IPv6 address segments.
     * 
     * @param ipAddress
     *            the string to be validated as a valid IPv6 address.
     * @return true if the string has valid IPv6 address segments, false otherwise.
     */
    private boolean hasValidIPv6AddressSegments(final String ipAddress) {

        String[] segments = getIPv6Segments(ipAddress);

        // the maximum number of segments is 8
        if (segments.length > 8) {
            return false;
        }

        return areValidIPv6Segments(ipAddress, segments);
    }

    /**
     * Returns if the given IPv6 segments extracted from the given IP address are valid IPv6 segments.
     * 
     * @param ipAddress
     *            the string to be validated as a valid IPv6 address.
     * @param segments
     *            the array of IPv6 segments.
     * @return true if the IPv6 segments are valid for the IPv6 address, false otherwise.
     */
    private boolean areValidIPv6Segments(final String ipAddress, String[] segments) {

        boolean hasConsecutiveEmptySegments = false;
        final NscsSegmentCounters nscsSegmentCounters = new NscsSegmentCounters();

        for (int index = 0; index < segments.length; index++) {
            final String segment = segments[index];
            if (segment.length() == 0) {
                // multiple consecutive empty segments are not allowed
                if (hasConsecutiveEmptySegments) {
                    return false;
                }
                hasConsecutiveEmptySegments = true;
            } else {
                // reset consecutive empty segments flag
                hasConsecutiveEmptySegments = false;

                if (!isValidNotEmptySegment(ipAddress, segments, nscsSegmentCounters, index, segment)) {
                    return false;
                }
            }
        }

        // the number of valid not empty IPv6 segments in absence of double colon shall be 8
        return nscsSegmentCounters.getNumOfValidNotEmptyIPv6Segments() == 8 || ipAddress.contains("::");
    }

    /**
     * Returns if the given segment of given index from given IPv6 segments extracted from the given IP address is a valid IPv6 not empty segment.
     * 
     * @param ipAddress
     *            the string to be validated as a valid IPv6 address.
     * @param segments
     *            the array of IPv6 segments.
     * @param nscsSegmentCounters
     *            the current segments counters.
     * @param index
     *            the index of segment in array of segments.
     * @param segment
     *            the current not empty segment.
     * @return true if valid not empty segment, false otherwise.
     */
    private boolean isValidNotEmptySegment(final String ipAddress, String[] segments, final NscsSegmentCounters nscsSegmentCounters, int index,
            final String segment) {

        // check if the segment contains an embedded IPv4 address (dual IPv6 address)
        if (segment.contains(".")) {
            // dual IPv6 address
            if (!isValidEmbeddedIPv4Segment(ipAddress, segments, index, segment)) {
                return false;
            }

            // the segment containing the embedded IPv4 address is equivalent to two IPv6 segments
            nscsSegmentCounters.incrementNumOfValidNotEmptyIPv6Segments(2);
        } else {
            // the segment does not contain an embedded IPv4 address (normal IPv6 address)

            if (!isValidIPv6Segment(segment)) {
                return false;
            }

            // increment the number of valid not empty IPv6 segments
            nscsSegmentCounters.incrementNumOfValidNotEmptyIPv6Segments(1);
        }
        return true;
    }

    /**
     * Returns if the given segment is a valid IPv6 segment.
     * 
     * @param segment
     *            the segment to be validated as a valid IPv6 segment.
     * @return true if valid IPv6 segment, false otherwise.
     */
    private boolean isValidIPv6Segment(final String segment) {

        // the not empty IPv6 segment can contain max 4 digits
        if (segment.length() > 4) {
            return false;
        }

        // the not empty IPv6 segment can be any hexadecimal value between 0 and FFFF (or ffff)
        int segmentInt = 0;
        try {
            segmentInt = Integer.valueOf(segment, 16).intValue();
        } catch (NumberFormatException e) {
            return false;
        }
        return !(segmentInt < 0 || segmentInt > 0xffff);
    }

    /**
     * Returns if the given segment of given index from given IPv6 segments extracted from the given IP address is a valid embedded IPv4 segment.
     * 
     * @param ipAddress
     *            the string to be validated as a valid IPv6 address.
     * @param segments
     *            the array of IPv6 segments.
     * @param index
     *            the index of segment in array of segments.
     * @param segment
     *            the current not empty segment.
     * @return true if valid embedded IPv4 not empty segment, false otherwise.
     */
    private boolean isValidEmbeddedIPv4Segment(final String ipAddress, String[] segments, int index, final String segment) {

        // the segment containing the embedded IPv4 address shall be the last segment
        if (!ipAddress.endsWith(segment)) {
            return false;
        }
        // the segment containing the embedded IPv4 address shall be the last segment and the previous IPv6 section cannot exceed 6 segments
        if (index > segments.length - 1 || index > 6) {
            return false;
        }
        // the segment containing the embedded IPv4 address shall contain a valid IPv4 address
        return isValidIPv4Address(segment);
    }

    /**
     * Gets the array of IPv6 segments from the given string to be validated as a valid IPv6 address.
     * 
     * @param ipAddress
     *            the string to be validated as a valid IPv6 address.
     * @return the array of IPv6 segments.
     */
    private String[] getIPv6Segments(final String ipAddress) {

        String[] segments = ipAddress.split(":");

        // if the address starts or ends with double colon the split returns an array that shall be adjusted
        if (ipAddress.contains("::")) {
            List<String> segmentsAsList = new ArrayList<>(Arrays.asList(segments));
            if (ipAddress.endsWith("::")) {
                // if address ends with double colon add an empty field representing the omitted all-zero fields
                segmentsAsList.add("");
            } else if (ipAddress.startsWith("::") && !segmentsAsList.isEmpty()) {
                // if address starts with double colon and at least one field is present (e.g. "::1") the split returns
                // two initial empty segments, so remove the first segment
                segmentsAsList.remove(0);
            }
            segments = segmentsAsList.toArray(new String[segmentsAsList.size()]);
        }
        return segments;
    }

    /**
     * Returns if the given string is a valid literal (not enclosed in square brackets) IPv6 address.
     * 
     * @param ipAddress
     *            the string to be validated as a valid literal IPv6 address.
     * @return true if the string contains a valid IPv6 address, false otherwise.
     */
    private boolean isValidLiteralIPv6Address(final String ipAddress) {
        if (ipAddress == null || ipAddress.isEmpty()) {
            return false;
        }

        // address shall contain at least a single colon
        if (!ipAddress.contains(":")) {
            return false;
        }

        // address cannot start or end with a single colon
        if ((ipAddress.startsWith(":") && !ipAddress.startsWith("::")) || (ipAddress.endsWith(":") && !ipAddress.endsWith("::"))) {
            return false;
        }

        // only a double colon is allowed
        if (ipAddress.contains("::") && ipAddress.indexOf("::") != ipAddress.lastIndexOf("::")) {
            return false;
        }

        return hasValidIPv6AddressSegments(ipAddress);
    }

    /**
     * Auxiliary inner class to manage segment counters.
     */
    class NscsSegmentCounters {

        int numOfValidNotEmptyIPv6Segments;

        public NscsSegmentCounters() {
            this.numOfValidNotEmptyIPv6Segments = 0;
        }

        /**
         * @return the numOfValidNotEmptyIPv6Segments
         */
        public int getNumOfValidNotEmptyIPv6Segments() {
            return numOfValidNotEmptyIPv6Segments;
        }

        /**
         * @param increment
         *            the increment to add to the numOfValidNotEmptyIPv6Segments.
         */
        public void incrementNumOfValidNotEmptyIPv6Segments(int increment) {
            this.numOfValidNotEmptyIPv6Segments += increment;
        }
    }
}
