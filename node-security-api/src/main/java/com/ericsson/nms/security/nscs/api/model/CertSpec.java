/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ericsson.nms.security.nscs.api.model;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Locale;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.X500NameTokenizer;

import com.ericsson.nms.security.nscs.api.exception.UnexpectedErrorException;

/**
 *
 * @author enmadmin
 */
public class CertSpec implements Serializable {

    private static final long serialVersionUID = -8141910057331911030L;

    String fileName;
    String serial;
    byte[] fingerPrint;
    X509Certificate cert;
    
    private static final String certFileNameExtension = ".der";

    public CertSpec(){
    }
    /**
     *
     * @param cert the X509Certificate cert
     * @throws java.security.cert.CertificateException exception for CertSpec
     */
    public CertSpec(final X509Certificate cert) throws CertificateException {
        //TODO implement file name calculation correctly
        this.serial = cert.getSerialNumber().toString();
        String subjectCN = getCNfromDN(cert.getSubjectX500Principal().toString()).replace(",","_").replace(" ","");
        String issuerCN = getCNfromDN(cert.getIssuerX500Principal().toString()).replace(",","_").replace(" ","");
        this.fileName = "I_" + issuerCN + "_S_" + subjectCN + "_" + serial + certFileNameExtension;
        try {
            final MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(cert.getEncoded());
            final byte[] digest = messageDigest.digest();
            this.fingerPrint = digest;
        } catch (final NoSuchAlgorithmException e) {
            throw new CertificateException("Failed MD5 fingerprint calculation", e);
        }
        this.cert = cert;
    }

    public final String getCNfromDN(final String dn) {
	if (dn == null) {
	    final String errorMessage = String.format("Wrong or unsupported DN [%s]", dn);
            throw new UnexpectedErrorException(errorMessage);
        }
	final X500Name x500Name = new X500Name(dn);
        final String distinguishedName= x500Name.toString();
        final X500NameTokenizer x500Tokenizer = new X500NameTokenizer(distinguishedName, ',');
        ArrayList<String> rdnNames = new ArrayList<>();
        while (x500Tokenizer.hasMoreTokens()) {
            rdnNames.add(x500Tokenizer.nextToken());
        }
        String commonName = null;
        int indexOf;
        String rdnType = "cn=";
        for (String s : rdnNames) {
            if (s.toLowerCase(Locale.ROOT).contains(rdnType)) {
                indexOf = s.toLowerCase(Locale.ROOT).indexOf(rdnType);
                commonName = s.substring(indexOf + 3);
                break;
            }
        }
        if (commonName == null || commonName.isEmpty()){
            final String errorMessage = "Empty or Null Common Name in the the Certificate Subject/Issuer Dn :"+ distinguishedName;
            throw new UnexpectedErrorException(errorMessage);
        }
        return commonName.replace("\\", "").replace("\"", "");
    }
    public String getFileName() {
        return fileName;
    }

    public byte[] getFingerPrint() {
        return fingerPrint;
    }

//    /**
//     * Gets MD5 fingerprint of the Certificate
//     *
//     * @return byte[] - MD5 fingerprint
//     * @throws NoSuchAlgorithmException
//     */
//    public byte[] getMD5FingerPrint() throws NoSuchAlgorithmException {
//        return cert.getCertificate().getFingerprint(AlgorithmID.md5.getName());
//    }

    public String getSerial() {
        return serial;
    }

    public X509Certificate getCertHolder() {
        return cert;
    }

    @Override
    public String toString() {
        final String ret = fileName + "/" + serial + "/" + bytesToHex(fingerPrint) + "\n";
        return ret;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((fileName == null) ? 0 : fileName.hashCode());
        result = prime * result
                + ((serial == null) ? 0 : serial.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        final CertSpec spec = (CertSpec) obj;
        return compare(fileName, spec.fileName)
                && compare(serial, spec.serial);
    }

    private static boolean compare(final Object compareThis, final Object toThat) {
        return ((compareThis == toThat)
                || (compareThis != null && compareThis.equals(toThat)));
    }

    public static String bytesToHex(final byte[] bytes) {
        final StringBuffer result = new StringBuffer();
        for (final byte b : bytes) {
            result.append(String.format("%02X", b));
            result.append(":");
        }
        result.deleteCharAt(result.length() - 1);
        return result.toString();
    }

    
}
