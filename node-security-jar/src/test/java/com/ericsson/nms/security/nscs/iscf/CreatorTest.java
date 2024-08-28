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

import com.ericsson.nms.security.nscs.iscf.dto.CertFileDto;
import com.ericsson.nms.security.nscs.iscf.dto.EncryptedContentDto;
import java.math.BigInteger;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Class containing methods common to both Security Level XML creator tests
 * and IPSec XML creator tests
 *
 * @author ealemca
 */
public class CreatorTest extends IscfTest {

    protected CertFileDto createCertFileDto(String content, String category) {
        EncryptedContentDto ec = new EncryptedContentDto();
        ec.setValue(content.getBytes());
        ec.setPBKDF2IterationCount(BigInteger.valueOf(1024L));
        ec.setPBKDF2Salt(content.getBytes());

        CertFileDto certFile = new CertFileDto();
        certFile.setCategory(category);
        certFile.setCertSerialNumber(ISCF_TEST_SERIAL_NUMBER);
        certFile.setCertFingerprint(ISCF_TEST_FINGERPRINT);
        certFile.setEncryptedContent(ec);

        return certFile;
    }

    protected void assertXMLIsValid(byte[] xmlOutput, String xmlOutputAsString) {
        assertTrue(xmlOutput.length > 0);
        assertTrue(
                String.format("XML output does not contain %s", XML_HEADER_STR),
                xmlOutputAsString.contains(XML_HEADER_STR)
        );
        assertTrue(
                String.format("XML output does not contain %s", ISCF_CLOSING_TAG),
                xmlOutputAsString.contains(ISCF_CLOSING_TAG)
        );
    }

    protected void assertXMLIsValidAndContainsString(byte[] xmlOutput, String xmlOutputAsString, String expected) {
        assertXMLIsValid(xmlOutput, xmlOutputAsString);
        assertTrue(
                String.format("XML output does not contain expected string: %s", expected),
                xmlOutputAsString.contains(expected)
        );
    }

    protected void assertXMLIsValidAndDoesNotContainString(byte[] xmlOutput, String xmlOutputAsString, String expected) {
        assertXMLIsValid(xmlOutput, xmlOutputAsString);
        assertFalse(
                String.format("XML output contains unexpected string: %s", expected),
                xmlOutputAsString.contains(expected)
        );
    }

}
