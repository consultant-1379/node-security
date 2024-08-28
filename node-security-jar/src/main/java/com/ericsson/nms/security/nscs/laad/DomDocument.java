/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ericsson.nms.security.nscs.laad;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This class contains utilities for creating and wrting DOM documents to file.
 * 
 * @author efalmag,enatbol
 */
class DomDocument {

    /**
     * Constructor for DomDocument.
     */
    DomDocument() {
    }

    /**
     * Creates and initializes a new DOM document.
     * 
     * @return a DOM document
     * @throws ParserConfigurationException
     *             if something went wrong during the creation of the document.
     */
    Document initDomDocument() throws ParserConfigurationException {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(true);
        final DocumentBuilder builder = factory.newDocumentBuilder();

        builder.setErrorHandler(new DefaultHandler());
        builder.setEntityResolver(new JarEntityResolver());

        final Document doc = builder.newDocument();

        return doc;
    }

    /**
     * Writes a DOM document to an OutputStream.
     * 
     * @param doc
     *            the DOM document to write to the Stream
     */
    void writeDomDocument(final Document doc, final Properties transProperties, final OutputStream os) throws TransformerFactoryConfigurationError, TransformerConfigurationException,
            TransformerException {
        final TransformerFactory tf = TransformerFactory.newInstance();
        final Transformer t = tf.newTransformer();

        t.setOutputProperties(transProperties);

        // Makes the transformer indent the document properly.
        t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        final StreamResult sr = new StreamResult(os);

        t.transform(new DOMSource(doc), sr);
    }

    static class JarEntityResolver extends DefaultHandler {

        /**
         * Resolves the SYSTEM property for the LAAD XML file.
         * 
         * @param publicId
         *            ignored in this implementation
         * @param systemId
         *            The path from the <code>/meta-inf/</code> directory in the jarfile
         * @return the input source stream to read the SYSTEM property from
         * @throws SAXException
         *             if the entity could not be found
         */
        @Override
        public InputSource resolveEntity(final String publicId, String systemId) throws SAXException {
            if (publicId == null || "-//Ericsson//DTD LAAD 1.0//EN".equals(publicId)) {
                systemId = "laadver1.dtd";
            } else {
                throw new SAXException("Unknown PUBLIC identifier: " + publicId);
            }

            final InputStream is = this.getClass().getResourceAsStream("/" + systemId);

            if (is == null) {
                throw new SAXException("Could not find system entity: " + systemId);
            } else {
                final InputSource inputSource = new InputSource(is);

                inputSource.setSystemId(systemId);
                inputSource.setPublicId(publicId);
                return inputSource;
            }
        }
    }
}
