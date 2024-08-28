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
import com.ericsson.nms.security.nscs.iscf.dto.EnrollmentDataDto;
import com.ericsson.nms.security.nscs.iscf.xml.CertFile;
import com.ericsson.nms.security.nscs.iscf.xml.EncryptedContent;
import com.ericsson.nms.security.nscs.iscf.xml.EnrollmentData;
import com.ericsson.nms.security.nscs.iscf.xml.ISCFEncryptedContent;
import com.ericsson.nms.security.nscs.iscf.xml.MetaData;
import com.ericsson.nms.security.nscs.iscf.xml.SecConfData;
import com.ericsson.nms.security.nscs.iscf.xml.Validators;
import com.ericsson.nms.security.nscs.util.CertDetails;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.MarshalException;
import javax.xml.bind.Marshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.slf4j.Logger;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Base class for generating valid ISCF XML data for node auto integration
 *
 * @author ealemca
 */
public abstract class IscfCreator {

    @Inject
    protected Logger log;

    @Inject
    protected IscfConfigurationBean config;

    @Inject
    private IscfValidatorsGenerator checksumGen;

    /**
     * Creates XML data validated against the ISCF XSD schema based on auto
     * integration data gathered from the user and the PKI services
     *
     * @param data
     * @return a byte array containing the validated XML content
     * @throws javax.xml.bind.MarshalException
     * @throws org.xml.sax.SAXParseException
     * @throws java.net.URISyntaxException
     * @throws java.io.UnsupportedEncodingException
     * @throws java.security.NoSuchAlgorithmException
     * @throws java.security.InvalidKeyException
     */
    public abstract byte[] create(final NodeAIData data)  throws MarshalException,
            JAXBException,
            SAXParseException,
            SAXException,
            URISyntaxException,
            UnsupportedEncodingException,
            NoSuchAlgorithmException,
            InvalidKeyException;

    protected Marshaller initMarshaller() throws JAXBException, SAXException {
        final JAXBContext ctx = JAXBContext.newInstance(SecConfData.class);
        final Marshaller ma = ctx.createMarshaller();
        ma.setSchema(createSchema(IscfConstants.ISCF_XSD_FILENAME));
        ma.setEventHandler(new IscfSchemaValidationEventHandler());
        return ma;
    }

    protected Schema createSchema(final String filename) throws SAXException {
        final SchemaFactory sf = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
        final URL schemaUrl = Thread.currentThread().getContextClassLoader().getResource(filename);
        log.trace("Validation schema loaded from getResource(): {}", schemaUrl);
        return sf.newSchema(schemaUrl);
    }

    protected MetaData getMetaData(final NodeAIData data) {
        final CertificateTimes certTimes = new CertificateTimes();
        final MetaData metaData = new MetaData();
        metaData.setLogicalName(data.getLogicalName());
        metaData.setCreationTime(certTimes.getCreationTime());
        metaData.setNotValidAfter(certTimes.getValidityPeriod());
        return metaData;
    }

    protected Validators getValidators(final NodeAIData data, final String bodyText)
            throws UnsupportedEncodingException,
            NoSuchAlgorithmException,
            InvalidKeyException {
        final Validators validators = new Validators();
        final int startIndex = bodyText.indexOf("<body>");
        final int endIndex = bodyText.indexOf("</body>");
        String bodyTextFiltered = bodyText.substring(startIndex, endIndex + 7);
        bodyTextFiltered = bodyTextFiltered.replaceAll("\\s", "");
        
        // Generate HASH digst
        final byte[] hashContent = checksumGen.getChecksum(bodyTextFiltered);
        
        // Generate HMAC
        final String ricString = new String(data.getRic());
        final byte[] hmacKey = checksumGen.getChecksum(ricString);
        final byte[] hmacContent = checksumGen.getHmac(bodyTextFiltered, hmacKey);
        validators.setHash(hashContent);
        validators.setHmac(hmacContent);
        return validators;
    }


    protected List<CertFile> getCertFilesFromDtos(
            final List<CertFile> certFileList,
            final List<CertFileDto> certFileDtoList
    ) {
        for (CertFileDto dto : certFileDtoList) {
            final EncryptedContent ec = new EncryptedContent();
            ec.setPBKDF2IterationCount(dto.getEncryptedContent().getPBKDF2IterationCount());
            ec.setPBKDF2Salt(dto.getEncryptedContent().getPBKDF2Salt());
            ec.setValue(dto.getEncryptedContent().getValue());

            final CertFile certFile = new CertFile();
            certFile.setCategory(dto.getCategory());
            certFile.setCertFingerprint(dto.getCertFingerprint());
            certFile.setCertSerialNumber(dto.getCertSerialNumber());
            certFile.setEncryptedContent(ec);

            certFileList.add(certFile);
        }
        return certFileList;
    }

    protected EnrollmentData getEnrollmentDataDetails(final EnrollmentDataDto enrollmentDto) {
        final EncryptedContent secEncryptedContent = new EncryptedContent();
        secEncryptedContent.setPBKDF2IterationCount(
                enrollmentDto
                        .getDataChallengePassword()
                        .getEncryptedContent()
                        .getPBKDF2IterationCount()
        );
        secEncryptedContent.setPBKDF2Salt(
                enrollmentDto
                        .getDataChallengePassword()
                        .getEncryptedContent()
                        .getPBKDF2Salt()
        );
        secEncryptedContent.setValue(
                enrollmentDto
                        .getDataChallengePassword()
                        .getEncryptedContent()
                        .getValue()
        );

        final ISCFEncryptedContent dataChallengePassword = new ISCFEncryptedContent();
        dataChallengePassword.setEncryptedContent(secEncryptedContent);

        final EnrollmentData enrollmentData = new EnrollmentData();
        enrollmentData.setDataChallengePassword(dataChallengePassword);
        enrollmentData.setCAFingerprint(enrollmentDto.getCAFingerprint());
        enrollmentData.setDistinguishedName(enrollmentDto.getDistinguishedName());
        enrollmentData.setEnrollmentServerURL(enrollmentDto.getEnrollmentServerURL());
        enrollmentData.setEnrollmentMode(enrollmentDto.getEnrollmentMode());
        enrollmentData.setKeyLength(enrollmentDto.getKeyLength());
        enrollmentData.setCertificateAuthorityDn(CertDetails.getBcX500Name(enrollmentDto.getCertificateAuthorityDn()));
        enrollmentData.setEnrollmentTimeLimit(enrollmentDto.getEnrollmentTimeLimit());
        return enrollmentData;
    }

    /**
     * Utility class to encapsulate the certificate validity times for ISCF XML
     * generation during auto-integration of a node. It models the 'creationTime'
     * and 'notValidAfter' values in the XML meta-data
     *
     */
    protected class CertificateTimes {

        SimpleDateFormat sdf;
        TimeZone timeZone;
        Calendar cal;
        long creationTimeInMillis;

        CertificateTimes() {
            sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            timeZone = TimeZone.getTimeZone("UTC");
            cal = Calendar.getInstance(timeZone);
            sdf.setTimeZone(timeZone);
            creationTimeInMillis = cal.getTimeInMillis();
        }

        String getCreationTime() {
            cal.setTimeInMillis(creationTimeInMillis);
            return sdf.format(cal.getTime());
        }

        String getValidityPeriod() {
            cal.setTimeInMillis(creationTimeInMillis);
            cal.add(Calendar.DATE, config.getValidityPeriodInDays());
            return sdf.format(cal.getTime());
        }

    }

}
