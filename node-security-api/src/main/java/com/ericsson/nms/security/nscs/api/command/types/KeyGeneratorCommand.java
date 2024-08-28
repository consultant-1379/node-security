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
package com.ericsson.nms.security.nscs.api.command.types;

/**
 * Key Generator command class for sshkey related command.
 * 
 * @author emehsau
 */

public class KeyGeneratorCommand extends NscsNodeCommand {

    private static final long serialVersionUID = -5267972378522721946L;

    public static final String ALGORITHM_TYPE_SIZE_PROPERTY = "algorithm-type-size";
    public static final String CONTINUE = "continue";

    public String getAlgorithmTypeSize() {
        return getValueString(ALGORITHM_TYPE_SIZE_PROPERTY);
    }
}
