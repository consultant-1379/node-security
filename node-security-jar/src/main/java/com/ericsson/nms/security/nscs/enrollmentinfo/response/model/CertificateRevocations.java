package com.ericsson.nms.security.nscs.enrollmentinfo.response.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "crl" })
public class CertificateRevocations implements Serializable {

    private static final long serialVersionUID = 7374636630063666413L;

    @XmlElement(required = true)
    private List<CertificateRevocation> crl;

    public List<CertificateRevocation> getCertificateRevocations() {
        if (crl == null) {
            crl = new ArrayList<>();
        }
        return new ArrayList<>(this.crl);
    }

    public void setCertificateRevocations(List<CertificateRevocation> crl) {
        crl = new ArrayList<>(crl);
        this.crl = Collections.unmodifiableList(crl);
    }
}
