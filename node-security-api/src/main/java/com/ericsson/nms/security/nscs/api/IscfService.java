/*
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 */

package com.ericsson.nms.security.nscs.api;

import com.ericsson.nms.security.nscs.api.cpp.level.CPPSecurityLevel;
import com.ericsson.nms.security.nscs.api.enums.EnrollmentMode;
import com.ericsson.nms.security.nscs.api.enums.SecurityLevel;
import com.ericsson.nms.security.nscs.api.exception.IscfServiceException;
import com.ericsson.nms.security.nscs.api.iscf.IpsecArea;
import com.ericsson.nms.security.nscs.api.iscf.IscfResponse;
import com.ericsson.nms.security.nscs.api.iscf.NodeIdentifier;
import com.ericsson.nms.security.nscs.api.iscf.SecurityDataResponse;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameFormat;
import com.ericsson.nms.security.nscs.api.iscf.SubjectAltNameParam;
import com.ericsson.nms.security.nscs.api.model.NodeModelInformation;
import com.ericsson.oss.itpf.sdk.core.annotation.EService;

import java.util.Set;
import java.net.StandardProtocolFamily;

import javax.ejb.Remote;

/**
 * This provides services to generate ISCF XML content.
 *
 * <p>
 * Following node auto-integration use cases are covered:
 * <ul>
 *     <li>O&amp;M</li>
 *     <li>IPSec Traffic</li>
 *     <li>IPSec O&amp;M</li>
 *     <li>IPSec Traffic + O&amp;M</li>
 *     <li>COMBO O&amp;M + IPSec Traffic</li>
 *     <li>COMBO O&amp;M + IPSec O&amp;M</li>
 *     <li>COMBO O&amp;M + IPSec Traffic + O&amp;M</li>
 * </ul>
 *
 * <p>
 * Following enrollment protocols are supported:
 * <ul>
 *     <li>SCEP</li>
 *     <li>CMPV2_VC</li>
 *     <li>CMPV2_INITIAL</li>
 * </ul>
 *
 * <p>
 * Following node types are supported:
 * <ul>
 *     <li>ERBS (CPP)</li>
 *     <li>MSRbs_V1 (COM/ECIM)</li>
 * </ul>
 *
 *
 * @author ENM/Skyfall Team
 */
@EService
@Remote
public interface IscfService {

    /**
     * Generate ISCF XML content that reflects the requested Security Level only, without
     * IPSec enabled. Wanted Level 3 is not supported
     *
     * The return type IscfResponse contains the XML content including <code>&lt;validators&gt;</code>,
     * the RBS Integrity Code used to encrypt the data and the Security Configuration Checksums
     *
     * <h1>Examples</h1>
     *
     * Level 2:
     * <pre>
     *     generate("logicalName", "fdn", CPPSecurityLevel.LEVEL_2, CPPSecurityLevel.LEVEL_2);
     * </pre>
     *
     * Level 3 (not supported in 14B):
     * <pre>
     *     generate("logicalName", "fdn", CPPSecurityLevel.LEVEL_3, CPPSecurityLevel.LEVEL_3);
     * </pre>
     *
     * @param logicalName BSIM name associated with the NE integration. Used by the NE at integration.
     * @param nodeFdn The FDN of the node undergoing auto-integration
     * @param wantedSecLevel The desired security level the node should be set to after auto-integration
     * @param minimumSecLevel The minimum security level the node should be left at should there be a
     *                        problem during auto-integration
     * @return IscfResponse
     * @throws IscfServiceException
     *              - the exception throws by IscfService
     * @deprecated
     * The corresponding generate method with extra parameters Enrollment Mode and Node Model Information shall be used instead.
     */
    @Deprecated
    IscfResponse generate(
        String logicalName,
        String nodeFdn,
        CPPSecurityLevel wantedSecLevel,
        CPPSecurityLevel minimumSecLevel
    ) throws IscfServiceException;

