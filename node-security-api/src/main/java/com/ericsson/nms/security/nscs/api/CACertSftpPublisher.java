/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2015
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.api;

import java.security.cert.CertificateException;
import java.util.Set;

import javax.ejb.Local;

import com.ericsson.nms.security.nscs.api.model.CertSpec;
import com.ericsson.nms.security.nscs.api.pki.NscsPkiEntitiesManagerException;
import com.ericsson.oss.itpf.sdk.core.annotation.EService;

@EService
@Local
public interface CACertSftpPublisher {

    //    SmrsAccount registerSftpTrustStore(String nt1) throws SmrsDirectoryException;

    Set<CertSpec> getCACertSpecs(String string) throws CertificateException, NscsPkiEntitiesManagerException;

    void publishCertificates(String neType, final String neName) throws CertificateException, NscsPkiEntitiesManagerException;

    void publishCertificates(Set<CertSpec> certSpecs, String neType, final String neName) throws CertificateException;
}
