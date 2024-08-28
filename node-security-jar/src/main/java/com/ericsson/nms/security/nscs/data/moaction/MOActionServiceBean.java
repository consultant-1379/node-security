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
package com.ericsson.nms.security.nscs.data.moaction;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.exception.DataAccessException;
import com.ericsson.nms.security.nscs.api.exception.NscsErrorCodes;
import com.ericsson.nms.security.nscs.data.moaction.param.AttributeSpecBuilder;
import com.ericsson.nms.security.nscs.data.moaction.param.MoParams;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.services.cm.cmshared.dto.*;
import com.ericsson.oss.services.cm.cmwriter.api.CmWriterService;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MOActionServiceBean implements MOActionService {

	@Inject Logger log;

	@EServiceRef
	CmWriterService writer;

	@Inject
	AttributeSpecBuilder attSpecBuilder;
	
	@Override
	public void performMOAction(final String neNameOrNodeRootFdn, final MoActionWithoutParameter action) {
		log.info("Starting performMOAction [{}], [{}]", neNameOrNodeRootFdn, action);
		final String fdn = action.getFDN(neNameOrNodeRootFdn);
		final String actionName = action.getAction(); 
		log.info("Invoking performAction [{}], [{}]", fdn, actionName);
		performAction(fdn, actionName, new ValidatedAttributeSpecifications());
		log.info("Finished performMOAction() successfully");
	}

	@Override
	public void performMOAction(final String neNameOrNodeRootFdn, final MoActionWithParameter action,final MoParams params) {
		log.info("Starting performMOAction [{}], [{}], [{}]", neNameOrNodeRootFdn, action, params);

		final String fdn = action.getFDN(neNameOrNodeRootFdn);
		final String actionName = action.getAction();
		log.debug("Building AttributeSpecificationContainer object from parameters");
		final AttributeSpecificationContainer parameters = attSpecBuilder.getAttributeSpecCont(params);	
		log.info("Invoking performAction [{}], [{}], [{}]", fdn, actionName, parameters);
		performAction(fdn, actionName,  parameters);
		log.info("Finished performMOAction() successfully");
	}
	
        @Override
        public void performMOAction(final String neNameOrNodeRootFdn, final MoActionWithParameter action, 
                                    final List<MoParams> paramsList) {
            log.info("Starting performMOAction [{}], [{}], [{}]", neNameOrNodeRootFdn, action, paramsList);
            final String fdn = action.getFDN(neNameOrNodeRootFdn);
            final String actionName = action.getAction();
            if ((paramsList != null) && !paramsList.isEmpty()) {
                final List<AttributeSpecificationContainer> attrSpecList = new ArrayList<>();
                log.debug("Building AttributeSpecificationContainer object from parameters");
                Iterator<MoParams> it = paramsList.iterator();
                while (it.hasNext()) {
                    attrSpecList.add(attSpecBuilder.getAttributeSpecCont(it.next()));
                }
                performAction(fdn, actionName, attrSpecList);
            }
            log.info("Finished performMOAction() successfully");
        }
        
	@Override
	public void performMOActionByMoFdn(final String moFdn, final MoActionWithoutParameter action) {
		final String actionName = action.getAction(); 
		log.info("Invoking performMOActionByMoFdn [{}], [{}]", moFdn, actionName);
		performAction(moFdn, actionName, new ValidatedAttributeSpecifications());
		log.info("Finished performMOActionByMoFdn() successfully");
	}

	@Override
	public void performMOActionByMoFdn(final String moFdn, final MoActionWithParameter action, final MoParams params) {
		log.info("Starting performMOActionByMoFdn [{}], [{}], [{}]", moFdn, action, params);

		final String actionName = action.getAction();
		log.debug("Building AttributeSpecificationContainer object from parameters");
		final AttributeSpecificationContainer parameters = attSpecBuilder.getAttributeSpecCont(params);	
		log.info("Invoking performAction [{}], [{}], [{}]", moFdn, actionName, parameters);
		performAction(moFdn, actionName,  parameters);
		log.info("Finished performMOActionByMoFdn() successfully");
	}
	
	private void performAction(final String fdn, final String action, final AttributeSpecificationContainer cont) {						
		log.debug("Performing MO action using CMWriterSerivce");
		CmResponse response = null;

		final ActionSpecification actionSpec = getActionSpecification(action, cont);
		response  = writer.performAction(fdn, actionSpec);

		throwExceptionIfFail(response);				
	}

	private void performAction(final String fdn, final String action, 
                                   final List<AttributeSpecificationContainer> contList) {
                Iterator<AttributeSpecificationContainer> it = contList.iterator();
		CmResponse response = null;
                while (it.hasNext()) {
                    final ActionSpecification actionSpec = getActionSpecification(action, it.next());
//                    log.debug("Performing MO action using CMWriterSerivce with enrollmentMode[" + 
//                            actionSpec.getAttributeSpecificationContainer().getAttributeSpecification("enrollmentMode").getValue() + "]");
                    response = writer.performAction(fdn, actionSpec);
                    if (response.getStatusCode() >= 0) // if cm-writer return success
                        break;
                }
		throwExceptionIfFail(response);				
	}

	private ActionSpecification getActionSpecification(final String action, final AttributeSpecificationContainer params) {							
		final ActionSpecification actionSpec = new ActionSpecification(action, params);
		return actionSpec;
	}
	
	private void throwExceptionIfFail(final CmResponse response) {        
		if (response.getStatusCode() < 0) {
			final String message = String.format("cm-writer response %s: %s", response.getStatusCode(), response.getStatusMessage());
			log.warn("Throwing exception because CM returned failure. Reason: {}", message);
			throw new DataAccessException(NscsErrorCodes.UNEXPECTED_INTERNAL_ERROR);
		} else {
			log.info("CMResponse number of instances: {}",response.getCmObjects().size());
		}
	}		
}