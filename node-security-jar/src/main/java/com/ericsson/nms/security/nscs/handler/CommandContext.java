package com.ericsson.nms.security.nscs.handler;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.ericsson.nms.security.nscs.api.exception.NscsServiceException;
import com.ericsson.nms.security.nscs.api.model.NodeReference;
import com.ericsson.nms.security.nscs.data.nodereference.NormalizableNodeReference;

/**
 * <p>
 * Defines a execution context for CommandHandler or Validator.
 * </p>
 * <p>
 * Provides access to a list of nodes classified as 'Valid', 'Invalid' or 'Not found'. this way, as the command processing goes through a chain of
 * Validators and CommandHandlers, it is possible to have a basic communication in between them, so if one validator sets a node as invalid the next
 * validator or event the CommandHandler will know about it.
 * </p>
 * <p>
 * Also provides support for conversion between normalized and non-normalized node references, in other words, allows translation between
 * NetworkElement and MeContext MOs, and vice-versa
 * </p>
 *
 * @author emaynes.
 */
public interface CommandContext {

    /**
     * Returns a list of existing nodes in current CommandContext regardless whether they are 'Valid' or 'Invalid' in the current context.
     * <p>
     * <b>Does not include 'Not found' nodes.</b>
     * </p>
     * 
     * @return List of existing nodes.
     */
    List<NodeReference> getAllNodes();

    /**
     * Returns a list of nodes considered valid up to now. Any valid node can be classified as invalid at any time by calling 'setAsInvalidOrFailed'
     * 
     * @return List of valid nodes
     */
    List<NormalizableNodeReference> getValidNodes();

    /**
     * Gets a list of nodes where their FDNs could not be found in the database.
     * 
     * @return List of NodeReference where FDNs where not found
     */
    List<NodeReference> getNodesNotFound();

    /**
     * Gets the list of Nodes which were marked as invalid.
     * 
     * @return list of invalid nodes.
     */
    Set<NodeReference> getInvalidNodes();

    /**
     * Utility method extract a list of Normalized references from a list of Normalizable references. In other words, gets a list of NetworkElement's
     * references(FDNs) out of a list of Normalizable (usually MeContext references)
     * 
     * @param nodes
     *            list of Normalizable references
     * @return list of references pointing to NetworkElement's FDNs
     */
    List<NodeReference> toNormalizedRef(List<? extends NormalizableNodeReference> nodes);

    /**
     * Used the get the actual reference (FDN) used in the command line.
     * 
     * @param node
     *            a node reference. It can be a NetworkElement FDN or the corresponding MeContext element
     * @return the FDN (Reference) used in the command line
     */
    NodeReference getCommandReference(NodeReference node);

    /**
     * Adds the specified collection of References the list of invalid references.
     * <p>
     * If the same reference exists in the Valid list, it is removed from there.
     * </p>
     * 
     * @param nodes
     *            collection of node references (namd/FDN)
     * @param error
     *            Exception representing the problem with the node
     */
    void setAsInvalidOrFailed(Collection<NodeReference> nodes, NscsServiceException error);

    /**
     * Adds the specified Reference to the list of invalid references.
     * <p>
     * If the same reference exists in the Valid list, it is removed from there.
     * </p>
     * 
     * @param node
     *            A node reference (namd/FDN)
     * @param error
     *            Exception representing the problem with the node
     */
    void setAsInvalidOrFailed(NodeReference node, NscsServiceException error);

    /**
     * Adds the specified collection of References the list of invalid references and also interrupts the current execution by throwing an exception.
     * <p>
     * If the same reference exists in the Valid list, it is removed from there.
     * </p>
     * 
     * @param nodes
     *            Collection of node references (namd/FDN)
     * @param error
     *            Exception representing the problem with the node
     */
    void setAsInvalidOrFailedAndThrow(Collection<NodeReference> nodes, NscsServiceException error);

    /**
     * Same as 'setAsInvalidOrFailed' but also interrupts the current execution by throwing an exception.
     * 
     * @param node
     *            A node reference (namd/FDN)
     * @param error
     *            Exception representing the problem with the node
     * @throws com.ericsson.nms.security.nscs.ejb.command.context.exception.ExistingInvalidNodesException
     */
    void setAsInvalidOrFailedAndThrow(NodeReference node, NscsServiceException error);

    /**
     * Move the specified Reference from the list of not found references to the list of valid references.
     * <p>
     * The reference is removed from not found list and added to the valid list.
     * </p>
     * This can happen for COM/ECIM nodes having only a valid normalized reference without normalizable (mirror) reference.
     * 
     * @param node
     *            A node reference (name/FDN)
     */
    void setAsValid(NodeReference node);
}
