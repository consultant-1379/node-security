/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.03.13 at 07:17:27 PM IST 
//


package com.ericsson.nms.security.nscs.handler.ciphersconfig.entities;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.ericsson.nms.security.nscs.handler.ciphersconfig.entities package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Cipher_QNAME = new QName("", "cipher");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.ericsson.nms.security.nscs.handler.ciphersconfig.entities
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link CiphersConfiguration }
     * 
     */
    public CiphersConfiguration createCiphersConfiguration() {
        return new CiphersConfiguration();
    }

    /**
     * Create an instance of {@link NodeCiphers }
     * 
     */
    public NodeCiphers createNodeCiphers() {
        return new NodeCiphers();
    }

    /**
     * Create an instance of {@link MacCiphers }
     * 
     */
    public MacCiphers createMacCiphers() {
        return new MacCiphers();
    }

    /**
     * Create an instance of {@link KeyExchangeCiphers }
     * 
     */
    public KeyExchangeCiphers createKeyExchangeCiphers() {
        return new KeyExchangeCiphers();
    }

    /**
     * Create an instance of {@link SshProtocol }
     * 
     */
    public SshProtocol createSshProtocol() {
        return new SshProtocol();
    }

    /**
     * Create an instance of {@link Nodes }
     * 
     */
    public Nodes createNodes() {
        return new Nodes();
    }

    /**
     * Create an instance of {@link EncryptCiphers }
     * 
     */
    public EncryptCiphers createEncryptCiphers() {
        return new EncryptCiphers();
    }

    /**
     * Create an instance of {@link TlsProtocol }
     * 
     */
    public TlsProtocol createTlsProtocol() {
        return new TlsProtocol();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "cipher")
    public JAXBElement<String> createCipher(String value) {
        return new JAXBElement<String>(_Cipher_QNAME, String.class, null, value);
    }

}
