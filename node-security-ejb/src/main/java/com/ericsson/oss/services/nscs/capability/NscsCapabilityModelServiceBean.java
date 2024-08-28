package com.ericsson.oss.services.nscs.capability;

import javax.inject.Inject;

import com.ericsson.nms.security.nscs.capabilitymodel.service.NscsCapabilityModelService;

/**
 * Auxiliary class to give access to NSCS Capability Model Service outside the
 * node-security-jar.
 * 
 * @author emaborz
 * 
 */
public class NscsCapabilityModelServiceBean {

	@Inject
	private NscsCapabilityModelService capabilityModelService;

	/**
	 * Return an instance of NSCS Capability Model Service
	 * 
	 * @return the NSCS Capability Model Service
	 */
	public NscsCapabilityModelService getCapabilityModelService() {
		return this.capabilityModelService;
	}

}
