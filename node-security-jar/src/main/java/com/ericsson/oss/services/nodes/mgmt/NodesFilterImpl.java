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
package com.ericsson.oss.services.nodes.mgmt;

import com.ericsson.nms.security.nscs.handler.command.utility.NscsNameMultipleValueResponseBuilder;
import com.ericsson.oss.services.cache.NodesConfigurationStatusRecord;
import com.ericsson.oss.services.nodes.dto.NodesFilterDTO;
import com.ericsson.oss.services.nodes.dto.interfaces.Filter;

public class NodesFilterImpl implements Filter<NodesConfigurationStatusRecord, NodesFilterDTO> {

    public static final String SL2_ACTIVATION_IN_PROGRESS = "SL2_ACTIVATION_IN_PROGRESS";
    public static final String SL2_DEACTIVATION_IN_PROGRESS = "SL2_DEACTIVATION_IN_PROGRESS";

    /*
     * (non-Javadoc)
     *
     * @see com.ericsson.oss.services.nodes.dto.interfaces.Filter#apply(java.lang.Object)
     */
    @Override
    public boolean apply(final NodesConfigurationStatusRecord source, final NodesFilterDTO dto) {

        String sourceLevel = source.getOperationalsecuritylevel();

        if (sourceLevel.equals(NscsNameMultipleValueResponseBuilder.SL2_ACTIVATION_IN_PROGRESS)) {
            sourceLevel = SL2_ACTIVATION_IN_PROGRESS;
        } else if (sourceLevel.equals(NscsNameMultipleValueResponseBuilder.SL2_DEACTIVATION_IN_PROGRESS)) {
            sourceLevel = SL2_DEACTIVATION_IN_PROGRESS;
        }

        if (dto.getSecurityLevel() != null && (!dto.getSecurityLevel().contains(sourceLevel))) {
            return false;
        }

        //      IPSEC is not supported
        //        if (dto.getIpsecconfig() != null && !dto.getIpsecconfig().contains(source.getIpsecconfig())) {
        //            return false;
        //        }

        if (dto.getName() != null && dto.getName().equals("%")) {
            return true;
        } else if (dto.getName() != null && !dto.getName().equals("")
                && ((dto.getName().startsWith("%") && !dto.getName().endsWith("%") && !source.getName().endsWith(dto.getName().substring(1)))
                        || (dto.getName().endsWith("%") && !dto.getName().startsWith("%")
                                && !source.getName().startsWith(dto.getName().substring(0, dto.getName().length() - 1)))
                        || (dto.getName().startsWith("%") && dto.getName().endsWith("%")
                                && !source.getName().contains(dto.getName().substring(1, dto.getName().length() - 1)))
                        || (!dto.getName().startsWith("%") && !dto.getName().endsWith("%") && !source.getName().equals(dto.getName())))) {
            return false;
        }

        return true;
    }

}
