package com.ericsson.nms.security.nscs.api.exception;

import java.util.List;

/**
 * <p>Base utility exception class that stores a list of invalid items.</p>
 * <p>Any exception that needs to return a list of invalid items of any type should
 * extend this class</p>
 * @author  emaynes
 * @see com.ericsson.nms.security.nscs.api.exception.InvalidTargetGroupException
 */
public abstract class NscsInvalidItemsException extends NscsServiceException {

    private static final long serialVersionUID = -5283970311768159601L;
    private List<String> invalidItemsList;
    private String itemType = NscsErrorCodes.ELEMENT;

    protected NscsInvalidItemsException() {
    }

    protected NscsInvalidItemsException(final String message) {
        super(message);
    }

    protected NscsInvalidItemsException(final String message, final Throwable cause) {
        super(message, cause);
    }

    protected NscsInvalidItemsException(final Throwable cause) {
        super(cause);
    }

    /**
     * Retrieves the list of invalid items associated with this exception.
     * @return List with invalid items names
     */
    public List<String> getItemsList() {
        return invalidItemsList;
    }

    /**
     * Sets a List with invalid items
     * @param invalidNodesList List with invalid item's names
     */
    public void setItemsList(final List<String> invalidNodesList) {
        this.invalidItemsList = invalidNodesList;
    }

    /**
     * <p>Gets the item type.</p>
     * <p>An item type should be a simple name that describes what one item in the list is. Eg.: 'Node', 'Target group' and etc</p>
     * @return the item type name
     */
    public String getItemType() {
        return itemType;
    }

    /**
     * <p>Sets the item type.</p>
     * @param itemType An item type should be a simple name that describes what one item in the list is. Eg.: 'Node', 'Target group' and etc
     */
    public void setItemType(final String itemType) {
        this.itemType = itemType;
    }
}
