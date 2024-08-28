/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.nscs.handler.command.impl;

import com.ericsson.oss.services.cm.cmshared.dto.CmObject
import java.nio.file.*

/**
 * This method is used to read the content from the input xml files.
 *
 *  @author xvadyas
 *
 */
public class TestSetupInitializer {

    final String namespace = "OSS_NE_CM_DEF"
    final int poId = 1111
    final String cmFunction = "CmFunction"

    /**
     * This method is used to read the content from the input xml file.
     * 
     */
    public byte[] getFileContent(final String filePath) {
        def byte[] fileContent
        try {
            final Path path = Paths.get(filePath)
            fileContent = Files.readAllBytes(path)
        }catch (Exception e) {
        }
        return fileContent;
    }

    /**
     * This method creates cmobjects used to validate the node synchronization.
     * 
     */
    public List<CmObject> createCmObjects(final String syncStatus, final String nodeName, final Map<String,String> attributes) {
        final List<CmObject> cmObjects = new ArrayList<CmObject>(0)
        CmObject cmObject = new CmObject()
        cmObject.setFdn(nodeName)
        cmObject.setName(nodeName)
        cmObject.setNamespace(namespace)
        cmObject.setPoId(poId)
        cmObject.setType(cmFunction)
        cmObject.setAttributes(attributes)
        cmObjects.add(cmObject)
        return cmObjects
    }
}