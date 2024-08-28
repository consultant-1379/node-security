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

import com.ericsson.nms.security.nscs.api.enums.SecurityLevel;
import com.ericsson.nms.security.nscs.api.iscf.IpsecArea;
import com.ericsson.nms.security.nscs.iscf.dto.CertFileDto;
import com.ericsson.nms.security.nscs.iscf.dto.EnrollmentDataDto;
import com.ericsson.nms.security.nscs.iscf.dto.IpsecEnrollmentDataDto;
import com.ericsson.nms.security.nscs.iscf.dto.SecEnrollmentDataDto;
import com.ericsson.nms.security.nscs.iscf.xml.Body;
import com.ericsson.nms.security.nscs.iscf.xml.CertFile;
import com.ericsson.nms.security.nscs.iscf.xml.EnrollmentData;
import com.ericsson.nms.security.nscs.iscf.xml.Ipsec;
import com.ericsson.nms.security.nscs.iscf.xml.IpsecEnrollmentData;
import com.ericsson.nms.security.nscs.iscf.xml.IpsecFiles;
import com.ericsson.nms.security.nscs.iscf.xml.Level2;
import com.ericsson.nms.security.nscs.iscf.xml.Level2ConfigSettings;
import com.ericsson.nms.security.nscs.iscf.xml.Level2Files;
import com.ericsson.nms.security.nscs.iscf.xml.SecConfData;
import com.ericsson.nms.security.nscs.iscf.xml.SecEnrollmentData;
import com.ericsson.nms.security.nscs.iscf.xml.Security;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.bind.MarshalException;
import javax.xml.bind.Marshaller;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Class for generating valid ISCF XML data for node auto integration with Security Level and
 * IPSec options
 *
 * @author ealemca
 */
public class CombinedIscfCreator extends IscfCreator {

    @Override
    public byte[] create(final NodeAIData data) throws MarshalException,
            JAXBException,
            SAXParseException,
            SAXException,
            URISyntaxException,
            UnsupportedEncodingException,
            NoSuchAlgorithmException,
            InvalidKeyException {
        log.info("Creating ISCF XML content for node {}", data.getFdn());

        final Body body = new Body();
        body.setMetaData(getMetaData(data));
        body.setSecurity(getSecurity(data));
        for(IpsecArea area : data.getIpsecAreas()) {
            body.getIpsec().add(getIpsec(area, data));
        }

        final Marshaller ma = initMarshaller();
        final ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        ma.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
        ma.marshal(body, bOut);
        final String bodyText = bOut.toString(IscfConstants.UTF8_CHARSET);
        ma.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.FALSE);
        bOut.reset();

        final SecConfData secConfData = new SecConfData();
        secConfData.setFileFormatVersion(IscfConstants.FILE_FORMAT_VERSION);

        secConfData.setBody(body);
        secConfData.setValidators(getValidators(data, bodyText));

