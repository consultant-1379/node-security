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
import java.util.*;

import com.ericsson.nms.security.nscs.api.enums.DigestAlgorithm;
import com.ericsson.nms.security.nscs.api.model.CertSpec;
import com.ericsson.nms.security.nscs.data.moaction.param.MoParams;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.model.TrustedCertCategory;

/**
 * TrustStoreInfo holding data to call <i>isntallTrustedCertificates</i> MO action on a CPP node.
 * 
 * <p>Check CPP reference <code>void installTrustedCertificates ( CertSpec[0..] certSpecList , string startTime , long duration , AccountInfo[0..] accountInfoList );</code></p>
 * 
 * @see <a href="https://cpp-mom.rnd.ki.sw.ericsson.se/cpp_c14/momdoc/CPP-LSV127-gen9-complete_vs_LSV125/EnrollmentData.html">CPP MOM</a>
 * 
 * @author egbobcs
 */
public class TrustStoreInfo implements Serializable {

    private static final long serialVersionUID = 2L;

    private final TrustedCertCategory category;
    private Set<CertSpec> certSpecs = new HashSet<>();
    private List<SmrsAccountInfo> accountInfos = new ArrayList<>();
    private final DigestAlgorithm fingerprintAlgorithm;
    
    /**
     * Constructor which sets all the fields according to the input parameters
     * 
     * @param category
     * @param certSpecSet
     * @param accountInfoList
     * @param certFingerprintAlgorithm the value of certFingerprintAlgorithm
     */
    public TrustStoreInfo(final TrustedCertCategory category, final Set<CertSpec> certSpecSet,
                final List<SmrsAccountInfo> accountInfoList, DigestAlgorithm certFingerprintAlgorithm) {
        this.category = category;
        if (certSpecSet != null)
            this.certSpecs = certSpecSet;
        if (accountInfoList != null)
            this.accountInfos = accountInfoList;
        this.fingerprintAlgorithm = certFingerprintAlgorithm;
    }

    /**
     * Gets the category
     * 
     * @return category
     */
    public TrustedCertCategory getCategory() {
        return category;
    }

    /**
     * Gets the accountInfos
     * 
     * @return accountInfos
     */
    public List<SmrsAccountInfo> getAccountInfo() {
        return accountInfos;
    }

    /**
     * Gets the certSpecs
     * 
     * @return certSpecs
     */
    public Set<CertSpec> getCertSpecs() {
        return certSpecs;
    }

    public DigestAlgorithm getFingerPrintAlgorithm() {
        return fingerprintAlgorithm;
    }

    @Override
    public String toString() {
        String ret = "TrustStoreInfo\n";
        ret += "  Category: " + category + "\n";
        ret += "  DigestAlgorithm: " + fingerprintAlgorithm.toString() + "\n";
        ret += "  CertSpecs: " + Arrays.toString(certSpecs.toArray()) + "\n";
        ret += "  AccountInfos: " + Arrays.toString(accountInfos.toArray()) + "\n";
        return ret;
    }
    
    /**
	 * Returns the MoParams representation of the supplied values.
	 * 
	 * <p>Check CPP reference <code>void installTrustedCertificates ( CertSpec[0..] certSpecList , string startTime , long duration , AccountInfo[0..] accountInfoList );</code></p>
	 * 
	 * @see <a href="https://cpp-mom.rnd.ki.sw.ericsson.se/cpp_c14/momdoc/CPP-LSV127-gen9-complete_vs_LSV125/EnrollmentData.html">CPP MOM</a>
	 */
    public static MoParams toMoParams(final List<MoParams> certSpecList, 
			final String startTime, final long duration, final List<MoParams> accountInfoList) {
    	final MoParams params = new MoParams();
		params.addParam("certSpecList", certSpecList);
		params.addParam("startTime", startTime);
		params.addParam("duration", String.valueOf(duration));
		params.addParam("accountInfoList", accountInfoList);
		return params;		
    }
    