    /**
     * Generate ISCF XML content that reflects the IPSec areas to be enabled. IpsecArea types are passed as
     * a Set to ensure that only unique types are requested.
     *
     * The return type IscfResponse contains the XML content including <code>&lt;validators&gt;</code>,
     * the RBS Integrity Code used to encrypt the data and the Security Configuration Checksums
     *
     * <h1>Examples</h1>
     * <b>IPSec "Traffic":</b>
     * <pre>
     *     Set&lt;IpSecArea&gt; ipsecAreas = new HashSet&lt;&gt;();
     *     ipsecAreas.add(IpSecArea.TRANSPORT);
     *     generate("logicalName", "fdn", "iPSecCUSUserLabel", "1.2.3.4", SubjectAltNameType.IPV4, ipsecAreas);
     * </pre>
     *
     * <b>IPSec "Traffic" and "O&amp;M":</b>
     * <pre>
     *     Set&lt;IpsecArea&gt; ipsecAreas = new HashSet&lt;&gt;();
     *     ipsecAreas.add(IpsecArea.OM);
     *     ipsecAreas.add(IpsecArea.TRANSPORT);
     *     generate("logicalName", "fdn", "iPSecCUSUserLabel", "2001:0db8:85a3:0042:1000:8a2e:0370:7334", SubjectAltNameType.IPV6, ipsecAreas);
     * </pre>
     *
     * @param logicalName BSIM name associated with the NE integration. Used by the NE at integration.
     * @param nodeFdn The FDN of the node undergoing auto-integration
     * @param ipsecUserLabel The user label
     * @param ipsecSubjectAltName the Subject Alt Name
     * @param subjectAltNameFormat the Subject Alt Name Format
     * @param wantedIpSecAreas A collection of unique IpsecArea types indicating what areas of IPSec
     *                          is required
     * @return IscfResponse
     * @throws IscfServiceException
     *              - the exception throws by IscfService
     * @deprecated
     * The corresponding generate method with extra parameters Enrollment Mode and Node Model Information shall be used instead.
     */
    @Deprecated
    IscfResponse generate(
        String logicalName,
        String nodeFdn,
        String ipsecUserLabel,
        String ipsecSubjectAltName,
        SubjectAltNameFormat subjectAltNameFormat,
        Set<IpsecArea> wantedIpSecAreas
    ) throws IscfServiceException;

    /**
     * Generate ISCF XML content that reflects the requested Security Level and the IPSec areas to be
     * enabled. This method can be used to request both Level 2 as well as all IPSec areas. The requested
     * IpsecArea types are passed as a Set to ensure that only unique types are requested.
     *
     * The return type IscfResponse contains the XML content including <code>&lt;validators&gt;</code>,
     * the RBS Integrity Code used to encrypt the data and the Security Configuration Checksums
     *
     * <h1>Examples</h1>
     * <b>Level 2 and IPSec "Traffic":</b>
     * <pre>
     *     Set&lt;IpSecArea&gt; ipsecAreas = new HashSet&lt;&gt;();
     *     ipsecAreas.add(IpSecArea.TRANSPORT);
     *     generate("logicalName", "fdn", CPPSecurityLevel.LEVEL_2, CPPSecurityLevel.LEVEL_2,
     *              "userLabel", "192.168.0.1", ipsecAreas);
     * </pre>
     *
     * <b>Level 3 (not supported in 14B) and IPSec "O&amp;M" (supported in 14B):</b>
     * <pre>
     *     Set&lt;IpSecArea&gt; ipsecAreas = new HashSet&lt;&gt;();
     *     ipsecAreas.add(IpSecArea.OM);
     *     generate("logicalName", "fdn", CPPSecurityLevel.LEVEL_3, CPPSecurityLevel.LEVEL_3,
     *              "userLabel", "2001:0db8:85a3:0042:1000:8a2e:0370:7334", ipsecAreas);
     * </pre>
     *
     * <b>Level 2 and IPSec "Traffic" and "O&amp;M":</b>
     * <pre>
     *     Set&lt;IpSecArea&gt; ipsecAreas = new HashSet&lt;&gt;();
     *     ipsecAreas.add(IpSecArea.OM);
     *     ipsecAreas.add(IpSecArea.TRANSPORT);
     *     generate("logicalName", "fdn", CPPSecurityLevel.LEVEL_2, CPPSecurityLevel.LEVEL_2,
     *              "userLabel", "1.2.3.4", SubjectAltNameType.IPV4, "MeContext=NODE_NAME", ipsecAreas);
     * </pre>
     *
     * @param logicalName BSIM name associated with the NE integration. Used by the NE at integration.
     * @param nodeFdn The fdn of the node undergoing auto-integration
     * @param wantedSecLevel The desired security level the node should be set to after auto-integration
     * @param minimumSecLevel The minimum security level the node should be left at should there be a
     *                        problem during auto-integration
     * @param ipsecUserLabel The user label
     * @param ipsecSubjectAltName the Subject Alt Name
     * @param subjectAltNameFormat the Subject Alt Name Format
     * @param wantedIpSecAreas A collection of unique IpsecArea types indicating what areas of IPSec
     *                          is required
     * @return IscfResponse
     * @throws IscfServiceException
     *              - the exception throws by IscfService
     * @deprecated
     * The corresponding generate method with extra parameters Enrollment Mode and Node Model Information shall be used instead.
     */
    @Deprecated
    IscfResponse generate(
        String logicalName,
        String nodeFdn,
        CPPSecurityLevel wantedSecLevel,
        CPPSecurityLevel minimumSecLevel,
        String ipsecUserLabel,
        String ipsecSubjectAltName,
        SubjectAltNameFormat subjectAltNameFormat,
        Set<IpsecArea> wantedIpSecAreas
    ) throws IscfServiceException;

