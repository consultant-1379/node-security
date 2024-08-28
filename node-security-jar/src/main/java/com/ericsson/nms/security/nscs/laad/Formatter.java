/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ericsson.nms.security.nscs.laad;

import java.io.OutputStream;

/**
 * <p>A Formatter is responsible to format LAADData information and write
 * it in a output stream.</p>
 * @author enatbol
 * @see com.ericsson.nms.security.nscs.laad.LAADVersionFormatter
 */
abstract class Formatter {

    protected final DomDocument domDocument;

    protected Formatter() {
        domDocument = new DomDocument();
    }

    /**
     * Fetches the default Formatter implementation
     * @return Formatter implementation
     * @throws FormatterException
     */
    static Formatter getInstance() throws FormatterException {
        return new LAADVersionFormatter();
    }

    abstract void formateAuthenticationData(OutputStream os, LAADData[] laadData, int fileVersion) throws FormatterException;

    abstract void formateAuthorizationData(OutputStream os, LAADData[] laadData, int fileVersion) throws FormatterException;

}
