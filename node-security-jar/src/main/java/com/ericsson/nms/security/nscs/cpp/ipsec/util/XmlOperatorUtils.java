package com.ericsson.nms.security.nscs.cpp.ipsec.util;

import com.ericsson.nms.security.nscs.api.exception.InvalidFileContentException;
import com.ericsson.nms.security.nscs.api.exception.IpSecActionException;
import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes;
import com.ericsson.nms.security.nscs.cpp.ipsec.summaryfile.xml.AutoIntegrationRbsSummaryFile;
import com.ericsson.nms.security.nscs.cpp.ipsec.summaryfile.xml.AutoIntegrationRbsSummaryFile.ConfigurationFiles;
import com.ericsson.nms.security.nscs.cpp.ipsec.summaryfile.xml.AutoIntegrationRbsSummaryFile.Format;
import com.ericsson.oss.itpf.sdk.resources.Resource;
import com.ericsson.oss.itpf.sdk.resources.Resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utils to generate the Setting and summary file content
 *  @author ediniku
 */
public class XmlOperatorUtils {

    private static final Logger LOG = LoggerFactory.getLogger(XmlOperatorUtils.class);
    private static final String FORMAT = "F";

    /**
     * Generate IpForOamSetting.xml content
     *
     * @param userInputXml
     * @return content of the file
     */
    public String generateIpForOamSettingFile(final String userInputXml, final String xslPathUri) throws IpSecActionException{
        final InputStream xslIs = this.getResourcesAsStream(xslPathUri);

        final StreamSource xslStream = new StreamSource(xslIs);
        Transformer transformer = null;
        String settingString = null;
        StringWriter writer = null;
        final TransformerFactory factory = TransformerFactory.newInstance();
        try {
            transformer = factory.newTransformer(xslStream);
            final StreamSource in = new StreamSource(new ByteArrayInputStream(userInputXml.getBytes(StandardCharsets.UTF_8)));
            writer = new StringWriter();
            final StreamResult out = new StreamResult(writer);
            transformer.transform(in, out);
            settingString = writer.getBuffer().toString();
        } catch (TransformerException e) {
            LOG.error(e.getMessage());
            throw new IpSecActionException(NscsErrorCodes.SETTING_FILE_GENERATION_FAILED);
        }  finally {
            Resources.safeClose(xslIs);
            Resources.safeClose(writer);
        }

        LOG.debug("The generated XML is : {}", settingString);
        return settingString;
    }

    /**
     * Creates the content of the Summary File
     *
     * @param settingXml Setting File contents
     * @param algorithm Algorithm
     * @return Object of the SummaryXml
     */
    public SummaryXmlInfo getSummaryFileContent(final String settingXml, final String smrsUri, final String algorithm) throws IpSecActionException{
        SummaryXmlInfo summaryXmlInfo = null;
        try {
            final AutoIntegrationRbsSummaryFile summaryFileObj = prepareSummaryXmlObj(smrsUri, getXmlHash(settingXml, algorithm));
            final String summaryXml = createSummaryXMLFile(summaryFileObj);
            final String summaryFileHash = getXmlHash(summaryXml, algorithm);
            summaryXmlInfo = new SummaryXmlInfo(summaryXml, summaryFileHash);
        } catch (NoSuchAlgorithmException e) {
            LOG.error("{} is not valid algorithm.", algorithm);
            throw new IpSecActionException(NscsErrorCodes.INVALID_ALGORITHM);
        }
        LOG.debug("summaryXmlInfo -> {}",summaryXmlInfo);
        return summaryXmlInfo;
    }

    /**
     * Builds Format
     *
     * @return Format
     */
    private Format buildFormat() {
        final Format format = new Format();
        format.setRevision(FORMAT);
        return format;
    }

    /**
     * Prepare Summary xml JAXB Object
     *
     * @param settingFileName Setting File Content
     * @param settingHash Hash code for setting file
     * @return AutoIntegrationRbsSummaryFile
     */
    private AutoIntegrationRbsSummaryFile prepareSummaryXmlObj(final String settingFileName, final String settingHash) {
        final ConfigurationFiles configFiles = new ConfigurationFiles();
        configFiles.setIpForOamSettingFileHash(settingHash);
        configFiles.setIpForOamSettingFilePath(settingFileName);

        final AutoIntegrationRbsSummaryFile summaryFileObj = new AutoIntegrationRbsSummaryFile();
        summaryFileObj.setFormat(buildFormat());
        summaryFileObj.setConfigurationFiles(configFiles);
        return summaryFileObj;
    }