    /**
     * Generate ISCF XML content for O&amp;M that reflects the requested Security Level,
     * the requested Enrollment Mode and the Model Information of the involved node.
     * <p>
     * The Node Model Information, used to calculate the length of the generated keys, contains:
     * <ul>
     *     <li>the Model Identifier Type, the type of the Model Identifier</li>
     *     <li>
     *     the Model Identifier can contain: the MIM Version (CPP nodes) or the Product Number (COM/ECIM nodes)
     *     </li>
     *     <li>
     *     the Node Type, a case-insensitive string containing the involved node type:
     *     supported value are "ERBS" (CPP) and "MSRbs_V1" (COM/ECIM)
     *     </li>
     * </ul>
     *
     * <p>
     * The MIM version (CPP) has format "major.minor.patch" where:
     * <ul>
     *     <li>major can be either a single upper case literal [A-Z] or a numeric</li>
     *     <li>minor can be a numeric</li>
     *     <li>patch can be a numeric</li>
     * </ul>
     *
     * <p>
     * The Product Number (COM/ECIM) has a to-be-defined format (let's use "CXPxxyyzz").
     * </p>
     * <p>
     * For CPP nodes, Wanted and Minimum Security Level 3 is not supported.
     * For COM/ECIM nodes, Wanted and Minimum Security Level are meaningless (LEVEL_NOT_SUPPORTED should be used).
     * </p>
     *
     * The return type IscfResponse contains the XML content including <code>&lt;validators&gt;</code>,
     * the RBS Integrity Code used to encrypt the data and the Security Configuration Checksums
     *
     * <h1>Examples</h1>
     * <b>O&amp;M, Level 2, Enrollment Mode SCEP, node type "ERBS", MIM version "E.1.49"</b>
     * <pre>
     NodeModelInformation modelInfo = new NodeModelInformation("E.1.49", ModelIdentifierType.MIM_VERSION, "ERBS");
     generate("logicalName", "fdn", CPPSecurityLevel.LEVEL_2, CPPSecurityLevel.LEVEL_2,
              EnrollmentMode.SCEP, modelInfo);
 </pre>
     * 
     * <b>O&amp;M, Enrollment Mode CMPV2_VC, node type "MSRbs_V1", product number "CXPxxyyzz"</b>
     * <pre>
     NodeModelInformation modelInfo = new NodeModelInformation("CXPxxyyzz", ModelIdentifierType.PRODUCT_NUMBER, "MSRbs_V1");
     generate("logicalName", "fdn", CPPSecurityLevel.LEVEL_NOT_SUPPORTED, CPPSecurityLevel.LEVEL_NOT_SUPPORTED, 
              EnrollmentMode.CMPV2_VC, modelInfo);
 </pre>
     * 
     * <b>O&amp;M, Level 2, Enrollment Mode CMPV2_INITIAL, node type "ERBS", MIM version "5.1.50"</b>
     * <pre>
     NodeModelInformation modelInfo = new NodeModelInformation("5.1.50", ModelIdentifierType.MIM_VERSION, "ERBS");
     generate("logicalName", "fdn", CPPSecurityLevel.LEVEL_2, CPPSecurityLevel.LEVEL_2,
              EnrollmentMode.CMPV2_INITIAL, modelInfo);
 </pre>
     *
     * <b>O&amp;M, Level 3 (not supported in 15B), Enrollment Mode SCEP, node type "ERBS", MIM version "E.1.49"</b>
     * <pre>
     NodeModelInformation modelInfo = new NodeModelInformation("E.1.49", ModelIdentifierType.MIM_VERSION, "ERBS");
     generate("logicalName", "fdn", CPPSecurityLevel.LEVEL_3, CPPSecurityLevel.LEVEL_3,
              EnrollmentMode.SCEP, modelInfo);
 </pre>
     * 
     * <b>O&amp;M, Level 3 (not supported in 15B), Enrollment Mode CMPV2_VC, node type "ERBS", MIM version "E.1.49"</b>
     * <pre>
     NodeModelInformation modelInfo = new NodeModelInformation("E.1.49", ModelIdentifierType.MIM_VERSION, "ERBS");
     generate("logicalName", "fdn", CPPSecurityLevel.LEVEL_3, CPPSecurityLevel.LEVEL_3,
              EnrollmentMode.CMPV2_VC, modelInfo);
 </pre>
     * 
     * <b>O&amp;M, Level 3 (not supported in 15B), Enrollment Mode CMPV2_INITIAL, node type "ERBS", MIM version "E.1.49"</b>
     * <pre>
     NodeModelInformation modelInfo = new NodeModelInformation("E.1.49", ModelIdentifierType.MIM_VERSION, "ERBS");
     generate("logicalName", "fdn", CPPSecurityLevel.LEVEL_3, CPPSecurityLevel.LEVEL_3,
              EnrollmentMode.CMPV2_INITIAL, modelInfo);
 </pre>
     *
     * @param logicalName BSIM name associated with the NE integration. Used by the NE at integration.
     * @param nodeFdn The FDN of the node undergoing auto-integration
     * @param wantedSecLevel The desired security level the node should be set to after auto-integration
     * @param minimumSecLevel The minimum security level the node should be left at, should there be a
     *                        problem during auto-integration, or LEVEL_NOT_SUPPORTED for COM/ECIM
     * @param wantedEnrollmentMode The desired enrollment mode for the node that will be written in the 
     *                             ISCF file or LEVEL_NOT_SUPPORTED for COM/ECIM
     * @param modelInfo The Node Model Information
     * @return IscfResponse
     * @throws IscfServiceException
     *           - the exception throws by IscfService
     */
    IscfResponse generate(
        String logicalName,
        String nodeFdn,
        SecurityLevel wantedSecLevel,
        SecurityLevel minimumSecLevel,
        EnrollmentMode wantedEnrollmentMode,
        NodeModelInformation modelInfo
    ) throws IscfServiceException;

