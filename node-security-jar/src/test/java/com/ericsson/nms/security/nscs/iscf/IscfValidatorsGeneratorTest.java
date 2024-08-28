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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.xml.serialize.XMLSerializer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 *
 * @author ealemca
 */
@RunWith(MockitoJUnitRunner.class)
public class IscfValidatorsGeneratorTest {

    @Spy
    private final Logger log = LoggerFactory.getLogger(IscfValidatorsGenerator.class);

    @InjectMocks
    private IscfValidatorsGenerator instance;

    @Test
    public void testGetChecksumGeneratesDifferentHashSimple() throws Exception {
        final String text = "Some text to hash";
        final String slightlyDifferentText = "some text to hash";
        assertFalse(text.equals(slightlyDifferentText));
        final String firstHash = bytesToBase64(instance.getChecksum(text));
        final String secondHash = bytesToBase64(instance.getChecksum(slightlyDifferentText));
        log.debug("Hash (Base64) of '{}': {}", text, firstHash);
        log.debug("Hash (Base64) of slighty different text: {}", secondHash);
        assertFalse(firstHash.equals(secondHash));
    }

    @Test
    public void testSameStringsProduceSameHash() throws Exception {
        final String text = "Some text to hash";
        final String sameText = "Some text to hash";
        assertTrue(text.equals(sameText));
        final String firstHash = bytesToBase64(instance.getChecksum(text));
        final String secondHash = bytesToBase64(instance.getChecksum(sameText));
        assertEquals(firstHash, secondHash);
    }

    @Test
    public void testNewHashGeneratorProducesSameHashAsLegacyCode() throws Exception {
        final String bodyXmlAsText = getBodyTextFromFile("src/test/resources/ISCF.xml");

        final String iscfXmlHash = bytesToBase64(instance.getChecksum(bodyXmlAsText));
        final String iscfXmlHashLegacy = bytesToBase64(getSHA1HashLegacy(bodyXmlAsText));

        log.debug("XML body new hash --> {}", iscfXmlHash);
        log.debug("XML body old hash --> {}", iscfXmlHashLegacy);

        assertEquals(iscfXmlHash, iscfXmlHashLegacy);
    }

    @Test
    public void testGetHmacSimple() throws Exception {
        final String keyText = "some_key";
        final String sampleText = "Some text to hash";
        final String sampleHash = bytesToBase64(instance.getChecksum(sampleText));

        final byte[] key = keyText.getBytes(IscfConstants.UTF8_CHARSET);
        final byte[] sampleHmac = instance.getHmac(sampleText, key);
        final byte[] legacyHmac = getHmacLegacy(sampleText, key);

        final String expected = "1RLAxt8lVv8E0azn8Qp6GRnjhoA=";
        final String actual = bytesToBase64(sampleHmac);
        final String legacy = bytesToBase64(legacyHmac);

        log.debug("New HMAC (Base64): {}", bytesToBase64(sampleHmac));
        log.debug("New HMAC again (Base64): {}", bytesToBase64(sampleHmac));
        log.debug("Legacy HMAC (Base64): {}", bytesToBase64(legacyHmac));
        log.debug("Legacy HMAC again (Base64): {}", bytesToBase64(legacyHmac));

        assertEquals(expected, actual);
        assertEquals(expected, legacy);
        assertEquals(legacy, actual);
    }

    @Test
    public void testGetHmacSampleISCFFile() throws Exception {
        final String bodyXmlAsText = getBodyTextFromFile("src/test/resources/ISCF.xml");
        final byte[] key = "some_key".getBytes(IscfConstants.UTF8_CHARSET);
        final String actualHmac = bytesToBase64(instance.getHmac(bodyXmlAsText, key));
        final String expectedHmac = "";
        final String legacyHmac = bytesToBase64(getHmacLegacy(bodyXmlAsText, key));
        log.debug("HMAC (Base64) of sample xml <body> content: {}", actualHmac);
        log.debug("Legacy HMAC (Base64) of sample xml <body> content: {}", actualHmac);

        assertEquals(legacyHmac, actualHmac);
    }

    private String getBodyTextFromFile(final String filePath) throws Exception {
        final String iscfXmlAsText = readFile(filePath, StandardCharsets.UTF_8);
        final Document legacyDoc = loadXMLFrom(iscfXmlAsText);
        final TransformerFactory transformerFactory = TransformerFactory.newInstance();
        final Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        final DOMSource source = new DOMSource(legacyDoc);
        final StringWriter writer = new StringWriter();
        transformer.transform(source, new StreamResult(writer));
        final Element rootElement = (Element) legacyDoc.getElementsByTagName("body").item(0);
        return getBodyText(rootElement);
    }

    @SuppressWarnings("restriction")
	private String getBodyText(final Element bodyElement) throws IOException {
        String bodyText;
        final ByteArrayOutputStream bas = new ByteArrayOutputStream();
		final XMLSerializer serializer = new XMLSerializer(bas, null);

        serializer.serialize(bodyElement);

        bodyText = bas.toString();
        final int startIndex = bodyText.indexOf("<body>");
        final int endIndex = bodyText.indexOf("</body>");
        bodyText = bodyText.substring(startIndex, endIndex + 7);
        bodyText = bodyText.replaceAll("\\s", "");
        return bodyText;
    }

    private Document loadXMLFrom(final String xml) throws SAXException, IOException {
        return loadXMLFrom(new java.io.ByteArrayInputStream(xml.getBytes()));
    }

    private Document loadXMLFrom(final InputStream is) throws SAXException, IOException {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);

        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (final ParserConfigurationException ex) {
        }
        final Document doc = builder.parse(is);
        is.close();
        return doc;
    }

    private byte[] getSHA1HashLegacy(final String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        final byte[] sha1hash = MessageDigest.getInstance("SHA-1").digest(text.getBytes("UTF-8"));
        return sha1hash;
    }

    private byte[] getHmacLegacy(final String text, final byte[] key) throws Exception {
        final Mac hMac = Mac.getInstance("HMacSHA1");
        final Key secretKey = new SecretKeySpec(key, 0, key.length, "HMacSHA1");
        hMac.init(secretKey);

        return hMac.doFinal(text.getBytes("UTF-8"));
    }

    private String bytesToBase64(final byte[] bytes) {
        return DatatypeConverter.printBase64Binary(bytes);
    }

    private String readFile(final String path, final Charset encoding)
            throws IOException {
        final byte[] encoded = Files.readAllBytes(Paths.get(path));
        return encoding.decode(ByteBuffer.wrap(encoded)).toString();
    }

}