    /**
     * Prepare Summary File Hash
     *
     * @param xml  XML
     * @param algorithm Algorithm
     * @return Hash code of XML
     * @throws java.security.NoSuchAlgorithmException
     * @throws java.io.IOException
     */
    private String getXmlHash(final String xml, final String algorithm) throws NoSuchAlgorithmException{
        final MessageDigest md = MessageDigest.getInstance(algorithm);
        final byte[] fileContent = xml.getBytes();
        final byte[] digest = md.digest(fileContent);
        return "SHA1=" + byteArrayToString(digest, true);

    }





    /**
     * Converts byte array to String
     *
     * @param arr Byte Array
     * @param withColon  WithColon
     * @return String
     */
    public static String byteArrayToString(final byte[] arr, final boolean withColon) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            final int j = 0xff & arr[i];
            if (j < 0x10) {
                sb.append("0");
            }
            sb.append(Integer.toHexString(j).toUpperCase());
            if (withColon && i + 1 < arr.length) {
                sb.append(":");
            }
        }
        return sb.toString();
    }


    /**
     * This will create a summary file from JAXB summary object.
     *
     * @param summaryFileObj AutoIntegrationRbsSummaryFile
     * @return content of summary file
     */
    private String createSummaryXMLFile(final AutoIntegrationRbsSummaryFile summaryFileObj) throws IpSecActionException {

        String summaryXml = null;
        StringWriter writer = null;
        try {
            final JAXBContext context = JAXBContext.newInstance(AutoIntegrationRbsSummaryFile.class);
            final Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            writer = new StringWriter();
            final StreamResult out = new StreamResult(writer);
            m.marshal(summaryFileObj, out);
            summaryXml = writer.getBuffer().toString();
            LOG.debug("Summary File -> {}",summaryXml);
        } catch (final JAXBException e) {
            LOG.error("JAXBException {} ", e.getMessage());
            throw new IpSecActionException(NscsErrorCodes.SUMMARY_FILE_GENERATION_FAILED);
        } finally {
            Resources.safeClose(writer);
        }
        return summaryXml;
    }

    /**
     *  Holds the Summary File content and hash
     *
     */
    public class SummaryXmlInfo {
        final private String content;
        final private String hash;

        public SummaryXmlInfo(final String content, final String hash) {
            this.content = content;
            this.hash = hash;
        }

        public String getContent() {
            return content;
        }

        public String getHash() {
            return hash;
        }
    }

    /**
     * Fetches InputStream of the file from the path provided
     * @param resourcePath ResourcePath
     * @return InputStream
     */
    public InputStream getResourcesAsStream(final String resourcePath) {
        LOG.info("Reading resource from classpath using SF: " + resourcePath);
        final Resource xmlResource = Resources.getClasspathResource(resourcePath);
        InputStream xslIs = null;
        if (xmlResource.exists()) {
            xslIs = xmlResource.getInputStream();
        }
        return xslIs;
    }
    
    /**
     * Method to convert Jaxb object to xml stream and returns byte array output stream.
     * 
     * @param objectInfo
     *            input object to convert xml
     * @param clazz
     *            input provided by user to create jaxbContext object
     * @throws JAXBException
     *             thrown when doing marshaling JAXB object to xml
     */

    public <T> ByteArrayOutputStream convertObjectToXmlStream(final Object objectInfo, Class<T> clazz) throws JAXBException {
        try {
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            final JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(objectInfo, byteArrayOutputStream);

            return byteArrayOutputStream;
        } catch (JAXBException jaxBException) {
            LOG.error("Error Occured while creating JAXB Content for enrollment info {} ", jaxBException);
            throw new JAXBException("Error Occured while creating JAXB Content for enrollment info");
        }
    }

    /**
     * Converts the input XML file as per the format provided in XSL
     *
     * @param userInputXml
     *            input XML in String
     * @param xslPathUri
     *            XSL file path URI
     * @return input xml converted as per the xsl format
     */
    public String transformXmlSchema(final String userInputXml, final String xslPathUri) {

        String settingString = null;

        try (final InputStream xslIs = this.getResourcesAsStream(xslPathUri); StringWriter writer = new StringWriter();) {
            final TransformerFactory factory = TransformerFactory.newInstance();
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            final Transformer transformer = factory.newTransformer(new StreamSource(xslIs));
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            final StreamSource in = new StreamSource(new ByteArrayInputStream(userInputXml.getBytes(StandardCharsets.UTF_8)));
            final StreamResult out = new StreamResult(writer);
            transformer.transform(in, out);
            settingString = writer.getBuffer().toString();
        } catch (TransformerException | IOException e) {
            LOG.error(NscsErrorCodes.SETTING_FILE_GENERATION_FAILED, e.getMessage());
            throw new InvalidFileContentException(e);
        }

        LOG.debug("The generated XML is : {}", settingString);
        return settingString;
    }
}