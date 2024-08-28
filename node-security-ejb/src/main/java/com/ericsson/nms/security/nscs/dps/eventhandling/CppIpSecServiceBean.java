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
package com.ericsson.nms.security.nscs.dps.eventhandling;

import java.util.Collection;

import javax.ejb.*;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.cpp.level.CppIpSecService;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.*;
import com.ericsson.nms.security.nscs.data.ModelDefinition.NetworkElementSecurity;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.oss.services.cm.cmshared.dto.CmObject;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;

@Stateless
public class CppIpSecServiceBean implements CppIpSecService {

    @Inject
    private Logger logger;

    @Inject
    NscsCMReaderService readerService;

    @Inject
    NscsCMWriterService writerService;

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateSummaryFileHashAttributeOfMo(final NodeReference nodeRef, final String summaryFileHash) {

        try {
            logger.info("Summary file hash  setting in DPS {}", summaryFileHash);

            final NormalizableNodeReference normNode = readerService.getNormalizableNodeReference(nodeRef);

            logger.info("Updating summary file hash in NetworkElementSecurity MO {}", normNode.getFdn());

            String networkElementSecurityFdn = Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.withNames(normNode.getName()).fdn();

            logger.info("Updating summary file hash in NetworkElementSecurity MO {} NES fdn", networkElementSecurityFdn);

            writerService.withSpecification(networkElementSecurityFdn).setAttribute(NetworkElementSecurity.SUMMARY_FILE_HASH, getRawHash(summaryFileHash)).updateMO();

            logger.info("Summary file hash  succesfully set for node {}", normNode.getFdn());

            CmResponse cmResponse = readerService.getMOAttribute(normNode.getNormalizedRef().getFdn(), Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.type(),
                    Model.NETWORK_ELEMENT.securityFunction.networkElementSecurity.namespace(), NetworkElementSecurity.SUMMARY_FILE_HASH);

            Collection<CmObject> cmObjs = cmResponse.getCmObjects();
            if (cmResponse == null || cmObjs == null || cmObjs.isEmpty() || cmObjs.size() > 1) {
                logger.info("Error reading attribute [{}] from NormalizedModel for [{}]...going to read from Capability model", NetworkElementSecurity.SUMMARY_FILE_HASH, normNode.getNormalizedRef()
                        .getFdn());
            } else {
                String fileHash = (String) cmObjs.iterator().next().getAttributes().get(NetworkElementSecurity.SUMMARY_FILE_HASH);
                logger.info("Summary file hash  after getting from DPS {}", fileHash);
            }

        } catch (final Exception e) {
            logger.info("Update of summary file hash in NetworkElementSecurity MO failed!", e);

        }

    }
    
    private String getRawHash(final String summaryFileHash){
    	return summaryFileHash.replace("SHA1=", "").replace(":", "");
    	    	
    }
}