    /**
     * Generate ISCF XML content for IPSec that reflects the IPSec areas to be enabled,
     * the requested Enrollment Mode and the Model Information of the involved node.
     * IpsecArea types are passed as a Set to ensure that only unique types are requested.
     * <p>
     * The Node Model Information, used to calculate the length of the generated keys, contains:
     * <ul>
     *     <li>the Model Identifier Type, the type of the Model Identifier</li>
     *     <li>
     *     the Model Identifier can contain: the MIM Version (CPP nodes) or the Product Number (COM/ECIM nodes)
     *     </li>
     *     <li>
     *     the Node Type, a case-insensitive string containing the involved node type:
     *     supported value are "ERBS" (CPP) and "MSRbs_V1" (COM/ECIM)
     *     </li>
     * </ul>
     *
     * <p>
     * The MIM version (CPP) has format "major.minor.patch" where:
     * <ul>
     *     <li>major can be either a single upper case literal [A-Z] or a numeric</li>
     *     <li>minor can be a numeric</li>
     *     <li>patch can be a numeric</li>
     * </ul>
     *
     * <p>
     * The Product Number (COM/ECIM) has a to-be-defined format (let's use "CXPxxyyzz").
     * </p>
     *
     * The return type IscfResponse contains the XML content including <code>&lt;validators&gt;</code>,
     * the RBS Integrity Code used to encrypt the data and the Security Configuration Checksums
     *
     * <h1>Examples</h1>
     * <b>IPSec "Traffic", Enrollment Mode SCEP, node type "ERBS", MIM version "E.1.49"</b>
     * <pre>
     *     Set&lt;IpSecArea&gt; ipsecAreas = new HashSet&lt;&gt;();
     ipsecAreas.add(IpSecArea.TRANSPORT);
     NodeModelInformation modelInfo = new NodeModelInformation("E.1.49", ModelIdentifierType.MIM_VERSION, "ERBS");
     generate("logicalName", "fdn", "iPSecCUSUserLabel", "1.2.3.4", SubjectAltNameType.IPV4, ipsecAreas, 
              EnrollmentMode.SCEP, modelInfo);
 </pre>
     *
     * <b>IPSec "Traffic", Enrollment Mode CMPV2_INITIAL, node type "MSRbs_V1", Product Number "CXPxxyyzz"</b>
     * <pre>
     *     Set&lt;IpSecArea&gt; ipsecAreas = new HashSet&lt;&gt;();
     ipsecAreas.add(IpSecArea.TRANSPORT);
     NodeModelInformation modelInfo = new NodeModelInformation("CXPxxyyzz", ModelIdentifierType.PRODUCT_NUMBER, "MSRbs_V1");
     generate("logicalName", "fdn", "iPSecCUSUserLabel", "1.2.3.4", SubjectAltNameType.IPV4, ipsecAreas, 
              EnrollmentMode.CMPV2_INITIAL, modelInfo);
 </pre>
     *
     * <b>IPSec "Traffic" and "O&amp;M", Enrollment Mode SCEP, node type "MSRbs_V1", Product Number "CXPxxyyzz"</b>
     * <pre>
     *     Set&lt;IpsecArea&gt; ipsecAreas = new HashSet&lt;&gt;();
     ipsecAreas.add(IpsecArea.OM);
     ipsecAreas.add(IpsecArea.TRANSPORT);
     NodeModelInformation modelInfo = new NodeModelInformation("CXPxxyyzz", ModelIdentifierType.PRODUCT_NUMBER, "MSRbs_V1");
     generate("logicalName", "fdn", "iPSecCUSUserLabel", "2001:0db8:85a3:0042:1000:8a2e:0370:7334", 
              SubjectAltNameType.IPV6, ipsecAreas, EnrollmentMode.SCEP, modelInfo);
 </pre>
     *
     * <b>IPSec "Traffic", Enrollment Mode CMPV2_VC, version "E.1.49", node type "ERBS"</b>
     * <pre>
     *     Set&lt;IpsecArea&gt; ipsecAreas = new HashSet&lt;&gt;();
     ipsecAreas.add(IpsecArea.TRANSPORT);
     NodeModelInformation modelInfo = new NodeModelInformation("E.1.49", ModelIdentifierType.MIM_VERSION, "ERBS");
     generate("logicalName", "fdn", "iPSecCUSUserLabel", "1.2.3.4", SubjectAltNameType.IPV4, ipsecAreas, 
              EnrollmentMode.CMPV2_VC, modelInfo);
 </pre>
     *
     * @param logicalName BSIM name associated with the NE integration. Used by the NE at integration.
     * @param nodeFdn The FDN of the node undergoing auto-integration
     * @param ipsecUserLabel The user label
     * @param ipsecSubjectAltName the Subject Alternative Name
     * @param wantedIpSecAreas A collection of unique IpsecArea types indicating what areas of IPSec
     *                          is required
     * @param wantedEnrollmentMode The desired enrollment mode for the node that will be written in the 
     *                             ISCF file
     * @param modelInfo The Node Model Information
     * @return IscfResponse
     * @throws IscfServiceException
     *              - the exception throws by IscfService
     */
    IscfResponse generate(
        String logicalName,
        String nodeFdn,
        String ipsecUserLabel,
        SubjectAltNameParam ipsecSubjectAltName,
        Set<IpsecArea> wantedIpSecAreas,
        EnrollmentMode wantedEnrollmentMode,
        NodeModelInformation modelInfo
    ) throws IscfServiceException;

