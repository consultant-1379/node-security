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
package com.ericsson.nms.security.nscs.api.pki;

import com.ericsson.nms.security.nscs.api.enums.CertificateType;

public enum NodeEntityCategory {

	OAM("oam"), IPSEC("ipsec");

	private final String nodeentitycategory;

	private NodeEntityCategory(final String nodeentitycategory) {
		this.nodeentitycategory = nodeentitycategory;
	}

	@Override
	public String toString() {
		return this.nodeentitycategory;
	}

	public static CertificateType toCertType(final NodeEntityCategory entityCategory) {
		CertificateType certType = null;
		switch (entityCategory) {
		case IPSEC:
			certType = CertificateType.IPSEC;
			break;
		case OAM:
			certType = CertificateType.OAM;
			break;
		default:
			break;
		}
		return certType;
	}

	public static NodeEntityCategory fromCertType(final CertificateType certificateType) {
		NodeEntityCategory nodeEntityCategory = null;
		switch (certificateType) {
		case IPSEC:
			nodeEntityCategory = NodeEntityCategory.IPSEC;
			break;
		case OAM:
			nodeEntityCategory = NodeEntityCategory.OAM;
			break;
		default:
			break;
		}
		return nodeEntityCategory;
	}
}
