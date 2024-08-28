package com.ericsson.nms.security.nscs.api.command.types;

import java.util.Collections;
import java.util.List;

import com.ericsson.nms.security.nscs.api.command.NscsCommand;
import com.ericsson.nms.security.nscs.api.command.NscsPropertyCommand;
import com.ericsson.nms.security.nscs.api.exception.*;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.api.model.NodeRef;

/**
 * Generic command class for any command that needs a list of nodes. Ideally a command that needs more parameters will extend this class and add it's
 * own list of attributes.
 * 
 * Created by emaynes on 02/05/2014.
 */
public class NscsNodeCommand extends NscsPropertyCommand {

    /**
     * 
     */
    private static final long serialVersionUID = 8181744525646942268L;
    public static final String NODE_LIST_PROPERTY = "nodelist";
    public static final String NODE_LIST_FILE_PROPERTY = "nodefile";
    public static final String SAVED_SERACH_NAME_PROPERTY = "savedsearch";
    public static final String COLLECTION_NAME_PROPERTY = "collection";
    private static final String ALL_NODES_VALUE = "*";

    private List<NodeReference> nodes = null;

    /**
     * <p>Return the list of nodes specified in the command line directly of by the use of a file;</p>
     *
     * @return List of node names
     */
    public List<NodeReference> getNodes() {
        if ( nodes == null ) {
        	try{
            nodes = NodeRef.from(getNodeNames());
        	}
        	catch(IllegalArgumentException e){
        		throw new InvalidArgumentValueException(e.getMessage());
        	}
        }

        return nodes;
    }

    /**
     * @return
     * Return true if command was entered using start '*' as node list parameter
     */
    public boolean isAllNodes() {
        return ALL_NODES_VALUE.equals(getValue(NODE_LIST_PROPERTY));
    }

    /**
     * @param command a NscsCommand command instance
     * @return
     * Returns true if the given command is a NscsNodeCommand or a sub-class of it.
     */
    public static boolean isNscsNodeCommand(final NscsCommand command) {
        return NscsNodeCommand.class.isAssignableFrom(command.getClass());
    }

    public List<String> getNodeNames() {
        List<String> names = null;
        if ( isAllNodes() ){
            names = Collections.EMPTY_LIST;
        } else if ( hasProperty(NODE_LIST_FILE_PROPERTY) ) {
            names = (List<String>) getValue(NODE_LIST_PROPERTY);
        } else {
            names = (List<String>) getValue(NODE_LIST_PROPERTY);
            if ( names == null ) {
                names = Collections.EMPTY_LIST;
            }
        }

        return names;
    }
    /**
     * <p>
     * Returns the list of topology saved search names
     * </p>
     *
     * @return List of saved search names
     */
    public List<String> getSavedSearchNames() {
        return (List<String>) getValue(SAVED_SERACH_NAME_PROPERTY);
    }

    /**
     * <p>
     * Returns the list of topology collection names
     * </p>
     *
     * @return List of saved search names
     */
    public List<String> getCollectionNames() {
        return (List<String>) getValue(COLLECTION_NAME_PROPERTY);
    }

    /**
     * <p>
     * Returns the list of topology expressions or node names
     * </p>
     *
     * @return List of saved search names
     */
    public List<String> getNodeNamesOrExpressions() {
        if (hasProperty(NODE_LIST_FILE_PROPERTY) && isAllNodes()) {
                throw new UnsupportedCommandArgumentException(NscsErrorCodes.NODE_FILE_MUST_NOT_CONTAIN_STAR, NscsErrorCodes.PLEASE_SPECIFY_VALID_NODE_NAMES_IN_NODEFILE);
        }
        List<String> names = (List<String>) getValue(NODE_LIST_PROPERTY);
        if (names == null) {
            names = Collections.EMPTY_LIST;
        }
        return names;
    }
}