    /**
     * Generate ISCF XML content for COMBO (O&amp;M and IPSec) that reflects the requested Security Level,
     * the IPSec areas to be enabled, the requested Enrollment Mode and the Model Information of the involved node.
     * This method can be used to request both Level 2 (O&amp;M) as well as all IPSec areas.
     * The requested IpsecArea types are passed as a Set to ensure that only unique types are requested.
     * <p>
     * The Node Model Information, used to calculate the length of the generated keys, contains:
     * <ul>
     *     <li>the Model Identifier Type, the type of the Model Identifier</li>
     *     <li>
     *     the Model Identifier can contain: the MIM Version (CPP nodes) or the Product Number (COM/ECIM nodes)
     *     </li>
     *     <li>
     *     the Node Type, a case-insensitive string containing the involved node type:
     *     supported value are "ERBS" (CPP) and "MSRbs_V1" (COM/ECIM)
     *     </li>
     * </ul>
     *
     * <p>
     * The MIM version (CPP) has format "major.minor.patch" where:
     * <ul>
     *     <li>major can be either a single upper case literal [A-Z] or a numeric</li>
     *     <li>minor can be a numeric</li>
     *     <li>patch can be a numeric</li>
     * </ul>
     *
     * <p>
     * The Product Number (COM/ECIM) has a to-be-defined format (let's use "CXPxxyyzz").
     * </p>
     * <p>
     * For CPP nodes, Wanted and Minimum Security Level 3 is not supported.
     * For COM/ECIM nodes, Wanted and Minimum Security Level are meaningless (LEVEL_NOT_SUPPORTED should be used).
     * </p>
     *
     * The return type IscfResponse contains the XML content including <code>&lt;validators&gt;</code>,
     * the RBS Integrity Code used to encrypt the data and the Security Configuration Checksums
     *
     * <h1>Examples</h1>
     * <b>COMBO (O&amp;M Level 2, IPSec "Traffic") with Enrollment Mode SCEP, node type "ERBS", MIM version "E.1.49"</b>
     * <pre>
     *     Set&lt;IpSecArea&gt; ipsecAreas = new HashSet&lt;&gt;();
     ipsecAreas.add(IpSecArea.TRANSPORT);
     NodeModelInformation modelInfo = new NodeModelInformation("E.1.49", ModelIdentifierType.MIM_VERSION, "ERBS");
     generate("logicalName", "fdn", CPPSecurityLevel.LEVEL_2, CPPSecurityLevel.LEVEL_2,
              "userLabel", "192.168.0.1", ipsecAreas, EnrollmentMode.SCEP, modelInfo);
 </pre>
     *
     * <b>COMBO (O&amp;M Level 3 (not supported in 15B), IPSec "O&amp;M"), Enrollment Mode SCEP, node type "ERBS", MIM version "E.1.49"</b>
     * <pre>
     *     Set&lt;IpSecArea&gt; ipsecAreas = new HashSet&lt;&gt;();
     ipsecAreas.add(IpSecArea.OM);
     NodeModelInformation modelInfo = new NodeModelInformation("E.1.49", ModelIdentifierType.MIM_VERSION, "ERBS");
     generate("logicalName", "fdn", CPPSecurityLevel.LEVEL_3, CPPSecurityLevel.LEVEL_3,
              "userLabel", "2001:0db8:85a3:0042:1000:8a2e:0370:7334", ipsecAreas, 
              EnrollmentMode.SCEP, modelInfo);
 </pre>
     *
     * <b>COMBO (O&amp;M and IPSec "Traffic" and "O&amp;M"), Enrollment Mode SCEP, node type "MSRbs_V1", Product Number "CXPxxyyzz"</b>
     * <pre>
     *     Set&lt;IpSecArea&gt; ipsecAreas = new HashSet&lt;&gt;();
     ipsecAreas.add(IpSecArea.OM);
     ipsecAreas.add(IpSecArea.TRANSPORT);
     NodeModelInformation modelInfo = new NodeModelInformation("CXPxxyyzz", ModelIdentifierType.PRODUCT_NUMBER, "MSRbs_V1");
     generate("logicalName", "fdn", CPPSecurityLevel.LEVEL_NOT_SUPPORTED, CPPSecurityLevel.LEVEL_NOT_SUPPORTED,
              "userLabel", "1.2.3.4", SubjectAltNameType.IPV4, "MeContext=NODE_NAME", ipsecAreas, 
              EnrollmentMode.SCEP, modelInfo);
 </pre>
     *
     * <b>COMBO (O&amp;M Level 2, IPSec "Traffic", Enrollment Mode CMPV2_INITIAL, node type "ERBS", MIM version "E.1.49"</b>
     * <pre>
     *     Set&lt;IpSecArea&gt; ipsecAreas = new HashSet&lt;&gt;();
     ipsecAreas.add(IpSecArea.TRANSPORT);
     NodeModelInformation modelInfo = new NodeModelInformation("E.1.49", ModelIdentifierType.MIM_VERSION, "ERBS");
     generate("logicalName", "fdn", CPPSecurityLevel.LEVEL_2, CPPSecurityLevel.LEVEL_2,
              "userLabel", "192.168.0.1", ipsecAreas,
              EnrollmentMode.CMPV2_INITIAL, modelInfo);
 </pre>
     *
     * @param logicalName BSIM name associated with the NE integration. Used by the NE at integration.
     * @param nodeFdn The FDN of the node undergoing auto-integration
     * @param wantedSecLevel The desired security level the node should be set to after auto-integration
     * @param minimumSecLevel The minimum security level the node should be left at should there be a
     *                        problem during auto-integration
     * @param ipsecUserLabel The user label
     * @param ipsecSubjectAltName the Subject Alternative Name
     * @param wantedIpSecAreas A collection of unique IpsecArea types indicating what areas of IPSec
     *                          is required
     * @param wantedEnrollmentMode The desired enrollment mode for the node that will be written in the 
     *                             ISCF file
     * @param modelInfo The Node Model Information
     * @return IscfResponse
     * @throws IscfServiceException
     *              - the exception throws by IscfService
     */
    IscfResponse generate(
        String logicalName,
        String nodeFdn,
        SecurityLevel wantedSecLevel,
        SecurityLevel minimumSecLevel,
        String ipsecUserLabel,
        SubjectAltNameParam ipsecSubjectAltName,
        Set<IpsecArea> wantedIpSecAreas,
        EnrollmentMode wantedEnrollmentMode,
        NodeModelInformation modelInfo
    ) throws IscfServiceException;

