package com.ericsson.nms.security.nscs.api.command.types;


/**
 * Representation of the set enrollment command
 * 
 */
public class SetEnrollmentCommand extends NscsNodeCommand {
	
	private static final long serialVersionUID = 7671362253676406244L;

	public static final String ENROLLMENT_MODE_PROPERTY = "enrollmentmode";

	/**
	 * @return the enrollmentModeProperty
	 */
	public static String getEnrollmentModeProperty() {
		return ENROLLMENT_MODE_PROPERTY;
	}

    
}