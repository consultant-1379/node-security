/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ericsson.nms.security.nscs.laad;

import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

/**
 * <p>Encapsulate the process to generate and verify LAAD files</p>
 * @author enatbol
 */
public class LAADGenerator {

    /**
     * <p>Crates a new LAADFile based on the provided LAADData and version</p>
     * @param laadData LAADData array to be written in the file
     * @param version file version information
     * @return LAADFile instance with the binary file data
     * @throws FormatterException
     * @throws NoSuchAlgorithmException
     */
    public LAADFile getLAADFile(final LAADData[] laadData, final int version) throws FormatterException, NoSuchAlgorithmException {
        final LAADFile laadFile = generateLAADFile(laadData, version);
        return laadFile;
    }

    /**
     * Verify if provided LAADFile is valid.
     * @param laad LAADFile to be verified
     * @param pkey
     * @throws Exception
     */
    public void verifyLaadFile(final LAADFile laad, final PublicKey pkey) throws Exception {
        laad.getAuthentication();
        laad.getAuthorization();

    }

    protected LAADFile generateLAADFile(final LAADData[] laadData, final int version) throws FormatterException, NoSuchAlgorithmException {
        final LAADFile laadFile = new LAADFile();
        try {
            final Formatter LaadFormatter = Formatter.getInstance();
            LaadFormatter.formateAuthenticationData(laadFile.getAuthentication(), laadData, version);
            LaadFormatter.formateAuthorizationData(laadFile.getAuthorization(), laadData, version);
        } catch (final FormatterException error) {
            throw new FormatterException(error.getMessage(), error);
        }
        return laadFile;
    }

}
