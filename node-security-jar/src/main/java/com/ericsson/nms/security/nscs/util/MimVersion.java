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
package com.ericsson.nms.security.nscs.util;

import com.ericsson.nms.security.nscs.api.exception.InvalidVersionException;

public class MimVersion implements Version {

	private static final long serialVersionUID = 1L;

	private String mimVersion = null;
	private String majorStr = null;
	private String minorStr = null;
	private String patchStr = null;
	private int majorInt = 0;
	private int minorInt = 0;
	private int patchInt = 0;

	/**
	 * Constructor from a MIM version String. Allowed formats are:
	 * <ul>
	 * <li>4.2.5</li>
	 * <li>D.2.5</li>
	 * <li>5.1.49</li>
	 * <li>E.1.49</li>
	 * </ul>
	 * @param mimVersion The MIM version string.
	 */
	public MimVersion(final String mimVersion) throws InvalidVersionException {
		
		String exceptionMessage = null;
		
		this.mimVersion = mimVersion;

		if (this.mimVersion != null && !this.mimVersion.isEmpty()) {
			try {
				final String[] versionParts = this.mimVersion.trim().split("\\.");
				if (versionParts.length == 3) {
					this.majorStr = versionParts[0];
					this.minorStr = versionParts[1];
					this.patchStr = versionParts[2];
					
					if (this.majorStr.matches("[A-Z]")) {
						char major = this.majorStr.charAt(0);
						this.majorInt = (int) major - (int) 'A' + 1;
					} else {
						this.majorInt = Integer.parseInt(this.majorStr);
					}
					this.minorInt = Integer.parseInt(this.minorStr);
					this.patchInt = Integer.parseInt(this.patchStr);
				} else {
					exceptionMessage = "Invalid format for MIM version: " + this.mimVersion;
					this.majorInt = 0;
					this.minorInt = 0;
					this.patchInt = 0;
				}
			} catch (Exception e) {
				exceptionMessage = "Caught exception while parsing MIM version: " + this.mimVersion;
				this.majorInt = 0;
				this.minorInt = 0;
				this.patchInt = 0;
			}
		} else {
			exceptionMessage = "Null or empty MIM version";
		}
		
		if (exceptionMessage != null) {
			throw new InvalidVersionException(exceptionMessage);
		}
	}

	/**
	 * @return the mimVersion
	 */
	public String getMimVersion() {
		return mimVersion;
	}

	/**
	 * @param mimVersion the mimVersion to set
	 */
	public void setMimVersion(String mimVersion) {
		this.mimVersion = mimVersion;
	}

	/**
	 * @return the majorStr
	 */
	public String getMajorStr() {
		return majorStr;
	}

	/**
	 * @param majorStr the majorStr to set
	 */
	public void setMajorStr(String majorStr) {
		this.majorStr = majorStr;
	}

	/**
	 * @return the minorStr
	 */
	public String getMinorStr() {
		return minorStr;
	}

	/**
	 * @param minorStr the minorStr to set
	 */
	public void setMinorStr(String minorStr) {
		this.minorStr = minorStr;
	}

	/**
	 * @return the patchStr
	 */
	public String getPatchStr() {
		return patchStr;
	}

	/**
	 * @param patchStr the patchStr to set
	 */
	public void setPatchStr(String patchStr) {
		this.patchStr = patchStr;
	}

	/**
	 * @return the majorInt
	 */
	public int getMajorInt() {
		return majorInt;
	}

	/**
	 * @param majorInt the majorInt to set
	 */
	public void setMajorInt(int majorInt) {
		this.majorInt = majorInt;
	}

	/**
	 * @return the minorInt
	 */
	public int getMinorInt() {
		return minorInt;
	}

	/**
	 * @param minorInt the minorInt to set
	 */
	public void setMinorInt(int minorInt) {
		this.minorInt = minorInt;
	}

	/**
	 * @return the patchInt
	 */
	public int getPatchInt() {
		return patchInt;
	}

	/**
	 * @param patchInt the patchInt to set
	 */
	public void setPatchInt(int patchInt) {
		this.patchInt = patchInt;
	}

	@Override
	public boolean isValid() {
		if (this.majorInt == 0 && this.minorInt == 0 && this.patchInt == 0)
			return false;
		return true;
	}

	@Override
	public boolean isLessThan(String version) throws InvalidVersionException {
		return isLessThanOrLessThanOrEqualTo(version, false);
	}

	@Override
	public boolean isLessThanOrEqualTo(String version) throws InvalidVersionException {
		return isLessThanOrLessThanOrEqualTo(version, true);
	}

	@Override
	public boolean isEqualTo(String version) throws InvalidVersionException {
		MimVersion mimVer = new MimVersion(version);
		if (this.getMajorInt() == mimVer.getMajorInt() &&
			this.getMinorInt() == mimVer.getMinorInt() &&
			this.getPatchInt() == mimVer.getPatchInt())
			return true;
		return false;
	}

	@Override
	public boolean isGreaterThan(String version) throws InvalidVersionException {
		return isGreaterThanOrGreaterThanOrEqualTo(version, false);
	}

	@Override
	public boolean isGreaterThanOrEqualTo(String version) throws InvalidVersionException {
		return isGreaterThanOrGreaterThanOrEqualTo(version, true);
	}
	
	private boolean isLessThanOrLessThanOrEqualTo(String version, boolean isEqualAllowed) throws InvalidVersionException {
		MimVersion mimVer = new MimVersion(version);
		if (this.getMajorInt() < mimVer.getMajorInt())
			return true;
		if (this.getMajorInt() == mimVer.getMajorInt()) {
			if (this.getMinorInt() < mimVer.getMinorInt())
				return true;
			if (this.getMinorInt() == mimVer.getMinorInt()) {
				if (this.getPatchInt() < mimVer.getPatchInt())
					return true;
				if (this.getPatchInt() == mimVer.getPatchInt() && isEqualAllowed)
					return true;
			}
		}
		return false;
	}
	
	private boolean isGreaterThanOrGreaterThanOrEqualTo(String version, boolean isEqualAllowed) throws InvalidVersionException {
		MimVersion mimVer = new MimVersion(version);
		if (this.getMajorInt() > mimVer.getMajorInt())
			return true;
		if (this.getMajorInt() == mimVer.getMajorInt()) {
			if (this.getMinorInt() > mimVer.getMinorInt())
				return true;
			if (this.getMinorInt() == mimVer.getMinorInt()) {
				if (this.getPatchInt() > mimVer.getPatchInt())
					return true;
				if ((this.getPatchInt() == mimVer.getPatchInt()) && isEqualAllowed) {
					return true;
				}
			}
		}
		return false;
	}

}
