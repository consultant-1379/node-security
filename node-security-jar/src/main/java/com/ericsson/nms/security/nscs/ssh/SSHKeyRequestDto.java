/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2023
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.ssh;

public class SSHKeyRequestDto {

    String fdn;
    String sshkeyOperation;
    String algorithm;
    String momType;
    String isModeledSshKey;

    public String getFdn() {
        return fdn;
    }

    public void setFdn(String fdn) {
        this.fdn = fdn;
    }

    public String getSshkeyOperation() {
        return sshkeyOperation;
    }

    public void setSshkeyOperation(String sshkeyOperation) {
        this.sshkeyOperation = sshkeyOperation;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getMomType() {
        return momType;
    }

    public void setMomType(String momType) {
        this.momType = momType;
    }

    public String isModeledSshKey() {
        return isModeledSshKey;
    }

    public void setModeledSshKey(String modeledSshKey) {
        isModeledSshKey = modeledSshKey;
    }

    @Override
    public String toString() {
        return "SSHKeyRequestDto{" +
                "fdn='" + fdn + '\'' +
                ", sshkeyOperation='" + sshkeyOperation + '\'' +
                ", algorithm='" + algorithm + '\'' +
                ", momType='" + momType + '\'' +
                ", isModeledSshKey='" + isModeledSshKey + '\'' +
                '}';
    }
}
