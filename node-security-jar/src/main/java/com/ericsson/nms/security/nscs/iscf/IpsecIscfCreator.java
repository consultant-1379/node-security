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

import com.ericsson.nms.security.nscs.api.iscf.IpsecArea;
import com.ericsson.nms.security.nscs.iscf.dto.CertFileDto;
import com.ericsson.nms.security.nscs.iscf.dto.EnrollmentDataDto;
import com.ericsson.nms.security.nscs.iscf.dto.IpsecEnrollmentDataDto;
import com.ericsson.nms.security.nscs.iscf.xml.Body;
import com.ericsson.nms.security.nscs.iscf.xml.CertFile;
import com.ericsson.nms.security.nscs.iscf.xml.EnrollmentData;
import com.ericsson.nms.security.nscs.iscf.xml.Ipsec;
import com.ericsson.nms.security.nscs.iscf.xml.IpsecEnrollmentData;
import com.ericsson.nms.security.nscs.iscf.xml.IpsecFiles;
import com.ericsson.nms.security.nscs.iscf.xml.SecConfData;
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
 * Class for generating valid ISCF XML data for node auto integration with IPSec-only options
 *
 * @author ealemca
 */
public class IpsecIscfCreator extends IscfCreator {
    
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

}