    /**
   	 * Returns the MoParams representation of the supplied values.
   	 * 
   	 * <p>Check CPP reference <code>void installTrustedCertificates ( CertSpec[0..] certSpecList , string startTime , long duration , AccountInfo[0..] accountInfoList );</code></p>
   	 * 
   	 * @see <a href="https://cpp-mom.rnd.ki.sw.ericsson.se/cpp_c14/momdoc/CPP-LSV127-gen9-complete_vs_LSV125/EnrollmentData.html">CPP MOM</a>
   	 */
       public static MoParams toMoParams(final List<MoParams> certSpecList, 
   			 final List<MoParams> accountInfoList) {
       	final MoParams params = new MoParams();
   		params.addParam("certSpecList", certSpecList);
   		params.addParam("accountInfoList", accountInfoList);
   		return params;		
       }

	/**
	 * Returns the MoParams representation of the supplied values.<br />
	 * 
	 * <p>Check CPP reference <code>void installTrustedCertificates ( CertSpec[0..] certSpecList , string startTime , long duration , AccountInfo[0..] accountInfoList );</code></p>
	 *
	 * <p>StartTime and duration are constants: "0" and 0</p>
	 * 
         * @return 
	 * @see<a href="https://cpp-mom.rnd.ki.sw.ericsson.se/cpp_c14/momdoc/CPP-LSV127-gen9-complete_vs_LSV125/EnrollmentData.html">CPP MOM</a> 
	 *
	 */
	public MoParams toMoParams()  {
    	//build certSpecList from certSpecs
		final List<MoParams> certSpecList = new ArrayList<>();
                String certificateRelativePath = null;
                if ((this.accountInfos) != null && (!this.accountInfos.isEmpty()))
                    certificateRelativePath = this.accountInfos.get(0).getSmrsRelativePath();
		for (final CertSpec c : certSpecs) {
                    final CPPCertSpec cmCertSpec;
                    try {
                        cmCertSpec = new CPPCertSpec(c, category, fingerprintAlgorithm, certificateRelativePath);
                    } catch (NoSuchAlgorithmException | CertificateEncodingException ex) {
                        continue;
                    }
                    certSpecList.add(cmCertSpec.toMoParams());
		}				
		//build accountInfoList from accountInfos
		final List<MoParams> accountInfoList = new ArrayList<>();
		for (final AccountInfo a : accountInfos) {
			accountInfoList.add(a.toMoParams());
		}	
		//startTime and duration are constants: "0" and 30
		return toMoParams(certSpecList, "0", 30, accountInfoList);						
	}	
	
	
	/**
	 * Returns the MoParams representation of the supplied values.
	 * <p> Check CPP reference <code> void installTrustedCertificates ( IpSecCertSpec[0..20] certSpecList , IpSecAccountInfo[0..20] accountInfoList ); </code></p>
	 * @see<a href="http://cpistore.internal.ericsson.com/alexserv?ID=18771&fn=15554-EN_LZN7850001_2-V1Uen.G.130.html">CPP MOM</a> 
	 * @return {@link MoParams}
	 */
	public MoParams toMoParamsIpSec()  {
    	//build certSpecList from certSpecs
		final List<MoParams> certSpecList = new ArrayList<>();
                String certificateRelativePath = null;
                if ((this.accountInfos) != null && (!this.accountInfos.isEmpty()))
                    certificateRelativePath = this.accountInfos.get(0).getSmrsRelativePath();
		for (final CertSpec c : certSpecs) {
                    final CPPCertSpec cmCertSpec;
                    try {
                        cmCertSpec = new CPPCertSpec(c, category, fingerprintAlgorithm, certificateRelativePath);
                    } catch (NoSuchAlgorithmException | CertificateEncodingException ex) {
                        continue;
                    }
                    certSpecList.add(cmCertSpec.toMoParamsIpSec());
		}				
		//build accountInfoList from accountInfos
		final List<MoParams> accountInfoList = new ArrayList<>();
		for (final AccountInfo a : accountInfos) {
			accountInfoList.add(a.toMoParams());
		}	
		return toMoParams(certSpecList, accountInfoList);						
	}
}
