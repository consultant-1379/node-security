/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.cpp.model;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;

import com.ericsson.nms.security.nscs.api.enums.DigestAlgorithm;
import com.ericsson.nms.security.nscs.api.model.CertSpec;
import com.ericsson.nms.security.nscs.data.moaction.param.MoParams;
import com.ericsson.nms.security.nscs.pki.NscsPkiUtils;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.model.TrustedCertCategory;

/**
 * Holder class of the pki-model CertSpec with the addition of CPP specific
 * fields and implementation.
 *
 * @see
 * <a href="https://cpp-mom.rnd.ki.sw.ericsson.se/cpp_c14/momdoc/CPP-LSV127-gen9-complete_vs_LSV125/CertSpec.html">CPP
 * MOM</a>
 *
 * @author egbobcs
 */
public class CPPCertSpec implements Serializable {

    private static final long serialVersionUID = 3L;

    private final CertSpec certSpec;
    private final String certificateRelativeFilePath;
    private final TrustedCertCategory category;
    private final DigestAlgorithm fingerPrintAlgorithm;
    private final byte[] fingerPrint;

    public CPPCertSpec(final CertSpec certSpec, final TrustedCertCategory category, 
            final DigestAlgorithm certFingerprintAlgorithm, final String certificateFilePath) 
            throws NoSuchAlgorithmException, CertificateEncodingException {
        this.certSpec = certSpec;
        this.category = category;
        this.fingerPrintAlgorithm = certFingerprintAlgorithm;
        this.certificateRelativeFilePath = certificateFilePath;
        this.fingerPrint = NscsPkiUtils.generateMessageDigest(fingerPrintAlgorithm, certSpec.getCertHolder().getEncoded());
    }

    /**
     * Returns the MoParams representation of the object's values.
     *
     * @see
     * <a href="https://cpp-mom.rnd.ki.sw.ericsson.se/cpp_c14/momdoc/CPP-LSV127-gen9-complete_vs_LSV125/CertSpec.html">CPP
     * MOM</a>
     *
     * @return MoParams
     */
    public MoParams toMoParams() {
        return CPPCertSpec.toMoParams(category, certSpec.getFileName(), this.certificateRelativeFilePath, 
                getFingerPrint(), certSpec.getSerial(), this.fingerPrintAlgorithm);
    }

    /**
     * Returns the MoParams representation of the supplied values.
     *
     * @param category
     * @param fileName
     * @param relativePath
     * @param fingerprint
     * @param serialNumber
     * @param fingerPrintAlgorithm
     * @see
     * <a href="https://cpp-mom.rnd.ki.sw.ericsson.se/cpp_c14/momdoc/CPP-LSV127-gen9-complete_vs_LSV125/CertSpec.html">CPP
     * MOM</a>
     *
     * @return MoParams
     */
    public static MoParams toMoParams(final TrustedCertCategory category, final String fileName, final String relativePath,
            final byte[] fingerprint, final String serialNumber, final DigestAlgorithm fingerPrintAlgorithm) {
        final MoParams params = new MoParams();
        params.addParam("category", category.toString());
        params.addParam("fileName", getRelativeFilePath(fileName, relativePath));
        params.addParam("fingerprint", formatFingerprint(fingerprint, fingerPrintAlgorithm));
        params.addParam("serialNumber", serialNumber);
        return params;
    }

    /**
     * Returns the MoParams representation of the object's values.
     *
     * @see
     * <a href="http://cpistore.internal.ericsson.com/alexserv?ID=18771&fn=15554-EN_LZN7850001_2-V1Uen.G.129.html">CPP
     * MOM</a>
     *
     * @return MoParams
     */
    public MoParams toMoParamsIpSec() {
        return CPPCertSpec.toMoParamsIpSec(certSpec.getFileName(), this.certificateRelativeFilePath, 
                getFingerPrint(), certSpec.getSerial(), this.fingerPrintAlgorithm);
    }

    /**
     * Returns the MoParams representation of the supplied values.
     *
     * @see
     * <a href="http://cpistore.internal.ericsson.com/alexserv?ID=18771&fn=15554-EN_LZN7850001_2-V1Uen.G.129.html">CPP
     * MOM</a>
     *
     * @return MoParams
     */
    public static MoParams toMoParamsIpSec(final String fileName,  final String relativePath, final byte[] fingerprint, 
            final String serialNumber, final DigestAlgorithm fingerPrintAlgorithm) {
        final MoParams params = new MoParams();
        params.addParam("fileName", getRelativeFilePath(fileName, relativePath));
        params.addParam("fingerprint", formatFingerprint(fingerprint, fingerPrintAlgorithm) );
        params.addParam("serialNumber", serialNumber);
        return params;
    }

    public byte[] getFingerPrint() {
        return fingerPrint;
    }

    public static String formatFingerprint(final byte[] fingerprint, final DigestAlgorithm fingerPrintAlgorithm) {
        return fingerPrintAlgorithm.getDigestValuePrefix() + CertSpec.bytesToHex(fingerprint);
    }

    private static String getRelativeFilePath(final String fileName, final String relativePath) {
        return relativePath + fileName;
    }
    
    public final CertSpec getCertSpec() {
    	return this.certSpec;
    }
    
}
