package com.ericsson.nms.security.nscs.ejb.command.context.exception;

/**
 * Exception thrown by the CommandContext implementation to stop
 * current execution and signal that there are exceptions associated
 * with node references
 * @author emaynes.
 */
public class ExistingInvalidNodesException extends RuntimeException {
    private static final long serialVersionUID = 4100770092521751115L;

    public ExistingInvalidNodesException() {
    }

    public ExistingInvalidNodesException(final String message) {
        super(message);
    }
}
