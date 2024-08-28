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
package com.ericsson.nms.security.nscs.handler.validation.impl;

import static com.ericsson.nms.security.nscs.data.Model.NETWORK_ELEMENT;

import java.security.Security;
import java.util.*;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.api.command.NscsCommand;
import com.ericsson.nms.security.nscs.api.command.NscsPropertyCommand;
import com.ericsson.nms.security.nscs.api.command.types.NscsNodeCommand;
import com.ericsson.nms.security.nscs.api.exception.*;
import com.ericsson.nms.security.nscs.api.model.NodeRef;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.handler.CommandContext;
import com.ericsson.nms.security.nscs.handler.validation.CommandValidator;
import com.ericsson.oss.itpf.datalayer.dps.DataPersistenceService;
import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.services.cm.cmshared.dto.CmObject;
import com.ericsson.oss.services.cm.cmshared.dto.CmResponse;

public class AlarmSupervisionValidator implements CommandValidator {

	
	@Inject
	private Logger logger;

	@Inject
	private NscsCMReaderService reader;
	
	@EServiceRef
	private DataPersistenceService dataPersistenceService;

	public static final String WILDCARD_NOT_SUPPORTED = "Command is using all nodes wildcard. Validation not supported for wildcard";
	
	/* (non-Javadoc)
	 * @see com.ericsson.nms.security.nscs.handler.validation.CommandValidator#validate(com.ericsson.nms.security.nscs.api.command.NscsPropertyCommand, com.ericsson.nms.security.nscs.handler.CommandContext)
	 */
	@Override
	public void validate(NscsPropertyCommand command, CommandContext context)
			throws NscsServiceException {
		logger.info("Starting AlarmSupervisionValidator with command type: {}"+command);
		assertNscsNodeCommand(command);

		final NscsNodeCommand nodeCommand = (NscsNodeCommand) command;
		if (nodeCommand.isAllNodes()) {
			logger.info(WILDCARD_NOT_SUPPORTED + ", throwing exception");
			throw new IllegalArgumentException(WILDCARD_NOT_SUPPORTED);
		}

        if ( context.getValidNodes().size() > 0 ) {
            //Check the nodes are SYNCHRONIZED using cm-reader
            CmResponse response = reader.getMOAttribute(context.toNormalizedRef(context.getValidNodes()),
                    NETWORK_ELEMENT.cmFunction.type(),
                    NETWORK_ELEMENT.cmFunction.namespace(),
                    Security.getProperty("active"));
            logger.info("cmresponse result"+response);
            List<NormalizableNodeReference> f = context.getValidNodes();
            for (NormalizableNodeReference nodeReference:f) {
            	response= reader.getMoByFdn(nodeReference);

                final Collection<CmObject> cmObjects = response.getCmObjects();

                if (cmObjects.size() != 1) {
                    logger.info("Make sure [{}] node exists" , nodeReference);
                }
                
                for(final CmObject cm : cmObjects)
                {
                	Map<String, Object> map1 =cm.getAttributes();
                	for (Map.Entry<String, Object> entry : map1.entrySet()) {
                	    logger.info("Key = " + entry.getKey() + ", Value is = " + entry.getValue().toString());
                	}
                }

                final InvalidAlarmSupervisionStateException invalidAlarmSupervisionStateException = new InvalidAlarmSupervisionStateException();

                for (final CmObject o : cmObjects) {
                    final NodeReference currentNode = new NodeRef(o.getFdn());
                    final String neFdn = currentNode.getFdn().replace("MeContext", "NetworkElement");
                    final String fmAlarmSupervisionFdn = neFdn.concat(",FmAlarmSupervision=1");  
                    final ManagedObject mo = dataPersistenceService.getLiveBucket().findMoByFdn(fmAlarmSupervisionFdn);
                    final boolean alarmSupervisionState = mo.getAttribute("active");
                    if (alarmSupervisionState!=true) {
                        context.setAsInvalidOrFailed(currentNode, invalidAlarmSupervisionStateException);
                    }
                    else{
                    	logger.info("entered in to the else condition");
                    }
                }

			}
            
        }

	}
	
	private void assertNscsNodeCommand(final NscsCommand command) {    
		if (!NscsNodeCommand.isNscsNodeCommand(command)) {
			logger.info("Got an unexpected type of command. '{}' expecting NscsNodeCommand", command);
			throw new UnexpectedCommandTypeException();	
		}    	
	}

}
