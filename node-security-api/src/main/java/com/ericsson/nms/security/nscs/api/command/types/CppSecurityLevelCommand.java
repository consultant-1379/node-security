package com.ericsson.nms.security.nscs.api.command.types;

import com.ericsson.nms.security.nscs.api.command.NscsSecLevelCommand;

/**
 * Representation of the cpp-set-securitylevel command
 * 
 * Created by emaynes on 09/05/2014.
 */

											//SL2 dheeraj code	
public class CppSecurityLevelCommand extends NscsSecLevelCommand {
    /**
     * 
     */
    private static final long serialVersionUID = 5812981047081013097L;
    public static final String SECURITY_LEVEL_PROPERTY = "level";


    /**
     * @return String
     *          - The security level entered in the command
     */
    public String getSecurityLevel() {
        return getValueString(SECURITY_LEVEL_PROPERTY);
    }


}
