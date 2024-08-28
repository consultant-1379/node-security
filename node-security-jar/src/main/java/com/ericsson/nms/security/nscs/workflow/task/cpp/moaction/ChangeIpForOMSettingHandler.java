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
package com.ericsson.nms.security.nscs.workflow.task.cpp.moaction;

import javax.ejb.Local;
import javax.inject.Inject;

import com.ericsson.nms.security.nscs.api.cpp.level.CppIpSecService;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.cpp.ipsec.util.SmrsUtils;
import com.ericsson.nms.security.nscs.cpp.ipsec.util.XmlOperatorUtils;
import com.ericsson.nms.security.nscs.cpp.model.RbsConfigInfo;
import com.ericsson.nms.security.nscs.cpp.model.SmrsAccountInfo;
import com.ericsson.nms.security.nscs.cpp.service.CppSecurityService;
import com.ericsson.nms.security.nscs.data.NscsCMReaderService;
import com.ericsson.nms.security.nscs.data.NscsCMWriterService;
import com.ericsson.nms.security.nscs.data.moaction.MOActionService;
import com.ericsson.nms.security.nscs.data.moaction.MoActionWithParameter;
import com.ericsson.nms.security.nscs.data.moaction.param.MoParams;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;
import com.ericsson.nms.security.nscs.logger.NscsLogger;
import com.ericsson.nms.security.nscs.workflow.task.WFActionTaskHandler;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskHandlerInterface;
import com.ericsson.nms.security.nscs.workflow.task.WFTaskType;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.itpf.sdk.instrument.annotation.Profiled;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.cpp.moaction.ChangeIpForOMSettingTask;

/**
 * <p>
 * Task handler for WorkflowTaskType.CPP_CREATE_UPLOAD_FILES_SMRS
 * </p>
 * <p>
 * Create and upload files for ipsec on SMRS.
 * </p>
 *
 * @author ediniku
 */
@WFTaskType(WorkflowTaskType.CPP_CHANGE_IP_OAM_SETTING)
@Local(WFTaskHandlerInterface.class)
public class ChangeIpForOMSettingHandler implements WFActionTaskHandler<ChangeIpForOMSettingTask>, WFTaskHandlerInterface {

    private static final String IP_FOR_OAM_SETTING_XML = "IpForOamSettingFile.xml";
    private static final String SUMMARY_XML = "summary.xml";
    private static final String RBS_INTEGRATION_CODE = "";
    private static final String XSL_PATH = "xsl/IpForOamSettingGenerator.xsl";
    private static final String SHA = "SHA";
    private static final String SITEBASIC_TAG = "<SiteBasic";
    private static final String SITEBASIC_ENDTAG = "</SiteBasic>";
    private static final String SITEBASIC_XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!-- \n"
            + "ProductNumber  = \'CXC 179 9121/4\' R-State  = \'R2C\' \n" + "DocumentNumber = \'6/006 91-HRB 105 500 \' \n" + "Node version: L14B \n"
            + "File    : IpForOamSetting.xml \n" + "Purpose : Template for the IpForOamSetting file for OaM,\n"
            + "for an OAM IP configuration of the following type:\n" + "o IPsec for OAM is activated \n" + "o OAM has a separate IpInterface/VLAN \n"
            + "o OAM has a separate IpAccessHostEt(outer IP host) \n" + "-->";

    @Inject
    private NscsLogger nscsLogger;

    @Inject
    private CppSecurityService securityService;

    @Inject
    private XmlOperatorUtils xmlOperatorUtils;

    @Inject
    private MOActionService moAction;

    @Inject
    private SmrsUtils smrsUtils;

    @Inject
    NscsCMReaderService readerService;

    @Inject
    NscsCMWriterService writerService;

    @EServiceRef
    CppIpSecService cppIpSecService;

