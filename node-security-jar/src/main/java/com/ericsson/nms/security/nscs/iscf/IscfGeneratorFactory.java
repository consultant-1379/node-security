/*
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 */

package com.ericsson.nms.security.nscs.iscf;

import java.util.Set;

import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.enums.SecurityLevel;
import com.ericsson.nms.security.nscs.api.iscf.BaseSubjectAltNameDataType;
import com.ericsson.nms.security.nscs.api.iscf.IpsecArea;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameFormat;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameParam;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation;

/**
 * Class for creating specific ISCF XML generators for Security Level, IPSec
 * and combined user cases
 *
 * @author ealemca
 */
public class IscfGeneratorFactory {

    @Inject
    SecurityLevelIscfGenerator secLevelGenerator;

    @Inject
    IpsecIscfGenerator ipsecGenerator;

    @Inject
    CombinedIscfGenerator combinedGenerator;

    public SecurityLevelIscfGenerator getSecLevelGenerator(
            final String logicalName,
            final String nodeFdn,
            final SecurityLevel wantedSecLevel,
            final SecurityLevel minimumSecLevel,
            final EnrollmentMode enrollmentMode,
            final NodeModelInformation modelInfo
    ) {
        secLevelGenerator.initGenerator(wantedSecLevel, minimumSecLevel, nodeFdn, logicalName,
        		enrollmentMode, modelInfo);
        return secLevelGenerator;
    }

    public IpsecIscfGenerator getIpsecGenerator(
            final String logicalName,
            final String nodeFdn,
            final String ipsecUserLabel,
            final SubjectAltNameParam ipsecSubjectAltName,
            final Set<IpsecArea> wantedIpSecAreas,
            final EnrollmentMode enrollmentMode,
            final NodeModelInformation modelInfo) {
        ipsecGenerator.initGenerator(nodeFdn, logicalName, ipsecUserLabel, ipsecSubjectAltName,
                wantedIpSecAreas, enrollmentMode, modelInfo);
        return ipsecGenerator;
    }

    public CombinedIscfGenerator getCombinedGenerator(
            final String logicalName,
            final String nodeFdn,
            final SecurityLevel wantedSecLevel,
            final SecurityLevel minimumSecLevel,
            final String ipsecUserLabel,
            final SubjectAltNameParam ipsecSubjectAltName,
            final Set<IpsecArea> wantedIpSecAreas,
            final EnrollmentMode enrollmentMode,
            final NodeModelInformation modelInfo
    ) {
        combinedGenerator.initGenerator(wantedSecLevel, minimumSecLevel, nodeFdn, logicalName,
                ipsecUserLabel, ipsecSubjectAltName, wantedIpSecAreas,
                enrollmentMode, modelInfo);
        return combinedGenerator;
    }
}