    /**
     * Cleanup needed if auto-integration is canceled for this node. This
     * removes any end entities created in PKI for the node
     *
     * @param fdn The FDN of the node
     * @throws IscfServiceException
     *              - the exception throws by IscfService
     */
    void cancel(String fdn) throws IscfServiceException;
    
    /**
     * Generate OAM auto-integration Security Data for a node
     * @param nodeFdn The FDN of the node undergoing auto-integration
     * @param wantedEnrollmentMode The desired enrollment mode for the node 
     * @param modelInfo The Node Model Information
     * @return SecurityDataResponse
     * @throws IscfServiceException
     *              - the exception throws by IscfService
     */
    @Deprecated
    SecurityDataResponse generateSecurityDataOam(
            String nodeFdn,
            EnrollmentMode wantedEnrollmentMode,
            NodeModelInformation modelInfo
    ) throws IscfServiceException;

    /**
     * Generate IPSEC auto-integration Security Data for a node
     * @param nodeFdn The FDN of the node undergoing auto-integration
     * @param ipsecSubjectAltName The Subject Alternative Name
     * @param wantedEnrollmentMode The desired enrollment mode for the node 
     * @param modelInfo The Node Model Information
     * @return SecurityDataResponse
     * @throws IscfServiceException
     *              - the exception throws by IscfService
     */
    @Deprecated
    SecurityDataResponse generateSecurityDataIpsec(
            String nodeFdn,
            SubjectAltNameParam ipsecSubjectAltName,
            EnrollmentMode wantedEnrollmentMode,
            NodeModelInformation modelInfo
    ) throws IscfServiceException;

