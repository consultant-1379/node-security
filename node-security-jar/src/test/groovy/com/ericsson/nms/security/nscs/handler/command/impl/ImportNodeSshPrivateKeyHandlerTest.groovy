package com.ericsson.nms.security.nscs.handler.command.impl

import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.nms.security.nscs.api.exception.ImportNodeSshPrivateKeyHandlerException
import com.ericsson.nms.security.nscs.data.NscsCMWriterService
import com.ericsson.nms.security.nscs.handler.command.utility.PasswordHelper
import com.ericsson.nms.security.nscs.logger.NscsLogger

import javax.inject.Inject

import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.nms.security.nscs.api.command.NscsCommandResponse

class ImportNodeSshPrivateKeyHandlerTest extends ImportNodeSshPrivateKeyHandlerSetupData{

    @ObjectUnderTest
    ImportNodeSshPrivateKeyHandler importNodeSshPrivateKeyHandler

    @MockedImplementation
    private NscsLogger nscsLogger;

    @MockedImplementation
    private NscsCMWriterService nscsCMWriterService;

    @MockedImplementation
    private NscsCMWriterService.WriterSpecificationBuilder specificationBuilder;

    @MockedImplementation
    private PasswordHelper passwordHelper;

    def IMPORT_NODE_SSH_PRIVATE_KEY_SUCCESS_MSG = "SshPrivatekey import command executed Successfully"
    def filePath = ""
    def setup() {
        passwordHelper.encryptEncode(_)>>"EncryptedMock"
    }

    def 'successfully Updates enmSshPrivateKey Attribute for valid nodes case'() {
        given:'filePath'
            fileUtil.isValidFileExtension(_,_) >> true
            specificationBuilder.setFdn(_)>>specificationBuilder
            specificationBuilder.setAttribute(_,_)>>specificationBuilder
            nscsCMWriterService.withSpecification(_)>>specificationBuilder
            specificationBuilder.updateMO()
            filePath = 'src/test/resources/ImportSshPrivateKey/NodeSshPrivateKeyValidFile.txt'
            setCommandDataForNodeSshPrivateKeyTxtFile(filePath,'VECE1234')
            setDataForNodeExists('validNode','VECE1234', true,true)

        when: 'execute Import Node Ssh Priavte Key Handler process method'
            NscsCommandResponse response=importNodeSshPrivateKeyHandler.process(command, context)

        then:
            IMPORT_NODE_SSH_PRIVATE_KEY_SUCCESS_MSG == response.getMessage()
    }

    def 'Invalid node case'() {
        given:'filePath'
            fileUtil.isValidFileExtension(_,_) >> true
            filePath = 'src/test/resources/ImportSshPrivateKey/NodeSshPrivateKeyValidFile.txt'
            setCommandDataForNodeSshPrivateKeyTxtFile(filePath,'NEWNODE1234')
            setDataForNodeExists('normNodeNull','NEWNODE1234', true,true)

        when: 'execute Import Node Ssh Private Key Handler process method'
            NscsCommandResponse response=importNodeSshPrivateKeyHandler.process(command, context)

        then:
            thrown(ImportNodeSshPrivateKeyHandlerException)
    }
}
