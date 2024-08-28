/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.workflow.task.cpp.moaction;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.cpp.level.CppIpSecService;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.cpp.ipsec.util.SmrsUtils;
import com.ericsson.nms.security.nscs.cpp.ipsec.util.XmlOperatorUtils;
import com.ericsson.nms.security.nscs.cpp.ipsec.util.XmlOperatorUtils.SummaryXmlInfo;
import com.ericsson.nms.security.nscs.cpp.model.RbsConfigInfo;
import com.ericsson.nms.security.nscs.cpp.model.SmrsAccountInfo;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityService;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.moaction.MOActionService;
import com.ericsson.nms.security.nscs.data.moaction.MoActionWithParameter;
import com.ericsson.nms.security.nscs.data.moaction.param.MoParams;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.workflow.task.WFQueryTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskResult;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.itpf.sdk.recording.ErrorSeverity;
import com.ericsson.oss.itpf.sdk.recording.SystemRecorder;
import com.ericsson.oss.services.nscs.workflow.tasks.api.exception.WorkflowTaskException;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction.DeactivateIpSecTask;

/**
 * <p>
 * Task handler for WorkflowTaskType.CPP_DEACTIVATE_IPSEC.
 * </p>
 * <p>
 * Deactivate IpSec on node
 * </p>
 *
 * @author esneani
 */
@WFTaskType(WorkflowTaskType.CPP_DEACTIVATE_IPSEC)
@Local(WFTaskHandlerInterface.class)
public class DeactivateIpSecTaskHandler implements WFQueryTaskHandler<DeactivateIpSecTask>, WFTaskHandlerInterface {

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private SystemRecorder systemRecorder;

    @Inject
    private MOActionService moAction;

    @Inject
    private CppSecurityService securityService;

    @Inject
    private XmlOperatorUtils xmlOperatorUtils;

    @Inject
    private SmrsUtils smrsUtils;

    @Inject
    NscsCMReaderService readerService;

    @EServiceRef
    CppIpSecService cppIpSecService;

    public static final String DISABLE_IP_SEC_XSL_PATH_URI = "xsl/IpForOamSettingFile_No_Ipsec.xsl";
    public static final String IP_FOR_OAM_SETTING_XML = "IpForOamSettingFile.xml";
    private static final String SUMMARY_XML = "summary.xml";
    private static final String RBS_INTEGRATION_CODE = "";
    private static final String SHA = "SHA";
    private static final String SITEBASIC_TAG = "<SiteBasic";
    private static final String SITEBASIC_ENDTAG = "</SiteBasic>";
    private static final String SITEBASIC_XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!-- \n"
            + "ProductNumber  = \'CXC 179 9121/4\' R-State  = \'R2C\' \n" + "DocumentNumber = \'6/006 91-HRB 105 500 \' \n" + "Node version: L14B \n"
            + "File    : IpForOamSetting.xml \n" + "Purpose : Template for the IpForOamSetting file for OaM,\n"
            + "for an OAM IP configuration of the following type:\n" + "o No IPsec for OAM \n" + "o OAM has a separate IpInterface/VLAN \n" + "-->";

