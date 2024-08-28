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

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.util.Set;

import javax.inject.Inject;
import javax.xml.bind.JAXBException;
import javax.xml.bind.MarshalException;

import org.slf4j.Logger;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.iscf.BaseSubjectAltNameDataType;
import com.ericsson.nms.security.nscs.api.iscf.IpsecArea;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameFormat;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameParam;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityServiceException;
import com.ericsson.nms.security.smrs.api.exception.SmrsDirectoryException;

/**
 * Generates XML content and checksum for IPSec only ISCF
 *
 * @author ealemca
 */
public class IpsecIscfGenerator extends BaseIscfGenerator {

    @Inject
    private Logger log;

    @Inject
    private RicGenerator ricGenerator;

    @Inject
    private IpsecDataCollector dataCollector;

    @Inject
    private IpsecIscfCreator creator;

    @Inject
    IpsecChecksumGenerator checksum;

    private String fdn;
    private String logicalName;
    private String userLabel;
    private BaseSubjectAltNameDataType subjectAltName;
    private SubjectAltNameFormat subjectAltNameFormat;
    private Set<IpsecArea> wantedIpsecAreas;
    private EnrollmentMode wantedEnrollmentMode;
    private NodeModelInformation modelInfo;

    /**
     * Initializes this implementation of {@link IscfGenerator} with all the values required
     * to fetch auto integration data, generate RBS Integrity Code, generate ISCF content and
     * generate the Security Configuration Checksum
     *
     * @param fdn
     * @param logicalName
     * @param userLabel
     * @param subjectAltName
     * @param wantedIpsecAreas 
     * @param wantedEnrollmentMode
     * @param modelInfo
     */
    public void initGenerator(
            final String fdn,
            final String logicalName,
            final String userLabel,
            final SubjectAltNameParam subjectAltName,
            final Set<IpsecArea> wantedIpsecAreas,
            final EnrollmentMode wantedEnrollmentMode,
            final NodeModelInformation modelInfo
    ) {
        this.fdn = fdn;
        this.logicalName = logicalName;
        this.userLabel = userLabel;
        this.subjectAltName = subjectAltName.getSubjectAltNameData();
        this.subjectAltNameFormat = subjectAltName.getSubjectAltNameFormat();
        this.wantedIpsecAreas = wantedIpsecAreas;
        this.wantedEnrollmentMode = wantedEnrollmentMode;
        this.modelInfo = modelInfo;
    }

    @Override
    protected NodeAIData getNodeAIData(final byte[] rbsIntegrityCode)
            throws UnsupportedEncodingException,
            CppSecurityServiceException,
            SmrsDirectoryException,
            UnknownHostException,
            IscfEncryptionException,
            CertificateEncodingException,
            UnsupportedEncodingException,
            NoSuchAlgorithmException,
            InvalidNodeAIDataException,
            CertificateException {
        log.debug("Gathering auto integration data for node {}", fdn);
        return dataCollector.getNodeAIData(
                this.fdn,
                this.logicalName,
                this.userLabel,
                this.subjectAltName,
                this.subjectAltNameFormat,
                this.wantedIpsecAreas,
                this.wantedEnrollmentMode,
                this.modelInfo,
                rbsIntegrityCode);
    }

    @Override
    protected String getRbsIntegrityCode() {
        return ricGenerator.generateRIC();
    }

    @Override
    protected byte[] getIscfContent(final NodeAIData data)
            throws UnsupportedEncodingException,
            MarshalException,
            JAXBException,
            SAXParseException,
            SAXException,
            URISyntaxException,
            NoSuchAlgorithmException,
            InvalidKeyException {
        return creator.create(data);
    }

    @Override
    protected String getSecurityConfigChecksum(final NodeAIData data)
            throws NoSuchAlgorithmException, CertificateEncodingException, UnsupportedEncodingException{
        return checksum.getSecurityConfigChecksum(data);
    }

}