    /**
     * Generate OAM and IPSEC auto-integration Security Data for a node
     * @param nodeFdn The FDN of the node undergoing auto-integration
     * @param ipsecSubjectAltName The Subject Alternative Name
     * @param wantedEnrollmentMode The desired enrollment mode for the node 
     * @param modelInfo The Node Model Information
     * @return SecurityDataResponse
     * @throws IscfServiceException
     *              - the exception throws by IscfService
     */
    @Deprecated
    SecurityDataResponse generateSecurityDataCombo(
            String nodeFdn,
            SubjectAltNameParam ipsecSubjectAltName,
            EnrollmentMode wantedEnrollmentMode,
            NodeModelInformation modelInfo
    ) throws IscfServiceException;

    /**
     * Generate OAM auto-integration Security Data for a node, without Subject Alternative Name. 
     * @param nodeId The identifier information of the node undergoing auto-integration
     * @param wantedEnrollmentMode The desired enrollment mode for the node 
     * @param modelInfo The Node Model Information
     * @return SecurityDataResponse
     * @throws IscfServiceException
     *              - the exception throws by IscfService
     */
    SecurityDataResponse generateSecurityDataOam(
            NodeIdentifier nodeId,
            EnrollmentMode wantedEnrollmentMode,
            NodeModelInformation modelInfo
    ) throws IscfServiceException;

    /**
     * Generate OAM auto-integration Security Data for a node, specifying Subject Alternative Name parameter.
     *
     * @param nodeId
     *            The identifier information of the node undergoing auto-integration.
     * @param subjectAltName
     *            The Subject Alternative Name. Mandatory parameter (must be not null.)
     * @param wantedEnrollmentMode
     *            The desired enrollment mode for the node. If null, default value is got from Capability Model.
     * @param modelInfo
     *            The Node Model Information
     * @return SecurityDataResponse
     * @throws IscfServiceException
     *              - the exception throws by IscfService
     */
    SecurityDataResponse generateSecurityDataOam(
            NodeIdentifier nodeId, 
            SubjectAltNameParam subjectAltName, 
            EnrollmentMode wantedEnrollmentMode,
            NodeModelInformation modelInfo
    ) throws IscfServiceException;

    /**
     * Generate IPSEC auto-integration Security Data for a node
     * @param nodeId The identifier information of the node undergoing auto-integration
     * @param ipsecSubjectAltName The Subject Alternative Name
     * @param wantedEnrollmentMode The desired enrollment mode for the node 
     * @param modelInfo The Node Model Information
     * @return SecurityDataResponse
     * @throws IscfServiceException
     *              - the exception throws by IscfService
     */
    SecurityDataResponse generateSecurityDataIpsec(
            NodeIdentifier nodeId,
            SubjectAltNameParam ipsecSubjectAltName,
            EnrollmentMode wantedEnrollmentMode,
            NodeModelInformation modelInfo
    ) throws IscfServiceException;

