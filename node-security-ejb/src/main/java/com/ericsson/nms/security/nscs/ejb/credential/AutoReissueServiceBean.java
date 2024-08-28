package com.ericsson.nms.security.nscs.ejb.credential;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.handler.command.impl.CertificateReissueAuto;

@Stateless
public class AutoReissueServiceBean {
    @Inject
    CertificateReissueAuto certificateReissueAuto;

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void process() {
        certificateReissueAuto.process();
    }

}