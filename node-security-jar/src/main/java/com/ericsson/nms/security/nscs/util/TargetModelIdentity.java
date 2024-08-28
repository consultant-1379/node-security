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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ericsson.nms.security.nscs.api.exception.InvalidTargetModelIdentityException;

public class TargetModelIdentity {

	private static final Pattern OSS_MODEL_IDENTITY_PATTERN = Pattern.compile(String.format("^\\d+-\\d+-\\d+$"), Pattern.CASE_INSENSITIVE);
	private static final Pattern TARGET_MODEL_IDENTITY_PATTERN = Pattern.compile(String.format("^(\\w+)-(\\w+)-(R\\w+)$"), Pattern.CASE_INSENSITIVE);
	private static final Pattern TARGET_MODEL_IDENTITY_PATTERN_1 = Pattern.compile(String.format("^(\\w+)-(R\\w+)$"), Pattern.CASE_INSENSITIVE);
	private static final Pattern TARGET_MODEL_IDENTITY_PATTERN_2 = Pattern.compile(String.format("^(\\w+)-(\\w\\.\\d+\\.\\d+)$"), Pattern.CASE_INSENSITIVE);
	private static final Pattern TARGET_MODEL_IDENTITY_PATTERN_3 = Pattern.compile(String.format("^(R\\w+)-(\\w+)$"), Pattern.CASE_INSENSITIVE);
        private static final Pattern TARGET_MODEL_IDENTITY_PATTERN_4 = Pattern.compile(String.format("^(\\w+\\.\\w+)-(\\w\\.\\d+\\.\\d+)$"), Pattern.CASE_INSENSITIVE);
        private static final Pattern TARGET_MODEL_IDENTITY_PATTERN_5 = Pattern.compile(String.format("^(\\w+\\.\\w+)-(\\w+)$"), Pattern.CASE_INSENSITIVE);
        private static final Pattern TARGET_MODEL_IDENTITY_PATTERN_6 = Pattern.compile(String.format("^\\d+\\.\\d+\\.\\d+\\.\\d+$"), Pattern.CASE_INSENSITIVE);
        @SuppressWarnings("squid:S3457")
        private static final Pattern TARGET_MODEL_IDENTITY_PATTERN_7 = Pattern.compile(String.format("^\\d+\\.\\d+(.\\d)?$"), Pattern.CASE_INSENSITIVE);

	private boolean isOssModelIdentity = false;
	private String targetModelIdentity = null;
	private String nodeType = null;
	private String release = null;
	private String revision = null;

	/**
	 * @param modelIdentity
	 */
	public TargetModelIdentity(String modelIdentity)
			throws InvalidTargetModelIdentityException {

		if (modelIdentity != null && !modelIdentity.isEmpty()) {
			Matcher matcher = OSS_MODEL_IDENTITY_PATTERN.matcher(modelIdentity);
			if (matcher.find()) {
				this.targetModelIdentity = modelIdentity;
				this.isOssModelIdentity = true;
			} else {
				matcher = TARGET_MODEL_IDENTITY_PATTERN.matcher(modelIdentity);
				if (matcher.find()) {
					this.targetModelIdentity = modelIdentity;
					this.nodeType = matcher.group(1);
					this.release = matcher.group(2);
					this.revision = matcher.group(3);
				} else {
                                    matcher = TARGET_MODEL_IDENTITY_PATTERN_1.matcher(modelIdentity);
                                    if (matcher.find()) {
					this.targetModelIdentity = modelIdentity;
					this.release = matcher.group(1);
					this.revision = matcher.group(2);
                                    } else {
                                        matcher = TARGET_MODEL_IDENTITY_PATTERN_2.matcher(modelIdentity);
                                        if (matcher.find()) {
                                            this.targetModelIdentity = modelIdentity;
                                            this.release = matcher.group(1);
                                            this.revision = matcher.group(2);
                                        }else {
                                            matcher = TARGET_MODEL_IDENTITY_PATTERN_3.matcher(modelIdentity);
                                            if (matcher.find()) {
                                                this.targetModelIdentity = modelIdentity;
                                                this.release = matcher.group(1).substring(1);
                                                this.revision = matcher.group(2);
                                            }
                                                else {
                                                    matcher = TARGET_MODEL_IDENTITY_PATTERN_4.matcher(modelIdentity);
                                                    if (matcher.find()) {
                                                        this.targetModelIdentity = modelIdentity;
                                                        this.release = matcher.group(1);
                                                        this.revision = matcher.group(2);
                                                    }
                                                    else {
                                                        matcher = TARGET_MODEL_IDENTITY_PATTERN_5.matcher(modelIdentity);
                                                        if (matcher.find()) {
                                                            this.targetModelIdentity = modelIdentity;
                                                            this.release = matcher.group(1);
                                                            this.revision = matcher.group(2);
                                                        }
                                                        else {
                                                            matcher = TARGET_MODEL_IDENTITY_PATTERN_6.matcher(modelIdentity);
                                                            if (matcher.find()) {
                                                                this.targetModelIdentity = modelIdentity;
                                                                this.revision = matcher.group();
                                                                this.release = matcher.group();
                                                            } else {
                                                            	matcher = TARGET_MODEL_IDENTITY_PATTERN_7.matcher(modelIdentity);
                                                            	 if (matcher.find()) {
                                                                     this.targetModelIdentity = modelIdentity;
                                                                     this.revision = matcher.group();
                                                                     this.release = matcher.group();
                                                                 }
                                        else {
                                            String errorMsg = String.format("Invalid modelIdentity[%s]", modelIdentity);
                                            throw new InvalidTargetModelIdentityException(errorMsg);
                                        }
                                                            }
                                      }
                                      }
                                    }
                               }
   		          }
		      }
		  }
		} else {
			String errorMsg = String.format("Null or empty modelIdentity[%s]", modelIdentity);
			throw new InvalidTargetModelIdentityException(errorMsg);
		}
	}

	/**
	 * @return the isOssModelIdentity
	 */
	public boolean isOssModelIdentity() {
		return isOssModelIdentity;
	}

	/**
	 * @param isOssModelIdentity the isOssModelIdentity to set
	 */
	public void setOssModelIdentity(boolean isOssModelIdentity) {
		this.isOssModelIdentity = isOssModelIdentity;
	}

	/**
	 * @return the targetModelIdentity
	 */
	public String getTargetModelIdentity() {
		return targetModelIdentity;
	}

	/**
	 * @param targetModelIdentity the targetModelIdentity to set
	 */
	public void setTargetModelIdentity(String targetModelIdentity) {
		this.targetModelIdentity = targetModelIdentity;
	}
	/**
	 * @return the nodeType
	 */
	public String getNodeType() {
		return nodeType;
	}

	/**
	 * @param nodeType the nodeType to set
	 */
	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}

	/**
	 * @return the release
	 */
	public String getRelease() {
		return release;
	}

	/**
	 * @param release the release to set
	 */
	public void setRelease(String release) {
		this.release = release;
	}

	/**
	 * @return the revision
	 */
	public String getRevision() {
		return revision;
	}

	/**
	 * @param revision the revision to set
	 */
	public void setRevision(String revision) {
		this.revision = revision;
	}

	/**
	 * Check whether Model Identity is valid or not.
	 * 
	 * @return
	 */
	public boolean isValid() {
		if (this.targetModelIdentity == null
				|| (this.isOssModelIdentity == false && ( /*this.nodeType == null
						|| */ this.release == null || this.revision == null)))
			return false;
		return true;
	}

}