    /**
     * Remove deactivates Ip Security on the node
     *
     * @param task
     *            WorkflowActionTask or subclass instance to be performed
     */
    @Override
    public String processTask(final DeactivateIpSecTask task) {
        nscsLogger.workFlowTaskHandlerStarted(task);
        final String nodeName = task.getNode().getName();
        systemRecorder.recordSecurityEvent("Node Security Service - Deactivating IPsec",
                "IPSec " + nodeName + " : Deactivating IPsec on Node '" + task.getNodeFdn() + "'", "", "NETWORK.INITIAL_NODE_ACCESS",
                ErrorSeverity.INFORMATIONAL, "IN-PROGRESS");
        final String nodeXml = task.getNodesXml();
        final String neType = readerService.getTargetType(task.getNode().getFdn());
        final SmrsAccountInfo smrsAccountInfo = securityService.getSmrsAccountInfoForNode(nodeName, neType);
        nscsLogger.debug("Got SMRS base url: " + smrsAccountInfo.prepareUri(""));
        WFTaskResult wfTaskResult = WFTaskResult.FAILURE;
        if (null != nodeXml) {
            final StringBuffer siteBasicExtractedORGenerated = new StringBuffer();
            final boolean validate = nodeXml.contains(SITEBASIC_TAG);
            if (!validate) {
                siteBasicExtractedORGenerated.append(xmlOperatorUtils.generateIpForOamSettingFile(nodeXml, DISABLE_IP_SEC_XSL_PATH_URI));
            } else {
                siteBasicExtractedORGenerated.append(SITEBASIC_XML_HEADER);
                siteBasicExtractedORGenerated.append(nodeXml.substring(nodeXml.indexOf(SITEBASIC_TAG), nodeXml.indexOf(SITEBASIC_ENDTAG)));
                siteBasicExtractedORGenerated.append(SITEBASIC_ENDTAG);
                nscsLogger.info("Extracted siteBasic tag is : {}", siteBasicExtractedORGenerated.toString());
            }

            final String ipForOamSettingXml = siteBasicExtractedORGenerated.toString();
            nscsLogger.info("ipForOamSettingXml Generated or Extracted for Deactivation: {} ", ipForOamSettingXml);
            //Create settings file
            final byte[] settingsFileContent = ipForOamSettingXml.getBytes();
            final String settingUri = smrsUtils.uploadSummaryFileToSmrs(smrsAccountInfo, IP_FOR_OAM_SETTING_XML, settingsFileContent);
            nscsLogger.debug("Setting file uri: " + settingUri);
            //Create summary file
            final SummaryXmlInfo summaryXmlInfo = xmlOperatorUtils.getSummaryFileContent(ipForOamSettingXml,
                    smrsAccountInfo.getSmrsRelativePath() + IP_FOR_OAM_SETTING_XML, SHA);
            nscsLogger.debug("Summary file object: " + summaryXmlInfo);
            if (null != summaryXmlInfo) {
                final String summaryUri = smrsUtils.uploadSummaryFileToSmrs(smrsAccountInfo, SUMMARY_XML, summaryXmlInfo.getContent().getBytes());
                nscsLogger.debug("Summary file uri: " + summaryUri);
                cppIpSecService.updateSummaryFileHashAttributeOfMo(task.getNode(), summaryXmlInfo.getHash());
            }
            //Call MO action for executing operation on node
            deactivateIpSec(task.getNode(), smrsAccountInfo, summaryXmlInfo, task);
            wfTaskResult = WFTaskResult.SUCCESS;
        } else {
            nscsLogger.workFlowTaskHandlerFinishedWithError(task, "Invalid user input xml file " + nodeXml);
            systemRecorder.recordSecurityEvent("Node Security Service - Deactivating IPsec",
                    "IPsec [" + nodeName + "] : Deactivating IPsec on Node '" + task.getNodeFdn() + "' failed! Invalid user input xml file.", "",
                    "NETWORK.INITIAL_NODE_ACCESS", ErrorSeverity.WARNING, "XML-ERROR");
            throw new WorkflowTaskException("Invalid user input xml file, provide valid input xml");
        }
        nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, "Deactivated IP_SEC result: " + wfTaskResult.toString());

        return wfTaskResult.toString();
    }

    private void deactivateIpSec(final NodeReference node, final SmrsAccountInfo smrsAccountInfo, final SummaryXmlInfo summaryXmlInfo,
            final DeactivateIpSecTask task) {
        try {
            final MoParams moParams = RbsConfigInfo.toMoParams(smrsAccountInfo.getUserName(), new String(smrsAccountInfo.getPassword()),
                    smrsAccountInfo.prepareUri(SUMMARY_XML), summaryXmlInfo.getHash(), RBS_INTEGRATION_CODE);
            nscsLogger.workFlowTaskHandlerOngoing(task, "Calling mo action with mo params: " + moParams.toString());
            final NormalizableNodeReference normalizable = readerService.getNormalizableNodeReference(node);
            moAction.performMOAction(normalizable.getFdn(), MoActionWithParameter.IpSec_changeIpForOamSetting, moParams);
        } catch (final RuntimeException e) {
            nscsLogger.workFlowTaskHandlerFinishedWithError(task,
                    "Action " + MoActionWithParameter.IpSec_changeIpForOamSetting + " on node " + node.getName() + " failed!");
            systemRecorder.recordSecurityEvent("Node Security Service - Deactivating IPsec",
                    "IPsec [" + node.getName() + "] : Deactivating IPsec on Node '" + node.getFdn()
                            + "' failed! Command sent to node wasn't executed successfully.",
                    "", "NETWORK.INITIAL_NODE_ACCESS", ErrorSeverity.WARNING, "XML-ERROR");
            throw e;
        }
    }
}