    /**
     * Creates the IpForOamSettingFile and SummaryFile and uploads to the SMRS
     *
     * @param task
     *            WorkflowActionTask or subclass instance to be performed
     */
    @Profiled
    @Override
    public void processTask(final ChangeIpForOMSettingTask task) {
        nscsLogger.workFlowTaskHandlerStarted(task);
        final String nodeName = task.getNode().getName();
        final String neType = readerService.getTargetType(task.getNode().getFdn());
        final SmrsAccountInfo smrsAccountInfo = securityService.getSmrsAccountInfoForNode(nodeName, neType);
        if (task.getUserInputXml() != null) {
            nscsLogger.debug("task.getUserInputXml() " + task.getUserInputXml());
            final StringBuffer siteBasicExtractedORGenerated = new StringBuffer();
            final String inputXml = task.getUserInputXml();
            final boolean validate = inputXml.contains(SITEBASIC_TAG);
            if (!validate) {
                nscsLogger.info(task.getUserInputXml());
                siteBasicExtractedORGenerated.append(xmlOperatorUtils.generateIpForOamSettingFile(task.getUserInputXml(), XSL_PATH));
            } else {
                siteBasicExtractedORGenerated.append(SITEBASIC_XML_HEADER);
                siteBasicExtractedORGenerated.append(inputXml.substring(inputXml.indexOf(SITEBASIC_TAG), inputXml.indexOf(SITEBASIC_ENDTAG)));
                siteBasicExtractedORGenerated.append(SITEBASIC_ENDTAG);
                nscsLogger.debug("Extracted siteBasic tag is : " + siteBasicExtractedORGenerated.toString());
            }
            final String ipForOamSettingXml = siteBasicExtractedORGenerated.toString();
            nscsLogger.info("ipForOamSettingXml Generated or Extracted: " + ipForOamSettingXml);
            final byte[] fileContent = ipForOamSettingXml.getBytes();
            final String settingUri = smrsUtils.uploadSummaryFileToSmrs(smrsAccountInfo, IP_FOR_OAM_SETTING_XML, fileContent);
            nscsLogger.debug("settingUri : " + settingUri);
            final XmlOperatorUtils.SummaryXmlInfo summaryXmlInfo = xmlOperatorUtils.getSummaryFileContent(ipForOamSettingXml,
                    smrsAccountInfo.getSmrsRelativePath() + IP_FOR_OAM_SETTING_XML, SHA);
            nscsLogger.debug("summaryXmlInfo : {}", summaryXmlInfo);
            if (summaryXmlInfo != null) {
                cppIpSecService.updateSummaryFileHashAttributeOfMo(task.getNode(), summaryXmlInfo.getHash());
                final String summaryUri = smrsUtils.uploadSummaryFileToSmrs(smrsAccountInfo, SUMMARY_XML, summaryXmlInfo.getContent().getBytes());
                nscsLogger.debug("changeIpForOamSetting call : [" + task.getNode() + "],[" + smrsAccountInfo + "],[" + summaryUri + "],["
                        + summaryXmlInfo.getHash() + "]");
                changeIpForOamSetting(task.getNode(), smrsAccountInfo, summaryUri, summaryXmlInfo.getHash(), task);
            }
        } else {
            nscsLogger.info("Invalid User Input file " + task.getUserInputXml());
        }
        nscsLogger.workFlowTaskHandlerFinishedWithSuccess(task, "ChangeIpForOMSettingTask completed", "action performed");
    }

    /**
     * Call MoAction for changing IpFor Oam Setting
     *
     * @param nodeRef
     * @param smrsAccountInfo
     * @param summaryFilePath
     * @param summaryFileHash
     */
    private void changeIpForOamSetting(final NodeReference nodeRef, final SmrsAccountInfo smrsAccountInfo, final String summaryFilePath,
            final String summaryFileHash, final ChangeIpForOMSettingTask task) {
        final MoParams moParams = RbsConfigInfo.toMoParams(smrsAccountInfo.getUserName(), new String(smrsAccountInfo.getPassword()), summaryFilePath,
                summaryFileHash, RBS_INTEGRATION_CODE);
        final NormalizableNodeReference normalizable = readerService.getNormalizableNodeReference(nodeRef);
        nscsLogger.workFlowTaskHandlerOngoing(task, "Performing Action for node: [" + task.getNodeFdn() + "]");
        moAction.performMOAction(normalizable.getFdn(), MoActionWithParameter.IpSec_changeIpForOamSetting, moParams);
    }

}