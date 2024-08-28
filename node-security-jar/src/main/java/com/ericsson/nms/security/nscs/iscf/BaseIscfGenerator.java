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

import com.ericsson.nms.security.nscs.api.exception.IscfServiceException;
import com.ericsson.nms.security.nscs.api.iscf.IscfResponse;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityServiceException;
import com.ericsson.nms.security.smrs.api.exception.SmrsDirectoryException;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;

import javax.inject.Inject;
import javax.xml.bind.JAXBException;
import javax.xml.bind.MarshalException;

import org.slf4j.Logger;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Base class for generating ISCF XML content, an RBS Integrity Code (RIC) and
 * the associated Security Configuration Checksum (SCC). This class implements the
 * <code>generate()</code> from the {@link IscfGenerator} interface and provides
 * abstract methods for generating an RBS Integrity Code, retrieving the required
 * data for generating ISCF content and for generating the Security Configuration
 * Checksum for a particular use case (Security Level and/or IPSec)
 *
 * @author ealemca
 */
public abstract class BaseIscfGenerator implements IscfGenerator {

    @Inject
    private Logger log;

    /**
     * Generate ISCF XML content, an RBS Integrity Code (RIC)
     * and a Security Configuration Checksum (SCC)
     *
     * @return IscfResponse A response object containing the XML content,
     *         the RIC and the SCC
     * @throws IscfServiceException
     */
    @Override
    public IscfResponse generate() throws IscfServiceException {
        byte[] rbsIntegrityCode = null, xmlContent = null;
        String securityConfigChecksum;
        String ricString;
        try {
            ricString = getRbsIntegrityCode();
            rbsIntegrityCode = ricString.getBytes(IscfConstants.UTF8_CHARSET);
            final NodeAIData data = getNodeAIData(rbsIntegrityCode);
            xmlContent = getIscfContent(data);
            securityConfigChecksum = getSecurityConfigChecksum(data);
        } catch (SecurityLevelNotSupportedException |
                InvalidNodeAIDataException |
                UnsupportedEncodingException |
                CppSecurityServiceException |
                SmrsDirectoryException |
                UnknownHostException | IscfEncryptionException |
//                CertificateEncodingException |
                JAXBException |
                SAXException |
                URISyntaxException |
                NoSuchAlgorithmException |
                InvalidKeyException |
                CertificateException e) {
            final String message = e.getMessage() == null ? "no message" : e.getMessage();
            log.warn("IscfService caught exception [{}] with message: [{}]. Re-throwing", 
                    e.getClass().toString(), message);
            throw new IscfServiceException(message);
        }
        final IscfResponse response = new IscfResponse();
        response.setIscfContent(xmlContent);
        response.setSecurityConfigChecksum(securityConfigChecksum);
        response.setRbsIntegrityCode(ricString);
        return response;
    }

    /**
     * Retrieves the data required to generate ISCF content from values provided by client and
     * trust and enrollment info from PKI.
     *
     * <b>N.B.</b> Classes implementing this method must have called <code>initGenerator(..)</code>
     * first before using this method
     *
     * @param rbsIntegrityCode
     * @return
     * @throws UnsupportedEncodingException
     * @throws CppSecurityServiceException
     * @throws SmrsDirectoryException
     * @throws UnknownHostException
     * @throws IscfEncryptionException
     * @throws CertificateEncodingException 
     * @throws java.security.NoSuchAlgorithmException 
     */
    protected abstract NodeAIData getNodeAIData(final byte[] rbsIntegrityCode)
            throws UnsupportedEncodingException,
            CppSecurityServiceException,
            SmrsDirectoryException,
            UnknownHostException,
            IscfEncryptionException,
            CertificateEncodingException,
            NoSuchAlgorithmException,            
            CertificateException;

    /**
     * Generates a secure random String to be used as a key for encrypting ISCF data.
     *
     * <b>N.B.</b> Classes implementing this method must have called <code>initGenerator(..)</code>
     * first before using this method
     *
     * @return 
     */
    protected abstract String getRbsIntegrityCode();

    /**
     * Generates ISCF XML content validated against the appropriate XSD for ISCF.
     * 
     * Content is based on data gathered from client and trust/enrollment info gathered from PKI.
     * <b>N.B.</b> Classes implementing this method must have called <code>initGenerator(..)</code>
     * and<code>getNodeAIData(..)</code> first before using this method
     *
     * @param data
     * @return
     * @throws UnsupportedEncodingException
     * @throws MarshalException
     * @throws JAXBException
     * @throws SAXParseException
     * @throws SAXException
     * @throws URISyntaxException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException 
     */
    protected abstract byte[] getIscfContent(final NodeAIData data)
            throws UnsupportedEncodingException,
            MarshalException,
            JAXBException,
            SAXParseException,
            SAXException,
            URISyntaxException,
            NoSuchAlgorithmException,
            InvalidKeyException;

    /**
     * Generates a Security Configuration Checksum based on CPP interface requirements.
     *
     * <b>N.B.</b> Classes implementing this method must have called <code>initGenerator(..)</code>
     * and<code>getNodeAIData(..)</code> first before using this method
     *
     * <p>
     * Reference: <a href="http://erilink.ericsson.se/eridoc/erl/objectId/09004cff87876ace?docno=EAB/FJK-09:0037Uen&format=msw8">
     *  CPPSecActivation4AutoIntegration.doc
     *  </a>
     * </p>
     *
     * @param data
     * @return
     * @throws NoSuchAlgorithmException
     * @throws CertificateEncodingException
     * @throws UnsupportedEncodingException 
     */
    protected abstract String getSecurityConfigChecksum(final NodeAIData data)
            throws NoSuchAlgorithmException,
            CertificateEncodingException,
            UnsupportedEncodingException;

}
