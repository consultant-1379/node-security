/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ericsson.nms.security.nscs.api.pki;

import com.ericsson.nms.security.nscs.api.enums.AlgorithmKeys;
import java.io.Serializable;
import com.ericsson.oss.itpf.security.pki.manager.model.entities.Entity;
import com.ericsson.oss.itpf.security.pki.manager.model.EnrollmentInfo;

/**
 *
 * @author enmadmin
 */
public class EnrollmentPartialInfos  implements Serializable {

    private static final long serialVersionUID = -1176742888486008390L;

    Entity ee;
    EnrollmentInfo enrollmentInfo;
    AlgorithmKeys keySize;

    public EnrollmentPartialInfos(final Entity enden, final EnrollmentInfo esi, final AlgorithmKeys keySize) {
        this.ee = enden;
        this.enrollmentInfo = esi;
        this.keySize = keySize;
    }

    public Entity getEndEntity() {
        return this.ee;
    }

    public EnrollmentInfo getEnrollmentServerInfo() {
        return this.enrollmentInfo;
    }

    /**
     * @return the keySize
     */
    public AlgorithmKeys getKeySize() {
        return keySize;
    }

    /**
     * @param keySize the keySize to set
     */
    public void setKeySize(AlgorithmKeys keySize) {
        this.keySize = keySize;
    }
    
}
