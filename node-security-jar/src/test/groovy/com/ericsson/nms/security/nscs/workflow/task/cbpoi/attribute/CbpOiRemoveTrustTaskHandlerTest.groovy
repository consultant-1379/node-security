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
package com.ericsson.nms.security.nscs.workflow.task.cbpoi.attribute

import java.io.Serializable
import java.util.HashMap
import java.util.Map

import javax.annotation.meta.When
import javax.inject.Inject
import javax.transaction.UserTransaction
import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.nms.security.nscs.data.NscsCMReaderService
import com.ericsson.nms.security.nscs.data.NscsCMWriterService
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference
import com.ericsson.oss.services.nscs.workflow.serializer.api.NscsObjectSerializer;
import com.ericsson.nms.security.nscs.api.model.NodeRef
import com.ericsson.nms.security.nscs.api.model.NodeReference
import com.ericsson.nms.security.nscs.logger.NscsLogger
import com.ericsson.oss.itpf.datalayer.dps.DataBucket
import com.ericsson.oss.itpf.datalayer.dps.DataPersistenceService
import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject
import com.ericsson.oss.itpf.datalayer.dps.stub.RuntimeConfigurableDps
import com.ericsson.oss.mediation.modeling.schema.gen.net_momdtd.Out
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cbpoi.attribute.CbpOiRemoveTrustTask
import com.ericsson.oss.services.nscs.workflow.utils.WorkflowOutputParameterKeys
import org.mockito.Mock
import org.mockito.Mockito
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.UnexpectedErrorException;

class CbpOiRemoveTrustTaskHandlerTest extends CdiSpecification {

    @ObjectUnderTest
    CbpOiRemoveTrustTaskHandler cbpOiRemoveTrustTaskHandler

    @MockedImplementation
    NscsLogger nscsLogger

    @MockedImplementation
    NodeReference nodeRef

    @MockedImplementation
    NscsCMReaderService reader

    @MockedImplementation
    NscsCMWriterService nscsCMWriterService

    @Inject
    NormalizableNodeReference normNodeRef

    private nodeName = "vDU00001"

    def setup() {
        NodeReference nodeRef = new NodeRef(nodeName)
        normNodeRef.getNormalizedRef() >> nodeRef
        reader.getNormalizableNodeReference(_) >> normNodeRef
    }

    def "When everything is correct task should return success response"() {
        given:
        Map<String, Serializable> outputParams = new HashMap<String, Serializable>();
        outputParams.put(WorkflowOutputParameterKeys.TRUSTED_CERTIFICATE_FDN.toString(), "rO0ABXNyABNqYXZhLnV0aWwuQXJyYXlMaXN0eIHSHZnHYZ0DAAFJAARzaXpleHAAAAACdwQAAAACdABYTWFuYWdlZEVsZW1lbnQ9dkRVMDAwMDEsdHJ1c3RzdG9yZT0xLGNlcnRpZmljYXRlcz1vYW1UcnVzdENhdGVnb3J5LGNlcnRpZmljYXRlPU5FX09BTV9DQXEAfgACeA==");
        CbpOiRemoveTrustTask task = new CbpOiRemoveTrustTask();
        task.setOutputParams(outputParams)
        NscsCMWriterService nscsCMWriterService = Mockito.mock(NscsCMWriterService.class);
        Mockito.doNothing().when(nscsCMWriterService).deleteMo("fdn")
        when:
        String result = cbpOiRemoveTrustTaskHandler.processTask(task)
        then:
        assert result
    }

    def "When output parameters are null"() {
        given:
        Map<String, Serializable> outputParams = null;
        CbpOiRemoveTrustTask task = new CbpOiRemoveTrustTask();
        task.setOutputParams(outputParams)
        when:
        String result = cbpOiRemoveTrustTaskHandler.processTask(task)
        then:
        thrown(UnexpectedErrorException)
    }

    def "When certificateMoFdn is null"() {
        given:
        Map<String, Serializable> outputParams = new HashMap<String, Serializable>();
        outputParams.put(WorkflowOutputParameterKeys.TRUSTED_CERTIFICATE_FDN.toString(), null);
        CbpOiRemoveTrustTask task = new CbpOiRemoveTrustTask();
        task.setOutputParams(outputParams)
        when:
        String result = cbpOiRemoveTrustTaskHandler.processTask(task)
        then:
        thrown(UnexpectedErrorException)
    }
}
