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
package com.ericsson.nms.security.nscs.data.moget;

import java.util.List;
import com.ericsson.nms.security.nscs.api.exception.SecurityMODoesNotExistException;
import com.ericsson.nms.security.nscs.api.exception.TrustCategoryMODoesNotExistException;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.moaction.MoActionState;
import com.ericsson.nms.security.nscs.data.moaction.MoActionWithParameter;
import com.ericsson.nms.security.nscs.data.moaction.MoActionWithoutParameter;
import com.ericsson.nms.security.nscs.data.moget.param.CertStateInfo;
import com.ericsson.nms.security.nscs.data.moget.param.NtpServer;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;

/**
 * Interface to provide MO Get operations on nodes.
 *
 */
public interface MOGetService {

    /**
     * Perform get of certificate enrollment state for the given node and the
     * given certificate type.
     *
     * Some details of certificate itself are returned as well.
     *
     * @param nodeRef
     * @param certType
     * @return
     */
    public CertStateInfo getCertificateIssueStateInfo(final NodeReference nodeRef, final String certType);

    /**
     * Perform get of trusted certificate install state for the given node and
     * the given trustCategory type.
     *
     * Some details of trusted certificates are returned as well.
     *
     * @param nodeRef
     * @param trustCategory
     * @return
     */
    public CertStateInfo getTrustCertificateStateInfo(final NodeReference nodeRef, final String trustCategory);

    /**
     * Gets the securityLevel of a node given its current nodeRef and syncstatus
     *
     * @author egicass
     * @param nodeRef
     * @param syncstatus
     * @return the actual securityLevel
     */
    public String getSecurityLevel(final NormalizableNodeReference nodeRef, final String syncstatus);

    /**
     * Returns the current IPSec configuration of the node
     *
     * @param normNode
     * @param syncstatus
     * @return the actual ipsec configuration
     */
    public String getIpsecConfig(final NormalizableNodeReference normNode, final String syncstatus);

    /**
     * Return progress state of specified MO action without parameters performed
     * on given MO (specified by FDN).
     *
     * @param moFdn
     * @param action
     * @return
     */
    public MoActionState getMoActionState(final String moFdn, final MoActionWithoutParameter action);

    /**
     * Return progress state of specified MO action with parameters performed on
     * given MO (specified by FDN).
     *
     * @param nodeRef
     * @param moFdn
     * @return
     */
    public MoActionState getMoActionState(final String moFdn, final MoActionWithParameter action);

    /**
     * Return crlCheck or certRevStatusCheck MO Attribute value
     *
     * @param normNode
     * @param certType
     *
     * @return the MO Attribute Value
     */
    public String getCrlCheckStatus(final NormalizableNodeReference normNode, final String certType);

    /**
     * To validate node for crl check MO existance
     *
     * @param normNode
     * @param certType
     */
    public boolean validateNodeForCrlCheckMO(final NormalizableNodeReference normNode, final String certType) throws SecurityMODoesNotExistException, TrustCategoryMODoesNotExistException;

    /**
     * Returns ntp key ids of the node
     *
     * @param normNode
     *            normalizable node reference
     * @return list of NTP server Key Id's
     */
    public List<NtpServer> listNtpServerDetails(final NormalizableNodeReference normNode);

    /**
     * To validate the node for NTP support by checking the NTP server related MO attributes
     *
     * @param normNode
     *            normalizable node reference
     */
    public boolean validateNodeForNtp(final NormalizableNodeReference nodeRef);

    /**
     * Convert the given keySize from the NSCS normalized value to the node supported value for the given node.
     *
     * @param nodeRef
     *            node reference
     * @param keySize
     *            the NSCS normalized key size value
     *
     * @return the node supported key size value
     */
    public String getNodeSupportedFormatOfKeyAlgorithm(final NodeReference nodeRef, final String keySize);
}