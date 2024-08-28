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

import com.ericsson.nms.security.nscs.data.moaction.param.MoParams;
import java.util.List;

/**
 * Interface to provide MOAction related operations on CPP nodes.
 * 
 * @author egbobcs
 * 
 */
public interface MOActionService {

	/**
	 * Performs MO action on the specified node.
	 * 
	 * For the list of the supported MO actions see MoActionWithoutParameter
	 * enum.
	 * 
	 * The MO actions from DPS down to the node are asynchronous. This means the
	 * result of the MO Action both in case of success and failure has to be
	 * checked through CPP AVCs Events or Alarms.
	 * 
	 * @param neNameOrNodeRootFdn
	 *            name of the NE or FDN of the node root MO.
	 * @param action
	 *            the action to perform
	 * @throws com.ericsson.nms.security.nscs.api.exception.DataAccessSystemException
	 *             in case the node does not exists
	 * 
	 */
	void performMOAction(final String neNameOrNodeRootFdn,
			final MoActionWithoutParameter action);

	/**
	 * Performs MO action on the specified node.
	 * 
	 * For the list of the supported MO actions see MoActionWithParameter enum.
	 * 
	 * The MO actions from DPS down to the node are asynchronous. This means the
	 * result of the MO Action both in case of success and failure has to be
	 * checked through CPP AVCs Events or Alarms.
	 * 
	 * @param neNameOrNodeRootFdn
	 *            name of the NE or FDN of the node root MO.
	 * @param action
	 *            the action to perform
	 * @param parameters
	 *            the parameters of the specified MO action
	 * @throws com.ericsson.nms.security.nscs.api.exception.DataAccessSystemException
	 *             in case the node does not exists or if the supplied
	 *             parameters are incorrect
	 */
	void performMOAction(final String neNameOrNodeRootFdn,
			final MoActionWithParameter action, MoParams parameters);

	/**
	 * Performs MO actions on the specified node with a list of MoParams.
         * The MO action is repeated on the same node with parameter list entries,
         * until a successful result is obtained.
	 * 
	 * For the list of the supported MO actions see MoActionWithParameter enum.
	 * 
	 * The MO actions from DPS down to the node are asynchronous. This means the
	 * result of the MO Action both in case of success and failure has to be
	 * checked through CPP AVCs Events or Alarms.
	 * 
	 * @param neNameOrNodeRootFdn
	 *            name of the NE or FDN of the node root MO.
	 * @param action
	 *            the action to perform
	 * @param paramsList
	 *            the list parameters for the MO actions to be performed 
	 * @throws com.ericsson.nms.security.nscs.api.exception.DataAccessSystemException
	 *             in case the node does not exists or if the supplied
	 *             parameters are incorrect
	 */
	void performMOAction(final String neNameOrNodeRootFdn,
			final MoActionWithParameter action, List<MoParams> paramsList);

	/**
	 * Performs MO action with parameters on the specified MO given the fdn.
	 * 
	 * @param moFdn
	 *            The MO fdn where the action will be invoked
	 * @param action
	 *            The action to perform
	 * @param params
	 *            the parameters of the specified MO action
	 */
	void performMOActionByMoFdn(String moFdn, MoActionWithParameter action,
			MoParams params);

	/**
	 * Performs MO action without parameters on the specified MO given the fdn.
	 * 
	 * @param moFdn
	 *            The MO fdn where the action will be invoked
	 * @param action
	 *            The action to perform
	 */
	void performMOActionByMoFdn(String moFdn, MoActionWithoutParameter action);

}