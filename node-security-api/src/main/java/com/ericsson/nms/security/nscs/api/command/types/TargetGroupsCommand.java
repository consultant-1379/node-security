package com.ericsson.nms.security.nscs.api.command.types;

import java.util.List;

/**
 * Representation of the set-target-groups command
 * 
 * Created by emaynes on 13/05/2014.
 */
public class TargetGroupsCommand extends NscsNodeCommand {
    /**
     * 
     */
    private static final long serialVersionUID = 6180230880529232425L;
    public static final String TARGET_GROUP_PROPERTY = "targetgroups";

    /**
     * @return
     * Returns a list of Target Groups
     */
    public List<String> getTargetGroup() {
        return (List<String>) getValue(TARGET_GROUP_PROPERTY);
    }
}