        ma.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        ma.marshal(secConfData, bOut);
        return bOut.toByteArray();

    }

    private Security getSecurity(final NodeAIData data) {
        final Security security = new Security();
        security.setMinimumSecurityLevel(parseSecurityLevel(data.getMinimumSecLevel()));
        security.setWantedSecurityLevel(parseSecurityLevel(data.getWantedSecLevel()));
        security.setFileTransferClient(config.getFileTransferClientMode());
        security.setTelnetAndFtpServers(config.getTelnetAndFtpServersMode());
        security.setUserLabel("");
        if(SecurityLevel.getSecurityLevel(Integer.toString(security.getWantedSecurityLevel()))==SecurityLevel.LEVEL_2){
            security.setLevel2(getLevel2(data));
        }
        // TODO: implement this in 15B
        // security.setLevel3(getLevel3(data));
        return security;
    }

    private Level2 getLevel2(final NodeAIData data) {
        final Level2 level2 = new Level2();
        level2.setLevel2ConfigSettings(getLevel2ConfigSettings(data));
        level2.setLevel2Files(getLevel2Files(data));
        level2.setSecEnrollmentData(getSecEnrollmentData(data));
        return level2;
    }

    private Level2Files getLevel2Files(final NodeAIData data) {
        final Level2Files level2Files = new Level2Files();
        for (CertFile certFile: populateSecLevelCertFileList(data) ){
            level2Files.getCertFile().add(certFile);
        }
        return level2Files;
    }

    private List<CertFile> populateSecLevelCertFileList(final NodeAIData data) {
        final List<CertFile> certFileList = new ArrayList<>();
        final List<CertFileDto> certFileDtoList = data.getSecLevelCertFileDtos();

        return getCertFilesFromDtos(certFileList, certFileDtoList);
    }

    private Level2ConfigSettings getLevel2ConfigSettings(final NodeAIData data) {
        final Level2ConfigSettings level2ConfigSettings = new Level2ConfigSettings();
        level2ConfigSettings.setCertExpirWarnTime(90);
        level2ConfigSettings.setInstallTrustedCertDuration(30);
        level2ConfigSettings.getLogonServerAddress().add(data.getLogonServerAddress());
        return level2ConfigSettings;
    }

    private SecEnrollmentData getSecEnrollmentData(final NodeAIData data) {
        final SecEnrollmentDataDto secDto = data.getEnrollmentDto();
        final EnrollmentData enrollmentData = getEnrollmentData(secDto);

        final SecEnrollmentData secEnrollmentData = new SecEnrollmentData();
        secEnrollmentData.setEnrollmentData(enrollmentData);
        return secEnrollmentData;
    }

    private Ipsec getIpsec(final IpsecArea area, final NodeAIData data) {

        final Ipsec ipsec = new Ipsec();
        ipsec.setType(area.toString());
        ipsec.setUserLabel(data.getIpsecUserLabel());
        ipsec.setCertExpirWarnTime(data.getIpsecCertExpirWarnTime());
        ipsec.setIpsecFiles(getIpsecFiles(data));
        ipsec.setIpsecEnrollmentData(getIpsecEnrollmentData(data));
        return ipsec;
    }

    private IpsecFiles getIpsecFiles(final NodeAIData data) {
        final IpsecFiles ipsecFiles = new IpsecFiles();
        for (CertFile certFile: populateIpsecCertFileList(data) ){
            ipsecFiles.getCertFile().add(certFile);
        }
        return  ipsecFiles;
    }

    private List<CertFile> populateIpsecCertFileList(final NodeAIData data) {
        final List<CertFile> certFileList = new ArrayList<>();
        final List<CertFileDto> certFileDtoList = data.getIpsecCertFileDtos();

        return getCertFilesFromDtos(certFileList, certFileDtoList);
    }

    private IpsecEnrollmentData getIpsecEnrollmentData(final NodeAIData data){
        final IpsecEnrollmentDataDto ipsecEnrollmentDataDto = data.getIpsecEnrollmentDataDto();

        ipsecEnrollmentDataDto.setEnrollmentData(data.getIpsecEnrollmentDataDto().getEnrollmentData());
        final EnrollmentData enrollmentData = getEnrollmentData(ipsecEnrollmentDataDto);

        final IpsecEnrollmentData ipsecEnrollmentData = new IpsecEnrollmentData();
        ipsecEnrollmentData.setSubjectAltName(data.getSubjectAltName().toString());
        ipsecEnrollmentData.setSubjectAltNameType(data.getSubjectAltNameFormat().toInt());
        ipsecEnrollmentData.setEnrollmentData(enrollmentData);
        return ipsecEnrollmentData;
    }

    private EnrollmentData getEnrollmentData(final IpsecEnrollmentDataDto secDto) {
        final EnrollmentDataDto enrollmentDto = secDto.getEnrollmentData();
        return getEnrollmentDataDetails(enrollmentDto);
    }

    private EnrollmentData getEnrollmentData(final SecEnrollmentDataDto secDto) {
        final EnrollmentDataDto enrollmentDto = secDto.getEnrollmentData();
        return getEnrollmentDataDetails(enrollmentDto);
    }

    private Integer parseSecurityLevel(final SecurityLevel secLevel) {
        return Integer.parseInt(secLevel.toString());
    }

}
