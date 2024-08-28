/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ericsson.nms.security.nscs.laad;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * <p>Encapsulates binary data related to LAAD authentication and authorization
 * information</p>
 * @author enatbol
 */
public class LAADFile {

    private ByteArrayOutputStream authentication;
    private ByteArrayOutputStream authorization;

    public LAADFile(final ByteArrayOutputStream authentication, final ByteArrayOutputStream authorization) {
        this.authentication = authentication;
        this.authorization = authorization;
    }

    public LAADFile() {
        authentication = new ByteArrayOutputStream();
        authorization = new ByteArrayOutputStream();
    }

    /**
     * Sets authentication file binary data
     * @param authentication ByteArrayOutputStream with the file data
     */
    public void setAuthentication(final ByteArrayOutputStream authentication) {
        this.authentication = authentication;
    }

    /**
     * Sets authentication file binary data
     * @param authentication byte array of file data
     * @throws IOException
     */
    public void setAuthentication(final byte[] authentication) throws IOException {
        setAuthentication(getOutputStreamWithData(authentication));
    }

    /**
     * Gets authentication file binary data
     * @return ByteArrayOutputStream with file data
     */
    public ByteArrayOutputStream getAuthentication() {
        return this.authentication;
    }

    /**
     * Gets authorization file binary data
     * @return ByteArrayOutputStream with file data
     */
    public ByteArrayOutputStream getAuthorization() {
        return this.authorization;
    }

    /**
     * Sets authorization file binary data
     * @param authorization ByteArrayOutputStream with file data
     */
    public void setAuthorization(final ByteArrayOutputStream authorization) {
        this.authorization = authorization;
    }

    /**
     * Sets authorization file binary data
     * @param authorization byte array of authorization file data
     * @throws IOException
     */
    public void setAuthorization(final byte[] authorization) throws IOException {
        setAuthorization(getOutputStreamWithData(authorization));
    }

    private ByteArrayOutputStream getOutputStreamWithData(final byte[] data) throws IOException {
        final ByteArrayOutputStream fos = new ByteArrayOutputStream();
        fos.write(data);
        fos.close();
        return fos;
    }
}