    /**
     * Generate OAM and IPSEC auto-integration Security Data for a node
     * @param nodeId The identifier information of the node undergoing auto-integration
     * @param ipsecSubjectAltName The Subject Alternative Name
     * @param wantedEnrollmentMode The desired enrollment mode for the node 
     * @param modelInfo The Node Model Information
     * @return SecurityDataResponse
     * @throws IscfServiceException
     *              - the exception throws by IscfService
     */
    SecurityDataResponse generateSecurityDataCombo(
            NodeIdentifier nodeId,
            SubjectAltNameParam ipsecSubjectAltName,
            EnrollmentMode wantedEnrollmentMode,
            NodeModelInformation modelInfo
    ) throws IscfServiceException;

    /**
     * Generate OAM auto-integration Security Data for a node, without Subject Alternative Name.
     *
     * @param nodeId
     *            The identifier information of the node undergoing auto-integration.
     * @param wantedEnrollmentMode
     *            The desired enrollment mode for the node. If null, default value is got from Capability Model.
     * @param modelInfo
     *            The Node Model Information
     * @param ipVersion
     *            The IP version (INET / INET6) to select the suitable enrollment server URI. If null, IP version is retrieved from  ConnectivityInformation
     * @return SecurityDataResponse
     * @throws IscfServiceException
     *              - the exception throws by IscfService
     */
    SecurityDataResponse generateSecurityDataOam(
            NodeIdentifier nodeId,
            EnrollmentMode wantedEnrollmentMode,
            NodeModelInformation modelInfo,
            StandardProtocolFamily ipVersion
    );

    /**
     * Generate OAM auto-integration Security Data for a node, specifying Subject Alternative Name parameter.
     *
     * @param nodeId
     *            The identifier information of the node undergoing auto-integration.
     * @param subjectAltName
     *            The Subject Alternative Name. Mandatory parameter (must be not null.)
     * @param wantedEnrollmentMode
     *            The desired enrollment mode for the node. If null, default value is got from Capability Model.
     * @param modelInfo
     *            The Node Model Information
     * @param ipVersion
     *            The IP version (INET / INET6) to select the suitable enrollment server URI. If null, IP version is retrieved from  ConnectivityInformation
     * @return SecurityDataResponse
     * @throws IscfServiceException
     *              - the exception throws by IscfService
     */
    SecurityDataResponse generateSecurityDataOam(
            NodeIdentifier nodeId,
            SubjectAltNameParam subjectAltName,
            EnrollmentMode wantedEnrollmentMode,
            NodeModelInformation modelInfo,
            StandardProtocolFamily ipVersion
    );

    /**
     * Generate IPSEC auto-integration Security Data for a node
     * @param nodeId The identifier information of the node undergoing auto-integration
     * @param ipsecSubjectAltName The Subject Alternative Name
     * @param wantedEnrollmentMode The desired enrollment mode for the node
     * @param modelInfo The Node Model Information
     * @param ipVersion
     *            The IP version (INET / INET6) to select the suitable enrollment server URI. If null, IP version is retrieved from  ConnectivityInformation
     * @return SecurityDataResponse
     * @throws IscfServiceException
     *              - the exception throws by IscfService
     */
    SecurityDataResponse generateSecurityDataIpsec(
            NodeIdentifier nodeId,
            SubjectAltNameParam ipsecSubjectAltName,
            EnrollmentMode wantedEnrollmentMode,
            NodeModelInformation modelInfo,
            StandardProtocolFamily ipVersion
    );

    /**
     * Generate OAM and IPSEC auto-integration Security Data for a node
     * @param nodeId The identifier information of the node undergoing auto-integration
     * @param ipsecSubjectAltName The Subject Alternative Name
     * @param wantedEnrollmentMode The desired enrollment mode for the node
     * @param modelInfo The Node Model Information
     * @param ipVersion
     *            The IP version (INET / INET6) to select the suitable enrollment server URI. If null, IP version is retrieved from  ConnectivityInformation
     * @return SecurityDataResponse
     * @throws IscfServiceException
     *              - the exception throws by IscfService
     */
    SecurityDataResponse generateSecurityDataCombo(
            NodeIdentifier nodeId,
            SubjectAltNameParam ipsecSubjectAltName,
            EnrollmentMode wantedEnrollmentMode,
            NodeModelInformation modelInfo,
            StandardProtocolFamily ipVersion
    );

}
