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
package com.ericsson.nms.security.nscs.api.command.types;

/**
 * CiphersConfigCommand Class is used to get input parameters from set and get ciphers command.
 * 
 * @author xkumkam
 * 
 */
public class CiphersConfigCommand extends NscsNodeCommand {

    private static final long serialVersionUID = 7982181134596483786L;

    public static final String PROTOCOL_PROPERTY = "protocol";

    public static final String ENCRYPT_ALGOS_PROPERTY = "encryptalgos";

    public static final String KEX_PROPERTY = "keyexchangealgos";

    public static final String MACS_PROPERTY = "macalgos";

    public static final String CIPHER_FILTER_PROPERTY = "cipherfilter";
    
    public static final String XML_FILE_PROPERTY = "xmlfile";

    /**
     * @return the protocolTypeProperty
     */
    public String getProtocolProperty() {
        return getValueString(PROTOCOL_PROPERTY);
    }

    /**
     * @return the encryptAlgosProperty
     */
    public String getEncryptAlgosProperty() {
        return getValueString(ENCRYPT_ALGOS_PROPERTY);
    }

    /**
     * @return the kexProperty
     */
    public String getKexProperty() {
        return getValueString(KEX_PROPERTY);
    }

    /**
     * @return the macsProperty
     */
    public String getMacsProperty() {
        return getValueString(MACS_PROPERTY);
    }

    /**
     * @return the cipherFilterProperty
     */
    public String getCipherFilterProperty() {
        return getValueString(CIPHER_FILTER_PROPERTY);
    }
    
    
    /**
     * Convenience method to check if the given property exists in the property Map
     * 
     * @param property
     *            name of the property
     * @return true if Properties Map contains a property with the given name
     */
    public boolean hasProperty(final String property) {
        return this.getProperties().containsKey(property);
    }

}
